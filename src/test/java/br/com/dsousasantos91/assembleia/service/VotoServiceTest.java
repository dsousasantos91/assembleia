package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Associado;
import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.domain.Voto;
import br.com.dsousasantos91.assembleia.domain.enumeration.VotoEnum;
import br.com.dsousasantos91.assembleia.exception.GenericBadRequestException;
import br.com.dsousasantos91.assembleia.mapper.AssociadoMapper;
import br.com.dsousasantos91.assembleia.mapper.PautaMapper;
import br.com.dsousasantos91.assembleia.mapper.SessaoMapper;
import br.com.dsousasantos91.assembleia.mapper.VotoMapper;
import br.com.dsousasantos91.assembleia.mock.PautaRequestMock;
import br.com.dsousasantos91.assembleia.mock.SessaoRequestMock;
import br.com.dsousasantos91.assembleia.mock.VotacaoRequestMock;
import br.com.dsousasantos91.assembleia.repository.AssociadoRepository;
import br.com.dsousasantos91.assembleia.repository.SessaoRepository;
import br.com.dsousasantos91.assembleia.repository.VotacaoRepository;
import br.com.dsousasantos91.assembleia.service.dto.request.VotoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import br.com.dsousasantos91.assembleia.service.dto.response.VotoResponse;
import br.com.dsousasantos91.assembleia.util.MaskUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class VotoServiceTest {

    @Autowired
    private VotoService votoService;

    @Autowired
    private VotoMapper votoMapper;

    @Autowired
    private AssociadoMapper associadoMapper;

    @Autowired
    private PautaMapper pautaMapper;

    @Autowired
    private SessaoMapper sessaoMapper;

    @MockBean
    private VotacaoRepository votacaoRepository;

    @MockBean
    private SessaoRepository sessaoRepository;

    @MockBean
    private AssociadoRepository associadoRepository;

    private VotoRequest request;
    private Voto voto;
    private Sessao sessao;
    private Pauta pauta;
    private Associado associado;

    @BeforeEach
    public void setUp() {
        request = VotacaoRequestMock.mocked().mock();
        voto = votoMapper.toEntity(request);
        sessao = sessaoMapper.toEntity(SessaoRequestMock.mocked().mock());
        pauta = pautaMapper.toEntity(PautaRequestMock.mocked().mock());
        voto.setId(1L);
        pauta.setId(1L);
        sessao.setId(1L);
        sessao.setPauta(pauta);
        voto.setSessao(sessao);
        associado = associadoMapper.toEntity(request.getAssociado());
    }

    @Test
    void deveVotarComSucesso() {
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessao));
        when(associadoRepository.findByCpf(associado.getCpf())).thenReturn(Optional.of(associado));
        when(votacaoRepository.save(any(Voto.class))).thenReturn(voto);
        VotoResponse response = votoService.votar(request);
        assertNotNull(response.getId());
        assertEquals(response.getSessao().getId(), request.getSessaoId());
        assertEquals(response.getAssociado().getCpf(), MaskUtil.cpf(request.getAssociado().getCpf()));
        assertEquals(response.getVoto(), request.getVoto().getValue());
    }

    @Test
    void deveLancarGenericBadRequestExceptionPorAssociadoNaoEstarRelacionadoASessao() {
        VotoRequest request = VotacaoRequestMock.mocked().mock();
        Sessao sessao = sessaoMapper.toEntity(SessaoRequestMock.mocked().mock());
        sessao.getAssociados().remove(0);
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessao));
        when(associadoRepository.findByCpf(associado.getCpf())).thenReturn(Optional.of(associado));
        assertThrows(GenericBadRequestException.class, () -> votoService.votar(request));
    }

    @Test
    void deveLancarGenericBadRequestExceptionPorSessaoEncerrada() {
        VotoRequest request = VotacaoRequestMock.mocked().mock();
        Sessao sessao = sessaoMapper.toEntity(SessaoRequestMock.mocked().mock());
        sessao.getAssociados().remove(0);
        sessao.setDataHoraFim(LocalDateTime.now().minusMinutes(1));
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessao));
        when(associadoRepository.findByCpf(associado.getCpf())).thenReturn(Optional.of(associado));
        assertThrows(GenericBadRequestException.class, () -> votoService.votar(request));
    }

    @Test
    void devePesquisarComSucesso() {
        PageRequest pageable = PageRequest.of(0, 1);
        List<Voto> votoList = singletonList(voto);
        PageImpl<Voto> pageResponse = new PageImpl<>(votoList, pageable, votoList.size());
        when(votacaoRepository.findAll(any(Pageable.class))).thenReturn(pageResponse);
        Page<VotoResponse> response = votoService.pesquisar(pageable);
        List<Long> idsVotacoes = response.stream().map(VotoResponse::getId).collect(toList());
        assertEquals(response.getPageable().getPageNumber(), pageable.getPageNumber());
        assertEquals(response.getPageable().getPageSize(), pageable.getPageSize());
        assertEquals(idsVotacoes.size(), 1);
        assertTrue(idsVotacoes.contains(voto.getId()));
    }

    @Test
    void deveContabilizarComSucesso() {
        when(sessaoRepository.findById(sessao.getId())).thenReturn(Optional.of(sessao));
        when(votacaoRepository.findBySessaoId(sessao.getId())).thenReturn(singletonList(voto));
        ContagemVotosResponse response = votoService.contabilizar(sessao.getId());
        assertEquals(response.getSessaoId(), sessao.getId());
        assertEquals(response.getPauta().getId(), pauta.getId());
        assertEquals(response.getVotos().get(VotoEnum.SIM).longValue(), 1);
        assertEquals(response.getVotos().get(VotoEnum.NAO).longValue(), 0);
    }

    @Test
    void deveAlterarVotoComSucesso() {
        VotoEnum votoPre = voto.getVoto();
        when(votacaoRepository.findBySessaoIdAndAssociadoCpf(sessao.getId(), request.getAssociado().getCpf())).thenReturn(Optional.of(voto));
        when(votacaoRepository.save(any(Voto.class))).thenReturn(voto);
        VotoResponse response = votoService.alterar(sessao.getId(), request.getAssociado().getCpf());
        assertEquals(response.getId(), voto.getId());
        assertNotEquals(response.getVoto(), votoPre.getValue());
    }
}

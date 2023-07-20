package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Associado;
import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.domain.Votacao;
import br.com.dsousasantos91.assembleia.domain.enumeration.Voto;
import br.com.dsousasantos91.assembleia.exception.GenericBadRequestException;
import br.com.dsousasantos91.assembleia.mapper.AssociadoMapper;
import br.com.dsousasantos91.assembleia.mapper.PautaMapper;
import br.com.dsousasantos91.assembleia.mapper.SessaoMapper;
import br.com.dsousasantos91.assembleia.mapper.VotacaoMapper;
import br.com.dsousasantos91.assembleia.mock.PautaRequestMock;
import br.com.dsousasantos91.assembleia.mock.SessaoRequestMock;
import br.com.dsousasantos91.assembleia.mock.VotacaoRequestMock;
import br.com.dsousasantos91.assembleia.repository.AssociadoRepository;
import br.com.dsousasantos91.assembleia.repository.SessaoRepository;
import br.com.dsousasantos91.assembleia.repository.VotacaoRepository;
import br.com.dsousasantos91.assembleia.service.dto.request.VotacaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import br.com.dsousasantos91.assembleia.service.dto.response.VotacaoResponse;
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
class VotacaoServiceTest {

    @Autowired
    private VotacaoService votacaoService;

    @Autowired
    private VotacaoMapper votacaoMapper;

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

    private VotacaoRequest request;
    private Votacao votacao;
    private Sessao sessao;
    private Pauta pauta;
    private Associado associado;

    @BeforeEach
    public void setUp() {
        request = VotacaoRequestMock.mocked().mock();
        votacao = votacaoMapper.toEntity(request);
        sessao = sessaoMapper.toEntity(SessaoRequestMock.mocked().mock());
        pauta = pautaMapper.toEntity(PautaRequestMock.mocked().mock());
        votacao.setId(1L);
        pauta.setId(1L);
        sessao.setId(1L);
        sessao.setPauta(pauta);
        votacao.setSessao(sessao);
        associado = associadoMapper.toEntity(request.getAssociado());
    }

    @Test
    void deveVotarComSucesso() {
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessao));
        when(associadoRepository.findByCpf(associado.getCpf())).thenReturn(Optional.of(associado));
        when(votacaoRepository.save(any(Votacao.class))).thenReturn(votacao);
        VotacaoResponse response = votacaoService.votar(request);
        assertNotNull(response.getId());
        assertEquals(response.getSessao().getId(), request.getSessaoId());
        assertEquals(response.getAssociado().getCpf(), MaskUtil.cpf(request.getAssociado().getCpf()));
        assertEquals(response.getVoto(), request.getVoto().getValue());
    }

    @Test
    void deveLancarGenericBadRequestExceptionPorAssociadoNaoEstarRelacionadoASessao() {
        VotacaoRequest request = VotacaoRequestMock.mocked().mock();
        Sessao sessao = sessaoMapper.toEntity(SessaoRequestMock.mocked().mock());
        sessao.getAssociados().remove(0);
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessao));
        when(associadoRepository.findByCpf(associado.getCpf())).thenReturn(Optional.of(associado));
        assertThrows(GenericBadRequestException.class, () -> votacaoService.votar(request));
    }

    @Test
    void deveLancarGenericBadRequestExceptionPorSessaoEncerrada() {
        VotacaoRequest request = VotacaoRequestMock.mocked().mock();
        Sessao sessao = sessaoMapper.toEntity(SessaoRequestMock.mocked().mock());
        sessao.getAssociados().remove(0);
        sessao.setDataHoraFim(LocalDateTime.now().minusMinutes(1));
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(sessao));
        when(associadoRepository.findByCpf(associado.getCpf())).thenReturn(Optional.of(associado));
        assertThrows(GenericBadRequestException.class, () -> votacaoService.votar(request));
    }

    @Test
    void devePesquisarComSucesso() {
        PageRequest pageable = PageRequest.of(0, 1);
        List<Votacao> votacaoList = singletonList(votacao);
        PageImpl<Votacao> pageResponse = new PageImpl<>(votacaoList, pageable, votacaoList.size());
        when(votacaoRepository.findAll(any(Pageable.class))).thenReturn(pageResponse);
        Page<VotacaoResponse> response = votacaoService.pesquisar(pageable);
        List<Long> idsVotacoes = response.stream().map(VotacaoResponse::getId).collect(toList());
        assertEquals(response.getPageable().getPageNumber(), pageable.getPageNumber());
        assertEquals(response.getPageable().getPageSize(), pageable.getPageSize());
        assertEquals(idsVotacoes.size(), 1);
        assertTrue(idsVotacoes.contains(votacao.getId()));
    }

    @Test
    void deveContabilizarComSucesso() {
        when(sessaoRepository.findById(sessao.getId())).thenReturn(Optional.of(sessao));
        when(votacaoRepository.findBySessaoId(sessao.getId())).thenReturn(Optional.of(singletonList(votacao)));
        ContagemVotosResponse response = votacaoService.contabilizar(sessao.getId());
        assertEquals(response.getSessaoId(), sessao.getId());
        assertEquals(response.getPauta().getId(), pauta.getId());
        assertEquals(response.getVotos().get(Voto.SIM).longValue(), 1);
        assertEquals(response.getVotos().get(Voto.NAO).longValue(), 0);
    }

    @Test
    void deveAlterarVotoComSucesso() {
        Voto votoPre = votacao.getVoto();
        when(votacaoRepository.findBySessaoIdAndAssociadoCpf(sessao.getId(), request.getAssociado().getCpf())).thenReturn(Optional.of(votacao));
        when(votacaoRepository.save(any(Votacao.class))).thenReturn(votacao);
        VotacaoResponse response = votacaoService.alterarVoto(sessao.getId(), request.getAssociado().getCpf());
        assertEquals(response.getId(), votacao.getId());
        assertNotEquals(response.getVoto(), votoPre.getValue());
    }
}

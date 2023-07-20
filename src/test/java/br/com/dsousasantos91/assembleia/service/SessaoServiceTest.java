package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Assembleia;
import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.domain.enumeration.Voto;
import br.com.dsousasantos91.assembleia.exception.GenericBadRequestException;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.AssembleiaMapper;
import br.com.dsousasantos91.assembleia.mapper.PautaMapper;
import br.com.dsousasantos91.assembleia.mapper.SessaoMapper;
import br.com.dsousasantos91.assembleia.mock.AssembleiaRequestMock;
import br.com.dsousasantos91.assembleia.mock.SessaoEmLoteRequestMock;
import br.com.dsousasantos91.assembleia.mock.SessaoRequestMock;
import br.com.dsousasantos91.assembleia.repository.AssembleiaRepository;
import br.com.dsousasantos91.assembleia.repository.PautaRepository;
import br.com.dsousasantos91.assembleia.repository.SessaoRepository;
import br.com.dsousasantos91.assembleia.scheduler.NotificadorScheduler;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoEmLoteRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import br.com.dsousasantos91.assembleia.service.dto.response.PautaResponse;
import br.com.dsousasantos91.assembleia.service.dto.response.SessaoResponse;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class SessaoServiceTest {

    @Autowired
    private SessaoService sessaoService;

    @Autowired
    private SessaoMapper sessaoMapper;

    @Autowired
    private PautaMapper pautaMapper;

    @Autowired
    private AssembleiaMapper assembleiaMapper;

    @MockBean
    private SessaoRepository sessaoRepository;

    @MockBean
    private PautaRepository pautaRepository;

    @MockBean
    private AssembleiaRepository assembleiaRepository;

    @MockBean
    private NotificadorScheduler notificadorScheduler;

    private SessaoRequest request;
    private Sessao entity1;

    @BeforeEach
    public void setUp() {
        Pauta pauta = Pauta.builder().id(1L).build();
        request = SessaoRequestMock.mocked().mock();
        entity1 = sessaoMapper.toEntity(request);
        entity1.setId(1L);
        entity1.setPauta(pauta);
        entity1.setDataHoraInicio(LocalDateTime.now());
        entity1.setResultadoEnviado(Boolean.FALSE);
        for (int i = 0; i < entity1.getAssociados().size(); i++) {
            entity1.getAssociados().get(i).setId(i + 1L);
        }
    }

    @Test
    void deveAbrirSessaoComSucesso() {
        when(pautaRepository.findById(anyLong())).thenReturn(Optional.of(entity1.getPauta()));
        when(sessaoRepository.save(any(Sessao.class))).thenReturn(entity1);
        SessaoResponse response = sessaoService.abrir(request);
        assertNotNull(response.getId());
        assertEquals(response.getPauta().getId(), request.getPautaId());
        assertEquals(response.getDataHoraInicio(), request.getDataHoraInicio());
        assertEquals(response.getDataHoraFim(), request.getDataHoraFim());
    }

    @Test
    void deveAbrirSessaoEmLoteComSucesso() {
        SessaoEmLoteRequest requestEmLote = SessaoEmLoteRequestMock.mocked().mock();
        Assembleia assembleia = assembleiaMapper.toEntity(AssembleiaRequestMock.mocked().mock());
        assembleia.setId(1L);
        for (int i = 0; i < assembleia.getPautas().size(); i++) {
            long idPauta = i + 1L;
            assembleia.getPautas().get(i).setId(idPauta);
            when(pautaRepository.findById(idPauta)).thenReturn(Optional.of(assembleia.getPautas().get(i)));
        }
        when(assembleiaRepository.findById(anyLong())).thenReturn(Optional.of(assembleia));
        doAnswer(invocation -> invocation.getArgument(0)).when(sessaoRepository).saveAll(anyList());
        List<SessaoResponse> response = sessaoService.abrirEmLote(requestEmLote);
        List<Long> idsPautasDaAssembleia = assembleia.getPautas().stream().map(Pauta::getId).collect(toList());
        List<Long> idsPautasResponse = response.stream().map(SessaoResponse::getPauta).map(PautaResponse::getId).collect(toList());
        assertEquals(response.size(), assembleia.getPautas().size());
        verify(sessaoRepository, times(1)).saveAll(anyList());
        assertTrue(idsPautasResponse.containsAll(idsPautasDaAssembleia));
    }

    @Test
    void devePesquisarSessoesPaginadasComSucesso() {
        Sessao entity2 = sessaoMapper.toEntity(request);
        entity2.setId(2L);
        Pauta pauta2 = Pauta.builder().id(2L).build();
        entity2.setPauta(pauta2);
        PageRequest pageable = PageRequest.of(0, 2);
        List<Sessao> sessaoList = Arrays.asList(entity1, entity2);
        PageImpl<Sessao> pageResponse = new PageImpl<>(sessaoList, pageable, sessaoList.size());
        when(sessaoRepository.findAll(any(Pageable.class))).thenReturn(pageResponse);
        Page<SessaoResponse> response = sessaoService.pesquisar(pageable);
        List<Long> idsSessoes = response.stream().map(SessaoResponse::getId).collect(toList());
        assertEquals(response.getPageable().getPageNumber(), pageable.getPageNumber());
        assertEquals(response.getPageable().getPageSize(), pageable.getPageSize());
        assertEquals(idsSessoes.size(), 2);
        assertTrue(idsSessoes.contains(entity1.getId()));
        assertTrue(idsSessoes.contains(entity2.getId()));
    }

    @Test
    void deveBuscarSessaoPorIdComSucesso() {
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(entity1));
        SessaoResponse response = sessaoService.buscarPorId(1L);
        assertEquals(response.getId(), entity1.getId());
        assertEquals(response.getPauta().getId(), request.getPautaId());
        assertEquals(response.getDataHoraInicio(), request.getDataHoraInicio());
        assertEquals(response.getDataHoraFim(), request.getDataHoraFim());
    }

    @Test
    void deveProrrogarSessaoComSucesso() {
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(entity1));
        when(sessaoRepository.save(any(Sessao.class))).thenReturn(entity1);
        request.getTempoSessao().setDias(1);
        SessaoResponse response = sessaoService.prorrogar(1L, request);
        assertEquals(response.getId(), entity1.getId());
        assertEquals(response.getPauta().getId(), request.getPautaId());
        assertEquals(response.getDataHoraInicio(), request.getDataHoraInicio());
        assertEquals(response.getDataHoraFim(), request.getDataHoraFim());
    }

    @Test
    void deveEncerrarSessaoComSucesso() {
        LocalDateTime dataHoraFimPre = entity1.getDataHoraFim();
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(entity1));
        when(sessaoRepository.save(any(Sessao.class))).thenReturn(entity1);
        doNothing().when(notificadorScheduler).agendarNotificacao(any(Sessao.class));
        SessaoResponse response = sessaoService.encerrar(1L);
        assertTrue(response.getDataHoraFim().isBefore(dataHoraFimPre));
    }

    @Test
    void deveLancarGenericBadRequestExceptionAoAbrirSessaoEmLoteSemAssembleiaIdentificadaEArrayPautaIdsNuloOuVazio() {
        SessaoEmLoteRequest requestEmLote = SessaoEmLoteRequestMock.mocked()
                .withAssembleiaId(1L)
                .withListIdsPautasList(null)
                .mock();
        when(assembleiaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GenericBadRequestException.class, () -> sessaoService.abrirEmLote(requestEmLote));
    }

    @Test
    void deveLancarGenericBadRequestExceptionAoAbrirSessaoEmLoteVatacaoLivreFalseEArrayAssociadosNuloOuVazio() {
        SessaoEmLoteRequest requestEmLote = SessaoEmLoteRequestMock.mocked()
                .withAssembleiaId(1L)
                .withVotacaoLivre(Boolean.FALSE)
                .withListAssociadosList(null)
                .mock();
        Assembleia assembleia = assembleiaMapper.toEntity(AssembleiaRequestMock.mocked().mock());
        assembleia.setId(1L);
        for (int i = 0; i < assembleia.getPautas().size(); i++) {
            long idPauta = i + 1L;
            assembleia.getPautas().get(i).setId(idPauta);
            when(pautaRepository.findById(idPauta)).thenReturn(Optional.of(assembleia.getPautas().get(i)));
        }
        when(assembleiaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GenericBadRequestException.class, () -> sessaoService.abrirEmLote(requestEmLote));
    }

    @Test
    void deveConfirmarEnvioDeResultadoComSucesso() {
        Map<Voto, Long> votos = new HashMap<>();
        votos.put(Voto.SIM, 5L);
        votos.put(Voto.NAO, 3L);
        ContagemVotosResponse contagemResponse = ContagemVotosResponse.builder()
                .sessaoId(entity1.getId())
                .pauta(pautaMapper.toResponse(entity1.getPauta()))
                .votos(votos)
                .build();
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.of(entity1));
        doAnswer(invocation -> {
            Sessao sessao = invocation.getArgument(0);
            assertTrue(sessao.getResultadoEnviado());
            return sessao;
        }).when(sessaoRepository).save(entity1);
        sessaoService.confirmarEnvioDeResultado(contagemResponse);
        verify(sessaoRepository, times(1)).save(entity1);
    }

    @Test
    void deveLancarGenericNotFoundExceptionAoBuscarSessaoPorId() {
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GenericNotFoundException.class, () -> sessaoService.buscarPorId(2L));
    }

    @Test
    void deveLancarGenericNotFoundExceptionAoProrrogarSessao() {
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GenericNotFoundException.class, () -> sessaoService.prorrogar(2L, request));
    }

    @Test
    void deveLancarGenericNotFoundExceptionAoEncerrarSessao() {
        when(sessaoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GenericNotFoundException.class, () -> sessaoService.encerrar(2L));
    }

    @Test
    void deveApagarComSucesso() {
        sessaoService.apagar(1L);
        verify(sessaoRepository, times(1)).deleteById(1L);
    }
}

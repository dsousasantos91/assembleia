package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.PautaMapper;
import br.com.dsousasantos91.assembleia.mock.PautaRequestMock;
import br.com.dsousasantos91.assembleia.repository.PautaRepository;
import br.com.dsousasantos91.assembleia.service.dto.request.PautaRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.PautaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class PautaServiceTest {

    @Autowired
    private PautaService pautaService;

    @Autowired
    private PautaMapper pautaMapper;

    @MockBean
    private PautaRepository pautaRepository;

    private PautaRequest request;
    private Pauta entity1;

    @BeforeEach
    public void setUp() {
        request = PautaRequestMock.mocked().mock();
        entity1 = pautaMapper.toEntity(request);
        entity1.setId(1L);
    }

    @Test
    void devePesquisarPautasPaginadasComSucesso() {
        Pauta entity2 = pautaMapper.toEntity(request);
        entity2.setId(2L);
        PageRequest pageable = PageRequest.of(0, 2);
        List<Pauta> pautaList = List.of(entity1, entity2);
        PageImpl<Pauta> pageResponse = new PageImpl<>(pautaList, pageable, pautaList.size());
        when(pautaRepository.findAll(any(Pageable.class))).thenReturn(pageResponse);
        Page<PautaResponse> response = pautaService.pesquisar(pageable);
        List<Long> idsPautas = response.stream().map(PautaResponse::getId).collect(toList());
        assertEquals(response.getPageable().getPageNumber(), pageable.getPageNumber());
        assertEquals(response.getPageable().getPageSize(), pageable.getPageSize());
        assertEquals(idsPautas.size(), 2);
        assertTrue(idsPautas.contains(entity1.getId()));
        assertTrue(idsPautas.contains(entity2.getId()));
    }

    @Test
    void deveBuscarPautaPorIdComSucesso() {
        when(pautaRepository.findById(anyLong())).thenReturn(Optional.of(entity1));
        PautaResponse response = pautaService.buscarPorId(1L);
        assertEquals(response.getId(), entity1.getId());
        assertEquals(response.getTitulo(), entity1.getTitulo());
        assertEquals(response.getDescricao(), entity1.getDescricao());
    }

    @Test
    void deveAtualizarPautaComSucesso() {
        when(pautaRepository.findById(anyLong())).thenReturn(Optional.of(entity1));
        when(pautaRepository.save(any(Pauta.class))).thenReturn(entity1);
        PautaResponse response = pautaService.atualizar(1L, request);
        assertEquals(response.getId(), entity1.getId());
        assertEquals(response.getTitulo(), entity1.getTitulo());
        assertEquals(response.getDescricao(), entity1.getDescricao());
    }

    @Test
    void deveLancarGenericNotFoundExceptionAoBuscarPautaPorId() {
        when(pautaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GenericNotFoundException.class, () -> pautaService.buscarPorId(2L));
    }

    @Test
    void deveLancarGenericNotFoundExceptionAoAtualizarPauta() {
        when(pautaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GenericNotFoundException.class, () -> pautaService.atualizar(2L, request));
    }

    @Test
    void deveApagarComSucesso() {
        pautaService.apagar(1L);
        verify(pautaRepository, times(1)).deleteById(1L);
    }
}

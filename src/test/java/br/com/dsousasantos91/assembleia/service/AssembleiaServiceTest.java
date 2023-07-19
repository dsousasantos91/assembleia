package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.domain.Assembleia;
import br.com.dsousasantos91.assembleia.exception.GenericNotFoundException;
import br.com.dsousasantos91.assembleia.mapper.AssembleiaMapper;
import br.com.dsousasantos91.assembleia.mock.AssembleiaRequestMock;
import br.com.dsousasantos91.assembleia.mock.AssembleiaUpdateRequestMock;
import br.com.dsousasantos91.assembleia.repository.AssembleiaRepository;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaUpdateRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.AssembleiaResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class AssembleiaServiceTest {

    @Autowired
    private AssembleiaService assembleiaService;

    @Autowired
    private AssembleiaMapper assembleiaMapper;

    @MockBean
    private AssembleiaRepository assembleiaRepository;

    private AssembleiaRequest request;
    private Assembleia entity1;

    @BeforeEach
    public void setUp() {
        request = AssembleiaRequestMock.mocked().mock();
        entity1 = assembleiaMapper.toEntity(request);
        entity1.setId(1L);
        entity1.getLocal().setId(1L);
    }

    @Test
    void deveCriarAssembleiaComSucesso() {
        when(assembleiaRepository.save(any(Assembleia.class))).thenReturn(entity1);
        AssembleiaResponse response = assembleiaService.criar(request);
        assertNotNull(response.getId());
        assertEquals(response.getTipoAssembleia(), request.getTipoAssembleia().getValue());
        assertEquals(response.getDataHoraInicioApuracao(), request.getDataHoraInicioApuracao());
        assertEquals(response.getDataHoraFimApuracao(), request.getDataHoraFimApuracao());
        assertEquals(response.getCooperativa(), request.getCooperativa());
        assertEquals(response.getPresidente(), request.getPresidente());
        assertEquals(response.getSecretario(), request.getSecretario());
        assertEquals(response.getPautas().size(), request.getPautas().size());
        assertNotNull(response.getLocal().getId());
        assertEquals(response.getLocal().getCep(), request.getLocal().getCep());
        assertEquals(response.getLocal().getLogradouro(), request.getLocal().getLogradouro());
        assertEquals(response.getLocal().getBairro(), request.getLocal().getBairro());
        assertEquals(response.getLocal().getCidade(), request.getLocal().getCidade());
        assertEquals(response.getLocal().getUf(), request.getLocal().getUf());
        assertEquals(response.getLocal().getComplemento(), request.getLocal().getComplemento());
    }

    @Test
    void devePesquisarAssembleiasPaginadasComSucesso() {
        Assembleia entity2 = assembleiaMapper.toEntity(request);
        entity2.setId(2L);
        entity2.getLocal().setId(2L);
        PageRequest pageable = PageRequest.of(0, 2);
        List<Assembleia> assembleiaList = List.of(entity1, entity2);
        PageImpl<Assembleia> pageResponse = new PageImpl<>(assembleiaList, pageable, assembleiaList.size());
        when(assembleiaRepository.findAll(any(Pageable.class))).thenReturn(pageResponse);
        Page<AssembleiaResponse> response = assembleiaService.pesquisar(pageable);
        List<Long> idsAssembleias = response.stream().map(AssembleiaResponse::getId).toList();
        assertEquals(response.getPageable().getPageNumber(), pageable.getPageNumber());
        assertEquals(response.getPageable().getPageSize(), pageable.getPageSize());
        assertEquals(idsAssembleias.size(), 2);
        assertTrue(idsAssembleias.contains(entity1.getId()));
        assertTrue(idsAssembleias.contains(entity2.getId()));
    }

    @Test
    void deveBuscarAssembleiaPorIdComSucesso() {
        when(assembleiaRepository.findById(anyLong())).thenReturn(Optional.of(entity1));
        AssembleiaResponse response = assembleiaService.buscarPorId(1L);
        assertEquals(response.getId(), entity1.getId());
        assertEquals(response.getTipoAssembleia(), entity1.getTipoAssembleia().getValue());
        assertEquals(response.getDataHoraInicioApuracao(), entity1.getDataHoraInicioApuracao());
        assertEquals(response.getDataHoraFimApuracao(), entity1.getDataHoraFimApuracao());
        assertEquals(response.getCooperativa(), entity1.getCooperativa());
        assertEquals(response.getPresidente(), entity1.getPresidente());
        assertEquals(response.getSecretario(), entity1.getSecretario());
        assertEquals(response.getPautas().size(), entity1.getPautas().size());
        assertEquals(response.getLocal().getId(), entity1.getLocal().getId());
        assertEquals(response.getLocal().getCep(), entity1.getLocal().getCep());
        assertEquals(response.getLocal().getLogradouro(), entity1.getLocal().getLogradouro());
        assertEquals(response.getLocal().getBairro(), entity1.getLocal().getBairro());
        assertEquals(response.getLocal().getCidade(), entity1.getLocal().getCidade());
        assertEquals(response.getLocal().getUf(), entity1.getLocal().getUf());
        assertEquals(response.getLocal().getComplemento(), entity1.getLocal().getComplemento());
    }

    @Test
    void deveAtualizarAssembleiaComSucesso() {
        AssembleiaUpdateRequest updateRequest = AssembleiaUpdateRequestMock.mocked()
                .withDataHoraInicioApuracao(request.getDataHoraInicioApuracao())
                .withDataHoraFimApuracao(request.getDataHoraFimApuracao())
                .mock();
        when(assembleiaRepository.findById(anyLong())).thenReturn(Optional.of(entity1));
        when(assembleiaRepository.save(any(Assembleia.class))).thenReturn(entity1);
        AssembleiaResponse response = assembleiaService.atualizar(1L, updateRequest);
        assertEquals(response.getId(), entity1.getId());
        assertEquals(response.getTipoAssembleia(), updateRequest.getTipoAssembleia().getValue());
        assertEquals(response.getDataHoraInicioApuracao(), updateRequest.getDataHoraInicioApuracao());
        assertEquals(response.getDataHoraFimApuracao(), updateRequest.getDataHoraFimApuracao());
        assertEquals(response.getCooperativa(), updateRequest.getCooperativa());
        assertEquals(response.getPresidente(), updateRequest.getPresidente());
        assertEquals(response.getSecretario(), updateRequest.getSecretario());
        assertEquals(response.getLocal().getId(), entity1.getLocal().getId());
        assertEquals(response.getLocal().getCep(), updateRequest.getLocal().getCep());
        assertEquals(response.getLocal().getLogradouro(), updateRequest.getLocal().getLogradouro());
        assertEquals(response.getLocal().getBairro(), updateRequest.getLocal().getBairro());
        assertEquals(response.getLocal().getCidade(), updateRequest.getLocal().getCidade());
        assertEquals(response.getLocal().getUf(), updateRequest.getLocal().getUf());
        assertEquals(response.getLocal().getComplemento(), updateRequest.getLocal().getComplemento());
    }

    @Test
    void deveEncerrarAssembleiaComSucesso() {
        when(assembleiaRepository.findById(anyLong())).thenReturn(Optional.of(entity1));
        when(assembleiaRepository.save(any(Assembleia.class))).thenReturn(entity1);
        AssembleiaResponse response = assembleiaService.encerrar(1L);
        assertTrue(response.getDataHoraFimApuracao().isBefore(request.getDataHoraFimApuracao()));
    }

    @Test
    void deveLancarGenericNotFoundExceptionAoBuscarAssembleiaPorId() {
        when(assembleiaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GenericNotFoundException.class, () -> assembleiaService.buscarPorId(2L));
    }

    @Test
    void deveLancarGenericNotFoundExceptionAoAtualizarAssembleia() {
        AssembleiaUpdateRequest updateRequest = AssembleiaUpdateRequestMock.mocked().mock();
        when(assembleiaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GenericNotFoundException.class, () -> assembleiaService.atualizar(2L, updateRequest));
    }

    @Test
    void deveLancarGenericNotFoundExceptionAoEncerrarAssembleia() {
        when(assembleiaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GenericNotFoundException.class, () -> assembleiaService.encerrar(2L));
    }

    @Test
    void deveApagarComSucesso() {
        assembleiaService.apagar(1L);
        verify(assembleiaRepository, times(1)).deleteById(1L);
    }
}

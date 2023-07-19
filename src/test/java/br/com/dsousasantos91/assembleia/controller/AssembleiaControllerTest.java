package br.com.dsousasantos91.assembleia.controller;

import br.com.dsousasantos91.assembleia.domain.Assembleia;
import br.com.dsousasantos91.assembleia.mapper.AssembleiaMapper;
import br.com.dsousasantos91.assembleia.mock.AssembleiaRequestMock;
import br.com.dsousasantos91.assembleia.service.AssembleiaService;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaUpdateRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.AssembleiaResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AssembleiaControllerTest {

    public static final MediaType APPLICATION_JSON_UTF_8 = new MediaType(MediaType.APPLICATION_JSON, UTF_8);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AssembleiaMapper assembleiaMapper;

    @MockBean
    private AssembleiaService assembleiaService;

    private AssembleiaResponse response;
    private AssembleiaRequest request;

    @BeforeEach
    public void setUp() {
        request = AssembleiaRequestMock.mocked().mock();
        Assembleia assembleia = assembleiaMapper.toEntity(request);
        response = assembleiaMapper.toResponse(assembleia);
    }

    @Test
    void deveCriarERetornarStatusCode201() throws Exception {
        when(assembleiaService.criar(any(AssembleiaRequest.class))).thenReturn(response);
        mockMvc.perform(
                post("/v1/assembleia")
                        .contentType(APPLICATION_JSON_UTF_8)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void devePesquisarERetornarStatusCode200() throws Exception {
        List<AssembleiaResponse> assembleiaList = List.of(response);
        PageRequest pageable = PageRequest.of(0, 1);
        PageImpl<AssembleiaResponse> pageResponse = new PageImpl<>(assembleiaList, pageable, assembleiaList.size());
        when(assembleiaService.pesquisar(any(Pageable.class))).thenReturn(pageResponse);
        mockMvc.perform(get("/v1/assembleia")
                        .contentType(APPLICATION_JSON_UTF_8)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(pageResponse)));
    }

    @Test
    void deveBuscaoPorIdERetornarStatusCode200() throws Exception {
        when(assembleiaService.buscarPorId(anyLong())).thenReturn(response);
        mockMvc.perform(get("/v1/assembleia/" + 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void deveAtualizarERetornarStatusCode200() throws Exception {
        when(assembleiaService.atualizar(anyLong(), any(AssembleiaUpdateRequest.class))).thenReturn(response);
        mockMvc.perform(
                        put("/v1/assembleia/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void deveEncerrarERetornarStatusCode200() throws Exception {
        when(assembleiaService.encerrar(anyLong())).thenReturn(response);
        mockMvc.perform(put("/v1/assembleia/encerrar/" + 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void deveApagarERetornarStatusCode204() throws Exception {
        doNothing().when(assembleiaService).apagar(anyLong());
        mockMvc.perform(delete("/v1/assembleia/" + 1))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}

package br.com.dsousasantos91.assembleia.controller;

import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.mapper.PautaMapper;
import br.com.dsousasantos91.assembleia.mock.PautaRequestMock;
import br.com.dsousasantos91.assembleia.service.PautaService;
import br.com.dsousasantos91.assembleia.service.dto.request.PautaRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.PautaResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PautaControllerTest {

    public static final MediaType APPLICATION_JSON_UTF_8 = new MediaType(MediaType.APPLICATION_JSON, UTF_8);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PautaMapper pautaMapper;

    @MockBean
    private PautaService pautaService;

    private PautaResponse response;
    private PautaRequest request;

    @BeforeEach
    public void setUp() {
        request = PautaRequestMock.mocked().mock();
        Pauta pauta = pautaMapper.toEntity(request);
        response = pautaMapper.toResponse(pauta);
    }

    @Test
    void devePesquisarPautaERetornarStatus200() throws Exception {
        List<PautaResponse> pautaResponseList = List.of(response);
        PageRequest pageable = PageRequest.of(0, 1);
        PageImpl<PautaResponse> pageResponse = new PageImpl<>(pautaResponseList, pageable, pautaResponseList.size());
        when(pautaService.pesquisar(any(Pageable.class))).thenReturn(pageResponse);
        mockMvc.perform(get("/v1/pauta")
                        .contentType(APPLICATION_JSON_UTF_8)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(pageResponse)));
    }

    @Test
    void deveBuscaoPorIdPautaERetornarStatus200() throws Exception {
        when(pautaService.buscarPorId(anyLong())).thenReturn(response);
        mockMvc.perform(get("/v1/pauta/" + 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void deveAtualizarPautaERetornarStatus200() throws Exception {
        when(pautaService.atualizar(anyLong(), any(PautaRequest.class))).thenReturn(response);
        mockMvc.perform(
                        put("/v1/pauta/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void deveApagarPautaERetornarStatus204() throws Exception {
        doNothing().when(pautaService).apagar(anyLong());
        mockMvc.perform(delete("/v1/pauta/" + 1))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}

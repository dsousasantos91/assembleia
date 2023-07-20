package br.com.dsousasantos91.assembleia.controller;

import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.mapper.PautaMapper;
import br.com.dsousasantos91.assembleia.mapper.SessaoMapper;
import br.com.dsousasantos91.assembleia.mock.PautaRequestMock;
import br.com.dsousasantos91.assembleia.mock.SessaoEmLoteRequestMock;
import br.com.dsousasantos91.assembleia.mock.SessaoRequestMock;
import br.com.dsousasantos91.assembleia.service.SessaoService;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoEmLoteRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.SessaoResponse;
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

import java.time.LocalDateTime;
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
class SessaoControllerTest {

    public static final MediaType APPLICATION_JSON_UTF_8 = new MediaType(MediaType.APPLICATION_JSON, UTF_8);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessaoMapper sessaoMapper;

    @Autowired
    private PautaMapper pautaMapper;

    @MockBean
    private SessaoService sessaoService;

    private SessaoResponse response;
    private SessaoRequest request;

    @BeforeEach
    public void setUp() {
        request = SessaoRequestMock.mocked().mock();
        Sessao sessao = sessaoMapper.toEntity(request);
        Pauta pauta = pautaMapper.toEntity(PautaRequestMock.mocked().mock());
        pauta.setId(1L);
        response = SessaoResponse.builder()
                .id(1L)
                .pauta(pautaMapper.toResponse(pauta))
                .dataHoraInicio(LocalDateTime.now())
                .dataHoraFim(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    @Test
    void deveAbrirERetornarStatus201() throws Exception {
        when(sessaoService.abrir(any(SessaoRequest.class))).thenReturn(response);
        mockMvc.perform(
                        post("/v1/sessao/abrir")
                                .contentType(APPLICATION_JSON_UTF_8)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void deveAbrirEmLoteERetornarStatus201() throws Exception {
        SessaoEmLoteRequest loteRequest = SessaoEmLoteRequestMock.mocked()
                .withListIdsPautasList(null)
                .withVotacaoLivre(Boolean.FALSE)
                .mock();

        SessaoResponse response2 = SessaoResponse.builder()
                .id(2L)
                .pauta(pautaMapper.toResponse(Pauta.builder().id(2L).build()))
                .dataHoraInicio(LocalDateTime.now())
                .dataHoraFim(LocalDateTime.now().plusMinutes(5))
                .build();
        SessaoResponse response3 = SessaoResponse.builder()
                .id(2L)
                .pauta(pautaMapper.toResponse(Pauta.builder().id(3L).build()))
                .dataHoraInicio(LocalDateTime.now())
                .dataHoraFim(LocalDateTime.now().plusMinutes(5))
                .build();
        List<SessaoResponse> responses = List.of(response, response2, response3);

        when(sessaoService.abrirEmLote(any(SessaoEmLoteRequest.class))).thenReturn(responses);
        mockMvc.perform(
                        post("/v1/sessao/abrirEmLote")
                                .contentType(APPLICATION_JSON_UTF_8)
                                .content(objectMapper.writeValueAsString(loteRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(responses)));
    }

    @Test
    void devePesquisarERetornarStatus200() throws Exception {
        List<SessaoResponse> sessaoResponseList = List.of(response);
        PageRequest pageable = PageRequest.of(0, 1);
        PageImpl<SessaoResponse> pageResponse = new PageImpl<>(sessaoResponseList, pageable, sessaoResponseList.size());
        when(sessaoService.pesquisar(any(Pageable.class))).thenReturn(pageResponse);
        mockMvc.perform(get("/v1/sessao")
                        .contentType(APPLICATION_JSON_UTF_8)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(pageResponse)));
    }

    @Test
    void deveBuscaoPorIdERetornarStatus200() throws Exception {
        when(sessaoService.buscarPorId(anyLong())).thenReturn(response);
        mockMvc.perform(get("/v1/sessao/" + 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void deveProrrogarERetornarStatus200() throws Exception {
        when(sessaoService.prorrogar(anyLong(), any(SessaoRequest.class))).thenReturn(response);
        mockMvc.perform(put("/v1/sessao/prorrogar/" + 1)
                        .contentType(APPLICATION_JSON_UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void deveEncerrarERetornarStatus200() throws Exception{
        when(sessaoService.encerrar(anyLong())).thenReturn(response);
        mockMvc.perform(put("/v1/sessao/encerrar/" + 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void deveApagarERetornarStatus204() throws Exception {
        doNothing().when(sessaoService).apagar(anyLong());
        mockMvc.perform(delete("/v1/sessao/" + 1))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}

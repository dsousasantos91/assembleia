package br.com.dsousasantos91.assembleia.controller;

import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.domain.Voto;
import br.com.dsousasantos91.assembleia.domain.enumeration.VotoEnum;
import br.com.dsousasantos91.assembleia.mapper.VotoMapper;
import br.com.dsousasantos91.assembleia.mock.VotacaoRequestMock;
import br.com.dsousasantos91.assembleia.service.VotoService;
import br.com.dsousasantos91.assembleia.service.dto.request.VotoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import br.com.dsousasantos91.assembleia.service.dto.response.VotoResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VotoControllerTest {

    public static final MediaType APPLICATION_JSON_UTF_8 = new MediaType(MediaType.APPLICATION_JSON, UTF_8);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VotoMapper votoMapper;

    private VotoRequest request;
    private VotoResponse response;
    private Voto voto;
    private Sessao sessao;

    @BeforeEach
    public void setUp() {
        request = VotacaoRequestMock.mocked().mock();
        voto = votoMapper.toEntity(request);
        sessao = Sessao.builder()
                .id(1L)
                .sessaoPrivada(Boolean.FALSE)
                .pauta(Pauta.builder().id(1L).build())
                .dataHoraInicio(LocalDateTime.now())
                .dataHoraFim(LocalDateTime.now().plusHours(10))
                .build();
        voto.setId(1L);
        voto.setSessao(sessao);
        voto.setDataHoraVoto(LocalDateTime.now());
        response = votoMapper.toResponse(voto);
    }

    @MockBean
    private VotoService votoService;

    @Test
    void deveVotarERetornarStatus201() throws Exception {
        when(votoService.votar(any(VotoRequest.class))).thenReturn(response);
        mockMvc.perform(
                        post("/api/v1/voto/votar")
                                .contentType(APPLICATION_JSON_UTF_8)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void devePesquisarERetornarStatus200() throws Exception {
        List<VotoResponse> votoResponseList = singletonList(response);
        PageRequest pageable = PageRequest.of(0, 1);
        PageImpl<VotoResponse> pageResponse = new PageImpl<>(votoResponseList, pageable, votoResponseList.size());
        when(votoService.pesquisar(any(Pageable.class))).thenReturn(pageResponse);
        mockMvc.perform(get("/api/v1/voto")
                        .contentType(APPLICATION_JSON_UTF_8)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(pageResponse)));
    }

    @Test
    void deveContabilizarERetornarStatus200() throws Exception{
        Map<VotoEnum, Long> votos = new HashMap<>();
        votos.put(VotoEnum.SIM, 5L);
        votos.put(VotoEnum.NAO, 4L);
        ContagemVotosResponse responseContagem = ContagemVotosResponse.builder()
                .sessaoId(1L)
                .pauta(response.getSessao().getPauta())
                .votos(votos)
                .build();
        when(votoService.contabilizar(anyLong())).thenReturn(responseContagem);
        mockMvc.perform(
                        get("/api/v1/voto/contabilizar/sessao/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(responseContagem)));
    }

    @Test
    void deveAlterarVotoERetornarStatus200() throws Exception {
        when(votoService.alterar(anyLong(), anyString())).thenReturn(response);
        mockMvc.perform(
                        put("/api/v1/voto/alterar/sessao/" + 1 + "/associado/" + voto.getAssociado().getCpf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }
}

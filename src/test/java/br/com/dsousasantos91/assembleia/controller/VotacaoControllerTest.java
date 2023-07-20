package br.com.dsousasantos91.assembleia.controller;

import br.com.dsousasantos91.assembleia.domain.Pauta;
import br.com.dsousasantos91.assembleia.domain.Sessao;
import br.com.dsousasantos91.assembleia.domain.Votacao;
import br.com.dsousasantos91.assembleia.domain.enumeration.Voto;
import br.com.dsousasantos91.assembleia.mapper.VotacaoMapper;
import br.com.dsousasantos91.assembleia.mock.VotacaoRequestMock;
import br.com.dsousasantos91.assembleia.service.VotacaoService;
import br.com.dsousasantos91.assembleia.service.dto.request.VotacaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import br.com.dsousasantos91.assembleia.service.dto.response.VotacaoResponse;
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
class VotacaoControllerTest {

    public static final MediaType APPLICATION_JSON_UTF_8 = new MediaType(MediaType.APPLICATION_JSON, UTF_8);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VotacaoMapper votacaoMapper;

    private VotacaoRequest request;
    private VotacaoResponse response;
    private Votacao votacao;
    private Sessao sessao;

    @BeforeEach
    public void setUp() {
        request = VotacaoRequestMock.mocked().mock();
        votacao = votacaoMapper.toEntity(request);
        sessao = Sessao.builder()
                .id(1L)
                .votacaoLivre(Boolean.FALSE)
                .pauta(Pauta.builder().id(1L).build())
                .dataHoraInicio(LocalDateTime.now())
                .dataHoraFim(LocalDateTime.now().plusHours(10))
                .build();
        votacao.setId(1L);
        votacao.setSessao(sessao);
        votacao.setDataHoraVoto(LocalDateTime.now());
        response = votacaoMapper.toResponse(votacao);
    }

    @MockBean
    private VotacaoService votacaoService;

    @Test
    void deveVotarERetornarStatus201() throws Exception {
        when(votacaoService.votar(any(VotacaoRequest.class))).thenReturn(response);
        mockMvc.perform(
                        post("/v1/votacao/votar")
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
        List<VotacaoResponse> votacaoResponseList = singletonList(response);
        PageRequest pageable = PageRequest.of(0, 1);
        PageImpl<VotacaoResponse> pageResponse = new PageImpl<>(votacaoResponseList, pageable, votacaoResponseList.size());
        when(votacaoService.pesquisar(any(Pageable.class))).thenReturn(pageResponse);
        mockMvc.perform(get("/v1/votacao")
                        .contentType(APPLICATION_JSON_UTF_8)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(pageResponse)));
    }

    @Test
    void deveContabilizarERetornarStatus200() throws Exception{
        Map<Voto, Long> votos = new HashMap<>();
        votos.put(Voto.SIM, 5L);
        votos.put(Voto.NAO, 4L);
        ContagemVotosResponse responseContagem = ContagemVotosResponse.builder()
                .sessaoId(1L)
                .pauta(response.getSessao().getPauta())
                .votos(votos)
                .build();
        when(votacaoService.contabilizar(anyLong())).thenReturn(responseContagem);
        mockMvc.perform(
                        get("/v1/votacao/contabilizar/sessao/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(responseContagem)));
    }

    @Test
    void deveAlterarVotoERetornarStatus200() throws Exception {
        when(votacaoService.alterarVoto(anyLong(), anyString())).thenReturn(response);
        mockMvc.perform(
                        put("/v1/votacao/alterarVoto/sessao/" + 1 + "/associado/" + votacao.getAssociado().getCpf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF_8))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }
}

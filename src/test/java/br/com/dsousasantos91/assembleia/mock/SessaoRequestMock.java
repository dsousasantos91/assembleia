package br.com.dsousasantos91.assembleia.mock;

import br.com.dsousasantos91.assembleia.service.dto.request.AssociadoRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.SessaoRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.TempoSessaoRequest;

import java.util.Arrays;

public class SessaoRequestMock {
    private SessaoRequest element;
    private SessaoRequestMock(){}

    public static SessaoRequestMock mocked() {
        SessaoRequestMock mock = new SessaoRequestMock();
        initializeDefaultData(mock);
        return mock;
    }

    public static void initializeDefaultData(SessaoRequestMock mock) {
        mock.element = SessaoRequest.builder()
                .pautaId(1L)
                .votacaoLivre(true)
                .associados(AssociadoRequestMock.mocked().mockList())
                .tempoSessao(TempoSessaoRequestMock.mocked().mock())
                .build();
    }

    public SessaoRequestMock withPautaId(Long param) {
        element.setPautaId(param);
        return this;
    }

    public SessaoRequestMock withVotacaoLivre(Boolean param) {
        element.setVotacaoLivre(param);
        return this;
    }

    public SessaoRequestMock withListAssociados(AssociadoRequest... params) {
        element.setAssociados(Arrays.asList(params));
        return this;
    }

    public SessaoRequestMock withTempoSessao(TempoSessaoRequest param) {
        element.setTempoSessao(param);
        return this;
    }

    public SessaoRequest mock() {
        return element;
    }
}

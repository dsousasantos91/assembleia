package br.com.dsousasantos91.assembleia.mock;

import br.com.dsousasantos91.assembleia.domain.enumeration.Voto;
import br.com.dsousasantos91.assembleia.service.dto.request.AssociadoRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.VotacaoRequest;

public class VotacaoRequestMock {
    private VotacaoRequest element;
    private VotacaoRequestMock(){}

    public static VotacaoRequestMock mocked() {
        VotacaoRequestMock mock = new VotacaoRequestMock();
        initializeDefaultData(mock);
        return mock;
    }

    public static void initializeDefaultData(VotacaoRequestMock mock) {
        mock.element = VotacaoRequest.builder()
                .sessaoId(1L)
                .associado(AssociadoRequestMock.mocked().mock())
                .voto(Voto.SIM)
                .build();
    }

    public VotacaoRequestMock withSessaoId(Long param) {
        element.setSessaoId(param);
        return this;
    }

    public VotacaoRequestMock withAssociado(AssociadoRequest param) {
        element.setAssociado(param);
        return this;
    }

    public VotacaoRequestMock withVoto(Voto param) {
        element.setVoto(param);
        return this;
    }

    public VotacaoRequest mock() {
        return element;
    }
}

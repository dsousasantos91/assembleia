package br.com.dsousasantos91.assembleia.mock;

import br.com.dsousasantos91.assembleia.domain.enumeration.VotoEnum;
import br.com.dsousasantos91.assembleia.service.dto.request.AssociadoRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.VotoRequest;

public class VotacaoRequestMock {
    private VotoRequest element;
    private VotacaoRequestMock(){}

    public static VotacaoRequestMock mocked() {
        VotacaoRequestMock mock = new VotacaoRequestMock();
        initializeDefaultData(mock);
        return mock;
    }

    public static void initializeDefaultData(VotacaoRequestMock mock) {
        mock.element = VotoRequest.builder()
                .sessaoId(1L)
                .associado(AssociadoRequestMock.mocked().mock())
                .voto(VotoEnum.SIM)
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

    public VotacaoRequestMock withVoto(VotoEnum param) {
        element.setVoto(param);
        return this;
    }

    public VotoRequest mock() {
        return element;
    }
}

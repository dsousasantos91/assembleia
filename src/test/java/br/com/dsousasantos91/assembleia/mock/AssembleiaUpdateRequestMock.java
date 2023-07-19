package br.com.dsousasantos91.assembleia.mock;

import br.com.dsousasantos91.assembleia.domain.enumeration.TipoAssembleia;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaUpdateRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.EnderecoRequest;

import java.time.LocalDateTime;

public class AssembleiaUpdateRequestMock {
    private AssembleiaUpdateRequest element;
    private AssembleiaUpdateRequestMock(){}

    public static AssembleiaUpdateRequestMock mocked() {
        AssembleiaUpdateRequestMock mock = new AssembleiaUpdateRequestMock();
        initializeDefaultData(mock);
        return mock;
    }

    public static void initializeDefaultData(AssembleiaUpdateRequestMock mock) {
        mock.element = AssembleiaUpdateRequest.builder()
                .tipoAssembleia(TipoAssembleia.EXTRAORDINARIA)
                .dataHoraInicioApuracao(LocalDateTime.now())
                .dataHoraFimApuracao(LocalDateTime.now().plusMinutes(10))
                .cooperativa("Cooperativa União")
                .local(EnderecoRequestMock.mocked().mock())
                .presidente("Louise Prohaska")
                .secretario("Lindsey Wyman")
                .build();
    }

    public AssembleiaUpdateRequestMock withTipoAssembleia(TipoAssembleia param) {
        element.setTipoAssembleia(param);
        return this;
    }

    public AssembleiaUpdateRequestMock withDataHoraInicioApuracao(LocalDateTime param) {
        element.setDataHoraInicioApuracao(param);
        return this;
    }

    public AssembleiaUpdateRequestMock withDataHoraFimApuracao(LocalDateTime param) {
        element.setDataHoraFimApuracao(param);
        return this;
    }

    public AssembleiaUpdateRequestMock withCooperativa(String param) {
        element.setCooperativa(param);
        return this;
    }

    public AssembleiaUpdateRequestMock withLocal(EnderecoRequest param) {
        element.setLocal(param);
        return this;
    }

    public AssembleiaUpdateRequestMock withPresidente(String param) {
        element.setPresidente(param);
        return this;
    }

    public AssembleiaUpdateRequestMock withSecretario(String param) {
        element.setSecretario(param);
        return this;
    }

    public AssembleiaUpdateRequest mock() {
        return element;
    }
}

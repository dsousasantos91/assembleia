package br.com.dsousasantos91.assembleia.mock;

import br.com.dsousasantos91.assembleia.domain.enumeration.TipoAssembleiaEnum;
import br.com.dsousasantos91.assembleia.service.dto.request.AssembleiaRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.EnderecoRequest;
import br.com.dsousasantos91.assembleia.service.dto.request.PautaRequest;

import java.time.LocalDateTime;
import java.util.Arrays;

public class AssembleiaRequestMock {
    private AssembleiaRequest element;
    private AssembleiaRequestMock(){}

    public static AssembleiaRequestMock mocked() {
        AssembleiaRequestMock mock = new AssembleiaRequestMock();
        initializeDefaultData(mock);
        return mock;
    }

    public static void initializeDefaultData(AssembleiaRequestMock mock) {
        mock.element = AssembleiaRequest.builder()
                .tipoAssembleia(TipoAssembleiaEnum.EXTRAORDINARIA)
                .dataHoraInicioApuracao(LocalDateTime.now())
                .dataHoraFimApuracao(LocalDateTime.now().plusMinutes(10))
                .cooperativa("Cooperativa União")
                .local(EnderecoRequestMock.mocked().mock())
                .presidente("Louise Prohaska")
                .secretario("Lindsey Wyman")
                .pautas(PautaRequestMock.mocked().mockList())
                .build();
    }

    public AssembleiaRequestMock withTipoAssembleia(TipoAssembleiaEnum param) {
        element.setTipoAssembleia(param);
        return this;
    }

    public AssembleiaRequestMock withDataHoraInicioApuracao(LocalDateTime param) {
        element.setDataHoraInicioApuracao(param);
        return this;
    }

    public AssembleiaRequestMock withDataHoraFimApuracao(LocalDateTime param) {
        element.setDataHoraFimApuracao(param);
        return this;
    }

    public AssembleiaRequestMock withCooperativa(String param) {
        element.setCooperativa(param);
        return this;
    }

    public AssembleiaRequestMock withLocal(EnderecoRequest param) {
        element.setLocal(param);
        return this;
    }

    public AssembleiaRequestMock withPresidente(String param) {
        element.setPresidente(param);
        return this;
    }

    public AssembleiaRequestMock withSecretario(String param) {
        element.setSecretario(param);
        return this;
    }

    public AssembleiaRequestMock withListPautas(PautaRequest... params) {
        element.setPautas(Arrays.asList(params));
        return this;
    }

    public AssembleiaRequest mock() {
        return element;
    }
}

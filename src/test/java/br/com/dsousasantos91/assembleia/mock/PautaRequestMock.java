package br.com.dsousasantos91.assembleia.mock;

import br.com.dsousasantos91.assembleia.service.dto.request.PautaRequest;

import java.util.List;

public class PautaRequestMock {
    private PautaRequest element;
    private List<PautaRequest> elements;
    private PautaRequestMock(){}

    public static PautaRequestMock mocked() {
        PautaRequestMock mock = new PautaRequestMock();
        initializeDefaultData(mock);
        initializeDefaultDataList(mock);
        return mock;
    }

    public static void initializeDefaultData(PautaRequestMock mock) {
        mock.element = PautaRequest.builder()
                .titulo("laoreet sit amet cursus sit")
                .descricao("faucibus a pellentesque sit amet porttitor eget dolor morbi non arcu risus quis varius quam")
                .build();
    }

    public static void initializeDefaultDataList(PautaRequestMock mock) {
        mock.elements = List.of(
                PautaRequest.builder()
                        .titulo("laoreet sit amet cursus sit")
                        .descricao("faucibus a pellentesque sit amet porttitor eget dolor morbi non arcu risus")
                        .build(),
                PautaRequest.builder()
                        .titulo("odio facilisis mauris sit amet")
                        .descricao("purus viverra accumsan in nisl nisi scelerisque eu ultrices vitae auctor eu")
                        .build(),
                PautaRequest.builder()
                        .titulo("ipsum dolor sit amet consectetur")
                        .descricao("tellus mauris a diam maecenas sed enim ut sem viverra aliquet eget")
                        .build()
        );
    }

    public PautaRequestMock withTitulo(String param) {
        element.setTitulo(param);
        return this;
    }

    public PautaRequestMock withDescricao(String param) {
        element.setDescricao(param);
        return this;
    }

    public PautaRequest mock() {
        return element;
    }
    public List<PautaRequest> mockList() {
        return elements;
    }
}

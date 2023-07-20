package br.com.dsousasantos91.assembleia.mock;

import br.com.dsousasantos91.assembleia.service.dto.request.AssociadoRequest;

import java.util.Arrays;
import java.util.List;

public class AssociadoRequestMock {
    private AssociadoRequest element;
    private List<AssociadoRequest> elements;
    private AssociadoRequestMock(){}

    public static AssociadoRequestMock mocked() {
        AssociadoRequestMock mock = new AssociadoRequestMock();
        initializeDefaultData(mock);
        initializeDefaultDataList(mock);
        return mock;
    }

    public static void initializeDefaultData(AssociadoRequestMock mock) {
        mock.element = AssociadoRequest.builder()
                .cpf("45906639020")
                .nome("Jermaine Miller")
                .build();
    }

    public static void initializeDefaultDataList(AssociadoRequestMock mock) {
        mock.elements = Arrays.asList(
                AssociadoRequest.builder()
                        .cpf("45906639020")
                        .nome("Jermaine Miller")
                        .build(),
                AssociadoRequest.builder()
                        .cpf("03620447080")
                        .nome("Tasha Glover")
                        .build(),
                AssociadoRequest.builder()
                        .cpf("23902804041")
                        .nome("John Wuckert")
                        .build()
        );
    }

    public AssociadoRequestMock withCpf(String param) {
        element.setCpf(param);
        return this;
    }

    public AssociadoRequestMock withNome(String param) {
        element.setNome(param);
        return this;
    }

    public AssociadoRequest mock() {
        return element;
    }
    public List<AssociadoRequest> mockList() {
        return elements;
    }
}

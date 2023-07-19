package br.com.dsousasantos91.assembleia.mock;

import br.com.dsousasantos91.assembleia.domain.enumeration.UF;
import br.com.dsousasantos91.assembleia.service.dto.request.EnderecoRequest;

import static br.com.dsousasantos91.assembleia.domain.enumeration.UF.GO;

public class EnderecoRequestMock {
    private EnderecoRequest element;
    private EnderecoRequestMock(){}

    public static EnderecoRequestMock mocked() {
        EnderecoRequestMock mock = new EnderecoRequestMock();
        initializeDefaultData(mock);
        return mock;
    }

    public static void initializeDefaultData(EnderecoRequestMock mock) {
        mock.element = EnderecoRequest.builder()
                .cep("72876-036")
                .logradouro("Quadra 10")
                .bairro("Valparaíso I - Etapa A")
                .cidade("Valparaíso de Goiás")
                .uf(GO)
                .complemento("Lote 10")
                .build();
    }

    public EnderecoRequestMock withCep(String param) {
        element.setCep(param);
        return this;
    }

    public EnderecoRequestMock withLogradouro(String param) {
        element.setLogradouro(param);
        return this;
    }

    public EnderecoRequestMock withBairro(String param) {
        element.setBairro(param);
        return this;
    }

    public EnderecoRequestMock withCidade(String param) {
        element.setCidade(param);
        return this;
    }

    public EnderecoRequestMock withUf(UF param) {
        element.setUf(param);
        return this;
    }

    public EnderecoRequestMock withComplemento(String param) {
        element.setComplemento(param);
        return this;
    }

    public EnderecoRequest mock() {
        return element;
    }
}

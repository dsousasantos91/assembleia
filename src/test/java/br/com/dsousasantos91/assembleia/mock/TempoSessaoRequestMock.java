package br.com.dsousasantos91.assembleia.mock;

import br.com.dsousasantos91.assembleia.service.dto.request.TempoSessaoRequest;

public class TempoSessaoRequestMock {
    private TempoSessaoRequest element;
    private TempoSessaoRequestMock(){}

    public static TempoSessaoRequestMock mocked() {
        TempoSessaoRequestMock mock = new TempoSessaoRequestMock();
        initializeDefaultData(mock);
        return mock;
    }

    public static void initializeDefaultData(TempoSessaoRequestMock mock) {
        mock.element = TempoSessaoRequest.builder()
                .dias(0)
                .horas(0)
                .minutos(1)
                .build();
    }

    public TempoSessaoRequestMock withDias(int param) {
        element.setDias(param);
        return this;
    }

    public TempoSessaoRequestMock withHoras(int param) {
        element.setHoras(param);
        return this;
    }

    public TempoSessaoRequestMock withMinutos(int param) {
        element.setMinutos(param);
        return this;
    }

    public TempoSessaoRequest mock() {
        return element;
    }
}

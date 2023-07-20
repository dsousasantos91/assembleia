package br.com.dsousasantos91.assembleia.domain.enumeration;

import lombok.Getter;

@Getter
public enum VotoEnum {
    SIM("Sim"),
    NAO("Não");

    private final String value;

    VotoEnum(String value) {
        this.value = value;
    }
}

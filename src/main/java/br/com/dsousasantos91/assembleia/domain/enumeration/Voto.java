package br.com.dsousasantos91.assembleia.domain.enumeration;

import lombok.Getter;

@Getter
public enum Voto {
    SIM("Sim"),
    NAO("Não");

    private final String value;

    Voto(String value) {
        this.value = value;
    }
}

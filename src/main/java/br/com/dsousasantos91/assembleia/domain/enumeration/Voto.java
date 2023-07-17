package br.com.dsousasantos91.assembleia.domain.enumeration;

import lombok.Getter;

@Getter
public enum Voto {
    SIM("Sim"),
    NAO("Não");

    private String voto;

    Voto(String voto) {
        this.voto = voto;
    }
}

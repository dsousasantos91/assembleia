package br.com.dsousasantos91.assembleia.domain.enumeration;

import lombok.Getter;

@Getter
public enum TipoAssembleia {
    ORDINARIA("Ordinária"),
    EXTRAORDINARIA("Extraordinária");

    private final String value;

    TipoAssembleia(String value) {
        this.value = value;
    }
}

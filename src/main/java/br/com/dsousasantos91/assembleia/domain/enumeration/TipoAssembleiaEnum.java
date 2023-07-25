package br.com.dsousasantos91.assembleia.domain.enumeration;

import lombok.Getter;

@Getter
public enum TipoAssembleiaEnum {
    ORDINARIA("Ordinária"),
    EXTRAORDINARIA("Extraordinária");

    private final String value;

    TipoAssembleiaEnum(String value) {
        this.value = value;
    }
}

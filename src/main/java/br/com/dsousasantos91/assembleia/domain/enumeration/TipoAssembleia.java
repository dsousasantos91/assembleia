package br.com.dsousasantos91.assembleia.domain.enumeration;

import lombok.Getter;

@Getter
public enum TipoAssembleia {
    ORDINARIA("Ordinária"),
    EXTRAORDINARIA("Extraordinária");

    private String nome;

    TipoAssembleia(String nome) {
        this.nome = nome;
    }
}

package br.com.dsousasantos91.assembleia.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pauta")
public class Pauta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(min = 5, max = 50, message = "É obrigatório digitar um mínimo de {1} para {0}")
    @NotBlank(message = "{0} é obrigatório")
    private String titulo;

    @Size(min = 10, max = 1000, message = "É obrigatório digitar um mínimo de {1} para {0}")
    @NotBlank(message = "{0} é obrigatório")
    private String descricao;

    @ManyToOne(cascade = CascadeType.ALL)
    private Assembleia assembleia;

    @OneToOne(mappedBy = "pauta", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, orphanRemoval = true)
    private Sessao sessao;
}

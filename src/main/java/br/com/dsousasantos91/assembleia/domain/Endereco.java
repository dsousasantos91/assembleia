package br.com.dsousasantos91.assembleia.domain;

import br.com.dsousasantos91.assembleia.domain.enumeration.UFEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "endereco")
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(min = 8, max = 8)
    @Pattern(regexp = "(\\d{8}$)", message = "Enviar apenas números para {0}")
    private String cep;

    @NotBlank(message = "{0} é obrigatório")
    @Size(max = 100, message = "É permitido digitar um máximo de {1} para {0}")
    private String logradouro;

    @NotBlank(message = "{0} é obrigatório")
    @Size(max = 50, message = "É permitido digitar um máximo de {1} para {0}")
    private String bairro;

    @NotBlank(message = "{0} é obrigatório")
    @Size(max = 50, message = "É permitido digitar um máximo de {1} para {0}")
    private String cidade;

    @NotNull(message = "{0} é obrigatório")
    @Enumerated(EnumType.STRING)
    private UFEnum uf;

    private String complemento;

    @NotNull(message = "{0} é obrigatório")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assembleia_id")
    private Assembleia assembleia;
}

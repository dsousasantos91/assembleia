package br.com.dsousasantos91.assembleia.domain;

import br.com.dsousasantos91.assembleia.domain.enumeration.TipoAssembleiaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assembleia")
public class Assembleia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "{0} é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoAssembleiaEnum tipoAssembleia;

    @NotNull(message = "{0} é obrigatório")
    private LocalDateTime dataHoraInicioApuracao;

    @NotNull(message = "{0} é obrigatório")
    private LocalDateTime dataHoraFimApuracao;

    @Size(min = 3, max = 50, message = "É obrigatório digitar um mínimo de {1} para {0}")
    @NotBlank(message = "{0} é obrigatório")
    private String cooperativa;

    @Valid
    @NotNull(message = "{0} é obrigatório")
    @OneToOne(mappedBy = "assembleia", cascade = CascadeType.ALL)
    private Endereco local;

    @Size(min = 3, max = 50, message = "É obrigatório digitar um mínimo de {1} para {0}")
    @NotBlank(message = "{0} é obrigatório")
    private String presidente;

    @Size(min = 3, max = 50, message = "É obrigatório digitar um mínimo de {1} para {0}")
    @NotBlank(message = "{0} é obrigatório")
    private String secretario;

    @Valid
    @NotEmpty(message = "{0} é obrigatório")
    @OneToMany(mappedBy = "assembleia" ,cascade = CascadeType.ALL)
    private List<Pauta> pautas;
}

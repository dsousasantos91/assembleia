package br.com.dsousasantos91.assembleia.domain;

import br.com.dsousasantos91.assembleia.domain.enumeration.VotoEnum;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voto", uniqueConstraints = @UniqueConstraint(name = "UniqueVotoSessaoAssociado", columnNames = { "sessao_id", "associado_id" }))
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Valid
    @NotNull(message = "{0} é obrigatório")
    @ManyToOne
    @JoinColumn(name = "sessao_id")
    private Sessao sessao;

    @Valid
    @NotNull(message = "{0} é obrigatório")
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "associado_id")
    private Associado associado;

    @NotNull(message = "{0} é obrigatório")
    @Enumerated(EnumType.STRING)
    private VotoEnum voto;

    private LocalDateTime dataHoraVoto;

    @PrePersist
    public void setDataHoraVoto() {
        this.dataHoraVoto = LocalDateTime.now();
    }
}

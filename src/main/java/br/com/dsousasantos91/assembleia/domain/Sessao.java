package br.com.dsousasantos91.assembleia.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "sessao", uniqueConstraints = @UniqueConstraint(name = "UniquePauta", columnNames = { "pauta_id" }))
@NoArgsConstructor
@AllArgsConstructor
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Valid
    @NotNull(message = "{0} é obrigatório")
    @OneToOne
    @JoinColumn(name = "pauta_id")
    private Pauta pauta;

    @NotNull(message = "{0} é obrigatório")
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "{0} é obrigatório")
    private LocalDateTime dataHoraFim;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Associado> associados;

    private Boolean sessaoPrivada;

    @Valid
    @OneToMany(mappedBy = "sessao" ,cascade = CascadeType.ALL)
    private List<Voto> votos;

    @JsonIgnore
    private Boolean resultadoEnviado;

    @PrePersist
    public void setDefaults() {
        this.resultadoEnviado = Boolean.FALSE;
    }
}

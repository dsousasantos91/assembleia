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
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "sessao")
@NoArgsConstructor
@AllArgsConstructor
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Valid
    @NotNull(message = "{0} é obrigatório")
    @ManyToOne
    @JoinColumn(name = "pauta_id")
    private Pauta pauta;

    @NotNull(message = "{0} é obrigatório")
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "{0} é obrigatório")
    private LocalDateTime dataHoraFim;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Associado> associados;

    private Boolean sessaoPrivada = Boolean.FALSE;

    @JsonIgnore
    private Boolean resultadoEnviado;

    @PrePersist
    public void setResultadoEnviado() {
        this.resultadoEnviado = Boolean.FALSE;
    }
}

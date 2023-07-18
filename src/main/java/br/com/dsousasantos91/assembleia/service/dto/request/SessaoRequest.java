package br.com.dsousasantos91.assembleia.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoRequest {
    @NotNull(message = "{0} é obrigatório")
    private Long pautaId;

    @NotNull(message = "{0} é obrigatório")
    private Boolean votacaoLivre;

    private List<AssociadoRequest> associados;

    @Valid
    private TempoSessaoRequest tempoSessao = new TempoSessaoRequest();
}

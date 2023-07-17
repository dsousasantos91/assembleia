package br.com.dsousasantos91.assembleia.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoRequest {
    @NotNull(message = "{0} é obrigatório")
    private Long pautaId;

    @Valid
    private TempoSessaoRequest tempoSessao = new TempoSessaoRequest();
}

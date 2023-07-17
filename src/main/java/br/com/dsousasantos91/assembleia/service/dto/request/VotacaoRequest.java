package br.com.dsousasantos91.assembleia.service.dto.request;

import br.com.dsousasantos91.assembleia.domain.enumeration.Voto;
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
public class VotacaoRequest {
    @NotNull(message = "{0} é obrigatório")
    private Long sessaoId;

    @Valid
    @NotNull(message = "{0} é obrigatório")
    private AssociadoRequest associado;

    @NotNull(message = "{0} é obrigatório")
    private Voto voto;
}

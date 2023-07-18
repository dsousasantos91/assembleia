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
public class SessaoEmLoteRequest {
    private Long assembleiaId;
    private List<Long> idsPautas;
    private List<AssociadoRequest> associados;

    @NotNull(message = "{0} é obrigatório")
    private Boolean votacaoLivre;

    @Valid
    private TempoSessaoRequest tempoSessao = new TempoSessaoRequest();
}

package br.com.dsousasantos91.assembleia.service.dto.response;

import br.com.dsousasantos91.assembleia.domain.enumeration.Voto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContagemVotosResponse {
    private Long sessaoId;
    private PautaResponse pauta;
    private Map<Voto, Long> votos;
}

package br.com.dsousasantos91.assembleia.service.dto.response;

import br.com.dsousasantos91.assembleia.domain.enumeration.Voto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotacaoResponse {
    private Long id;
    private SessaoResponse sessao;
    private AssociadoResponse associado;
    private Voto voto;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHoraVoto;

    public String getVoto() {
        return voto.getValue();
    }
}

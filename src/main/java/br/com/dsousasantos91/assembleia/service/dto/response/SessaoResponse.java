package br.com.dsousasantos91.assembleia.service.dto.response;

import br.com.dsousasantos91.assembleia.domain.Associado;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoResponse {
    private Long id;
    private PautaResponse pauta;
    private Boolean sessaoPrivada;
    private List<Associado> associados;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHoraInicio;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHoraFim;

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio.truncatedTo(ChronoUnit.SECONDS);
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim.truncatedTo(ChronoUnit.SECONDS);
    }
}

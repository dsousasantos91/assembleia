package br.com.dsousasantos91.assembleia.service.dto.response;

import br.com.dsousasantos91.assembleia.domain.enumeration.TipoAssembleia;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssembleiaResponse {
    private Long id;
    private TipoAssembleia tipoAssembleia;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHoraInicioApuracao;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHoraFimApuracao;

    private String cooperativa;
    private EnderecoResponse local;
    private String presidente;
    private String secretario;
    private List<PautaResponse> pautas;

    public String getTipoAssembleia() {
        return tipoAssembleia.getNome();
    }
}

package br.com.dsousasantos91.assembleia.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PautaResponse {
    private Long id;
    private String titulo;
    private String descricao;

}

package br.com.dsousasantos91.assembleia.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempoSessaoRequest {
    @Min(value = 0, message = "{0} deve ser um valor positivo.")
    private int dias = 0;

    @Min(value = 0, message = "{0} deve ser um valor positivo.")
    private int horas = 0;

    @Min(value = 0, message = "{0} deve ser um valor positivo.")
    private int minutos = 1;
}

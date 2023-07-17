package br.com.dsousasantos91.assembleia.service.dto.request;

import br.com.dsousasantos91.assembleia.domain.enumeration.UF;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoRequest {
    @Size(min = 8, max = 8, message = "É obrigatório que o {0} tenha {1} digitos")
    @Pattern(regexp = "(\\d{8}$)", message = "Enviar apenas números para {0}")
    private String cep;

    @NotBlank(message = "{0} é obrigatório")
    @Size(max = 100, message = "É permitido digitar um máximo de {1} para {0}")
    private String logradouro;

    @NotBlank(message = "{0} é obrigatório")
    @Size(max = 50, message = "É permitido digitar um máximo de {1} para {0}")
    private String bairro;

    @NotBlank(message = "{0} é obrigatório")
    @Size(max = 50, message = "É permitido digitar um máximo de {1} para {0}")
    private String cidade;

    @NotNull(message = "{0} é obrigatório")
    private UF uf;

    private String complemento;
}

package br.com.dsousasantos91.assembleia.service.dto.response;

import br.com.dsousasantos91.assembleia.domain.enumeration.UF;
import br.com.dsousasantos91.assembleia.util.MaskUtil;
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
public class EnderecoResponse {
    private Long id;
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private UF uf;
    private String complemento;

    public String getCep() {
        return MaskUtil.cep(this.cep);
    }
}

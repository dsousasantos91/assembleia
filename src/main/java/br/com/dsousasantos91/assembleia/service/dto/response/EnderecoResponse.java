package br.com.dsousasantos91.assembleia.service.dto.response;

import br.com.dsousasantos91.assembleia.domain.enumeration.UF;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.swing.text.MaskFormatter;
import java.text.ParseException;

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

        try {
            MaskFormatter mf = new MaskFormatter("#####-###");
            mf.setValueContainsLiteralCharacters(false);
            this.cep = mf.valueToString(this.cep);
        } catch (ParseException e) {
            e.getCause();
        }

        return this.cep;
    }
}

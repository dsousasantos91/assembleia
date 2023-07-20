package br.com.dsousasantos91.assembleia.service.dto.response;

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
public class AssociadoResponse {
    private Long id;
    private String cpf;
    private String nome;

    public String getCpf() {
        return MaskUtil.cpf(this.cpf);
    }
}

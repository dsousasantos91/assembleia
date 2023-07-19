package br.com.dsousasantos91.assembleia.service;

import br.com.dsousasantos91.assembleia.exception.GenericBadRequestException;
import br.com.dsousasantos91.assembleia.feign.ValidadorCPFClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidarCPFService {
    private static final String FALSO = "Falso";

    private final ValidadorCPFClient validadorCPFClient;

    public void validar(String cpf) {
        log.info("Verificando se o CPF [{}] é válido", cpf);
        String responseValidador = validadorCPFClient.validarCPF(getBodyValidacaoCPF(cpf));
        if (responseValidador.contains(FALSO))
            throw new GenericBadRequestException(String.format("CPF %s.", responseValidador));
        log.info("CPF [{}] é válido", responseValidador);
    }

    private String getBodyValidacaoCPF(String cpf) {
        return "acao=validar_cpf&txt_cpf=" + cpf;
    }
}

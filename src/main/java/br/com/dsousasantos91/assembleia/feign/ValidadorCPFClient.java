package br.com.dsousasantos91.assembleia.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "validadorDeCPF", url = "${cliente.validador.url}")
public interface ValidadorCPFClient {
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String validarCPF(@RequestBody String requestBody);
}

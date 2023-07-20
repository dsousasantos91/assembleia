package br.com.dsousasantos91.assembleia.consumer;

import br.com.dsousasantos91.assembleia.service.SessaoService;
import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificarVotacaoConsumer {

    private static final String VOTACAO_QUEUE = "votacao.queue";

    private final SessaoService sessaoService;

    @RabbitListener(queues = VOTACAO_QUEUE)
    public void receber(@Payload ContagemVotosResponse contagem) {
        log.info("Enviando notificação de contagem da sessão [{}] pauta [{}].",
                contagem.getSessaoId(), contagem.getPauta().getTitulo());
        sessaoService.confirmarEnvioDeResultado(contagem);
        log.info("Notificação de contagem da sessão [{}] pauta [{}] enviada com sucesso.",
                contagem.getSessaoId(), contagem.getPauta().getTitulo());
    }
}

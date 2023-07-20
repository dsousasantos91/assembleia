package br.com.dsousasantos91.assembleia.consumer;

import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificarVotacaoDLQConsumer {

    private final RabbitTemplate rabbitTemplate;
    private static final String X_RETRY_HEADER = "x-dlq-retry";
    private static final String VOTACAO_QUEUE = "votacao.queue";
    private static final String VOTACAO_QUEUE_DLQ = "votacao.queue-dlq";

    @RabbitListener(queues = VOTACAO_QUEUE_DLQ)
    public void processar(@Payload ContagemVotosResponse contagem, @Headers Map<String, Object> headers) {
        Integer retryHeader = (Integer) headers.get(X_RETRY_HEADER);
        if (isNull(retryHeader)) retryHeader = 0;
        log.info("Reprocessando notificação de contagem sessão [{}]", contagem.getSessaoId());
        if (retryHeader < 3) {
            Map<String, Object> updateHeaders = new HashMap<>(headers);
            int tryCount = retryHeader + 1;
            updateHeaders.put(X_RETRY_HEADER, tryCount);
            this.rabbitTemplate.convertAndSend(VOTACAO_QUEUE, contagem);
            final MessagePostProcessor messagePostProcessor = message -> {
                MessageProperties messageProperties = message.getMessageProperties();
                updateHeaders.forEach(messageProperties::setHeader);
                return message;
            };
            log.info("Enviando notificação de contagem sessão [{}] para DLQ", contagem.getSessaoId());
            this.rabbitTemplate.convertAndSend(VOTACAO_QUEUE_DLQ, contagem, messagePostProcessor);
        }
    }
}

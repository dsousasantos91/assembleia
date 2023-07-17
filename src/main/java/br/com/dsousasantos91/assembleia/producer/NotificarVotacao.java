package br.com.dsousasantos91.assembleia.producer;

import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificarVotacao {

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    @Value("${votacao.rabbitmq.routingkey}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public void enviarResultadoVotacao(ContagemVotosResponse contagemVotos) {
        rabbitTemplate.convertAndSend(exchange, routingKey, contagemVotos);
    }
}

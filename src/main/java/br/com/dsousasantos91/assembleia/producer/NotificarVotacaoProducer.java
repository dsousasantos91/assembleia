package br.com.dsousasantos91.assembleia.producer;

import br.com.dsousasantos91.assembleia.service.dto.response.ContagemVotosResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificarVotacaoProducer {

    private static final String VOTACAO_EXCHANGE = "votacao.exchange";
    private static final String VOTACAO_ROUTINGKEY = "votacao.routingkey";

    private final RabbitTemplate rabbitTemplate;

    public void enviarResultadoVotacao(ContagemVotosResponse contagemVotos) {
        log.info("Notificando contagem dos votos da sessao ID [{}] pauta [{}].",
                contagemVotos.getSessaoId(), contagemVotos.getPauta().getTitulo());
        rabbitTemplate.convertAndSend(VOTACAO_EXCHANGE, VOTACAO_ROUTINGKEY, contagemVotos);
        log.info("Notificação de contagem dos votos da sessao ID [{}] pauta [{}] enviada com sucesso.",
                contagemVotos.getSessaoId(), contagemVotos.getPauta().getTitulo());
    }
}

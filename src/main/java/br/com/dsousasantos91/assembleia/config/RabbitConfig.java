package br.com.dsousasantos91.assembleia.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@EnableRabbit
@Configuration
public class RabbitConfig {

    private static final String VOTACAO_QUEUE = "votacao.queue";
    private static final String VOTACAO_QUEUE_DLQ = "votacao.queue-dlq";
    private static final String VOTACAO_EXCHANGE = "votacao.exchange";
    private static final String VOTACAO_EXCHANGE_DLX = "votacao.exchange-dlx";

    @Bean
    public Queue votacaoQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchante", VOTACAO_EXCHANGE_DLX);
        return new Queue(VOTACAO_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue votacaoQueueDLQ() {
        return new Queue(VOTACAO_QUEUE_DLQ);
    }

    @Bean
    public FanoutExchange exchange() {
        return ExchangeBuilder.fanoutExchange(VOTACAO_EXCHANGE).durable(true).build();
    }

    @Bean
    public FanoutExchange exchangeDLQ() {
        return ExchangeBuilder.fanoutExchange(VOTACAO_EXCHANGE_DLX).durable(true).build();
    }

    @Bean
    public Binding binding(@Qualifier("votacaoQueue") Queue notificationsQueue, FanoutExchange exchange) {
        return BindingBuilder.bind(notificationsQueue).to(exchange);
    }

    @Bean
    public Binding DLQbinding(@Qualifier("votacaoQueueDLQ") Queue notificationsQueue, FanoutExchange exchange) {
        return BindingBuilder.bind(notificationsQueue).to(exchange);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

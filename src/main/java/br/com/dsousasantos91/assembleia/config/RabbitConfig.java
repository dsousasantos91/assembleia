package br.com.dsousasantos91.assembleia.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitConfig {

    @Value("${votacao.rabbitmq.queue}")
    String votacaoQueue;

    @Value("${votacao.rabbitmq.routingkey}")
    private String votacaoRoutingkey;

    @Value("${spring.rabbitmq.template.exchange}")
    String exchange;

    @Bean
    public Queue declareVotacaoQueue() {
        return QueueBuilder.durable(votacaoQueue).build();
    }

    @Bean
    public DirectExchange declareExchange() {
        return ExchangeBuilder.directExchange(exchange).durable(true).build();
    }

    @Bean
    public Binding declareClientBinding(@Qualifier("declareVotacaoQueue") Queue notificationsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(notificationsQueue).to(exchange).with(votacaoRoutingkey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

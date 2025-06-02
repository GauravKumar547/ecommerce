package org.ecommerce.emailservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_EMAIL = "email-queue";
    public static final String QUEUE_EMAIL_DLQ = "email-queue-dlq";
    public static final String EXCHANGE_EMAIL = "email-exchange";
    public static final String ROUTING_KEY_EMAIL = "email.#";
    public static final String EXCHANGE_DLX = "email-dlx";

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(QUEUE_EMAIL)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", "email.dead")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_EMAIL_DLQ).build();
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(EXCHANGE_EMAIL);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(EXCHANGE_DLX);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(emailExchange())
                .with(ROUTING_KEY_EMAIL);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("email.dead");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
} 
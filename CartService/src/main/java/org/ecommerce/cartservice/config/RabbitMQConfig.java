package org.ecommerce.cartservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_CARTS = "carts-exchange";
    public static final String QUEUE_CART_UPDATED = "cart-updated-queue";
    public static final String QUEUE_CART_CLEARED = "cart-cleared-queue";
    public static final String QUEUE_CART_ITEM_ADDED = "cart-item-added-queue";
    public static final String QUEUE_CART_ITEM_REMOVED = "cart-item-removed-queue";
    public static final String ROUTING_KEY_CART_UPDATED = "cart.updated";
    public static final String ROUTING_KEY_CART_CLEARED = "cart.cleared";
    public static final String ROUTING_KEY_CART_ITEM_ADDED = "cart.item.added";
    public static final String ROUTING_KEY_CART_ITEM_REMOVED = "cart.item.removed";

    @Bean
    public TopicExchange cartExchange() {
        return new TopicExchange(EXCHANGE_CARTS);
    }

    @Bean
    public Queue cartUpdatedQueue() {
        return QueueBuilder.durable(QUEUE_CART_UPDATED)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "cart-updated-dlq")
                .build();
    }

    @Bean
    public Queue cartClearedQueue() {
        return QueueBuilder.durable(QUEUE_CART_CLEARED)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "cart-cleared-dlq")
                .build();
    }

    @Bean
    public Queue cartItemAddedQueue() {
        return QueueBuilder.durable(QUEUE_CART_ITEM_ADDED)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "cart-item-added-dlq")
                .build();
    }

    @Bean
    public Queue cartItemRemovedQueue() {
        return QueueBuilder.durable(QUEUE_CART_ITEM_REMOVED)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "cart-item-removed-dlq")
                .build();
    }

    @Bean
    public Binding cartUpdatedBinding(Queue cartUpdatedQueue, TopicExchange cartExchange) {
        return BindingBuilder
                .bind(cartUpdatedQueue)
                .to(cartExchange)
                .with(ROUTING_KEY_CART_UPDATED);
    }

    @Bean
    public Binding cartClearedBinding(Queue cartClearedQueue, TopicExchange cartExchange) {
        return BindingBuilder
                .bind(cartClearedQueue)
                .to(cartExchange)
                .with(ROUTING_KEY_CART_CLEARED);
    }

    @Bean
    public Binding cartItemAddedBinding(Queue cartItemAddedQueue, TopicExchange cartExchange) {
        return BindingBuilder
                .bind(cartItemAddedQueue)
                .to(cartExchange)
                .with(ROUTING_KEY_CART_ITEM_ADDED);
    }

    @Bean
    public Binding cartItemRemovedBinding(Queue cartItemRemovedQueue, TopicExchange cartExchange) {
        return BindingBuilder
                .bind(cartItemRemovedQueue)
                .to(cartExchange)
                .with(ROUTING_KEY_CART_ITEM_REMOVED);
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
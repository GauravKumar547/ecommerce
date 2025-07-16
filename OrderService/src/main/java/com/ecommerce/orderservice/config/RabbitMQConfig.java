package com.ecommerce.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_ORDERS = "orders-exchange";
    public static final String QUEUE_ORDER_CREATED = "order-created-queue";
    public static final String QUEUE_ORDER_UPDATED = "order-updated-queue";
    public static final String QUEUE_ORDER_CANCELLED = "order-cancelled-queue";
    public static final String QUEUE_ORDER_SHIPPED = "order-shipped-queue";
    public static final String ROUTING_KEY_ORDER_CREATED = "order.created";
    public static final String ROUTING_KEY_ORDER_UPDATED = "order.updated";
    public static final String ROUTING_KEY_ORDER_CANCELLED = "order.cancelled";
    public static final String ROUTING_KEY_ORDER_SHIPPED = "order.shipped";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(EXCHANGE_ORDERS);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_CREATED)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "order-created-dlq")
                .build();
    }

    @Bean
    public Queue orderUpdatedQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_UPDATED)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "order-updated-dlq")
                .build();
    }

    @Bean
    public Queue orderCancelledQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_CANCELLED)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "order-cancelled-dlq")
                .build();
    }

    @Bean
    public Queue orderShippedQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_SHIPPED)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "order-shipped-dlq")
                .build();
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder
                .bind(orderCreatedQueue)
                .to(orderExchange)
                .with(ROUTING_KEY_ORDER_CREATED);
    }

    @Bean
    public Binding orderUpdatedBinding(Queue orderUpdatedQueue, TopicExchange orderExchange) {
        return BindingBuilder
                .bind(orderUpdatedQueue)
                .to(orderExchange)
                .with(ROUTING_KEY_ORDER_UPDATED);
    }

    @Bean
    public Binding orderCancelledBinding(Queue orderCancelledQueue, TopicExchange orderExchange) {
        return BindingBuilder
                .bind(orderCancelledQueue)
                .to(orderExchange)
                .with(ROUTING_KEY_ORDER_CANCELLED);
    }

    @Bean
    public Binding orderShippedBinding(Queue orderShippedQueue, TopicExchange orderExchange) {
        return BindingBuilder
                .bind(orderShippedQueue)
                .to(orderExchange)
                .with(ROUTING_KEY_ORDER_SHIPPED);
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
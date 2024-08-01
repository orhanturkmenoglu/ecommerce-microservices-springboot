package com.example.inventory_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbit.mq.routing.key}")
    private String routingKey;

    @Value("${rabbit.mq.exchange.name}")
    private String exchange;

    @Value("${rabbit.mq.queue.name}")
    private String queue;

    @Value("${rabbit.mq.queue.create.product}")
    private String queueCreateProduct;

    @Value("${rabbit.mq.routing.create.product}")
    private String routingKeyCreateProduct;



    @Bean
    public Queue queue() {
        return new Queue(queue);
    }

    @Bean
    public Queue queueCreateProduct() {
        return new Queue(queueCreateProduct);
    }


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }


    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(routingKey);
    }

    @Bean
    public Binding bindingCreateProduct() {
        return BindingBuilder
                .bind(queueCreateProduct())
                .to(exchange())
                .with(routingKeyCreateProduct);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }



}
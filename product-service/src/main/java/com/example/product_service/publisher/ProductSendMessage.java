package com.example.product_service.publisher;

import com.example.product_service.model.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSendMessage {

    private final RabbitTemplate rabbitTemplate;


    @Value("${rabbit.mq.routing.create.product}")
    private String routingKeyCreateProduct;

    @Value("${rabbit.mq.exchange.name}")
    private String exchange;

    public void sendMessage(Inventory inventory) {
        log.info(String.format("SEND MESSAGE -> : %s", inventory));
        rabbitTemplate.convertAndSend(exchange, routingKeyCreateProduct, inventory);

    }
}

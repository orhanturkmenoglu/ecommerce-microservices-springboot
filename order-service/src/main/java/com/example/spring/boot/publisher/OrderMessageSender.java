package com.example.spring.boot.publisher;

import com.example.spring.boot.model.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderMessageSender {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbit.mq.routing.key}")
    private String routingKey;

    @Value("${rabbit.mq.exchange.name}")
    private String exchange;

    public void sendInventoryUpdateMessage(Inventory inventory) {
        log.info(String.format("SEND MESSAGE -> : %s", inventory));
        rabbitTemplate.convertAndSend(exchange, routingKey, inventory);
    }
}

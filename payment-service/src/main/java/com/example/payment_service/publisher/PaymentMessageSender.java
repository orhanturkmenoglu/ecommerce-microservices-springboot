package com.example.payment_service.publisher;


import com.example.payment_service.model.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMessageSender {

    private final KafkaTemplate<String,Inventory> kafkaTemplate;

    public void sendInventoryUpdateMessage(Inventory inventory) {
        log.info(String.format("SEND MESSAGE -> : %s", inventory));
        kafkaTemplate.send("inventory", inventory);
    }

}

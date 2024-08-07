package com.example.payment_service.model;

import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.enums.PaymentType;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "payments")
public class Payment implements Serializable {

    @Id
    private String id;

    private String orderId;

    private Double amount;

    private PaymentStatus paymentStatus = PaymentStatus.PENDING; // ödeme durumu default değer.


    private PaymentType paymentType;  // ödeme tipi


    @PostConstruct
    private void init() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}

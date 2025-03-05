package com.example.payment_service.model;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    private String id;
    private String invoiceNumber;
    private String customerId;
    private String orderId;
    private String productId;
    private String customerName;

    private LocalDate invoiceDate;
    private Double totalAmount;

    @Lob  // PDF içeriklerini büyük veri tipiyle kaydediyoruz
    private byte[] pdfContent;  // PDF içeriği
}

package com.example.payment_service.external;

import com.example.payment_service.model.Invoice;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "INVOICE-SERVICE")
public interface InvoiceClientService {

    @GetMapping("/api/v1/invoices/create-invoice")
    Invoice createInvoice(@RequestParam Map<String, Object> data);

}

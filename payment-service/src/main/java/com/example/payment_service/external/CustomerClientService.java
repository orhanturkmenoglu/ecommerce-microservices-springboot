package com.example.payment_service.external;

import com.example.payment_service.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClientService {

    @GetMapping("api/v1/customers/{customerId}")
    Customer getCustomerById(@PathVariable("customerId") String customerId);

}

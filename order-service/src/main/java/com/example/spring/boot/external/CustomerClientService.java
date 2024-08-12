package com.example.spring.boot.external;

import com.example.spring.boot.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClientService {

    @GetMapping("api/v1/customers/{customerId}")
    Customer getCustomerById(@PathVariable("customerId") String customerId);
}

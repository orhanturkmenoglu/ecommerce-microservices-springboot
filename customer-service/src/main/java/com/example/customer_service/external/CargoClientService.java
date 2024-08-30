package com.example.customer_service.external;

import com.example.customer_service.model.Cargo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("CARGO-SERVICE")
public interface CargoClientService {

    @GetMapping("api/v1/cargo/trackingNumber/{trackingNumber}")
    Cargo getCargoByTrackingNumber(@PathVariable("trackingNumber") String trackingNumber);
}

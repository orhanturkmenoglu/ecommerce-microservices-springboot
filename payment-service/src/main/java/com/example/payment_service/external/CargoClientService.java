package com.example.payment_service.external;

import com.example.payment_service.model.Cargo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient("CARGO-SERVICE")
public interface CargoClientService {

    @PutMapping("api/v1/cargo")
    Cargo updateCargo(Cargo cargo);
}

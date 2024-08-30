package com.example.spring.boot.external;

import com.example.spring.boot.dto.cargoDto.CargoRequestDto;
import com.example.spring.boot.dto.cargoDto.CargoResponseDto;
import com.example.spring.boot.dto.cargoDto.CargoUpdateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient("CARGO-SERVICE")
public interface CargoClientService {

    @PostMapping("api/v1/cargo")
    CargoResponseDto createCargo(CargoRequestDto cargoRequestDto);

    @PutMapping("api/v1/cargo")
    CargoResponseDto updateCargo(CargoUpdateRequestDto cargoUpdateRequestDto);
}

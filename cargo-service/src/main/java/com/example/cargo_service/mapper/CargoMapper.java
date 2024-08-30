package com.example.cargo_service.mapper;

import com.example.cargo_service.dto.CargoRequestDto;
import com.example.cargo_service.dto.CargoResponseDto;
import com.example.cargo_service.model.Cargo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CargoMapper {
    public Cargo mapToCargo(CargoRequestDto cargoRequestDto) {
        return Cargo.builder()
                .orderId(cargoRequestDto.getOrderId())
                .customerId(cargoRequestDto.getCustomerId())
                .status(cargoRequestDto.getStatus())
                .lastUpdated(LocalDateTime.now())
                .build();
    }


    public CargoResponseDto mapToCargoResponseDto(Cargo cargo) {
        return CargoResponseDto.builder()
                .id(cargo.getId())
                .orderId(cargo.getOrderId())
                .customerId(cargo.getCustomerId())
                .trackingNumber(cargo.getTrackingNumber())
                .status(cargo.getStatus())
                .lastUpdated(cargo.getLastUpdated())
                .build();
    }

    public List<CargoResponseDto> mapToCargoResponseDtoList(List<Cargo> cargoList) {
        return cargoList.stream()
                .map(this::mapToCargoResponseDto)
                .toList();
    }
}

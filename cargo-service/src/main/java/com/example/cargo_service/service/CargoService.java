package com.example.cargo_service.service;

import com.example.cargo_service.dto.CargoRequestDto;
import com.example.cargo_service.dto.CargoResponseDto;
import com.example.cargo_service.dto.CargoUpdateRequestDto;
import com.example.cargo_service.enums.CargoStatus;
import com.example.cargo_service.exception.CargoNotFoundException;
import com.example.cargo_service.external.CustomerClientService;
import com.example.cargo_service.mapper.CargoMapper;
import com.example.cargo_service.model.Cargo;
import com.example.cargo_service.model.Customer;
import com.example.cargo_service.repository.CargoRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CargoService {

    private final CargoRepository cargoRepository;

    private final CargoMapper cargoMapper;

    private final CustomerClientService customerClientService;

    @Transactional
    @CircuitBreaker(name = "cargoServiceBreaker", fallbackMethod = "cargoServiceFallback")
    @Retry(name = "cargoServiceBreaker", fallbackMethod = "cargoServiceFallback")
    @RateLimiter(name = "createCargoLimiter", fallbackMethod = "cargoServiceFallback")
    public CargoResponseDto createCargo(CargoRequestDto cargoRequestDto) {
        log.info("CargoService::createCargo started");

        // müşteri bilgilerini doğrula veya işle
        Customer customer = customerClientService.getCustomerById(cargoRequestDto.getCustomerId());
        if (customer ==null) {
            throw new IllegalArgumentException("Customer not found");
        }

        Cargo cargo = cargoMapper.mapToCargo(cargoRequestDto);
        cargo.setStatus(CargoStatus.PENDING);
        cargo.setTrackingNumber(UUID.randomUUID().toString());
        log.info("CargoService::createCargo cargo : {}", cargo);


        Cargo savedCargo = cargoRepository.save(cargo);
        log.info("CargoService::createCargo savedCargo : {}", savedCargo);

        log.info("CargoService::createCargo finished");
        return cargoMapper.mapToCargoResponseDto(savedCargo);
    }

    // read

    @Cacheable(value = "cargo",key = "'all'")
    public List<CargoResponseDto> getCargoList() {
        log.info("CargoService::getCargoList started");

        List<Cargo> cargoList = cargoRepository.findAll();
        log.info("CargoService::getCargoList cargoList : {}", cargoList);

        log.info("CargoService::getCargoList finished");
        return cargoMapper.mapToCargoResponseDtoList(cargoList);
    }


    // get cargo by id
    @Cacheable(value = "cargo",key = "#cargoId")
    public CargoResponseDto getCargoById(String cargoId) {
        log.info("CargoService::getCargoById started");

        Cargo cargo = getCargo(cargoId);
        log.info("CargoService::getCargoById cargo : {}", cargo);

        log.info("CargoService::getCargoById finished");
        return cargoMapper.mapToCargoResponseDto(cargo);
    }

    @Cacheable(value = "cargo",key = "#orderId")
    public CargoResponseDto getCargoByOrderId(String orderId) {
        log.info("CargoService::getCargoByOrderId started");

        Cargo cargo = cargoRepository.findByOrderId(orderId).orElseThrow(
                () -> new NullPointerException("Cargo order id not found : {}" + orderId));
        log.info("CargoService::getCargoByOrderId cargo : {}", cargo);

        log.info("CargoService::getCargoByOrderId finished");
        return cargoMapper.mapToCargoResponseDto(cargo);
    }

    @Cacheable(value = "cargo",key = "#trackingNumber")
    public CargoResponseDto getCargoByTrackingNumber(String trackingNumber){
        log.info("CargoService::getCargoByTrackingNumber started");

        Cargo cargo = cargoRepository.findCargoByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new NullPointerException("Cargo tracking number not found : " + trackingNumber));

        log.info("CargoService::getCargoByTrackingNumber cargo : {}", cargo);

        log.info("CargoService::getCargoByTrackingNumber finished");
        return cargoMapper.mapToCargoResponseDto(cargo);
    }

    @Transactional
    @CircuitBreaker(name = "cargoServiceBreaker", fallbackMethod = "cargoServiceFallback")
    @Retry(name = "cargoServiceBreaker", fallbackMethod = "cargoServiceFallback")
    @RateLimiter(name = "createCargoLimiter", fallbackMethod = "cargoServiceFallback")
    // update
    @CacheEvict(value = "carg",key = "#cargoUpdateRequestDto.id",allEntries = true)
    public CargoResponseDto updateCargo(CargoUpdateRequestDto cargoUpdateRequestDto) {
        log.info("CargoService::updateCargo started");

        Cargo cargo = getCargo(cargoUpdateRequestDto.getId());
        cargo.setCustomerId(cargoUpdateRequestDto.getCustomerId());
        cargo.setOrderId(cargoUpdateRequestDto.getOrderId());
        cargo.setStatus(cargoUpdateRequestDto.getStatus());
        log.info("CargoService::updateCargo cargo : {}", cargo);

        Cargo updatedCargo = cargoRepository.save(cargo);
        log.info("CargoService::updateCargo updatedCargo : {}", updatedCargo);


        log.info("CargoService::updateCargo finished");
        return cargoMapper.mapToCargoResponseDto(updatedCargo);
    }

    // delete
    @CacheEvict(value = "cargo", key = "#cargoId",allEntries = true)
    public void deleteCargoById(String cargoId) {
        log.info("CargoService::deleteCargoById started");

        Cargo cargo = getCargo(cargoId);
        log.info("CargoService::deleteCargoById cargo : {}", cargo);

        cargoRepository.deleteById(cargo.getId());
        log.info("CargoService::deleteCargoById finished");
    }


    private Cargo getCargo(String cargoId) {
        return cargoRepository.findById(cargoId)
                .orElseThrow(() -> new CargoNotFoundException("Cargo not found id : " + cargoId));
    }


    private CargoResponseDto cargoServiceFallback(Exception exception) {
        log.info("fallback is executed because servise is down :{}", exception.getMessage());
        return CargoResponseDto.builder().build();
    }

}

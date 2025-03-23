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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CargoServiceTest {

    @Mock
    private CargoRepository cargoRepository;

    @InjectMocks
    private CargoService cargoService;

    @Mock
    private CustomerClientService customerClientService;

    @Mock
    private CargoMapper cargoMapper;

    private Cargo cargo;
    private CargoRequestDto cargoRequestDto;
    private CargoUpdateRequestDto cargoUpdateRequestDto;
    private CargoResponseDto cargoResponseDto;
    private Customer customer;

    @BeforeEach
    public void setUp() {

        cargo = new Cargo();
        cargo.setId("CARGO123");
        cargo.setOrderId("ORDER123");
        cargo.setCustomerId("CUSTOMER123");
        cargo.setStatus(CargoStatus.PENDING);
        cargo.setTrackingNumber("TRACKING123");
        cargo.setLastUpdated(LocalDateTime.now());

        cargoRequestDto = new CargoRequestDto();
        cargoRequestDto.setCustomerId(cargo.getCustomerId());
        cargoRequestDto.setOrderId(cargo.getOrderId());
        cargoRequestDto.setStatus(cargo.getStatus());
        cargoRequestDto.setTrackingNumber(cargo.getTrackingNumber());
        cargoRequestDto.setLastUpdated(cargo.getLastUpdated());

        cargoUpdateRequestDto = new CargoUpdateRequestDto();
        cargoUpdateRequestDto.setId(cargo.getId());
        cargoUpdateRequestDto.setCustomerId(cargo.getCustomerId());
        cargoUpdateRequestDto.setOrderId(cargo.getOrderId());
        cargoUpdateRequestDto.setStatus(cargo.getStatus());
        cargoUpdateRequestDto.setTrackingNumber(cargo.getTrackingNumber());
        cargoUpdateRequestDto.setLastUpdated(cargo.getLastUpdated());

        cargoResponseDto = new CargoResponseDto();
        cargoResponseDto.setId(cargo.getId());
        cargoResponseDto.setCustomerId(cargo.getCustomerId());
        cargoResponseDto.setOrderId(cargo.getOrderId());
        cargoResponseDto.setStatus(cargo.getStatus());
        cargoResponseDto.setTrackingNumber(cargo.getTrackingNumber());
        cargoResponseDto.setLastUpdated(cargo.getLastUpdated());

        customer = new Customer();
        customer.setId("CUSTOMER123");
        customer.setFirstName("Orhan");
        customer.setLastName("TÜRKMENOĞLU");
        customer.setPhoneNumber("0555 555 55 55");
        customer.setEmail("orhan.turkmengul@gmail.com");
        customer.setCreatedDate(LocalDateTime.now());
    }

    @Test
    public void createCargo_ShouldReturnCargoResponseDto_WhenCargoRequestDtoIsValid() {

        // Arrange
        when(cargoMapper.mapToCargo(cargoRequestDto)).thenReturn(cargo);
        when(cargoRepository.save(cargo)).thenReturn(cargo);
        when(cargoMapper.mapToCargoResponseDto(cargo)).thenReturn(cargoResponseDto);
        when(customerClientService.getCustomerById(cargoRequestDto.getCustomerId())).thenReturn(customer);

        // Actual
        CargoResponseDto result = cargoService.createCargo(cargoRequestDto);

        // Assert
        assert result != null;
        assert result.equals(cargoResponseDto);
        assert result.getCustomerId().equals(cargoRequestDto.getCustomerId());
        assert result.getOrderId().equals(cargoRequestDto.getOrderId());
        assert result.getStatus().equals(cargoRequestDto.getStatus());
        assert result.getTrackingNumber().equals(cargoRequestDto.getTrackingNumber());
        assert result.getLastUpdated().equals(cargoRequestDto.getLastUpdated());
    }

    @Test
    public void createCargo_ShouldThrowException_WhenCargoRequestDtoIsNotValid() {

        // Arrange
        customer = null;
        when(customerClientService.getCustomerById(cargoRequestDto.getCustomerId())).thenReturn(customer);


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cargoService.createCargo(cargoRequestDto);
        });

        String expectedMessage = "Customer not found";

        // Assert
        assert exception != null;
        assert exception.getMessage().equals(expectedMessage);
    }

    @Test
    void getCargoList_ShouldReturnCargoList() {
        // Arrange
        when(cargoRepository.findAll()).thenReturn(java.util.List.of(cargo));
        when(cargoMapper.mapToCargoResponseDtoList(any())).thenReturn(java.util.List.of(cargoResponseDto));

        // Act
        List<CargoResponseDto> result = cargoService.getCargoList();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    void getCargoById_ShouldReturnCargoResponseDto_WhenCargoFound() {
        // Arrange
        when(cargoRepository.findById(any())).thenReturn(java.util.Optional.of(cargo));
        when(cargoMapper.mapToCargoResponseDto(any(Cargo.class))).thenReturn(cargoResponseDto);

        // Act
        CargoResponseDto result = cargoService.getCargoById(cargo.getId());

        // Assert
        assertNotNull(result);
        assertEquals(cargo.getId(), result.getId());
    }

    @Test
    void getCargoById_ShouldThrowCargoNotFoundException_WhenCargoNotFound() {
        // Arrange
        when(cargoRepository.findById(any())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(CargoNotFoundException.class, () -> cargoService.getCargoById(cargo.getId()));
    }

    @Test
    void updateCargo_ShouldReturnUpdatedCargoResponseDto_WhenCargoUpdated() {
        // Arrange
        CargoUpdateRequestDto cargoUpdateRequestDto = new CargoUpdateRequestDto();
        cargoUpdateRequestDto.setId(cargo.getId());
        cargoUpdateRequestDto.setStatus(CargoStatus.PENDING);

        when(cargoRepository.findById(any())).thenReturn(java.util.Optional.of(cargo));
        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargo);
        when(cargoMapper.mapToCargoResponseDto(any(Cargo.class))).thenReturn(cargoResponseDto);

        // Act
        CargoResponseDto result = cargoService.updateCargo(cargoUpdateRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(cargo.getId(), result.getId());
        assertEquals(cargo.getStatus(), result.getStatus());
    }

    @Test
    void deleteCargoById_ShouldDeleteCargo_WhenCargoExists() {
        // Arrange
        when(cargoRepository.findById(any())).thenReturn(java.util.Optional.of(cargo));

        // Act
        cargoService.deleteCargoById(cargo.getId());

        // Assert
        verify(cargoRepository, times(1)).deleteById(any());
    }

    @Test
    void deleteCargoById_ShouldThrowCargoNotFoundException_WhenCargoNotFound() {
        // Arrange
        when(cargoRepository.findById(any())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(CargoNotFoundException.class, () -> cargoService.deleteCargoById(cargo.getId()));
        verify(cargoRepository, never()).deleteById(any());
    }

    @Test
    void cargoServiceFallback_ShouldReturnEmptyCargoResponseDto_WhenServiceFails() throws Exception {
        // Arrange
        Exception exception = new Exception("Service failure");

        // Using reflection to access the private method
        Method method = CargoService.class.getDeclaredMethod("cargoServiceFallback", Exception.class);
        method.setAccessible(true);

        // Act
        CargoResponseDto result = (CargoResponseDto) method.invoke(cargoService, exception);

        // Assert
        assertNotNull(result);
    }
}

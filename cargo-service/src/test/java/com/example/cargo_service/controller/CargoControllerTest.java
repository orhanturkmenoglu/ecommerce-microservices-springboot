package com.example.cargo_service.controller;

import com.example.cargo_service.dto.CargoRequestDto;
import com.example.cargo_service.dto.CargoResponseDto;
import com.example.cargo_service.dto.CargoUpdateRequestDto;
import com.example.cargo_service.enums.CargoStatus;
import com.example.cargo_service.model.Cargo;
import com.example.cargo_service.model.Customer;
import com.example.cargo_service.service.CargoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CargoController.class)
public class CargoControllerTest {

    private final Logger log = LoggerFactory.getLogger(CargoControllerTest.class);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CargoService cargoService;

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
    public void createCargo_ShouldReturnCargoResponseDto_WhenCargoExists() throws Exception {

        when(cargoService.createCargo(cargoRequestDto)).thenReturn(cargoResponseDto);

        mockMvc.perform(post("/api/v1/cargo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cargoRequestDto)))
                .andDo(result -> log.info(result.getResponse().getContentAsString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(cargoResponseDto.getId()));
    }

    @Test
    public void getCargoList_ShouldReturnCargoList_WhenCargoExist() throws Exception {

        when(cargoService.getCargoList()).thenReturn(List.of(cargoResponseDto));

        mockMvc.perform(get("/api/v1/cargo/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> log.info(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(cargoResponseDto.getId()))
                .andExpect(jsonPath("$[0].orderId").value(cargoResponseDto.getOrderId()))
                .andExpect(jsonPath("$[0].customerId").value(cargoResponseDto.getCustomerId()))
                .andExpect(jsonPath("$[0].trackingNumber").value(cargoResponseDto.getTrackingNumber()))
                .andExpect(jsonPath("$[0].status").value(cargoResponseDto.getStatus().name()));
    }

    @Test
    public void getCargoList_ShouldReturnNoContent_WhenNoCargosExist() throws Exception {

        when(cargoService.getCargoList()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/cargo/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> log.info(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$").isEmpty());  // Boş liste döndürülür, status 200
    }

    @Test
    public void getCargoById_ShouldReturnCargoResponseDto_WhenCargoExists() throws Exception {
        when(cargoService.getCargoById(cargoResponseDto.getId())).thenReturn(cargoResponseDto);

        mockMvc.perform(get("/api/v1/cargo/{id}", cargoResponseDto.getId()))
                .andDo(result -> log.info(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cargoResponseDto.getId()));
    }

    @Test
    public void getCargoById_ShouldReturnNotFound_WhenCargoDoesNotExist() throws Exception {
        // Geçersiz bir id ile kargo aranıyor
        String invalidId = "999";
        // CargoService'in getCargoById metodu, geçerli bir kargo olmadığı için null döndürecek.
        when(cargoService.getCargoById(invalidId)).thenReturn(null);

        // İlgili endpoint'e istek gönderiliyor
        mockMvc.perform(get("/api/v1/cargo/{id}", cargoResponseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> log.info(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound());  // 404 dönecek çünkü kargo bulunamıyor
    }

    @Test
    public void getCargoOrderByID_ShouldReturnCargoResponseDto_WhenCargoByOrderIdExists() throws Exception {

        when(cargoService.getCargoByOrderId(cargoResponseDto.getOrderId())).thenReturn(cargoResponseDto);

        mockMvc.perform(get("/api/v1/cargo/order/{orderId}", cargoResponseDto.getOrderId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> log.info(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(cargoResponseDto.getOrderId()));

    }

    @Test
    public void getCargoByTrackingNumber_ShouldReturnCargoResponseDto_WhenCargoByTrackingNumberExists() throws Exception {
        when(cargoService.getCargoByTrackingNumber(cargoResponseDto.getTrackingNumber())).thenReturn(cargoResponseDto);

        mockMvc.perform(get("/api/v1/cargo/trackingNumber/{trackingNumber}", cargoResponseDto.getTrackingNumber())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> log.info(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value(cargoResponseDto.getTrackingNumber()));

    }

    @Test
    public void updateCargo_ShouldReturnCargoResponseDto_WhenCargoUpdatedSuccessfully() throws Exception {
        when(cargoService.updateCargo(cargoUpdateRequestDto)).thenReturn(cargoResponseDto);

        mockMvc.perform(put("/api/v1/cargo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoUpdateRequestDto)))
                .andDo(result -> log.info(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cargoResponseDto.getId()));

    }

    @Test
    public void deleteCargoById_ShouldReturnNoContent_WhenCargoDeletedSuccessfully() throws Exception {

        assert cargoResponseDto.getId() !=null;

        mockMvc.perform(delete("/api/v1/cargo/{cargoId}", cargoResponseDto.getId()))
                .andDo(result -> log.info(result.getResponse().getContentAsString()))
                .andExpect(status().isNoContent());

    }
}

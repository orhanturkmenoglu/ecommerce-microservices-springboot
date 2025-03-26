package com.example.customer_service.controller;

import com.example.customer_service.dto.addressDto.AddressRequestDto;
import com.example.customer_service.dto.addressDto.AddressResponseDto;
import com.example.customer_service.enums.Country;
import com.example.customer_service.model.Address;
import com.example.customer_service.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AddressController.class)
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    private Address address;
    private AddressResponseDto addressResponseDto;
    private AddressRequestDto addressRequestDto;

    @BeforeEach
    public void setUp() {

        address = new Address();
        address.setId("ADDR1234");
        address.setCity("Hatay");
        address.setCountry(Country.TURKEY);
        address.setDistrict("Antakya");
        address.setStreet("Gazi Mahallesi");
        address.setZipCode("31440");
        address.setDescription("Ev Adresi");
        address.setCreatedDate(LocalDateTime.now());


        addressRequestDto = new AddressRequestDto();
        addressRequestDto.setCity(address.getCity());
        addressRequestDto.setCountry(address.getCountry());
        addressRequestDto.setDistrict(address.getDistrict());
        addressRequestDto.setStreet(address.getStreet());
        addressRequestDto.setZipCode(address.getZipCode());
        addressRequestDto.setDescription(address.getDescription());
        addressRequestDto.setCreatedDate(address.getCreatedDate());


        addressResponseDto = new AddressResponseDto();
        addressResponseDto.setId(address.getId());
        addressResponseDto.setCity(address.getCity());
        addressResponseDto.setCountry(address.getCountry());
        addressResponseDto.setDistrict(address.getDistrict());
        addressResponseDto.setStreet(address.getStreet());
        addressResponseDto.setZipCode(address.getZipCode());
        addressResponseDto.setDescription(address.getDescription());
        addressResponseDto.setCreatedDate(address.getCreatedDate());
    }


    @Test
    public void getAddressAll_ShouldReturnAddressList_WhenAddressExists() throws Exception {
        when(addressService.getAddressAll()).thenReturn(List.of(addressResponseDto));

        mockMvc.perform(get("/api/v1/address/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(addressResponseDto.getId()));
    }

    @Test
    public void getAddressById_ShouldReturnAddressResponseDto_WhenAddressByIdExists() throws Exception {
        when(addressService.getAddressById(address.getId())).thenReturn(addressResponseDto);

        mockMvc.perform(get("/api/v1/address/{addressId}", address.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressResponseDto.getId()))
                .andExpect(jsonPath("$.city").value(addressResponseDto.getCity()));
    }

    @Test
    public void updateAddress_ShouldReturnAddressResponseDto_WhenUpdatedAddressSuccessfully() throws Exception {

        when(addressService.updateAddress(any(String.class), any(AddressRequestDto.class)))
                .thenReturn(addressResponseDto);

        mockMvc.perform(put("/api/v1/address/{addressId}", address.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.city").value(addressResponseDto.getCity()))
                .andExpect(jsonPath("$.district").value(addressResponseDto.getDistrict()));
    }

    @Test
    public void deleteAddressById_ShouldReturnNoContent_WhenAddressDeletedSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/address/{addressId}", address.getId()))
                .andExpect(status().isNoContent());
    }

}

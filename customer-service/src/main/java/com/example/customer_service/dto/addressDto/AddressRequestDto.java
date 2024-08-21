package com.example.customer_service.dto.addressDto;

import com.example.customer_service.enums.Country;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDto implements Serializable {


    private Country country = Country.TURKEY;

    @NotEmpty(message = "Address city cannot be empty")
    private String city;

    @NotEmpty(message = "Address district cannot be empty")
    private String district;

    @NotEmpty(message = "Address street cannot be empty")
    private String street;

    @NotEmpty(message = "Address zipCode cannot be empty")
    private String zipCode;

    private String description;

    private LocalDateTime createdDate;
}

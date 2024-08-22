package com.example.customer_service.model;

import com.example.customer_service.enums.Country;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator
    private String id;

    @Enumerated(EnumType.STRING)
    private Country country = Country.TURKEY;
    private String city;
    private String district;
    private String street;
    private String zipCode;
    private String description;
    private LocalDateTime createdDate;
}

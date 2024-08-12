package com.example.payment_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}

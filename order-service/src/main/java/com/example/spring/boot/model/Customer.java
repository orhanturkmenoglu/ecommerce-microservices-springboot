package com.example.spring.boot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


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

    private List<Address> addressList;

}

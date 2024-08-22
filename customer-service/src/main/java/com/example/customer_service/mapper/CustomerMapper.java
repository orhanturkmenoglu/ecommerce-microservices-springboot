package com.example.customer_service.mapper;

import com.example.customer_service.dto.customerDto.CustomerRequestDto;
import com.example.customer_service.dto.customerDto.CustomerResponseDto;
import com.example.customer_service.model.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {

    public Customer mapToCustomer(CustomerRequestDto customerRequestDto){
        return Customer.builder()
                .firstName(customerRequestDto.getFirstName())
                .lastName(customerRequestDto.getLastName())
                .email(customerRequestDto.getEmail())
                .phoneNumber(customerRequestDto.getPhoneNumber())
                .createdDate(LocalDateTime.now())
                .addressList(customerRequestDto.getAddressList())
                .build();
    }

    public CustomerResponseDto mapToCustomerResponseDto(Customer customer){
        return CustomerResponseDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .createdDate(customer.getCreatedDate())
                .addressList(customer.getAddressList())
                .build();
    }

    public List<CustomerResponseDto> mapToCustomerResponseDtoList(List<Customer> customerList){
        return customerList.stream()
                .map(this::mapToCustomerResponseDto)
                .collect(Collectors.toList());
    }
}

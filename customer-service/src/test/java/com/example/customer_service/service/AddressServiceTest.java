package com.example.customer_service.service;

import com.example.customer_service.mapper.AddressMapper;
import com.example.customer_service.repository.AddressRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private  AddressService addressService;

    @Mock
    private AddressMapper addressMapper
}

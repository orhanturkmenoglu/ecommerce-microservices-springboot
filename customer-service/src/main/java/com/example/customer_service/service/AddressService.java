package com.example.customer_service.service;

import com.example.customer_service.dto.addressDto.AddressRequestDto;
import com.example.customer_service.dto.addressDto.AddressResponseDto;
import com.example.customer_service.mapper.AddressMapper;
import com.example.customer_service.model.Address;
import com.example.customer_service.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;


    public List<AddressResponseDto> getAddressAll() {
        log.info("AddressService::getAddressList started");

        List<Address> addressList = addressRepository.findAll();
        log.info("AddressService::getAddressList addressList : {}", addressList);

        log.info("AddressService::getAddressList finished");
        return addressMapper.mapToAddressResponseDtoList(addressList);
    }

    public AddressResponseDto getAddressById(String addressId) {
        log.info("AddressService::getAddressById started");

        Address address = getAddress(addressId);
        log.info("AddressService::getAddressById address : {}", address);


        log.info("AddressService::getAddressById finished");
        return addressMapper.mapToAddressResponseDto(address);
    }


    public void deleteAddressById(String addressId) {
        log.info("AddressService::deleteAddressById started");

        AddressResponseDto address = getAddressById(addressId);
        log.info("AddressService::deleteAddressById address : {}", address);

        addressRepository.deleteById(addressId);
        log.info("AddressService::deleteAddressById finished");
    }

    @Transactional
    public AddressResponseDto updateAddress(String addressId, AddressRequestDto addressRequestDto) {
        log.info("AddressService::updateAddress started");

        Address address = getAddress(addressId);

        address.setCountry(addressRequestDto.getCountry());
        address.setCity(addressRequestDto.getCity());
        address.setDistrict(addressRequestDto.getDistrict());
        address.setStreet(addressRequestDto.getStreet());
        address.setZipCode(addressRequestDto.getZipCode());
        address.setDescription(addressRequestDto.getDescription());
        address.setCreatedDate(LocalDateTime.now());
        log.info("AddressService::updateAddress address : {}", address);

        Address updatedAddress = addressRepository.save(address);
        log.info("AddressService::updateAddress address : {}", address);
        log.info("AddressService::updateAddress updatedAddress : {}", updatedAddress);


        log.info("AddressService::updateAddress finished");
        return addressMapper.mapToAddressResponseDto(updatedAddress);
    }

    private Address getAddress(String addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new NullPointerException("Address not found with id : " + addressId));
    }

}

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

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;


    @Transactional
    public AddressResponseDto createAddress(AddressRequestDto addressRequestDto) {
        log.info("AddressService::createAddress started");

        if (Objects.isNull(addressRequestDto)) {
            throw new NullPointerException("Address cannot be null or empty");
        }

        Address address = addressMapper.mapToAddress(addressRequestDto);
        log.info("AddressService::createAddress address : {}", address);

        Address savedAddress = addressRepository.save(address);
        log.info("AddressService::createAddress savedAddress : {}", savedAddress);

        log.info("AddressService::createAddress finished");
        return addressMapper.mapToAddressResponseDto(savedAddress);
    }


    public List<AddressResponseDto> getAddressAll() {
        log.info("AddressService::getAddressList started");

        List<Address> addressList = addressRepository.findAll();
        log.info("AddressService::getAddressList addressList : {}", addressList);

        log.info("AddressService::getAddressList finished");
        return addressMapper.mapToAddressResponseDtoList(addressList);
    }

    public AddressResponseDto getAddressById(String addressId) {
        log.info("AddressService::getAddressById started");

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new NullPointerException("Address not found with id : " + addressId));
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

    public AddressResponseDto updateAddress(String addressId, AddressRequestDto addressRequestDto) {
        log.info("AddressService::updateAddress started");

        AddressResponseDto addressResponseDto = getAddressById(addressId);

        addressResponseDto.setCountry(addressRequestDto.getCountry());
        addressResponseDto.setCity(addressRequestDto.getCity());
        addressResponseDto.setDistrict(addressRequestDto.getDistrict());
        addressResponseDto.setStreet(addressRequestDto.getStreet());
        addressResponseDto.setZipCode(addressRequestDto.getZipCode());
        addressResponseDto.setDescription(addressRequestDto.getDescription());

        log.info("AddressService::updateAddress addressResponseDto : {}", addressResponseDto);

        Address address = addressMapper.mapToAddress(addressResponseDto);
        Address updatedAddress = addressRepository.save(address);
        log.info("AddressService::updateAddress address : {}", address);
        log.info("AddressService::updateAddress updatedAddress : {}", updatedAddress);


        log.info("AddressService::updateAddress finished");
        return addressMapper.mapToAddressResponseDto(updatedAddress);
    }
}

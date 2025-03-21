package com.example.customer_service.service;

import com.example.customer_service.dto.addressDto.AddressRequestDto;
import com.example.customer_service.dto.addressDto.AddressResponseDto;
import com.example.customer_service.enums.Country;
import com.example.customer_service.mapper.AddressMapper;
import com.example.customer_service.model.Address;
import com.example.customer_service.repository.AddressRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.Simple.class) // Metodların adlarını belirliyor
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @Mock
    private AddressMapper addressMapper;

    private AddressRequestDto addressRequestDto;
    private AddressResponseDto addressResponseDto;
    private Address address;

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
    public void getAddressAll_ReturnListOfAddressResponseDto_WhenAddressExists() {

        // Arrange
        when(addressRepository.findAll()).thenReturn(List.of(address));
        when(addressMapper.mapToAddressResponseDtoList(List.of(address))).thenReturn(List.of(addressResponseDto));

        // Actual
        List<AddressResponseDto> addressList = addressService.getAddressAll();

        // Assert
        assert addressList != null;
        assert addressList.size() == 1;
        assert addressList.get(0).getId().equals(address.getId());
        assert addressList.get(0).getCity().equals(address.getCity());
        assert addressList.get(0).getCountry().equals(address.getCountry());
        assert addressList.get(0).getDistrict().equals(address.getDistrict());
        assert addressList.get(0).getStreet().equals(address.getStreet());
        assert addressList.get(0).getZipCode().equals(address.getZipCode());
        assert addressList.get(0).getDescription().equals(address.getDescription());

        // Verify
        verify(addressRepository, times(1)).findAll();
        verify(addressMapper, times(1)).mapToAddressResponseDtoList(List.of(address));
    }

    @Test
    public void getAddressAll_ReturnEmptyList_WhenAddressDoesNotExist() {

        // Arrange
        when(addressRepository.findAll()).thenReturn(List.of());
        when(addressMapper.mapToAddressResponseDtoList(List.of())).thenReturn(List.of());

        // Actual
        List<AddressResponseDto> addressList = addressService.getAddressAll();

        // Assert
        assert addressList != null;
        assert addressList.isEmpty();

        // Verify
        verify(addressRepository, times(1)).findAll();
        verify(addressMapper, times(1)).mapToAddressResponseDtoList(List.of());
    }

    @Test
    public void getAddressByID_ReturnAddressResponseDto_WhenAddressExists() {

        // Arrange
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(addressMapper.mapToAddressResponseDto(address)).thenReturn(addressResponseDto);

        // Actual
        AddressResponseDto addressResponseDto = addressService.getAddressById(address.getId());

        // Assert
        assert addressResponseDto != null;
        assert addressResponseDto.getId().equals(address.getId());
        assert addressResponseDto.getCity().equals(address.getCity());
        assert addressResponseDto.getCountry().equals(address.getCountry());
        assert addressResponseDto.getDistrict().equals(address.getDistrict());
        assert addressResponseDto.getStreet().equals(address.getStreet());
        assert addressResponseDto.getZipCode().equals(address.getZipCode());
        assert addressResponseDto.getDescription().equals(address.getDescription());

        // Verify
        verify(addressRepository, times(1)).findById(address.getId());
        verify(addressMapper, times(1)).mapToAddressResponseDto(address);
    }

    @Test
    public void getAddressByID_ReturnNull_WhenAddressDoesNotExist() {

        // Arrange
        String addressId="ADDR1234";
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());


        assertThrows(NullPointerException.class, () -> {
            addressService.getAddressById(addressId);
        });

        // Verify
        verify(addressRepository, times(1)).findById(addressId);
    }
    @Test
    public void updateAddress_ShouldReturnUpdatedAddressResponseDto_WhenValidRequest() {
        // Arrange
        String addressId = "ADDR1234";
        Address existingAddress = getAddress();

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(existingAddress));
        when(addressRepository.save(existingAddress)).thenReturn(existingAddress);
        when(addressMapper.mapToAddressResponseDto(existingAddress)).thenReturn(addressResponseDto);


        // Act
        AddressResponseDto response = addressService.updateAddress(addressId, addressRequestDto);

        // Assert
        assertNotNull(response.getCreatedDate());  // createdDate null olmamalı
        assertEquals(addressRequestDto.getCountry(), response.getCountry());
        assertEquals(addressRequestDto.getCity(), response.getCity());
        assertEquals(addressRequestDto.getDistrict(), response.getDistrict());
        assertEquals(addressRequestDto.getStreet(), response.getStreet());
        assertEquals(addressRequestDto.getZipCode(), response.getZipCode());
        assertEquals(addressRequestDto.getDescription(), response.getDescription());
    }

    private Address getAddress() {
        Address existingAddress = address;


        existingAddress.setCity(addressRequestDto.getCity());
        existingAddress.setCountry(addressRequestDto.getCountry());
        existingAddress.setDistrict(addressRequestDto.getDistrict());
        existingAddress.setStreet(addressRequestDto.getStreet());
        existingAddress.setZipCode(addressRequestDto.getZipCode());
        existingAddress.setDescription(addressRequestDto.getDescription());
        existingAddress.setCreatedDate(addressRequestDto.getCreatedDate());
        return existingAddress;
    }

    @Test
    public void updateAddress_ShouldThrowException_WhenAddressNotFound() {
        // Arrange
        String addressId = "ADDRES1234";
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act & Assert: Address bulunamazsa NullPointerException fırlatılmalı
        assertThrows(NullPointerException.class, () -> addressService.updateAddress(addressId, addressRequestDto));

        // Verify: findById metodunun çağrıldığını kontrol et
        verify(addressRepository, times(1)).findById(addressId);
    }

    @Test
    public void deleteAddressByID_ShouldDeleteAddress_WhenAddressExists() {

        // Arrange
        String addressId="ADDR1234";
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        // Act
        addressService.deleteAddressById(addressId);

        // Verify
        verify(addressRepository, times(1)).findById(addressId);
        verify(addressRepository, times(1)).deleteById(addressId);
    }

    @Test
    public void deleteAddressByID_ShouldThrowException_WhenAddressDoesNotExist() {

        // Arrange
        String addressId="ADDR1234";
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            addressService.deleteAddressById(addressId);
        });

        // Verify
        verify(addressRepository, times(1)).findById(addressId);
        verify(addressRepository, times(0)).deleteById(addressId);

    }
}

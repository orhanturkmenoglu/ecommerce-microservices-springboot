package com.example.customer_service.service;

import com.example.customer_service.dto.customerDto.CustomerRequestDto;
import com.example.customer_service.dto.customerDto.CustomerResponseDto;
import com.example.customer_service.dto.customerDto.CustomerUpdateRequestDto;
import com.example.customer_service.enums.Country;
import com.example.customer_service.mapper.CustomerMapper;
import com.example.customer_service.model.Address;
import com.example.customer_service.model.Customer;
import com.example.customer_service.repository.CustomerRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.Simple.class) // Metodların adlarını belirliyor
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private MailService mailService;

    @Mock
    private CustomerMapper customerMapper;

    private CustomerRequestDto customerRequestDto;
    private CustomerUpdateRequestDto customerUpdateRequestDto;
    private CustomerResponseDto customerResponseDto;
    private Customer customer;
    private Address address;

    @BeforeEach
    public void setUp() {

        // Müşteri nesnesi oluşturuluyor
        customer = new Customer();
        customer.setId("CUST1234");
        customer.setFirstName("Orhan");
        customer.setLastName("TÜRKMENOĞLU");
        customer.setPhoneNumber("5555555555");
        customer.setEmail("orhanturkmenoglu1@gmail.com");
        customer.setCreatedDate(LocalDateTime.now());
        customer.setAddressList(Collections.singletonList(address));

        // CustomerRequestDto nesnesi oluşturuluyor
        customerRequestDto = new CustomerRequestDto();
        customerRequestDto.setFirstName(customer.getFirstName());
        customerRequestDto.setLastName(customer.getLastName());
        customerRequestDto.setPhoneNumber(customer.getPhoneNumber());
        customerRequestDto.setEmail(customer.getEmail());
        customerRequestDto.setCreatedDate(customer.getCreatedDate());

        // Address nesnesi oluşturuluyor
        address = new Address();
        address.setId("ADDRES1234");
        address.setCity("HATAY");
        address.setDistrict("Antakya");
        address.setZipCode("31440");
        address.setStreet("Gazi Mahallesi");
        address.setCountry(Country.TURKEY);
        address.setCreatedDate(LocalDateTime.now());
        address.setDescription("Ev Adresi");

        customerRequestDto.setAddressList(List.of(address));

        // CustomerUpdateRequestDto hazırlanıyor
        customerUpdateRequestDto = new CustomerUpdateRequestDto();
        customerUpdateRequestDto.setFirstName("Orhan");
        customerUpdateRequestDto.setLastName("TÜRKMENOĞLU");
        customerUpdateRequestDto.setPhoneNumber("5555555555");
        customerUpdateRequestDto.setEmail("orhanturkmenoglu13@gmail.com");
        customerUpdateRequestDto.setCreatedDate(LocalDateTime.now());

        // CustomerResponseDto oluşturuluyor
        customerResponseDto = new CustomerResponseDto();
        customerResponseDto.setId(customer.getId());
        customerResponseDto.setFirstName(customer.getFirstName());
        customerResponseDto.setLastName(customer.getLastName());
        customerResponseDto.setPhoneNumber(customer.getPhoneNumber());
        customerResponseDto.setEmail(customer.getEmail());
        customerResponseDto.setCreatedDate(customer.getCreatedDate());

    }

    @Test
    public void createCustomer_ShouldReturnCustomerResponseDto_WhenValidRequest() throws Exception {

        // Arrange: Mock işlemleri
        when(customerMapper.mapToCustomer(customerRequestDto)).thenReturn(customer);
        when(customerMapper.mapToCustomerResponseDto(customer)).thenReturn(customerResponseDto);
        when(customerRepository.save(customer)).thenReturn(customer);

        // Mock email servisi
        doNothing().when(mailService).sendHtmlEmail(eq(customer.getEmail()), eq("Müşteri Oluşturuldu"),
                anyString(), eq(customer.getId()), eq(customer.getEmail()));

        // Act: createCustomer metodunu çağırıyoruz
        CustomerResponseDto response = customerService.createCustomer(customerRequestDto);

        // Assert: Testin doğruluğunu kontrol et
        assertNotNull(response);
        assertEquals(customer.getId(), response.getId());
        assertEquals(customer.getFirstName(), response.getFirstName());
        assertEquals(customer.getLastName(), response.getLastName());
        assertEquals(customer.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(customer.getEmail(), response.getEmail());
        assertEquals(customer.getCreatedDate(), response.getCreatedDate());

        // Verify: E-posta gönderiminin bir kez yapıldığını kontrol et
        verify(mailService, times(1)).sendHtmlEmail(eq(customer.getEmail()), eq("Müşteri Oluşturuldu"),
                anyString(), eq(customer.getId()), eq(customer.getEmail()));

        // Verifying :Repository ve mapper etkileşimlerinin doğrulanması
        verify(customerRepository, times(1)).save(customer);
        verify(customerMapper, times(1)).mapToCustomer(customerRequestDto);
        verify(customerMapper, times(1)).mapToCustomerResponseDto(customer);
    }

    @Test
    public void createCustomer_ShouldThrowNullPointerException_WhenCustomerRequestDtoIsNul() {

        // Arrange: customerRequestDto null olarak ayarlanıyor
        customerRequestDto = null;

        // Act & Assert : NullPointer hatası oluşturuluyor
        Exception exception = assertThrows(NullPointerException.class, () -> customerService.createCustomer(customerRequestDto));
        assertEquals("Customer cannot be null or empty", exception.getMessage());

    }

    @Test
    public void createCustomer_ShouldTriggerCircuitBreakerAndRetry_WhenRepositoryFails() throws Exception {

        // customerRepository.save hata verecek şekilde ayarlanıyor
        when(customerRepository.save(any(Customer.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert: CircuitBreaker ve Retry mekanizmalarının hata durumunda çalışıp çalışmadığını kontrol ediyoruz
        assertThrows(RuntimeException.class, () -> {
            customerService.createCustomer(customerRequestDto);
        });

        // Verify: Fallback metodunun çağrıldığını kontrol etmek için
        verify(mailService, times(0)).sendHtmlEmail(anyString(), anyString(), anyString(), any(), anyString());
    }

    @Test
    public void getCustomersAll_ReturnsListOfCustomerResponseDto_WhenCustomersExist() {

        // Arrange: Mock islemleri
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerMapper.mapToCustomerResponseDtoList(List.of(customer))).thenReturn(List.of(customerResponseDto));

        // Actual
        List<CustomerResponseDto> customerList = customerService.getCustomersAll();

        // Assert
        assertNotNull(customerList);
        assertEquals(1, customerList.size());
        assertEquals(customer.getId(), customerList.get(0).getId());
        assertEquals(customer.getFirstName(), customerList.get(0).getFirstName());
        assertEquals(customer.getLastName(), customerList.get(0).getLastName());
        assertEquals(customer.getPhoneNumber(), customerList.get(0).getPhoneNumber());
        assertEquals(customer.getEmail(), customerList.get(0).getEmail());

        // Verify
        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, times(1)).mapToCustomerResponseDtoList(List.of(customer));
    }

    @Test
    public void getCustomersAll_ReturnsEmptyList_WhenCustomersDoNotExist() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        when(customerMapper.mapToCustomerResponseDtoList(anyList())).thenReturn(Collections.emptyList());

        // Actual
        List<CustomerResponseDto> customerList = customerService.getCustomersAll();

        // Assert
        assertNotNull(customerList);
        assertTrue(customerList.isEmpty());

        // Verify
        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, times(1)).mapToCustomerResponseDtoList(anyList());
    }

    @Test
    public void getCustomerByID_ReturnsCustomerResponseDto_WhenCustomerExists() {
        // Arrange
        String customerId = "CUST1234";
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.mapToCustomerResponseDto(customer)).thenReturn(customerResponseDto);

        // Actual
        customerService.getCustomerById(customerId);

        // Assert
        assertNotNull(customerResponseDto);
        assertEquals(customer.getId(), customerResponseDto.getId());
        assertEquals(customer.getFirstName(), customerResponseDto.getFirstName());
        assertEquals(customer.getLastName(), customerResponseDto.getLastName());
        assertEquals(customer.getPhoneNumber(), customerResponseDto.getPhoneNumber());
        assertEquals(customer.getEmail(), customerResponseDto.getEmail());

        // Verify
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerMapper, times(1)).mapToCustomerResponseDto(customer);
    }

    @Test
    public void getCustomerById_ReturnsNull_WhenCustomerDoesNotExist() {
        // Arrange
        String customerId = "CUST1234";
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(NullPointerException.class, () -> {
            customerService.getCustomerById(customerId);
        });


        // Verify
        verify(customerRepository, times(1)).findById(customerId);
    }


    @Test
    public void getCustomersByFirstName_ReturnsListOfCustomerResponseDto_WhenCustomersExist() {
        // Arrange
        String firstName = "Orhan";
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerMapper.mapToCustomerResponseDtoList(List.of(customer))).thenReturn(List.of(customerResponseDto));

        // Actual
        List<CustomerResponseDto> customerList = customerService.getCustomersByFirstName(firstName);

        // Assert
        assertNotNull(customerList);
        assertEquals(1, customerList.size());
        assertEquals(customer.getId(), customerList.get(0).getId());
        assertEquals(customer.getFirstName(), customerList.get(0).getFirstName());
        assertEquals(customer.getLastName(), customerList.get(0).getLastName());
        assertEquals(customer.getPhoneNumber(), customerList.get(0).getPhoneNumber());
        assertEquals(customer.getEmail(), customerList.get(0).getEmail());

        // Verify
        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, times(1)).mapToCustomerResponseDtoList(List.of(customer));

    }

    @Test
    public void getCustomersByFirstName_ReturnsEmptyList_WhenCustomersDoNotExist() {
        // Arrange
        String firstName = "Orhan";
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        when(customerMapper.mapToCustomerResponseDtoList(anyList())).thenReturn(Collections.emptyList());

        // Actual
        List<CustomerResponseDto> customerList = customerService.getCustomersByFirstName(firstName);

        // Assert
        assertNotNull(customerList);
        assertTrue(customerList.isEmpty());

        // Verify
        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, times(1)).mapToCustomerResponseDtoList(anyList());
    }

    @Test
    public void updateCustomer_ShouldUpdateCustomer_WhenCustomerExists() {
        // Arrange: Setup kısmında oluşturduğumuz customer nesnesi ve güncelleme requesti kullanılacak
        String customerId = "CUST1234"; // Setup'da zaten bu müşteri mevcut
        Customer existingCustomer = customer; // Setup'da zaten customer nesnesi mevcut


        // Beklenen güncellenmiş müşteri verisi
        existingCustomer.setFirstName(customerUpdateRequestDto.getFirstName());
        existingCustomer.setLastName(customerUpdateRequestDto.getLastName());
        existingCustomer.setPhoneNumber(customerUpdateRequestDto.getPhoneNumber());
        existingCustomer.setEmail(customerUpdateRequestDto.getEmail());
        existingCustomer.setCreatedDate(LocalDateTime.now());

        // Mapper ve repository mock işlemleri
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);
        when(customerMapper.mapToCustomerResponseDto(existingCustomer)).thenReturn(customerResponseDto);

        // Act: updateCustomer metodunu çağırıyoruz
        CustomerResponseDto response = customerService.updateCustomer(customerId, customerUpdateRequestDto);

        // Assert: Testin doğruluğunu kontrol et
        assertNotNull(response);
        assertEquals(customerId, response.getId());

        // Verify: Repository ve mapper etkileşimlerinin doğrulanması
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(existingCustomer);
        verify(customerMapper, times(1)).mapToCustomerResponseDto(existingCustomer);
    }

    @Test
    public void updateCustomer_ShouldThrowException_WhenCustomerDoesNotExist() {
        // Arrange
        String customerId = "CUST1234";
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            customerService.updateCustomer(customerId, customerUpdateRequestDto);
        });

        // Verify
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    public void deleteCustomerById_ShouldDeleteCustomer_WhenCustomerIdExists() {
        // Arrange
        String customerId = "CUST1234";

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));


        // Actual
        customerService.deleteCustomerById(customerId);

        // Assert
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    public void deleteCustomerById_ShouldThrowException_WhenCustomerIdDoesNotExist() {
        // Arrange
        String customerId = "CUST1234";
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            customerService.deleteCustomerById(customerId);
        });

        // Verify
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    public void customerServiceFallback_ShouldReturnEmptyCustomerResponseDto_WhenExceptionOccurs() throws Exception {
        // Arrange: Exception simüle ediyoruz
        Exception exception = new RuntimeException("Service is down");

        // Reflection ile private metoda erişim
        Method fallbackMethod = CustomerService.class.getDeclaredMethod("customerServiceFallback", Exception.class);
        fallbackMethod.setAccessible(true);

        // Act: customerServiceFallback metodunu çağırıyoruz
        CustomerResponseDto response = (CustomerResponseDto) fallbackMethod.invoke(customerService, exception);

        // Assert: Dönen CustomerResponseDto'nun boş olması gerektiğini kontrol ediyoruz
        assertNotNull(response);
        assertNull(response.getId()); // ID'nin null olması bekleniyor
        assertNull(response.getFirstName()); // FirstName'in null olması bekleniyor
        assertNull(response.getLastName()); // LastName'in null olması bekleniyor
        assertNull(response.getPhoneNumber()); // PhoneNumber'ın null olması bekleniyor
        assertNull(response.getEmail()); // Email'in null olması bekleniyor
        assertNull(response.getCreatedDate()); // CreatedDate'in null olması bekleniyor
    }

}

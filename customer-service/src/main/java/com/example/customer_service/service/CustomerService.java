package com.example.customer_service.service;

import com.example.customer_service.dto.customerDto.CustomerRequestDto;
import com.example.customer_service.dto.customerDto.CustomerResponseDto;
import com.example.customer_service.dto.customerDto.CustomerUpdateRequestDto;
import com.example.customer_service.mapper.CustomerMapper;
import com.example.customer_service.model.Address;
import com.example.customer_service.model.Customer;
import com.example.customer_service.repository.CustomerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    @Transactional
    @CircuitBreaker(name = "customerServiceBreaker", fallbackMethod = "customerServiceFallback")
    @Retry(name = "customerServiceBreaker", fallbackMethod = "customerServiceFallback")
    @RateLimiter(name = "createCustomerLimiter", fallbackMethod = "customerServiceFallback")
    public CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto) {
        log.info("CustomerService::createCustomer started");

        if (Objects.isNull(customerRequestDto)) {
            throw new NullPointerException("Customer cannot be null or empty");
        }

        List<Address> addressList = customerRequestDto.getAddressList();

        if (addressList != null) {
            addressList.forEach(address -> {
                address.setCreatedDate(LocalDateTime.now());
            });
        }


        Customer customer = customerMapper.mapToCustomer(customerRequestDto);
        log.info("CustomerService::createCustomer customer : {}", customer);

        Customer savedCustomer = customerRepository.save(customer);
        log.info("CustomerService::createCustomer savedCustomer : {}", savedCustomer);

        log.info("CustomerService::createCustomer finished");
        return customerMapper.mapToCustomerResponseDto(savedCustomer);
    }

    @Cacheable(value = "customers", key = "'all'")
    public List<CustomerResponseDto> getCustomersAll() {
        log.info("CustomerService::getCustomersAll started");

        List<Customer> customerList = getCustomers();
        log.info("CustomerService::getCustomersAll customerList : {}", customerList);

        log.info("CustomerService::getCustomersAll finished");
        return customerMapper.mapToCustomerResponseDtoList(customerList);
    }

    @Cacheable(value = "customers", key = "#customerId")
    public CustomerResponseDto getCustomerById(String customerId) {
        log.info("CustomerService::getCustomerById started");

        Customer customer = getCustomer(customerId);
        log.info("CustomerService::getCustomerById customer : {}", customer);


        log.info("CustomerService::getCustomerById finished");
        return customerMapper.mapToCustomerResponseDto(customer);
    }

    @Cacheable(value = "customers", key = "#firstName")
    public List<CustomerResponseDto> getCustomersByFirstName(String firstName) {
        log.info("CustomerService::getCustomerByFirstName started");

        List<Customer> customerList = getCustomers();

        List<Customer> customers = customerList.stream().filter(customer -> customer.getFirstName().contains(firstName)).sorted().toList();

        log.info("CustomerService::getCustomerByFirstName customers : {}", customers);


        log.info("CustomerService::getCustomerByFirstName finished");
        return customerMapper.mapToCustomerResponseDtoList(customers);
    }

    @CacheEvict(value = "customers", key = "#customerId", allEntries = true)
    public void deleteCustomerById(String customerId) {
        log.info("CustomerService::deleteCustomerById started");

        Customer customer = getCustomer(customerId);
        log.info("CustomerService::deleteCustomerById customer : {}", customer);


        customerRepository.deleteById(customer.getId());
        log.info("CustomerService::deleteCustomerById finished");
    }

    @CacheEvict(value = "customers", key = "#customerId", allEntries = true)
    public CustomerResponseDto updateCustomer(String customerId, CustomerUpdateRequestDto customerUpdateRequestDto) {
        log.info("CustomerService::updateCustomer started");

        Customer customer = getCustomer(customerId);

        log.info("CustomerService::updateCustomer customer : {}", customer);

        customer.setFirstName(customerUpdateRequestDto.getFirstName());
        customer.setLastName(customerUpdateRequestDto.getLastName());
        customer.setEmail(customerUpdateRequestDto.getEmail());
        customer.setPhoneNumber(customerUpdateRequestDto.getPhoneNumber());


        Customer updatedCustomer = customerRepository.save(customer);
        log.info("CustomerService::updateCustomer updatedCustomer : {}", updatedCustomer);


        log.info("CustomerService::updateCustomer finished");
        return customerMapper.mapToCustomerResponseDto(updatedCustomer);
    }

    private Customer getCustomer(String customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> new NullPointerException("Customer not found with id :" + customerId));
    }

    private List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    private CustomerResponseDto customerServiceFallback(Exception exception) {
        log.info("fallback is executed because servise is down :{}", exception.getMessage());
        return CustomerResponseDto.builder().build();
    }

}

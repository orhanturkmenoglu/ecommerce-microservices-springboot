package com.example.customer_service.service;

import com.example.customer_service.dto.customerDto.CustomerRequestDto;
import com.example.customer_service.dto.customerDto.CustomerResponseDto;
import com.example.customer_service.exception.ResourceCustomerNotFoundException;
import com.example.customer_service.mapper.CustomerMapper;
import com.example.customer_service.model.Customer;
import com.example.customer_service.repository.CustomerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Customer customer = customerMapper.mapToCustomer(customerRequestDto);
        log.info("CustomerService::createCustomer customer : {}", customer);

        Customer savedCustomer = customerRepository.save(customer);
        log.info("CustomerService::createCustomer savedCustomer : {}", savedCustomer);

        log.info("CustomerService::createCustomer finished");
        return customerMapper.mapToCustomerResponseDto(savedCustomer);
    }

    public List<CustomerResponseDto> getCustomersAll() {
        log.info("CustomerService::getCustomersAll started");

        List<Customer> customerList = customerRepository.findAll();
        log.info("CustomerService::getCustomersAll customerList : {}", customerList);

        log.info("CustomerService::getCustomersAll finished");
        return customerMapper.mapToCustomerResponseDtoList(customerList);
    }

    public CustomerResponseDto getCustomerById(String customerId) {
        log.info("CustomerService::getCustomerById started");

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new NullPointerException("Customer not found with id :" + customerId));
        log.info("CustomerService::getCustomerById customer : {}", customer);


        log.info("CustomerService::getCustomerById finished");
        return customerMapper.mapToCustomerResponseDto(customer);
    }

    public List<CustomerResponseDto> getCustomersByFirstName(String firstName) {
        log.info("CustomerService::getCustomerByFirstName started");

        List<Customer> customerList = customerRepository.findAll();

        List<Customer> customers = customerList.stream()
                .filter(customer ->customer.getFirstName().contains(firstName))
                .sorted()
                .toList();

        log.info("CustomerService::getCustomerByFirstName customers : {}", customers);


        log.info("CustomerService::getCustomerByFirstName finished");
        return customerMapper.mapToCustomerResponseDtoList(customers);
    }

    public void deleteCustomerById(String customerId) {
        log.info("CustomerService::deleteCustomerById started");

        CustomerResponseDto customer = getCustomerById(customerId);
        log.info("CustomerService::deleteCustomerById customer : {}", customer);


        customerRepository.deleteById(customerId);
        log.info("CustomerService::deleteCustomerById finished");
    }


    public CustomerResponseDto updateCustomer(String customerId, CustomerRequestDto customerRequestDto) {
        log.info("CustomerService::updateCustomer started");

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceCustomerNotFoundException("Customer not found"));

        log.info("CustomerService::updateCustomer customer : {}", customer);

        customer.setFirstName(customerRequestDto.getFirstName());
        customer.setLastName(customerRequestDto.getLastName());
        customer.setEmail(customerRequestDto.getEmail());
        customer.setPhoneNumber(customerRequestDto.getPhoneNumber());
        customer.setAddressList(customerRequestDto.getAddressList());


        Customer updatedCustomer = customerRepository.save(customer);
        log.info("CustomerService::updateCustomer updatedCustomer : {}", updatedCustomer);


        log.info("CustomerService::updateCustomer finished");
        return customerMapper.mapToCustomerResponseDto(updatedCustomer);
    }


    private CustomerResponseDto customerServiceFallback(Exception exception) {
        log.info("fallback is executed because servise is down :{}", exception.getMessage());
        return CustomerResponseDto.builder().build();
    }

}

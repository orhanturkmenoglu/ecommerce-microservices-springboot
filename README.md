# E-commerce Microservices with Spring Boot
This repository contains a sample e-commerce application built using Spring Boot microservices architecture.
The project demonstrates various concepts and best practices of microservices, including service registry, API gateway, fault tolerance, distributed configuration, and more.


## Technologies and Features

- **Spring Boot**: Core framework for building microservices.
- **Spring Cloud Config**: Centralized configuration management.
- **Spring Cloud Netflix Eureka**: Service registry and discovery.
- **Spring Cloud Gateway**: API gateway for routing requests.
- **Feign Client**: Declarative REST client for inter-service communication.
- **Resilience4J**: Fault tolerance library for implementing circuit breakers, retries, and rate limiters.
- **MapStruct**: Java-based code generator for mapping DTOs.
- **Validation**: Input validation using Hibernate Validator.
- **SLF4J**: Logging framework.
- **Health Checks**: Monitoring the health of each microservice.

## Project Structure

- **config-server**: Centralized configuration server.
- **discovery-server**: Service registry using Eureka.
- **api-gateway**: API Gateway using Spring Cloud Gateway.
- **product-service**: Manages product information.
- **inventory-service**: Manages inventory and stock levels.
- **order-service**: Manages customer orders.

### Features

- **Service Discovery**: Using Eureka for registering and discovering services.
- **API Gateway**: Central entry point for all client requests.
- **Centralized Configuration**: Using Spring Cloud Config for managing configurations.
- **Fault Tolerance**: Implementing Resilience4J for circuit breaker, retry mechanisms, and rate limiting.
- **Validation**: Using Hibernate Validator for input validation.
- **DTO Mapping**: Using MapStruct for mapping DTOs and entities.
- **Health Checks**: Monitoring the health status of each microservice.


### DTO Structure

The project follows a standard DTO (Data Transfer Object) structure to decouple internal models from API models.
DTOs are used for request and response payloads.

### Configuration

- **Config Server**: Manages the configurations for all services. Make sure to update the `config-server` repository path in `application.yml` of each service.
- **Discovery Server**: Registers and discovers all the services. Ensure all services point to the `discovery-server` in their `application.yml`.
- **API Gateway**: Routes requests to appropriate services based on the defined routes in `application.yml`.
  

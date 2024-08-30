package com.example.customer_service.dto.customerDto;

import com.example.customer_service.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for Customer request")
public class CustomerRequestDto implements Serializable {

    @NotEmpty(message = "Customer firstName cannot be empty")
    @Schema(description = "The first name of the customer", example = "John")
    private String firstName;

    @NotEmpty(message = "Customer lastName cannot be empty")
    @Schema(description = "The last name of the customer", example = "Doe")
    private String lastName;

    @NotEmpty(message = "Customer email cannot be empty")
    @Schema(description = "The email address of the customer", example = "john.doe@example.com")
    private String email;

    @NotEmpty(message = "Customer phoneNumber cannot be empty")
    @Schema(description = "The phone number of the customer", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "The date and time when the customer was created", example = "2024-08-30T10:00:00")
    private LocalDateTime createdDate;

    @NotEmpty(message = "Customer addressList cannot be empty")
    @Schema(description = "List of addresses associated with the customer")
    private List<Address> addressList;
}

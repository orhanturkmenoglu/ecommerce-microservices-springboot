package com.example.customer_service.dto.customerDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for updating customer details")
public class CustomerUpdateRequestDto implements Serializable {

    @Schema(description = "The first name of the customer", example = "Jane")
    @NotEmpty(message = "Customer firstName cannot be empty")
    private String firstName;

    @Schema(description = "The last name of the customer", example = "Smith")
    @NotEmpty(message = "Customer lastName cannot be empty")
    private String lastName;

    @Schema(description = "The email address of the customer", example = "jane.smith@example.com")
    @NotEmpty(message = "Customer email cannot be empty")
    private String email;

    @Schema(description = "The phone number of the customer", example = "+0987654321")
    @NotEmpty(message = "Customer phoneNumber cannot be empty")
    private String phoneNumber;

    @Schema(description = "The date and time when the customer was created", example = "2024-08-30T10:00:00")
    private LocalDateTime createdDate;
}

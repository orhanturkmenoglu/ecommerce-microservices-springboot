package com.example.customer_service.dto.customerDto;

import com.example.customer_service.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data Transfer Object for Customer response")
public class CustomerResponseDto implements Serializable {

    @Schema(description = "The unique identifier of the customer", example = "12345")
    private String id;

    @Schema(description = "The first name of the customer", example = "John")
    private String firstName;

    @Schema(description = "The last name of the customer", example = "Doe")
    private String lastName;

    @Schema(description = "The email address of the customer", example = "john.doe@example.com")
    private String email;

    @Schema(description = "The phone number of the customer", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "The date and time when the customer was created", example = "2024-08-30T10:00:00")
    private LocalDateTime createdDate;

    @Schema(description = "List of addresses associated with the customer")
    private List<Address> addressList;

}

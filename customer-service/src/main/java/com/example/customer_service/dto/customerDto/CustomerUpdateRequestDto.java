package com.example.customer_service.dto.customerDto;

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
public class CustomerUpdateRequestDto implements Serializable {

    @NotEmpty(message = "Customer firstName cannot be empty")
    private String firstName;

    @NotEmpty(message = "Customer lastName cannot be empty")
    private String lastName;

    @NotEmpty(message = "Customer email cannot be empty")
    private String email;

    @NotEmpty(message = "Customer phoneNumber cannot be empty")
    private String phoneNumber;

    private LocalDateTime createdDate;

}

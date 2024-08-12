package com.example.customer_service.dto.customerDto;

import com.example.customer_service.model.Address;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestDto implements Serializable {

    @NotEmpty(message = "Customer firstName cannot be empty")
    private String firstName;

    @NotEmpty(message = "Customer lastName cannot be empty")
    private String lastName;

    @NotEmpty(message = "Customer email cannot be empty")
    private String email;

    @NotEmpty(message = "Customer phoneNumber cannot be empty")
    private String phoneNumber;

    @NotEmpty(message = "Customer addressList cannot be empty")
    private List<Address> addressList;
}

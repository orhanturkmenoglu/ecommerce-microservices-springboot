package com.example.auth_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the response data for user registration.")
public class UserRegistrationResponseDTO {
    
    @Schema(description = "Message indicating the result of the user registration process.", example = "User registered successfully.")
    private String message;
}

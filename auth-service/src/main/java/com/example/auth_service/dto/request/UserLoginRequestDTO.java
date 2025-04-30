package com.example.auth_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents user login information.")
public class UserLoginRequestDTO {

    @Email(message = "Invalid email format.")
    @NotBlank(message = "Email cannot be empty.")
    @Schema(description = "User's email address.", example = "orhanturkmenoglu@example.com")
    private String email;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    @Schema(description = "User's password.", example = "securePassword123", minLength = 6)
    private String password;
}

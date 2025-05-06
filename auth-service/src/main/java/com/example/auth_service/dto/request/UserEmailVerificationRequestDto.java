package com.example.auth_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for verifying a user's email with a verification code.")
public class UserEmailVerificationRequestDto {

    @Email(message = "Invalid email format.")
    @NotBlank(message = "Email cannot be empty.")
    @Schema(description = "User's email address.", example = "orhanturkmenoglu@example.com")
    private String email;

    @NotBlank(message = "Verification code cannot be empty.")
    @Schema(description = "Email verification code sent to the user.", example = "123456")
    private String verificationCode;
}

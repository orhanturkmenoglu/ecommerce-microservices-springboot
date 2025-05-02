package com.example.auth_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request object for updating user password.")
public class UserUpdatePasswordRequestDTO {

    @NotBlank(message = "Old password cannot be empty.")
    @Schema(description = "User's old password.", example = "oldPassword123")
    private String oldPassword;

    @NotBlank(message = "New password cannot be empty.")
    @Schema(description = "User's new password.", example = "newPassword123")
    private String newPassword;

    @NotBlank(message = "Confirm password cannot be empty.")
    @Schema(description = "User's confirmed password.", example = "newPassword123")
    private String confirmPassword;
}

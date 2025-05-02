package com.example.auth_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object for updating user password.")
public class UserUpdatePasswordResponseDTO {

    @Schema(description = "Message indicating the result of the password update process.", example = "Password updated successfully.")
    String message;
}

package com.example.auth_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the response data for user login including the access and refresh tokens.")
public class UserLoginResponseDTO {

    @JsonProperty("access_token")
    @Schema(description = "JWT Access Token that allows the user to authenticate requests.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(description = "JWT Refresh Token used to obtain new access tokens when the old one expires.", example = "dGhpc2lzYXJlZnJlc2h0b2tlbg==")
    private String refreshToken;
}

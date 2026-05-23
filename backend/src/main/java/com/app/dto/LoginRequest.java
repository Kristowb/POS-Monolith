package com.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username atau email tidak boleh kosong")
    private String usernameOrEmail;

    @NotBlank(message = "Password tidak boleh kosong")
    private String password;
}

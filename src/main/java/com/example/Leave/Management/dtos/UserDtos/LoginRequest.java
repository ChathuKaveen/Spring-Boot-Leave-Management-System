package com.example.Leave.Management.dtos.UserDtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Add email")
    private String email;

    @NotBlank(message = "Add Password")
    private String password;
}

package com.example.Leave.Management.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "Name can't be empty")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Add a email")
    @Email(message = "Must be valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}

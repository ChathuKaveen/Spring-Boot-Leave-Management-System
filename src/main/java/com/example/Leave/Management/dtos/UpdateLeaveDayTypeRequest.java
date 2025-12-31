package com.example.Leave.Management.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateLeaveDayTypeRequest {
    @NotBlank(message = "Add a Type")
    @Size(max = 100)
    private String type;
}

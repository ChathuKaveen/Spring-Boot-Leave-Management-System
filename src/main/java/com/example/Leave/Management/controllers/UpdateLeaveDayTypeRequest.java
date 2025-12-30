package com.example.Leave.Management.controllers;

import com.example.Leave.Management.entities.DayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateLeaveDayTypeRequest {
    @NotBlank(message = "Add a Type")
    @Size(max = 100)
    private String type;
}

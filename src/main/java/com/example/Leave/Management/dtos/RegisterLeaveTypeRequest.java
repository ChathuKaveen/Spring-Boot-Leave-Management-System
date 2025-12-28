package com.example.Leave.Management.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterLeaveTypeRequest {
    @NotBlank(message = "Type can't be empty")
    @Size(max = 45)
    private String type;

    @NotBlank(message = "Add a description")
    @Size(max = 100)
    private String description;

    @NotNull(message = "Paid must be specified")
    private Boolean paid;

    @NotNull(message = "Half-day allowed must be specified")
    private Boolean halfdayAllowed;
}

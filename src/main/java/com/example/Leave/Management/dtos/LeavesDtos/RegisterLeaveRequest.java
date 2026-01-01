package com.example.Leave.Management.dtos.LeavesDtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterLeaveRequest {
    @NotNull(message = "Leave type is required")
    private int leaveType;
    @NotNull(message = "From date is required")
    private LocalDate from_date;
    private LocalDate to_date;
    @NotNull(message = "From date type is required")
    private Integer from_date_type;
    @Size(max = 200, message = "Reason must be at most 200 characters")
    private String reason;
}

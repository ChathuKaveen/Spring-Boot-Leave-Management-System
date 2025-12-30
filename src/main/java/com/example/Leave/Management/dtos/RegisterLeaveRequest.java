package com.example.Leave.Management.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterLeaveRequest {
    private int leaveType;
    private LocalDate from_date;
    private LocalDate to_date;
    private Integer from_date_type;
    private Integer to_date_type;
    private String reason;
}

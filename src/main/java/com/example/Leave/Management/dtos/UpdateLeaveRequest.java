package com.example.Leave.Management.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateLeaveRequest {
    private Integer leaveType;
    private LocalDate from_date;
    private LocalDate to_date;
    private Integer from_date_type;
    private String reason;
}

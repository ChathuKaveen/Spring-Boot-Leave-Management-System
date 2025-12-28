package com.example.Leave.Management.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class RegisterLeaveRequest {

    private Long userId;
    private int leaveType;
    private int days;
    private Date from_date;
    private Date to_date;
    private Integer from_date_type;
    private Integer to_date_type;
    private String reason;
}

package com.example.Leave.Management.dtos.LeavesDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@Getter
public class LeaveDto {
    private Long id;
    private Long userId;
    private int leaveType;
    private String status;
    private double days;
    private Date from_date;
    private Date to_date;
    private Integer from_date_type;
    private Integer to_date_type;
    private String reason;
    private int updated_by;
    private LocalDateTime updated_on;
}

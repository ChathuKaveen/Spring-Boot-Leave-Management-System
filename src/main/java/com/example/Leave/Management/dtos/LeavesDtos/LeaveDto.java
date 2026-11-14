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
    private String userName;
    private String leaveType;
    private Long leaveTypeId;
    private String status;
    private double days;
    private Date from_date;
    private Date to_date;
    private String from_date_type;
    private String to_date_type;
    private Long from_date_type_id;
    private Long to_date_type_id;
    private String reason;
    private int updated_by;
    private LocalDateTime updated_on;
}

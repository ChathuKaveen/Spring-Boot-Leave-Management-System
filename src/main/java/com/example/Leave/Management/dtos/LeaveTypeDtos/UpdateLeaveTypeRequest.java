package com.example.Leave.Management.dtos.LeaveTypeDtos;

import lombok.Data;

@Data
public class UpdateLeaveTypeRequest {

    private String type;
    private String description;
    private boolean paid;
    private boolean halfday_allowed;
}

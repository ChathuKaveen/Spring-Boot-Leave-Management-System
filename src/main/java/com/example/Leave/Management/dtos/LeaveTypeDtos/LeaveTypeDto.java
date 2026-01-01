package com.example.Leave.Management.dtos.LeaveTypeDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LeaveTypeDto {

    private int id;
    private String type;
    private String description;
    private boolean paid;
    private boolean halfday_allowed;
}

package com.example.Leave.Management.dtos;

import com.example.Leave.Management.entities.DayType;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UpdateLeaveTypeRequest {

    private String type;
    private String description;
    private boolean paid;
    private boolean halfday_allowed;
}

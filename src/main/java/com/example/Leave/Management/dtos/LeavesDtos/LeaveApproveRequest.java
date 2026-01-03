package com.example.Leave.Management.dtos.LeavesDtos;

import com.example.Leave.Management.entities.Status;
import lombok.Data;

@Data
public class LeaveApproveRequest {
    private Status status;
}

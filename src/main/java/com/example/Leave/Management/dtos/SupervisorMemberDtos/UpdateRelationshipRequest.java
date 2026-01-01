package com.example.Leave.Management.dtos.SupervisorMemberDtos;

import com.example.Leave.Management.entities.SupervisorType;
import lombok.Data;

@Data
public class UpdateRelationshipRequest {
    private Long supervisor;
    private SupervisorType type;
}

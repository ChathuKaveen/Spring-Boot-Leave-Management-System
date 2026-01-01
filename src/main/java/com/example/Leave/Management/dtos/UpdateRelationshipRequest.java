package com.example.Leave.Management.dtos;

import com.example.Leave.Management.entities.SupervisorType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRelationshipRequest {
    private Long supervisor;
    private SupervisorType type;
}

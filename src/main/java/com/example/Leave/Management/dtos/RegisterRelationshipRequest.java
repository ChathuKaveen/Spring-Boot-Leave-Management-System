package com.example.Leave.Management.dtos;

import com.example.Leave.Management.entities.SupervisorMember;
import com.example.Leave.Management.entities.SupervisorType;
import com.example.Leave.Management.entities.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRelationshipRequest {
    @NotNull(message = "Please add the user")
    private Long user;

    @NotNull(message = "Please add the supervisor")
    private Long supervisor;

    @NotNull(message = "Please add the supervisor type PRIMARY or SECONDARY")
    private SupervisorType type;
}

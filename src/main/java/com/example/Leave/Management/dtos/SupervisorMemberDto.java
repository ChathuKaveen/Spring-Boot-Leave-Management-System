package com.example.Leave.Management.dtos;

import com.example.Leave.Management.entities.SupervisorType;
import com.example.Leave.Management.entities.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class SupervisorMemberDto {
    private Long id;
    private User user;
    private User supervisor;
    private SupervisorType type;
}

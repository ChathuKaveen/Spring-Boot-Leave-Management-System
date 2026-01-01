package com.example.Leave.Management.dtos;

import com.example.Leave.Management.entities.SupervisorType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupervisorMemberResponse {
        private Long id;
        private Long userId;
        private Long supervisorId;
        private SupervisorType type;

}

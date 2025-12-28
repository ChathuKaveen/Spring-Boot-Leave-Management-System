package com.example.Leave.Management.services;

import com.example.Leave.Management.dtos.RegisterUserRequest;
import com.example.Leave.Management.dtos.UserDto;
import com.example.Leave.Management.exceptions.UserAlreadyExisist;
import com.example.Leave.Management.repositories.LeaveTypeRepository;
import com.example.Leave.Management.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LeaveTypeService {
    private final LeaveTypeRepository leaveTypeRepository;

}

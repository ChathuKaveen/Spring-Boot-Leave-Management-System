package com.example.Leave.Management.services;

import com.example.Leave.Management.repositories.LeaveTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LeaveTypeService {
    private final LeaveTypeRepository leaveTypeRepository;

}

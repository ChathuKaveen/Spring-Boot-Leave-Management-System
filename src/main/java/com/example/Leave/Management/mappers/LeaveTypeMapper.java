package com.example.Leave.Management.mappers;

import com.example.Leave.Management.dtos.LeaveTypeDtos.LeaveTypeDto;
import com.example.Leave.Management.entities.LeaveTypes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaveTypeMapper {
    LeaveTypeDto toDto(LeaveTypes leaveTypes);
}

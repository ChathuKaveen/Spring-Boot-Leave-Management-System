package com.example.Leave.Management.mappers;

import com.example.Leave.Management.dtos.LeaveDayTypeDtos.LeaveDayTypeDto;
import com.example.Leave.Management.entities.LeaveDayType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaveDayTypeMapper {
    LeaveDayTypeDto toDto(LeaveDayType type);
}

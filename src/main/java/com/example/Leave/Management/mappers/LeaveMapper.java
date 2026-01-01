package com.example.Leave.Management.mappers;

import com.example.Leave.Management.dtos.LeavesDtos.LeaveDto;
import com.example.Leave.Management.entities.Leaves;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveMapper {
    @Mapping(target = "userId" , source = "user.id")
    @Mapping(target = "leaveType" , source = "leaveTypes.id")
    @Mapping(target = "from_date_type" , source = "from_date_type.id")
    @Mapping(target = "to_date_type" , source = "to_date_type.id")
    LeaveDto toDto(Leaves leaves);
}

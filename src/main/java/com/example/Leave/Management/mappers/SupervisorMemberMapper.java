package com.example.Leave.Management.mappers;

import com.example.Leave.Management.dtos.SupervisorMemberDtos.SupervisorMemberDto;
import com.example.Leave.Management.dtos.SupervisorMemberDtos.SupervisorMemberResponse;
import com.example.Leave.Management.entities.SupervisorMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupervisorMemberMapper {
    SupervisorMemberDto toDto(SupervisorMember sm);

    @Mapping(target = "id" , source = "id")
    @Mapping(target = "userId" , source = "user.id")
    @Mapping(target = "supervisorId" , source = "supervisor.id")
    @Mapping(target = "type" , source = "type")
    SupervisorMemberResponse toResponse(SupervisorMember sm);
}

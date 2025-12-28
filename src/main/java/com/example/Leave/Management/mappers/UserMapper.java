package com.example.Leave.Management.mappers;

import com.example.Leave.Management.dtos.RegisterUserRequest;
import com.example.Leave.Management.dtos.UserDto;
import com.example.Leave.Management.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User u);
    User toEntity(RegisterUserRequest request);
}

package com.example.Leave.Management.dtos.UserDtos;

import com.example.Leave.Management.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
}

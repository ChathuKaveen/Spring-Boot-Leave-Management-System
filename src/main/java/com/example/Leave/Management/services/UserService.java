package com.example.Leave.Management.services;

import com.example.Leave.Management.dtos.RegisterUserRequest;
import com.example.Leave.Management.dtos.UpdateUserRequest;
import com.example.Leave.Management.dtos.UserDto;
import com.example.Leave.Management.entities.User;
import com.example.Leave.Management.exceptions.UserAlreadyExisist;
import com.example.Leave.Management.exceptions.UserNotFoundException;
import com.example.Leave.Management.mappers.UserMapper;
import com.example.Leave.Management.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    public UserDto getUser(Long id){
        var user =  userRepository.findById(id).orElse(null);
        if(user == null){
            throw  new UserNotFoundException();
        }
        return userMapper.toDto(user);
    }

    public UserDto createUser(RegisterUserRequest request){
        var obj = userMapper.toEntity(request);
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExisist();
        }
        obj.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(obj);
        return userMapper.toDto(obj);
    }

    public void deleteUser(Long id){
        var u  = userRepository.findById(id).orElseThrow();
        if(u == null){
            throw  new UserNotFoundException();
        }
        userRepository.delete(u);
    }

    public UserDto updateUser(UpdateUserRequest request , Long id){
        var u  = userRepository.findById(id).orElseThrow();
        if(u == null){
            throw  new UserNotFoundException();
        }
        u.setName(request.getName());
        u.setEmail(request.getEmail());
        userRepository.save(u);

        return userMapper.toDto(u);
    }
}

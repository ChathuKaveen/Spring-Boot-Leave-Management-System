package com.example.Leave.Management.controllers;

import com.example.Leave.Management.dtos.ChangePasswordRequest;
import com.example.Leave.Management.dtos.RegisterUserRequest;
import com.example.Leave.Management.dtos.UpdateUserRequest;
import com.example.Leave.Management.dtos.UserDto;
import com.example.Leave.Management.exceptions.UserNotFoundException;
import com.example.Leave.Management.mappers.UserMapper;
import com.example.Leave.Management.repositories.UserRepository;
import com.example.Leave.Management.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;
    @GetMapping
    public Iterable<UserDto> getAllUsers(){
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterUserRequest request , UriComponentsBuilder uriBuilder){
        var userDto = userService.createUser(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserRequest request ,@PathVariable(name="id") Long id){
        return ResponseEntity.ok(userService.updateUser(request , id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable(name="id") Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable(name = "id") Long id , @RequestBody ChangePasswordRequest request){
        var user = userRepository.findById(id).orElseThrow();
        if(!user.getPassword().equals(request.getOldPassword())){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String , String>> handleUserNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error" , "User Not Found"));
    }

}

package com.example.Leave.Management.controllers;

import com.example.Leave.Management.dtos.*;
import com.example.Leave.Management.exceptions.*;
import com.example.Leave.Management.mappers.LeaveMapper;
import com.example.Leave.Management.repositories.LeaveDayTypeRepository;
import com.example.Leave.Management.repositories.LeaveTypeRepository;
import com.example.Leave.Management.repositories.LeavesRepository;
import com.example.Leave.Management.services.LeaveService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/leave")
public class LeaveController {
    private final LeavesRepository leavesRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveDayTypeRepository leaveDayTypeRepository;
    private final LeaveMapper leaveMapper;

    private final LeaveService leaveService;

    @PostMapping
    public ResponseEntity<?> createLeaveDayType(@Valid @RequestBody RegisterLeaveRequest request , UriComponentsBuilder uriBuilder){
        var response = leaveService.createLeave(request);
        var uri = uriBuilder.path("/leave-day-type/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public Iterable<LeaveDto> getLeaves(){
        return leavesRepository.findAll().stream().map(leaveMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveDto> getLeaveById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateLeave(@RequestBody UpdateLeaveRequest request , @PathVariable(name="id") Long id){
        return ResponseEntity.ok(leaveService.updateLeave(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> deleteLeave(@PathVariable(name="id") Long id){
        leaveService.deleteLeave(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(FromDateToDateException.class)
    public ResponseEntity<Map<String , String>> handleUserNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "To Date cant be before From Date"));
    }

    @ExceptionHandler(LeaveTypeNotFoundException.class)
    public ResponseEntity<Map<String , String>> handleLeaveTypeNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "Leave day type not found"));
    }
    @ExceptionHandler(HalfDayMustBeSeperateDayException.class)
    public ResponseEntity<Map<String , String>> handleHalfDay(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "HALF DAY MORNING leave must be for a single day"));
    }

    @ExceptionHandler(LeaveNotFoundException.class)
    public ResponseEntity<Map<String , String>> leaveNotFoundExceptionHandler(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "Leave not found"));
    }
}

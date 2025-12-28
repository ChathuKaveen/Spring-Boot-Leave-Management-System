package com.example.Leave.Management.controllers;

import com.example.Leave.Management.dtos.*;
import com.example.Leave.Management.entities.LeaveDayType;
import com.example.Leave.Management.entities.Leaves;
import com.example.Leave.Management.entities.Status;
import com.example.Leave.Management.exceptions.UserNotFoundException;
import com.example.Leave.Management.mappers.LeaveMapper;
import com.example.Leave.Management.repositories.LeaveDayTypeRepository;
import com.example.Leave.Management.repositories.LeaveTypeRepository;
import com.example.Leave.Management.repositories.LeavesRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class LeaveController {
    private final LeavesRepository leavesRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveDayTypeRepository leaveDayTypeRepository;
    private final LeaveMapper leaveMapper;

    @PostMapping
    public ResponseEntity<?> createLeaveDayType(@Valid @RequestBody RegisterLeaveRequest request , UriComponentsBuilder uriBuilder){
        Leaves leave = new Leaves();
        var leaveTypes = leaveTypeRepository.findById(request.getLeaveType()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND  , "This leave type not found"));
        leave.setLeaveTypes(leaveTypes);
        leave.setReason(request.getReason());
        leave.setStatus(Status.PENDING);
        leave.setFrom_date(request.getFrom_date());
        leave.setTo_date(request.getTo_date());
        var leaveFromDayTypeFinder = leaveDayTypeRepository.findById(request.getFrom_date_type()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND  , "This leave day type not found"));
        var leaveToDayTypeFinder = leaveDayTypeRepository.findById(request.getTo_date_type()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND  , "This leave day type not found"));
        leave.setFrom_date_type(leaveFromDayTypeFinder);
        leave.setTo_date_type(leaveToDayTypeFinder);

        // need to add user

        var saved = leavesRepository.save(leave);
        var response = leaveMapper.toDto(saved);
        var uri = uriBuilder.path("/leave-day-type/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

//    @GetMapping
//    public Iterable<LeaveDayTypeDto> getLeaveDayTypes(){
//        return leaveDayTypeRepository.findAll().stream().map(leaveDayTypeMapper::toDto).toList();
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<LeaveDayTypeDto> getLeaveTypeById(@PathVariable(name = "id") int id){
//        var type = leaveDayTypeRepository.findById(id).orElseThrow(null);
//        if(type == null){
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(leaveDayTypeMapper.toDto(type));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<LeaveDayTypeDto> updateLeaveType(@RequestBody UpdateLeaveTypeRequest request , @PathVariable(name="id") int id){
//        var type = leaveDayTypeRepository.findById(id).orElseThrow();
//        if(type == null){
//            return ResponseEntity.notFound().build();
//        }
//        type.setType(request.getType());
//
//        leaveDayTypeRepository.save(type);
//        return ResponseEntity.ok(leaveDayTypeMapper.toDto(type));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<LeaveTypeDto> deleteLeaveType(@PathVariable(name="id") int id){
//        var u  = leaveDayTypeRepository.findById(id).orElseThrow();
//        if(u == null){
//            throw  new UserNotFoundException();
//        }
//        leaveDayTypeRepository.delete(u);
//        return ResponseEntity.noContent().build();
//    }
}

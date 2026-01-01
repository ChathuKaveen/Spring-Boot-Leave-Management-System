package com.example.Leave.Management.controllers;

import com.example.Leave.Management.dtos.LeaveDayTypeDtos.LeaveDayTypeDto;
import com.example.Leave.Management.dtos.LeaveDayTypeDtos.RegisterLeaveDayTypeDto;
import com.example.Leave.Management.dtos.LeaveDayTypeDtos.UpdateLeaveDayTypeRequest;
import com.example.Leave.Management.dtos.LeaveTypeDtos.LeaveTypeDto;
import com.example.Leave.Management.entities.DayType;
import com.example.Leave.Management.entities.LeaveDayType;
import com.example.Leave.Management.exceptions.UserNotFoundException;
import com.example.Leave.Management.mappers.LeaveDayTypeMapper;
import com.example.Leave.Management.repositories.LeaveDayTypeRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/leave-day-type")
@AllArgsConstructor
public class LeaveDayTypeController {
    private final LeaveDayTypeMapper leaveDayTypeMapper;
    private final LeaveDayTypeRepository leaveDayTypeRepository;

    @PostMapping
    public ResponseEntity<?> createLeaveDayType(@Valid @RequestBody RegisterLeaveDayTypeDto request , UriComponentsBuilder uriBuilder){
        LeaveDayType leaveTypes = new LeaveDayType();
        leaveTypes.setType(DayType.valueOf(request.getType()));

        var saved = leaveDayTypeRepository.save(leaveTypes);
        var response = leaveDayTypeMapper.toDto(saved);
        var uri = uriBuilder.path("/leave-day-type/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public Iterable<LeaveDayTypeDto> getLeaveDayTypes(){
        return leaveDayTypeRepository.findAll().stream().map(leaveDayTypeMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveDayTypeDto> getLeaveTypeById(@PathVariable(name = "id") int id){
        var type = leaveDayTypeRepository.findById(id).orElseThrow(null);
        if(type == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(leaveDayTypeMapper.toDto(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveDayTypeDto> updateLeaveType(@RequestBody UpdateLeaveDayTypeRequest request , @PathVariable(name="id") int id){
        var type = leaveDayTypeRepository.findById(id).orElseThrow(null);
        if(type == null){
            return ResponseEntity.notFound().build();
        }
        type.setType(DayType.valueOf(request.getType()));

        leaveDayTypeRepository.save(type);
        return ResponseEntity.ok(leaveDayTypeMapper.toDto(type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> deleteLeaveType(@PathVariable(name="id") int id){
        var u  = leaveDayTypeRepository.findById(id).orElseThrow();
        if(u == null){
            throw  new UserNotFoundException();
        }
        leaveDayTypeRepository.delete(u);
        return ResponseEntity.noContent().build();
    }
}

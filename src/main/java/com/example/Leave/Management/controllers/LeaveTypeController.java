package com.example.Leave.Management.controllers;

import com.example.Leave.Management.dtos.LeaveTypeDtos.LeaveTypeDto;
import com.example.Leave.Management.dtos.LeaveTypeDtos.RegisterLeaveTypeRequest;
import com.example.Leave.Management.dtos.LeaveTypeDtos.UpdateLeaveTypeRequest;
import com.example.Leave.Management.entities.LeaveTypes;
import com.example.Leave.Management.exceptions.UserNotFoundException;
import com.example.Leave.Management.mappers.LeaveTypeMapper;
import com.example.Leave.Management.repositories.LeaveTypeRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/leave-type")
public class LeaveTypeController {
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    @PostMapping
    public ResponseEntity<?> createLeaveType(@Valid @RequestBody RegisterLeaveTypeRequest request , UriComponentsBuilder uriBuilder){
        LeaveTypes leaveTypes = new LeaveTypes();
        leaveTypes.setType(request.getType());
        leaveTypes.setDescription(request.getDescription());
        leaveTypes.setPaid(request.getPaid());
        leaveTypes.setHalfday_allowed(request.getHalfdayAllowed());

        var saved = leaveTypeRepository.save(leaveTypes);
        var response = leaveTypeMapper.toDto(saved);
        var uri = uriBuilder.path("/leave-type/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public Iterable<LeaveTypeDto> getLeaveTypes(){
        return leaveTypeRepository.findAll().stream().map(leaveTypeMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> getLeaveTypeById(@PathVariable(name = "id") int id){
        var type = leaveTypeRepository.findById(id).orElseThrow(null);
        if(type == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(leaveTypeMapper.toDto(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> updateLeaveType(@RequestBody UpdateLeaveTypeRequest request , @PathVariable(name="id") int id){
        var type = leaveTypeRepository.findById(id).orElseThrow();
        if(type == null){
            return ResponseEntity.notFound().build();
        }
        type.setType(request.getType());
        type.setDescription(request.getDescription());
        type.setPaid(request.isPaid());
        type.setHalfday_allowed(request.isHalfday_allowed());

        leaveTypeRepository.save(type);
        return ResponseEntity.ok(leaveTypeMapper.toDto(type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> deleteLeaveType(@PathVariable(name="id") int id){
        var u  = leaveTypeRepository.findById(id).orElseThrow();
        if(u == null){
            throw  new UserNotFoundException();
        }
        leaveTypeRepository.delete(u);
        return ResponseEntity.noContent().build();
    }
}

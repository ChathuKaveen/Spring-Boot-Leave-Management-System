package com.example.Leave.Management.controllers;

import com.example.Leave.Management.dtos.*;
import com.example.Leave.Management.mappers.LeaveMapper;
import com.example.Leave.Management.repositories.LeaveDayTypeRepository;
import com.example.Leave.Management.repositories.LeaveTypeRepository;
import com.example.Leave.Management.repositories.LeavesRepository;
import com.example.Leave.Management.repositories.UserRepository;
import com.example.Leave.Management.services.LeaveService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/leave")
public class LeaveController {
    private final LeavesRepository leavesRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveDayTypeRepository leaveDayTypeRepository;
    private final LeaveMapper leaveMapper;

    private final LeaveService leaveService;
    private final UserRepository userRepository;
    @PostMapping
    public ResponseEntity<?> createLeaveDayType(@Valid @RequestBody RegisterLeaveRequest request , UriComponentsBuilder uriBuilder){
        var response = leaveService.createLeave(request);
        var uri = uriBuilder.path("/leave-day-type/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public Iterable<LeaveDto> getMyLeaves(){
        return leaveService.getMyLeaves();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveDto> getLeaveById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateLeave(@RequestBody UpdateLeaveRequest request , @PathVariable(name="id") Long id){
        return ResponseEntity.ok(leaveService.updateLeave(request, id));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<LeaveDto> deleteLeave(@PathVariable(name="id") Long id){
        leaveService.deleteLeave(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("cancel/{id}")
    public ResponseEntity<LeaveDto> cancelLeave(@PathVariable(name="id") Long id){
        leaveService.cancelLeave(id);
        return ResponseEntity.noContent().build();
    }


}

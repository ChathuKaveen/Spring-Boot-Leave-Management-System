package com.example.Leave.Management.controllers;

import com.example.Leave.Management.dtos.*;
import com.example.Leave.Management.entities.DayType;
import com.example.Leave.Management.entities.LeaveDayType;
import com.example.Leave.Management.entities.Leaves;
import com.example.Leave.Management.entities.Status;
import com.example.Leave.Management.exceptions.FromDateToDateException;
import com.example.Leave.Management.exceptions.UserNotFoundException;
import com.example.Leave.Management.mappers.LeaveMapper;
import com.example.Leave.Management.repositories.LeaveDayTypeRepository;
import com.example.Leave.Management.repositories.LeaveTypeRepository;
import com.example.Leave.Management.repositories.LeavesRepository;
import com.example.Leave.Management.repositories.UserRepository;
import com.example.Leave.Management.services.LeaveService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/leave")
public class LeaveController {
    private final LeavesRepository leavesRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveDayTypeRepository leaveDayTypeRepository;
    private final LeaveMapper leaveMapper;
    private final UserRepository userRepository;
    private final LeaveService leaveService;

    @PostMapping
    public ResponseEntity<?> createLeaveDayType(@Valid @RequestBody RegisterLeaveRequest request , UriComponentsBuilder uriBuilder){
        Leaves leave = new Leaves();

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId =(Long) authentication.getPrincipal();
        System.out.println(userId);
        var user = userRepository.findById(userId).orElse(null);
        leave.setUser(user);


        var leaveTypes = leaveTypeRepository.findById(request.getLeaveType()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND  , "This leave type not found"));
        leave.setLeaveTypes(leaveTypes);
        leave.setReason(request.getReason());

        leave.setStatus(Status.PENDING);

        var leaveFromDayTypeFinder = leaveDayTypeRepository.findById(request.getFrom_date_type()).orElse(null);

        if(leaveFromDayTypeFinder == null ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message" , "Leave day type not found"));
        }
        leave.setFrom_date_type(leaveFromDayTypeFinder);
        leave.setTo_date_type(leaveDayTypeRepository.findIdByType(DayType.FULL_DAY));

        //from to dates
        var fromDate = request.getFrom_date();
        var toDate = request.getTo_date()!= null ? request.getTo_date() : fromDate;
        if(fromDate.isAfter(toDate)){
            throw new FromDateToDateException();
        }
        if((leaveFromDayTypeFinder.getType() == DayType.HALF_DAY_MORNING || leaveFromDayTypeFinder.getType() == DayType.HALF_DAY_EVENING) && !fromDate.isEqual(toDate)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message" , "HALF DAY MORNING leave must be for a single day"));
        }

        leave.setFrom_date(fromDate);
        leave.setTo_date(toDate);

        leave.setDays(leaveService.calLeaveDays(fromDate ,toDate , leaveFromDayTypeFinder ));//, leaveToDayTypeFinder
        var saved = leavesRepository.save(leave);
        var response = leaveMapper.toDto(saved);
        var uri = uriBuilder.path("/leave-day-type/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }



    @GetMapping
    public Iterable<LeaveDto> getLeaves(){
        return leavesRepository.findAll().stream().map(leaveMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveDto> getLeaveById(@PathVariable(name = "id") Long id){
        var type = leavesRepository.findById(id).orElseThrow(null);
        if(type == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(leaveMapper.toDto(type));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateLeave(@RequestBody UpdateLeaveRequest request , @PathVariable(name="id") Long id){
        var leave = leavesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Leave not found"));

        if (request.getLeaveType() != null) {
            var leaveType = leaveTypeRepository
                    .findById(request.getLeaveType())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Leave type not found"));
            leave.setLeaveTypes(leaveType);
        }

        if (request.getFrom_date() != null) {
            leave.setFrom_date(request.getFrom_date());
        }

        if (request.getTo_date() != null) {
            leave.setTo_date(request.getTo_date());
        }
        if (leave.getTo_date().isBefore(leave.getFrom_date())) {
            throw new FromDateToDateException();
        }

        if (request.getFrom_date_type() != null) {
            var dayType = leaveDayTypeRepository
                    .findById(request.getFrom_date_type())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Leave day type not found"));
            leave.setFrom_date_type(dayType);
        }

        if(request.getFrom_date() != null || request.getFrom_date_type() != null){
            leave.setDays(leaveService.calLeaveDays(
                                            request.getFrom_date() ,
                                            leave.getTo_date() ,
                                            leaveDayTypeRepository
                                                    .findById(request.getFrom_date_type())
                                                    .orElseThrow(() -> new ResponseStatusException(
                                                            HttpStatus.NOT_FOUND, "Leave day type not found")))
            );
        }


        if (request.getReason() != null) {
            leave.setReason(request.getReason());
        }

        leavesRepository.save(leave);
        return ResponseEntity.ok(leaveMapper.toDto(leave));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> deleteLeave(@PathVariable(name="id") Long id){
        var u  = leavesRepository.findById(id).orElseThrow();
        if(u == null){
            throw  new UserNotFoundException();
        }
        leavesRepository.delete(u);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(FromDateToDateException.class)
    public ResponseEntity<Map<String , String>> handleUserNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "To Date cant be before From Date"));
    }
}

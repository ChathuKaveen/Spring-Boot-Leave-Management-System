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

        var leaveFromDayTypeFinder = leaveDayTypeRepository.findById(request.getFrom_date_type()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND  , "This leave day type not found"));
        var leaveToDayTypeFinder = leaveDayTypeRepository.findById(request.getTo_date_type()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND  , "This leave day type not found"));
        leave.setFrom_date_type(leaveFromDayTypeFinder);
        leave.setTo_date_type(leaveToDayTypeFinder);

        //from to dates
        var fromDate = request.getFrom_date();
        var toDate = request.getTo_date()!= null ? request.getTo_date() : fromDate;
        if(fromDate.isAfter(toDate)){
            throw new FromDateToDateException();
        }
        if(leaveFromDayTypeFinder.getType() == DayType.HALF_DAY_MORNING && !fromDate.isEqual(toDate)){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "HALF DAY MORNING leave must be for a single day"
            );
        }

        if (leaveToDayTypeFinder.getType() == DayType.HALF_DAY_EVENING
                && !fromDate.isEqual(toDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "HALF DAY EVENING leave must be for a single day"
            );
        }
        leave.setFrom_date(fromDate);
        leave.setTo_date(toDate);

        leave.setDays(calLeaveDays(fromDate ,toDate , leaveFromDayTypeFinder , leaveToDayTypeFinder));

        var saved = leavesRepository.save(leave);
        var response = leaveMapper.toDto(saved);
        var uri = uriBuilder.path("/leave-day-type/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    private double calLeaveDays(LocalDate from , LocalDate to , LeaveDayType leaveFromDayTypeFinder , LeaveDayType leaveToDayTypeFinder){
        var fromDayType = leaveFromDayTypeFinder.getType();
        var toDayType = leaveToDayTypeFinder.getType();

        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        long totalDays = ChronoUnit.DAYS.between(from, to) + 1;

        if (totalDays > 1) {
            if (fromDayType != DayType.FULL_DAY || toDayType != DayType.FULL_DAY) {
                throw new IllegalArgumentException(
                        "Half-day leave is allowed only for a single day"
                );
            }
            return totalDays;
        }
        if (fromDayType == DayType.FULL_DAY && toDayType == DayType.FULL_DAY) {
            return 1.0;
        }
        if (fromDayType == DayType.HALF_DAY_MORNING
                && toDayType == DayType.HALF_DAY_EVENING) {
            return 1.0;
        }
        if (fromDayType == DayType.HALF_DAY_MORNING
                || toDayType == DayType.HALF_DAY_EVENING) {
            return 0.5;
        }
        throw new IllegalArgumentException("Invalid leave day combination");
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

    @ExceptionHandler(FromDateToDateException.class)
    public ResponseEntity<Map<String , String>> handleUserNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "Your Leave End date can't be before Leave beginning Day"));
    }
}

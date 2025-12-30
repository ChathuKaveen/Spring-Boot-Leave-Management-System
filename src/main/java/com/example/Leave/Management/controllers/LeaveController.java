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

        var leaveFromDayTypeFinder = leaveDayTypeRepository.findById(request.getFrom_date_type()).orElse(null);
        //var leaveToDayTypeFinder = leaveDayTypeRepository.findById(request.getTo_date_type()).orElse(null);
        if(leaveFromDayTypeFinder == null ){//|| leaveToDayTypeFinder == null
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

//        if (leaveToDayTypeFinder.getType() == DayType.HALF_DAY_EVENING
//                && !fromDate.isEqual(toDate)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message" , "HALF DAY EVENING leave must be for a single day"));
//        }
        leave.setFrom_date(fromDate);
        leave.setTo_date(toDate);

        leave.setDays(calLeaveDays(fromDate ,toDate , leaveFromDayTypeFinder ));//, leaveToDayTypeFinder
        System.out.println(calLeaveDays(fromDate ,toDate , leaveFromDayTypeFinder ));
        var saved = leavesRepository.save(leave);
        var response = leaveMapper.toDto(saved);
        var uri = uriBuilder.path("/leave-day-type/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    private double calLeaveDays(LocalDate from , LocalDate to , LeaveDayType leaveFromDayTypeFinder ){//LeaveDayType leaveToDayTypeFinder
        var fromDayType = leaveFromDayTypeFinder.getType();
//        var toDayType = leaveToDayTypeFinder.getType();

        if(from.isAfter(to)){
            throw new FromDateToDateException();
        }
        long totalDays = ChronoUnit.DAYS.between(from, to) + 1;

        if (totalDays > 1) {
            if (fromDayType != DayType.FULL_DAY ) {//|| toDayType != DayType.FULL_DAY
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Half-day leave is allowed only for a single day"
                );
            }
            return totalDays;
        }
        if (fromDayType == DayType.FULL_DAY ) {//&& toDayType == DayType.FULL_DAY
            return 1.0;
        }
        if (fromDayType == DayType.HALF_DAY_MORNING
               ) {// && toDayType == DayType.HALF_DAY_EVENING
            return 0.5;
        }
        if (fromDayType == DayType.HALF_DAY_EVENING
                ) {//|| toDayType == DayType.HALF_DAY_EVENING
            return 0.5;
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid leave day combination"
        );
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "To Date cant be before From Date"));
    }
}

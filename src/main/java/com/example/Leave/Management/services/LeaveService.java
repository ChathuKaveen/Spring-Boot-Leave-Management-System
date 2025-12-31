package com.example.Leave.Management.services;

import com.example.Leave.Management.dtos.LeaveDto;
import com.example.Leave.Management.dtos.RegisterLeaveRequest;
import com.example.Leave.Management.dtos.UpdateLeaveRequest;
import com.example.Leave.Management.entities.DayType;
import com.example.Leave.Management.entities.LeaveDayType;
import com.example.Leave.Management.entities.Leaves;
import com.example.Leave.Management.entities.Status;
import com.example.Leave.Management.exceptions.*;
import com.example.Leave.Management.mappers.LeaveMapper;
import com.example.Leave.Management.repositories.LeaveDayTypeRepository;
import com.example.Leave.Management.repositories.LeaveTypeRepository;
import com.example.Leave.Management.repositories.LeavesRepository;
import com.example.Leave.Management.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
@AllArgsConstructor
@Service
public class LeaveService {
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveDayTypeRepository leaveDayTypeRepository;
    private final LeavesRepository leavesRepository;
    private final LeaveMapper leaveMapper;

    public double calLeaveDays(LocalDate from , LocalDate to , LeaveDayType leaveFromDayTypeFinder ){
        var fromDayType = leaveFromDayTypeFinder.getType();
        if(from.isAfter(to)){
            throw new FromDateToDateException();
        }
        long totalDays = ChronoUnit.DAYS.between(from, to) + 1;

        if (totalDays > 1) {
            if (fromDayType != DayType.FULL_DAY ) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Half-day leave is allowed only for a single day"
                );
            }
            return totalDays;
        }
        if (fromDayType == DayType.FULL_DAY ) {
            return 1.0;
        }
        if (fromDayType == DayType.HALF_DAY_MORNING
        ) {
            return 0.5;
        }
        if (fromDayType == DayType.HALF_DAY_EVENING
        ) {
            return 0.5;
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid leave day combination"
        );
    }

    public LeaveDto createLeave(RegisterLeaveRequest request){
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
            throw new LeaveTypeNotFoundException();
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
            throw new HalfDayMustBeSeperateDayException();
        }

        leave.setFrom_date(fromDate);
        leave.setTo_date(toDate);

        leave.setDays(calLeaveDays(fromDate ,toDate , leaveFromDayTypeFinder ));//, leaveToDayTypeFinder
        var saved = leavesRepository.save(leave);

        return leaveMapper.toDto(saved);
    }

    public LeaveDto getLeaveById(Long id){
        var type = leavesRepository.findById(id).orElseThrow(null);
        if(type == null){
            throw new LeaveNotFoundException();
        }
        return leaveMapper.toDto(type);
    }

    public LeaveDto updateLeave(UpdateLeaveRequest request, Long id){
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
            leave.setDays(calLeaveDays(
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
        return leaveMapper.toDto(leave);
    }

    public void deleteLeave(Long id){
        var u  = leavesRepository.findById(id).orElse(null);
        if(u == null){
            throw  new UserNotFoundException();
        }
        leavesRepository.delete(u);
    }
}

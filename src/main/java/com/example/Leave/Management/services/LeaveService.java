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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Service
public class LeaveService {
    private static final Logger log = LoggerFactory.getLogger(LeaveService.class);
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveDayTypeRepository leaveDayTypeRepository;
    private final LeavesRepository leavesRepository;
    private final LeaveMapper leaveMapper;

    public double calLeaveDays(LocalDate from , LocalDate to , LeaveDayType leaveFromDayTypeFinder ){
        var fromDayType = leaveFromDayTypeFinder.getType();
        if(from.isAfter(to)){
            log.info("HIT 3");
            throw new FromDateToDateException();
        }
        long totalDays = ChronoUnit.DAYS.between(from, to) + 1;

        if (totalDays > 1) {
            if (fromDayType != DayType.FULL_DAY ) {
                throw new HalfDayMustBeSeperateDayException();
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
        var user = userRepository.findById(userId).orElse(null);
        leave.setUser(user);


        var leaveTypes = leaveTypeRepository.findById(request.getLeaveType()).orElseThrow(LeaveTypeNotFoundException::new);
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
        var overlappingLeaves = leavesRepository.findOverlappingLeaves(userId , fromDate , toDate);
        if(!overlappingLeaves.isEmpty()){
            throw new LeavesOverlappingException();
        }

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
        var type = leavesRepository.findById(id).orElse(null);
        if(type == null){
            throw new LeaveNotFoundException();
        }
        return leaveMapper.toDto(type);
    }

    public Iterable<LeaveDto> getMyLeaves(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId =(Long) authentication.getPrincipal();
        var user = userRepository.findById(userId).orElse(null);
        return leavesRepository.findByUser(user).stream().map(leaveMapper::toDto).toList();
    }

    public LeaveDto updateLeave(UpdateLeaveRequest request, Long id){
        var leave = leavesRepository.findById(id)
                .orElseThrow(LeaveNotFoundException::new);
        if (request.getLeaveType() != null) {
            var leaveType = leaveTypeRepository
                    .findById(request.getLeaveType())
                    .orElseThrow(LeaveTypeNotFoundException::new);
            leave.setLeaveTypes(leaveType);
        }

        var finalFrom_date = request.getFrom_date();
        var finalTo_date = request.getTo_date();

        var exsistingFrom_date = leave.getFrom_date();
        var exsistingTo_date = leave.getTo_date();

        if (finalFrom_date != null && finalTo_date == null) {
            var overlappingLeaves = leavesRepository.findOverlappingLeavesForUpdate(leave.getId(),leave.getUser().getId() , finalFrom_date, exsistingTo_date);
            if(!overlappingLeaves.isEmpty()){
                throw new LeavesOverlappingException();
            }
            if (exsistingTo_date.isBefore(finalFrom_date)) {
                log.info("HIT 1 {} - {}" , exsistingTo_date , finalFrom_date);
                throw new FromDateToDateException();
            }
            if(request.getFrom_date_type() != null){
                leave.setDays(calLeaveDays(
                        finalFrom_date,
                        exsistingTo_date,
                        leaveDayTypeRepository
                                .findById(request.getFrom_date_type())
                                .orElseThrow(LeaveDayTyoNotFoundException::new))
                );
            }
            if(request.getFrom_date_type() == null){
                var type = leave.getFrom_date_type();
                leave.setDays(calLeaveDays(
                        finalFrom_date ,
                        exsistingTo_date,
                        type)
                );
            }
            leave.setFrom_date(finalFrom_date);
        }else if(finalFrom_date == null && finalTo_date != null){
            var overlappingLeaves = leavesRepository.findOverlappingLeavesForUpdate(leave.getId(),leave.getUser().getId() ,exsistingFrom_date, finalTo_date);
            if(!overlappingLeaves.isEmpty()){
                throw new LeavesOverlappingException();
            }
            if (exsistingTo_date.isBefore(exsistingFrom_date)) {
                log.info("HIT 2");
                throw new FromDateToDateException();
            }

            leave.setTo_date(finalTo_date);
        } else if (finalFrom_date != null && finalTo_date != null) {
            var overlappingLeaves = leavesRepository.findOverlappingLeavesForUpdate(leave.getId(),leave.getUser().getId() , finalFrom_date, finalTo_date);
            if(!overlappingLeaves.isEmpty()){
                throw new LeavesOverlappingException();
            }
            if (finalTo_date.isBefore(finalFrom_date)) {
                throw new FromDateToDateException();
            }
            if(request.getFrom_date_type() != null){
                leave.setDays(calLeaveDays(
                        finalFrom_date,
                        finalTo_date,
                        leaveDayTypeRepository
                                .findById(request.getFrom_date_type())
                                .orElseThrow(LeaveDayTyoNotFoundException::new))
                );
            }
            if(request.getFrom_date_type() == null){
                var type = leave.getFrom_date_type();
                leave.setDays(calLeaveDays(
                        finalFrom_date ,
                        finalTo_date,
                        type)
                );
            }
            leave.setFrom_date(finalFrom_date);
            leave.setTo_date(finalTo_date);
        }

//        if (request.getTo_date() != null) {
//            var overlappingLeaves = leavesRepository.findOverlappingLeaves(leave.getUser().getId() , leave.getFrom_date() , request.getTo_date());
//            if(!overlappingLeaves.isEmpty()){
//                throw new LeavesOverlappingException();
//            }
//            if (request.getTo_date().isBefore(leave.getFrom_date())) {
//                throw new FromDateToDateException();
//            }
//            leave.setTo_date(request.getTo_date());
//        }


        if (request.getFrom_date_type() != null) {
            var dayType = leaveDayTypeRepository
                    .findById(request.getFrom_date_type())
                    .orElseThrow(LeaveDayTyoNotFoundException::new);
            leave.setFrom_date_type(dayType);
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
            throw  new LeaveNotFoundException();
        }
        leavesRepository.delete(u);
    }
}

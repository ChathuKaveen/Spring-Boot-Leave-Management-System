package com.example.Leave.Management.services;

import com.example.Leave.Management.dtos.LeavesDtos.LeaveApproveRequest;
import com.example.Leave.Management.dtos.LeavesDtos.LeaveDto;
import com.example.Leave.Management.dtos.LeavesDtos.RegisterLeaveRequest;
import com.example.Leave.Management.dtos.LeavesDtos.UpdateLeaveRequest;
import com.example.Leave.Management.entities.*;
import com.example.Leave.Management.exceptions.*;
import com.example.Leave.Management.mappers.LeaveMapper;
import com.example.Leave.Management.repositories.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class LeaveService {
    private static final Logger log = LoggerFactory.getLogger(LeaveService.class);
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveDayTypeRepository leaveDayTypeRepository;
    private final LeavesRepository leavesRepository;
    private final LeaveMapper leaveMapper;
    private final SupervisorMemberRepository supervisorMemberRepository;
    public double calLeaveDays(LocalDate from , LocalDate to , LeaveDayType leaveFromDayTypeFinder ){
        var fromDayType = leaveFromDayTypeFinder.getType();
        if(from.isAfter(to)){
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
        var today = LocalDate.now();
        var fromDate = request.getFrom_date();
        var toDate = request.getTo_date()!= null ? request.getTo_date() : fromDate;
        if(fromDate.isBefore(today) || toDate.isBefore(today)){
            throw new LeaveDayCantBeforeTodayException("To-Date or From-Date must not be older date");
        }
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
    public Page<Leaves> getAllLeaves(
            int page,
            int size,
            String sort,
            String status,
            String fromDate,
            String toDate
    ){
        PageRequest pageRequest = PageRequest.of(page , size);
        Page<Leaves> leaveList = leavesRepository.findAll(pageRequest);
        return leaveList;
        //return leaveList.stream().map(leaveMapper::toDto).toList();
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
        var today = LocalDate.now();
        var finalFrom_date = request.getFrom_date();
        var finalTo_date = request.getTo_date();

        var exsistingFrom_date = leave.getFrom_date();
        var exsistingTo_date = leave.getTo_date();

        if (finalFrom_date != null && finalTo_date == null) {
            if(finalFrom_date.isBefore(today)){
                throw new LeaveDayCantBeforeTodayException("From-Date must not be older date");
            }
            var overlappingLeaves = leavesRepository.findOverlappingLeavesForUpdate(leave.getId(),leave.getUser().getId() , finalFrom_date, exsistingTo_date);
            if(!overlappingLeaves.isEmpty()){
                throw new LeavesOverlappingException();
            }
            if (exsistingTo_date.isBefore(finalFrom_date)) {
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
            if (finalTo_date.isBefore(exsistingFrom_date)) {
                throw new FromDateToDateException();
            }

            leave.setTo_date(finalTo_date);
        } else if (finalFrom_date != null && finalTo_date != null) {
            if(finalFrom_date.isBefore(today) || finalTo_date.isBefore(today)){
                throw new LeaveDayCantBeforeTodayException("To-Date or From-Date must not be older date");
            }
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

    public void cancelLeave(Long id){
        var leave = leavesRepository.findById(id) .orElseThrow(LeaveNotFoundException::new);
        var exsistingFrom_date = leave.getFrom_date();
        var today = LocalDate.now();
        var leaveStatus = leave.getStatus();

        if (leaveStatus == Status.REJECTED) {
            throw new LeaveDayCantBeforeTodayException("Can't Cancel rejected leaves");
        }
        if (leaveStatus == Status.CANCELLED) {
            throw new LeaveDayCantBeforeTodayException("Leave is already cancelled");
        }
        if (exsistingFrom_date.isBefore(today)) {
            throw new LeaveDayCantBeforeTodayException("Can't Cancel past leaves");
        }
        leave.setStatus(Status.CANCELLED);
        leavesRepository.save(leave);
    }

    public Iterable<LeaveDto> getSubordinatesLeaves(){
        List<Leaves> allLeaves;
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId =(Long) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        var isAdmin = user.getRole();
        if(isAdmin != Role.ADMIN){
            var supervisor = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
            var subordinates = getAllSubordinates(supervisor);
            allLeaves = leavesRepository.findLeavesByUsers(subordinates);
        }else{
            allLeaves = leavesRepository.findAll();
        }

        return allLeaves.stream().map(leaveMapper::toDto).toList();
    }

    private List<User> getAllSubordinates(User supervisor){
        Set<Long> visited = new HashSet<>();
        List<User> result = new ArrayList<>();
        collectSubordinates(supervisor , result , visited);
        return result;
    }

    private void collectSubordinates(User supervisor , List<User> result , Set<Long> visited){
        if (!visited.add(supervisor.getId())) {
            return;
        }
        List<SupervisorMember> directSubs = supervisorMemberRepository.findDirectSubordinates(supervisor);

        for(SupervisorMember sm:directSubs){
            User subordinate = sm.getUser();
            result.add(subordinate);
            collectSubordinates(subordinate , result , visited);
        }
    }

    public void approveRejectLeaves(LeaveApproveRequest request , Long id){
        var leave = leavesRepository.findById(id)
                .orElseThrow(LeaveNotFoundException::new);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId =(Long) authentication.getPrincipal();
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        var leavedUser = leave.getUser();
        var isAdmin = user.getRole();
        if(isAdmin != Role.ADMIN){
            var isSupervisor = supervisorMemberRepository.existsBySupervisorAndUser(user , leavedUser);
            if(!isSupervisor){
                throw new YouAreNotSupervisorException("You are not the supervisor of this member");
            }
        }
        if(leave.getStatus() == Status.CANCELLED){
            throw new YouAreNotSupervisorException("Member Already Cancelled the leave");
        }
        leave.setStatus(request.getStatus());
        leave.setUpdated_by(userId);
        leavesRepository.save(leave);
    }
}

package com.example.Leave.Management.services;

import com.example.Leave.Management.entities.DayType;
import com.example.Leave.Management.entities.LeaveDayType;
import com.example.Leave.Management.exceptions.FromDateToDateException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class LeaveService {
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
}

package com.example.Leave.Management.controllers;

import com.example.Leave.Management.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String , String>> handleValidationExceptions(MethodArgumentNotValidException exception){
        var errors = new HashMap<String,String>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField() , error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(FromDateToDateException.class)
    public ResponseEntity<Map<String , String>> handleUserNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "To Date cant be before From Date"));
    }

    @ExceptionHandler(LeaveTypeNotFoundException.class)
    public ResponseEntity<Map<String , String>> handleLeaveTypeNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "Leave day type not found"));
    }
    @ExceptionHandler(HalfDayMustBeSeperateDayException.class)
    public ResponseEntity<Map<String , String>> handleHalfDay(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "HALF DAY leave must be for a single day"));
    }

    @ExceptionHandler(LeaveNotFoundException.class)
    public ResponseEntity<Map<String , String>> leaveNotFoundExceptionHandler(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "Leave not found"));
    }

    @ExceptionHandler(LeaveDayTyoNotFoundException.class)
    public ResponseEntity<Map<String , String>> leaveDayTypeNotFoundExceptionHandler(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "Leave not found"));
    }

    @ExceptionHandler(LeavesOverlappingException.class)
    public ResponseEntity<Map<String , String>> leavesOverlapping(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error" , "You already have some leaves that days range"));
    }
}

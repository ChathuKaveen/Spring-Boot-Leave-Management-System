package com.example.Leave.Management.exceptions;

public class LeaveDayCantBeforeTodayException extends RuntimeException{
    public LeaveDayCantBeforeTodayException(String message){
        super(message);
    }
}

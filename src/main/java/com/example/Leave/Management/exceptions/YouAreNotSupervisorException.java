package com.example.Leave.Management.exceptions;

public class YouAreNotSupervisorException extends RuntimeException{
    public YouAreNotSupervisorException (String message){
        super(message);
    }
}

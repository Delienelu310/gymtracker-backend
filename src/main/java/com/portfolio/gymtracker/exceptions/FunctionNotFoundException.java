package com.portfolio.gymtracker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND)
public class FunctionNotFoundException extends RuntimeException{
    public FunctionNotFoundException(String message){
        super(message);
    }
}

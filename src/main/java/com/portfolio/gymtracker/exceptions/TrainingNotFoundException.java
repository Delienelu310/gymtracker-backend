package com.portfolio.gymtracker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND)
public class TrainingNotFoundException extends RuntimeException{
    
    public TrainingNotFoundException(String message){
        super(message);
    }

}

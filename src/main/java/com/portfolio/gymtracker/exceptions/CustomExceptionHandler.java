package com.portfolio.gymtracker.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{
    

    // @ExceptionHandler(Exception.class)
    // public final ResponseEntity<ErrorDetails> handleUnpredictedExceptions(Exception ex, WebRequest request){
    //     ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    
    //     return new ResponseEntity<ErrorDetails>(errorDetails, null, HttpStatus.INTERNAL_SERVER_ERROR);
    // }

    @ExceptionHandler({UserNotFoundException.class, ExerciseNotFoundException.class, FunctionNotFoundException.class, TrainingNotFoundException.class })
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request){
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
    
        return new ResponseEntity<ErrorDetails>(errorDetails, null, HttpStatus.NOT_FOUND);
    
    }

    // @ExceptionHandler(ExerciseNotFoundException.class)
    // public final ResponseEntity<ErrorDetails> handleExerciseNotFoundException(Exception ex, WebRequest request){
    //     return null;
    // }

    // @ExceptionHandler(FunctionNotFoundException.class)
    // public final ResponseEntity<ErrorDetails> handleFunctionNotFoundException(Exception ex, WebRequest request){
    //     return null;
    // }

    // @ExceptionHandler(TrainingNotFoundException.class)
    // public final ResponseEntity<ErrorDetails> handleTrainingNotFoundException(Exception ex, WebRequest request){
    //     return null;
    // }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
            "Total Errors:" + ex.getErrorCount() + " First Error:" + ex.getFieldError().getDefaultMessage(), 
            request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}

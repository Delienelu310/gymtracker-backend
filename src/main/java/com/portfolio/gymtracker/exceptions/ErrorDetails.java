package com.portfolio.gymtracker.exceptions;

import java.time.LocalDateTime;

public class ErrorDetails {
    
    private LocalDateTime dateTime;
    private String message;
    private String description;
    public ErrorDetails(LocalDateTime dateTime, String message, String description) {
        this.dateTime = dateTime;
        this.message = message;
        this.description = description;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    
}

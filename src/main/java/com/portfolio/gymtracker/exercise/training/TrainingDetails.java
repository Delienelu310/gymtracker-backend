package com.portfolio.gymtracker.exercise.training;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PastOrPresent;

@Embeddable
public class TrainingDetails {

    @PastOrPresent
    private LocalDateTime dateTime;

    public TrainingDetails(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public TrainingDetails() {
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    
    
}

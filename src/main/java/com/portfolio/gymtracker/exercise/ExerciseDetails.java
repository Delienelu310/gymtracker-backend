package com.portfolio.gymtracker.exercise;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Embeddable
public class ExerciseDetails {

    @Size(min=3, max=30)
    @NotNull
    private String title;
    @Size(max=300)
    private String description;

    public ExerciseDetails(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public ExerciseDetails() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
}

package com.portfolio.gymtracker.exercise;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Embeddable
public class ExerciseDetails {

    @Size(min=3, max=30)
    @NotNull
    private String title;
    @Size(max=300)
    private String description;

    //base64 image string
    @Column(columnDefinition = "TEXT")
    private String image;

    public ExerciseDetails(String title, String description, String image) {
        this.title = title;
        this.description = description;
        this.image = image;
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
    
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    
}

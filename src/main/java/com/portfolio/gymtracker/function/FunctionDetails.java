package com.portfolio.gymtracker.function;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Embeddable
public class FunctionDetails {

    @Size(min=3, max=30)
    @NotNull
    private String title;
    @Size(max = 300)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public FunctionDetails(String title, String description, String image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public FunctionDetails() {
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

package com.portfolio.gymtracker.function;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Embeddable
public class FunctionGroupDetails {
    
    @NotNull
    @Size(min = 3, max = 30)
    private String title;

    @Size(max = 300)
    private String description;

    public FunctionGroupDetails(@NotNull @Size(min = 3, max = 30) String title, @Size(max = 300) String description) {
        this.title = title;
        this.description = description;
    }

    public FunctionGroupDetails() {
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

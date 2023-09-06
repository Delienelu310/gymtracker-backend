package com.portfolio.gymtracker.function;

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

    public FunctionDetails(String title, String description) {
        this.title = title;
        this.description = description;
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

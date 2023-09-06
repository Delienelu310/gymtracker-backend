package com.portfolio.gymtracker.exercise.training;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Take {
    
    @Min(1)
    @Max(1000)
    @NotNull
    private int repeats;
    @NotNull
    @Min(1)
    @Max(1000)
    private int level;

    public Take(int repeats, int level) {
        this.repeats = repeats;
        this.level = level;
    }

    public Take(){

    }

    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    
}

package com.portfolio.gymtracker.exercise.training;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.portfolio.gymtracker.exercise.Exercise;
import com.portfolio.gymtracker.user.AppUser;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

@Entity
@JsonFilter("TrainingFilter")
public class Training {

    @Id
    // @Null
    private long trainingId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JsonFilter("UserFilter")
    // @Null
    private AppUser user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonFilter("ExerciseFilter")
    // @Null
    private Exercise exercise;

    @ElementCollection
    @Size(min = 1)
    @NotNull
    private List<Take> takes = new ArrayList<>();

    @Embedded
    @JsonFilter("TrainingDetailsFilter")
    @NotNull
    private TrainingDetails trainingDetails;

    public Training(AppUser user, Exercise exercise, TrainingDetails trainingDetails) {
        this.user = user;
        this.exercise = exercise;
        this.trainingDetails = trainingDetails;
    }

    public Training(){

    }

    public long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(long trainingId) {
        this.trainingId = trainingId;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<Take> getTakes() {
        return takes;
    }

    public void setTakes(List<Take> takes) {
        this.takes = takes;
    }

    public TrainingDetails getTrainingDetails() {
        return trainingDetails;
    }

    public void setTrainingDetails(TrainingDetails trainingDetails) {
        this.trainingDetails = trainingDetails;
    }


}

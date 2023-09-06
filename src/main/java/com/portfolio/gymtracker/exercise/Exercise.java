package com.portfolio.gymtracker.exercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.gymtracker.exercise.training.Training;
import com.portfolio.gymtracker.function.Function;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import com.portfolio.gymtracker.user.AppUser;

@Entity
@JsonFilter("ExerciseFilter")
public class Exercise {
    
    @Id
    @GeneratedValue
    private int exerciseId;

    //relations with other entities
    
    @ManyToOne(fetch = FetchType.LAZY)
    // @JsonIgnore
    @JsonFilter("UserFilter")
    private AppUser author;

    private boolean published = false;

    //usually goes between 0.0 and 2.0, where 1.0 is normal
    @ElementCollection
    private Map<Integer, Double> functionPerformance = new HashMap<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonFilter("FunctionFilter")
    private List<Function> functionsIncluded = new ArrayList<>();

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Training> trainingsList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "followedExercises")
    @JsonIgnore
    private List<AppUser> followers = new ArrayList<>();


    @Embedded
    @JsonFilter("ExerciseDetailsFilter")
    private ExerciseDetails exerciseDetails;



    public Exercise(AppUser author, ExerciseDetails exerciseDetails, boolean published) {
        this.author = author;
        this.exerciseDetails = exerciseDetails;
        this.published = published;
    }


    //constructors, getters and setters
    public Exercise(){

    }
    
    public List<AppUser> getFollowers() {
        return followers;
    }

    public void setFollowers(List<AppUser> followers) {
        this.followers = followers;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public AppUser getAuthor() {
        return author;
    }

    public void setAuthor(AppUser author) {
        this.author = author;
    }

    public Map<Integer, Double> getFunctionPerformance() {
        return functionPerformance;
    }

    public void setFunctionPerformance(Map<Integer, Double> functionPerformance) {
        this.functionPerformance = functionPerformance;
    }

    public List<Function> getFunctionsIncluded() {
        return functionsIncluded;
    }

    public void setFunctionsIncluded(List<Function> functionsIncluded) {
        this.functionsIncluded = functionsIncluded;
    }

    public List<Training> getTrainingsList() {
        return trainingsList;
    }

    public void setTrainingsList(List<Training> trainingsList) {
        this.trainingsList = trainingsList;
    }

    public ExerciseDetails getExerciseDetails() {
        return exerciseDetails;
    }

    public void setExerciseDetails(ExerciseDetails exerciseDetails) {
        this.exerciseDetails = exerciseDetails;
    }

    public boolean isPublished() {
        return published;
    }


    public void setPublished(boolean publised) {
        this.published = publised;
    }

 
}

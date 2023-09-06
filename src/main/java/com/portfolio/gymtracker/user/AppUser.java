package com.portfolio.gymtracker.user;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.gymtracker.exercise.Exercise;
import com.portfolio.gymtracker.exercise.training.Training;
import com.portfolio.gymtracker.function.Function;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;


@Entity
// @JsonFilter("UserFilter")
public class AppUser {

    @Id
    @GeneratedValue
    private int userId;

    //for making identifiers
    private int trainingsCount = 0;

    //relations with other entities

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Exercise> createdExercises = new ArrayList<>();


    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Function> createdFunctions = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Training> trainingsList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Exercise> followedExercises = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Function> followedFunctions = new ArrayList<>();

    //entity properties

    @Embedded
    @JsonFilter("UserDetailsFilter")
    private AppUserDetails appUserDetails;

    public AppUser(AppUserDetails appUserDetails) {
        this.appUserDetails = appUserDetails;
    }

    //construcotr, getters, setters etc

    public AppUser() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTrainingsCount() {
        return trainingsCount;
    }

    public void setTrainingsCount(int trainingsCount) {
        this.trainingsCount = trainingsCount;
    }

    public List<Exercise> getCreatedExercises() {
        return createdExercises;
    }

    public void setCreatedExercises(List<Exercise> createdExercises) {
        this.createdExercises = createdExercises;
    }

    public List<Function> getCreatedFunctions() {
        return createdFunctions;
    }

    public void setCreatedFunctions(List<Function> createdFunctions) {
        this.createdFunctions = createdFunctions;
    }

    public List<Training> getTrainingsList() {
        return trainingsList;
    }

    public void setTrainingsList(List<Training> trainingsList) {
        this.trainingsList = trainingsList;
    }

    public List<Exercise> getFollowedExercises() {
        return followedExercises;
    }

    public void setFollowedExercises(List<Exercise> followedExercises) {
        this.followedExercises = followedExercises;
    }

    public List<Function> getFollowedFunctions() {
        return followedFunctions;
    }

    public void setFollowedFunctions(List<Function> followedFunctions) {
        this.followedFunctions = followedFunctions;
    }

    public AppUserDetails getAppUserDetails() {
        return appUserDetails;
    }

    public void setAppUserDetails(AppUserDetails appUserDetails) {
        this.appUserDetails = appUserDetails;
    }
}

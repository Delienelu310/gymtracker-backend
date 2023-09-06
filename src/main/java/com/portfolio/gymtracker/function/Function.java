package com.portfolio.gymtracker.function;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.gymtracker.exercise.Exercise;
import com.portfolio.gymtracker.user.AppUser;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
@JsonFilter("FunctionFilter")
public class Function {

    @Id
    @GeneratedValue
    private int functionId;

    //realtions with other entities

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonFilter("UserFilter")
    private AppUser author;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "followedFunctions")
    @JsonIgnore
    private List<AppUser> followers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "functionsIncluded")
    @JsonIgnore
    private List<Exercise> exercises = new ArrayList<>();


    //private properties
    private boolean published = false;

    @Embedded
    @JsonFilter("FunctionDetailsFilter")
    private FunctionDetails functionDetails;

    //constructors, getters and setters

    public Function(AppUser author, boolean published, FunctionDetails functionDetails) {
        this.author = author;
        this.published = published;
        this.functionDetails = functionDetails;
    }

    public Function(){

    }

    public int getFunctionId() {
        return functionId;
    }

    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public AppUser getAuthor() {
        return author;
    }

    public void setAuthor(AppUser author) {
        this.author = author;
    }

    public List<AppUser> getFollowers() {
        return followers;
    }

    public void setFollowers(List<AppUser> followers) {
        this.followers = followers;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public FunctionDetails getFunctionDetails() {
        return functionDetails;
    }

    public void setFunctionDetails(FunctionDetails functionDetails) {
        this.functionDetails = functionDetails;
    }

    
}

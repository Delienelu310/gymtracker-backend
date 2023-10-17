package com.portfolio.gymtracker.user;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.gymtracker.exercise.Exercise;
import com.portfolio.gymtracker.exercise.training.Training;
import com.portfolio.gymtracker.function.Function;
import com.portfolio.gymtracker.function.FunctionGroup;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@JsonFilter("UserFilter")
@Data
@NoArgsConstructor
public class AppUser {

    //data about the user

    @Id
    @GeneratedValue
    private int userId;

    //for making identifiers
    private int trainingsCount = 0;

    private boolean published;
    
    @Embedded
    @JsonFilter("UserDetailsFilter")
    private AppUserDetails appUserDetails;


    //created data by user

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Exercise> createdExercises = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Function> createdFunctions = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FunctionGroup> createdFunctionGroups = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Training> trainingsList = new ArrayList<>();


    //follows data

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Exercise> followedExercises = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Function> followedFunctions = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<FunctionGroup> followedFunctionGroups = new ArrayList<>();

}

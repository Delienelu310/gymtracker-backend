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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "func")
@Data
@NoArgsConstructor
@JsonFilter("FunctionFilter")
public class Function {

    @Id
    @GeneratedValue
    private int functionId;
    
    private boolean published = false;

    @Embedded
    @JsonFilter("FunctionDetailsFilter")
    private FunctionDetails functionDetails;

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


    
    
}

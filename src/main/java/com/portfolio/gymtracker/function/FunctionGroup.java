package com.portfolio.gymtracker.function;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.gymtracker.user.AppUser;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@JsonFilter("FunctionGroupFilter")
public class FunctionGroup {

    @Id
    @GeneratedValue
    private Long functionGroupId;
    
    private boolean published = false;

    @Embedded
    @NotNull
    @JsonFilter("FunctionGroupDetailsFilter")
    private FunctionGroupDetails functionGroupDetails;

    @ManyToMany(mappedBy = "functionGroups")
    @JsonFilter("GroupFunctionsFilter")
    private List<Function> functions = new ArrayList<>();

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JsonFilter("FunctionGroupAuthor")
    private AppUser author; 

    @ManyToMany
    @JsonIgnore
    private List<AppUser> followers = new ArrayList<>();

}

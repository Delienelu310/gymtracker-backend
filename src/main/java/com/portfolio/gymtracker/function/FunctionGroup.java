package com.portfolio.gymtracker.function;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;

@Entity
@JsonFilter("FunctionGroupFilter")
public class FunctionGroup {

    @Id
    @GeneratedValue
    private Long functionGroupId;

    @Embedded
    @NotNull
    @JsonFilter("FunctionGroupDetailsFilter")
    private FunctionGroupDetails functionGroupDetails;

    @ManyToMany
    @JsonFilter("GroupFunctionsFilter")
    private List<Function> functions = new ArrayList<>();

    private boolean published = false;
    
    
    public FunctionGroup(Long functionGroupId, @NotNull FunctionGroupDetails functionGroupDetails,
            List<Function> functions, boolean published) {
        this.functionGroupId = functionGroupId;
        this.functionGroupDetails = functionGroupDetails;
        this.functions = functions;
        this.published = published;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

 

    public FunctionGroup() {

    }

    public Long getFunctionGroupId() {
        return functionGroupId;
    }

    public void setFunctionGroupId(Long functionGroupId) {
        this.functionGroupId = functionGroupId;
    }

    public FunctionGroupDetails getFunctionGroupDetails() {
        return functionGroupDetails;
    }

    public void setFunctionGroupDetails(FunctionGroupDetails functionGroupDetails) {
        this.functionGroupDetails = functionGroupDetails;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    
}

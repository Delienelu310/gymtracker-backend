package com.portfolio.gymtracker.function;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
@JsonFilter("FunctionGroupFilter")
public class FunctionGroup {
    
    

    @Id
    @GeneratedValue
    private Long functionGroupId;

    @Embedded
    @JsonFilter("FunctionGroupDetailsFilter")
    private FunctionGroupDetails functionGroupDetails;

    @ManyToMany
    @JsonFilter("GroupFunctionsFilter")
    List<Function> functions;
    
    
    public FunctionGroup(Long functionGroupId, FunctionGroupDetails functionGroupDetails, List<Function> functions) {
        this.functionGroupId = functionGroupId;
        this.functionGroupDetails = functionGroupDetails;
        this.functions = functions;
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

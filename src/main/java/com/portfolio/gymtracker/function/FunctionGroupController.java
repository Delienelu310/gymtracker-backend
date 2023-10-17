package com.portfolio.gymtracker.function;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.gymtracker.JacksonMappers.NormalMapper;
import com.portfolio.gymtracker.user.AppUser;
import com.portfolio.gymtracker.user.UserJpaRepository;

import jakarta.validation.Valid;

@RestController
public class FunctionGroupController {

    private void validatePathParameters(Optional<AppUser> user, Optional<Function> function, Optional<FunctionGroup> functionGroup){
 
        if(user != null && user.isEmpty()){
            throw new RuntimeException("User with give user id does not exist");
        }

        if(function != null && function.isEmpty()){
            throw new RuntimeException("The function with given id is missing");
        }

        if(functionGroup != null && functionGroup.isEmpty()){
            throw new RuntimeException("The function group with id given was not found");
        }
        
    }
    
    @Autowired
    private FunctionGroupJpaRepository functionGroupJpaRepository;
    @Autowired
    private FunctionJpaRepository functionJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @GetMapping("/public/functiongroups")
    public MappingJacksonValue getPublicFunctionGroups(){

        return null;
    }

    @GetMapping("/public/functiongroups/{functiongroup_id}")
    public MappingJacksonValue getPublicFunctionGroupById(@PathVariable("functiongroup_id") Long functionGroupId){

        return null;
    }


    @GetMapping("users/{user_id}/functiongroups/{functiongroup_id}")
    public MappingJacksonValue getFunctionGroupById(@PathVariable("user_id") int userId, @PathVariable("functiongroup_id") Long functionGroupId){
        Optional<FunctionGroup> functionGroup =  functionGroupJpaRepository.findById(functionGroupId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(user, null, functionGroup);
        
        //return mapped value
        return null;
    }

    @GetMapping("users/{user_id}/functiongroups")
    public MappingJacksonValue getFunctionGroups(){

        return null;
    }

    @DeleteMapping("users/{user_id}/functiongroups/functiongroup_id")
    public void deleteFunctionGroupById(@PathVariable("user_id") int userId, @PathVariable("functiongroup_id") Long functionGroupId){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(user, null, functionGroup);

        functionGroupJpaRepository.deleteById(functionGroupId);
    }

    @PostMapping("/users/{user_id}/functiongroups")
    public void createFunctionGroup(@PathVariable("user_id") int userId, @RequestBody FunctionGroupDetails functionGroupDetails){

        Optional<AppUser> user = userJpaRepository.findById(userId);
        validatePathParameters(user, null, null);

        FunctionGroup functionGroup = new FunctionGroup();
        functionGroup.setFunctionGroupDetails(functionGroupDetails);
        
        functionGroupJpaRepository.save(functionGroup);
    }

    @PutMapping("users/{user_id}/functiongroups/{functiongroup_id}")
    public void changeFunctionGroupDetails(@PathVariable("user_id") int userId, @PathVariable("functiongroup_id") Long functionGroupId, @Valid @RequestBody FunctionGroupDetails functionGroupDetails){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(user, null, functionGroup);

        functionGroup.get().setFunctionGroupDetails(functionGroupDetails);

        functionGroupJpaRepository.save(functionGroup.get());
    }

    @PutMapping("users/{user_id}/functiongroups/{functiongroup_id}/add/{function_id}")
    public void addFunctionIntoGroup(@PathVariable("user_id") int userId, @PathVariable("functiongroup_id") Long functionGroupId, @PathVariable("function_id") int functionId){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        Optional<Function> function = functionJpaRepository.findById(functionId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(user, function, functionGroup);

        if(functionGroup.get().getFunctions().stream().anyMatch(func -> func.getFunctionId() == functionId)){
            throw new RuntimeException("The function exists inside of the group already");
        }

        functionGroup.get().getFunctions().add(function.get());
    }

    @PutMapping("users/{user_id}/functiongroups/{functiongroup_id}/remove/{function_id}")
    public void removeFunctionFromGroup(@PathVariable("user_id") int userId, @PathVariable("functiongroup_id") Long functionGroupId, @PathVariable("function_id") int functionId){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        Optional<Function> function = functionJpaRepository.findById(functionId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(user, function, functionGroup);

        if(! functionGroup.get().getFunctions().stream().anyMatch(func -> func.getFunctionId() == functionId)){
            throw new RuntimeException("The function does not exist inside of the group ");
        }

        functionGroup.get().getFunctions().remove(function.get());

    }

    @PutMapping("/users/{user_id}/functiongroups/{functiongroup_id}/publish")
    public void publishFunctionGroup(){

    }

    @PutMapping("/users/{user_id}/functiongroups/{functiongroup_id}/unpublish")
    public void unpublishFunctionGroup(){

    }
}

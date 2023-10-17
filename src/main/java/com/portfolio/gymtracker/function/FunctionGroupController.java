package com.portfolio.gymtracker.function;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import com.portfolio.gymtracker.JacksonMappers.NormalMapper;
import com.portfolio.gymtracker.exceptions.FunctionNotFoundException;
import com.portfolio.gymtracker.user.AppUser;
import com.portfolio.gymtracker.user.UserJpaRepository;

import static com.portfolio.gymtracker.security.AccessChecking.checkIfUserAccessable;
import static com.portfolio.gymtracker.security.AccessChecking.checkIfFunctionAccessable;
import static com.portfolio.gymtracker.security.AccessChecking.checkIfFunctionGroupAccessable;

import jakarta.validation.Valid;

@RestController
public class FunctionGroupController {

    private void validatePathParameters(
        Authentication authentication,
        Optional<AppUser> user, 
        Optional<Function> function, 
        Optional<FunctionGroup> functionGroup
    ){
 
        if(user.isEmpty())
            throw new RuntimeException("User with give user id does not exist");
        else 
            checkIfUserAccessable(authentication, user.get());

        if(function != null){
            if(function.isEmpty())
                throw new RuntimeException("The function with given id is missing");
            else 
                checkIfFunctionAccessable(user.get(), function.get());
        }

        if(functionGroup != null){
            if(functionGroup.isEmpty())
                throw new RuntimeException("The function group with id given was not found");
            else 
                checkIfFunctionGroupAccessable(user.get(), functionGroup.get());
        }
        
    }
    
    @Autowired
    private FunctionGroupJpaRepository functionGroupJpaRepository;
    @Autowired
    private FunctionJpaRepository functionJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private NormalMapper normalMapper;

    @GetMapping("/public/functiongroups")
    public MappingJacksonValue getPublicFunctionGroups(){
        return normalMapper.mapFunctionGroupList(functionGroupJpaRepository.findAllByPublished());
    }

    @GetMapping("/public/functiongroups/{functiongroup_id}")
    public MappingJacksonValue getPublicFunctionGroupById(@PathVariable("functiongroup_id") Long functionGroupId){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        if(functionGroup.isEmpty()) throw new FunctionNotFoundException("The function group with given id  was not found");
        if(!functionGroup.get().isPublished()) throw new RuntimeException("The function group is not published");

        return normalMapper.mapFunctionGroupDetailed(functionGroup.get());
    }


    @GetMapping("/users/{user_id}/functiongroups/{functiongroup_id}")
    public MappingJacksonValue getFunctionGroupById(
        Authentication authentication,
        @PathVariable("user_id") int userId, 
        @PathVariable("functiongroup_id") Long functionGroupId
    ){
        Optional<FunctionGroup> functionGroup =  functionGroupJpaRepository.findById(functionGroupId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(authentication, user, null, functionGroup);
        
        return normalMapper.mapFunctionGroupDetailed(functionGroup.get());
    }

    @GetMapping("/users/{user_id}/functiongroups/created")
    public MappingJacksonValue getFunctionGroups(Authentication authentication, @PathVariable("user_id") int userId){
    
        Optional<AppUser> user = userJpaRepository.findById(userId);
        validatePathParameters(authentication, user, null, null);

        return normalMapper.mapFunctionGroupList(user.get().getCreatedFunctionGroups());
    }

    @GetMapping("/users/{user_id}/functiongroups/followed")
    public MappingJacksonValue getFunctionGroupsFollowed(Authentication authentication, @PathVariable("user_id") int userId){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        validatePathParameters(authentication, user, null, null);

        return normalMapper.mapFunctionGroupList(user.get().getFollowedFunctionGroups());

    }

    @GetMapping("/users/{user_id}/functiongroups")
    public MappingJacksonValue getFunctionGroupsAll(Authentication authentication, @PathVariable("user_id") int userId){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        validatePathParameters(authentication, user, null, null);

        List<FunctionGroup> result = new ArrayList<>();

        result.addAll(user.get().getCreatedFunctionGroups());
        result.addAll(user.get().getFollowedFunctionGroups());

        return normalMapper.mapFunctionGroupList(result);

    }

    @DeleteMapping("/users/{user_id}/functiongroups/functiongroup_id")
    public void deleteFunctionGroupById(
        Authentication authentication, 
        @PathVariable("user_id") int userId, 
        @PathVariable("functiongroup_id") Long functionGroupId
    ){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(authentication, user, null, functionGroup);

        functionGroupJpaRepository.deleteById(functionGroupId);
    }

    @PostMapping("/users/{user_id}/functiongroups")
    public void createFunctionGroup(
        Authentication authentication,
        @PathVariable("user_id") int userId, 
        @Valid @RequestBody FunctionGroupDetails functionGroupDetails
    ){

        Optional<AppUser> user = userJpaRepository.findById(userId);
        validatePathParameters(authentication, user, null, null);

        FunctionGroup functionGroup = new FunctionGroup();

        functionGroup.setFunctionGroupDetails(functionGroupDetails);
        functionGroup.setAuthor(user.get());
        
        functionGroupJpaRepository.save(functionGroup);
    }

    @PutMapping("users/{user_id}/functiongroups/{functiongroup_id}")
    public void changeFunctionGroupDetails(
        Authentication authentication,
        @PathVariable("user_id") int userId, 
        @PathVariable("functiongroup_id") Long functionGroupId, 
        @Valid @RequestBody FunctionGroupDetails functionGroupDetails
    ){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(authentication, user, null, functionGroup);

        functionGroup.get().setFunctionGroupDetails(functionGroupDetails);

        functionGroupJpaRepository.save(functionGroup.get());
    }

    @PutMapping("users/{user_id}/functiongroups/{functiongroup_id}/add/{function_id}")
    public void addFunctionIntoGroup(
        Authentication authentication,
        @PathVariable("user_id") int userId, 
        @PathVariable("functiongroup_id") Long functionGroupId, 
        @PathVariable("function_id") int functionId
    ){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        Optional<Function> function = functionJpaRepository.findById(functionId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(authentication, user, function, functionGroup);

        if(functionGroup.get().getFunctions().stream().anyMatch(func -> func.getFunctionId() == functionId)){
            throw new RuntimeException("The function exists inside of the group already");
        }

        functionGroup.get().getFunctions().add(function.get());
        functionGroupJpaRepository.save(functionGroup.get());
    }

    @PutMapping("users/{user_id}/functiongroups/{functiongroup_id}/remove/{function_id}")
    public void removeFunctionFromGroup(
        Authentication authentication,
        @PathVariable("user_id") int userId, 
        @PathVariable("functiongroup_id") Long functionGroupId, 
        @PathVariable("function_id") int functionId
    ){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        Optional<Function> function = functionJpaRepository.findById(functionId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(authentication, user, function, functionGroup);

        if(! functionGroup.get().getFunctions().stream().anyMatch(func -> func.getFunctionId() == functionId)){
            throw new RuntimeException("The function does not exist inside of the group ");
        }

        functionGroup.get().getFunctions().remove(function.get());

        functionGroupJpaRepository.save(functionGroup.get());
    }

    @PutMapping("/users/{user_id}/functiongroups/{functiongroup_id}/publish")
    public void publishFunctionGroup(
        Authentication authentication,
        @PathVariable("user_id") int userId,
        @PathVariable("functiongroup_id") Long functionGroupId
    ){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(authentication, user, null, functionGroup);

        if(functionGroup.get().isPublished())
            throw new RuntimeException("The function group is published already");

        functionGroup.get().setPublished(true);

        functionGroupJpaRepository.save(functionGroup.get());
    }

    @PutMapping("/users/{user_id}/functiongroups/{functiongroup_id}/unpublish")
    public void unpublishFunctionGroup(
        Authentication authentication,
        @PathVariable("user_id") int userId,
        @PathVariable("functiongroup_id") Long functionGroupId
    ){
        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        Optional<AppUser> user = userJpaRepository.findById(userId);

        validatePathParameters(authentication, user, null, functionGroup);

        if(!functionGroup.get().isPublished())
            throw new RuntimeException("The function group is unpublished already");

        functionGroup.get().setPublished(false);

        functionGroupJpaRepository.save(functionGroup.get());
    }
}

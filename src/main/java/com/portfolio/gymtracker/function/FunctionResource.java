package com.portfolio.gymtracker.function;

import java.util.List;
import java.util.Optional;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.gymtracker.user.AppUser;
import com.portfolio.gymtracker.JacksonMappers.NormalMapper;
import com.portfolio.gymtracker.exceptions.ExerciseNotFoundException;
import com.portfolio.gymtracker.exceptions.FunctionNotFoundException;
import com.portfolio.gymtracker.exceptions.UserNotFoundException;
import com.portfolio.gymtracker.exercise.Exercise;
import com.portfolio.gymtracker.user.UserJpaRepository;
import static com.portfolio.gymtracker.security.AccessChecking.checkIfUserAccessable;
import static com.portfolio.gymtracker.security.AccessChecking.checkIfExerciseAccessable;
import static com.portfolio.gymtracker.security.AccessChecking.checkIfFunctionAccessable;

import jakarta.validation.Valid;

import com.portfolio.gymtracker.exercise.ExerciseJpaRepository;

@RestController
public class FunctionResource {
    
    private FunctionJpaRepository functionJpaRepository;
    private UserJpaRepository userJpaRepository;
    private ExerciseJpaRepository exerciseJpaRepository;
    private FunctionGroupJpaRepository functionGroupJpaRepository;
    private NormalMapper normalMapper;

    public FunctionResource(FunctionJpaRepository functionJpaRepository, 
        UserJpaRepository userJpaRepository, ExerciseJpaRepository exerciseJpaRepository,
        FunctionGroupJpaRepository functionGroupJpaRepository,
        NormalMapper normalMapper
    ){
        this.functionJpaRepository = functionJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.exerciseJpaRepository = exerciseJpaRepository;
        this.functionGroupJpaRepository = functionGroupJpaRepository;
        this.normalMapper = normalMapper;
    }

    //get all published functions
    @GetMapping("/public/functions")
    public MappingJacksonValue getPublishedFunctions(){
        return normalMapper.mapFunctionList(functionJpaRepository.findAllByPublished());
    }

    @GetMapping("/public/functions/{function_id}")
    public MappingJacksonValue getPublishedFunctionById(@PathVariable("function_id") int functionId){
        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("The function with id " + functionId + " was not found");
        if(!function.get().isPublished()) throw new RuntimeException("The function is not published");

        return normalMapper.mapFunctionDetailed(function.get());
    }

    @GetMapping("/users/{user_id}/functions/title/{title}")
    public MappingJacksonValue getCreatedFunctionByTitle(
        Authentication authentication, 
        @PathVariable("user_id") int userId, 
        @PathVariable("title") String title
    ){
        Optional<Function> function = functionJpaRepository.findByTitle(title);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with given title");

        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("User was not found");

        checkIfUserAccessable(authentication, user.get());
        checkIfFunctionAccessable(user.get(), function.get());

        return normalMapper.mapFunctionDetailed(function.get());
    }

    //get function created by the user
    @GetMapping("/users/{user_id}/functions/created")
    public MappingJacksonValue getCreatedFunctionsList(Authentication authentication, @PathVariable("user_id") int userId){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("User was not found");
        
        checkIfUserAccessable(authentication, user.get());
        
        return normalMapper.mapFunctionList(userJpaRepository.findById(userId).get().getCreatedFunctions());
    }

    //get functions, that user is followed on
    @GetMapping("/users/{user_id}/functions/followed")
    public MappingJacksonValue getFollowedFunctionsList(Authentication authentication, @PathVariable("user_id") int userId){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("User was not found");
        
        checkIfUserAccessable(authentication, user.get());

        return normalMapper.mapFunctionList(userJpaRepository.findById(userId).get().getFollowedFunctions());
    }

    //get all functions of the user
    @GetMapping("/users/{user_id}/functions")
    public MappingJacksonValue getAllExercisesList(Authentication authentication, @PathVariable("user_id") int userId){

        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("User was not found");
        
        checkIfUserAccessable(authentication, user.get());

        List<Function> functions = user.get().getCreatedFunctions();
        functions.addAll(user.get().getFollowedFunctions());

        return normalMapper.mapFunctionList(functions);
    }

    //get function details for the current user
    @GetMapping("/users/{user_id}/functions/{function_id}")
    public MappingJacksonValue getFunctionById(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("function_id") int functionId
    ){

        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("User was not found");

        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id" + functionId);

        checkIfUserAccessable(authentication, user.get());
        checkIfFunctionAccessable(user.get(), function.get());
    
        return normalMapper.mapFunctionDetailed(function.get());
    }

    //get functions of the exercise
    @GetMapping("/users/{user_id}/functions/exercise/{exercise_id}")
    public MappingJacksonValue getFunctionsOfTheExercise(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("exercise_id") int exerciseId
    ){

        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("User was not found");

        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no function with id" + exerciseId);

        checkIfUserAccessable(authentication, user.get());
        checkIfExerciseAccessable(user.get(), exercise.get());

        return normalMapper.mapFunctionList(exercise.get().getFunctionsIncluded());
    }

    @DeleteMapping("/users/{user_id}/functions/{function_id}")
    public void deleteFunction(Authentication authentication, @PathVariable("user_id") int userId, @PathVariable("function_id") int functionId){

        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("User was not found");

        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id" + functionId);

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != function.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");

        for(Exercise e : function.get().getExercises()){
            e.getFunctionsIncluded().remove(function.get());
            e.getFunctionPerformance().remove(functionId);
        }

        for(AppUser follower : function.get().getFollowers()){
            follower.getFollowedFunctions().remove(function.get());
        }

        for(FunctionGroup group : function.get().getFunctionGroups()){
            group.getFunctions().remove(function.get());
        }
        
        functionJpaRepository.deleteById(functionId);
    }

    //adding function    
    @PostMapping("/users/{user_id}/functions")
    public void createFunction(Authentication authentication, @PathVariable("user_id") int userId, @Valid @RequestBody FunctionDetails functionDetails){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("User was not found");

        checkIfUserAccessable(authentication, user.get());

        Function function = new Function();
        function.setAuthor(userJpaRepository.findById(userId).get());
        function.setFunctionDetails(functionDetails);
        functionJpaRepository.save(function);
    }

    //changing function basic details
    @PutMapping("/users/{user_id}/functions/{function_id}")
    public void updateFunction(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("function_id") int functionId, @Valid @RequestBody FunctionDetails functionDetails
    ){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("User was not found");

        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id" + functionId);
        
        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != function.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");

        function.get().setFunctionDetails(functionDetails);
        functionJpaRepository.save(function.get());
    }

    @PutMapping("/users/{user_id}/functions/{function_id}/publish")
    @PreAuthorize("hasRole('MODER')")
    public void publishFunction(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("function_id") int functionId
    ){ 
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != function.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");

        function.get().setPublished(true);
        functionJpaRepository.save(function.get());
    }

    @PutMapping("/users/{user_id}/functions/{function_id}/unpublish")
    @PreAuthorize("hasRole('MODER')")
    public void unpublishFunction(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("function_id") int functionId
    ){ 
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != function.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");

        function.get().setPublished(false);
        functionJpaRepository.save(function.get());
    }

    @PutMapping("/users/{user_id}/functions/{function_id}/add/functiongroup/{functiongroup_id}")
    public void addFunctionGroup(
        Authentication authentication, 
        @PathVariable("user_id") int userId, 
        @PathVariable("function_id") int functionId,
        @PathVariable("functiongroup_id") Long functionGroupId 
    ){ 
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        if(functionGroup.isEmpty()) throw new RuntimeException("There`s no function group with given id");

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != function.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");

        if(functionGroup.get().getAuthor().getUserId() != user.get().getUserId())
            if( !functionGroup.get().isPublished())
                throw new RuntimeException("You dont have access to the function group");

        if(function.get().getFunctionGroups().contains(functionGroup.get()))
            throw new RuntimeException("The function is already inside of the group");


        function.get().getFunctionGroups().add(functionGroup.get());
        functionJpaRepository.save(function.get());
    }

    @PutMapping("/users/{user_id}/functions/{function_id}/remove/functiongroup/{functiongroup_id}")
    public void removeFunctionGroup(
        Authentication authentication, 
        @PathVariable("user_id") int userId, 
        @PathVariable("function_id") int functionId,
        @PathVariable("functiongroup_id") Long functionGroupId 
    ){ 
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        Optional<FunctionGroup> functionGroup = functionGroupJpaRepository.findById(functionGroupId);
        if(functionGroup.isEmpty()) throw new RuntimeException("There`s no function group with given id");

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != function.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");

        if(functionGroup.get().getAuthor().getUserId() != user.get().getUserId())
            if( !functionGroup.get().isPublished())
                throw new RuntimeException("You dont have access to the function group");

        if(!function.get().getFunctionGroups().contains(functionGroup.get()))
            throw new RuntimeException("The function is not inside of the group");

        function.get().getFunctionGroups().remove(functionGroup.get());
        functionJpaRepository.save(function.get());
    }


}

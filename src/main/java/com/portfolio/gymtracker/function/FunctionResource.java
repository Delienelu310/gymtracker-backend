package com.portfolio.gymtracker.function;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
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

import jakarta.validation.Valid;

import com.portfolio.gymtracker.exercise.ExerciseJpaRepository;

@RestController
public class FunctionResource {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private FunctionJpaRepository functionJpaRepository;
    private UserJpaRepository userJpaRepository;
    private ExerciseJpaRepository exerciseJpaRepository;
    private NormalMapper normalMapper;

    public FunctionResource(FunctionJpaRepository functionJpaRepository, 
        UserJpaRepository userJpaRepository, ExerciseJpaRepository exerciseJpaRepository,
        NormalMapper normalMapper
    ){
        this.functionJpaRepository = functionJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.exerciseJpaRepository = exerciseJpaRepository;
        this.normalMapper = normalMapper;
    }

    //get all published functions
    @GetMapping("/public/functions")
    public MappingJacksonValue getPublishedFunctions(){
        return normalMapper.mapFunctionList(functionJpaRepository.findAllByPublished());
    }

    //get function created by the user
    @GetMapping("/users/{user_id}/functions/created")
    public MappingJacksonValue getCreatedFunctionsList(@PathVariable("user_id") int userId){
        //depending on the request author we may or may not show private functions
        if(!userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);
        
        return normalMapper.mapFunctionList(userJpaRepository.findById(userId).get().getCreatedFunctions());
    }

    //get functions, that user is followed on
    @GetMapping("/users/{user_id}/functions/followed")
    public MappingJacksonValue getFollowedFunctionsList(@PathVariable("user_id") int userId){
        //depending on privacy settings, we may or may not show the functions user is followed on to the third party
        if(!userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);

        return normalMapper.mapFunctionList(userJpaRepository.findById(userId).get().getFollowedFunctions());
    }

    //get all functions of the user
    @GetMapping("/users/{user_id}/functions")
    public MappingJacksonValue getAllExercisesList(@PathVariable("user_id") int userId){


        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        List<Function> functions = user.get().getCreatedFunctions();
        functions.addAll(user.get().getFollowedFunctions());

        return normalMapper.mapFunctionList(functions);
    }

    //get function details for the current user
    @GetMapping("/users/{user_id}/functions/{function_id}")
    public MappingJacksonValue getFunctionById(@PathVariable("user_id") int userId, @PathVariable("function_id") int functionId){
        //depending if user is author, privacy and if function is published
        if(!userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);
        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id" + functionId);
    
        return normalMapper.mapFunctionDetailed(function.get());
    }

    //get functions of the exercise
    @GetMapping("/users/{user_id}/functions/exercise/{exercise_id}")
    public MappingJacksonValue getFunctionsOfTheExercise(@PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId){
        //depending on privacy settings and etc
        if(!userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);

        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        return normalMapper.mapFunctionList(exercise.get().getFunctionsIncluded());
    }

    @DeleteMapping("/users/{user_id}/functions/{function_id}")
    public void deleteFunction(@PathVariable("user_id") int userId, @PathVariable("function_id") int functionId){
        //of course there must be check for security and validation
        if(! userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);


        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id" + functionId);

        for(Exercise e : function.get().getExercises()){
            e.getFunctionsIncluded().remove(function.get());
            e.getFunctionPerformance().remove(functionId);
        }

        for(AppUser follower : function.get().getFollowers()){
            follower.getFollowedFunctions().remove(function.get());
        }
        
        functionJpaRepository.deleteById(functionId);
    }

    //adding function    
    @PostMapping("/users/{user_id}/functions")
    public void createFunction(@PathVariable("user_id") int userId, @Valid @RequestBody FunctionDetails functionDetails){
        if(! userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);

        functionJpaRepository.save(new Function(userJpaRepository.findById(userId).get(), false, functionDetails));
    }

    //changing function basic details
    @PutMapping("/users/{user_id}/functions/{function_id}")
    public void updateFunction(@PathVariable("user_id") int userId, @PathVariable("function_id") int functionId, @Valid @RequestBody FunctionDetails functionDetails){
        if(userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);


        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id" + functionId);
        function.get().setFunctionDetails(functionDetails);

        functionJpaRepository.save(function.get());
    }



}

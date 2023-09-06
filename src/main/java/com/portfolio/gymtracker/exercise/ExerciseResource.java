package com.portfolio.gymtracker.exercise;

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

import com.portfolio.gymtracker.JacksonMappers.NormalMapper;
import com.portfolio.gymtracker.exceptions.ExerciseNotFoundException;
import com.portfolio.gymtracker.exceptions.FunctionNotFoundException;
import com.portfolio.gymtracker.exceptions.UserNotFoundException;
import com.portfolio.gymtracker.function.Function;
import com.portfolio.gymtracker.function.FunctionJpaRepository;
import com.portfolio.gymtracker.user.AppUser;
import com.portfolio.gymtracker.user.UserJpaRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;

@RestController
public class ExerciseResource{

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ExerciseJpaRepository exerciseJpaRepository;
    private UserJpaRepository userJpaRepository;
    private FunctionJpaRepository functionJpaRepository;
    private NormalMapper normalMapper;

    public ExerciseResource(ExerciseJpaRepository exerciseJpaRepository, 
        UserJpaRepository userJpaRepository, FunctionJpaRepository functionJpaRepository,
        NormalMapper normalMapper
    ){
        this.exerciseJpaRepository = exerciseJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.functionJpaRepository = functionJpaRepository;
        this.normalMapper = normalMapper;
    }


    //getting public exercises list
    @GetMapping("/public/exercises")
    public MappingJacksonValue getPublicExercisesList(){
        return normalMapper.mapExerciseList(exerciseJpaRepository.findAllByPublished());
    }

    //getting details of an exercise for the current user
    @GetMapping("/users/{user_id}/exercises/{exercise_id}")
    public MappingJacksonValue getExerciseDetails(@PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId){
        if(!userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        return normalMapper.mapExerciseDetailed(exercise.get());
    }

    //get all exercises that user has ever practised
    @GetMapping("/users/{user_id}/exercises")
    public MappingJacksonValue getAllExercisesList(@PathVariable("user_id") int userId){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        List<Exercise> exercises = user.get().getCreatedExercises();
        exercises.addAll(user.get().getFollowedExercises());

        return normalMapper.mapExerciseList(exercises);
    }

    //get list of exercises created by user with "user_id"
    @GetMapping("/users/{user_id}/exercises/created")
    public MappingJacksonValue getCreatedExercisesList(@PathVariable("user_id") int userId){
        //depending on the request author we may show exercises with or without private exercises
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        return normalMapper.mapExerciseList(user.get().getCreatedExercises());
    }

    //get list of pubilc exercises, that user with id "user_id" is followed on
    @GetMapping("/users/{user_id}/exercises/followed")
    public MappingJacksonValue getFollowedExercisesList(@PathVariable("user_id") int userId){
        //depending on the request author and privacy setting we may or may not return the list
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        return normalMapper.mapExerciseList(user.get().getFollowedExercises());
    }

    //get exercises of the user for a chosen function
    @GetMapping("/users/{user_id}/exercises/function/{function_id}")
    public MappingJacksonValue getExercisesListForFunction(@PathVariable("user_id") int userId, @PathVariable("function_id") int functionId){
        //depending on the request author and privacy setting we may or may not return the list

        return  normalMapper.mapExerciseList( ((List<Exercise> )(getAllExercisesList(userId).getValue()) ).stream().filter(
            exercise -> {
                return exercise.getFunctionsIncluded().contains(functionJpaRepository.findById(functionId).get());
            }
        ).toList());
    }

    //delete the exercise
    @DeleteMapping("/users/{user_id}/exercises/{exercise_id}")
    public void deleteExercise(@PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId){
        //of course there must be check, if user if the author and requester is the author
        if(! userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);

        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        for(AppUser follower : exercise.get().getFollowers()){
            follower.getFollowedExercises().remove(exercise.get());
        }

        exerciseJpaRepository.deleteById(exerciseId);
    }

    //adding exercise
    @PostMapping("/users/{user_id}/created/exercises")
    public void createExercise(@PathVariable("user_id") int userId, @Valid @RequestBody ExerciseDetails exerciseDetails){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        Exercise exercise = new Exercise(user.get() , exerciseDetails, false);
        exerciseJpaRepository.save(exercise);
    }

    //changing basic details
    @PutMapping("/users/{user_id}/created/exercises/{exercise_id}")
    public void updateExercise(@PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId, @Valid @RequestBody ExerciseDetails exerciseDetails){
        if(! userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        exercise.get().setExerciseDetails(exerciseDetails);
        exerciseJpaRepository.save(exercise.get());
    } 

    //adding functions
    @PutMapping("/users/{user_id}/exercises/{exercise_id}/functions/add/{function_id}")
    public void addFunctionToExercise(@PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId, 
        @PathVariable("function_id") int functionId
    ){
        if(! userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        exercise.get().getFunctionsIncluded().add(function.get());
        exercise.get().getFunctionPerformance().put(functionId, 1.0);

        exerciseJpaRepository.save(exercise.get());
    } 

    //removing functions
    @PutMapping("/users/{user_id}/exercises/{exercise_id}/functions/remove/{function_id}")
    public void removeFunctionToExercise(@PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId, 
        @PathVariable("function_id") int functionId
    ){
        if(! userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        exercise.get().getFunctionsIncluded().remove(function.get());
        exercise.get().getFunctionPerformance().remove(functionId);

        exerciseJpaRepository.save(exercise.get());
    } 

    //setting performance of the function
    @PutMapping("/users/{user_id}/exercises/{exercise_id}/functions/{function_id}/set/{value}")
    public void removeFunctionToExercise(@PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId, 
        @PathVariable("function_id") int functionId, @PathVariable @Digits(fraction = 1, integer = 2) double value
    ){
        if(! userJpaRepository.existsById(userId)) throw new UserNotFoundException("There`s no user with id " + userId);
        if(! functionJpaRepository.existsById(functionId)) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        if(value == 0) exercise.get().getFunctionPerformance().remove(functionId);
        else exercise.get().getFunctionPerformance().put(functionId, value);
    }
}

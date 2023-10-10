package com.portfolio.gymtracker.exercise;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import static com.portfolio.gymtracker.security.AccessChecking.checkIfUserAccessable;
import static com.portfolio.gymtracker.security.AccessChecking.checkIfExerciseAccessable;
import static com.portfolio.gymtracker.security.AccessChecking.checkIfFunctionAccessable;

import jakarta.validation.Valid;

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
        return normalMapper.mapExerciseList(exerciseJpaRepository.findAllByPublished().stream().filter(
            exercise -> exercise.isPublished()
        ).toList());
    }

    //getting public exercises list for function
    @GetMapping("/public/functions/{function_id}/exercises")
    public MappingJacksonValue getPublicExercisesListByFunction(@PathVariable("function_id") int functionId){
        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("The function with id " + functionId + " was not found");
        if(!function.get().isPublished()) throw new RuntimeException("The function is not published");

        return normalMapper.mapExerciseList(exerciseJpaRepository.findAllByPublished().stream().filter(
            exercise -> 
                exercise.isPublished() && 
                exercise.getFunctionsIncluded().stream().anyMatch(func -> func.getFunctionId() == functionId)
        ).toList());
    }

    @GetMapping("/public/exercises/{exercise_id}")
    public MappingJacksonValue getPublicExercise(@PathVariable("exercise_id") int exercise_id){
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exercise_id);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("The exercise with id " + exercise_id + " does not exist");

        if(! exercise.get().isPublished()) throw new RuntimeException("The exercise was not published");

        return normalMapper.mapExerciseDetailed(exercise.get());
    }

    //getting details of an exercise for the current user
    @GetMapping("/users/{user_id}/exercises/{exercise_id}")
    public MappingJacksonValue getExerciseDetails(Authentication authentication, @PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId){

        //checking if exercise and user exist
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        //checking if user has access
        checkIfUserAccessable(authentication, user.get());
        checkIfExerciseAccessable(user.get(), exercise.get());


        return normalMapper.mapExerciseDetailed(exercise.get());
    }

    //get all exercises that user has ever practised
    @GetMapping("/users/{user_id}/exercises")
    public MappingJacksonValue getAllExercisesList(Authentication authentication, @PathVariable("user_id") int userId){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        List<Exercise> exercises = user.get().getCreatedExercises();
        exercises.addAll(user.get().getFollowedExercises());

        checkIfUserAccessable(authentication, user.get());

        return normalMapper.mapExerciseList(exercises);
    }

    //get list of exercises created by user with "user_id"
    @GetMapping("/users/{user_id}/exercises/created")
    public MappingJacksonValue getCreatedExercisesList(Authentication authentication, @PathVariable("user_id") int userId){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        checkIfUserAccessable(authentication, user.get());

        return normalMapper.mapExerciseList(user.get().getCreatedExercises());
    }

    //get list of pubilc exercises, that user with id "user_id" is followed on
    @GetMapping("/users/{user_id}/exercises/followed")
    public MappingJacksonValue getFollowedExercisesList(Authentication authentication, @PathVariable("user_id") int userId){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        checkIfUserAccessable(authentication, user.get());

        return normalMapper.mapExerciseList(user.get().getFollowedExercises());
    }

    //get exercises of the user for a chosen function
    @GetMapping("/users/{user_id}/exercises/function/{function_id}")
    public MappingJacksonValue getExercisesListForFunction(Authentication authentication, @PathVariable("user_id") int userId, @PathVariable("function_id") int functionId){
        
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        checkIfUserAccessable(authentication, user.get());
        checkIfFunctionAccessable(user.get(), function.get());

        return  normalMapper.mapExerciseList( ((List<Exercise>)getAllExercisesList(authentication, userId).getValue()).stream().filter(
            exercise -> {
                return exercise.getFunctionsIncluded().contains(function.get());
            }
        ).toList());
    }

    //delete the exercise
    @DeleteMapping("/users/{user_id}/exercises/{exercise_id}")
    public void deleteExercise(Authentication authentication, @PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId){
        //of course there must be check, if user if the author and requester is the author
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != exercise.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");

        for(AppUser follower : exercise.get().getFollowers()){
            follower.getFollowedExercises().remove(exercise.get());
        }

        exerciseJpaRepository.deleteById(exerciseId);
    }

    //adding exercise
    @PostMapping("/users/{user_id}/created/exercises")
    public void createExercise(Authentication authentication, @PathVariable("user_id") int userId, @Valid @RequestBody ExerciseDetails exerciseDetails){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        checkIfUserAccessable(authentication, user.get());

        Exercise exercise = new Exercise(user.get() , exerciseDetails, false);
        exerciseJpaRepository.save(exercise);
    }

    //changing basic details
    @PutMapping("/users/{user_id}/created/exercises/{exercise_id}")
    public void updateExercise(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("exercise_id") int exerciseId, @Valid @RequestBody ExerciseDetails exerciseDetails
    ){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != exercise.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");

        exercise.get().setExerciseDetails(exerciseDetails);
        exerciseJpaRepository.save(exercise.get());
    } 

    //adding functions
    @PutMapping("/users/{user_id}/exercises/{exercise_id}/functions/add/{function_id}")
    public void addFunctionToExercise(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("exercise_id") int exerciseId, @PathVariable("function_id") int functionId
    ){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != exercise.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");
        checkIfFunctionAccessable(user.get(), function.get());

        exercise.get().getFunctionsIncluded().add(function.get());
        exercise.get().getFunctionPerformance().put(functionId, 1.0);

        exerciseJpaRepository.save(exercise.get());
    } 

    //removing functions
    @PutMapping("/users/{user_id}/exercises/{exercise_id}/functions/remove/{function_id}")
    public void removeFunctionToExercise(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("exercise_id") int exerciseId, @PathVariable("function_id") int functionId
    ){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != exercise.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");
        checkIfFunctionAccessable(user.get(), function.get());

        exercise.get().getFunctionsIncluded().remove(function.get());
        exercise.get().getFunctionPerformance().remove(functionId);

        exerciseJpaRepository.save(exercise.get());
    } 

    //setting performance of the function
    @PutMapping("/users/{user_id}/exercises/{exercise_id}/functions/{function_id}/set/{value}")
    public void removeFunctionToExercise(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("exercise_id") int exerciseId, @PathVariable("function_id") int functionId, 
        @PathVariable double value
    ){
        logger.info("Value setted: " + value);
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        if(! functionJpaRepository.existsById(functionId)) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != exercise.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");
        
        logger.info("Continue");
        if(value == 0) exercise.get().getFunctionPerformance().remove(functionId);
        else {
            exercise.get().getFunctionPerformance().remove(functionId);
            exercise.get().getFunctionPerformance().put(functionId, value);
            logger.info("Value setted inside: " + exercise.get().getFunctionPerformance().get(functionId));
        }
        exerciseJpaRepository.save(exercise.get());
    }

    @PutMapping("/users/{user_id}/exercises/{exercise_id}/publish")
    @PreAuthorize("hasRole('MODER')")
    public void publishExercise(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("exercise_id") int exerciseId
    ){ 
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != exercise.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");
        

        exercise.get().setPublished(true);
        exerciseJpaRepository.save(exercise.get());
    }

    @PutMapping("/users/{user_id}/exercises/{exercise_id}/unpublish")
    @PreAuthorize("hasRole('MODER')")
    public void unpublishExercise(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("exercise_id") int exerciseId
    ){ 
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        checkIfUserAccessable(authentication, user.get());
        if(user.get().getUserId() != exercise.get().getAuthor().getUserId()) throw new RuntimeException("You have not access to this action");

        exercise.get().setPublished(false);
        exerciseJpaRepository.save(exercise.get());

    }
}

package com.portfolio.gymtracker.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.portfolio.gymtracker.JacksonMappers.NormalMapper;
import com.portfolio.gymtracker.exceptions.ExerciseNotFoundException;
import com.portfolio.gymtracker.exceptions.FunctionNotFoundException;
import com.portfolio.gymtracker.exceptions.UserNotFoundException;
import com.portfolio.gymtracker.exercise.Exercise;
import com.portfolio.gymtracker.exercise.ExerciseJpaRepository;
import com.portfolio.gymtracker.exercise.ExerciseResource;
import com.portfolio.gymtracker.function.Function;
import com.portfolio.gymtracker.function.FunctionJpaRepository;
import com.portfolio.gymtracker.function.FunctionResource;

import jakarta.validation.Valid;

@RestController
public class UserResource {

    Logger logger = LoggerFactory.getLogger(getClass());

    private UserJpaRepository userJpaRepository;
    private ExerciseJpaRepository exerciseJpaRepository;
    private FunctionJpaRepository functionJpaRepository;
    private NormalMapper normalMapper;

    public UserResource(UserJpaRepository userJpaRepository,
        ExerciseJpaRepository exerciseJpaRepository,
        FunctionJpaRepository functionJpaRepository,
        NormalMapper normalMapper
    ){
        this.userJpaRepository = userJpaRepository;
        this.exerciseJpaRepository = exerciseJpaRepository;
        this.functionJpaRepository = functionJpaRepository;
        this.normalMapper = normalMapper;
    }

    private boolean isAdmin(Authentication authentication){
        return authentication.getAuthorities().stream().anyMatch(
            auth ->  auth.getAuthority().equals("ROLE_ADMIN")
        );
    }
    

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public MappingJacksonValue getAppUsersList(){
        //depending on users privacy settings we may or may not show them
        
        return normalMapper.mapUserList(userJpaRepository.findAll()); 
    }

    @GetMapping("/users/{user_id}")
    public MappingJacksonValue getAppUser(Authentication authentication, @PathVariable("user_id") int userId){

        //check whether user exists
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        //security
        if( ! authentication.getName().equals( user.get().getAppUserDetails().getUsername()))
            if( !isAdmin(authentication))
                throw new RuntimeException("You cannot access this page");


        //if valid, then return user details
        return normalMapper.mapUserDetailed(user.get());
    }

    @GetMapping("/users/username/{username}")
    public MappingJacksonValue getAppUserByUsername(Authentication authentication, @PathVariable("username") String username){
        //checking if user exists
        Optional<AppUser> user = userJpaRepository.findByUsername(username);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with username " + username);

        //security
        if( ! authentication.getName().equals( user.get().getAppUserDetails().getUsername()))
            if( !isAdmin(authentication))
                throw new RuntimeException("You cannot access this page");

        return normalMapper.mapUserDetailed(user.get());
    }

    //getting followers of an exercise
    @GetMapping("/public/exercise/{exercise_id}/followers")
    public MappingJacksonValue getUsersFollowingExercise(@PathVariable("exercise_id") int exercise_id){

        //checking if the exercise exists
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exercise_id);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exercise_id);


        return normalMapper.mapUserList(exercise.get().getFollowers()); 
    }
    
    //getting followers of a function
    @GetMapping("/public/function/{function_id}/followers")
    public MappingJacksonValue getUsersFollowingFunction(@PathVariable("function_id") int function_id){

        //does functio nactually exist?
        Optional<Function> function = functionJpaRepository.findById(function_id);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + function_id);
        
        //returning public followers
        return normalMapper.mapUserList(function.get().getFollowers().stream().filter(
            follower -> follower.isPublished()
        ).toList()); 
    }

    //deleting user (by cascad the training data is cleared, but public functions and exercises are remained)
    @DeleteMapping("/users/{user_id}")
    public void deleteAppUser(Authentication authentication, @PathVariable("user_id") int userId){
        //of course there must be security and validation

        //does user exist?
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        //does client have access?
        if( !authentication.getName().equals(user.get().getAppUserDetails().getUsername()) )
            if( ! isAdmin(authentication))
                throw new RuntimeException("You don`t have access to this page");

        //unfollowing form exercises and functions
        for(Exercise e : user.get().getFollowedExercises()){
            e.getFollowers().remove(user.get());
        }
        for(Function f : user.get().getFollowedFunctions()){
            f.getFollowers().remove(user.get());
        }


        //deleting exercises manually in order to manually unfollow its followers
        for(Exercise e : user.get().getCreatedExercises()){
            ExerciseResource exerciseResource = new ExerciseResource(exerciseJpaRepository, userJpaRepository, functionJpaRepository, normalMapper);
            exerciseResource.deleteExercise(userId, e.getExerciseId());
        }


        for(Function  f: user.get().getCreatedFunctions()){
            FunctionResource functionResource = new FunctionResource(functionJpaRepository, userJpaRepository, exerciseJpaRepository, normalMapper);
            functionResource.deleteFunction(userId, f.getFunctionId());
        }
        //finally, deleting the user
        userJpaRepository.deleteById(userId);
    } 

    //registering new user
    // @PostMapping("/users")
    public void registerAppUser(@RequestBody @Valid AppUserDetails userDetails){
        userJpaRepository.save(new AppUser(userDetails));
    }

    //changing basic user details
    @PutMapping("/users/{user_id}")
    public void updateAppUser(Authentication authentication, @PathVariable("user_id") int userId, @RequestBody @Valid AppUserDetails userDetails){
        //does user exist
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        //does client have access?
        if(! authentication.getName().equals(user.get().getAppUserDetails().getUsername()))
            if (! isAdmin(authentication))
                throw new RuntimeException("You don`t have access");

        //returning result
        user.get().setAppUserDetails(userDetails);
        userJpaRepository.save(user.get());
    } 


    @PutMapping("users/{user_id}/following/exercises/remove/{exercise_id}")
    public void unfollowExercise(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("exercise_id") int exerciseId
    ){

        //checking if user and exercise exist
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        //checking the access:
        if( !authentication.getName().equals(user.get().getAppUserDetails().getUsername()))
            if(! isAdmin(authentication))
                throw new RuntimeException("You don`t have access");

        //if user is following the exericse, then you can unfollow
        user.get().getFollowedExercises().removeIf( exericse -> {
            return exericse.getExerciseId() == exerciseId;
        });

        userJpaRepository.save(user.get());
    }

    @PutMapping("users/{user_id}/following/functions/remove/{function_id}")
    public void unfollowFunction(@PathVariable("user_id") int userId, @PathVariable("function_id") int functionId){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        user.get().getFollowedFunctions().removeIf( function -> {
            return function.getFunctionId() == functionId;
        });

        userJpaRepository.save(user.get());
    }

    @PutMapping("users/{user_id}/following/exercises/add/{exercise_id}")
    public void followExercise(Authentication authentication, @PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId){

        //checking if exercise is available and if user exists
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        if(!exercise.get().isPublished()) throw new RuntimeException("The exercise is unpublished");

        //checking if user has access:
        if(! authentication.getName().equals(user.get().getAppUserDetails().getUsername()))
            if(! isAdmin(authentication))
                throw new RuntimeException("You don`t have access to this action");

        //adding
        user.get().getFollowedExercises().add(exerciseJpaRepository.findById(exerciseId).get());

        userJpaRepository.save(user.get());
    }

    @PutMapping("users/{user_id}/following/functions/add/{function_id}")
    public void followFunction(Authentication authentication, @PathVariable("user_id") int userId, @PathVariable("function_id") int functionId){

        //do function and user exist
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        Optional<Function> function = functionJpaRepository.findById(functionId);
        if(function.isEmpty()) throw new FunctionNotFoundException("There`s no function with id " + functionId);

        if(! function.get().isPublished()) throw new RuntimeException("The function is now available");

        //checking if user has access:
        if(! authentication.getName().equals(user.get().getAppUserDetails().getUsername()))
            if(! isAdmin(authentication))
                throw new RuntimeException("You don`t have access to this action");

        //adding
        user.get().getFollowedFunctions().add(functionJpaRepository.findById(functionId).get());

        userJpaRepository.save(user.get());
    }
    
}

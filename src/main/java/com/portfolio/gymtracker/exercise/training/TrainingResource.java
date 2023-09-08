package com.portfolio.gymtracker.exercise.training;

import java.util.Optional;

import org.springframework.http.converter.json.MappingJacksonValue;
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
import com.portfolio.gymtracker.exceptions.TrainingNotFoundException;
import com.portfolio.gymtracker.exceptions.UserNotFoundException;
import com.portfolio.gymtracker.exercise.Exercise;
import com.portfolio.gymtracker.exercise.ExerciseJpaRepository;
import com.portfolio.gymtracker.user.AppUser;
import com.portfolio.gymtracker.user.UserJpaRepository;
import static com.portfolio.gymtracker.security.AccessChecking.checkIfUserAccessable;

import jakarta.validation.Valid;

@RestController
public class TrainingResource {
    
    private TrainingJpaRepository trainingJpaRepository;
    private UserJpaRepository userJpaRepository;
    private ExerciseJpaRepository exerciseJpaRepository;
    private NormalMapper normalMapper;

    public TrainingResource(
        TrainingJpaRepository trainingJpaRepository, 
        UserJpaRepository userJpaRepository, 
        ExerciseJpaRepository exerciseJpaRepository,
        NormalMapper normalMapper
    ){
        this.trainingJpaRepository = trainingJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.exerciseJpaRepository = exerciseJpaRepository;
        this.normalMapper = normalMapper;
    }

    //getting trainings of the user on this exercise
    @GetMapping("/users/{user_id}/exercises/{exercise_id}/trainings")
    public MappingJacksonValue getTrainingsList(Authentication authentication, @PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId){
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        checkIfUserAccessable(authentication, user.get());

        return normalMapper.mapTrainingList(trainingJpaRepository.findAllByUserIdAndExerciseId(userId, exerciseId));
    }

    @GetMapping("/users/{user_id}/exercises/{exercise_id}/trainings/{training_id}")
    public MappingJacksonValue getTraining(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("exercise_id") int exerciseId, @PathVariable("training_id") int trainingId
    ){
          
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        checkIfUserAccessable(authentication, user.get());

        Optional<Training> training = trainingJpaRepository.findByUserIdAndExerciseIdAndTrainingId(userId, exerciseId, trainingId);
        if(training.isEmpty()) throw new TrainingNotFoundException("There`s no training with id " + trainingId);


        return normalMapper.mapTraining(training.get());

    }

    //getting all trainings of the user
    @GetMapping("/users/{user_id}/trainings")
    public MappingJacksonValue getAllUserTrainings(Authentication authentication, @PathVariable("user_id") int userId){

        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        checkIfUserAccessable(authentication, user.get());

        return normalMapper.mapTrainingList(trainingJpaRepository.findAllByUserId(userId));
    }

    @DeleteMapping("/users/{user_id}/exercises/{exercise_id}/trainings/{training_id}")
    public void deleteTraining(Authentication authentication, @PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId, 
        @PathVariable("training_id") int trainingId
    ){

        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        checkIfUserAccessable(authentication, user.get());

        Optional<Training> training = trainingJpaRepository.findByUserIdAndExerciseIdAndTrainingId(userId, exerciseId, trainingId);
        if(training.isEmpty()) throw new TrainingNotFoundException("There`s no training with id " + trainingId);
        
        trainingJpaRepository.delete(training.get()); 
    }

    //delete all training data for current exercise
    @DeleteMapping("/users/{user_id}/exercises/{exercise_id}/trainings")
    public void deleteTrainingsForExercise(Authentication authentication, @PathVariable("user_id") int userId, 
        @PathVariable("exercise_id") int exerciseId
    ){

        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        checkIfUserAccessable(authentication, user.get());

        trainingJpaRepository.deleteAll(trainingJpaRepository.findAllByUserIdAndExerciseId(userId, exerciseId)); 
    }

    //delete all training data for current user
    @DeleteMapping("/users/{user_id}/trainings")
    public void deleteTrainingsForUser(Authentication authentication, @PathVariable("user_id") int userId){
        
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        checkIfUserAccessable(authentication, user.get());

        trainingJpaRepository.deleteAll(trainingJpaRepository.findAllByUserId(userId)); 
    }

    //adding training
    @PostMapping("/users/{user_id}/exercises/{exercise_id}/trainings")
    public void createTraining(Authentication authentication, @PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId, 
        @Valid @RequestBody Training training
    ){
        
        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);
        
        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);
        
        checkIfUserAccessable(authentication, user.get());


        training.setUser(user.get());
        training.setExercise(exercise.get());

        user.get().setTrainingsCount(user.get().getTrainingsCount() + 1);
        training.setTrainingId(user.get().getTrainingsCount());
        
        trainingJpaRepository.save(training);
    }


    @PutMapping("/users/{user_id}/exercises/{exercise_id}/trainings/{training_id}")
    public void updateTrainingById(Authentication authentication, @PathVariable("user_id") int userId, @PathVariable("exercise_id") int exerciseId, 
        @PathVariable("training_id") int trainingId, @Valid @RequestBody Training training
    ){

        Optional<AppUser> user = userJpaRepository.findById(userId);
        if(user.isEmpty()) throw new UserNotFoundException("There`s no user with id " + userId);

        if( ! user.get().getTrainingsList().stream().anyMatch( (tr) -> {
            return tr.getTrainingId() == trainingId;
        })) throw new TrainingNotFoundException("There`s no training with id " + trainingId);

        Optional<Exercise> exercise = exerciseJpaRepository.findById(exerciseId);
        if(exercise.isEmpty()) throw new ExerciseNotFoundException("There`s no exercise with id " + exerciseId);

        checkIfUserAccessable(authentication, user.get());

        
        training.setUser(user.get());
        training.setExercise(exercise.get());

        training.setTrainingId(trainingId);
        
        trainingJpaRepository.save(training);
        return;
    }


}

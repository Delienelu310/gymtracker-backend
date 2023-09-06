package com.portfolio.gymtracker.exercise.training;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingJpaRepository extends JpaRepository<Training, Integer>{

    @Query("select t from Training t where t.user.userId=:userId")
    public List<Training> findAllByUserId(@Param("userId") int userId);

    @Query("select t from Training t where t.user.userId=:userId and t.exercise.exerciseId=:exerciseId")
    public List<Training> findAllByUserIdAndExerciseId(@Param("userId") int userId, @Param("exerciseId") int exerciseId);

    @Query("select t from Training t where t.user.userId=:userId and t.exercise.exerciseId=:exerciseId and t.trainingId=:trainingId")
    public Optional<Training> findByUserIdAndExerciseIdAndTrainingId(@Param("userId") int userId, @Param("exerciseId") int exerciseId, @Param("trainingId") int trainingId);

    // @Query("delete from Training t where t.user.userId=:userId and t.exercise.exerciseId=:exerciseId and t.trainingId=:trainingId")
    // public void deleteByUserIdAndExerciseIdAndTrainingId(@Param("userId") int userId, @Param("exerciseId") int exerciseId, @Param("trainingId") int trainingId);

    // @Query("delete from Training t where t.user.userId=:userId and t.exercise.exerciseId=:exerciseId")
    // public void deleteAllByUserIdAndExerciseId(@Param("userId") int userId, @Param("exerciseId") int exerciseId);

    // @Query("delete from Training t where t.user.userId=:userId")
    // public void deleteAllByUserId(@Param("userId") int userId);
}

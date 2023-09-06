package com.portfolio.gymtracker.exercise;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseJpaRepository extends JpaRepository<Exercise, Integer>{
    
    @Query("select e from Exercise e where e.published=true")
    public List<Exercise> findAllByPublished();
}


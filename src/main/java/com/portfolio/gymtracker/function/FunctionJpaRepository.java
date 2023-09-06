package com.portfolio.gymtracker.function;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;;

@Repository
public interface FunctionJpaRepository extends JpaRepository<Function, Integer>{
    // List<Function> findAllByAppUserId(int userId);

     @Query("select f from Function f where f.published=true")
    public List<Function> findAllByPublished();
}

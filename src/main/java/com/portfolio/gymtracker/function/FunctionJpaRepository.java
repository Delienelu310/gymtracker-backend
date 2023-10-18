package com.portfolio.gymtracker.function;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;;

@Repository
public interface FunctionJpaRepository extends JpaRepository<Function, Integer>{
    // List<Function> findAllByAppUserId(int userId);

    @Query("select f from func f where f.published=true")
    public List<Function> findAllByPublished();

    @Query("SELECT f FROM func f where f.functionDetails.title =:title")
    Optional<Function> findByTitle(@Param("title") String title);
}

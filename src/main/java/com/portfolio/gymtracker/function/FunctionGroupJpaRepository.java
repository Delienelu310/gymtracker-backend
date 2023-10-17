package com.portfolio.gymtracker.function;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionGroupJpaRepository extends JpaRepository<FunctionGroup, Long>{
    @Query("select f from FunctionGroup f where f.published=true")
    public List<FunctionGroup> findAllByPublished();
}

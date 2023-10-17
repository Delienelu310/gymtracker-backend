package com.portfolio.gymtracker.function;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionGroupJpaRepository extends JpaRepository<FunctionGroup, Long>{
    
}

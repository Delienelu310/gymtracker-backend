package com.portfolio.gymtracker.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<AppUser, Integer>{
    @Query("SELECT u FROM AppUser u where u.appUserDetails.username =:username")
    Optional<AppUser> findByUsername(@Param("username") String username);
}

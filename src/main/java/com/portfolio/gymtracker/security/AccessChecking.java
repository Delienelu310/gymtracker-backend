package com.portfolio.gymtracker.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.portfolio.gymtracker.exercise.Exercise;
import com.portfolio.gymtracker.function.Function;
import com.portfolio.gymtracker.user.AppUser;

public class AccessChecking {

    static Logger logger = LoggerFactory.getLogger(AccessChecking.class);

    private static boolean isAdmin(Authentication authentication){
        
        return authentication.getAuthorities().stream().anyMatch(
            auth ->  {
                return auth.getAuthority().equals("SCOPE_ROLE_ADMIN");
            }
        );
    }

    public static void checkIfUserAccessable(Authentication authentication, AppUser user){
        if(! authentication.getName().equals(user.getAppUserDetails().getUsername()))
            if(! isAdmin(authentication))
                throw new RuntimeException("You don`t have access to this action");
    }

    public static void checkIfExerciseAccessable(AppUser user, Exercise exercise){
        if(! user.getCreatedExercises().contains(exercise))
            if(!user.getFollowedExercises().contains(exercise))
                throw new RuntimeException("You have not access to this page");
    }

    public static void checkIfFunctionAccessable(AppUser user, Function function){
        if(! user.getCreatedFunctions().contains(function))
            if(!user.getFollowedFunctions().contains(function))
                throw new RuntimeException("You have not access to this page");
    }
}

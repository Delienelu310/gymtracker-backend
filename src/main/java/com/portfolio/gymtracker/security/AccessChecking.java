package com.portfolio.gymtracker.security;

import org.springframework.security.core.Authentication;

import com.portfolio.gymtracker.user.AppUser;

public class AccessChecking {
    private static boolean isAdmin(Authentication authentication){
        return authentication.getAuthorities().stream().anyMatch(
            auth ->  auth.getAuthority().equals("ROLE_ADMIN")
        );
    }

    public static void checkIfUserAccessable(Authentication authentication, AppUser user){
        if(! authentication.getName().equals(user.getAppUserDetails().getUsername()))
            if(! isAdmin(authentication))
                throw new RuntimeException("You don`t have access to this action");
    }
}

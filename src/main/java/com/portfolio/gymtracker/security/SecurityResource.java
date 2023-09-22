package com.portfolio.gymtracker.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.gymtracker.user.AppUserDetails;
import com.portfolio.gymtracker.user.UserResource;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class SecurityResource {
    
    private PasswordEncoder passwordEncoder;
    private UserResource userResource;
    private UserDetailsService userDetailsService;

    public SecurityResource(PasswordEncoder passwordEncoder, UserResource userResource, UserDetailsService userDetailsService ){
        this.passwordEncoder = passwordEncoder;
        this.userResource = userResource;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public void register(@RequestBody  @Valid UserDTO userDTO){

        //firstly check if everything is okay before registration
        
        // add user details:
        AppUserDetails userDetails = new AppUserDetails();
        userDetails.setUsername(userDTO.getUsername());
        userDetails.setFullname(userDTO.getFullname());
        userDetails.setEmail(userDTO.getEmail()); 

        userResource.registerAppUser(userDetails);

        //register authentication details
        var user = User.withUsername(userDTO.getUsername())
            .password(userDTO.getPassword())
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .roles("USER")
            .build();

        ((JdbcUserDetailsManager)userDetailsService).createUser(user);
        // var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        // jdbcUserDetailsManager.createUser(user);

    }

    @PostMapping("/users/moderator")
    @PreAuthorize("hasRole('ADMIN')")
    public void createModerator(@RequestBody @Valid UserDTO userDTO){


        //check if input is valid

        //...

        var user = User.withUsername(userDTO.getUsername())
            .password(userDTO.getPassword())
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .roles("MODER")
            .build();

        ((JdbcUserDetailsManager)userDetailsService).createUser(user);
        // var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        // jdbcUserDetailsManager.createUser(user);

        //and add user details:
        AppUserDetails userDetails = new AppUserDetails();
        userDetails.setUsername(userDTO.getUsername());
        userDetails.setFullname(userDTO.getFullname());
        userDetails.setEmail(userDTO.getEmail());

        userResource.registerAppUser(userDetails);
    }

}

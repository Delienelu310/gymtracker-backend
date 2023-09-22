package com.portfolio.gymtracker.security;

import jakarta.validation.constraints.NotNull;

public class UserDTO {
    

    @NotNull
    private String username;
    @NotNull
    private String fullname;
    @NotNull
    private String password;
    @NotNull
    private String email;


    public UserDTO(String username, String fullname, String password, String email) {
        this.username = username;
        this.fullname = fullname;
        this.password = password;
        this.email = email;
    }

    public UserDTO(){

    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    
}

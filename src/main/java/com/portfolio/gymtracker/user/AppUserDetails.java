package com.portfolio.gymtracker.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Embeddable
public class AppUserDetails {


    @Column(unique = true)
    @NotNull
    @Size(min=2, max= 20)
    private String username;
    @NotNull 
    @Size(min = 2, max = 50)
    private String fullname;

    @Column(unique = true)
    @NotNull
    @Size(min = 3, max = 50)
    private String email;

    @Min(0)
    @Max(100)
    private int age;

    @Min(0)
    @Max(300)
    private int height;

    @Min(0)
    @Max(200)
    private int weight;

    //for registration
    public AppUserDetails(String username, String fullname, String email) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
    }

    //for settings 
    public AppUserDetails(String username, String fullname, String email, String password, int age, int height,
            int weight) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    //for jpa:

    public AppUserDetails() {
        
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

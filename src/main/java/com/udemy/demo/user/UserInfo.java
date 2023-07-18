package com.udemy.demo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public class UserInfo {

     @Id @JsonIgnore
    private int id;

    private String email;

    @Size(min = 2, max = 25, message = "Firstname between 2 and 25 characters")
    private String lastName;

    @Size(min = 2, max = 25, message = "Lastname between 2 and 25 characters")
    private String firstName;
    private String password;

    @Transient @JsonIgnore
    private String token;

    public UserInfo() {
    }

    public UserInfo(String email) {
        this.email = email;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
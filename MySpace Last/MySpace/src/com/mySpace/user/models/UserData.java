package com.mySpace.user.models;

import java.io.Serializable;
import java.time.LocalDate;


public class UserData implements Serializable {

    private String email;
    private String password;
    private String name;
    private int id;
    private Gender gender;
    private  LocalDate birthday;
    private String description;
    private static int totalU = 1;

    public UserData(String email, String password, String name, Gender gender, LocalDate birthday, String description) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.id = totalU;
        this.gender = gender;
        this.birthday = birthday;
        this.description = description;
        totalU++;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate bDay){
        this.birthday = bDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}

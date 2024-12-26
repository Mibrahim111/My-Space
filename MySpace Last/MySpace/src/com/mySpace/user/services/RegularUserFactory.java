package com.mySpace.user.services;

import com.mySpace.user.models.Gender;
import com.mySpace.user.models.User;

import java.time.LocalDate;
import java.util.Random;

public class RegularUserFactory extends AbstractUserFactory{

    // Generate a random unique ID
    public static int generateID(int a, int b) {
        Random random = new Random();
        return random.nextInt(b - a + 1) + a;
    }

    public User editUser(User clonedUser, String email, String password, String name, Gender gender,
    LocalDate birthDate, String bio){

        User newUser = clonedUser.DeepCopy();


        if (email != null) newUser.getData().setEmail(email);
        if (password != null) newUser.getData().setPassword(password);
        if (name != null) newUser.getData().setName(name);
        if (gender != null) newUser.getData().setGender(gender);
        if (birthDate != null) newUser.getData().setBirthday(birthDate);
        if (bio != null) newUser.getData().setDescription(bio);

        return newUser;
    }

    @Override
    public User createUser(String name, String userName, String password, String email,
                             Gender gender, LocalDate birthDate, String bio) {

        User user = new User(userName,email,password,name,gender,birthDate,bio);
        user.getData().setId(generateID(1,2000000));
        System.out.println("Registration successful! Welcome, " + userName);
        return user;
    }



}

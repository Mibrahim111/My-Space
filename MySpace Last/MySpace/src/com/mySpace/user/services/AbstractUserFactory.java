package com.mySpace.user.services;

import com.mySpace.user.models.Gender;
import com.mySpace.user.models.User;

import java.time.LocalDate;

public abstract class AbstractUserFactory {

    public abstract User createUser(String name, String userName, String password, String email,
                                    Gender gender, LocalDate birthDate, String bio);
}

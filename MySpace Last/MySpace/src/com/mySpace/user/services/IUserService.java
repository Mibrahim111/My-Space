package com.mySpace.user.services;

import com.mySpace.user.models.Gender;
import com.mySpace.user.models.User;

import java.time.LocalDate;
import java.util.Set;

public interface IUserService extends IFollowService {
     User registerUser(String name , String userName , String password , String email , Gender gender , LocalDate birthDate , String bio);
     User login(String email,String password);
     User updateUser(User olduser, String email, String password, String name, Gender gender, LocalDate birthDate
            , String bio , int ID);

     Set<User> peopleYouMayKnow(User myUser);


}

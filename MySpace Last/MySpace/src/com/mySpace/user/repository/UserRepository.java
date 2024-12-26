package com.mySpace.user.repository;
import com.mySpace.user.models.User;

import java.io.IOException;
import java.util.Map;

//repo Contains all Users in a HashMap
//creation ,delete , searching
//Hashmap is used for fast search o(1)
//in repo you can create a new instance "User"
//in addition to deletion ,editing ,searching
//only one email can create one account
//used singleton design pattern "will be Used in all repos"
//Ensures a single instance of each repository throughout the runtime, making it global.
//focusing on thread safety to add multithreading in the future


public class UserRepository extends AbstractUserRepo {

    private static UserRepository instance;

    private UserRepository() {
        try {
            loadFromFile();
        } catch (IOException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveToFile();
            } catch (IOException e) {
                System.err.println("Failed to save users: " + e.getMessage());
            }
        }));
    }


    public static UserRepository getInstance() {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) {
                    instance = new UserRepository();
                }
            }
        }
        return instance;
    }

   @Override
    public void add(User user) {

            userStorage.put(user.getUserName(), user);

    }


    public User findByKey(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            System.err.println("User Name cannot be null or empty.");
            return null;
        }

        return userStorage.get(userName);

    }

    public boolean exists(String username) {
        return userStorage.containsKey(username);
    }


    public void deleteByUserName(String userName) {
        synchronized (userStorage) {
            userStorage.remove(userName);
        }
    }

    public Map<String, User> getUserStorage() {
        return userStorage;
    }


}
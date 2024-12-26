package com.mySpace.post.repository;

import com.mySpace.post.models.Post;
import com.mySpace.user.models.User;


import java.io.IOException;
import java.util.*;

public class PostRepository extends AbstractPostRepo {

    private static PostRepository instance;


    // Singleton design pattern
    private PostRepository() {
        try {
            loadFromFile();
        } catch (IOException e) {
            System.err.println("Failed to load posts: " + e.getMessage());
        }

        // Register a shutdown hook to save data on termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveToFile();
            } catch (IOException e) {
                System.err.println("Failed to save posts: " + e.getMessage());
            }
        }));
    }

    // Singleton design pattern
    public static PostRepository getInstance() {
        if (instance == null) {
            synchronized (PostRepository.class) {
                if (instance == null) {
                    instance = new PostRepository();
                }
            }
        }
        return instance;
    }


     public List<Post> getMyFyp(User user){

        List<Post> fyp = new ArrayList<>();

        for(Post post : postStorage){
            if(post.getPrivacyStrategy().isVisible(post.getAuthor(),user))
                fyp.add(post);
        }

        fyp.sort(Post::compareTo);
        return fyp;
     }


}

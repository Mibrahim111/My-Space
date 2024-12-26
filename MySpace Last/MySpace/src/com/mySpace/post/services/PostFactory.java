package com.mySpace.post.services;

import com.mySpace.post.models.Post;
import com.mySpace.user.models.User;
import com.mySpace.user.services.RegularUserFactory;

import java.util.List;

public class PostFactory {

    public Post createPost(String content, User author, boolean isPrivate , List<User> taggedUsers) {

        Post post = new Post(author,content,
                isPrivate ? new PrivateStrategy() : new PublicStrategy());
        post.setTaggedUsers(taggedUsers);
        post.setId(RegularUserFactory.generateID(1,20000));

        System.out.println("Posted!!");
        return post;
    }

}

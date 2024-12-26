package com.mySpace.post.services;

import com.mySpace.post.models.Post;
import com.mySpace.user.models.User;

import java.io.Serializable;

public class PrivateStrategy implements IPrivacyStrategy, Serializable {


    @Override
    public boolean isVisible(User author, User viewer) {
        return (author.getFollowers().contains(viewer) &&
                !author.getRestrictedUsers().contains(viewer))||author.equals(viewer);
    }

    @Override
    public boolean allowRepost(Post post) {
        return false;
    }
}

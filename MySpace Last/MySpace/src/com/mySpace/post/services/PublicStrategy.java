package com.mySpace.post.services;

import com.mySpace.post.models.Post;
import com.mySpace.user.models.User;

import java.io.Serializable;

public class PublicStrategy implements IPrivacyStrategy, Serializable {
    public boolean isVisible(User Author, User viewer) {
        return true;
    }

    @Override
    public boolean allowRepost(Post post) {

        return true;
    }
}

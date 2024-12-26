package com.mySpace.post.services;

import com.mySpace.post.models.Post;
import com.mySpace.user.models.User;

import java.io.Serializable;

public interface IPrivacyStrategy extends Serializable {
    boolean isVisible(User Author , User viewer);

    boolean allowRepost(Post post);



}

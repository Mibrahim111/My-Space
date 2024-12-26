package com.mySpace.post.services;

import com.mySpace.post.models.Post;
import com.mySpace.user.models.User;

public interface IPostServices {

     void repost(Post originalPost, User newAuthor);
     void addLikeToPost(Post post, User user);
     void removeLikeFromPost(Post post, User user);

}

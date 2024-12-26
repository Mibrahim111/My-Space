package com.mySpace.post.models;

import com.mySpace.user.models.User;

public enum Reactions {
    Like,
    Love,
    HaHa,
    Sad,
    Angry;

    private User author;

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}

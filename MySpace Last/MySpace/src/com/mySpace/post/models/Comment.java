package com.mySpace.post.models;

import com.mySpace.user.models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Comment implements Serializable {
    private int commentId; //generate different ids from posts
    private String text;
    private User author;
    ArrayList<Comment> replies;
    private List<Reactions> reactions;
    private static int totalComments = 1;
    // REACTIONS

    public Comment(String text, User author) {
        commentId = totalComments++;
        this.text = text;
        this.author = author;
        replies = new ArrayList<>();
        this.reactions = new ArrayList<>();
    }

    public int getCommentId()
    {
        return commentId;
    }

    public String getText()
    {
        return text;
    }

    public User getAuthor()
    {
        return author;
    }

    public ArrayList<Comment> getReplies()
    {
        return replies;
    }

    public List<Reactions> getReactions()
    {
        return reactions;
    }


    // Add a reply to the comment
    public void addReply(Comment reply)
    {
        this.replies.add(reply);
    }


    // Add a reaction to the comment
    public void addReaction(Reactions reaction)
    {
        this.reactions.add(reaction);
    }


    // Display comment details
    public void displayComment()
    {
        System.out.println(author.getData().getName() + ": " + text);
        System.out.println("Reactions: " + reactions);
        for (Comment reply : replies) {
            System.out.println("  Reply: ");
            reply.displayComment();
        }
    }


    // In Comment class
    private Set<User> likedByUsers = new HashSet<>();

    public Set<User> getLikedByUsers() {
        return likedByUsers;
    }

    public void addLike(User user) {
        likedByUsers.add(user);
    }

    public boolean hasLiked(User user) {
        return likedByUsers.contains(user);
    }

//    @Override
//    public String toString() {
//        return author.getUserName() + '\n' + text + '\n';
//    }

    @Override
    public String toString() {
        return author.getUserName() + ": " + text + " [" + likedByUsers.size() + " likes]";
    }

}

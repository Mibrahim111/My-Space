package com.mySpace.post.models;
import com.mySpace.user.models.User;
import com.mySpace.post.services.IPrivacyStrategy;
import com.mySpace.post.services.PrivateStrategy;
import com.mySpace.post.services.PublicStrategy;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Post implements Cloneable, Serializable , Comparable<Post>{

    private static final long serialVersionUID = 1L;

    private int id;
    private User author;
    private IPrivacyStrategy privacyStrategy;
    private LocalDate timestamp;
    private String content; // temporary until we add media
    private int numOfReposts;
    private static int totP = 0;

    private List<User> taggedUsers;
    private Set<User> likeUser;
    private Set<User> repostUser;
    private List<Comment> comments; // List of comments
    private List<Reactions> reactions;

    public Post(User author, String content , IPrivacyStrategy privacyStrategy) {
        this.id = totP++;
        this.author = author;
        this.privacyStrategy = privacyStrategy;
        this.timestamp = LocalDate.now();
        this.comments = new ArrayList<>();
        this.reactions = new ArrayList<>();
        this.repostUser = new HashSet<>();
        this.likeUser = new HashSet<>();
        this.numOfReposts = 0;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public int getNumOfReposts() {
        return numOfReposts;
    }

    public IPrivacyStrategy getPrivacyStrategy() {
        return privacyStrategy;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Reactions> getReactions() {
        return reactions;
    }

    public void displayPost() {
        try {
            System.out.println(this.author.getData().getName());
            System.out.println("Posted in: " + this.timestamp);
            System.out.println(this.content);
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }

    public void setReactions(List<Reactions> reactions) {
        this.reactions = reactions;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Set<User> getRepostUser() {
        return repostUser;
    }

    public void setRepostUser(Set<User> repostUser) {
        this.repostUser = repostUser;
    }

    public Set<User> getLikeUser() {
        if (!likeUser.isEmpty())
            return likeUser;
        return null;
    }

    public void setLikeUser(Set<User> likeUser) {
        this.likeUser = likeUser;
    }

    public void setTaggedUsers(List<User> taggedUsers) {
        this.taggedUsers = taggedUsers;
    }

    public String getContent() {
        return content;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void incrementNumOfReposts() {
        this.numOfReposts++;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPrivacyStrategy(IPrivacyStrategy privacyStrategy) {
        this.privacyStrategy = privacyStrategy;
    }

    public void setPrivacyStrategy(boolean isPrivate) {
        this.privacyStrategy = (isPrivate ? new PrivateStrategy() : new PublicStrategy());
    }

    @Override
    public Post clone() {
        try {
            Post cloned = (Post) super.clone();
            cloned.comments = new ArrayList<>(); // Comments of a repost are empty
            cloned.reactions = new ArrayList<>(); // same
            cloned.author = null;

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<User> getTaggedUsers() {
        return taggedUsers;
    }

    public void addTaggedUser(User user) {
        if (!taggedUsers.contains(user)) {
            taggedUsers.add(user);
        }
    }


    @Override
    public String toString() {
        return " Date :" + timestamp + " ID :" + id + "     Name:" + author.getUserName() + "\n\n" + content + "\n\n" +
                "Likes: " + likeUser.size()  + " Reposts: " +  repostUser.size()
                + " Comments: " +  comments.size() + '\n'
                + "Tagged: " +  taggedUsers.size() + '\n' ;
    }

    // Adds a user to the like list
    public void addLike(User user) {
        likeUser.add(user);

    }

    // Removes a user from the like list
    public void removeLike(User user) {
        likeUser.remove(user);
    }

    // Checks if a user has already liked the post
    public boolean hasLiked(User user) {
        return likeUser.contains(user);
    }
    public void addReaction(Reactions reaction) {
        this.reactions.add(reaction);
    }

    public int getPublicityFYP(){
        return (likeUser.size() + repostUser.size());
        //descending
    }

    public boolean hasReposted(User user) {
        return repostUser.contains(user);
    }

    @Override
    public int compareTo(Post other) {
        // Sorting by publicity FYP in descending order
        return Integer.compare(other.getPublicityFYP(), this.getPublicityFYP());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Post post = (Post) obj;
        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

}


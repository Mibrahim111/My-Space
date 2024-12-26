package com.mySpace.user.models;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class User implements Serializable ,Cloneable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userName;
    private UserData data;

    private Set<User> followers;
    private Set<User> following;
    private Set<User> restrictedUsers;
    private Set<User> followRequests = new HashSet<>();


    private ArrayList<String> followersNotifications; // for private Users only

    private User()  {

        followers = new HashSet<>();
        following = new HashSet<>();
        restrictedUsers = new HashSet<>();
        followersNotifications = new ArrayList<>();
    }

    public User(String userName , String email, String password, String name, Gender gender, LocalDate birthday,
                String description){
        this();
        this.userName = userName;
        this.data = new UserData(email, password, name, gender, birthday, description);
//        this.privacy = Privacy.PRIVATE;
    }

    // copy constructor

    public User(User other) {
        this.userName = other.userName;
        this.data = other.data;



        this.followers = other.followers;
        this.following = other.following;
        this.restrictedUsers = other.restrictedUsers;
        this.followRequests = other.followRequests;


        this.followersNotifications = other.followersNotifications;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setData(UserData data) {
        this.data = data;
    }


    public Set<User> getFollowers() {
        if(this.followers.isEmpty())
            return new HashSet<>();
        return this.followers;
    }

    public Set<User> getRestrictedUsers() {
        if(this.restrictedUsers.isEmpty())
            return new HashSet<>();
        return this.restrictedUsers;
    }

    public Set<User> getFollowing() {
        if(this.following.isEmpty())
            return new HashSet<>();
        return this.following;
    }

    public UserData getData() {
        return data;
    }


    public void addFollower(User user) {
        if(user == null) throw new IllegalArgumentException("No Follower with these credentials found");
        followers.add(user);
    }

    public void removeFollower(User user) {
        if(user == null || this.equals(user))
            throw new IllegalArgumentException("No Follower with these credentials found");
        followers.remove(user);
        user.unfollow(this);
    }

    public void follow(User user) {
        if (user == null || this.equals(user))
            throw new IllegalArgumentException("Invalid user to follow.");

        following.add(user);
    }

    public void addRestrictedUser(User user){
        if(user == null || this.equals(user))
            throw new IllegalArgumentException("Invalid user to Restrict.");
        restrictedUsers.add(user);
    }



    public Set<User> getFollowRequests() {
        if(this.followRequests.isEmpty())
           return new HashSet<>();

        return this.followRequests;
    }


    public void unfollow(User user) {
        following.remove(user);
        user.getFollowers().remove(this);
    }

    public void viewUser(){
        System.out.println(this.getUserName());
        System.out.println("Bio: " + this.data.getDescription());
        System.out.println("followers: " + this.followers.size());
        System.out.println("following" + this.following.size());
    }

    public void PrintUserINFO(){ // private User Data for testing
        System.out.println("Name: " + this.data.getName());
        System.out.println("Email: " + this.data.getEmail());
        System.out.println("Password: " + this.data.getPassword());
        System.out.println("ID: " + this.data.getId());
        System.out.println("Gender: " + this.data.getGender());
        System.out.println("Description: " + this.data.getDescription());
        System.out.println("Date of Birth: " + this.data.getBirthday());
    }

    public User DeepCopy() {
        User cloned = new User(this); // Use the copy constructor as a base

        // Deep copy of mutable
        cloned.followers = new HashSet<>(this.followers);
        cloned.following = new HashSet<>(this.following);
        cloned.restrictedUsers = new HashSet<>(this.restrictedUsers);
        cloned.followRequests = new HashSet<>(this.followRequests);
        cloned.followersNotifications = new ArrayList<>(this.followersNotifications);

        return cloned;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
       return super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public User getRestrictedUser(String target){
        for(User user : restrictedUsers)
            if(user.getUserName().equals(target))
                return user;
        return null;
    }
    public User getRequestingUser(String target){
        for(User user : followRequests)
            if(user.getUserName().equals(target))
                return user;
        return null;
    }

    @Override
    public String toString() {
        return "Username: " + userName + '\n';
    }
}

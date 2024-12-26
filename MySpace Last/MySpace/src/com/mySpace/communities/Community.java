package com.mySpace.communities;

import com.mySpace.post.models.Post;
import com.mySpace.post.services.PostFactory;
import com.mySpace.user.models.User;


import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Community implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    private final Set<User> members;
    private final ArrayList<Post> posts;
    private String name;
    private Visibility visibility;

    public Community(String name, Visibility visibility){
        this.name = name;
        this.members = new HashSet<>();

        this.posts= new ArrayList<>();
        this.visibility = visibility;
    }

    public Set<User> getMembers() {
        return members;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void addMember(User user){
        members.add(user);
        System.out.println(user.getUserName() + " has been added as a member.");
    }

    public void viewMembers(){
        for (User user : members)
            user.viewUser();
    }

    public void addPost(String content, User author, boolean isPrivate , List<User> taggedUsers){
        PostFactory postFactory  = new PostFactory();
        Post post = postFactory.createPost( content, author, isPrivate ,  taggedUsers);
        posts.add(post);
    }


    public void removeMember(User user) {
       if(members.contains(user))
        this.members.remove(user);
    }

    public boolean isMember(String username)
    {
        for(User user : members)
            if(user.getUserName().equalsIgnoreCase(username))
                return true;
        return false;
    }
}

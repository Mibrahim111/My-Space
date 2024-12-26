package com.mySpace.user.services;


import com.mySpace.post.models.Post;
import com.mySpace.post.repository.PostRepository;
import com.mySpace.user.repository.UserRepository;
import com.mySpace.user.models.Gender;
import com.mySpace.user.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.time.LocalDate;
import java.util.*;

public class UserService implements IUserService {


    private final UserRepository userRepository;
    private static UserService instance;
    RegularUserFactory regularUserFactory = new RegularUserFactory();//singleton design pattern


    private UserService() {
        this.userRepository = UserRepository.getInstance();
    }

    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }
    // using prototype design pattern
    @Override
    public User registerUser(String name, String userName, String password, String email,
                             Gender gender, LocalDate birthDate, String bio) {
        if (userRepository.exists(userName)) {
            System.err.println("Error: User with this username already exists!");
            return null;
        }


        User newUser = regularUserFactory.createUser( name,  userName,  password,  email, gender ,  birthDate, bio);
        userRepository.add(newUser);

        System.out.println("Registration successful! Welcome, " + name);
        return newUser;
    }


    @Override
    public User updateUser(User oldUser, String email, String password, String name, Gender gender, LocalDate birthDate, String bio,
                           int ID) {

        User newUser = regularUserFactory.editUser(oldUser, email, password, name, gender,
                birthDate, bio);

        userRepository.deleteByUserName(oldUser.getUserName());
        userRepository.add(newUser);
        return newUser;
    }

    @Override
    public User login(String userName , String password) {

        User user = userRepository.findByKey(userName);

        if (user == null) {
            System.err.println("Error: No account found with this UserName ");
            return null;
        }

        if (!user.getData().getPassword().equals(password)) {
            System.err.println("Invalid credentials! Incorrect password ");
            return null;
        }

        System.out.println("Login successful! Welcome back, " + user.getData().getName());
        return user;
    }


    public void deleteUser(User user){
        for(User user1 : user.getFollowing())
            user.unfollow(user1);
        for(User user1 : user.getFollowers())
            user.removeFollower(user1);
        for(User user1 : user.getRestrictedUsers())
            unblockFollower(user,user1);
       for(User user1 : userRepository.getUserStorage().values())
          if(user1.getFollowRequests().contains(user)) user1.getFollowRequests().remove(user);

        PostRepository postRepository = PostRepository.getInstance();
        for(Post post : postRepository.getPostStorage())
            if(post.getAuthor().equals(user))
                postRepository.getPostStorage().remove(post);

        this.userRepository.deleteByUserName(user.getUserName());
    }


    @Override
    public void blockFollower(User myUser , User blockedUser){

        if(blockedUser == null)
        {
            System.err.println("Error: No account found with this user name.");
            return;
        }

        if(myUser.getFollowers().contains(blockedUser) || myUser.getFollowing().contains(blockedUser)){
          myUser.addRestrictedUser(blockedUser);
            System.out.println("User blocked Successfully!");
        }
        else {
            System.err.println("You have No Followers with this User name");
        }
    }

    @Override
    public void unblockFollower(User myUser, User blockedUser) {
        if(myUser == null || blockedUser == null)
            return;

        if(myUser.getRestrictedUsers().contains(blockedUser))
        {
            myUser.getRestrictedUsers().remove(blockedUser);
        }
    }

    @Override
    public Set<User> peopleYouMayKnow(User myUser) {
        System.out.println("People You May Know: ");
        Set<User> myUsers = new HashSet<>();
        myUsers.addAll(myUser.getFollowers());
        myUsers.addAll(myUser.getFollowing());

        Set<User> ret = new HashSet<>();

        for(User user: myUsers){

            for(User user2 : user.getFollowers()){
               if(!user2.getUserName().equals(myUser.getUserName()) && !myUser.getFollowers().contains(user2) && !myUser.getFollowing().contains(user2))
                   ret.add(user2);
            }
            for(User user2 : user.getFollowing()){
                if(!user2.getUserName().equals(myUser.getUserName())&& !myUser.getFollowers().contains(user2) && !myUser.getFollowing().contains(user2))
                    ret.add(user2);
            }

        }

        return ret;
    }



    @Override
    public void removeFollower(User myUser,User removedFollower) {


        if(removedFollower == null)
        {
            System.err.println("Error: No account found with this user name.");
            return;
        }

        if(myUser.getFollowers().contains(removedFollower)){
            myUser.removeFollower(removedFollower);

            System.out.println("This User Doesn't follow you anymore");
        }
        else {
            System.err.println("You have No Followers with this User name");
        }

    }


    @Override
    public void follow(User myUser , User followedUser){

        if(followedUser == null)
        {
            System.err.println("Error: No account found with this user name.");
            return;
        }

        {
            followedUser.addFollower(myUser);
            myUser.follow(followedUser);
            System.out.println("You are Now following " + followedUser.getUserName());

        }

    }

    @Override
    public ArrayList<User> FollowsMeBack(User myUser){
        ArrayList<User> ret = new ArrayList<>();
        for(User user : myUser.getFollowing())
        {
            if(user.getFollowing().contains(myUser))
                ret.add(user);
        }
        return ret;
    }


    public void YouBothFollow(User myUser, User targetUser) {

        Stage resultStage = new Stage();
        resultStage.setTitle("Users You Both Follow");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Users you both follow:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> userList = new ListView<>();

        for (User user : userRepository.getUserStorage().values())
            if (myUser.getFollowing().contains(user) && targetUser.getFollowing().contains(user))
                userList.getItems().add(user.getUserName());



        if (userList.getItems().isEmpty()) {
            userList.getItems().add("No mutual users found.");
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> resultStage.close());

        layout.getChildren().addAll(titleLabel, userList, closeButton);

        Scene scene = new Scene(layout, 400, 300);
        resultStage.setScene(scene);
        resultStage.show();
    }

    public void YouBothAreFollowedBy(User myUser, User targetUser) {

        Stage resultStage = new Stage();
        resultStage.setTitle("Users Following You Both");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Users following you both:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> userList = new ListView<>();

        for(User user : userRepository.getUserStorage().values())
            if (myUser.getFollowers().contains(user) && targetUser.getFollowers().contains(user)) {
                userList.getItems().add(user.getUserName());
            }

    if (userList.getItems().isEmpty()) {
            userList.getItems().add("No mutual users found.");
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> resultStage.close());

        layout.getChildren().addAll(titleLabel, userList, closeButton);

        Scene scene = new Scene(layout, 400, 300);
        resultStage.setScene(scene);
        resultStage.show();
    }

}

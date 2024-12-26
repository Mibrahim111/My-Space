package com.utils.gui;

import com.mySpace.user.models.User;
import com.mySpace.user.services.UserSearchService;
import com.mySpace.user.services.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Connections {

    private final UserService userService;
    private final User loggedInUser;
    private final UserSearchService userSearchService;

    public Connections(UserService userService, User loggedInUser) {
        this.userService = userService;
        this.userSearchService = new UserSearchService();
        this.loggedInUser = loggedInUser;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Connections");

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);

        Button findFriendsButton = new Button("People You may Know");
        Button followersButton = new Button("Followers");
        Button followingButton = new Button("Following");
        Button blockedUsersButton = new Button("Blocked Users");
        Button backButton = new Button("Back");

        findFriendsButton.setOnAction(e -> findFriends(primaryStage));
        followersButton.setOnAction(e -> showFollowers(primaryStage));
        followingButton.setOnAction(e -> showFollowing(primaryStage));
        blockedUsersButton.setOnAction(e -> showBlockedUsers(primaryStage));
        backButton.setOnAction(e -> navigateToMainMenu(primaryStage));

        mainLayout.getChildren().addAll(findFriendsButton,followersButton, followingButton, blockedUsersButton, backButton);

        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void findFriends(Stage primaryStage) {
        findFriendsList(
                "People You May Know",
                userService.peopleYouMayKnow(loggedInUser),
                primaryStage,
                "View",
                "Follow",
                this::viewUserData,
                this::follow
        );
    }

    private void follow(User user, User targetUser) {
        if (user.getFollowing().contains(targetUser) || targetUser.getFollowers().contains(user)) {
            showAlert(Alert.AlertType.WARNING, "Follow User", "You are already following " + targetUser.getUserName());
        }
        else if(targetUser.getFollowRequests().contains(user)){
            showAlert(Alert.AlertType.WARNING, "Follow User", "You already sent a follow request " + targetUser.getUserName());
        }
        else if (user.equals(targetUser)) {

            showAlert(Alert.AlertType.WARNING, "Follow User", "You Can't follow yourself " + targetUser.getUserName());
        } else {
            userService.follow(user, targetUser);

                showAlert(Alert.AlertType.INFORMATION, "Follow User", "You are now following " + targetUser.getUserName());

        }
    }

    private void findFriendsList(String title, Iterable<User> users, Stage primaryStage, String action2Text
            ,String action3Text , UserAction action2 , UserAction action3) {
        Stage userListStage = new Stage();
        userListStage.setTitle(title);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title + ":");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> userListView = new ListView<>();
        users.forEach(user -> userListView.getItems().add(user.getUserName()));

        if (userListView.getItems().isEmpty()) {
            userListView.getItems().add("No users found ");
        }


        Button action2Button = new Button(action2Text);
        Button action3Button = new Button(action3Text);


        action2Button.setOnAction(e -> {
            String selectedUsername = userListView.getSelectionModel().getSelectedItem();
            handleUserSelection(selectedUsername, action2);
        });

        action3Button.setOnAction(e -> {
            String selectedUsername = userListView.getSelectionModel().getSelectedItem();
            handleUserSelection(selectedUsername, action3);
        });

        HBox buttonLayout = new HBox(12, action2Button , action3Button);
        buttonLayout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, userListView, buttonLayout);

        Scene scene = new Scene(layout, 900, 600);
        userListStage.setScene(scene);
        userListStage.show();
    }


    private void showFollowers(Stage primaryStage) {
        showUserList(
                "Your Followers",
                loggedInUser.getFollowers(),
                primaryStage,
                "Block",
                "Remove",
                "View",
                this::blockFollower,
                this::removeFollower,
                this::viewUserData
        );
    }



    private void showBlockedUsers(Stage primaryStage) {
        Stage blockedUsersStage = new Stage();
        blockedUsersStage.setTitle("Blocked Users");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Blocked Users:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> blockedUsersList = new ListView<>();
        loggedInUser.getRestrictedUsers().forEach(user -> blockedUsersList.getItems().add(user.getUserName()));

        if (blockedUsersList.getItems().isEmpty()) {
            blockedUsersList.getItems().add("No blocked users found.");
        }

        Button unblockButton = new Button("Unblock");

        unblockButton.setOnAction(e -> {
            User selectedUser = loggedInUser.getRestrictedUser(
                    blockedUsersList.getSelectionModel().getSelectedItem());
            if (selectedUser != null && !blockedUsersList.getSelectionModel().getSelectedItem().equals("No blocked users found.")) {
                userService.unblockFollower(loggedInUser, selectedUser);
                blockedUsersList.getItems().remove(selectedUser);
                showAlert(Alert.AlertType.INFORMATION, "Unblock", "Unblocked " + selectedUser.getUserName() + " successfully.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Unblock", "Please select a user to unblock.");
            }
        });

        layout.getChildren().addAll(titleLabel, blockedUsersList, unblockButton);

        Scene scene = new Scene(layout, 900, 600);
        blockedUsersStage.setScene(scene);
        blockedUsersStage.show();
    }

    private void showFollowing(Stage primaryStage) {
        showFollowingList(
                "Your Following",
                loggedInUser.getFollowing(),
                primaryStage,

                "Unfollow",
                "View",

                this::unfollow,
                this::viewUserData
        );
    }
    private void viewUserData(User myUser, User selectedUser) {
        Stage userDataStage = new Stage();
        userDataStage.setTitle("User Details: " + selectedUser.getUserName());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Name: " + selectedUser.getData().getName());
        Label usernameLabel = new Label("Username: " + selectedUser.getUserName());
        Label bioLabel = new Label("Bio: " + selectedUser.getData().getDescription());
        Label followerCountLabel = new Label("Followers: " + selectedUser.getFollowers().size());
        Label followingCountLabel = new Label("Following: " + selectedUser.getFollowing().size());

        layout.getChildren().addAll(nameLabel, usernameLabel, bioLabel, followerCountLabel, followingCountLabel);

        Scene scene = new Scene(layout, 900, 600);
        userDataStage.setScene(scene);
        userDataStage.show();
    }

    private void showFollowingList(String title, Iterable<User> users, Stage primaryStage, String action2Text
            ,String action3Text , UserAction action2 , UserAction action3) {
        Stage userListStage = new Stage();
        userListStage.setTitle(title);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title + ":");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> userListView = new ListView<>();
        users.forEach(user -> userListView.getItems().add(user.getUserName()));

        if (userListView.getItems().isEmpty()) {
            userListView.getItems().add("No users found ");
        }


        Button action2Button = new Button(action2Text);
        Button action3Button = new Button(action3Text);


        action2Button.setOnAction(e -> {
            String selectedUsername = userListView.getSelectionModel().getSelectedItem();
            handleUserSelection(selectedUsername, action2);
        });

        action3Button.setOnAction(e -> {
            String selectedUsername = userListView.getSelectionModel().getSelectedItem();
            handleUserSelection(selectedUsername, action3);
        });

        HBox buttonLayout = new HBox(12, action2Button , action3Button);
        buttonLayout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, userListView, buttonLayout);

        Scene scene = new Scene(layout, 900, 600);
        userListStage.setScene(scene);
        userListStage.show();
    }



    // iterators
    private void showUserList(String title, Iterable<User> users, Stage primaryStage, String action1Text, String action2Text
            ,String action3Text , UserAction action1, UserAction action2 , UserAction action3) {
        Stage userListStage = new Stage();
        userListStage.setTitle(title);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title + ":");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> userListView = new ListView<>();
        users.forEach(user -> userListView.getItems().add(user.getUserName()));

        if (userListView.getItems().isEmpty()) {
            userListView.getItems().add("No users found.");
        }

        Button action1Button = new Button(action1Text);
        Button action2Button = new Button(action2Text);
        Button action3Button = new Button(action3Text);

        action1Button.setOnAction(e -> {
            String selectedUsername = userListView.getSelectionModel().getSelectedItem();
            handleUserSelection(selectedUsername, action1);
        });

        action2Button.setOnAction(e -> {
            String selectedUsername = userListView.getSelectionModel().getSelectedItem();
            handleUserSelection(selectedUsername, action2);
        });

        action3Button.setOnAction(e -> {
            String selectedUsername = userListView.getSelectionModel().getSelectedItem();
            handleUserSelection(selectedUsername, action3);
        });

        HBox buttonLayout = new HBox(12, action1Button, action2Button , action3Button);
        buttonLayout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, userListView, buttonLayout);

        Scene scene = new Scene(layout, 900, 600);
        userListStage.setScene(scene);
        userListStage.show();
    }

    private void handleUserSelection(String selectedUsername, UserAction action) {
        if (selectedUsername == null || selectedUsername.equals("No users found.")) {
            showAlert(Alert.AlertType.WARNING, "Action", "Please select a valid user ");
        } else {
            User user = userSearchService.searchByKey(selectedUsername);
            if (user != null) {
                action.execute(loggedInUser, user);
            } else {
                showAlert(Alert.AlertType.ERROR, "Action", "No user found with this username ");
            }
        }
    }

    private void blockFollower(User myUser, User blockedUser) {
        if (!myUser.getRestrictedUsers().contains(blockedUser)) {
            userService.blockFollower(myUser, blockedUser);
            showAlert(Alert.AlertType.INFORMATION, "Block", "You have successfully blocked " + blockedUser.getUserName());
        } else {
            showAlert(Alert.AlertType.WARNING, "Block", "This user is already blocked ");
        }
    }

    private void removeFollower(User myUser, User removedFollower) {
        userService.removeFollower(myUser,removedFollower);
        showAlert(Alert.AlertType.INFORMATION, "Remove Follower", "Removed " + removedFollower.getUserName() + " from your followers ");
    }

    private void unfollow(User myUser, User unfollowedUser) {
        myUser.unfollow(unfollowedUser);
        showAlert(Alert.AlertType.INFORMATION, "Unfollow", "You have unfollowed " + unfollowedUser.getUserName());
    }

    private void acceptFollowRequest(User myUser, User requestingUser) {
        myUser.addFollower(requestingUser);
        myUser.getFollowRequests().remove(requestingUser);
        showAlert(Alert.AlertType.INFORMATION, "Accept Request", "Accepted follow request from " + requestingUser.getUserName());
    }

    private void rejectFollowRequest(User myUser, User requestingUser) {
        myUser.getFollowRequests().remove(requestingUser);
        showAlert(Alert.AlertType.INFORMATION, "Reject Request", "Rejected follow request from " + requestingUser.getUserName());
    }

    private void navigateToMainMenu(Stage primaryStage) {
        MainMenu mainMenu = new MainMenu(userService, loggedInUser);
        mainMenu.show(primaryStage);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FunctionalInterface
    private interface UserAction {
        void execute(User myUser, User targetUser);
    }
}

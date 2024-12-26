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

import java.util.List;

public class MainMenu {

    private final UserService userService;
    private final UserSearchService userSearchService;
    private final User loggedInUser;

    public MainMenu(UserService userService, User loggedInUser) {
        this.userService = userService;
        this.userSearchService = new UserSearchService();
        this.loggedInUser = loggedInUser;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Minor Space - Main Menu");

        // Top Search Bar
        HBox searchBarLayout = new HBox(10);
        searchBarLayout.setPadding(new Insets(10));
        searchBarLayout.setAlignment(Pos.CENTER);
        TextField searchField = new TextField();
        searchField.setPromptText("Search Users");
        Button searchButton = new Button("Search");

        searchBarLayout.getChildren().addAll(searchField, searchButton);

        // Main Menu Buttons
        VBox buttonsLayout = new VBox(15);
        buttonsLayout.setPadding(new Insets(20));
        buttonsLayout.setAlignment(Pos.CENTER);

        Button writePostButton = new Button("Write a Post");
        Button connectButton = new Button("Connect");
        Button fypButton = new Button("FYP");
        Button chatButton = new Button("Chat");
        Button communitiesButton = new Button("Community");
        Button logoutButton = new Button("Logout");
        Button myAccountButton = new Button("My Account");

        buttonsLayout.getChildren().addAll(
                writePostButton, fypButton, connectButton, chatButton, communitiesButton, myAccountButton, logoutButton
        );

        // Set Theme Styles
        setButtonStyle(writePostButton, connectButton, fypButton, chatButton, communitiesButton, logoutButton, myAccountButton);
        searchField.setStyle("-fx-font-size: 14px; -fx-border-color: #30393B; -fx-border-radius: 5px;");
        searchButton.setStyle("-fx-background-color: #303934; -fx-text-fill: white; -fx-font-size: 14px;");

        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(15));
        mainLayout.getChildren().addAll(searchBarLayout, buttonsLayout);
        mainLayout.setStyle("-fx-background-color: #FFFFFF;");

        // Search Action
        searchButton.setOnAction(e -> {
            String userName = searchField.getText();
            User targetUser = userSearchService.searchByKey(userName);

            if (targetUser != null) {
                showAlert(Alert.AlertType.INFORMATION, "User Found", "Username: " + targetUser.getUserName() + "\n" +
                        "ID: " + targetUser.getData().getId() + "\n" +
                        "Bio: " + targetUser.getData().getDescription() + "\n" +
                        "Followers: " + targetUser.getFollowers().size() + "\n" +
                        "Following: " + targetUser.getFollowing().size());
                displayUserOptions(targetUser);
            } else {
                List<String> suggestions = userSearchService.searchByPrefix(userName);
                if (!suggestions.isEmpty()) {
                    StringBuilder suggestionMessage = new StringBuilder("Did you mean:\n");
                    for (String suggestion : suggestions) {
                        suggestionMessage.append("- ").append(suggestion).append("\n");
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Did You Mean ", suggestionMessage.toString());
                } else {
                    showAlert(Alert.AlertType.ERROR, "User Not Found", "No user found with username: " + userName);
                }
            }
        });


        // Button Actions
        writePostButton.setOnAction(e -> {
            PostCreator postCreator = new PostCreator(loggedInUser);
            postCreator.show(primaryStage);
        });
        connectButton.setOnAction(e -> navigateToConnections(primaryStage, "Connect"));
        fypButton.setOnAction(e -> navigateToFyp(primaryStage, "FYP"));

        // Chat Button Action - Open the chat window with users you follow
        chatButton.setOnAction(e -> {
            navigateToChat(primaryStage,"Chat");
        });

        communitiesButton.setOnAction(e -> navigateToCommunities(primaryStage));
        logoutButton.setOnAction(e -> {
            primaryStage.close();
            navigateToUserApp(primaryStage);
        });
        myAccountButton.setOnAction(e -> navigateToMyAccount(primaryStage));

        // Set Scene
        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void navigateToCommunities(Stage primaryStage) {
        AllCommunitiesView allCommunitiesView = new AllCommunitiesView(loggedInUser);
        allCommunitiesView.show(primaryStage);
    }

    private void displayUserOptions(User targetUser) {
        Stage optionsStage = new Stage();
        optionsStage.setTitle("User Options - " + targetUser.getUserName());

        VBox optionsLayout = new VBox(15);
        optionsLayout.setPadding(new Insets(20));
        optionsLayout.setAlignment(Pos.CENTER);

        Button followButton = new Button("Follow");
        Button bothFollowButton = new Button("You Both Follow");
        Button bothFollowedByButton = new Button("You Both Are Followed By");

        optionsLayout.getChildren().addAll(followButton, bothFollowButton, bothFollowedByButton);

        // Follow Button Action
        followButton.setOnAction(e -> {
            if (loggedInUser.getFollowing().contains(targetUser)) {
                showAlert(Alert.AlertType.WARNING, "Follow User", "You are already following " + targetUser.getUserName());
            }
            else if(targetUser.getFollowRequests().contains(loggedInUser)){
                showAlert(Alert.AlertType.WARNING, "Follow User", "You already sent a follow request " + targetUser.getUserName());
            }
            else if (loggedInUser.equals(targetUser)) {

                showAlert(Alert.AlertType.WARNING, "Follow User", "You Can't follow yourself " + targetUser.getUserName());
            } else {
                userService.follow(loggedInUser, targetUser);

                showAlert(Alert.AlertType.INFORMATION, "Follow User", "You are now following " + targetUser.getUserName());

            }
        });

        // Both Follow Button Action
        bothFollowButton.setOnAction(e -> {
            userService.YouBothFollow(loggedInUser, targetUser);
        });

        // Both Are Followed By Button Action
        bothFollowedByButton.setOnAction(e -> {
            userService.YouBothAreFollowedBy(loggedInUser, targetUser);
        });

        Scene optionsScene = new Scene(optionsLayout, 400, 300);
        optionsStage.setScene(optionsScene);
        optionsStage.show();
    }

    private void navigateToUserApp(Stage primaryStage) {
        WelcomeMenu welcomeMenu = new WelcomeMenu();
        welcomeMenu.start(primaryStage);
    }

    private void navigateToFyp(Stage primaryStage ,String name) {
        Fyp fyp = new Fyp(loggedInUser);
        fyp.show(primaryStage);
    }

    private void setButtonStyle(Button... buttons) {
        for (Button button : buttons) {
            button.setStyle("-fx-background-color: #363545; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 10px;");
        }
    }

    private void navigateToChat(Stage primaryStage , String windowTitle) {
        ChatGui chat = new ChatGui(loggedInUser);
        chat.show(primaryStage);
    }


    private void navigateToConnections(Stage primaryStage , String windowTitle) {
        Connections connections = new Connections(userService, loggedInUser);
        connections.show(primaryStage);
    }

    private void navigateToMyAccount(Stage primaryStage) {
        MyAccount myAccountWindow = new MyAccount(userService, loggedInUser);
        myAccountWindow.show(primaryStage);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

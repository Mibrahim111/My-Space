package com.utils.gui;

import com.mySpace.user.models.User;
import com.mySpace.post.services.PostService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PostCreator {

    private final PostService postService;
    private final User loggedInUser;
    private final List<User> taggedUsers = new ArrayList<>();

    public PostCreator(User loggedInUser) {
        this.postService = PostService.getInstance();
        this.loggedInUser = loggedInUser;
    }


    public void show(Stage primaryStage) {
        Dialog<Pair<String, String>> postDialog = new Dialog<>();
        postDialog.setTitle("Write a Post");
        postDialog.setHeaderText("Create a new post");


        Label contentLabel = new Label("Post Content:");
        TextArea postContentArea = new TextArea();
        postContentArea.setWrapText(true);

        Label privacyLabel = new Label("Privacy:");
        ToggleGroup privacyGroup = new ToggleGroup();
        RadioButton publicOption = new RadioButton("Public");
        publicOption.setToggleGroup(privacyGroup);
        publicOption.setSelected(true);
        RadioButton privateOption = new RadioButton("Private");
        privateOption.setToggleGroup(privacyGroup);

        Button tagUsersButton = new Button("Tag Users");
        tagUsersButton.setOnAction(e -> navigateToFollowers(primaryStage));

        VBox contentLayout = new VBox(10, contentLabel, postContentArea, privacyLabel, publicOption, privateOption, tagUsersButton);
        contentLayout.setPadding(new Insets(10));

        postDialog.getDialogPane().setContent(contentLayout);
        postDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle OK button action
        postDialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String content = postContentArea.getText().trim();
                String privacy = ((RadioButton) privacyGroup.getSelectedToggle()).getText();
                return new Pair<>(content, privacy);
            }
            return null;
        });

        // Show the dialog and create the post if valid
        postDialog.showAndWait().ifPresent(result -> {
            String content = result.getKey();
            String privacy = result.getValue();

            if (content.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Post Creation Failed", "Post content cannot be empty.");
            } else {
                boolean isPrivate = privacy.equalsIgnoreCase("Private");
                postService.createPost(content, loggedInUser, isPrivate, taggedUsers);
                showAlert(Alert.AlertType.INFORMATION, "Post Created", "Your post has been successfully created with " +
                        (isPrivate ? "Private" : "Public") + " privacy and tagged " + taggedUsers.size() + " users.");
            }
        });
    }


    private void navigateToFollowers(Stage primaryStage) {
        Stage tagStage = new Stage();
        tagStage.setTitle("Tag Users");

        ListView<User> followerList = new ListView<>();
        followerList.getItems().addAll(loggedInUser.getFollowers());

        followerList.setPlaceholder(new Label("No followers available"));

        Button tagButton = new Button("Tag Selected Users");
        tagButton.setOnAction(e -> {
            List<User> selectedUsers = new ArrayList<>(followerList.getSelectionModel().getSelectedItems());
            if (!selectedUsers.isEmpty()) {
                taggedUsers.clear();
                taggedUsers.addAll(selectedUsers);
                showAlert(Alert.AlertType.INFORMATION, "Tagging Successful", "Users tagged successfully!");
                tagStage.close();
            } else {
                showAlert(Alert.AlertType.WARNING, "Tagging Failed", "No users selected.");
            }
        });

        followerList.setOnMouseClicked(event -> {
            User selectedUser = followerList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                viewUserData(selectedUser);
            }
        });

        VBox layout = new VBox(10, new Label("Select users to tag:"), followerList, tagButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 300);
        tagStage.setScene(scene);
        tagStage.show();
    }


    private void viewUserData(User user) {
        showAlert(Alert.AlertType.INFORMATION, "User Info", "User: " + user.getUserName() + "\n" +
                "Followers: " + user.getFollowers().size() + "\n" +
                "Following: " + user.getFollowing().size() + "\n" +
                "Bio: " + user.getData().getDescription());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

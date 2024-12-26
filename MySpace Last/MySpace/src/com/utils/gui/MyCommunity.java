package com.utils.gui;

import com.mySpace.communities.Community;
import com.mySpace.post.models.Post;
import com.mySpace.user.models.User;
import com.mySpace.user.services.UserSearchService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MyCommunity {

    private final Community community;
    private final User loggedInUser;
    private final UserSearchService userSearchService;

    public MyCommunity(Community community, User loggedInUser) {
        this.community = community;
        this.loggedInUser = loggedInUser;
        this.userSearchService = new UserSearchService();
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Community: " + community.getName());

        Label communityNameLabel = new Label("Community: " + community.getName());

        ListView<String> postsListView = new ListView<>();
        refreshPosts(postsListView);

        TextField postContentField = new TextField();
        postContentField.setPromptText("Write your post");
        Button addPostButton = new Button("Post");
        Button backButton = new Button("Back");
        addPostButton.setOnAction(e -> {
            String content = postContentField.getText().trim();
            if (!content.isEmpty()) {
                if(!community.isMember(loggedInUser.getUserName())){
                    showAlert(Alert.AlertType.WARNING, "Not A Member", " You Can't Post in " + community.getName() + " unless you are member");
                }
                    else{
                community.addPost(content, loggedInUser, false, new ArrayList<>()); // Add post to community
                refreshPosts(postsListView);
                postContentField.clear();}
            }
        });

        backButton.setOnAction(e -> navigateToComms(primaryStage));

        TextField memberField = new TextField();
        memberField.setPromptText("Enter username to add...");
        Button addMemberButton = new Button("Add Member");

        addMemberButton.setOnAction(e -> {
            String username = memberField.getText().trim();
            if (!username.isEmpty()) {
                User newMember = userSearchService.searchByKey(username);
                if (newMember != null && !community.isMember(newMember.getUserName())) {
                    community.addMember(newMember);
                    showAlert(Alert.AlertType.INFORMATION, "Add Member", username + " added to the community.");
                    memberField.clear();
                } else if (!community.isMember(newMember.getUserName())) {
                    showAlert(Alert.AlertType.WARNING, "Already Exists", username + " already a member in the community.");
                } else {
                    List<String> suggestions = userSearchService.searchByPrefix(username);
                    if (!suggestions.isEmpty()) {
                        StringBuilder suggestionMessage = new StringBuilder("Did you mean:\n");
                        for (String suggestion : suggestions) {
                            suggestionMessage.append("- ").append(suggestion).append("\n");
                        }
                        showAlert(Alert.AlertType.INFORMATION, "Did You Mean", suggestionMessage.toString());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "User Not Found", "No user found with username: " + username);
                    }
                }
            }
        });


        Button leaveCommunityButton = new Button("Leave Community");
        leaveCommunityButton.setOnAction(e -> {
            if (community.isMember(loggedInUser.getUserName())) {
                community.removeMember(loggedInUser);
                showAlert(Alert.AlertType.INFORMATION, "Leave Community", "You have left the community.");
                navigateToComms(primaryStage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "You are not a member of this community.");
            }
        });

        // View All Members Button
        Button viewAllMembersButton = new Button("View All Members");
        viewAllMembersButton.setOnAction(e -> {
            List<User> members = community.getMembers().stream().toList();
            StringBuilder membersList = new StringBuilder("Members of " + community.getName() + ":\n");
            for (User member : members) {
                membersList.append("- ").append(member.getUserName()).append("\n");
            }
            showAlert(Alert.AlertType.INFORMATION, "Community Members", membersList.toString());
        });

        VBox layout = new VBox(10, communityNameLabel,
                new Label("Posts:"), postsListView, postContentField, addPostButton,
                new Label("Add Member:"), memberField, addMemberButton,
                leaveCommunityButton, viewAllMembersButton, backButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void navigateToComms(Stage primaryStage) {
        AllCommunitiesView menu = new AllCommunitiesView(loggedInUser);
        menu.show(primaryStage);
    }

    private void refreshPosts(ListView<String> postsListView) {
        postsListView.getItems().clear();
        for (Post post : community.getPosts()) {
            postsListView.getItems().add("Post by " + post.getAuthor().getUserName() + ": " + post.getContent());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

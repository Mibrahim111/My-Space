package com.utils.gui;

import com.mySpace.post.models.Comment;
import com.mySpace.post.models.Post;
import com.mySpace.user.models.User;
import com.mySpace.post.services.PostService;
import com.mySpace.user.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Fyp {

    private final PostService postService;
    private final User loggedInUser;

    public Fyp(User loggedInUser) {
        this.postService = PostService.getInstance();
        this.loggedInUser = loggedInUser;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("For Your Page");

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        // ListView to display posts
        ListView<Post> postListView = new ListView<>();
        postListView.setPlaceholder(new Label("No posts available"));
        ObservableList<Post> postList = FXCollections.observableArrayList(postService.getPostRepository().getMyFyp(loggedInUser));
        postListView.setItems(postList);

        // Sorting ComboBox
        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("Sort by Relevance", "Sort by Date (Oldest)", "Sort by Date (Newest)");
        sortComboBox.setValue("Sort by Relevance"); // Default sorting

        // Update the ListView based on the selected sort option
        sortComboBox.setOnAction(e -> {
            String selectedSort = sortComboBox.getValue();
            List<Post> sortedPosts;
            if (selectedSort.equals("Sort by Date (Oldest)")) {
                sortedPosts = postList.stream()
                        .sorted(Comparator.comparing(Post::getTimestamp))
                        .collect(Collectors.toList());
            } else if (selectedSort.equals("Sort by Date (Newest)")) {
                sortedPosts = postList.stream()
                        .sorted(Comparator.comparing(Post::getTimestamp).reversed())
                        .collect(Collectors.toList());
            } else {
                sortedPosts = postService.getPostRepository().getMyFyp(loggedInUser);
            }
            postList.setAll(sortedPosts);
        });

        // Post selection to view thread
        postListView.setOnMouseClicked(event -> {
            Post selectedPost = postListView.getSelectionModel().getSelectedItem();
            if (selectedPost != null) {
                showPostThread(primaryStage, selectedPost, postListView);
            }
        });

        // Refresh button
        Button refreshButton = new Button("Refresh Posts");
        refreshButton.setOnAction(e -> {
            postList.setAll(postService.getPostRepository().getMyFyp(loggedInUser));
            sortComboBox.setValue("Sort by FYP"); // Reset sort option to default
        });

        // Back button
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu(UserService.getInstance(), loggedInUser);
            mainMenu.show(primaryStage);
        });

        // Layout setup
        VBox listContainer = new VBox(10, sortComboBox, postListView, refreshButton, backButton);
        layout.setCenter(listContainer);

        // Scene setup
        Scene mainScene = new Scene(layout, 900, 600);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void showPostThread(Stage primaryStage, Post post, ListView<Post> postListView) {
        primaryStage.setTitle("Post Thread");

        // Layout
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        // Display post details
        Label postContentLabel = new Label("Post: " + post.getContent());
        Label postAuthorLabel = new Label("Author: " + post.getAuthor().getUserName());
        Label taggedLabel = new Label("Tagged Users: " + post.getTaggedUsers());

        // Comments ListView
        ListView<Comment> commentListView = new ListView<>();
        commentListView.setPlaceholder(new Label("No comments yet"));
        commentListView.getItems().addAll(post.getComments());

        // Like comment button
        Button likeCommentButton = new Button("Like Comment");
        likeCommentButton.setOnAction(e -> {
            Comment selectedComment = commentListView.getSelectionModel().getSelectedItem();
            if (selectedComment != null) {
                postService.likeComment(selectedComment, loggedInUser);
                showAlert(Alert.AlertType.INFORMATION, "Liked", "You liked the comment by: " + selectedComment.getAuthor().getUserName());
                commentListView.refresh();
            } else {
                showAlert(Alert.AlertType.WARNING, "No Comment Selected", "Please select a comment to like.");
            }
        });

        // Comments section
        VBox commentsSection = new VBox(10, new Label("Comments"), commentListView, likeCommentButton);

        // Add comment section
        TextField commentField = new TextField();
        commentField.setPromptText("Write a comment...");
        Button addCommentButton = new Button("Comment");
        addCommentButton.setOnAction(e -> {
            String text = commentField.getText().trim();
            if (!text.isEmpty()) {
                postService.addComment(post, text, loggedInUser);
                commentListView.getItems().clear();
                commentListView.getItems().addAll(post.getComments());
                commentField.clear();
            }
        });

        // Reply to comment section
        Button replyButton = new Button("View Replies");
        replyButton.setOnAction(e -> {
            Comment selectedComment = commentListView.getSelectionModel().getSelectedItem();
            if (selectedComment != null) {
                RepliesView repliesView = new RepliesView(selectedComment, loggedInUser);
                repliesView.show(primaryStage);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Comment Selected", "Please select a comment to view replies.");
            }
        });

        // Like and repost section
        Button likeButton = new Button("Like");
        likeButton.setOnAction(e -> {
            postService.addLikeToPost(post, loggedInUser);
            updateLikeButton(likeButton, post);
        });

        Button repostButton = new Button("Repost");
        repostButton.setOnAction(e -> {
            postService.repost(post, loggedInUser);
            showAlert(Alert.AlertType.INFORMATION, "Reposted", "You have successfully reposted this post.");
            postListView.getItems().clear();
            postListView.getItems().addAll(postService.getPostRepository().getMyFyp(loggedInUser));
        });

        // Back button
        Button backButton = new Button("Back to FYP");
        backButton.setOnAction(e -> show(primaryStage));

        // Bottom button section
        HBox buttonsBox = new HBox(10, likeButton, addCommentButton, replyButton, repostButton, backButton);
        buttonsBox.setPadding(new Insets(10));

        // Layout setup
        VBox postDetails = new VBox(10, postContentLabel, postAuthorLabel, taggedLabel);
        VBox mainSection = new VBox(10, commentsSection, commentField, buttonsBox);
        layout.setTop(postDetails);
        layout.setCenter(mainSection);

        // Scene setup
        Scene threadScene = new Scene(layout, 600, 500);
        primaryStage.setScene(threadScene);

        // Update like button state
        updateLikeButton(likeButton, post);
    }

    private void updateLikeButton(Button likeButton, Post post) {
        if (post.getLikeUser() != null && post.getLikeUser().contains(loggedInUser)) {
            likeButton.setText("Liked");
        } else {
            likeButton.setText("Like");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

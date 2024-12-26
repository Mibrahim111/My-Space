package com.utils.gui;
import com.mySpace.post.models.Comment;
import com.mySpace.user.models.User;
import com.mySpace.post.services.PostService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RepliesView {

    private final PostService postService = PostService.getInstance();
    private final Comment comment;
    private final User loggedInUser;

    public RepliesView(Comment comment, User loggedInUser) {
        this.comment = comment;
        this.loggedInUser = loggedInUser;
    }

    public void show(Stage parentStage) {
        Stage repliesStage = new Stage();
        repliesStage.setTitle("Replies to Comment");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));


        Label commentLabel = new Label("Comment: " + comment.getText());
        commentLabel.setWrapText(true);


        ListView<Comment> repliesListView = new ListView<>();
        refreshRepliesList(repliesListView);

        repliesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Comment reply, boolean empty) {
                super.updateItem(reply, empty);
                if (empty || reply == null) {
                    setText(null);
                } else {
                    setText(reply.getAuthor().getData().getName() + ": " + reply.getText());
                }
            }
        });

        // Add Reply Field
        TextField replyField = new TextField();
        replyField.setPromptText("Write a reply...");

        Button addReplyButton = new Button("Reply");
        addReplyButton.setOnAction(e -> {
            String replyText = replyField.getText().trim();
            if (!replyText.isEmpty()) {
                postService.addReply(comment, replyText, loggedInUser);
                refreshRepliesList(repliesListView);
                replyField.clear();
            }
        });

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> repliesStage.close());

        // Add components to layout
        root.getChildren().addAll(commentLabel, repliesListView, replyField, addReplyButton, closeButton);

        // Set up the scene
        Scene scene = new Scene(root, 400, 500);
        repliesStage.setScene(scene);

        // Show the replies window
        repliesStage.initOwner(parentStage); // Parent reference for modal behavior
        repliesStage.show();
    }

    private void refreshRepliesList(ListView<Comment> repliesListView) {
        repliesListView.setItems(FXCollections.observableArrayList(comment.getReplies()));
    }
}
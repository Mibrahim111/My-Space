package com.utils.gui;

import com.mySpace.communities.Community;
import com.mySpace.communities.CommunitySearchService;
import com.mySpace.communities.CommunityServices;
import com.mySpace.communities.Visibility;
import com.mySpace.user.models.User;
import com.mySpace.user.services.UserService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AllCommunitiesView {

    private final CommunityServices communityServices;
    private final CommunitySearchService communitySearchService;
    private final UserService userService = UserService.getInstance();
    private final User loggedInUser;

    public AllCommunitiesView(User loggedInUser) {
        this.communityServices = new CommunityServices();
        this.communitySearchService = new CommunitySearchService();
        this.loggedInUser = loggedInUser;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Communities");

        ListView<String> communityListView = new ListView<>();
        communityListView.setPlaceholder(new Label("No communities available"));

        refreshCommunityList(communityListView);

        // Search Bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search communities...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                refreshCommunityList(communityListView); // Show all communities
            } else {
                searchCommunitiesByName(newValue, communityListView);
            }
        });

        Button createCommunityButton = new Button("Create New Community");
        createCommunityButton.setOnAction(e -> createCommunity(communityListView));

        Button viewCommunityButton = new Button("View Community");
        viewCommunityButton.setOnAction(e -> {
            String selectedCommunityName = communityListView.getSelectionModel().getSelectedItem();
            if (selectedCommunityName != null) {
                Community selectedCommunity = communityServices.findCommunityByName(selectedCommunityName);
                if (selectedCommunity != null) {
                    MyCommunity detailsView = new MyCommunity(selectedCommunity, loggedInUser);
                    detailsView.show(primaryStage);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Community not found.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a community.");
            }
        });

        Button joinCommunityButton = new Button("Join Community");
        joinCommunityButton.setOnAction(e -> {
            String selectedCommunityName = communityListView.getSelectionModel().getSelectedItem();
            if (selectedCommunityName != null) {
                Community selectedCommunity = communityServices.findCommunityByName(selectedCommunityName);
                if (selectedCommunity != null) {
                    if (selectedCommunity.isMember(loggedInUser.getUserName())) {
                        showAlert(Alert.AlertType.WARNING, "Join Community", "You are already a member of this community.");
                    } else {
                        selectedCommunity.addMember(loggedInUser);
                        showAlert(Alert.AlertType.INFORMATION, "Join Community", "You have successfully joined " + selectedCommunity.getName() + ".");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Community not found.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a community.");
            }
        });

        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu(userService, loggedInUser);
            mainMenu.show(primaryStage);
        });

        VBox layout = new VBox(10, searchField, communityListView, createCommunityButton, viewCommunityButton, joinCommunityButton, backButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createCommunity(ListView<String> communityListView) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Community");
        dialog.setHeaderText("Enter Community Name");

        dialog.showAndWait().ifPresent(name -> {
            Community community = communityServices.createCommunity(name, Visibility.VISIBLE);
            community.addMember(loggedInUser);
            showAlert(Alert.AlertType.INFORMATION, "Create Community", name);
            refreshCommunityList(communityListView);
        });
    }

    private void refreshCommunityList(ListView<String> communityListView) {
        communityListView.getItems().clear();
        List<Community> communities = communityServices.listAllCommunities();
        for (Community community : communities) {
            communityListView.getItems().add(community.getName());
        }
    }

    private void searchCommunitiesByName(String query, ListView<String> communityListView) {
        communityListView.getItems().clear();
        List<String> searchResults = communitySearchService.searchByPrefix(query);
        if (searchResults.isEmpty()) {
            communityListView.setPlaceholder(new Label("No communities found"));
        } else {
            communityListView.getItems().addAll(searchResults);
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

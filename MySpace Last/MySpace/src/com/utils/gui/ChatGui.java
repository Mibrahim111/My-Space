package com.utils.gui;

import com.mySpace.chat.Chat;
import com.mySpace.chat.ChatController;
import com.mySpace.chat.GroupChat;
import com.mySpace.chat.Message;
import com.mySpace.user.models.User;
import com.mySpace.user.services.UserService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class ChatGui {
    private final Map<String, List<String>> chatHistory = new HashMap<>();
    private final ChatController chatController;
    private User loggedInUser;
    private User selectedUser;
    private Chat selectedChat;
    private TextArea messageHistoryArea;
    private TextField messageInputField;
    private Button sendMessageButton;
    private Button backButton;
    private Button addGroupButton;
    private Label chatStatusLabel; //
    private VBox chatViewPane = createChatViewPane();
    Button manageMembersButton = new Button("Manage Group Members");

    // Store the group chats and direct message chats
    private final List<Chat> groupChats = new ArrayList<>();
    private final List<Chat> directMessageChats = new ArrayList<>();

    public ChatGui(User loggedInUser) {
        if (loggedInUser == null) {
            throw new IllegalArgumentException("Logged-in user cannot be null.");
        }
        this.loggedInUser = loggedInUser;
        this.chatController = new ChatController(loggedInUser);
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Chat Application");

        // Main Layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Add back button to the top of the layout
        backButton = new Button("Back to Main Menu");
        backButton.setStyle("-fx-background-color: #3399ff; -fx-text-fill: white;");
        backButton.setOnAction(e -> returnToMainMenu(primaryStage));

        // Create "Add Group" button
        addGroupButton = new Button("Make Group");
        addGroupButton.setStyle("-fx-background-color: #3399ff; -fx-text-fill: white;");

        // Create ListView for Group Chats (to display the group list)
        ListView<Chat> groupListView = new ListView<>();
        groupListView.setPrefWidth(200);  // Set the preferred width for the list view

        // Add existing group chats to the list view
        groupListView.getItems().addAll(groupChats);

        // Handle Group Chat Selection
        groupListView.getSelectionModel().selectedItemProperty().addListener((obs, oldChat, newChat) -> {
            if (newChat != null) {
                selectedChat = newChat;
                updateChatView(newChat);  // Update the chat view for the selected group
            }
        });

        addGroupButton.setOnAction(e -> handleAddGroup(groupListView));  // Pass the ListView to the handler to refresh the list

        // Create Left Panel for Chats and Groups
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-background-color: #F4F4F4;");

        // Create a ScrollPane for the left panel to handle overflow in chats/groups
        ScrollPane leftScrollPane = new ScrollPane();
        leftScrollPane.setContent(leftPanel);
        leftScrollPane.setFitToHeight(true);
        leftScrollPane.setFitToWidth(true);

        // Chats Section
        VBox chatSection = createChatSection(); // Chat Section
        leftPanel.getChildren().add(new Label("Chats"));
        leftPanel.getChildren().add(chatSection);

        // Groups Section
        VBox groupSection = createGroupSection(); // Group Section
        leftPanel.getChildren().add(new Label("Groups"));
        leftPanel.getChildren().add(groupSection);

        // Create Chat View Pane
        VBox chatViewPane = createChatViewPane();  // The pane that displays the chat
        mainLayout.setLeft(leftScrollPane);  // Set the left pane as a ScrollPane
        mainLayout.setCenter(chatViewPane);

        // Setup Scene and Show Stage
        VBox topLayout = new VBox(14, backButton, addGroupButton);  // Add "Add Group" and "Back" buttons to top layout
        mainLayout.setTop(topLayout); // Set the buttons at the top
        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openChatWithSelectedUser(User user) {
        // Fetch or create a chat with the selected user
        Chat chat = chatController.getOrCreateChat(loggedInUser, user);

        if (chat != null) {
            // Load the saved chat for the logged-in user
            chatController.loadChat(loggedInUser);

            // Load the chat for the selected user
            chatController.loadChat(user);

            // Update the chat view (e.g., populate message history area)
            updateChatView(chat);
        } else {
            showAlert("Failed to open chat with this user.");
        }
    }

    private VBox createChatSection() {
        VBox chatSection = new VBox(10);
        chatSection.setStyle("-fx-background-color: #F4F4F4;");

        Label chatTitle = new Label("Chats");
        chatTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // ListView for Direct Message Chats
        ListView<User> chatListView = new ListView<>();
        chatListView.setPrefWidth(200);

        // Get the users who follow each other (mutual followers)
        List<User> mutualFollowers = UserService.getInstance().FollowsMeBack(loggedInUser);
        chatListView.getItems().addAll(mutualFollowers);

        // Handle User Selection for Chat
        chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> {
            if (newUser != null) {
                selectedUser = newUser;
                openChatWithSelectedUser(newUser);
            }
        });

        chatSection.getChildren().addAll(chatTitle, chatListView);
        return chatSection;
    }

    private VBox createGroupSection() {
        VBox groupSection = new VBox(10);
        groupSection.setStyle("-fx-background-color: #F4F4F4;");

        Label groupTitle = new Label("Groups");
        groupTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // ListView for Group Chats
        ListView<Chat> groupListView = new ListView<>();
        groupListView.setPrefWidth(200);

        // Add the existing group chats to the list view
        groupListView.getItems().addAll(groupChats);  // Assuming groupChats is a list of GroupChat objects

        // Handle Group Chat Selection
        groupListView.getSelectionModel().selectedItemProperty().addListener((obs, oldChat, newChat) -> {
            if (newChat != null) {
                selectedChat = newChat;
                updateChatView(newChat);  // Update the chat view for the selected group
            }
        });

        // Add a listener to the "Add Group" button to create a new group and update the list
        addGroupButton.setOnAction(e -> handleAddGroup(groupListView));  // Pass groupListView to refresh the list

        groupSection.getChildren().addAll(groupTitle, groupListView);
        return groupSection;
    }

    private VBox createChatViewPane() {
        VBox chatViewPane = new VBox(10);
        chatViewPane.setPadding(new Insets(10));
        chatViewPane.setStyle("-fx-background-color: #FFFFFF;");

        // Add a label to display the chat status
        chatStatusLabel = new Label("No chat selected");
        chatStatusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        messageHistoryArea = new TextArea();
        messageHistoryArea.setEditable(false);
        messageHistoryArea.setWrapText(true);

        messageInputField = new TextField();
        messageInputField.setPrefWidth(600);

        sendMessageButton = new Button("Send");
        sendMessageButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        sendMessageButton.setOnAction(e -> sendMessage());

        HBox inputPane = new HBox(10, messageInputField, sendMessageButton);
        chatViewPane.getChildren().addAll(chatStatusLabel, messageHistoryArea, inputPane);

        return chatViewPane;
    }

    private void updateChatView(Chat chat) {
        // Clear the previous messages from the message history area
        messageHistoryArea.clear();

        // Check if the selected chat is a GroupChat
        if (chat instanceof GroupChat) {
            GroupChat groupChat = (GroupChat) chat;
            chatStatusLabel.setText("Chatting in Group: " + groupChat.getGroupName());

            // Display group name at the top of the chat view
            messageHistoryArea.appendText("Group: " + groupChat.getGroupName() + "\n");

            // Display all messages in the group chat
            for (Message message : groupChat.getMessages()) {
                messageHistoryArea.appendText(message.getSender().getData().getName() + ": " + message.getContent() + "\n");
            }

            // Update the send button action to handle group chat messages
            sendMessageButton.setOnAction(e -> {
                String messageContent = messageInputField.getText().trim();
                if (!messageContent.isEmpty()) {
                    // Create and send a new message
                    Message newMessage = new Message(messageContent, loggedInUser); // loggedInUser is the sender
                    groupChat.addMessage(newMessage); // Add the new message to the group

                    // Update the chat view to show the new message
                    updateChatView(groupChat);
                    messageInputField.clear(); // Clear the input field
                }
            });
        } else {
            // Handle Direct Message (DM) chat
            // Assuming getParticipants() returns a Map<String, User>
            Map<String, User> participants = chat.getParticipants();

            // Find the other user in the direct message chat
            User otherUser = participants.entrySet().stream()
                    .filter(entry -> !entry.getValue().equals(loggedInUser)) // Filter out the logged-in user
                    .map(Map.Entry::getValue) // Get the user (not the name)
                    .findFirst() // Get the first (and only) other participant
                    .orElse(null); // Return null if no other user is found

            // Set the chat status label based on the other user
            if (otherUser != null) {
                chatStatusLabel.setText("Chatting with: " + otherUser.getData().getName());
            } else {
                chatStatusLabel.setText("Chatting with: Unknown User");
            }

            // Display all messages in the direct message chat
            for (Message message : chat.getMessages()) {
                messageHistoryArea.appendText(message.getSender().getData().getName() + ": " + message.getContent() + "\n");
            }

            // Update the send button action to handle direct message chat
            sendMessageButton.setOnAction(e -> {
                String messageContent = messageInputField.getText().trim();
                if (!messageContent.isEmpty()) {
                    // Create and send a new message
                    Message newMessage = new Message(messageContent, loggedInUser); // loggedInUser is the sender
                    chat.addMessage(newMessage); // Add the new message to the DM chat

                    // Update the chat view to show the new message
                    updateChatView(chat);
                    messageInputField.clear(); // Clear the input field
                }
            });
        }
    }

    private void sendMessage() {
        String messageContent = messageInputField.getText();

        if (messageContent != null && !messageContent.trim().isEmpty() && selectedUser != null) {
            // Use ChatController to get or create the chat
            Chat chat = chatController.getOrCreateChat(loggedInUser, selectedUser);

            if (chat != null) {
                // Send the message using the ChatController
                chatController.sendMessage(loggedInUser, selectedUser, messageContent);

                // Save the chat for both the logged-in user and the selected user
                chatController.saveChat(loggedInUser);
                chatController.saveChat(selectedUser);

                // Update the message history area with the sent message
                messageHistoryArea.appendText("You: " + messageContent + "\n");
                messageInputField.clear(); // Clear the input field after sending the message

            } else {
                showAlert("Error Failed to send the message. Could not create or fetch the chat.");
            }
        } else {
            showAlert("Error Message content cannot be empty.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void returnToMainMenu(Stage primaryStage) {
        // Close the current chat window
        primaryStage.close();

        // Instantiate MainMenu with UserService and logged-in User
        UserService userService = UserService.getInstance(); // Ensure UserService is available
        MainMenu mainMenu = new MainMenu(userService, loggedInUser);

        // Open the Main Menu
        mainMenu.show(new Stage());
    }

    private void handleAddGroup(ListView<Chat> groupListView) {
        // Prompt for the group name
        TextInputDialog groupNameDialog = new TextInputDialog();
        groupNameDialog.setTitle("Create Group");
        groupNameDialog.setHeaderText("Enter the group name:");
        groupNameDialog.setContentText("Group Name:");

        Optional<String> groupNameResult = groupNameDialog.showAndWait();

        if (groupNameResult.isPresent() && !groupNameResult.get().trim().isEmpty()) {
            String groupName = groupNameResult.get().trim();

            // Predefined list of users (or get them from any logic you want)
            List<User> availableUsers = getAvailableUsersForGroup();  // Your custom method to get the available users

            // Prompt to select users for the group
            List<User> selectedUsers = promptForGroupMembers(availableUsers);

            if (!selectedUsers.isEmpty()) {
                // Create the group chat
                GroupChat groupChat = new GroupChat(groupName, loggedInUser, selectedUsers);

                // Add the new group to the groupChats list and the ListView
                groupChats.add(groupChat);
                groupListView.getItems().add(groupChat);  // Update the ListView with the new group

                // Show success alert
                showAlert("Success: Group created successfully: " + groupName);
            } else {
                showAlert("Warning: No users selected for the group.");
            }
        } else {
            showAlert("Warning: Group name cannot be empty.");
        }
    }

    private List<User> getAvailableUsersForGroup() {
        // Assuming you have a UserService that can provide a list of users the logged-in user follows back
        List<User> followedBackUsers = UserService.getInstance().FollowsMeBack(loggedInUser);

        // You can modify the logic here to filter out users that the logged-in user shouldn't be able to invite.
        // For example, exclude the logged-in user from the list
        followedBackUsers.remove(loggedInUser);

        return followedBackUsers;
    }

    private List<User> promptForGroupMembers(List<User> availableUsers) {
        // Create a list of checkboxes for each user
        List<CheckBox> userCheckboxes = availableUsers.stream()
                .filter(user -> !user.equals(loggedInUser)) // Exclude the logged-in user
                .map(user -> new CheckBox(user.getUserName()))  // Assuming user has a getUserName() method
                .collect(Collectors.toList());

        // Create a dialog with checkboxes
        Dialog<List<User>> dialog = new Dialog<>();
        dialog.setTitle("Select Group Members");
        dialog.setHeaderText("Choose members for the group:");

        VBox content = new VBox(10);
        content.getChildren().addAll(userCheckboxes);
        dialog.getDialogPane().setContent(content);

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                // Get the selected users from the checkboxes
                List<User> selectedUsers = userCheckboxes.stream()
                        .filter(CheckBox::isSelected)
                        .map(cb -> availableUsers.get(userCheckboxes.indexOf(cb)))
                        .collect(Collectors.toList());

                return selectedUsers;
            }
            return null;
        });

        Optional<List<User>> result = dialog.showAndWait();
        return result.orElse(Collections.emptyList());
    }


}
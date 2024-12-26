package com.utils.gui;

import com.mySpace.user.models.Gender;
import com.mySpace.user.models.User;
import com.mySpace.user.services.UserService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MyAccount {

    private final UserService userService;
    private User loggedInUser;

    public MyAccount(UserService userService, User loggedInUser) {
        this.userService = userService;
        this.loggedInUser = loggedInUser;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("My Account");

        VBox accountLayout = new VBox(15);
        accountLayout.setPadding(new Insets(20));
        accountLayout.setStyle("-fx-background-color: #FFFFFF;");

        Button showDataButton = new Button("Show My Data");
        Button editAccountButton = new Button("Edit My Account");
        Button deleteAccountButton = new Button("Delete My Account");
        Button backButton = new Button("Back");

        accountLayout.getChildren().addAll(showDataButton, editAccountButton, deleteAccountButton ,backButton);

        // Set Button Styles
        setButtonStyle(showDataButton, editAccountButton, deleteAccountButton,backButton);

        // Button Actions
        showDataButton.setOnAction(e -> showUserData());
        editAccountButton.setOnAction(e -> updateUser(primaryStage));
        deleteAccountButton.setOnAction(e -> deleteUser(primaryStage));
        backButton.setOnAction(e -> {
            primaryStage.close();
            navigateToMainMenu(primaryStage); // Navigate to WelcomeMenu after logging out
        });

        Scene accountScene = new Scene(accountLayout, 900, 600);
        primaryStage.setScene(accountScene);
        primaryStage.show();
    }

    private void setButtonStyle(Button... buttons) {
        for (Button button : buttons) {
            button.setStyle("-fx-background-color: #363545; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 10px;");
        }
    }

    private void showUserData() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Data");
        alert.setHeaderText(null);
        alert.setContentText("Name: " + loggedInUser.getData().getName() + "\n" +
                "Username: " + loggedInUser.getUserName() + "\n" +
                "Email: " + loggedInUser.getData().getEmail() + "\n" +
                "Password: " + loggedInUser.getData().getPassword() + "\n" +
                "Bio: " + loggedInUser.getData().getDescription() + "\n" +
                "UserID: " + loggedInUser.getData().getId() + "\n" +
                "Birthday: " + loggedInUser.getData().getBirthday() + "\n" +
                "Gender: " + loggedInUser.getData().getGender());
        alert.showAndWait();
    }
    private void updateUser(Stage primaryStage) {
        // Create a form for updating user details
        TextField nameField = new TextField(loggedInUser.getData().getName());
        TextField emailField = new TextField(loggedInUser.getData().getEmail());
        TextField bioField = new TextField(loggedInUser.getData().getDescription());
        TextField passwordField = new TextField(loggedInUser.getData().getPassword());
        passwordField.setPromptText("Enter new password");
        DatePicker birthDateField = new DatePicker(loggedInUser.getData().getBirthday());
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female");
        genderComboBox.setValue(loggedInUser.getData().getGender().toString());




        Button saveButton = new Button("Save Changes");
        Button cancelButton = new Button("Cancel");


        saveButton.setOnAction(e -> {
            updateUserDetails(nameField, emailField, bioField, passwordField,birthDateField.getValue(), genderComboBox);
            showAlert(Alert.AlertType.INFORMATION, "Account Updated", "Your account information has been successfully updated!");

        });

        cancelButton.setOnAction(e -> navigateToMainMenu(primaryStage));


        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(20));
        formLayout.getChildren().addAll(
                new Label("Name"), nameField,
                new Label("Email"), emailField,
                new Label("Bio"), bioField,
                new Label("Password"), passwordField,
                new Label("Birthday (yyyy-mm-dd)"),birthDateField,
                new Label("Gender"), genderComboBox,

                saveButton, cancelButton
        );
        Scene updateScene = new Scene(formLayout, 900, 600);
        primaryStage.setScene(updateScene);
        primaryStage.show();
    }
    private void navigateToMainMenu(Stage primaryStage) {
        MainMenu mainMenu = new MainMenu(userService, loggedInUser);
        mainMenu.show(primaryStage);
    }

    private void updateUserDetails(TextField nameField, TextField emailField, TextField bioField,
                                   TextField passwordField, LocalDate birthdayField,
                                   ComboBox<String> genderComboBox) {
        // Update user data based on the form input
        String newName = nameField.getText();
        String newEmail = emailField.getText();
        String newBio = bioField.getText();
        String newPassword = passwordField.getText();
        LocalDate newBirthday = birthdayField;
        String newGender = genderComboBox.getValue();


        // Update the logged-in user's data
        loggedInUser = userService.updateUser(loggedInUser,newEmail,newPassword,newName, Gender.fromString(newGender),newBirthday,
                newBio,loggedInUser.getData().getId());

        // Confirmation message
        showAlert(Alert.AlertType.INFORMATION, "Account Updated", "Your account information has been successfully updated!");
    }

    private void deleteUser(Stage primaryStage) {
        // Confirm before deleting the account
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Delete Account");
        confirmDelete.setHeaderText(null);
        confirmDelete.setContentText("Are you sure you want to delete your account?");

        confirmDelete.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.deleteUser(loggedInUser);
                primaryStage.close();
                showAlert(Alert.AlertType.INFORMATION, "Account Deleted", "Your account has been deleted successfully.");
            }
        });
        {
            WelcomeMenu welcomeMenu = new WelcomeMenu();
            welcomeMenu.start(primaryStage);
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

package com.utils.gui;

import com.mySpace.user.models.Gender;
import com.mySpace.user.models.User;
import com.mySpace.user.services.UserService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class WelcomeMenu extends Application {

    private final UserService userService = UserService.getInstance();
    private final InputValidation inputValidation = new InputValidation();
    private User loggedInUser = null;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My Space");


        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #FFFFFF;");

        Button registerButton = new Button("Register User");
        Button loginButton = new Button("Login");
        Button exitButton = new Button("Exit"); // Exit button


        styleButton(registerButton);
        styleButton(loginButton);
        styleButton(exitButton);

        mainLayout.getChildren().addAll(registerButton, loginButton, exitButton); // Add Exit button to the layout
        Scene mainScene = new Scene(mainLayout, 700, 500);


        GridPane registerLayout = createRegisterLayout(primaryStage, mainScene);
        Scene registerScene = new Scene(registerLayout, 800, 500);


        GridPane loginLayout = createLoginLayout(primaryStage, mainScene);
        Scene loginScene = new Scene(loginLayout, 900, 500);


        registerButton.setOnAction(e -> primaryStage.setScene(registerScene));
        loginButton.setOnAction(e -> primaryStage.setScene(loginScene));


        exitButton.setOnAction(e -> {
            primaryStage.close();
            System.exit(0);
        });

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private GridPane createRegisterLayout(Stage primaryStage, Scene mainScene) {
        GridPane registerLayout = new GridPane();
        registerLayout.setPadding(new Insets(20));
        registerLayout.setHgap(10);
        registerLayout.setVgap(10);
        registerLayout.setStyle("-fx-background-color: #FFFFFF;");

        // Text fields and controls
        TextField nameField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField emailField = new TextField();
        DatePicker birthDateField = new DatePicker();
        TextArea bioField = new TextArea();
        bioField.setPrefRowCount(3);

        ComboBox<Gender> genderComboBox = new ComboBox<Gender>();
        genderComboBox.getItems().setAll(Gender.values());

        Button registerSubmit = new Button("Register");
        Button backToMainFromRegister = new Button("Back");

        styleButton(registerSubmit);
        styleButton(backToMainFromRegister);


        registerLayout.add(new Label("Name:"), 0, 0);
        registerLayout.add(nameField, 1, 0);
        registerLayout.add(new Label("Username:"), 0, 1);
        registerLayout.add(usernameField, 1, 1);
        registerLayout.add(new Label("Password:"), 0, 2);
        registerLayout.add(passwordField, 1, 2);
        registerLayout.add(new Label("Email:"), 0, 3);
        registerLayout.add(emailField, 1, 3);
        registerLayout.add(new Label("Birthdate:"), 0, 4);
        registerLayout.add(birthDateField, 1, 4);
        registerLayout.add(new Label("Bio:"), 0, 5);
        registerLayout.add(bioField, 1, 5);
        registerLayout.add(new Label("Gender:"), 0, 6);
        registerLayout.add(genderComboBox, 1, 6);
        registerLayout.add(registerSubmit, 0, 7);
        registerLayout.add(backToMainFromRegister, 1, 7);

        backToMainFromRegister.setOnAction(e -> primaryStage.setScene(mainScene));

        registerSubmit.setOnAction(e -> {
            String name = nameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            LocalDate birthDate = birthDateField.getValue();
            String bio = bioField.getText();
            Gender gender = genderComboBox.getValue();

            try {
                inputValidation.validateNonEmpty(name);
                inputValidation.validateNonEmpty(username);
                inputValidation.validatePassword(password);
                inputValidation.validateEmail(email);
                inputValidation.validateDate(birthDate.toString());
                inputValidation.validateNonEmpty(bio);

                if (gender == null) {
                    throw new IllegalArgumentException("Gender is required.");
                }

                User user = userService.registerUser(name, username, password, email, gender, birthDate, bio);
                if (user != null) {
                    showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Welcome, " + username);
                    loggedInUser = user;
                    navigateToMainMenu(primaryStage);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Registration Failed", "User already exists.");
                }

            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", ex.getMessage());
            }
        });

        return registerLayout;
    }

    private GridPane createLoginLayout(Stage primaryStage, Scene mainScene) {
        GridPane loginLayout = new GridPane();
        loginLayout.setPadding(new Insets(20));
        loginLayout.setHgap(10);
        loginLayout.setVgap(10);
        loginLayout.setStyle("-fx-background-color: #FFFFFF;");

        TextField loginUsernameField = new TextField();
        PasswordField loginPasswordField = new PasswordField();
        Button loginSubmit = new Button("Login");
        Button backToMainFromLogin = new Button("Back");

        styleButton(loginSubmit);
        styleButton(backToMainFromLogin);

        loginLayout.add(new Label("Username:"), 0, 0);
        loginLayout.add(loginUsernameField, 1, 0);
        loginLayout.add(new Label("Password:"), 0, 1);
        loginLayout.add(loginPasswordField, 1, 1);
        loginLayout.add(loginSubmit, 0, 2);
        loginLayout.add(backToMainFromLogin, 1, 2);

        backToMainFromLogin.setOnAction(e -> primaryStage.setScene(mainScene));

        loginSubmit.setOnAction(e -> {
            String username = loginUsernameField.getText();
            String password = loginPasswordField.getText();

            loggedInUser = userService.login(username, password);
            if (loggedInUser != null) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome back, " + loggedInUser.getUserName());
                navigateToMainMenu(primaryStage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        });

        return loginLayout;
    }

    private void navigateToMainMenu(Stage primaryStage) {
        MainMenu mainMenu = new MainMenu(userService, loggedInUser);
        mainMenu.show(primaryStage);
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #363545; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 10px;");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

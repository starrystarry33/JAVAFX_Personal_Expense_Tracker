package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import Services.UserService;
import application.Utilities.AlertHelper;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class RegisterController {

    private UserService userService = new UserService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordConfirmationField;

    @FXML
    private Button submitButton;
    
    private static final String EMAIL_REGEX = "^\\S+@\\S+\\.\\S+$";

    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return pattern.matcher(email).matches();
    }

    @FXML
    public void register(ActionEvent event) throws IOException {
        Window owner = submitButton.getScene().getWindow();

        if (usernameField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter your email");
            return;
        }
        
        if (!validateEmail(usernameField.getText())) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter a valid email");
            return;
        }

        if (passwordField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter your password");
            return;
        }

        if (passwordConfirmationField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter your password (confirm)");
            return;
        }

        if (!passwordField.getText().equals(passwordConfirmationField.getText())) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Passwords do not match");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean isExists = userService.isUsernameExists(username);

        if (isExists) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, owner, "Warning", "Username already taken. Please choose another one.");
            return;
        }
        
        // GmailUtil.sendEMail("thea.xiaoya@gmail.com","igcggcrqbfxbuwam"
        //         ,username,"Congratulations! Your registration has been successful.","Notification of successful registration");

        userService.create(username, password);
        Optional<ButtonType> result = AlertHelper.showAlertAndWait(Alert.AlertType.INFORMATION, owner, "Success", "Your registration completed successfully.");
        
        if (!result.isPresent()) {
            goToLogin((Stage) owner);
        } else if (result.get() == ButtonType.OK) {
        	goToLogin((Stage) owner);
        } else if (result.get() == ButtonType.CANCEL) {
            goToLogin((Stage) owner);
        }
       
    }

    @FXML
    public void goToLogin(ActionEvent event) throws IOException {
        goToLogin((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    private void goToLogin(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Scene scene = new Scene(loader.load(), 800, 500);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}
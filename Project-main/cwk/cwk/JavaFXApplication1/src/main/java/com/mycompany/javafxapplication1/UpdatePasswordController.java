package com.mycompany.javafxapplication1;

import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class UpdatePasswordController {

    @FXML private PasswordField passwordField;
    
    private DB myObj = new DB();
    private String loggedInUser;

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @FXML
    private void handleSaveChanges(ActionEvent event) {
        String newPassword = passwordField.getText();

        if (newPassword.isEmpty()) {
            showAlert("Input Error", "Password cannot be empty.");
        } else {
            try {
                // Call the updatePassword method from DB class
                myObj.updatePassword(loggedInUser, newPassword);

                // Close the current window
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.close();

                // Optionally, you could show a success message or redirect to another screen.
                showAlert("Success", "Password updated successfully.");
            } catch (SQLException ex) {
                showAlert("Database Error", "Failed to update password.");
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

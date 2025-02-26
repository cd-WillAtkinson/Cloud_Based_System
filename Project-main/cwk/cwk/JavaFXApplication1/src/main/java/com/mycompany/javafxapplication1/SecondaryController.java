package com.mycompany.javafxapplication1;

import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class SecondaryController {
    
    @FXML private TextField userTextField;
    @FXML private TableView<User> dataTableView;
    @FXML private Button updateAccountButton;
    @FXML private Button deleteUserButton;
    @FXML private Button openTerminalButton;
    @FXML private TextField customTextField;
    
    private final DB myObj = new DB();
    private ObservableList<User> data = FXCollections.observableArrayList();
    private String loggedInUser;
    private String loggedInUserRole;
    
    public void initialise(String[] credentials) {
        String username = credentials[0];
        String password = credentials[1];
        loadUserData();
    }
    
    @FXML
    private void switchToFileMod() {
        switchScene("CreateFile.fxml", "File Management");
    }
    
    @FXML
    private void switchToPrimary() {
        switchScene("primary.fxml", "Login");
    }
    
    @FXML
    private void refreshBtnHandler(ActionEvent event) {
        Stage primaryStage = (Stage) customTextField.getScene().getWindow();
        customTextField.setText((String) primaryStage.getUserData());
    }

    
    private void loadUserData() {
        dataTableView.getColumns().clear(); // Prevent duplicate columns
        try {
            data = myObj.getDataFromTable();
            TableColumn<User, String> userColumn = new TableColumn<>("User");
            userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
            
            TableColumn<User, String> roleColumn = new TableColumn<>("Role");
            roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
            
            dataTableView.setItems(data);
            dataTableView.getColumns().addAll(userColumn, roleColumn);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void updateAccount() {
        User selectedUser = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null && (loggedInUser.equals(selectedUser.getUser()) || "admin".equals(loggedInUserRole))) {
            switchScene("updatePassword.fxml", "Update Password");
        } else {
            showAlert("Unauthorized", "You can only update your own account unless you are an admin.");
        }
    }
    
    @FXML
    private void deleteUser() {
        User selectedUser = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null && "admin".equals(loggedInUserRole)) {
            try {
                myObj.deleteUser(selectedUser.getUser());
                loadUserData(); // Refresh data
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete user: " + e.getMessage());
            }
        } else {
            showAlert("Unauthorized", "Only admins can delete users.");
        }
    }
    
    @FXML
    private void openTerminal() {
        CommandCheckerWindow.openCommandChecker();
    }
    
    private void switchScene(String fxmlFile, String title) {
        try {
            Stage primaryStage = (Stage) userTextField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 640, 480));
            newStage.setTitle(title);
            newStage.show();
            primaryStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void setLoggedInUser(String username, String role) {
        this.loggedInUser = username;
        this.loggedInUserRole = role;
    }
}

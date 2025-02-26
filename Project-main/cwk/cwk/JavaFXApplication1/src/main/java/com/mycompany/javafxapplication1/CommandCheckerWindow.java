/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;

public class CommandCheckerWindow {
    
    @FXML private TextArea outputArea;
    @FXML private TextField commandInput;
    @FXML private Button executeButton;
    @FXML private Button closeButton;
    
    public void initialize() {
        outputArea.setText("Command Checker Window Opened. Type a command and press Execute.\n");
    }
    
    @FXML
    private void executeCommand() {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            process.waitFor();
            outputArea.appendText("\n> " + command + "\n" + output.toString());
            commandInput.clear();
        } catch (Exception e) {
            outputArea.appendText("\nError executing command: " + command + "\n");
        }
    }
    
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    public static void openCommandChecker() {
        try {
            FXMLLoader loader = new FXMLLoader(CommandCheckerWindow.class.getResource("CommandCheckerWindow.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Command Checker");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

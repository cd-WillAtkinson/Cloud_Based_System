/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.FileWriter;

/**
 *
 * @author ntu-user
 */
public class CreateFileController {
    
    @FXML
    private Button createFileButton;
    
    @FXML
    private TextField fileName;
    
    @FXML
    private TextArea fileData;
    
    private static final String DIRECTORY_PATH = "files";
    
    @FXML
    public void handleCreateFile(ActionEvent event) {
        String fileNameText = fileName.getText().trim();
        String fileContent = fileData.getText();
        
        if (fileNameText.isEmpty()) {
            System.out.println("Filename cannot be empty.");
            return;
        }
        
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        File newFile = new File(DIRECTORY_PATH + "/" + fileNameText);
        try {
            if (newFile.createNewFile()) {
                System.out.println("File created: " + newFile.getName());
                
                try (FileWriter writer = new FileWriter(newFile)) {
                    writer.write(fileContent);
                    System.out.println("File content written successfully.");
                }
                
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FileList.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) createFileButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("File List");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
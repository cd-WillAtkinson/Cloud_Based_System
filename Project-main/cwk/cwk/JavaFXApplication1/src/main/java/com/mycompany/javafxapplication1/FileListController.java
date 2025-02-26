package com.mycompany.javafxapplication1;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class FileListController {
    
    @FXML
    private ListView<String> fileListView;

    @FXML
    private TextArea fileContentArea;

    @FXML
    private Button saveFileButton;

    private static final String DIRECTORY_PATH = "files";
    private String selectedFileName;

    private static final String LOAD_BALANCER_HOST = "localhost"; // Host for load balancer
    private static final int LOAD_BALANCER_PORT = 5000; // Port where load balancer is listening

    @FXML
    public void initialize() {
        loadFileList();
    }

    private void loadFileList() {
        File folder = new File(DIRECTORY_PATH);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdir();
            return;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            ObservableList<String> fileNames = FXCollections.observableArrayList();
            for (File file : files) {
                fileNames.add(file.getName());
            }
            fileListView.setItems(fileNames);
        }
    }

    @FXML
    private void handleFileSelection(MouseEvent event) {
        selectedFileName = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFileName != null) {
            loadFileContent(selectedFileName);
        }
    }

    private void loadFileContent(String fileName) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(DIRECTORY_PATH + "/" + fileName)));
            fileContentArea.setText(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveFile(ActionEvent event) {
        if (selectedFileName != null) {
            try {
                // Save the content to the local file system
                try (FileWriter writer = new FileWriter(DIRECTORY_PATH + "/" + selectedFileName)) {
                    writer.write(fileContentArea.getText());
                    System.out.println("File saved locally!");
                }

                // Send the file to the load balancer
                sendFileToLoadBalancer(selectedFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFileToLoadBalancer(String fileName) {
        try (Socket socket = new Socket(LOAD_BALANCER_HOST, LOAD_BALANCER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            // Send the request with sessionId, action (e.g., "UPLOAD"), and file name
            String sessionId = "user_session"; // Replace with actual session ID
            out.println(sessionId + " UPLOAD " + fileName);

            // Wait for response from load balancer
            String response = in.readLine();
            if (response != null && response.startsWith("FORWARD")) {
                System.out.println("File forwarded to " + response);
            } else {
                System.out.println("Error forwarding file to load balancer");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


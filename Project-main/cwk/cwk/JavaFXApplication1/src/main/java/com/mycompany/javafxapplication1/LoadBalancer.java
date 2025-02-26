package com.mycompany.javafxapplication1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LoadBalancer {
    private static final String[] FILE_CONTAINERS = {
        "172.18.0.2",  // Container 4
        "172.18.0.3",  // Container 3
        "172.18.0.4",  // Container 1
        "172.18.0.5"   // Container 2
    };
    private static final int FILE_CONTAINER_PORT = 4848; // Default SSH port
    private static final String SSH_USER = "ntu-user";
    
    private static final BlockingQueue<Runnable> requestQueue = new ArrayBlockingQueue<>(100);
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private static int currentContainerIndex = 0;
    private static final Object LOCK = new Object();
    
    public static void main(String[] args) {
        new Thread(new QueueProcessor()).start();
        
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Load Balancer started on port 5000...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                requestQueue.put(new ClientHandler(clientSocket));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    static class QueueProcessor implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable request = requestQueue.take();
                    threadPool.submit(request);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                
                String request = in.readLine();
                if (request != null) {
                    String[] parts = request.split(" ");
                    if (parts.length < 3) {
                        out.println("ERROR: Invalid request format");
                        return;
                    }
                    
                    String action = parts[1];
                    String fileName = parts[2];
                    
                    // Simulate artificial delay based on traffic conditions
                    simulateArtificialDelay();
                    
                    synchronized (fileName.intern()) {
                        if ("UPLOAD".equalsIgnoreCase(action)) {
                            boolean success = sendFileToContainer(fileName);
                            if (success) {
                                out.println("SUCCESS: File uploaded");
                            } else {
                                out.println("ERROR: File upload failed");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void simulateArtificialDelay() {
            int baseDelay = 30000 + new Random().nextInt(60000); // 30 to 90 seconds
            double trafficCoefficient = getTrafficCoefficient(); // Adjusts delay based on traffic
            int finalDelay = (int) (baseDelay * trafficCoefficient);
            
            try {
                System.out.println("Simulating delay: " + finalDelay + " ms");
                Thread.sleep(finalDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        private double getTrafficCoefficient() {
            String trafficLevel = getTrafficLevel();
            switch (trafficLevel) {
                case "LOW": return 0.75;
                case "MEDIUM": return 1.0;
                case "HIGH": return 1.5;
                default: return 1.0;
            }
        }
        
        private String getTrafficLevel() {
            int randomValue = new Random().nextInt(100);
            if (randomValue < 30) return "LOW";
            if (randomValue < 70) return "MEDIUM";
            return "HIGH";
        }
        
        private boolean sendFileToContainer(String fileName) {
            String localFilePath = "/home/ntu-user/NetBeansProjects/test_Project/Project-main/Project-main/cwk/cwk/JavaFXApplication1/files/" + fileName;
            String remoteFilePath = "/files/";
            
            String fileContainer;
            synchronized (LOCK) {
                fileContainer = FILE_CONTAINERS[currentContainerIndex];
                currentContainerIndex = (currentContainerIndex + 1) % FILE_CONTAINERS.length; // Round Robin
            }
            
            String scpCommand = "scp " + localFilePath + " " + SSH_USER + "@" + fileContainer + ":" + remoteFilePath;
            
            System.out.println("Running SCP command: " + scpCommand);
            
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", scpCommand);
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                
                if (exitCode == 0) {
                    System.out.println("File transferred successfully to " + fileContainer);
                    return true;
                } else {
                    System.err.println("Error: SCP command failed with exit code " + exitCode);
                    return false;
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Error: SCP file transfer failed: " + e.getMessage());
                return false;
            }
        }
    }
}


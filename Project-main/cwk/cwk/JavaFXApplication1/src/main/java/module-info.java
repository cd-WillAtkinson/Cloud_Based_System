module com.mycompany.javafxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.base;
    requires java.sql; // added
    requires org.eclipse.paho.client.mqttv3;
    
    opens com.mycompany.javafxapplication1 to javafx.fxml;
    exports com.mycompany.javafxapplication1;
}

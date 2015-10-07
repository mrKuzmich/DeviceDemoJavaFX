package org.device.demo;

import com.taliter.fiscal.port.rxtx.RXTXFiscalPort;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GSXJavaFX.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Device Demo Application");
        primaryStage.setScene(new Scene(root, 640, 500));
        primaryStage.show();
        Controller controller = loader.getController();
        controller.setFiscalPort(new FiscalPortMediator());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package org.device.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Platform.runLater(() -> showErrorDialog(t, e)));
        Thread.currentThread().setUncaughtExceptionHandler(this::showErrorDialog);


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GSXJavaFX2.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Device Demo Application");
        primaryStage.setScene(new Scene(root, 674, 648));
        primaryStage.show();
        Controller controller = loader.getController();
        controller.setFiscalPort(new FiscalPortCommander());
    }

    private void showErrorDialog(Thread t, Throwable e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText(null);
      Throwable th = e.getCause().getCause();
      alert.setContentText(th != null ? th.getMessage() : e.getMessage());

// Create expandable Exception.
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String exceptionText = sw.toString();

      Label label = new Label("The exception stacktrace was:");

      TextArea textArea = new TextArea(exceptionText);
      textArea.setEditable(false);
      textArea.setWrapText(true);

      textArea.setMaxWidth(Double.MAX_VALUE);
      textArea.setMaxHeight(Double.MAX_VALUE);
      GridPane.setVgrow(textArea, Priority.ALWAYS);
      GridPane.setHgrow(textArea, Priority.ALWAYS);

      GridPane expContent = new GridPane();
      expContent.setMaxWidth(Double.MAX_VALUE);
      expContent.add(label, 0, 0);
      expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
      alert.getDialogPane().setExpandableContent(expContent);
      alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

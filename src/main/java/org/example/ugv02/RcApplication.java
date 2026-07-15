package org.example.ugv02;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RcApplication extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(RcApplication.class.getResource("ugv02.fxml"));
    Parent parent = fxmlLoader.load();
    UiController uiController = fxmlLoader.getController();
    uiController.setStage(stage);
    Scene scene = new Scene(parent);
    stage.setTitle("UGV02 RC");
    stage.setScene(scene);
    stage.show();
  }
}

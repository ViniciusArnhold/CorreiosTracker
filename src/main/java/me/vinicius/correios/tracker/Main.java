package me.vinicius.correios.tracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlloader = new FXMLLoader(getClass().getClassLoader().getResource("interface.fxml"));

        Controller controller = new Controller();
        fxmlloader.setController(controller);
        controller.setPrimaryStage(primaryStage);

        Parent root = fxmlloader.load();
        Scene scene = new Scene(root,600,400);

        primaryStage.setTitle("Correios Package Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();

    }



    public static void main(String[] args) {
        launch(args);
    }
}

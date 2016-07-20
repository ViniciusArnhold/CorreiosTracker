package me.vinicius.correios.tracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlloader = new FXMLLoader(getClass().getClassLoader().getResource("interface.fxml"));

        Controller controller = new Controller();
        fxmlloader.setController(controller);
        controller.setPrimaryStage(primaryStage);

        Parent root = fxmlloader.load();
        Scene scene = new Scene(root,650,400);


        primaryStage.setTitle("Correios Package Tracker");
        primaryStage.getIcons().add(new Image("http://globalestudio.com.br/loja/image/cache/catalog/PRODUTOS/modulosmodulo-de-frete-correios-opencart-54-746x746.jpg"));
        primaryStage.setScene(scene);
        primaryStage.resizableProperty().set(false);
        primaryStage.show();

    }



    public static void main(String[] args) {
        launch(args);
    }
}

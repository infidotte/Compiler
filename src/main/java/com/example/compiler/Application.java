package com.example.compiler;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(new Image("icon.png"));
        stage.setTitle("Compiler");
        stage.setScene(scene);
        stage.show();
    }
}
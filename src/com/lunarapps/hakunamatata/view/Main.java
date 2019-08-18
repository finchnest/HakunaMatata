package com.lunarapps.hakunamatata.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    //this method is just setting up the UI
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("matatamain.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("serverstyler.css");

        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }


    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");

        Thread serviceStarter = new Thread(new ServerServiceStarter());
        serviceStarter.start();

        //The launch method does not return until the application has exited
        //so any code that has to run in this main method gotta be above it
        launch(args);
    }


}



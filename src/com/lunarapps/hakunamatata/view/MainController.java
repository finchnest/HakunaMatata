package com.lunarapps.hakunamatata.view;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private ImageView runningImage;
    @FXML
    private TextArea serverStatusTextArea;

    private String rotationThread;

    public MainController() {
        serverStatusTextArea = new TextArea();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayStatus("Current Thread-->" + Thread.currentThread().getName());
        Thread rota = new Thread(new RotationHandler());
        rota.start();
    }

    private void displayStatus(String mess) {
        serverStatusTextArea.appendText("\n" + mess);
    }

    class RotationHandler implements Runnable {

        public RotationHandler() {
        }

        @Override
        public void run() {
            Thread.currentThread().setName("Rotation Handler");
            rotationThread = Thread.currentThread().getName();
            displayStatus("\n" + "Rotation started on thread--> " + rotationThread + "\n");

            rotator();
        }

        private void rotator() {
            RotateTransition rt = new RotateTransition(Duration.millis(7000), runningImage);
            rt.setByAngle(360);
            rt.setCycleCount(Animation.INDEFINITE);
            rt.setInterpolator(Interpolator.LINEAR);
            rt.play();
        }
    }


}

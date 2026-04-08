package com.pestpredictor.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class JavaFXApp extends Application {

    // ✅ FIXED URL
    private static final String BASE_URL = "http://localhost:8080/login.html";

    private WebEngine webEngine;
    private TextField addressBar;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crop Pest Predictor");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);

        WebView webView = new WebView();
        webEngine = webView.getEngine();

        webEngine.setJavaScriptEnabled(true);

        Button btnBack = new Button("←");
        Button btnForward = new Button("→");
        Button btnRefresh = new Button("↻");

        addressBar = new TextField(BASE_URL);
        HBox.setHgrow(addressBar, Priority.ALWAYS);

        addressBar.setOnAction(e -> webEngine.load(addressBar.getText()));

        btnBack.setOnAction(e -> {
            if (webEngine.getHistory().getCurrentIndex() > 0) {
                webEngine.getHistory().go(-1);
            }
        });

        btnForward.setOnAction(e -> {
            if (webEngine.getHistory().getCurrentIndex() <
                webEngine.getHistory().getEntries().size() - 1) {
                webEngine.getHistory().go(1);
            }
        });

        btnRefresh.setOnAction(e -> webEngine.reload());

        webEngine.locationProperty().addListener((obs, oldLoc, newLoc) ->
                Platform.runLater(() -> addressBar.setText(newLoc)));

        webEngine.getLoadWorker().stateProperty().addListener((obs, old, state) -> {
            if (state == Worker.State.RUNNING) {
                primaryStage.setTitle("Loading...");
            } else if (state == Worker.State.SUCCEEDED) {
                primaryStage.setTitle("Crop Pest Predictor");
            }
        });

        ToolBar toolbar = new ToolBar(btnBack, btnForward, btnRefresh, addressBar);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(webView);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // ✅ LOAD STATIC PAGE
        webEngine.load(BASE_URL);

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
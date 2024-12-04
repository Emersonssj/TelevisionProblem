package com.example.televisionproblem;

import javafx.collections.FXCollections;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        // Configuração do layout principal
        BorderPane root = new BorderPane();

        // Parte superior: Botões de menu
        HBox topMenu = new HBox(10);
        topMenu.setStyle("-fx-padding: 10; -fx-background-color: #2c3e50;");
        Button addHospedeButton = new Button("Adicionar Hospede");
        topMenu.getChildren().addAll(addHospedeButton);
        root.setTop(topMenu);

        // Parte direita: Log de mensagens
        ObservableList<String> messages = FXCollections.observableArrayList();
        VBox logBox = new VBox(5);
        logBox.setStyle("-fx-padding: 10; -fx-background-color: #f7f7f7;");

        // Vincular mensagens da lista ao logBox
        messages.addListener((javafx.collections.ListChangeListener.Change<? extends String> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (String message : change.getAddedSubList()) {
                        logBox.getChildren().add(new Text(message));
                    }
                }
            }
        });

        ScrollPane logScrollPane = new ScrollPane(logBox);
        logScrollPane.setFitToWidth(true);
        logScrollPane.setPrefWidth(200);
        VBox.setVgrow(logScrollPane, Priority.ALWAYS);

        VBox logArea = new VBox(5, logScrollPane);
        logArea.setStyle("-fx-padding: 10;");
        root.setRight(logArea);

        // Parte central: Animação
        Pane animationPane = new Pane();
        animationPane.setStyle("-fx-background-color: #ecf0f1;");
        root.setCenter(animationPane);

        // Chamar a janela secundária e passar o Pane de animação
        addHospedeButton.setOnAction(e -> openSecondaryStage(messages, animationPane));

        // Configurar a cena
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Problema da televisão");
        stage.setScene(scene);
        stage.show();
    }

    public static void generateHospedeAnimation(Pane animationPane, String id, String channel, String timeWatchingTV) {
        // Criar o boneco
        Circle person = new Circle(20, Color.BLUE);
        person.setTranslateX(1); // Posição inicial X
        person.setTranslateY(500); // Posição inicial Y
        animationPane.getChildren().add(person);

        // Configurar a animação
        TranslateTransition moveToPoint = new TranslateTransition(Duration.seconds(4), person);
        moveToPoint.setToX(500); // Posição final X
        moveToPoint.setToY(500); // Posição final Y

        PauseTransition pauseAtPoint = new PauseTransition(Duration.seconds(2));

        SequentialTransition animation = new SequentialTransition(moveToPoint, pauseAtPoint);
        animation.play();
    }

    private void openSecondaryStage(ObservableList<String> messages, Pane animationPane) {
        Stage secondaryStage = new Stage();
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
        secondaryStage.setTitle("Adicionar Mensagens");

        // Layout da nova tela
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        // Campo de entrada para adicionar mensagens
        Label idLabel = new Label("ID do hóspede");
        TextField idTextField = new TextField();

        Label channelLabel = new Label("Canal do hóspede");
        TextField channelTextField = new TextField();

        Label timeLabel = new Label("Tempo assistindo do hóspede");
        TextField timeWatchingTextField = new TextField();

        Button createHospedeButton = new Button("Adicionar");
        createHospedeButton.setOnAction(e -> {
                secondaryStage.close();
                generateHospedeAnimation(animationPane, idTextField.getText(), channelTextField.getText(), timeWatchingTextField.getText());
        });
        layout.getChildren().addAll(idLabel, idTextField, channelLabel, channelTextField, timeLabel, timeWatchingTextField, createHospedeButton);

        // Configurar a cena e mostrar a tela
        Scene scene = new Scene(layout, 300, 300);
        secondaryStage.setScene(scene);
        secondaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

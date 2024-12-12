package com.example.televisionproblem;
import java.util.concurrent.Semaphore;

import adapters.Hospede;
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
    public static final Semaphore mutex = new Semaphore(1);
    public static final Semaphore changeChannel = new Semaphore(0);

    public static int currentChannel = 0;
    public static int currentWatchers = 0;

    public static void main(String[] args) {
        launch();
    }
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

    public static void generateHospedeAnimation(Pane animationPane, String id, String channel, String timeWatchingTV, String timeResting) {
        // Criar o boneco
        VBox vbox = new VBox(5); // Espaçamento de 10 pixels entre os elementos
        vbox.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");

        Text text1 = new Text("ID: " + id);
        Text text2 = new Text("Canal: " + channel);
        Text text3 = new Text("Tempo assistindo: " + timeWatchingTV);
        Text text4 = new Text("Tempo descansando: " + timeResting);


        vbox.getChildren().addAll(text1, text2, text3, text4);
        vbox.setTranslateX(1); // Posição inicial X
        vbox.setTranslateY(500); // Posição inicial Y
        animationPane.getChildren().add(vbox);

        // Configurar a animação
        TranslateTransition moveToPoint = new TranslateTransition(Duration.seconds(4), vbox);
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

        Label timeRestingLabel = new Label("Tempo descansando");
        TextField timeRestingTextField = new TextField();

        Button createHospedeButton = new Button("Adicionar");
        createHospedeButton.setOnAction(e -> {
            String id = idTextField.getText();
            String channel = channelTextField.getText();
            String timeWatching = timeWatchingTextField.getText();
            String timeResting = timeRestingTextField.getText();

            Hospede hospede = new Hospede(
                    Integer.parseInt(id),
                    Integer.parseInt(channel),
                    Integer.parseInt(timeWatching),
                    Integer.parseInt(timeResting)
            );
            new Thread(hospede).start();
            secondaryStage.close();
            generateHospedeAnimation(animationPane, id, channel, timeWatching, timeResting);
        });
        layout.getChildren().addAll(idLabel, idTextField, channelLabel, channelTextField, timeLabel, timeWatchingTextField, timeRestingLabel, timeRestingTextField , createHospedeButton);

        // Configurar a cena e mostrar a tela
        Scene scene = new Scene(layout, 300, 300);
        secondaryStage.setScene(scene);
        secondaryStage.show();
    }
}

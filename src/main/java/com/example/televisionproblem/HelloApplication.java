package com.example.televisionproblem;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;

import adapters.Hospede;
import javafx.collections.FXCollections;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import widgets.BallWidget;
import widgets.TVWidget;

public class HelloApplication extends Application {
    public static int canalAtual = 0;
    public static int visualizadores = 0;
    public static Semaphore controleRemoto = new Semaphore(1);

    public static TVWidget tvWidget = new TVWidget();
    public static List<BallWidget> ballWidgets =  new ArrayList<>();
    public static List<Hospede> hospedes = new ArrayList<>();
    public static StackPane centerLayout = new StackPane();
    public static ObservableList<String> messages = FXCollections.observableArrayList();

    public static void removeBallWidgetById(String id) {
        int index = 0;
        for (int i = 0; i < ballWidgets.size(); i++) {
            BallWidget ball = ballWidgets.get(i);
            if (ball.getIdd() == id) {
                index = i;
            }
        }
        ballWidgets.remove(index);
    }

    public static void moveBallById(String id, double x, double y){
        int index = -1;
        for (int i = 0; i < ballWidgets.size(); i++) {
            BallWidget ball = ballWidgets.get(i);
            if (Objects.equals(ball.id, id)) {
                index = i;
            }
        }
        ballWidgets.get(index).moveTo(x, y);
    }

    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage primaryStage) {
        // Configuração da primeira tela (janela inicial)
        Label label = new Label("Insira a quantidade de canais:");
        TextField numberInput = new TextField();
        Button proceedButton = new Button("Prosseguir");

        // Layout da primeira tela
        VBox smallLayout = new VBox(10);
        smallLayout.setPadding(new Insets(10));
        smallLayout.getChildren().addAll(label, numberInput, proceedButton);

        // Cena da janela inicial
        Scene smallScene = new Scene(smallLayout, 250, 150);
        primaryStage.setTitle("Janela Inicial");
        primaryStage.setScene(smallScene);
        primaryStage.show();

        // Ação ao clicar no botão "Prosseguir"
        proceedButton.setOnAction(event -> {
            String input = numberInput.getText();
            if (!input.isEmpty()) {
                openMainStage(input);
                primaryStage.close();
            } else {
                label.setText("Por favor, insira um número!");
            }
        });
    }

    private void openMainStage(String number) {
        // Configuração do layout principal
        BorderPane root = new BorderPane();

        // Parte superior: Botões de menu
        HBox topMenu = new HBox(10);
        topMenu.setStyle("-fx-padding: 10; -fx-background-color: #2c3e50;");
        Button addHospedeButton = new Button("Adicionar Hospede");
        topMenu.getChildren().addAll(addHospedeButton);
        root.setTop(topMenu);

        // Parte direita: Log de mensagens
        ListView<String> logListView = new ListView<>(messages);

        ScrollPane logScrollPane = new ScrollPane(logListView);
        logScrollPane.setFitToWidth(true);
        logScrollPane.setPrefWidth(250);
        VBox.setVgrow(logScrollPane, Priority.ALWAYS);

        VBox logArea = new VBox(5, logScrollPane);
        logArea.setStyle("-fx-padding: 10;");
        root.setRight(logArea);

        // Parte central: Animação
        centerLayout.setStyle("-fx-background-color: #ecf0f1;");
        centerLayout.getChildren().add(tvWidget);
        root.setCenter(centerLayout);

        // Chamar a janela secundária e passar o Pane de animação
        addHospedeButton.setOnAction(e -> openSecondaryStage(messages, centerLayout));

        // Configurar a cena
        Stage mainStage = new Stage();
        Scene scene = new Scene(root);
        mainStage.setTitle("Problema da televisão");
        mainStage.setScene(scene);
        mainStage.setMaximized(true);
        mainStage.show();
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

            hospedes.add(new Hospede(
                            Integer.parseInt(id),
                            Integer.parseInt(channel),
                            Integer.parseInt(timeWatching),
                            Integer.parseInt(timeResting)
                    )
            );

            BallWidget ball = new BallWidget(
                    id,
                    Color.color(Math.random(),Math.random(),Math.random()),
                    30,
                    30
            );
            ballWidgets.add(ball);
            centerLayout.getChildren().add(ball);

            hospedes.get(hospedes.size()-1).start();
            secondaryStage.close();
        });
        layout.getChildren().addAll(idLabel, idTextField, channelLabel, channelTextField, timeLabel, timeWatchingTextField, timeRestingLabel, timeRestingTextField , createHospedeButton);

        // Configurar a cena e mostrar a tela
        Scene scene = new Scene(layout, 300, 300);
        secondaryStage.setScene(scene);
        secondaryStage.show();
    }
}
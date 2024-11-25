package com.example.televisionproblem;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.collections.FXCollections;
import javafx.animation.Timeline;
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

        // Parte central: Área para animação
        Pane animationPane = new Pane();
        animationPane.setStyle("-fx-background-color: #ecf0f1;");

        // Criar uma animação simples
        Circle circle = new Circle(50, Color.CORNFLOWERBLUE);
        circle.setTranslateX(50);
        circle.setTranslateY(100);
        animationPane.getChildren().add(circle);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            circle.setTranslateX(circle.getTranslateX() + 1);
            if (circle.getTranslateX() > animationPane.getWidth()) {
                circle.setTranslateX(-circle.getRadius()); // Recomeça do lado esquerdo
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        root.setCenter(animationPane);

        // Parte direita: Log de mensagens
        ObservableList<String> messages = FXCollections.observableArrayList();
        addHospedeButton.setOnAction(e -> openSecondaryStage(messages));
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

        // Configurar a cena
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Problema da televisão");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    private void openSecondaryStage(ObservableList<String> messages) {
        // Criar o novo Stage
        Stage secondaryStage = new Stage();
        secondaryStage.initModality(Modality.APPLICATION_MODAL); // Modal para bloquear a tela principal
        secondaryStage.setTitle("Adicionar Mensagens");

        // Layout da nova tela
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        // Campo de entrada para adicionar mensagens
        TextField idField = new TextField();
        idField.setPromptText("Digite o nome");
        TextField channelField = new TextField();
        channelField.setPromptText("Digite o canal");
        TextField inputField = new TextField();
        inputField.setPromptText("Digite o tempo");
        Button addButton = new Button("Adicionar");
        addButton.setOnAction(e -> {
            String newMessage = inputField.getText();
            if (!newMessage.isEmpty()) {
                messages.add(newMessage); // Adiciona à lista de mensagens
                inputField.clear(); // Limpa o campo de entrada
                secondaryStage.close();
            }
        });

        layout.getChildren().addAll(new Label("Preencha informações sobre o hóspede"), inputField, addButton);

        // Configurar a cena e mostrar a tela
        Scene scene = new Scene(layout, 300, 200);
        secondaryStage.setScene(scene);
        secondaryStage.show();
    }

}
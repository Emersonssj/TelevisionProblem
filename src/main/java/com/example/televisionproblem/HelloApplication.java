package com.example.televisionproblem;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    public static final int MAX_HOSPEDES_NA_TV = 3;
    public static final Semaphore tvSemaphore = new Semaphore(MAX_HOSPEDES_NA_TV);
    public static int canalAtual = -1;
    public static int espectadoresAssistindo = 0;

    List<Hospede> hospedes = new ArrayList<>();
    List<Circle> hospedesDraw = new ArrayList<>();

    public static void incrementaEspectador(){
        espectadoresAssistindo++;
    }

    public static  void decrementaEspectador(){
        espectadoresAssistindo--;
    }

    public static int mostraQtdEspectadores(){
        return espectadoresAssistindo;
    }

    public static void atualizaCanalAtual(int novoValor){
        canalAtual = novoValor;
    }

    public static int mostraCanalAtual(){
        return canalAtual;
    }

    public static void reservaTv(){
        try {
            tvSemaphore.acquire(); // Adquire acesso à TV
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void liberaTv(){
        tvSemaphore.release();
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
                openMainStage(input); // Abrir janela principal
                primaryStage.close(); // Fecha a janela inicial
            } else {
                label.setText("Por favor, insira um número!"); // Mensagem de erro
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
        Stage mainStage = new Stage();
        Scene scene = new Scene(root, 800, 600);
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

            Circle hospedeCircle = new Circle(20, Color.BLUE);
            hospedeCircle.setCenterX(50);
            hospedeCircle.setCenterY(200);

            hospedes.add(new Hospede(
                            Integer.parseInt(id),
                            Integer.parseInt(channel),
                            Integer.parseInt(timeWatching),
                            Integer.parseInt(timeResting)
                    )
            );

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
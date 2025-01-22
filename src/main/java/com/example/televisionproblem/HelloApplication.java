package com.example.televisionproblem;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import adapters.Resource;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    public static List<Semaphore> arrayE = new ArrayList<>();
    public static List<Semaphore> arrayA = new ArrayList<>();

    public static ArrayList<ArrayList<Semaphore>> arrayC = new ArrayList<>();
    public static ArrayList<ArrayList<Semaphore>> arrayR = new ArrayList<>();


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        // Configuração da primeira tela (janela inicial)
        Label idLabel = new Label("Id do recurso:");
        TextField idInput = new TextField();
        Label namelabel = new Label("Nome do recurso:");
        TextField nameInput = new TextField();
        Label quantityLabel = new Label("Quantidade de instâncias:");
        TextField quantityInput = new TextField();

        Button proceedButton = new Button("Prosseguir");

        // Layout da primeira tela
        VBox smallLayout = new VBox(10);
        smallLayout.setPadding(new Insets(10));
        smallLayout.getChildren().addAll(idLabel, idInput, namelabel, nameInput, quantityLabel, quantityInput,
                proceedButton);

        // Cena da janela inicial
        Scene smallScene = new Scene(smallLayout, 250, 250);
        primaryStage.setTitle("Janela Inicial");
        primaryStage.setScene(smallScene);
        primaryStage.show();

        // Ação ao clicar no botão "Prosseguir"
        proceedButton.setOnAction(event -> {
            Resource recurso = new Resource(1, namelabel.getText(), 2);
            openMainStage(recurso);
            primaryStage.close();

        });
    }

    private void openMainStage(Resource recurso) {
        // Configuração do layout principal
        BorderPane root = new BorderPane();

        // Parte superior: Botões de menu
        HBox topMenu = new HBox(10);
        topMenu.setStyle("-fx-padding: 10; -fx-background-color: #2c3e50;");
        Button addResourceButton = new Button("Criar recurso");
        addResourceButton.setOnAction(e -> addResourceStage());
        Button killResourceButton = new Button("Eliminar processo");
        killResourceButton.setOnAction(e -> addProcessStage());
        topMenu.getChildren().addAll(addResourceButton, killResourceButton);
        root.setTop(topMenu);

        // Configurar a cena
        Stage mainStage = new Stage();
        Scene scene = new Scene(root);
        mainStage.setTitle("Detecção de deadlocks");
        mainStage.setScene(scene);
        mainStage.setMaximized(true);
        mainStage.show();
    }

    private void addResourceStage() {
        Stage secondaryStage = new Stage();
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
        secondaryStage.setTitle("Adicionar recurso");

        // Layout da nova tela
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        // Campo de entrada para adicionar recurso
        Label idLabel = new Label("Id do recurso:");
        TextField idInput = new TextField();
        Label namelabel = new Label("Nome do recurso:");
        TextField nameInput = new TextField();
        Label quantityLabel = new Label("Quantidade de instâncias:");
        TextField quantityInput = new TextField();

        Button proceedButton = new Button("Adicionar");
        proceedButton.setOnAction(e -> {
            String id = idLabel.getText();
            String name = namelabel.getText();
            String quantity = quantityLabel.getText();

            secondaryStage.close();
        });
        layout.getChildren().addAll(idLabel, idInput, namelabel, nameInput, quantityLabel,
                quantityInput, proceedButton);

        // Configurar a cena e mostrar a tela
        Scene scene = new Scene(layout, 250, 250);
        secondaryStage.setScene(scene);
        secondaryStage.show();
    }

    private void addProcessStage() {
        Stage secondaryStage = new Stage();
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
        secondaryStage.setTitle("Adicionar recurso");

        // Layout da nova tela
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        // Campo de entrada para adicionar recurso
        Label idLabel = new Label("Id do recurso:");
        TextField idInput = new TextField();
        Label namelabel = new Label("Nome do recurso:");
        TextField nameInput = new TextField();
        Label quantityLabel = new Label("Quantidade de instâncias:");
        TextField quantityInput = new TextField();

        Button proceedButton = new Button("Adicionar");
        proceedButton.setOnAction(e -> {
            String id = idLabel.getText();
            String name = namelabel.getText();
            String quantity = quantityLabel.getText();

            secondaryStage.close();
        });
        layout.getChildren().addAll(idLabel, idInput, namelabel, nameInput, quantityLabel,
                quantityInput, proceedButton);

        // Configurar a cena e mostrar a tela
        Scene scene = new Scene(layout, 250, 250);
        secondaryStage.setScene(scene);
        secondaryStage.show();
    }
}
//        Monte o vetor de recursos existentes E;
//        2. Monte a matriz de alocação corrente C;
//        3. Monte a matriz de requisições R;
package com.example.televisionproblem;

import adapters.Process;
import adapters.Resource;
import adapters.SO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class HelloApplication extends Application {
    public static SO so;
    public static ObservableList<String> messages = FXCollections.observableArrayList();

    public static List<Integer> arrayE = new ArrayList<>();
    public static List<Semaphore> arrayA = new ArrayList<>();
    public static ArrayList<ArrayList<Semaphore>> arrayC = new ArrayList<>();
    public static ArrayList<ArrayList<Semaphore>> arrayR = new ArrayList<>();

    public static Semaphore mutex = new Semaphore(1);

    // Lista para armazenar os recursos cadastrados (para exibição e para criação do vetor E)
    private ObservableList<Resource> resourceData = FXCollections.observableArrayList();

    // Lista para armazenar os nomes dos processos (índice = id do processo)
    private List<String> processNames = new ArrayList<>();

    // No início, nenhum processo é criado automaticamente
    private int initialProcesses = 0;

    // Componentes para exibir os dados em forma de tabela (na tela principal)
    private TableView<ResourceRow> resourceTable;
    private TableView<ObservableList<String>> matrixCTable;
    private TableView<ObservableList<String>> matrixRTable;

    // Número de recursos (atualizado a cada atualização, mas pode ser obtido via so.getE().length)
    private int numResources = 0;

    @Override
    public void start(Stage primaryStage) {
        // Tela inicial de cadastro de recursos
        primaryStage.setTitle("Configuração de Recursos");

        // Campos para cadastro de um recurso
        Label resIdLabel = new Label("Identificador do Recurso:");
        TextField resIdInput = new TextField();
        Label resNameLabel = new Label("Nome do Recurso:");
        TextField resNameInput = new TextField();
        Label resQuantityLabel = new Label("Quantidade de Instâncias:");
        TextField resQuantityInput = new TextField();

        // Botão para adicionar recurso à lista
        Button btnAddResource = new Button("Adicionar Recurso");
        // Botão para iniciar o sistema (só habilitado se houver pelo menos 1 recurso)
        Button btnStartSystem = new Button("Iniciar Sistema");
        btnStartSystem.setDisable(true);

        // Área para exibir os recursos cadastrados (ListView simples)
        ListView<String> listViewResources = new ListView<>();
        listViewResources.setPrefHeight(150);

        VBox configLayout = new VBox(10);
        configLayout.setPadding(new Insets(10));
        configLayout.getChildren().addAll(
                resIdLabel, resIdInput,
                resNameLabel, resNameInput,
                resQuantityLabel, resQuantityInput,
                btnAddResource,
                new Label("Recursos cadastrados:"), listViewResources,
                btnStartSystem
        );

        Scene configScene = new Scene(configLayout, 300, 400);
        primaryStage.setScene(configScene);
        primaryStage.show();

        btnAddResource.setOnAction(e -> {
            try {
                if (resourceData.size() >= 10) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Número máximo de tipos de recursos (10) atingido!");
                    alert.showAndWait();
                    return;
                }
                int resId = Integer.parseInt(resIdInput.getText());
                String resName = resNameInput.getText().trim();
                int totalInstances = Integer.parseInt(resQuantityInput.getText());
                if (resName.isEmpty()) {
                    throw new NumberFormatException("Nome vazio");
                }
                Resource novoRecurso = new Resource(resId, resName, totalInstances);
                resourceData.add(novoRecurso);
                listViewResources.getItems().add("[" + resId + "] " + resName + " - " + totalInstances + " instâncias");
                resIdInput.clear();
                resNameInput.clear();
                resQuantityInput.clear();
                btnStartSystem.setDisable(resourceData.isEmpty());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Preencha os campos corretamente!");
                alert.showAndWait();
            }
        });

        btnStartSystem.setOnAction(e -> {
            numResources = resourceData.size();
            int[] vetorE = new int[numResources];
            for (int i = 0; i < numResources; i++) {
                vetorE[i] = resourceData.get(i).getTotalInstances();
            }
            so = new SO(0, vetorE, initialProcesses);
            so.start(); // inicia verificação de deadlock
            openMainStage();
            primaryStage.close();
        });
    }

    // Classe interna para representar cada linha do recurso (para os vetores E e A)
    public static class ResourceRow {
        private final SimpleStringProperty recurso;
        private final SimpleIntegerProperty total;   // Vetor E
        private final SimpleIntegerProperty disponivel; // Vetor A

        public ResourceRow(String recurso, int total, int disponivel) {
            this.recurso = new SimpleStringProperty(recurso);
            this.total = new SimpleIntegerProperty(total);
            this.disponivel = new SimpleIntegerProperty(disponivel);
        }

        public String getRecurso() { return recurso.get(); }
        public int getTotal() { return total.get(); }
        public int getDisponivel() { return disponivel.get(); }

        public void setTotal(int total) { this.total.set(total); }
        public void setDisponivel(int disponivel) { this.disponivel.set(disponivel); }
    }

    // Tela principal do sistema
    private void openMainStage() {
        BorderPane root = new BorderPane();



        // Topo: Botões de menu
        HBox topMenu = new HBox(10);
        topMenu.setPadding(new Insets(10));
        Button btnAddResource = new Button("Adicionar Recurso");
        Button btnAddProcess = new Button("Adicionar Processo");
        topMenu.getChildren().addAll(btnAddResource, btnAddProcess);
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

        // Centro: Área para exibição dos dados em forma de tabelas
        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(10));

        // Tabela de recursos (vetores E e A)
        resourceTable = new TableView<>();
        TableColumn<ResourceRow, String> colRecurso = new TableColumn<>("Recurso");
        colRecurso.setCellValueFactory(new PropertyValueFactory<>("recurso"));
        TableColumn<ResourceRow, Integer> colE = new TableColumn<>("E");
        colE.setCellValueFactory(new PropertyValueFactory<>("total"));
        TableColumn<ResourceRow, Integer> colA = new TableColumn<>("A");
        colA.setCellValueFactory(new PropertyValueFactory<>("disponivel"));
        resourceTable.getColumns().addAll(colRecurso, colE, colA);
        resourceTable.setPrefHeight(400);

        // Tabela de Matriz de Alocação (C)
        matrixCTable = new TableView<>();
        // Tabela de Matriz de Requisição (R)
        matrixRTable = new TableView<>();

        // Criação dinâmica das colunas para as matrizes: a primeira coluna mostra o nome do processo e depois uma coluna para cada recurso
        TableColumn<ObservableList<String>, String> procColC = new TableColumn<>("Processo");
        procColC.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        matrixCTable.getColumns().add(procColC);

        TableColumn<ObservableList<String>, String> procColR = new TableColumn<>("Processo");
        procColR.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        matrixRTable.getColumns().add(procColR);

        for (int j = 0; j < numResources; j++) {
            final int colIndex = j + 1; // pois a coluna 0 é o nome do processo
            TableColumn<ObservableList<String>, String> colC = new TableColumn<>("R" + (j+1));
            colC.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIndex)));
            matrixCTable.getColumns().add(colC);

            TableColumn<ObservableList<String>, String> colR = new TableColumn<>("R" + (j+1));
            colR.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIndex)));
            matrixRTable.getColumns().add(colR);
        }

        // Rótulos para identificar as tabelas
        Label labelRecursos = new Label("Recursos (Vetores E e A):");
        Label labelC = new Label("Matriz de Alocação (C):");
        Label labelR = new Label("Matriz de Requisição (R):");

        centerBox.getChildren().addAll(labelRecursos, resourceTable, labelC, matrixCTable, labelR, matrixRTable);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 800, 600);
        Stage mainStage = new Stage();
        mainStage.setTitle("Simulação de Deadlock");
        mainStage.setScene(scene);
        mainStage.show();

        // Atualiza as tabelas periodicamente
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTables()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Botão "Adicionar Recurso" na tela principal (permite expandir o vetor de recursos)
        btnAddResource.setOnAction(e -> {
            Stage addResourceStage = new Stage();
            addResourceStage.initModality(Modality.APPLICATION_MODAL);
            addResourceStage.setTitle("Adicionar Recurso");

            Label idLabel = new Label("Identificador do Recurso:");
            TextField idInput = new TextField();
            Label nameLabel = new Label("Nome do Recurso:");
            TextField nameInput = new TextField();
            Label quantityLabel = new Label("Quantidade de Instâncias:");
            TextField quantityInput = new TextField();
            Button addButton = new Button("Adicionar");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));
            layout.getChildren().addAll(idLabel, idInput, nameLabel, nameInput, quantityLabel, quantityInput, addButton);

            Scene sceneResource = new Scene(layout, 250, 250);
            addResourceStage.setScene(sceneResource);
            addResourceStage.show();

            addButton.setOnAction(event -> {
                try {
                    int resId = Integer.parseInt(idInput.getText());
                    String resName = nameInput.getText().trim();
                    int totalInstances = Integer.parseInt(quantityInput.getText());
                    if (resName.isEmpty()) {
                        throw new NumberFormatException("Nome vazio");
                    }
                    Resource novoRecurso = new Resource(resId, resName, totalInstances);
                    resourceData.add(novoRecurso);
                    so.addResource(totalInstances);
                    numResources = HelloApplication.arrayE.size();
                    updateMatrixColumns();
                    addResourceStage.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Recurso adicionado com sucesso!");
                    alert.showAndWait();
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Preencha os campos corretamente!");
                    alert.showAndWait();
                }
            });
        });

        // Botão "Adicionar Processo" na tela principal
        btnAddProcess.setOnAction(e -> {
            Stage addProcessStage = new Stage();
            addProcessStage.initModality(Modality.APPLICATION_MODAL);
            addProcessStage.setTitle("Adicionar Processo");

            Label nameLabel = new Label("Nome do Processo:");
            TextField nameInput = new TextField();
            Label intervalLabel = new Label("Intervalo de Requisição (segundos):");
            TextField intervalInput = new TextField();
            Label utilizationLabel = new Label("Tempo de Utilização (segundos):");
            TextField utilizationInput = new TextField();
            Button addButton = new Button("Adicionar");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));
            layout.getChildren().addAll(nameLabel, nameInput, intervalLabel, intervalInput, utilizationLabel, utilizationInput, addButton);

            Scene sceneProcess = new Scene(layout, 250, 250);
            addProcessStage.setScene(sceneProcess);
            addProcessStage.show();

            addButton.setOnAction(event -> {
                try {
                    String novoProcName = nameInput.getText().trim();
                    if(novoProcName.isEmpty()){
                        throw new NumberFormatException("Nome vazio");
                    }
                    int requestIntervalTime = Integer.parseInt(intervalInput.getText());
                    int utilizationTime = Integer.parseInt(utilizationInput.getText());

                    int newProcessId = so.addProcess();
                    processNames.add(novoProcName);

                    Process novoProcesso = new Process(newProcessId, novoProcName, requestIntervalTime, utilizationTime);
                    novoProcesso.start();

                    addProcessStage.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Processo adicionado com sucesso!");
                    alert.showAndWait();
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Preencha os campos corretamente e informe um nome válido!");
                    alert.showAndWait();
                }
            });
        });

        //  Botão para remover processo
        Button btnRemoveProcess = new Button("Remover Processo");
        topMenu.getChildren().add(btnRemoveProcess);


        btnRemoveProcess.setOnAction(e -> {
            Stage removeProcessStage = new Stage();
            removeProcessStage.initModality(Modality.APPLICATION_MODAL);
            removeProcessStage.setTitle("Remover Processo");

            Label nameLabel = new Label("Nome do Processo:");
            TextField nameInput = new TextField();
            Button removeButton = new Button("Remover");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));
            layout.getChildren().addAll(nameLabel, nameInput, removeButton);

            Scene sceneRemove = new Scene(layout, 250, 150);
            removeProcessStage.setScene(sceneRemove);
            removeProcessStage.show();

            removeButton.setOnAction(event -> {
                String processName = nameInput.getText().trim();
                if (processName.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nome do processo não pode estar vazio!");
                    alert.showAndWait();
                    return;
                }

                // Encontra o ID do processo com base no nome
                int processId = -1;
                for (int i = 0; i < processNames.size(); i++) {
                    if (processNames.get(i).equals(processName)) {
                        processId = i;
                        break;
                    }
                }

                if (processId != -1) {
                    so.removeProcess(processId);
                    processNames.remove(processId); // Remove o nome da lista
                    removeProcessStage.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Processo removido com sucesso!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Processo com nome '" + processName + "' não encontrado!");
                    alert.showAndWait();
                }
            });
        });
    }

    private void updateMatrixColumns() {
        // Atualiza as colunas da matriz de alocação
        matrixCTable.getColumns().clear();
        TableColumn<ObservableList<String>, String> procColC = new TableColumn<>("Processo");
        procColC.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        matrixCTable.getColumns().add(procColC);

        // Atualiza as colunas da matriz de requisição
        matrixRTable.getColumns().clear();
        TableColumn<ObservableList<String>, String> procColR = new TableColumn<>("Processo");
        procColR.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        matrixRTable.getColumns().add(procColR);

        for (int j = 0; j < HelloApplication.arrayE.size(); j++) {
            final int colIndex = j + 1;
            TableColumn<ObservableList<String>, String> colC = new TableColumn<>("R" + (j+1));
            colC.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIndex)));
            matrixCTable.getColumns().add(colC);

            TableColumn<ObservableList<String>, String> colR = new TableColumn<>("R" + (j+1));
            colR.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIndex)));
            matrixRTable.getColumns().add(colR);
        }
    }

    private void updateTables() {
        // Obtém a lista de processos em deadlock
        List<Integer> deadlockedProcesses = so.detectDeadlock();

        // Atualiza a tabela de recursos
        ObservableList<ResourceRow> resourceRows = FXCollections.observableArrayList();
        for (int i = 0; i < HelloApplication.arrayE.size(); i++) {
            String nome = (i < resourceData.size()) ? resourceData.get(i).getName() : "R" + (i + 1);
            int total = HelloApplication.arrayE.get(i);
            int disponivel = HelloApplication.arrayA.get(i).availablePermits();
            resourceRows.add(new ResourceRow(nome, total, disponivel));
        }
        resourceTable.setItems(resourceRows);

        // Atualiza a tabela da Matriz de Alocação (C)
        ObservableList<ObservableList<String>> dataC = FXCollections.observableArrayList();
        for (int i = 0; i < HelloApplication.arrayC.size(); i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            String procName = (i < processNames.size()) ? processNames.get(i) : "P" + i;
            row.add(procName);
            for (int j = 0; j < HelloApplication.arrayC.get(i).size(); j++) {
                row.add(String.valueOf(HelloApplication.arrayC.get(i).get(j).availablePermits()));
            }
            dataC.add(row);
        }
        matrixCTable.setItems(dataC);

        // Atualiza a tabela da Matriz de Requisição (R)
        ObservableList<ObservableList<String>> dataR = FXCollections.observableArrayList();
        for (int i = 0; i < HelloApplication.arrayR.size(); i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            String procName = (i < processNames.size()) ? processNames.get(i) : "P" + i;
            row.add(procName);
            for (int j = 0; j < HelloApplication.arrayR.get(i).size(); j++) {
                row.add(String.valueOf(HelloApplication.arrayR.get(i).get(j).availablePermits()));
            }
            dataR.add(row);
        }
        matrixRTable.setItems(dataR);

        // Aplica o estilo vermelho às linhas dos processos em deadlock
        for (int processId : deadlockedProcesses) {
            if (processId < matrixCTable.getItems().size()) {
                // Obtém a linha correspondente ao processo em deadlock na tabela C
                matrixCTable.getItems().get(processId).forEach(cell -> {
                    matrixCTable.lookup(".table-row-cell").setStyle("-fx-background-color: red;");
                });
            }
            if (processId < matrixRTable.getItems().size()) {
                // Obtém a linha correspondente ao processo em deadlock na tabela R
                matrixRTable.getItems().get(processId).forEach(cell -> {
                    matrixRTable.lookup(".table-row-cell").setStyle("-fx-background-color: red;");
                });
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
/////////// Arquivo Process.java /////////////////////
package adapters;

import com.example.televisionproblem.HelloApplication;
import javafx.application.Platform;

import java.util.Random;

public class Process extends Thread {
    private int id;
    private String processName;  // Novo atributo
    private int requestIntervalTime;
    private int utilizationTime;

    public Process(int id, String processName, int requestIntervalTime, int utilizationTime) {
        this.id = id;
        this.processName = processName;
        this.requestIntervalTime = requestIntervalTime;
        this.utilizationTime = utilizationTime;
    }

    public void requestResource() {
        if (Thread.currentThread().isInterrupted()) {
            return; // Se a thread foi interrompida, não tenta acessar recursos
        }

        Random random = new Random();

        int resourceRange = HelloApplication.arrayE.size();
        int[] request = new int[resourceRange];
        for (int j = 0; j < resourceRange; j++) {
            // Gera um número aleatório entre 1 e o número máximo de recursos disponíveis
            request[j] = random.nextInt(HelloApplication.arrayE.get(j)) + 1;
        }

        // Requisitar um recurso de um elemento da lista available
        HelloApplication.so.setRequest(id, request);
        Platform.runLater(() -> {
            HelloApplication.messages.add("Processo " + processName + " solicitou: " + vectorToString(request));
        });

        boolean allocated = HelloApplication.so.requestResources(id, request);
        if (allocated) {
            Platform.runLater(() -> {
                HelloApplication.messages.add("Processo " + processName + " teve os recursos alocados. Iniciando execução...");
            });
            try {
                Thread.sleep(utilizationTime * 1000);
            } catch (InterruptedException e) {
                // Thread interrompida, finaliza o processo
                Thread.currentThread().interrupt(); // Restaura o status de interrupção
                return;
            }
            HelloApplication.so.releaseResources(id);
            Platform.runLater(() -> {
                HelloApplication.messages.add("Processo " + processName + " concluiu e liberou os recursos.");
            });
        } else {
            Platform.runLater(() -> {
                HelloApplication.messages.add("Processo " + processName + " não conseguiu recursos e permanecerá aguardando.");
            });
        }
    }

    private String vectorToString(int[] v) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < v.length; i++) {
            sb.append(v[i]);
            if (i < v.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) { // Verifica se a thread foi interrompida
                Thread.sleep(requestIntervalTime * 1000);
                requestResource();
            }
        } catch (InterruptedException e) {
            // Thread interrompida, finaliza o processo
            Platform.runLater(() -> {
                HelloApplication.messages.add("Processo " + processName + " foi interrompido e removido.");
            });
        }
    }
}
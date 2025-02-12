/////////// Arquivo Process.java /////////////////////
///
/// TODO: Trazer solicitar, utilizar e liberar recursos existentes no sistema que esta em SO e trazer para PROCESS.
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
        Random random = new Random();

        // Cria um vetor de solicitação com N recursos requisitados
        int[] request = new int[HelloApplication.arrayE.size()];
        boolean hasRequestedAtLeastOne = false;

        while (!hasRequestedAtLeastOne) { // Garante que pelo menos 1 recurso seja requisitado
            for (int j = 0; j < request.length; j++) {
                // Define quantas instâncias do recurso j serão requisitadas (entre 1 e o máximo disponível)
                int maxInstances = HelloApplication.arrayE.get(j);
                request[j] = random.nextInt(maxInstances) + 1; // Requisita entre 1 e maxInstances
                hasRequestedAtLeastOne = true; // Marca que pelo menos 1 recurso foi requisitado
            }
        }

        // Requisita os recursos (atualiza a matriz R)
        HelloApplication.so.setRequest(id, request);
        Platform.runLater(() -> {
            HelloApplication.messages.add("Processo " + processName + " solicitou: " + vectorToString(request));
        });

        // Tenta alocar e usar os recursos
        while (true) {
            // Tenta alocar todos os recursos solicitados
            boolean allocated = HelloApplication.so.requestAllResources(id, request);
            if (allocated) {
                Platform.runLater(() -> {
                    HelloApplication.messages.add("Processo " + processName + " alocou todos os recursos. Iniciando execução...");
                });
                try {
                    Thread.sleep(utilizationTime * 1000); // Usa o recurso por ΔTu segundos
                } catch (InterruptedException e) {
                    // Tratamento de exceção
                }
                HelloApplication.so.releaseResources(id); // Libera os recursos após o uso
                Platform.runLater(() -> {
                    HelloApplication.messages.add("Processo " + processName + " liberou os recursos.");
                });
                break; // Sai do loop após usar e liberar os recursos
            } else {
                // Se não conseguir alocar, aguarda um tempo antes de tentar novamente
                try {
                    Thread.sleep(1000); // Aguarda 1 segundo antes de tentar novamente
                } catch (InterruptedException e) {
                    // Tratamento de exceção
                }
            }
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
            while (true) {
                // Verifica se o processo já está utilizando algum recurso
                boolean isUsingResource = false;
                for (int j = 0; j < HelloApplication.arrayC.get(id).size(); j++) {
                    if (HelloApplication.arrayC.get(id).get(j).availablePermits() > 0) {
                        isUsingResource = true;
                        break;
                    }
                }

                // Se não estiver utilizando nenhum recurso, solicita um novo
                if (!isUsingResource) {
                    Thread.sleep(requestIntervalTime * 1000); // Aguarda ΔTs segundos
                    requestResource(); // Solicita um recurso
                } else {
                    // Se estiver utilizando um recurso, aguarda até liberá-lo
                    Thread.sleep(100); // Aguarda um curto período antes de verificar novamente
                }
            }
        } catch (InterruptedException e) {
            // Tratamento de exceção
        }
    }
}
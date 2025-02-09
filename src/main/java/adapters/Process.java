/////////// Arquivo Process.java /////////////////////
package adapters;

import com.example.televisionproblem.HelloApplication;

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

        int resourceRange = HelloApplication.arrayE.size();
        int[] request = new int[resourceRange];
        for (int j = 0; j < resourceRange; j++) {
            // Gera um número aleatório entre 1 e o número máximo de recursos disponíveis
            request[j] = random.nextInt(HelloApplication.arrayE.get(j)) + 1;
        }

        //requisitar um recurso de um elemento da lista avaiable

        HelloApplication.so.setRequest(id, request);
        HelloApplication.messages.add("Processo " + processName + " solicitou: " + vectorToString(request));
        boolean allocated = HelloApplication.so.requestResources(id, request);
        if (allocated) {
            HelloApplication.messages.add("Processo " + processName + " teve os recursos alocados. Iniciando execução...");
            try {
                Thread.sleep(utilizationTime * 1000);
            } catch (InterruptedException e) { }
            HelloApplication.so.releaseResources(id);
            HelloApplication.messages.add("Processo " + processName + " concluiu e liberou os recursos.");
        } else {
            HelloApplication.messages.add("Processo " + processName + " não conseguiu recursos e permanecerá aguardando.");
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
                Thread.sleep(requestIntervalTime * 1000);
                requestResource();
            }
        } catch (InterruptedException e) {
            // tratamento, se necessário
        }
    }
}
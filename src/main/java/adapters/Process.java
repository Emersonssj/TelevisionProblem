package adapters;

import java.util.Random;

public class Process extends Thread {
    private int id;
    private String processName;
    private int requestIntervalTime;
    private int utilizationTime;
    private SO so;
    private Random random = new Random();

    public Process(int id, String processName, int requestIntervalTime, int utilizationTime, SO so) {
        this.id = id;
        this.processName = processName;
        this.requestIntervalTime = requestIntervalTime;
        this.utilizationTime = utilizationTime;
        this.so = so;
    }

    private int[] generateRequest() {
        int[] E = so.getE(); // Obtém o vetor de recursos existentes
        int[] req = new int[E.length];
        for (int j = 0; j < E.length; j++) {
            req[j] = random.nextInt(E[j] + 1); // Solicita até o limite existente
        }
        return req;
    }

    public void requestResource() {
        int[] request = generateRequest();
        so.setRequest(id, request); // Define a requisição na matriz R
        System.out.println("Processo " + processName + " solicitou: " + vectorToString(request));

        boolean allocated = so.requestResources(id, request); // Tenta alocar
        if (allocated) {
            System.out.println("Processo " + processName + " teve os recursos alocados. Iniciando execução...");
            try {
                Thread.sleep(utilizationTime * 1000); // Simula o tempo de utilização
            } catch (InterruptedException e) { }

            so.releaseResources(id); // Libera os recursos
            System.out.println("Processo " + processName + " concluiu e liberou os recursos.");
        } else {
            System.out.println("Processo " + processName + " não conseguiu recursos e permanecerá aguardando.");
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
                Thread.sleep(requestIntervalTime * 1000); // Intervalo entre requisições
                requestResource();
            }
        } catch (InterruptedException e) {
            // Tratamento de interrupção, se necessário
        }
    }
}
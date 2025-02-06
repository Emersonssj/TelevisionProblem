package adapters;

import java.util.Random;

public class Process extends Thread {
    private int id;
    private String processName;  // Novo atributo
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
        int numResources = so.getE().length;
        int[] req = new int[numResources];
        for (int j = 0; j < numResources; j++) {
            // Gera uma quantidade aleatória, mas não excede os recursos disponíveis
            int availableResources = so.getE()[j];
            req[j] = random.nextInt(availableResources) + 1;
        }
        return req;
    }

    public void requestResource() {
        int[] request = generateRequest();
        so.setRequest(id, request);
        System.out.println("Processo " + processName + " solicitou: " + vectorToString(request));
        boolean allocated = so.requestResources(id, request);
        if (allocated) {
            System.out.println("Processo " + processName + " teve os recursos alocados. Iniciando execução...");
            try {
                Thread.sleep(utilizationTime * 1000);
            } catch (InterruptedException e) {
                // Tratamento de interrupção
            }
            so.releaseResources(id);
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
                Thread.sleep(requestIntervalTime * 1000);
                requestResource();
            }
        } catch (InterruptedException e) {
            // Tratamento de interrupção
            System.out.println("Processo " + processName + " foi interrompido.");
        }
    }
}
package adapters;

import com.example.televisionproblem.HelloApplication;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SO extends Thread {
    private int timeDuration; // Pode ser utilizado para controle de execução (não usado aqui)

    // Controle do número de processos e recursos
    private int numProcesses;
    private int numResources;

    public SO(int timeDuration, int[] E, int numProcesses) {
        this.timeDuration = timeDuration;
        this.numResources = E.length;
        this.numProcesses = numProcesses;

        // Inicializa os semáforos
        for (int i = 0; i < E.length; i++) {
            HelloApplication.arrayE.add(E[i]);
            HelloApplication.arrayA.add(new Semaphore(E[i]));
        }

        for (int i = 0; i < numProcesses; i++) {
            ArrayList<Semaphore> rowC = new ArrayList<>();
            ArrayList<Semaphore> rowR = new ArrayList<>();
            for (int j = 0; j < numResources; j++) {
                rowC.add(new Semaphore(0));
                rowR.add(new Semaphore(0));
            }
            HelloApplication.arrayC.add(rowC);
            HelloApplication.arrayR.add(rowR);
        }
    }

    public synchronized void setRequest(int processId, int[] request) {
        if (processId < HelloApplication.arrayR.size()) {
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayR.get(processId).get(j).release(request[j]);
            }
        }
    }

    public synchronized boolean requestResources(int processId, int[] request) {
        for (int j = 0; j < numResources; j++) {
            if (request[j] > HelloApplication.arrayA.get(j).availablePermits()) {
                Platform.runLater(() -> {
                    HelloApplication.messages.add("Processo " + processId + " não conseguiu recursos e permanecerá aguardando.");
                });
                return false;
            }
        }
        for (int j = 0; j < numResources; j++) {
            try {
                HelloApplication.arrayA.get(j).acquire(request[j]);
                HelloApplication.arrayC.get(processId).get(j).release(request[j]);
                HelloApplication.arrayR.get(processId).get(j).acquire(request[j]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Platform.runLater(() -> {
            HelloApplication.messages.add("Processo " + processId + " teve os recursos alocados. Iniciando execução...");
        });
        return true;
    }

    public synchronized void releaseResources(int processId) {
        for (int j = 0; j < numResources; j++) {
            int permits = HelloApplication.arrayC.get(processId).get(j).availablePermits();
            if (permits > 0) {
                HelloApplication.arrayC.get(processId).get(j).acquireUninterruptibly(permits);
                HelloApplication.arrayA.get(j).release(permits);
            }
        }
    }

    public synchronized List<Integer> detectDeadlock() {
        boolean[] finish = new boolean[numProcesses];
        int[] work = new int[numResources];
        for (int j = 0; j < numResources; j++) {
            work[j] = HelloApplication.arrayA.get(j).availablePermits();
        }
        boolean progress;
        do {
            progress = false;
            for (int i = 0; i < numProcesses; i++) {
                if (!finish[i]) {
                    boolean canFinish = true;
                    for (int j = 0; j < numResources; j++) {
                        if (HelloApplication.arrayR.get(i).get(j).availablePermits() > work[j]) {
                            canFinish = false;
                            break;
                        }
                    }
                    if (canFinish) {
                        for (int j = 0; j < numResources; j++) {
                            work[j] += HelloApplication.arrayC.get(i).get(j).availablePermits();
                        }
                        finish[i] = true;
                        progress = true;
                    }
                }
            }
        } while (progress);

        // Identifica os processos em deadlock (aqueles que não podem terminar)
        List<Integer> deadlockedProcesses = new ArrayList<>();
        for (int i = 0; i < numProcesses; i++) {
            if (!finish[i]) {
                deadlockedProcesses.add(i);
            }
        }
        return deadlockedProcesses;
    }

    // Método para adicionar um novo processo (expande as matrizes e retorna o novo id)
    public synchronized int addProcess() {
        int newProcessId = numProcesses;
        ArrayList<Semaphore> newRowC = new ArrayList<>();
        ArrayList<Semaphore> newRowR = new ArrayList<>();
        for (int j = 0; j < numResources; j++) {
            newRowC.add(new Semaphore(0));
            newRowR.add(new Semaphore(0));
        }
        HelloApplication.arrayC.add(newRowC);
        HelloApplication.arrayR.add(newRowR);
        numProcesses++;
        HelloApplication.messages.add("Novo processo adicionado. Total de processos agora: " + numProcesses);
        return newProcessId;
    }

    public synchronized void removeProcess(int processId) {
        if (processId < numProcesses) {
            // Interrompe a thread do processo, se estiver rodando
            for (Thread thread : Thread.getAllStackTraces().keySet()) {
                if (thread instanceof Process && ((Process) thread).getId() == processId) {
                    thread.interrupt(); // Interrompe a thread do processo
                    break;
                }
            }

            // Libera os recursos alocados pelo processo
            releaseResources(processId);

            // Remove o processo das estruturas de dados
            HelloApplication.arrayC.remove(processId);
            HelloApplication.arrayR.remove(processId);

            // Atualiza o número de processos
            numProcesses--;

            // Adiciona uma mensagem ao log
            Platform.runLater(() -> {
                HelloApplication.messages.add("Processo " + processId + " removido com sucesso.");
            });
        } else {
            Platform.runLater(() -> {
                HelloApplication.messages.add("Processo " + processId + " não encontrado.");
            });
        }
    }

    public synchronized boolean requestAllResources(int processId, int[] request) {
        // Verifica se todos os recursos solicitados estão disponíveis
        for (int j = 0; j < numResources; j++) {
            if (request[j] > HelloApplication.arrayA.get(j).availablePermits()) {
                // Se algum recurso não estiver disponível, retorna false
                Platform.runLater(() -> {
                    HelloApplication.messages.add("Processo " + processId + " não conseguiu alocar todos os recursos. Aguardando...");
                });
                return false;
            }
        }

        // Se todos os recursos estiverem disponíveis, aloca-os
        for (int j = 0; j < numResources; j++) {
            if (request[j] > 0) {
                try {
                    HelloApplication.arrayA.get(j).acquire(request[j]);
                    HelloApplication.arrayC.get(processId).get(j).release(request[j]);
                    HelloApplication.arrayR.get(processId).get(j).acquire(request[j]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        Platform.runLater(() -> {
            HelloApplication.messages.add("Processo " + processId + " alocou todos os recursos solicitados.");
        });
        return true;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(1000);
                List<Integer> deadlockedProcesses = detectDeadlock();
                Platform.runLater(() -> {
                    if (!deadlockedProcesses.isEmpty()) {
                        HelloApplication.messages.add("DEADLOCK DETECTADO! Processos envolvidos: " + deadlockedProcesses);
                    }
//                    else {
//                       // HelloApplication.messages.add("Sistema seguro. Nenhum deadlock detectado.");
//                    }
                });
            }
        } catch (InterruptedException e) {
            // Tratamento, se necessário
        }
    }
}
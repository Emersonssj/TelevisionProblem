package adapters;

import com.example.televisionproblem.HelloApplication;

import java.util.concurrent.Semaphore;
import java.util.List;

public class SO extends Thread {
    private int timeDuration; // Pode ser utilizado para controle de execução (não usado aqui)

    // Controle do número de processos e recursos
    private int numProcesses;
    private int numResources;

    public SO(int timeDuration, int numProcesses) {
        this.timeDuration = timeDuration;
        this.numResources = HelloApplication.arrayE.size();
        this.numProcesses = numProcesses;
    }

    public void setRequest(int processId, int[] request) {
        if (processId < HelloApplication.arrayR.size()) {
            try {
                // Bloqueando o acesso ao vetor R enquanto a requisição é atualizada
                for (int j = 0; j < numResources; j++) {
                    HelloApplication.arrayR.get(processId).get(j).acquire();
                    HelloApplication.arrayR.get(processId).get(j).release(request[j]); // Atualiza o recurso solicitado
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean requestResources(int processId, int[] request) {
        try {
            // Verifica se os recursos estão disponíveis
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayA.get(j).acquire(); // Acessa semáforo dos recursos disponíveis
                if (request[j] > HelloApplication.arrayA.get(j).availablePermits()) {
                    // Se não houver recursos suficientes, libera os semáforos já adquiridos e retorna false
                    for (int k = 0; k < j; k++) {
                        HelloApplication.arrayA.get(k).release();
                    }
                    return false;
                }
            }

            // Se os recursos estiverem disponíveis, faz a alocação
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayA.get(j).acquire(request[j]); // Reduz os recursos disponíveis
                HelloApplication.arrayC.get(processId).get(j).acquire();
                HelloApplication.arrayC.get(processId).get(j).release(request[j]); // Atualiza a alocação
                HelloApplication.arrayR.get(processId).get(j).acquire();
                HelloApplication.arrayR.get(processId).get(j).release(0); // Reseta a requisição
            }

            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void releaseResources(int processId) {
        try {
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayC.get(processId).get(j).acquire();
                int allocated = HelloApplication.arrayC.get(processId).get(j).availablePermits();
                HelloApplication.arrayC.get(processId).set(j, new Semaphore(0)); // Libera os recursos alocados
                HelloApplication.arrayC.get(processId).get(j).release();
                HelloApplication.arrayA.get(j).release(allocated); // Devolve os recursos ao total disponível
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean detectDeadlock() {
        boolean[] finish = new boolean[numProcesses];
        int[] work = new int[numResources];

        // Inicializa o vetor work com os recursos disponíveis (arrayA)
        try {
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayA.get(j).acquire();
                work[j] = HelloApplication.arrayA.get(j).availablePermits();
                HelloApplication.arrayA.get(j).release();
            }

            boolean progress;
            do {
                progress = false;
                for (int i = 0; i < numProcesses; i++) {
                    if (!finish[i]) {
                        boolean canFinish = true;
                        for (int j = 0; j < numResources; j++) {
                            HelloApplication.arrayR.get(i).get(j).acquire();
                            if (HelloApplication.arrayR.get(i).get(j).availablePermits() > work[j]) {
                                canFinish = false;
                            }
                            HelloApplication.arrayR.get(i).get(j).release();
                        }

                        if (canFinish) {
                            for (int j = 0; j < numResources; j++) {
                                HelloApplication.arrayC.get(i).get(j).acquire();
                                work[j] += HelloApplication.arrayC.get(i).get(j).availablePermits(); // Libera os recursos
                                HelloApplication.arrayC.get(i).get(j).release();
                            }
                            finish[i] = true;
                            progress = true;
                        }
                    }
                }
            } while (progress);

            for (boolean f : finish) {
                if (!f) {
                    return true; // Deadlock detectado
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(1000);
                if (detectDeadlock()) {
                    System.out.println("DEADLOCK DETECTADO!");
                } else {
                    System.out.println("Sistema seguro. Nenhum deadlock detectado.");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

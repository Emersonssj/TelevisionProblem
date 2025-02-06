package adapters;

import com.example.televisionproblem.HelloApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class SO extends Thread {
    private int timeDuration;

    // Controle do número de processos e recursos
    private int numProcesses;
    private int numResources;

    public SO(int timeDuration, int[] E, int numProcesses) {
        this.timeDuration = timeDuration;
        this.numResources = E.length;
        this.numProcesses = numProcesses;

        // Inicializar semáforos e arrays em HelloApplication
        for (int i = 0; i < numResources; i++) {
            HelloApplication.arrayE.add(new Semaphore(1));
            HelloApplication.arrayA.add(new Semaphore(1));
        }

        for (int i = 0; i < numProcesses; i++) {
            ArrayList<Semaphore> rowC = new ArrayList<>();
            ArrayList<Semaphore> rowR = new ArrayList<>();
            for (int j = 0; j < numResources; j++) {
                rowC.add(new Semaphore(1));
                rowR.add(new Semaphore(1));
            }
            HelloApplication.arrayC.add(rowC);
            HelloApplication.arrayR.add(rowR);
        }
    }

    public void setRequest(int processId, int[] request) {
        try {
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayR.get(processId).get(j).acquire();
            }
            // Atualiza a matriz R em HelloApplication
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayR.get(processId).set(j, new Semaphore(request[j]));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayR.get(processId).get(j).release();
            }
        }
    }

    public boolean requestResources(int processId, int[] request) {
        try {
            // Verifica se há recursos disponíveis
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayA.get(j).acquire();
                if (request[j] > HelloApplication.arrayA.get(j).availablePermits()) {
                    for (int k = 0; k <= j; k++) {
                        HelloApplication.arrayA.get(k).release();
                    }
                    return false;
                }
            }

            // Aloca os recursos
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayA.get(j).acquire(request[j]);
                HelloApplication.arrayC.get(processId).get(j).acquire();
                HelloApplication.arrayC.get(processId).get(j).release(request[j]);
                HelloApplication.arrayR.get(processId).get(j).acquire();
                HelloApplication.arrayR.get(processId).get(j).release(0); // Zera a requisição
                HelloApplication.arrayA.get(j).release();
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
                HelloApplication.arrayA.get(j).acquire();
                HelloApplication.arrayC.get(processId).get(j).acquire();
                HelloApplication.arrayA.get(j).release(HelloApplication.arrayC.get(processId).get(j).availablePermits());
                HelloApplication.arrayC.get(processId).get(j).release(0); // Zera a alocação
                HelloApplication.arrayA.get(j).release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean detectDeadlock() {
        boolean[] finish = new boolean[numProcesses];
        int[] work = new int[numResources];

        try {
            // Inicializa o vetor work com os recursos disponíveis
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
                                HelloApplication.arrayR.get(i).get(j).release();
                                break;
                            }
                            HelloApplication.arrayR.get(i).get(j).release();
                        }
                        if (canFinish) {
                            for (int j = 0; j < numResources; j++) {
                                HelloApplication.arrayC.get(i).get(j).acquire();
                                work[j] += HelloApplication.arrayC.get(i).get(j).availablePermits();
                                HelloApplication.arrayC.get(i).get(j).release();
                            }
                            finish[i] = true;
                            progress = true;
                        }
                    }
                }
            } while (progress);

            // Verifica se há processos não finalizados
            for (boolean f : finish) {
                if (!f) {
                    return true;
                }
            }
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para adicionar um novo recurso (expande as listas e atualiza as matrizes)
    public void addResource(int totalInstances) {
        try {
            // Adiciona um novo semáforo para o recurso em arrayE e arrayA
            HelloApplication.arrayE.add(new Semaphore(totalInstances));
            HelloApplication.arrayA.add(new Semaphore(totalInstances));

            // Expande as matrizes arrayC e arrayR para incluir o novo recurso
            for (int i = 0; i < numProcesses; i++) {
                HelloApplication.arrayC.get(i).add(new Semaphore(0)); // Inicialmente, nenhum recurso alocado
                HelloApplication.arrayR.get(i).add(new Semaphore(0)); // Inicialmente, nenhuma requisição
            }

            numResources++;
            System.out.println("Novo recurso adicionado. Total de recursos agora: " + numResources);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para adicionar um novo processo (expande as matrizes e retorna o novo id)
    public int addProcess() {
        int newProcessId = numProcesses;

        // Adiciona uma nova linha para o processo nas matrizes arrayC e arrayR
        ArrayList<Semaphore> newCRow = new ArrayList<>();
        ArrayList<Semaphore> newRRow = new ArrayList<>();
        for (int j = 0; j < numResources; j++) {
            newCRow.add(new Semaphore(0)); // Inicialmente, nenhum recurso alocado
            newRRow.add(new Semaphore(0)); // Inicialmente, nenhuma requisição
        }
        HelloApplication.arrayC.add(newCRow);
        HelloApplication.arrayR.add(newRRow);

        numProcesses++;
        System.out.println("Novo processo adicionado. Total de processos agora: " + numProcesses);
        return newProcessId;
    }

    // Retorna o vetor E (recursos existentes)
    public int[] getE() {
        int[] E = new int[numResources];
        for (int j = 0; j < numResources; j++) {
            E[j] = HelloApplication.arrayE.get(j).availablePermits();
        }
        return E;
    }

    // Retorna o vetor A (recursos disponíveis)
    public int[] getA() {
        int[] A = new int[numResources];
        for (int j = 0; j < numResources; j++) {
            A[j] = HelloApplication.arrayA.get(j).availablePermits();
        }
        return A;
    }

    // Retorna a matriz C (alocação de recursos)
    public int[][] getC() {
        int[][] C = new int[numProcesses][numResources];
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                C[i][j] = HelloApplication.arrayC.get(i).get(j).availablePermits();
            }
        }
        return C;
    }

    // Retorna a matriz R (requisição de recursos)
    public int[][] getR() {
        int[][] R = new int[numProcesses][numResources];
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                R[i][j] = HelloApplication.arrayR.get(i).get(j).availablePermits();
            }
        }
        return R;
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
            // tratamento, se necessário
        }
    }
}

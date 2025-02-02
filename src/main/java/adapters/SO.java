package adapters;

import com.example.televisionproblem.HelloApplication;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class SO extends Thread {
    private int timeDuration;
    private int numProcesses;
    private int numResources;

    public SO(int timeDuration, int[] E, int numProcesses) {
        this.timeDuration = timeDuration;
        this.numResources = E.length;
        this.numProcesses = numProcesses;

        // Inicializar semáforos
        for (int i = 0; i < numResources; i++) {
            HelloApplication.arrayE.add(new Semaphore(E[i], true)); // Recursos existentes
            HelloApplication.arrayA.add(new Semaphore(E[i], true)); // Recursos disponíveis
        }

        // Inicializar matrizes C e R
        for (int i = 0; i < numProcesses; i++) {
            ArrayList<Semaphore> rowC = new ArrayList<>();
            ArrayList<Semaphore> rowR = new ArrayList<>();
            for (int j = 0; j < numResources; j++) {
                rowC.add(new Semaphore(0, true)); // Inicialmente, nenhum recurso alocado
                rowR.add(new Semaphore(0, true)); // Inicialmente, nenhuma requisição
            }
            HelloApplication.arrayC.add(rowC);
            HelloApplication.arrayR.add(rowR);
        }
    }

    // Método para adicionar um novo recurso
    public void addResource(int totalInstances) {
        try {
            HelloApplication.arrayE.add(new Semaphore(totalInstances));
            HelloApplication.arrayA.add(new Semaphore(totalInstances));

            // Expande as matrizes C e R para incluir o novo recurso
            for (int i = 0; i < numProcesses; i++) {
                HelloApplication.arrayC.get(i).add(new Semaphore(0));
                HelloApplication.arrayR.get(i).add(new Semaphore(0));
            }

            numResources++;
            System.out.println("Novo recurso adicionado. Total de recursos agora: " + numResources);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para adicionar um novo processo
    public int addProcess() {
        int newProcessId = numProcesses;

        ArrayList<Semaphore> newCRow = new ArrayList<>();
        ArrayList<Semaphore> newRRow = new ArrayList<>();
        for (int j = 0; j < numResources; j++) {
            newCRow.add(new Semaphore(0));
            newRRow.add(new Semaphore(0));
        }
        HelloApplication.arrayC.add(newCRow);
        HelloApplication.arrayR.add(newRRow);

        numProcesses++;
        System.out.println("Novo processo adicionado. Total de processos agora: " + numProcesses);
        return newProcessId;
    }


    public void setRequest(int processId, int[] request) {
        try {
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayR.get(processId).get(j).acquire(); // Bloqueia o recurso
                HelloApplication.arrayR.get(processId).get(j).release(request[j]); // Define a requisição
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public boolean requestResources(int processId, int[] request) {
        try {
            // Verifica se há recursos suficientes disponíveis
            for (int j = 0; j < numResources; j++) {
                if (request[j] > HelloApplication.arrayA.get(j).availablePermits()) {
                    return false;
                }
            }

            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayA.get(j).acquire(request[j]); // Reduz os recursos disponíveis
                HelloApplication.arrayC.get(processId).get(j).release(request[j]); // Aloca para o processo
                HelloApplication.arrayR.get(processId).get(j).acquire(request[j]); // Remove a requisição
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
                int allocated = HelloApplication.arrayC.get(processId).get(j).availablePermits();
                HelloApplication.arrayC.get(processId).get(j).acquire(allocated); // Remove a alocação
                HelloApplication.arrayA.get(j).release(allocated); // Devolve aos recursos disponíveis
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean detectDeadlock() {
        boolean[] finish = new boolean[numProcesses];
        int[] work = new int[numResources];
        try {
            for (int j = 0; j < numResources; j++) {
                HelloApplication.arrayA.get(j).acquire();
                work[j] = HelloApplication.arrayA.get(j).availablePermits();
                HelloApplication.arrayA.get(j).release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean progress;
        do {
            progress = false;
            for (int i = 0; i < numProcesses; i++) {
                if (!finish[i]) {
                    boolean canFinish = true;
                    try {
                        for (int j = 0; j < numResources; j++) {
                            HelloApplication.arrayR.get(i).get(j).acquire();
                            if (HelloApplication.arrayR.get(i).get(j).availablePermits() > work[j]) {
                                canFinish = false;
                            }
                            HelloApplication.arrayR.get(i).get(j).release();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (canFinish) {
                        try {
                            for (int j = 0; j < numResources; j++) {
                                HelloApplication.arrayC.get(i).get(j).acquire();
                                work[j] += HelloApplication.arrayC.get(i).get(j).availablePermits();
                                HelloApplication.arrayC.get(i).get(j).release();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finish[i] = true;
                        progress = true;
                    }
                }
            }
        } while (progress);

        for (boolean f : finish) {
            if (!f) {
                return true;
            }
        }
        return false;
    }


    public int[] getE() {
        int[] E = new int[numResources];
        for (int i = 0; i < numResources; i++) {
            E[i] = HelloApplication.arrayE.get(i).availablePermits();
        }
        return E;
    }


    public int[] getA() {
        int[] A = new int[numResources];
        for (int i = 0; i < numResources; i++) {
            A[i] = HelloApplication.arrayA.get(i).availablePermits();
        }
        return A;
    }


    public int[][] getC() {
        int[][] C = new int[numProcesses][numResources];
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                C[i][j] = HelloApplication.arrayC.get(i).get(j).availablePermits();
            }
        }
        return C;
    }

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
            System.out.println("Eu preciso tratar o erro aqui");
        }
    }
}
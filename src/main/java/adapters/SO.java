package adapters;

import java.util.Arrays;

public class SO extends Thread {
    private int timeDuration; // Pode ser utilizado para controle de execução (não usado aqui)

    // Gerenciamento de recursos:
    private int[] E; // Vetor dos recursos existentes (total de instâncias)
    private int[] A; // Vetor dos recursos disponíveis
    private int[][] C; // Matriz de alocação: C[i][j] = quantidade do recurso j alocado para o processo  (talvez eu tenha que mudar)i
    private int[][] R; // Matriz de requisição: R[i][j] = quantidade do recurso j requisitado pelo processo (talvez eu tenha que mudar isso) i

    // Controle do número de processos e recursos
    private int numProcesses;
    private int numResources;

    public SO(int timeDuration, int[] E, int numProcesses) {
        this.timeDuration = timeDuration;
        this.E = Arrays.copyOf(E, E.length);
        this.A = Arrays.copyOf(E, E.length); // inicialmente, todos os recursos estão disponíveis
        this.numResources = E.length;
        this.numProcesses = numProcesses;
        this.C = new int[numProcesses][numResources];
        this.R = new int[numProcesses][numResources];
    }

    public void setRequest(int processId, int[] request) {
        if (processId < R.length) {
            R[processId] = Arrays.copyOf(request, numResources);
        }
    }

    public boolean requestResources(int processId, int[] request) {
        for (int j = 0; j < numResources; j++) {
            if (request[j] > A[j]) {
                return false;
            }
        }
        for (int j = 0; j < numResources; j++) {
            A[j] -= request[j];
            C[processId][j] += request[j];
            R[processId][j] = 0;
        }
        return true;
    }

    public void releaseResources(int processId) {
        for (int j = 0; j < numResources; j++) {
            A[j] += C[processId][j];
            C[processId][j] = 0;
        }
    }

    public boolean detectDeadlock() {
        boolean[] finish = new boolean[numProcesses];
        int[] work = Arrays.copyOf(A, numResources);
        boolean progress;
        do {
            progress = false;
            for (int i = 0; i < numProcesses; i++) {
                if (!finish[i]) {
                    boolean canFinish = true;
                    for (int j = 0; j < numResources; j++) {
                        if (R[i][j] > work[j]) {
                            canFinish = false;
                            break;
                        }
                    }
                    if (canFinish) {
                        for (int j = 0; j < numResources; j++) {
                            work[j] += C[i][j];
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

    // Método para adicionar um novo recurso (expande os arrays e atualiza as matrizes)
    public void addResource(int totalInstances) {
        int newNumResources = numResources + 1;
        int[] newE = new int[newNumResources];
        int[] newA = new int[newNumResources];
        for (int j = 0; j < numResources; j++) {
            newE[j] = E[j];
            newA[j] = A[j];
        }
        newE[newNumResources - 1] = totalInstances;
        newA[newNumResources - 1] = totalInstances;
        E = newE;
        A = newA;
        for (int i = 0; i < numProcesses; i++) {
            int[] newCRow = new int[newNumResources];
            int[] newRRow = new int[newNumResources];
            for (int j = 0; j < numResources; j++) {
                newCRow[j] = C[i][j];
                newRRow[j] = R[i][j];
            }
            C[i] = newCRow;
            R[i] = newRRow;
        }
        numResources = newNumResources;
        System.out.println("Novo recurso adicionado. Total de recursos agora: " + numResources);
    }

    // Método para adicionar um novo processo (expande as matrizes e retorna o novo id)
    public int addProcess() {
        int newProcessId = numProcesses;
        int newNumProcesses = numProcesses + 1;
        int[][] newC = new int[newNumProcesses][numResources];
        int[][] newR = new int[newNumProcesses][numResources];
        for (int i = 0; i < numProcesses; i++) {
            newC[i] = C[i];
            newR[i] = R[i];
        }
        newC[newProcessId] = new int[numResources];
        newR[newProcessId] = new int[numResources];
        C = newC;
        R = newR;
        numProcesses = newNumProcesses;
        System.out.println("Novo processo adicionado. Total de processos agora: " + numProcesses);
        return newProcessId;
    }

    public int[] getE() { return Arrays.copyOf(E, numResources); }
    public int[] getA() { return Arrays.copyOf(A, numResources); }
    public int[][] getC() { return C; }
    public int[][] getR() { return R; }

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
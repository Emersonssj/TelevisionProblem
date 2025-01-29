package adapters;

public class Process extends Thread {
    public int id;
    public int requestIntervalTime;
    public int utilizationTime;

    public Process(int id, int requestIntervalTime, int utilizationTime) {
        this.id = id;
        this.requestIntervalTime = requestIntervalTime;
        this.utilizationTime = utilizationTime;
    }

    public void requestResource() {

//        4. Encontre o vetor de recursos disponíveis A;
//        5. Percorra as linhas da matriz R verificando se algum dos processos
//        poderá ser executado com os recursos disponíveis A;
//          • Se sim, então
//          • Simule a entrega dos recursos para o processo;
//          • Atualize C, R e A;
//          • Simule a conclusão do processo e a liberação dos seus
//           recursos alocados;
//          • Atualize o vetor A;
//          • Volte para o passo 5

    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(requestIntervalTime * 1000);
                requestResource();
            }
        } catch (Exception e) {
        }
    }
}

package adapters;

public class Hospede extends Thread {
    private int id;
    private int canal;
    private int ttv;
    private int td;
    private Semaforo semaforo;

    public Hospede(int id, int canal, int ttv, int td, Semaforo semaforo) {
        this.id = id;
        this.canal = canal;
        this.ttv = ttv;
        this.td = td;
        this.semaforo = semaforo;
    }

    @Override
    public void run() {
        while (true) {
            semaforo.adquirir();
            try {
                System.out.println("Hóspede " + id + " está assistindo ao canal " + canal);
                Thread.sleep(ttv * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Erro: " + e.getMessage());
            } finally {
                semaforo.liberar();
                System.out.println("Hóspede " + id + " terminou de assistir e liberou a televisão.");
            }

            System.out.println("Hóspede " + id + " está descansando.");
            try {
                Thread.sleep(td * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }
}

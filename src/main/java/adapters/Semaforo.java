package adapters;

import java.util.concurrent.Semaphore;

public class Semaforo {
    private Semaphore lock;

    public Semaforo() {
        this.lock = new Semaphore(1);
    }

    public void adquirir() {
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Erro ao adquirir o semáforo: " + e.getMessage());
        }
    }

    public void liberar() {
        lock.release();
    }
}

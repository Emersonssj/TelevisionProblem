package adapters;

import com.example.televisionproblem.HelloApplication;

public class Hospede extends Thread {
    public int id;
    public int canal;
    public int ttv;
    public int td;

    public Hospede(int id, int channel, int ttv, int td) {
        this.id = id;
        this.canal = channel;
        this.ttv = ttv;
        this.td = td;
    }

    public void watchTV() throws InterruptedException {
        //    public static final Semaphore mutex = new Semaphore(1);
        //    public static final Semaphore changeChannel = new Semaphore(1);
        //    public static int currentChannel = 0;
        //    public static int currentWatchers = 0;
        // verificar se tv livre;
        // se tiver assiste, se nao verificar o canal;
        // se o canal for o msm assistir, se nao bloqueado
        HelloApplication.mutex.acquire();
        if (HelloApplication.currentWatchers == 0) {
            HelloApplication.currentChannel = canal;
            HelloApplication.currentWatchers++;
            HelloApplication.mutex.release();
        }
        else if (HelloApplication.currentChannel == canal) {
            HelloApplication.currentWatchers++;
            HelloApplication.mutex.release();
        }
        else {
            HelloApplication.mutex.release();
            HelloApplication.changeChannel.acquire();
            HelloApplication.mutex.acquire();
            HelloApplication.currentChannel = canal;
            HelloApplication.currentWatchers++;
            HelloApplication.mutex.release();
        }

        while (ttv > 1) {
            ttv--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }


        HelloApplication.mutex.acquire();
        HelloApplication.currentWatchers --;
        if(HelloApplication.currentWatchers == 0) HelloApplication.changeChannel.release();
        HelloApplication.mutex.release();
    }

    public void rest() throws InterruptedException {
    }

    @Override
    public void run() {
        try {
            while (true) {
                watchTV();
                rest();
            }
        } catch (InterruptedException e) {
        }
    }
}
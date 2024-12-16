package adapters;

import com.example.televisionproblem.HelloApplication;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

public class Hospede extends Thread {
    public int id;
    public int canal;
    public int ttv;
    public int td;
    public Circle circle;

    public Hospede(int id, int channel, int ttv, int td, Circle circle) {
        this.id = id;
        this.canal = channel;
        this.ttv = ttv;
        this.td = td;
        this.circle = circle;
    }

    TimerTask reduceTTV = new TimerTask() {
        @Override
        public void run() {
            ttv--;
        }
    };

    public void watchTV() throws InterruptedException {
        Timer timer = new Timer();
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

        // Movendo para a posição inicial para "assistir TV"
        TranslateTransition moveToWatchTV = new TranslateTransition(Duration.seconds(2), circle);
        moveToWatchTV.setToX(300);
        moveToWatchTV.setToY(200);
        moveToWatchTV.play();

        for(int i = 0; ttv > i;){
            timer.scheduleAtFixedRate(reduceTTV, 0, 1000);
        }

        HelloApplication.mutex.acquire();
        HelloApplication.currentWatchers --;
        if(HelloApplication.currentWatchers == 0) HelloApplication.changeChannel.release();
        HelloApplication.mutex.release();

        TranslateTransition moveToFinalPosition = new TranslateTransition(Duration.seconds(2), circle);
        moveToFinalPosition.setToX(500);
        moveToFinalPosition.setToY(200);
        moveToFinalPosition.play();
    }


    @Override
    public void run() {
        try {
            while (true) {
                watchTV();
            }
        } catch (InterruptedException e) {
        }
    }
}
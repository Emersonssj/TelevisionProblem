package adapters;

import com.example.televisionproblem.HelloApplication;
import javafx.application.Platform;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Hospede extends Thread {
    public int id;
    public int canalPreferido;
    public int tempoAssistindo;
    public int tempoDescansando;

    public Hospede(int id, int canalPreferido, int tempoAssistindo, int tempoDescansando) {
        this.id = id;
        this.canalPreferido = canalPreferido;
        this.tempoAssistindo = tempoAssistindo;
        this.tempoDescansando = tempoDescansando;
    }

    // Método para assistir à TV
    public void assistirTV() throws InterruptedException {
        boolean assistindo = false;
        while (!assistindo) {
            HelloApplication.controleRemoto.acquire(); // Tenta adquirir o controle remoto
            if (HelloApplication.canalAtual == 0 || HelloApplication.canalAtual == canalPreferido) {
                // Se a TV estiver livre ou já no canal preferido, assiste
                if (HelloApplication.canalAtual == 0) {
                    HelloApplication.canalAtual = canalPreferido;
                    HelloApplication.moveBallById(String.valueOf(id), 700, 450);
                    HelloApplication.tvWidget.setChannel(canalPreferido);
                    Platform.runLater(() -> {HelloApplication.messages.add(id + " mudou para o canal " + HelloApplication.canalAtual);});
                } else {
                    HelloApplication.moveBallById(String.valueOf(id), 700, 450);
                    Platform.runLater(() -> {HelloApplication.messages.add(id + " se juntou para ver TV no canal " + HelloApplication.canalAtual);});
                }
                HelloApplication.visualizadores++;
                HelloApplication.controleRemoto.release();

                // Simula tempo assistindo (consome CPU)
                Platform.runLater(() -> {HelloApplication.messages.add(id + " está assistindo ao canal " + HelloApplication.canalAtual);});
                long inicioAssistindo = System.currentTimeMillis();
                while (System.currentTimeMillis() - inicioAssistindo < tempoAssistindo * 1000) {
                    // Simulando o tempo assistindo (gasta CPU)
                }

                // Sai do canal
                HelloApplication.controleRemoto.acquire();
                HelloApplication.visualizadores--;
                HelloApplication.moveBallById(String.valueOf(id), 0, 0);
                Platform.runLater(() -> {HelloApplication.messages.add(id + " terminou de assistir ao canal " + canalPreferido);});
                if (HelloApplication.visualizadores == 0) {
                    Platform.runLater(() -> {HelloApplication.messages.add("Canal " + canalPreferido + " não está mais sendo assistido.");});
                    HelloApplication.tvWidget.setChannel(0);
                    HelloApplication.canalAtual = 0;
                }
                HelloApplication.controleRemoto.release();
                assistindo = true;
            } else {
                //HelloApplication.moveBallById(String.valueOf(id), 1200, 450);
                Platform.runLater(() -> {HelloApplication.messages.add(id + " está bloqueado aguardando o canal " + canalPreferido);});
                HelloApplication.controleRemoto.release(); // Libera o semáforo para outros

                // Bloqueia por 1 segundo antes de tentar novamente (não consome CPU)
                Semaphore semaforoBloqueio = new Semaphore(0);
                semaforoBloqueio.tryAcquire(1, TimeUnit.SECONDS);
            }
        }
    }

    // Método para descansar e realizar outras atividades
    public void descansar() {
        String[] atividades = {"jogando bola", "lendo um livro", "jogando xadrez"};
        String atividade = atividades[(int) (Math.random() * atividades.length)];
        Platform.runLater(() -> {HelloApplication.messages.add(id + " está " + atividade);});

        // Simula o tempo de descanso (consome CPU)
        long inicioAtividade = System.currentTimeMillis();
        while (System.currentTimeMillis() - inicioAtividade < tempoDescansando * 1000) {
            // Simulando o tempo da atividade (gasta CPU)
        }
    }

    // Thread principal
    @Override
    public void run() {
        try {
            while (true) {
                assistirTV();
                descansar();
            }
        } catch (InterruptedException e) {
            Platform.runLater(() -> {HelloApplication.messages.add(id + " foi interrompido.");});
        }
    }
}

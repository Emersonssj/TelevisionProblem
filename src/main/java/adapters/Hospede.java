package adapters;

import javafx.scene.Node;
import javafx.scene.shape.Circle;

import java.util.concurrent.Semaphore;
import java.util.Random;

// Classe Hospede
public class Hospede extends Thread {
    private static final int MAX_HOSPEDES_NA_TV = 3;
    private static final Semaphore tvSemaphore = new Semaphore(MAX_HOSPEDES_NA_TV);
    private static int canalAtual = -1;
    private static int espectadoresAssistindo = 0;
    private int id;
    private int canalFavorito;
    private int tempoAssistindoTv;
    private int tempoDescansando;
    private Random random = new Random();

    public Hospede(int id, int canalFavorito, int tempoAssistindoTv, int tempoDescansando) {
        this.id = id;
        this.canalFavorito = canalFavorito;
        this.tempoAssistindoTv = tempoAssistindoTv;
        this.tempoDescansando = tempoDescansando;
    }

    // Método que simula o hóspede assistindo TV
    public void assistirTv() {
        boolean conseguiuAssistir = false;
        while (!conseguiuAssistir) {
            synchronized (Hospede.class) {
                if (espectadoresAssistindo == 0 || canalAtual == canalFavorito) {
                    try {
                        tvSemaphore.acquire();
                        espectadoresAssistindo++;
                        canalAtual = canalFavorito;
                        System.out.println(id + " ligou a TV no canal " + canalAtual + " e está assistindo por " + tempoAssistindoTv + " segundos.");
                        conseguiuAssistir = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {

                    System.out.println(id + " está esperando que a TV fique disponível para mudar para o canal " + canalFavorito + ".");
                    try {
                        Hospede.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        try {
            Thread.sleep(tempoAssistindoTv * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            synchronized (Hospede.class) {
                espectadoresAssistindo--;
                if (espectadoresAssistindo == 0) {
                    System.out.println(id + " foi o último a sair da TV. TV desligada.");
                    canalAtual = -1;
                    tvSemaphore.release();
                    Hospede.class.notifyAll();
                } else {
                    System.out.println(id + " parou de assistir TV. Ainda há " + espectadoresAssistindo + " pessoa(s) assistindo.");
                }
            }
        }
    }


    public void descansar() {
        try {
            String atividade = escolherOutraAtividade();
            System.out.println(id + " está " + atividade + " por " + tempoDescansando + " segundos.");
            Thread.sleep(tempoDescansando * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private String escolherOutraAtividade() {
        String[] atividades = {"descansando", "jogando bola", "lendo", "fazendo um passeio"};
        return atividades[random.nextInt(atividades.length)];
    }


    @Override
    public void run() {
        while (true) {
            descansar();
            assistirTv();
        }
    }

}

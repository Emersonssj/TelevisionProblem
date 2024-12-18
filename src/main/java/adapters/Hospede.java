package adapters;

import com.example.televisionproblem.HelloApplication;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Hospede extends Thread {
    private int id;
    private int canalPreferido;
    private int tempoAssistindo;
    private int tempoDescansando;

    private static int canalAtual = -1; // Canal atual da TV
    private static int visualizadores = 0; // Contador de hóspedes assistindo
    private static Semaphore controleRemoto = new Semaphore(1); // Semáforo para controle do canal

    // Construtor
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
            controleRemoto.acquire(); // Tenta adquirir o controle remoto
            if (canalAtual == -1 || canalAtual == canalPreferido) {
                // Se a TV estiver livre ou já no canal preferido, assiste
                if (canalAtual == -1) {
                    canalAtual = canalPreferido; // Define o canal
                    System.out.println(id + " mudou para o canal " + canalAtual);
                } else {
                    System.out.println(id + " se juntou para ver TV no canal " + canalAtual);
                }
                visualizadores++;
                controleRemoto.release(); // Libera o controle remoto

                // Simula tempo assistindo (consome CPU)
                System.out.println(id + " está assistindo ao canal " + canalAtual);
                long inicioAssistindo = System.currentTimeMillis();
                while (System.currentTimeMillis() - inicioAssistindo < tempoAssistindo * 1000) {
                    // Simulando o tempo assistindo (gasta CPU)
                }

                // Sai do canal
                controleRemoto.acquire();
                visualizadores--;
                System.out.println(id + " terminou de assistir ao canal " + canalAtual);
                if (visualizadores == 0) {
                    System.out.println("Canal " + canalAtual + " não está mais sendo assistido.");
                    canalAtual = -1; // Libera o canal
                }
                controleRemoto.release();
                assistindo = true;
            } else {
                // Se o canal atual não é o preferido, bloqueia
                System.out.println(id + " está bloqueado aguardando o canal " + canalPreferido);
                controleRemoto.release(); // Libera o semáforo para outros

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
        System.out.println(id + " está " + atividade);

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
                descansar(); // Hóspede descansa (consome CPU)
                assistirTV(); // Tenta assistir à TV (consome CPU)
            }
        } catch (InterruptedException e) {
            System.out.println(id + " foi interrompido.");
        }
    }
}

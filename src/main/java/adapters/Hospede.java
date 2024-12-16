package adapters;

import java.util.concurrent.Semaphore;
import java.util.Random;

// Classe Hospede
public class Hospede extends Thread {
    private static final int MAX_HOSPEDES_NA_TV = 3; // Quantidade máxima de hóspedes assistindo TV ao mesmo tempo
    private static final Semaphore tvSemaphore = new Semaphore(MAX_HOSPEDES_NA_TV);
    private static int canalAtual = -1; // Canal atualmente sendo assistido (-1 indica que a TV está desligada)
    private static int espectadoresAssistindo = 0; // Quantidade de hóspedes assistindo TV
    private String nome;
    private int canalFavorito;
    private int tempoAssistindoTv;
    private int tempoDescansando;
    private Random random = new Random();

    public Hospede(String nome, int canalFavorito, int tempoAssistindoTv, int tempoDescansando) {
        this.nome = nome;
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
                        tvSemaphore.acquire(); // Adquire acesso à TV
                        espectadoresAssistindo++;
                        canalAtual = canalFavorito;
                        System.out.println(nome + " ligou a TV no canal " + canalAtual + " e está assistindo por " + tempoAssistindoTv + " segundos.");
                        conseguiuAssistir = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(nome + " está esperando que a TV fique disponível para mudar para o canal " + canalFavorito + ".");
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
                    System.out.println(nome + " foi o último a sair da TV. TV desligada.");
                    canalAtual = -1;
                    tvSemaphore.release();
                    Hospede.class.notifyAll(); // Notifica os outros hóspedes que a TV está disponível
                } else {
                    System.out.println(nome + " parou de assistir TV. Ainda há " + espectadoresAssistindo + " pessoa(s) assistindo.");
                }
            }
        }
    }

    public void descansar() {
        try {
            String atividade = escolherOutraAtividade();
            System.out.println(nome + " está " + atividade + " por " + tempoDescansando + " segundos.");
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

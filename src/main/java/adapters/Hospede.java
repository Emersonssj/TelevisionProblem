package adapters;

import com.example.televisionproblem.HelloApplication;

import java.util.concurrent.Semaphore;
import java.util.Random;

// Classe Hospede
public class Hospede extends Thread {
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
    public void assistir() {
        boolean conseguiuAssistir = false;
        while (!conseguiuAssistir) {
            synchronized (Hospede.class) {
                if (HelloApplication.mostraQtdEspectadores() == 0 || HelloApplication.mostraCanalAtual() == canalFavorito) {
                    HelloApplication.reservaTv();
                    HelloApplication.incrementaEspectador();
                    HelloApplication.atualizaCanalAtual(canalFavorito);
                    System.out.println(id + " ligou a TV no canal " + HelloApplication.mostraCanalAtual() + " e está assistindo por " + tempoAssistindoTv + " segundos.");
                    conseguiuAssistir = true;
                } else {
                    System.out.println(id + " verificou que não está passando o canal que ele gosta e foi dormir");
                    try {
                        Hospede.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!conseguiuAssistir) {
                descansando();
            }
        }

        // Controle de tempo sem usar sleep
        long inicio = System.currentTimeMillis();
        while (System.currentTimeMillis() - inicio < tempoAssistindoTv * 1000) {
            Thread.yield(); // Cede o controle da CPU para evitar busy-waiting
        }

        synchronized (Hospede.class) {
            HelloApplication.decrementaEspectador();
            if (HelloApplication.mostraQtdEspectadores() == 0) {
                System.out.println(id + " foi o último a sair da TV. TV desligada.");
                HelloApplication.atualizaCanalAtual(-1);
                HelloApplication.liberaTv();
                Hospede.class.notifyAll();
            } else {
                System.out.println(id + " parou de assistir TV. Ainda há " + HelloApplication.mostraQtdEspectadores() + " pessoa(s) assistindo.");
            }
        }
    }

    private String escolherOutraAtividade() {
        String[] atividades = {"jogando xadrez", "jogando bola", "lendo", "bebendo água"};
        return atividades[random.nextInt(atividades.length)];
    }

    public void descansando() {
        String atividade = escolherOutraAtividade();
        System.out.println(id + " está " + atividade + " por " + tempoDescansando + " segundos.");

        // Controle de tempo
        long inicio = System.currentTimeMillis();
        while (System.currentTimeMillis() - inicio < tempoDescansando * 1000) {
            Thread.yield(); // Cede o controle da CPU para evitar busy-waiting
        }
    }

    @Override
    public void run() {
        while (true) {
            assistir();
            descansando();
        }
    }
}
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
                // Se não há ninguém assistindo ou se o canal atual é o favorito, ele pode assistir
                if (HelloApplication.mostraQtdEspectadores() == 0 || HelloApplication.mostraCanalAtual() == canalFavorito) {
                    HelloApplication.reservaTv(); // Adquire acesso à TV
                    HelloApplication.incrementaEspectador();
                    HelloApplication.atualizaCanalAtual(canalFavorito);
                    System.out.println(id + " ligou a TV no canal " + HelloApplication.mostraCanalAtual() + " e está assistindo por " + tempoAssistindoTv + " segundos.");
                    conseguiuAssistir = true;

                    // Depois que terminar de assistir, matar a thread, pois ja viu o seu programa favotiro

                } else {
                    // Se não puder assistir, o hóspede decide ir fazer outra coisa
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

        try {
            Thread.sleep(tempoAssistindoTv * 1000); // Hóspede assiste TV
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            synchronized (Hospede.class) {
                // Quando o hóspede termina de assistir
                HelloApplication.decrementaEspectador();
                if (HelloApplication.mostraQtdEspectadores() == 0) {
                    System.out.println(id + " foi o último a sair da TV. TV desligada.");
                    HelloApplication.atualizaCanalAtual(-1);
                    HelloApplication.liberaTv();
                    Hospede.class.notifyAll(); // Notifica todos que a TV foi liberada
                } else {
                    System.out.println(id + " parou de assistir TV. Ainda há " + HelloApplication.mostraQtdEspectadores() + " pessoa(s) assistindo.");
                }
            }
        }
    }

    private String escolherOutraAtividade() {
        String[] atividades = {"jogando xadrez", "jogando bola", "lendo", "bebendo água"};
        return atividades[random.nextInt(atividades.length)];
    }

    public void descansando() {
        try {
            String atividade = escolherOutraAtividade();
            System.out.println(id + " está " + atividade + " por " + tempoDescansando + " segundos.");
            Thread.sleep(tempoDescansando * 1000); // O hóspede "descansa"
        } catch (InterruptedException e) {
            e.printStackTrace();
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

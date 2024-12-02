package adapters;

import java.util.ArrayList;
import java.util.List;

public class Hospede extends Thread{
    private int id;
    private int canal;
    private int ttv;
    private int td;

    public Hospede(int qtdCanais, int id, int tempoAssistido,int tempoDescansando) {
        this.id = id;
        this.canal = qtdCanais;
        this.ttv = tempoAssistido;
        this.td = tempoDescansando;

        // Aqui será mostrado uma mensagem na interface com as infomações de hospede criado com sucesso
        System.out.println("Hospede "+id+" "+canal+" "+ttv+" "+td +" Criado com sucesso");
    }

    @Override
    public void run(){
        System.out.println("Estou assistindo TV no canal "+this.canal);
    }

    public List<Integer> MostraDados(){
        List<Integer> hospedeDados = new ArrayList<>();

        hospedeDados.add(this.id);
        hospedeDados.add(this.canal);
        hospedeDados.add(this.ttv);
        hospedeDados.add(this.td);

        System.out.println("Hospede "+id+" "+canal+" "+ttv+" "+td);
        return hospedeDados;
    }
}

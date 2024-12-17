package widgets;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TVWidget extends StackPane {
    private Text channelText;

    public TVWidget() {
        // Criação do Canvas para desenhar a TV
        Canvas tvCanvas = new Canvas(150, 100);
        drawTV(tvCanvas.getGraphicsContext2D());

        // Texto para exibir o número do canal
        channelText = new Text("0"); // Canal inicial
        channelText.setFont(new Font("Arial", 30));
        channelText.setFill(Color.WHITE);

        // Adicionar Canvas e Texto ao StackPane
        this.getChildren().addAll(tvCanvas, channelText);
        this.setAlignment(Pos.CENTER_RIGHT); // Centraliza o texto
    }

    // Método para desenhar a TV
    private void drawTV(GraphicsContext gc) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(10, 10, 130, 80, 15, 15); // Corpo da TV

        gc.setFill(Color.BLACK);
        gc.fillRect(20, 20, 110, 60); // Tela da TV
    }

    // Método público para atualizar o número do canal
    public void setChannel(int newChannel) {
        channelText.setText(String.valueOf(newChannel));
    }
}

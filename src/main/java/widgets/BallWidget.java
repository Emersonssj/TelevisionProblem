package widgets;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class BallWidget extends Pane {
    private String id;          // Identificador único
    private Circle ball;        // Bola
    private Text idText;        // Texto com o ID
    private TranslateTransition transition; // Animação de movimento

    public BallWidget(String id, Color color, double startX, double startY) {
        this.id = id;

        // Criar a bola
        ball = new Circle(30, color); // Raio 30
        ball.setCenterX(startX);
        ball.setCenterY(startY);

        // Criar o texto com o ID
        idText = new Text(id);
        idText.setFill(Color.WHITE);
        idText.setFont(Font.font(14));
        idText.setMouseTransparent(true); // Evita interferência de clique no texto

        // Centralizar o texto na bola
        updateTextPosition();

        // Criar a animação
        transition = new TranslateTransition(Duration.seconds(2), this); // Transição de 1 segundo

        // Adicionar a bola e o texto ao layout
        this.getChildren().addAll(ball, idText);
    }

    // Atualiza a posição do texto para centralizá-lo na bola
    private void updateTextPosition() {
        idText.setX(ball.getCenterX() - idText.getLayoutBounds().getWidth() / 2);
        idText.setY(ball.getCenterY() + idText.getLayoutBounds().getHeight() / 4);
    }

    // Getter para o ID
    public String getIdd() {
        return id;
    }

    // Método para mover a bola suavemente até a posição de destino
    public void moveTo(double destX, double destY) {
        // Definir o destino da animação
        transition.setToX(destX - ball.getCenterX());
        transition.setToY(destY - ball.getCenterY());

        // Iniciar a animação
        transition.play();

        // Atualizar a posição da bola e recalcular o texto após o movimento
        transition.setOnFinished(event -> {
            ball.setCenterX(destX);
            ball.setCenterY(destY);
            updateTextPosition();
        });
    }
}

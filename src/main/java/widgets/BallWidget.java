package widgets;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class BallWidget extends Pane {
    public String id;          // Identificador único
    public Circle ball;        // Bola
    public Text idText;        // Texto com o ID
    public TranslateTransition transition; // Animação de movimento

    public BallWidget(String id, Color color, double startX, double startY) {
        this.id = id;

        // Criar a bola
        ball = new Circle(30, color);
        ball.setCenterX(startX);
        ball.setCenterY(startY);
        ball.maxHeight(30);
        ball.maxWidth(30);

        // Criar o texto com o ID
        idText = new Text(id);
        idText.setFill(Color.WHITE);
        idText.setFont(Font.font(14));
        idText.setMouseTransparent(true);

        // Centralizar o texto na bola
        updateTextPosition();

        // Criar a animação
        transition = new TranslateTransition(Duration.seconds(2), this);

        // Adicionar a bola e o texto ao layout
        this.getChildren().addAll(ball, idText);
    }

    // Atualiza a posição do texto para centralizá-lo na bola
    private void updateTextPosition() {
        idText.setX(ball.getCenterX() - idText.getLayoutBounds().getWidth() / 2);
        idText.setY(ball.getCenterY() + idText.getLayoutBounds().getHeight() / 4);
    }

    public String getIdd() {
        return id;
    }

    public void moveTo(double destX, double destY) {
        transition.setToX(destX);
        transition.setToY(destY);

        transition.play();
    }
}

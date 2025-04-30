package application;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Ponggame extends Application {

    private static final int width = 800;
    private static final int height = 600;
    private static final int P_HEIGHT = 100;
    private static final int P_WIDTH = 20;
    private static final double BALL_RAD = 20;

    private int ballYSpeed = 1;
    private int ballXSpeed = 1;
    private double POneYPos = height / 2;
    private double PTwoYPos = height / 2;
    private double ballXPos = width / 2;
    private double ballYPos = height / 2;
    private int scoP1 = 0;
    private int scoP2 = 0;
    private boolean gameStarted = false;
    private boolean showInstructions = true;

    private int POneXPos = 0;
    private double PTwoXPos = width - P_WIDTH;
    int s = 0;

    Button restartButton = new Button("🔁 Restart");

    public void start(Stage stage) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Timeline t = new Timeline(new KeyFrame(Duration.millis(10), e -> run(gc)));
        t.setCycleCount(Timeline.INDEFINITE);

        VBox root = new VBox();
        root.setAlignment(Pos.BOTTOM_CENTER);
        StackPane stack = new StackPane(canvas);
        root.getChildren().addAll(stack, restartButton);

        restartButton.setVisible(false);
        restartButton.setStyle("-fx-font-size: 16px; -fx-background-color: #A6BB8D; -fx-text-fill: white; -fx-background-radius: 20px;");
        restartButton.setOnAction(e -> {
            scoP1 = 0;
            scoP2 = 0;
            ballXSpeed = 1;
            ballYSpeed = 1;
            gameStarted = false;
            showInstructions = true; 
            s = 0;
            restartButton.setVisible(false);
        });

        Scene scene = new Scene(root);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.UP && PTwoYPos > 0) {
                    PTwoYPos -= 50;
                }
                if (event.getCode() == KeyCode.DOWN && PTwoYPos < height - P_HEIGHT) {
                    PTwoYPos += 50;
                }
                if (event.getCode() == KeyCode.W && POneYPos > 0) {
                    POneYPos -= 50;
                }
                if (event.getCode() == KeyCode.S && POneYPos < height - P_HEIGHT) {
                    POneYPos += 50;
                }
            }
        });

        canvas.setOnMouseClicked(e -> {
            if (!gameStarted && showInstructions) {
                showInstructions = false;
                gameStarted = true;
                s = 1;
            }
        });

        stage.setScene(scene);
        stage.setTitle("🌟 Pong Game 🌟");
        stage.show();
        t.play();
    }

    private void run(GraphicsContext gc) {
        // Background Gradient
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web("#F9F7F7")),
            new Stop(1, Color.web("#C9E4CA"))
        };
        LinearGradient lg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        gc.setFill(lg);
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.web("#333"));
        gc.setFont(Font.font("Arial", 30));
        gc.setTextAlign(TextAlignment.CENTER);

        // Show instructions only once
        if (showInstructions) {
            gc.setFont(Font.font("Verdana", 24));
            gc.setFill(Color.web("#333"));
            gc.fillText("🎮 Welcome to Pong Game!", width / 2, height / 2 - 80);
            gc.fillText("Player 1: W (up), S (down)", width / 2, height / 2 - 40);
            gc.fillText("Player 2: UP, DOWN arrow keys", width / 2, height / 2);
            gc.fillText("Click anywhere to start", width / 2, height / 2 + 80);
            return;
        }

        // Game movement
        if (gameStarted) {
            ballXPos += ballXSpeed;
            ballYPos += ballYSpeed;
            gc.setFill(Color.web("#4C4C6D"));
            gc.fillOval(ballXPos, ballYPos, BALL_RAD, BALL_RAD);
        } else if (s == 0) {
            gc.setFill(Color.web("#555"));
            gc.fillText("Click to Start", width / 2, height / 2);
            ballXPos = width / 2;
            ballYPos = height / 6;
            POneYPos = height / 2;
            PTwoYPos = height / 2;
        } else {
            ballXPos = width / 2;
            ballYPos = height / 4;
            ballXSpeed = new Random().nextInt(2) == 0 ? 2 : -1;
            ballYSpeed = new Random().nextInt(2) == 0 ? 2 : -1;
            gameStarted = true;
        }

        if (ballYPos > height || ballYPos < 0)
            ballYSpeed *= -1;

        if (ballXPos < POneXPos - P_WIDTH) {
            scoP2++;
            gameStarted = false;
        }

        if (ballXPos > PTwoXPos + P_WIDTH) {
            scoP1++;
            gameStarted = false;
        }

        if (((ballXPos + BALL_RAD > PTwoXPos) && ballYPos >= PTwoYPos && ballYPos <= PTwoYPos + P_HEIGHT) ||
            ((ballXPos < POneXPos + P_WIDTH) && ballYPos >= POneYPos && ballYPos <= POneYPos + P_HEIGHT)) {
            ballYSpeed += 1 * Math.signum(ballYSpeed);
            ballXSpeed += 1 * Math.signum(ballXSpeed);
            ballXSpeed *= -1;
            ballYSpeed *= -1;
        }

        // Player Labels
        gc.setFont(Font.font("Courier New", 26));
        gc.fillText("Player 1", width / 4, 50);
        gc.fillText("Player 2", width * 3 / 4, 50);

        gc.setFont(Font.font("Courier New", 32));
        gc.fillText(String.valueOf(scoP1), width / 4, 90);
        gc.fillText(String.valueOf(scoP2), width * 3 / 4, 90);

        gc.setFont(Font.font("Arial", 18));
        gc.setFill(Color.web("#555"));
        gc.fillText("Score 3 points to claim victory!", width / 2, 120);

        gc.setFill(Color.web("#5C8374"));
        gc.fillRoundRect(POneXPos, POneYPos, P_WIDTH, P_HEIGHT, 10, 10);
        gc.fillRoundRect(PTwoXPos, PTwoYPos, P_WIDTH, P_HEIGHT, 10, 10);

        if (scoP1 == 3) {
            gc.setFill(Color.web("#3A4D39"));
            gc.setFont(Font.font("Verdana", 32));
            gc.fillText("🏆 Player 1 Wins! 🏆", width / 2, height / 2);
            gameStarted = false;
            restartButton.setVisible(true);
        }

        if (scoP2 == 3) {
            gc.setFill(Color.web("#3A4D39"));
            gc.setFont(Font.font("Verdana", 32));
            gc.fillText("🏆 Player 2 Wins! 🏆", width / 2, height / 2);
            gameStarted = false;
            restartButton.setVisible(true);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.example.memoriegame;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {

    @FXML
    private AnchorPane apPremierePage;
    @FXML
    private AnchorPane apDeuxiemePage;
    @FXML
    private GridPane gameGrid;
    @FXML
    private Label timerLabel;

    private List<Carte> cartes = new ArrayList<>();
    private List<ImageView> imageViews = new ArrayList<>();
    private ImageView firstSelected = null;
    private ImageView secondSelected = null;
    private Image carteDos = new Image(getClass().getResource("/Image/unnamed.jpg").toExternalForm());
    private int pairsFound = 0;
    private final int TOTAL_PAIRS = 9;
    private boolean canClick = true;
    private Timer gameTimer;
    private int timeRemaining = 90; // 60 secondes

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearAll();
        visible(apPremierePage);
        initializeCards();
    }

    private void initializeCards() {
        Carte sasuke = new Carte("Sasuke", "Image/Sasuke_Part_1.jpg");
        Carte gaara = new Carte("Gaara", "Image/Gaara_in_Part_I.jpg");
        Carte madara = new Carte("Madara", "Image/Madara_img2.jpg");
        Carte boruto = new Carte("Boruto", "Image/Boruto_Infobox.jpg");
        Carte iwabe = new Carte("Iwabe", "Image/Iwabee_Yuino.jpg");
        Carte naruto = new Carte("Naruto", "Image/Naruto_newshot.jpg");
        Carte kakashi = new Carte("Kakashi", "Image/Kakashi_Hatake.jpg");
        Carte yamato = new Carte("Yamato", "Image/Yamato_newshot.jpg");
        Carte minato = new Carte("Minato", "Image/Minato_Namikaze.jpg");

        cartes.addAll(Arrays.asList(
                sasuke, sasuke,
                gaara, gaara,
                madara, madara,
                boruto, boruto,
                iwabe, iwabe,
                naruto, naruto,
                kakashi, kakashi,
                yamato, yamato,
                minato, minato
        ));

        Collections.shuffle(cartes);
    }

    @FXML
    public void btnCommencer(MouseEvent event) {
        clearAll();
        visible(apDeuxiemePage);
        setupGame();
        startTimer();
    }

    private void setupGame() {
        gameGrid.getChildren().clear();
        imageViews.clear();
        firstSelected = null;
        secondSelected = null;
        pairsFound = 0;
        canClick = true;
        timeRemaining = 90;
        timerLabel.setText("90");

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                int index = i * 5 + j;
                if (index < cartes.size()) {
                    ImageView imageView = new ImageView(carteDos);
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(150);
                    imageView.setUserData(cartes.get(index));

                    imageView.setOnMouseClicked(this::handleCardClick);
                    gameGrid.add(imageView, j, i);
                    imageViews.add(imageView);
                }
            }
        }
    }

    private void startTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }

        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    timeRemaining--;
                    timerLabel.setText(String.valueOf(timeRemaining));

                    if (timeRemaining <= 0) {
                        gameTimer.cancel();
                        timeUp();
                    }
                });
            }
        }, 1000, 1000); // Démarrer après 1 seconde, puis répéter chaque seconde
    }

    private void timeUp() {
        canClick = false;
        // Animation de fin de temps
        FadeTransition ft = new FadeTransition(Duration.seconds(1), gameGrid);
        ft.setFromValue(1.0);
        ft.setToValue(0.3);
        ft.play();

        ft.setOnFinished(e -> {
            clearAll();
            visible(apPremierePage);
        });
    }

    private void handleCardClick(MouseEvent event) {
        if (!canClick) return;

        ImageView clickedImageView = (ImageView) event.getSource();

        if (clickedImageView == firstSelected || clickedImageView.getImage() == null) {
            return;
        }

        Carte carte = (Carte) clickedImageView.getUserData();
        clickedImageView.setImage(new Image(getClass().getResource("/" + carte.getUrlCarte()).toExternalForm()));

        if (firstSelected == null) {
            firstSelected = clickedImageView;
        } else {
            secondSelected = clickedImageView;
            canClick = false;

            Carte carte1 = (Carte) firstSelected.getUserData();
            Carte carte2 = (Carte) secondSelected.getUserData();

            if (carte1.getNomCarte().equals(carte2.getNomCarte())) {
                pairsFound++;

                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                javafx.application.Platform.runLater(() -> {
                                    animatePairSuccess(firstSelected, secondSelected);
                                    firstSelected = null;
                                    secondSelected = null;
                                    canClick = true;
                                    checkGameEnd();
                                });
                            }
                        },
                        500
                );
            } else {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                javafx.application.Platform.runLater(() -> {
                                    firstSelected.setImage(carteDos);
                                    secondSelected.setImage(carteDos);
                                    firstSelected = null;
                                    secondSelected = null;
                                    canClick = true;
                                });
                            }
                        },
                        1000
                );
            }
        }
    }

    private void animatePairSuccess(ImageView img1, ImageView img2) {
        // Animation de disparition
        FadeTransition ft1 = new FadeTransition(Duration.seconds(0.5), img1);
        ft1.setFromValue(1.0);
        ft1.setToValue(0.0);

        FadeTransition ft2 = new FadeTransition(Duration.seconds(0.5), img2);
        ft2.setFromValue(1.0);
        ft2.setToValue(0.0);

        ft1.play();
        ft2.play();

        ft1.setOnFinished(e -> img1.setImage(null));
        ft2.setOnFinished(e -> img2.setImage(null));
    }

    private void checkGameEnd() {
        if (pairsFound == TOTAL_PAIRS) {
            gameTimer.cancel();
            showVictoryAnimation();
        }
    }

    private void showVictoryAnimation() {
        canClick = false;

        // Créer un label de victoire
        Label victoryLabel = new Label("Félicitations ! Vous avez gagné !");
        victoryLabel.setFont(new Font(24));
        victoryLabel.setStyle("-fx-text-fill: gold; -fx-font-weight: bold;");
        victoryLabel.setLayoutX(150);
        victoryLabel.setLayoutY(150);

        apDeuxiemePage.getChildren().add(victoryLabel);

        // Animation
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), victoryLabel);
        st.setFromX(0.5);
        st.setFromY(0.5);
        st.setToX(1.5);
        st.setToY(1.5);
        st.setAutoReverse(true);
        st.setCycleCount(4);

        FadeTransition ft = new FadeTransition(Duration.seconds(4), victoryLabel);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        st.play();
        ft.play();

        ft.setOnFinished(e -> {
            apDeuxiemePage.getChildren().remove(victoryLabel);
            clearAll();
            visible(apPremierePage);
        });
    }

    public void visible(AnchorPane apCourante) {
        apCourante.setVisible(true);
    }

    public void invisible(AnchorPane apCourante) {
        apCourante.setVisible(false);
    }

    public void clearAll() {
        invisible(apDeuxiemePage);
        invisible(apPremierePage);
        if (gameTimer != null) {
            gameTimer.cancel();
        }
    }
}
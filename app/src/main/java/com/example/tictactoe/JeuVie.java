package com.example.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class JeuVie extends Activity {  // Retiré 'static' car une Activity ne peut pas être statique
    private GameOfLifeView gameView;
    private Button startButton, stopButton, resetButton, returnButton;
    private TextView iterationText, resultText, sizeText, densityText;
    private SeekBar sizeSeekBar, densitySeekBar;
    private GameOfLife game;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jeu_vie);  // Doit correspondre à votre fichier XML

        // Initialisation des vues
        gameView = findViewById(R.id.game_view);
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        resetButton = findViewById(R.id.reset_button);
        returnButton = findViewById(R.id.btn_retour);
        iterationText = findViewById(R.id.iteration_text);
        resultText = findViewById(R.id.result_text);
        sizeText = findViewById(R.id.size_text);
        densityText = findViewById(R.id.density_text);
        sizeSeekBar = findViewById(R.id.size_seekbar);  // Vérifiez l'orthographe dans votre XML
        densitySeekBar = findViewById(R.id.density_seekbar);

        // Valeurs par défaut (taille: 20x20, densité: 0.3)
        sizeSeekBar.setProgress(20);
        densitySeekBar.setProgress(30); // 30% = 0.3

        // Mise à jour des textes
        updateSizeText(20);
        updateDensityText(0.3f);

        // Listeners pour les SeekBars
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = progress;
                updateSizeText(size);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        densitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float density = progress / 100f;
                updateDensityText(density);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Initialisation du jeu avec les valeurs par défaut
        resetGame(20, 0.3f);

        // Boutons
        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                isRunning = true;
                new Thread(new GameRunner()).start();
            }
        });

        stopButton.setOnClickListener(v -> isRunning = false);

        resetButton.setOnClickListener(v -> {
            isRunning = false;
            int size = sizeSeekBar.getProgress();
            float density = densitySeekBar.getProgress() / 100f;
            resetGame(size, density);
        });

        returnButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void updateSizeText(int size) {
        sizeText.setText("Taille: " + size + "x" + size);
    }

    private void updateDensityText(float density) {
        densityText.setText("Densité: " + String.format("%.2f", density));
    }

    private void resetGame(int size, float density) {
        game = new GameOfLife(size, size, density);
        gameView.setGame(game);
        gameView.invalidate();
        iterationText.setText("Itération: 0");
        resultText.setText("");
    }

    private class GameRunner implements Runnable {
        @Override
        public void run() {
            while (isRunning && game.getIteration() < 1000) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final int result = game.nextGeneration();

                runOnUiThread(() -> {
                    gameView.invalidate();
                    iterationText.setText("Itération: " + game.getIteration());

                    if (result != -1) {
                        isRunning = false;
                        if (result == 1000) {
                            resultText.setText("Terminé: 1000 itérations (pas de périodicité)");
                        } else {
                            resultText.setText("Périodicité détectée après " + result + " itérations");
                        }
                    }
                });
            }
            isRunning = false;
        }
    }
}
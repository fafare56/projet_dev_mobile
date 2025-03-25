package com.example.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class JeuVie extends Activity
{
    private GameOfLifeView gameView;
    private Button startButton, stopButton, resetButton, settingsButton;
    private TextView iterationText, resultText;
    private GameOfLife game;
    private boolean isRunning = false;
    private static final int SETTINGS_REQUEST = 1;
    private int currentSize = 20;
    private float currentDensity = 0.3f;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jeu_vie);

        gameView        = findViewById(R.id.game_view);
        startButton     = findViewById(R.id.start_button);
        stopButton      = findViewById(R.id.stop_button);
        resetButton     = findViewById(R.id.reset_button);
        settingsButton  = findViewById(R.id.settings_button);
        iterationText   = findViewById(R.id.iteration_text);
        resultText      = findViewById(R.id.result_text);

        resetGame(currentSize, currentDensity);

        Intent intentVal = getIntent();
        int size = intentVal.getIntExtra("size", 20);
        float density = intentVal.getFloatExtra("density", 0.3f);

        game = new GameOfLife(size, size, density);
        gameView.setGame(game);

        startButton.setOnClickListener(v ->
        {
            if (!isRunning)
            {
                isRunning = true;
                new Thread(new GameRunner()).start();
            }
        });

        stopButton.setOnClickListener(v -> isRunning = false);

        resetButton.setOnClickListener(v ->
        {
            isRunning = false;
            resetGame(currentSize, currentDensity);
        });

        settingsButton.setOnClickListener(v ->
        {
            Intent intent = new Intent(JeuVie.this, JeuVieParam.class);
            intent.putExtra("currentSize", currentSize);
            intent.putExtra("currentDensity", currentDensity);
            startActivityForResult(intent, SETTINGS_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST && resultCode == RESULT_OK)
        {
            currentSize = data.getIntExtra("size", 20);
            currentDensity = data.getFloatExtra("density", 0.3f);
            resetGame(currentSize, currentDensity);
        }
    }

    private void resetGame(int size, float density)
    {
        isRunning = false;
        game = new GameOfLife(size, size, density);
        gameView.setGame(game);
        gameView.invalidate();
        iterationText.setText(String.format("Itération: %d", 0));
        resultText.setText("");
    }

    private class GameRunner implements Runnable
    {
        @Override
        public void run()
        {
            while (isRunning && game.getIteration() < 1000)
            {
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                final int result = game.nextGeneration();

                runOnUiThread(() ->
                {
                    gameView.invalidate();
                    iterationText.setText(String.format("Itération: %d", game.getIteration()));

                    if (result != -1)
                    {
                        isRunning = false;
                        if (result == 1000)
                            resultText.setText("Terminé: 1000 itérations (pas de périodicité)");
                        else
                            resultText.setText(String.format("Périodicité détectée après %d itérations", result));
                    }
                });
            }
            isRunning = false;
        }
    }
}
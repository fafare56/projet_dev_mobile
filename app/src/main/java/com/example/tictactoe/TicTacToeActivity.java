
package com.example.tictactoe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TicTacToeActivity extends AppCompatActivity
{
    private int scoreX = 0;
    private int scoreO = 0;
    private TextView scoreXText, scoreOText;
    private TicTacToeView ticTacToeView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tictactoe);

        scoreXText = findViewById(R.id.scoreX);
        scoreOText = findViewById(R.id.scoreO);
        ticTacToeView = findViewById(R.id.ticTacToeView);

        int tailleGrille = getIntent().getIntExtra("TAILLE_GRILLE", 3);
        ticTacToeView.setTailleGrille(tailleGrille);

        updateScoreDisplay();

        ticTacToeView.setScoreUpdateListener(new TicTacToeView.ScoreUpdateListener()
        {
            @Override
            public void onScoreUpdated(char winner)
            {
                if (winner == 'X')
                    scoreX++;
                else if (winner == 'O')
                    scoreO++;
                updateScoreDisplay();
            }
        });

        Button btnRejouer = findViewById(R.id.btnRejouer);
        btnRejouer.setOnClickListener(v ->
        {
            ticTacToeView.resetBoard();
        });

        Button btnQuitter = findViewById(R.id.btnQuitter);
        btnQuitter.setOnClickListener(v -> finish());
    }

    private void updateScoreDisplay()
    {
        scoreXText.setText("Score Joueur : " + scoreX);
        scoreOText.setText("Score IA : " + scoreO);
    }
}

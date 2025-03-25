package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TicTacToeActivity extends AppCompatActivity {
    private int scoreX = 0;
    private int scoreO = 0;
    private int tailleGrille = 3; // Par défaut 3x3
    private TextView scoreXText, scoreOText;
    private TicTacToeView ticTacToeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tictactoe);

        tailleGrille = getIntent().getIntExtra("TAILLE_GRILLE", 3);

        scoreXText = findViewById(R.id.scoreX);
        scoreOText = findViewById(R.id.scoreO);
        ticTacToeView = findViewById(R.id.ticTacToeView);

        ticTacToeView.setTailleGrille(tailleGrille);

        Button btnRejouer = findViewById(R.id.btnRejouer);
        btnRejouer.setOnClickListener(v -> ticTacToeView.resetBoard());

    }

    public void updateScore(char winner) {
        if (winner == 'X') {
            scoreX++;
            scoreXText.setText("Joueur X : " + scoreX);
        } else if (winner == 'O') {
            scoreO++;
            scoreOText.setText("Joueur O : " + scoreO);
        }
    }
}

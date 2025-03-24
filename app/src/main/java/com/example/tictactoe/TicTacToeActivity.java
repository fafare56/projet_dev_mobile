package com.example.tictactoe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TicTacToeActivity extends AppCompatActivity {
    private int scoreX = 0;
    private int scoreO = 0;
    private TextView scoreXText, scoreOText;
    private TicTacToeView ticTacToeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreXText = findViewById(R.id.scoreX);
        scoreOText = findViewById(R.id.scoreO);
        ticTacToeView = findViewById(R.id.ticTacToeView);

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

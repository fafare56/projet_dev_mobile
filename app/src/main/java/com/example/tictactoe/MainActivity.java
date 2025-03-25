package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Récupérer les boutons
        Button btnExit      = findViewById(R.id.btn_exit);
        Button btnTicTacToe = findViewById(R.id.btn_TicTactoe);
        Button btnJeuVie    = findViewById(R.id.btn_JeuVie);
        Button btnRond      = findViewById(R.id.btn_SuiviCourbes);

        // Bouton Quitter
        btnExit.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

        // Bouton TicTacToe -> Lancer TicTacToeParamActivity
        btnTicTacToe.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TicTacToeParamActivity.class);
            startActivity(intent);
        });

        // Bouton Jeu de la Vie -> Lancer JeuDeLaVieActivity (à créer)
        btnJeuVie.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JeuVieParam.class);
            startActivity(intent);
        });

        // Bouton Suivi des Courbes -> Lancer CourbesActivity (à créer)
        btnRond.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Main_Suivi.class);
            startActivity(intent);
        });
    }
}

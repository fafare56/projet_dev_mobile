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

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnExit      = findViewById(R.id.btn_exit);
        Button btnTicTacToe = findViewById(R.id.btn_TicTactoe);
        Button btnJeuVie    = findViewById(R.id.btn_JeuVie);
        Button btnRond      = findViewById(R.id.btn_SuiviCourbes);

        btnExit.setOnClickListener(v ->
        {
            finishAffinity();
            System.exit(0);
        });

        btnTicTacToe.setOnClickListener(v ->
        {
            Intent intent = new Intent(MainActivity.this, TicTacToeParamActivity.class);
            startActivity(intent);
        });

        btnJeuVie.setOnClickListener(v ->
        {
            Intent intent = new Intent(MainActivity.this, JeuVieParam.class);
            startActivity(intent);
        });

        btnRond.setOnClickListener(v ->
        {
            Intent intent = new Intent(MainActivity.this, Main_Suivi.class);
            startActivity(intent);
        });
    }
}

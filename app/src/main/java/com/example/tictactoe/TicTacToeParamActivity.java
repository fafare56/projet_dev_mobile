package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TicTacToeParamActivity extends AppCompatActivity
{
    private EditText editGrill;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.param_tictactoe);

        editGrill = findViewById(R.id.editGrill);
        Button btnJouer = findViewById(R.id.btn_TicTactoe);
        Button btnExit = findViewById(R.id.btn_exit);

        btnJouer.setOnClickListener(v ->
        {
            String tailleGrilleStr = editGrill.getText().toString().trim();

            if (!tailleGrilleStr.isEmpty())
            {
                try
                {
                    int tailleGrille = Integer.parseInt(tailleGrilleStr);

                    if (tailleGrille >= 3 && tailleGrille <= 10)
                        new Handler().postDelayed(() ->
                        {
                            Intent intent = new Intent(TicTacToeParamActivity.this, TicTacToeActivity.class);
                            intent.putExtra("TAILLE_GRILLE", tailleGrille);
                            startActivity(intent);
                            finish();
                        }, 500);
                    else
                        Toast.makeText(this, "La taille doit être entre 3 et 10 !", Toast.LENGTH_SHORT).show();
                }
                catch (NumberFormatException e)
                {
                    Toast.makeText(this, "Veuillez entrer un nombre valide.", Toast.LENGTH_SHORT).show();
                }
            }
            else
                Toast.makeText(this, "Veuillez entrer une taille de grille.", Toast.LENGTH_SHORT).show();
        });

        btnExit.setOnClickListener(v -> finish());
    }
}

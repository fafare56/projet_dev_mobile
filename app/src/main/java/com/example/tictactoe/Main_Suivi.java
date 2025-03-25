package com.example.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Main_Suivi extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_suivi); // Doit correspondre à votre layout XML

        // Configurer le Spinner de difficulté
        Spinner spinnerDifficulte = findViewById(R.id.spinnerDifficulte);
        String[] difficultes = {"Facile", "Moyen", "Difficile"};
        ArrayAdapter<String> difficulteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, difficultes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.blue_clair));
                return view;
            }
        };
        difficulteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulte.setAdapter(difficulteAdapter);

        // Configurer le Spinner de forme
        Spinner spinnerForme = findViewById(R.id.spinnerForme);
        String[] formes = {"Cercle", "Carré", "Triangle"};
        ArrayAdapter<String> formeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, formes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.blue_clair));
                return view;
            }
        };
        formeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerForme.setAdapter(formeAdapter);

        // Configurer le bouton
        Button buttonStartGame = findViewById(R.id.buttonStartGame);
        buttonStartGame.setBackgroundColor(getResources().getColor(R.color.blue_clair));

        // Action du bouton
        buttonStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String difficulte = spinnerDifficulte.getSelectedItem().toString();
                String forme = spinnerForme.getSelectedItem().toString();
                Intent intent = new Intent(Main_Suivi.this,GameActivity.class);
                intent.putExtra("difficulte", difficulte);
                intent.putExtra("forme", forme);
                startActivityForResult(intent, 1);
            }
        });

        // Configurer le TextView du score
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText("Score: -");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            int score = data.getIntExtra("score", 0);
            TextView scoreTextView = findViewById(R.id.scoreTextView);
            scoreTextView.setText("Score: " + score);
        }
    }
}
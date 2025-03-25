package com.example.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class JeuVieParam extends Activity
{
    private SeekBar sizeSeekBar, densitySeekBar;
    private TextView sizeText, densityText;
    private Button confirmButton, retourMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceuil_jeuvie);

        sizeSeekBar    = findViewById(R.id.size_seekbar);
        densitySeekBar = findViewById(R.id.density_seekbar);
        sizeText       = findViewById(R.id.size_text);
        densityText    = findViewById(R.id.density_text);
        confirmButton  = findViewById(R.id.confirm_button);
        retourMenu     = findViewById(R.id.retour_menu);

        sizeSeekBar.setProgress(20);
        densitySeekBar.setProgress(30);
        updateSizeText(20);
        updateDensityText(0.3f);

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                updateSizeText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        densitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                updateDensityText(progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        confirmButton.setOnClickListener(v ->
        {
            Intent jeuVieIntent = new Intent(JeuVieParam.this, JeuVie.class);
            jeuVieIntent.putExtra("size", sizeSeekBar.getProgress());
            jeuVieIntent.putExtra("density", densitySeekBar.getProgress() / 100f);
            startActivity(jeuVieIntent);
            finish();
        });

        retourMenu.setOnClickListener(v ->
        {
            Intent menu = new Intent(JeuVieParam.this, MainActivity.class);
            startActivity(menu);
            finish();
        });
    }

    private void updateSizeText(int size) {
        sizeText.setText("Taille: " + size + "x" + size);
    }

    private void updateDensityText(float density)
    {
        densityText.setText("Densité: " + String.format("%.2f", density));
    }
}
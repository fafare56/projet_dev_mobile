package com.example.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements SensorEventListener {
    private static final String TAG = "GameActivity";
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private GameView gameView;
    private TextView infoText;
    private String difficulte;
    private String forme;
    private boolean isRunning = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suivi);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscope == null) {
            Toast.makeText(this, "Gyroscope non disponible", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        infoText = findViewById(R.id.infoTextView);
        gameView = findViewById(R.id.gameView);

        difficulte = getIntent().getStringExtra("difficulte");
        if (difficulte == null) difficulte = "Moyen";
        forme = getIntent().getStringExtra("forme");
        if (forme == null) forme = "Cercle";

        gameView.setupGame(difficulte, forme);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        isRunning = true;
        startGameLoop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        isRunning = false;
    }

    private void startGameLoop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                gameView.updateGameState();

                if (gameView.isTourComplet()) {
                    int score = gameView.calculateScore();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("score", score);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    return;
                }

                infoText.setText("Forme: " + forme + " | Progression: " + gameView.getProgression() + "%");
                handler.postDelayed(this, 50); // 20 FPS
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float rotX = event.values[0]; // Inclinaison haut-bas
            float rotY = event.values[1]; // Inclinaison gauche-droite
            float rotZ = event.values[2];

            Log.d(TAG, "Gyroscope - X: " + rotX + ", Y: " + rotY + ", Z: " + rotZ);

            runOnUiThread(() -> gameView.updateCursor(rotY, rotX));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
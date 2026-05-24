package com.example.sensordash;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView tvX, tvY, tvZ;
    private Button btnAccel;
    private boolean isListening = false;

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

        btnAccel = findViewById(R.id.btnAccel);
        tvX = findViewById(R.id.tvX);
        tvY = findViewById(R.id.tvY);
        tvZ = findViewById(R.id.tvZ);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (accelerometer == null) {
            btnAccel.setEnabled(false);
            btnAccel.setText("Accelerometer Not Available");
            tvX.setText("Sensor not found");
            tvY.setText("");
            tvZ.setText("");
            return;
        }

        btnAccel.setOnClickListener(v -> {
            if (isListening) {
                stopSensor();
                btnAccel.setText(R.string.btn_start_accel);
            } else {
                startSensor();
                btnAccel.setText(R.string.btn_stop_accel);
            }
            isListening = !isListening;
        });
    }

    private void startSensor() {
        if (accelerometer != null && sensorManager != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void stopSensor() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            tvX.setText(String.format(Locale.getDefault(), getString(R.string.label_x), x));
            tvY.setText(String.format(Locale.getDefault(), getString(R.string.label_y), y));
            tvZ.setText(String.format(Locale.getDefault(), getString(R.string.label_z), z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isListening) {
            stopSensor();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isListening && accelerometer != null) {
            startSensor();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isListening) {
            stopSensor();
        }
    }
}

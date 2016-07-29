package com.example.radek.grumpyandroid;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor proximitySensor;

    private static float MOVEMENT_THRESHOLD = 2f;
    private static float BASE_AXES_FORCE = 8.0f;

    private AtomicBoolean movementDetectingEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movementDetectingEnabled = new AtomicBoolean(true);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensors();
    }

    public void setMovementDetectingEnabled(boolean movementDetectingEnabled) {
        this.movementDetectingEnabled.set(movementDetectingEnabled);
    }

    public boolean getMovementDetectingEnabled() {
        return movementDetectingEnabled.get();
    }

    final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            final float alpha = 0.8f;

            float[] gravity = new float[3];
            float[] linear_acceleration = new float[3];

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            processLinearAcceleration(linear_acceleration);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void registerSensors() {
        sensorManager.registerListener(sensorEventListener,proximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensors() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void processLinearAcceleration(float[] sensorValues){
        if(getSummedAccelerationForce(sensorValues) > MOVEMENT_THRESHOLD + BASE_AXES_FORCE && getMovementDetectingEnabled()){
            playRandomRantSound();
            setMovementDetectingEnabled(false);

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    setMovementDetectingEnabled(true);
                }

            }, 2000L);
        }
    }

    private float getSummedAccelerationForce(float[] axesForce){
        return Math.abs(axesForce[0]) + Math.abs(axesForce[1]) + Math.abs(axesForce[2]);
    }

    private void playRandomRantSound() {
        Log.d("MOVEMENT", "stop moving me!");
    }
}

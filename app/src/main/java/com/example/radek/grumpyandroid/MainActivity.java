package com.example.radek.grumpyandroid;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ImageReader;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity{

    private ImageView grumpyImageView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor proximityLightSensor;
    private SoundPlayer soundPlayer;
    private static float MOVEMENT_THRESHOLD = 2f;
    private static float BASE_AXES_FORCE = 8.0f;
    private static float PROXIMITY_DISTANCE_THRESHOLD = 20f;

    private AtomicBoolean grumpinessModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grumpyImageView = (ImageView) findViewById(R.id.grumpyAndroidImageView);

        soundPlayer = new SoundPlayer(this, new SoundPlayer.SoundPlayerInterface() {
            @Override
            public void OnSoundsLoaded() {
                Toast.makeText(MainActivity.this, "Sound loaded", Toast.LENGTH_SHORT).show();
            }
        });

        grumpinessModeEnabled = new AtomicBoolean(true);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        proximityLightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        checkForSensorExistenceAndDisplayErrors();
    }

    private void checkForSensorExistenceAndDisplayErrors() {
        if(accelerometer == null && proximityLightSensor == null)
            Toast.makeText(MainActivity.this, "No accelerometer and light sensor! What a rubbish device!", Toast.LENGTH_SHORT).show();
        else if(accelerometer == null)
            Toast.makeText(MainActivity.this, "Nah, Y U NO HAVE ACCELEROMETER", Toast.LENGTH_SHORT).show();
        else if(proximityLightSensor == null)
            Toast.makeText(MainActivity.this, "Nah, Y U NO HAVE LIGHT SENSOR", Toast.LENGTH_SHORT).show();
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

    public void setGrumpiness(boolean flag) {
        this.grumpinessModeEnabled.set(flag);
    }

    public boolean getGrumpinessModeEnabled() {
        return grumpinessModeEnabled.get();
    }

    final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
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
            else if(event.sensor.getType() == Sensor.TYPE_LIGHT)
                processProximityAndLightingDistance(event.values);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void registerSensors() {
        sensorManager.registerListener(sensorEventListener, proximityLightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensors() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void processLinearAcceleration(float[] sensorValues){
        if(getSummedAccelerationForce(sensorValues) > MOVEMENT_THRESHOLD + BASE_AXES_FORCE && getGrumpinessModeEnabled()){
            playRandomRantSound();
            setGrumpyImageVisibility(true);
            muffleGrumpiness();
        }
    }

    private void processProximityAndLightingDistance(float[] sensorValues){
        if(sensorValues[0] < PROXIMITY_DISTANCE_THRESHOLD && getGrumpinessModeEnabled()){
            playRandomRantSound();
            setGrumpyImageVisibility(true);
            muffleGrumpiness();
        }
    }

    private void muffleGrumpiness(){
        setGrumpiness(false);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setGrumpiness(true);
            }
        }, 2000L);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setGrumpyImageVisibility(false);
            }
        },1500);
    }

    private float getSummedAccelerationForce(float[] axesForce){
        return Math.abs(axesForce[0]) + Math.abs(axesForce[1]) + Math.abs(axesForce[2]);
    }

    private void playRandomRantSound() {
        soundPlayer.playRandomRantSound();
    }

    private void setGrumpyImageVisibility(boolean flag){
        grumpyImageView.setVisibility(flag?View.VISIBLE:View.INVISIBLE);
    }
}
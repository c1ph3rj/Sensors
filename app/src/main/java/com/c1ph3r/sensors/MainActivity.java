package com.c1ph3r.sensors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.c1ph3r.sensors.databinding.ActivityMainBinding;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding MAIN;
    SensorManager sensorManager;
    boolean isFlashLightIsOn = false;
    ArrayList<Long> timeStamp = new ArrayList<>();
    Sensor ProximitySensor, MagneticFieldSensor, AccelerometerSensor, LightSensor, GyroSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MAIN = ActivityMainBinding.inflate(getLayoutInflater());
        View view = MAIN.getRoot();
        setContentView(view);



        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        ProximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        MagneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        AccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        GyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(magneticFieldSensorListener, MagneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(proximitySensorListener, ProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(AccelerometerSensorListener, AccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(LightSensorListener,LightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(GyroSensorListener, GyroSensor, SensorManager.SENSOR_DELAY_UI);


    }

    private final SensorEventListener2 GyroSensorListener = new SensorEventListener2() {
        @Override
        public void onFlushCompleted(Sensor sensor) {

        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            String value = "Gyro: " + sensorEvent.values[1];
            MAIN.GyroValue.setText(value);
            MAIN.GyroValue.setOnClickListener(OnClickGyro -> {
                System.out.println(sensorEvent.timestamp);
                    Toast.makeText(MainActivity.this, (sensorEvent.timestamp + " QWERTY"), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }


    };

    private final SensorEventListener LightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            String value = "Available Light: " + sensorEvent.values[0];
            MAIN.LightValue.setText(value);
            if(sensorEvent.values[0]<= 100){
                MAIN.testView.setBackgroundColor(getColor(R.color.white));
                MAIN.textViewOne.setTextColor(getColor(R.color.black));
                MAIN.MagneticFieldValue.setTextColor(getColor(R.color.black));
                MAIN.AccelerometerValue.setTextColor(getColor(R.color.black));
                MAIN.LightValue.setTextColor(getColor(R.color.black));
            }else{
                MAIN.testView.setBackgroundColor(getColor(R.color.black));
                MAIN.textViewOne.setTextColor(getColor(R.color.white));
                MAIN.MagneticFieldValue.setTextColor(getColor(R.color.white));
                MAIN.AccelerometerValue.setTextColor(getColor(R.color.white));
                MAIN.LightValue.setTextColor(getColor(R.color.white));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private final SensorEventListener AccelerometerSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Float valX, valY, valZ;
            if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
                valX = sensorEvent.values[0];
                valY = sensorEvent.values[1];
                valZ = sensorEvent.values[2];
                double acceleration = Math.sqrt((valX * valX) + (valY * valY) + (valZ * valZ));
                String finalValue = "Accelerometer: " + Math.ceil(acceleration) + " m/s^2";
                MAIN.AccelerometerValue.setText(finalValue);

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    private final SensorEventListener magneticFieldSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
           Float valX, valY, valZ;
           if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
               valX = sensorEvent.values[0];
               valY = sensorEvent.values[1];
               valZ = sensorEvent.values[2];
               double magnitude = Math.sqrt((valX * valX) + (valY * valY) + (valZ * valZ));
               String finalValue = "Magnetic Level: " + Math.ceil(magnitude) + " µT";
               MAIN.MagneticFieldValue.setText(finalValue);

           }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private final SensorEventListener proximitySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            String value = "proximity : " + sensorEvent.values[0] + "CM";
            MAIN.textViewOne.setText(value);
            String sensorValue = (sensorEvent.values[0] ==0)? "Sensor Blocked" : "Sensor is not blocked";
            MAIN.textViewOne.setOnClickListener(onClickProximity -> Toast.makeText(MainActivity.this, sensorValue, Toast.LENGTH_SHORT).show());
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(magneticFieldSensorListener);
        sensorManager.unregisterListener(proximitySensorListener);
        sensorManager.unregisterListener(AccelerometerSensorListener);
        sensorManager.unregisterListener(LightSensorListener);
        sensorManager.unregisterListener(GyroSensorListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(magneticFieldSensorListener, MagneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(proximitySensorListener, ProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(AccelerometerSensorListener, AccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(LightSensorListener,LightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(GyroSensorListener, GyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
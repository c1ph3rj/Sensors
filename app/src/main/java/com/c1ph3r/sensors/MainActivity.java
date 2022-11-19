package com.c1ph3r.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.c1ph3r.sensors.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding MAIN;
    SensorManager sensorManager;
    String camId;
    int azimuthDegree, lastDegree = 0;
    RotateAnimation anim;
    float[] accelerometerValues;
    float[] magneticFieldValues;
    CameraManager cameraManager;
    float[] rat = new float[9];
    float[] irat = new float[9];
    float[] ori = new float[3];
    boolean isFlashLightIsOn = false;
    ArrayList<Double> timeStamp = new ArrayList<>();
    Sensor ProximitySensor, MagneticFieldSensor, LinearAccelerometerSensor, LightSensor, GyroSensor, AccelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MAIN = ActivityMainBinding.inflate(getLayoutInflater());
        View view = MAIN.getRoot();
        setContentView(view);

        accelerometerValues = new float[3];
        magneticFieldValues = new float[3];

        cameraManager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        try {
            camId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // Declaring Sensors and Sensor Manager.
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        ProximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        MagneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        LinearAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        GyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        AccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);



        // Registering Sensors to the sensor manager.

        sensorManager.registerListener(magneticFieldSensorListener, MagneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(proximitySensorListener, ProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(LinearAccelerometerSensorListener, LinearAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(LightSensorListener, LightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(GyroSensorListener, GyroSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(AccelerometerSensorListener, AccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);





    }

    private final SensorEventListener AccelerometerSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            accelerometerValues = sensorEvent.values.clone();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // GYROSCOPE SENSOR

    private final SensorEventListener GyroSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            String value = "stable";
            if(sensorEvent.values[1] > 0.5f)
                value = "Gyro: Left";
            else if(sensorEvent.values[1] < -0.5f)
                value = "Gyro: Right";
            if(sensorEvent.values[0] > 0.5f)
                value = "Gyro: up";
            else if(sensorEvent.values[0] < -0.5f)
                value = "Gyro: down";


            MAIN.GyroValue.setText(value);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // LIGHT SENSOR

    private final SensorEventListener LightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            String value = "Available Light: " + sensorEvent.values[0];
            MAIN.LightValue.setText(value);
            if (sensorEvent.values[0] <= 50) {
                MAIN.testView.setBackgroundColor(getColor(R.color.white));
                MAIN.SENSORS.setTextColor(getColor(R.color.color1));
            } else {
                MAIN.testView.setBackgroundColor(getColor(R.color.black));
                MAIN.SENSORS.setTextColor(getColor(R.color.color2));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // ACCELEROMETER SENSOR

    private final SensorEventListener LinearAccelerometerSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Float valX, valY, valZ;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                valX = sensorEvent.values[0];
                valY = sensorEvent.values[1];
                valZ = sensorEvent.values[2];
                double acceleration = Math.sqrt((valX * valX) + (valY * valY) + (valZ * valZ));
                String finalValue = "Accelerometer: " + Math.floor(acceleration) + " m/s^2";
                MAIN.AccelerometerValue.setText(finalValue);
               turnOnFlashLight(acceleration);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    // Chop Chop flashLight.

    private void turnOnFlashLight(double acceleration) {
        if (Math.floor(acceleration) > 75) {
            timeStamp.add(acceleration);
            if (timeStamp.size() == 8) {
                System.out.println(Arrays.toString(timeStamp.toArray()));
                if (camId != null) {
                    try {
                        if (!isFlashLightIsOn) {
                            cameraManager.setTorchMode(camId, true);
                            isFlashLightIsOn = true;
                        } else {
                            cameraManager.setTorchMode(camId, false);
                            isFlashLightIsOn = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                timeStamp = new ArrayList<>();
            }
        }

    }

    // MAGNETIC FIELD SENSOR

    private final SensorEventListener magneticFieldSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Float valX, valY, valZ;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                valX = sensorEvent.values[0];
                valY = sensorEvent.values[1];
                valZ = sensorEvent.values[2];
                double magnitude = Math.sqrt((valX * valX) + (valY * valY) + (valZ * valZ));
                String finalValue = "Magnetic Level: " + Math.ceil(magnitude) + " µT";
                MAIN.MagneticFieldValue.setText(finalValue);

                magneticFieldValues = sensorEvent.values.clone();
                getRotationValues();


                anim = new RotateAnimation(lastDegree, -azimuthDegree, Animation.RELATIVE_TO_SELF ,0.5f, Animation.RELATIVE_TO_SELF ,0.5f );
                lastDegree = -azimuthDegree;
                anim.setDuration(200);
                anim.setFillAfter(true);
                MAIN.compass.startAnimation(anim);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void getRotationValues() {
        SensorManager.getRotationMatrix(rat, ori, accelerometerValues, magneticFieldValues);
        azimuthDegree =  (int) Math.toDegrees(SensorManager.getOrientation(rat, ori )[0]);
    }

    // PROXIMITY SENSOR

    private final SensorEventListener proximitySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            String value = "proximity : " + sensorEvent.values[0] + "CM";
            MAIN.textViewOne.setText(value);
            String sensorValue = (sensorEvent.values[0] == 0) ? "Sensor Blocked" : "Sensor is not blocked";
            MAIN.textViewOne.setOnClickListener(onClickProximity -> Toast.makeText(MainActivity.this, sensorValue, Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // ON PAUSE METHOD

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(magneticFieldSensorListener);
        sensorManager.unregisterListener(proximitySensorListener);
        sensorManager.unregisterListener(LinearAccelerometerSensorListener);
        sensorManager.unregisterListener(LightSensorListener);
        sensorManager.unregisterListener(GyroSensorListener);
    }

    // ON RESUME METHOD

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(magneticFieldSensorListener, MagneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(proximitySensorListener, ProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(LinearAccelerometerSensorListener, LinearAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(LightSensorListener, LightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(GyroSensorListener, GyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
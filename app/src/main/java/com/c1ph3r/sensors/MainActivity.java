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


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding MAIN;
    // Initializing and declaring required variables;
    SensorManager sensorManager;
    CameraManager cameraManager;
    String camId;
    RotateAnimation anim;
    Sensor ProximitySensor, MagneticFieldSensor, LinearAccelerometerSensor, LightSensor, GyroSensor, AccelerometerSensor;
    int azimuthDegree, lastDegree = 0;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
    float[] val = new float[9];
    float[] val2 = new float[9];
    boolean isFlashLightIsOn = false;
    ArrayList<Double> timeStamp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View Binding
        MAIN = ActivityMainBinding.inflate(getLayoutInflater());
        View view = MAIN.getRoot();
        setContentView(view);

        // Getting camera access from the system to turn on flashlight.
        cameraManager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        // Getting ID of the camera for turning on the flashlight.
        try {
            camId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // Declaring Sensors and Sensor Manager.
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        // PROXIMITY - To detect if there is any object close to the screen.
        ProximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        // MAGNETIC FIELD - To detect the magnetic level around the mobile phone.
        MagneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        // LINEAR ACCELERATION - To calculate the magnitude of the device without gravity.
        LinearAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        // LIGHT - To calculate the amount of light available around the device.
        LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        // GYROSCOPE - To calculate the device movement in which direction.
        GyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // ACCELEROMETER - To calculate the magnitude of the device with the gravity.
        AccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);



        // Registering Sensors to the sensor manager with the required listeners.
        sensorManager.registerListener(magneticFieldSensorListener, MagneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(proximitySensorListener, ProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(LinearAccelerometerSensorListener, LinearAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(LightSensorListener, LightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(GyroSensorListener, GyroSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(AccelerometerSensorListener, AccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);


    }
    // ACCELEROMETER SENSOR LISTENER.
    private final SensorEventListener AccelerometerSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // Storing accelerometer values if the values changed.
            accelerometerValues = sensorEvent.values.clone();
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // GYROSCOPE SENSOR LISTENER.
    private final SensorEventListener GyroSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // Detect the device movement axis by the value change.
            String value = getString(R.string.stable_text);
            if(sensorEvent.values[1] > 0.5f)
                value = getString(R.string.GYROLeft_Text);
            else if(sensorEvent.values[1] < -0.5f)
                value = getString(R.string.GYRORight_Text);
            if(sensorEvent.values[0] > 0.5f)
                value = getString(R.string.GYROUp_Text);
            else if(sensorEvent.values[0] < -0.5f)
                value = getString(R.string.GYRODown_Text);

            // Displaying the direction to the user.
            MAIN.GyroValue.setText(value);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // LIGHT SENSOR LISTENER.
    private final SensorEventListener LightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // Detect the light value change.
            String value = getString(R.string.LightSensor_Text) + sensorEvent.values[0];
            // Displaying the amount of light around the user.
            MAIN.LightValue.setText(value);
            // If the light level is less then 50 change the color of the background and the components color according to the background.
            if (sensorEvent.values[0] <= 50) {
                MAIN.testView.setBackgroundColor(getColor(R.color.white));
                MAIN.SENSORS.setTextColor(getColor(R.color.color1));
            } else {
                // Default color of the background and components.
                MAIN.testView.setBackgroundColor(getColor(R.color.black));
                MAIN.SENSORS.setTextColor(getColor(R.color.color2));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // LINEAR_ACCELERATION SENSOR LISTENER.
    private final SensorEventListener LinearAccelerometerSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // Initializing the variable to store the event change.
            Float valX, valY, valZ;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                // Storing the values of X, Y, Z in variables.
                valX = sensorEvent.values[0];
                valY = sensorEvent.values[1];
                valZ = sensorEvent.values[2];
                // Converting the values to the Acceleration.
                double acceleration = Math.sqrt((valX * valX) + (valY * valY) + (valZ * valZ));
                // Converting the Magnitude Level to exact value.
                String finalValue = getString(R.string.Accelerometer_Text) + Math.floor(acceleration) + getString(R.string.Magnitude_Text);
                // Displaying the value to the user.
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
        // If the acceleration is above 75 execute this method.
        if (Math.floor(acceleration) > 75) {
            // Storing the time stamp
            timeStamp.add(acceleration);
            // If there is continues shake equals to double shake execute this method.
            if (timeStamp.size() == 8) {
                // If the camera Id has a value execute this method.
                if (camId != null) {
                    try {
                        // Detecting the flashlight is on or Of by boolean.
                        if (!isFlashLightIsOn) {
                            // If the flashLight is off turn on the flash light.
                            cameraManager.setTorchMode(camId, true);
                            // Set the boolean to true.
                            isFlashLightIsOn = true;
                        } else {
                            // If the flashLight is on turn off the flash light.
                            cameraManager.setTorchMode(camId, false);
                            // Set the boolean value to false.
                            isFlashLightIsOn = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Clearing the timestamp arrayList for another shake.
                timeStamp = new ArrayList<>();
            }
        }

    }

    // MAGNETIC FIELD SENSOR LISTENER.
    private final SensorEventListener magneticFieldSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // Declaring the required variables to store the values of the sensor event.
            Float valX, valY, valZ;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // Storing the event values in the declared variables.
                valX = sensorEvent.values[0];
                valY = sensorEvent.values[1];
                valZ = sensorEvent.values[2];
                // Converting the values to the magnitude.
                double magnitude = Math.sqrt((valX * valX) + (valY * valY) + (valZ * valZ));
                // Converting the point value to the float
                String finalValue = getString(R.string.MagneticField_Text) + Math.floor(magnitude) + getString(R.string.Magnitude_Value);
                MAIN.MagneticFieldValue.setText(finalValue);

                // Storing the event values for the compass.
                magneticFieldValues = sensorEvent.values.clone();

                // Get the AZIMUTH value using Magnetic and Accelerometer sensor.
                getRotationValues();
                // setting the compass animation according to the AZIMUTH degree occurred.
                anim = new RotateAnimation(lastDegree, -azimuthDegree, Animation.RELATIVE_TO_SELF ,0.5f, Animation.RELATIVE_TO_SELF ,0.5f );
                lastDegree = -azimuthDegree;
                anim.setDuration(200);
                anim.setFillAfter(true);
                // Starting the animation for the compass.
                MAIN.compass.startAnimation(anim);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // Method used to get the azimuth degree from the accelerometer values and magnetic sensor values.
    private void getRotationValues() {
        // Getting the rotation matrix from the accelerometer and magnetic sensor.
        SensorManager.getRotationMatrix(val, val2, accelerometerValues, magneticFieldValues);
        // Converting the orientation occurred form the rotationMatrix to the azimuth degree.
        azimuthDegree =  (int) Math.toDegrees(SensorManager.getOrientation(val, val2 )[0]);
    }

    // PROXIMITY SENSOR LISTENER.
    private final SensorEventListener proximitySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // Getting the proximity value from the sensor and storing the value to the string.
            String value = getString(R.string.Proximity_Text) + sensorEvent.values[0] + getString(R.string.Proximity_Value);
            // Displaying the String to the user.
            MAIN.textViewOne.setText(value);
            // Toast a message to the user if the sensor is blocked or not using if else.
            if ((sensorEvent.values[0] == 0)) {
                Toast.makeText(MainActivity.this, R.string.sensorBlocked, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, R.string.sensorUnblock, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // ON PAUSE METHOD
    @Override
    protected void onPause() {
        super.onPause();
        // Un Registering the sensors from sensor manager to avoid battery consumption and background running.
        sensorManager.unregisterListener(magneticFieldSensorListener);
        sensorManager.unregisterListener(proximitySensorListener);
        sensorManager.unregisterListener(LinearAccelerometerSensorListener);
        sensorManager.unregisterListener(LightSensorListener);
        sensorManager.unregisterListener(GyroSensorListener);
        sensorManager.unregisterListener(AccelerometerSensorListener);
    }

    // ON RESUME METHOD

    @Override
    protected void onResume() {
        super.onResume();
        // Re Registering the sensor to avoid exception and continue the use of sensor.
        sensorManager.registerListener(magneticFieldSensorListener, MagneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(proximitySensorListener, ProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(LinearAccelerometerSensorListener, LinearAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(LightSensorListener, LightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(GyroSensorListener, GyroSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(AccelerometerSensorListener, AccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);


    }
}
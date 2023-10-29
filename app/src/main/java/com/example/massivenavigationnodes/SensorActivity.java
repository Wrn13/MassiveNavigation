package com.example.massivenavigationnodes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Arrays;

import massive_navigation.snapchat.R;

public class SensorActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;

    private final float [] runningVelocityReading = {0.0F,0.0F,0.0F};

    private final float [] positionReading = new float[3];

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    private ImageView compassImage;
    private Sensor magnetometer;
    private Sensor accelerometer;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private float[] orientation = new float[3];
    private TextView compassOutput;


    long prevTime = 0L;
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_display);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compassImage = findViewById(R.id.compass);
        compassOutput = findViewById(R.id.compassOutput);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sensorManager.registerListener(this, accelerometer,
                        SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
            }
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sensorManager.registerListener(this, magneticField,
                        SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
            }
        }

        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);

        sensorManager.unregisterListener(this, magnetometer);
        sensorManager.unregisterListener(this, accelerometer);
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
        long currentTime = System.currentTimeMillis();
        if(currentTime- prevTime > 1000){
            updateOrientationAngles();
            prevTime = currentTime;
        }

        if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;

        } else if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            compassOutput.setText(String.format("%f", azimuthInDegrees));
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "rotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        positionReading[0] = runningVelocityReading[0] + (float) (Math.cos(orientationAngles[0]) * Math.cos(orientationAngles[1]) * accelerometerReading[0]
        + (Math.cos(orientationAngles[0]) * Math.sin(orientationAngles[1]) * Math.sin(orientationAngles[2]) - Math.sin(orientationAngles[0])*Math.cos(orientationAngles[2]))*accelerometerReading[1]
        + (Math.cos(orientationAngles[0]) * Math.sin(orientationAngles[1]) * Math.cos(orientationAngles[2]) + Math.sin(orientationAngles[0])*Math.sin(orientationAngles[2]))*accelerometerReading[2]);
        positionReading[1] = runningVelocityReading[1] + (float) (Math.sin(orientationAngles[0]) * Math.cos(orientationAngles[1]) *accelerometerReading[0]
                + (Math.sin(orientationAngles[0]) * Math.sin(orientationAngles[1]) * Math.sin(orientationAngles[2]) + Math.cos(orientationAngles[0])*Math.cos(orientationAngles[2]))*accelerometerReading[1]
                + (Math.sin(orientationAngles[0]) * Math.sin(orientationAngles[1]) * Math.cos(orientationAngles[2]) - Math.cos(orientationAngles[0])*Math.sin(orientationAngles[2]))*accelerometerReading[2]);
        positionReading[2] = runningVelocityReading[2] + (float) (-1*Math.sin(orientationAngles[1]) * positionReading[0] + Math.sin(orientationAngles[0])*Math.cos(orientationAngles[1]) * accelerometerReading[1]
        + Math.cos(orientationAngles[0]) * Math.cos(orientationAngles[1]) * accelerometerReading[2]);
    }
}
/*package com.example.massivenavigation;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.widget.TextView;

public class SensorActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;

    private final float [] runningVelocityReading = {0.0F,0.0F,0.0F};

    private final float [] positionReading = new float[3];

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    TextView orientationView;
    TextView positionView;

    long prevTime = 0L;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_display);
        orientationView = (TextView)findViewById(R.id.OrientationData);
        positionView = (TextView) findViewById(R.id.PositionData);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

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
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);
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
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "rotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        // "orientationAngles" now has up-to-date information.
        orientationView.setText("");
        orientationView.append("Orientation Matrix:\n ");
        for (float orientationAngle : orientationAngles) {
            orientationView.append(orientationAngle + " ");
        }
        orientationView.append("\n");

        positionView.setText("");
        positionView.append("Position Data:");
        positionReading[0] = runningVelocityReading[0] + (float) (Math.cos(orientationAngles[0]) * Math.cos(orientationAngles[1]) * accelerometerReading[0]
        + (Math.cos(orientationAngles[0]) * Math.sin(orientationAngles[1]) * Math.sin(orientationAngles[2]) - Math.sin(orientationAngles[0])*Math.cos(orientationAngles[2]))*accelerometerReading[1]
        + (Math.cos(orientationAngles[0]) * Math.sin(orientationAngles[1]) * Math.cos(orientationAngles[2]) + Math.sin(orientationAngles[0])*Math.sin(orientationAngles[2]))*accelerometerReading[2]);
        positionReading[1] = runningVelocityReading[1] + (float) (Math.sin(orientationAngles[0]) * Math.cos(orientationAngles[1]) *accelerometerReading[0]
                + (Math.sin(orientationAngles[0]) * Math.sin(orientationAngles[1]) * Math.sin(orientationAngles[2]) + Math.cos(orientationAngles[0])*Math.cos(orientationAngles[2]))*accelerometerReading[1]
                + (Math.sin(orientationAngles[0]) * Math.sin(orientationAngles[1]) * Math.cos(orientationAngles[2]) - Math.cos(orientationAngles[0])*Math.sin(orientationAngles[2]))*accelerometerReading[2]);
        positionReading[2] = runningVelocityReading[2] + (float) (-1*Math.sin(orientationAngles[1]) * positionReading[0] + Math.sin(orientationAngles[0])*Math.cos(orientationAngles[1]) * accelerometerReading[1]
        + Math.cos(orientationAngles[0]) * Math.cos(orientationAngles[1]) * accelerometerReading[2]);
        for(int i =(int)'x'; i<=(int)'z'; ++i){
            positionView.append((char) i + ": " + positionReading[(int)(i-'x')] + "\n");
        }
        positionView.append("\n");
    }
}*/

/* $Id$ */
package org.crazydays.android.sensor;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.crazydays.android.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;

/**
 * SensorActivity
 */
public class SensorsActivity
    extends Activity
    implements SensorEventListener, LocationListener
{
    /** sensor manager */
    protected SensorManager sensorManager;

    /** magnetic sensor */
    protected Sensor magnetic;

    /** magnetic sensor reading times */
    protected List<Long> magneticTimes = new ArrayList<Long>(11);

    /** magnetic values */
    protected float[] magneticValues;

    /** accelerometer */
    protected Sensor accelerometer;

    /** accelerometer sensor reading times */
    protected List<Long> accelerometerTimes = new ArrayList<Long>(11);

    /** accelerometer values */
    protected float[] accelerometerValues;

    /** orientation values */
    protected float[] orientationValues = new float[3];

    /** location manager */
    protected LocationManager locationManager;

    /** location reading times */
    protected List<Long> locationTimes = new ArrayList<Long>();

    /** satellites count */
    protected int satellites;

    /**
     * On create.
     * 
     * @param state State
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        setContentView(R.layout.sensor);

        setupSensors();
    }

    /**
     * Setup sensors.
     */
    protected void setupSensors()
    {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer =
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        sensorManager.registerListener(this, magnetic,
            SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, accelerometer,
            SensorManager.SENSOR_DELAY_UI);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
            0, this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        sensorManager.unregisterListener(this, magnetic);
        sensorManager.unregisterListener(this, accelerometer);
        locationManager.removeUpdates(this);

    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor == magnetic) {
            handleMagneticEvent(event);
        } else if (event.sensor == accelerometer) {
            handleAccelerometerEvent(event);
        }

        updateOrientation();
    }

    /**
     * Calculate average times and reset queue to 10.
     * 
     * @param values Values
     * @return Average
     */
    protected long averageTime(List<Long> values)
    {
        if (values.size() < 2) {
            return 0;
        }

        long first = values.remove(0);
        long last = values.get(values.size() - 1);

        return (last - first) / values.size();
    }

    /**
     * Handle magnetic events.
     * 
     * @param event Events
     */
    protected void handleMagneticEvent(SensorEvent event)
    {
        magneticTimes.add(System.currentTimeMillis());
        ((EditText) findViewById(R.id.magneticDisplayTimes)).setText(Long
            .toString(averageTime(magneticTimes)));

        magneticValues = event.values.clone();

        setText((EditText) findViewById(R.id.magneticDisplayX),
            magneticValues[0]);
        setText((EditText) findViewById(R.id.magneticDisplayY),
            magneticValues[1]);
        setText((EditText) findViewById(R.id.magneticDisplayZ),
            magneticValues[2]);
    }

    /**
     * Handle accelerometer events.
     * 
     * @param event Event
     */
    protected void handleAccelerometerEvent(SensorEvent event)
    {
        accelerometerTimes.add(System.currentTimeMillis());
        ((EditText) findViewById(R.id.accelDisplayTimes)).setText(Long
            .toString(averageTime(accelerometerTimes)));

        accelerometerValues = event.values.clone();

        setText((EditText) findViewById(R.id.accelDisplayX),
            accelerometerValues[0]);
        setText((EditText) findViewById(R.id.accelDisplayY),
            accelerometerValues[1]);
        setText((EditText) findViewById(R.id.accelDisplayZ),
            accelerometerValues[2]);
    }

    /**
     * Update orientation values.
     */
    protected void updateOrientation()
    {
        if (magneticValues == null || accelerometerValues == null) {
            return;
        }

        float[] r = new float[9];
        float[] i = new float[9];

        SensorManager.getRotationMatrix(r, i, accelerometerValues,
            magneticValues);

        SensorManager.getOrientation(r, orientationValues);

        setText((EditText) findViewById(R.id.orientDisplayX),
            Math.toDegrees(orientationValues[0]));
        setText((EditText) findViewById(R.id.orientDisplayY),
            Math.toDegrees(orientationValues[1]));
        setText((EditText) findViewById(R.id.orientDisplayZ),
            Math.toDegrees(orientationValues[2]));
    }

    /**
     * Set editText text value.
     * 
     * @param editText EditText
     * @param value Value
     */
    protected void setText(EditText editText, double value)
    {
        editText.setText(MessageFormat.format("{0,number,#.####}", value));
    }

    @Override
    public void onLocationChanged(Location location)
    {
        locationTimes.add(System.currentTimeMillis());
        ((EditText) findViewById(R.id.locationDisplayTimes)).setText(Long
            .toString(averageTime(locationTimes)));

        ((EditText) findViewById(R.id.locationDisplaySource)).setText(location
            .getProvider() + ": " + satellites);
        setText((EditText) findViewById(R.id.locationDisplayLatitude),
            location.getLatitude());
        setText((EditText) findViewById(R.id.locationDisplayLongitude),
            location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        ((EditText) findViewById(R.id.locationDisplaySource))
            .setText(getString(R.string.disabled));
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        ((EditText) findViewById(R.id.locationDisplaySource)).setText(provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        satellites = extras.getInt("satellites");
    }
}

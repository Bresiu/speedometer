package bresiu.speedometer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class MyActivity extends Activity implements SensorEventListener, View.OnClickListener {

    private static final int SENSOR_RATE = SensorManager.SENSOR_DELAY_FASTEST;
    private static final double N2S = 1000000000.0d;

    private SensorManager mSensorManager;

    private Sensor mGyroSensor;
    private Sensor mMagSensor;
    private Sensor mAccSensor;
    private Sensor mLinearAccSensor;

    private int mCurrentSensorAccuracy = 0;
    private long mStartTimestamp = 0l;
    private long mTimeFromStart = 0l;
    private double mSpeed;
    private long dt = 0l;
    private double distance = 0.0;
    private boolean isCalibrating = false;

    private int calibratingCycle = 0;
    private double calibratingValue = 0.0;

    private TextView mSpeedValue;
    private TextView mDistanceValue;
    private TextView mSensorAccuracy;
    private TextView mTimeValue;
    private Button mCalibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        initViews();
        initSensors();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGyroSensor != null) {
            mSensorManager.registerListener(this, mGyroSensor, SENSOR_RATE);
        }
        if (mMagSensor != null) {
            mSensorManager.registerListener(this, mMagSensor, SENSOR_RATE);
        }
        if (mAccSensor != null) {
            mSensorManager.registerListener(this, mAccSensor, SENSOR_RATE);
        }
        if (mLinearAccSensor != null) {
            mSensorManager.registerListener(this, mLinearAccSensor, SENSOR_RATE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void initSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    private void initViews() {
        Button mResetButton = (Button) findViewById(R.id.reset_button);
        mCalibrate = (Button) findViewById(R.id.calibrate_button);
        mSpeedValue = (TextView) findViewById(R.id.speed_value);
        mDistanceValue = (TextView) findViewById(R.id.distance_value);
        mSensorAccuracy = (TextView) findViewById(R.id.sensor_accuracy_value);
        mTimeValue = (TextView) findViewById(R.id.time_value);
        mResetButton.setOnClickListener(this);
        mCalibrate.setOnClickListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                calculateSpeed(event);
                break;
            case Sensor.TYPE_GYROSCOPE:

                break;
            case Sensor.TYPE_MAGNETIC_FIELD:

                break;
            case Sensor.TYPE_ACCELEROMETER:

                break;
        }
    }

    private void calibrate() {
        if (isCalibrating) {
            String toastText = (calibratingValue / calibratingCycle) + "";
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
            isCalibrating = false;
            calibratingCycle = 0;
            calibratingValue = 0.0;
            mCalibrate.setText("Calibrate");
        } else {
            isCalibrating = true;
            mCalibrate.setText("Stop Calibrating");
        }
    }

    private void calculateSpeed(SensorEvent event) {
        if (mStartTimestamp != 0l) {
            double a = event.values[1];
            dt = event.timestamp - mStartTimestamp;
            mStartTimestamp = event.timestamp;
            mSpeed += a * (dt / N2S);
            if (mSpeed >= 0) {
                updateSpeed(mSpeed);
                calculateDistance(mSpeed, dt / N2S);
                if (isCalibrating) {
                    addValues(a);
                }
            } else {
                mSpeed = 0.0;
                mSpeedValue.setText("0.0");
            }
            calculateTimeFromStart(dt);
        } else {
            mStartTimestamp = event.timestamp;
        }
    }

    private void addValues(double a) {
        calibratingCycle++;
        calibratingValue += a;
    }

    private void calculateDistance(double speed, double dt) {
        distance += (speed * dt);
        mDistanceValue.setText(round(distance, 2) + "");
    }

    private void updateSpeed(double currentSpeed) {
        String speedString = (currentSpeed + "").substring(0, 3);
        mSpeedValue.setText(speedString);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void calculateTimeFromStart(long dt) {
        mTimeFromStart += dt;
        mTimeValue.setText(round(mTimeFromStart / N2S, 2) + "");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (mCurrentSensorAccuracy != accuracy) {
                mSensorAccuracy.setText(getAccuracyText(accuracy));
                mCurrentSensorAccuracy = accuracy;
            }
        }
    }

    private String getAccuracyText(int accuracy) {
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                return "high";
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                return "medium";
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                return "low";
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                return "unreliable";
            default:
                return "?";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_button:
                reset();
                break;
            case R.id.calibrate_button:
                calibrate();
                break;
        }
    }

    private void reset() {
        mSensorManager.unregisterListener(this);
        mStartTimestamp = 0l;
        mTimeFromStart = 0l;
        dt = 0l;
        mSpeed = 0;
        mSpeedValue.setText("0.0");
        distance = 0.0;
        mDistanceValue.setText("0");
        onResume();
    }
}

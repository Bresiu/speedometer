package bresiu.speedometer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

import bresiu.speedometer.logs.Array;
import bresiu.speedometer.logs.DeltaZArray;
import bresiu.speedometer.logs.NewArray;
import bresiu.speedometer.logs.Point;


public class MyActivity extends Activity implements SensorEventListener, View.OnClickListener {

    private static final int SENSOR_RATE = SensorManager.SENSOR_DELAY_FASTEST;
    private static final double N2S = 1000000000.0d;
    private static final float GERROR = 0.004f;
    private static final float DELTA_ERROR = 2.0f;

    private Vibrator vibrator;

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
    private int count = 0;
    private int smoothCount = 0;
    private long previousTimeStamp = 0l;

    private double x;
    private double y;
    private double z;
    private double v;

    private int mYScale = 5;

    private int calibratingCycle = 0;
    private double calibratingValue = 0.0;

    private Array array;
    private NewArray newArray;
    private DeltaZArray deltaZArray;

    private TextView mStepCount;
    private TextView mSpeedValue;
    private TextView mDistanceValue;
    private TextView mSensorAccuracy;
    private TextView mTimeValue;
    private TextView mYScaleValue;
    private Button mCalibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        initViews();
        initSensors();

        array = new Array();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        if (mGyroSensor != null) {
            mSensorManager.registerListener(this, mGyroSensor, SENSOR_RATE);
        }
        if (mMagSensor != null) {
            mSensorManager.registerListener(this, mMagSensor, SENSOR_RATE);
        }
        if (mAccSensor != null) {
            mSensorManager.registerListener(this, mAccSensor, SENSOR_RATE);
        }
        */
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
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //mMagSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    private void initViews() {
        Button mResetButton = (Button) findViewById(R.id.reset_button);
        Button mPlus = (Button) findViewById(R.id.plus_button);
        Button mMinus = (Button) findViewById(R.id.minus_button);
        mStepCount = (TextView) findViewById(R.id.step_count);
        mYScaleValue = (TextView) findViewById(R.id.acc_value);
        mCalibrate = (Button) findViewById(R.id.calibrate_button);
        mSpeedValue = (TextView) findViewById(R.id.speed_value);
        mDistanceValue = (TextView) findViewById(R.id.distance_value);
        mSensorAccuracy = (TextView) findViewById(R.id.sensor_accuracy_value);
        mTimeValue = (TextView) findViewById(R.id.time_value);
        mResetButton.setOnClickListener(this);
        mPlus.setOnClickListener(this);
        mMinus.setOnClickListener(this);
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
            Toast.makeText(this, toastText + " in " + calibratingCycle, Toast.LENGTH_LONG).show();
            isCalibrating = false;
            calibratingCycle = 0;
            calibratingValue = 0.0;
            mCalibrate.setText("Calibrate");
            array.saveLogs(array.returnArray());
            newArray.saveLogs(newArray.returnArray());
            deltaZArray.saveLogs(deltaZArray.returnArray());
        } else {
            array = new Array();
            newArray = new NewArray();
            deltaZArray = new DeltaZArray();
            isCalibrating = true;
            mCalibrate.setText("Stop Calibrating");
        }
    }

    private void calculateSpeed(SensorEvent event) {
        double accX = event.values[0];
        double accY = event.values[1];
        double accZ = event.values[2];
        double accV = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

        // Removing bias on each measurement High Pass Filter
        if (accX < 2 && accX > -2) { accX = 0.0; }
        if (accY < 2 && accY > -2) { accY = 0.0; }
        if (accZ < 2 && accZ > -2) { accZ = 0.0; }
        if (accV < 2 && accV > -2) { accV = 0.0; }

        // 5-point smoothing
        if (smoothCount <= 5) {
            x += accX;
            y += accY;
            z += accZ;
            v += accV;
            smoothCount++;
        } else {
            x /= smoothCount;
            y /= smoothCount;
            z /= smoothCount;
            v /= smoothCount;
            smoothCount = 0;

            Point point = new Point(count, x, y, z, v);
            array.insertPoint(point);

            x = 0;
            y = 0;
            z = 0;
            v = 0;
        }
        count++;
        mTimeValue.setText("" + count);
        if (count == 5000) {
            vibrator.vibrate(500);
            array.saveLogs(array.returnArray());
        }

        /*
        if (mStartTimestamp != 0l) {
            double a = event.values[1];
            if (event.values[2] > mYScale) {
                vibrator.vibrate(100);
            }
            dt = event.timestamp - mStartTimestamp;
            mStartTimestamp = event.timestamp;
            mSpeed += a * (dt / N2S);
            if (mSpeed >= 0) {
                updateSpeed(mSpeed);
                double distance = calculateDistance(mSpeed, dt / N2S);
                if (isCalibrating) {
                    addValues(a);
                    Point point = new Point(date.get(DateTimeFieldType.millisOfDay()),
                            event.values[0], event.values[1],
                            event.values[2], mSpeed, distance);
                    double vector = Math.sqrt(event.values[0] * 2 + event.values[1] * 2 + event
                            .values[2] * 2);
                    NewPoint newPoint = new NewPoint(date.get(DateTimeFieldType.millisOfDay()),
                            vector);
                    newArray.insertPoint(newPoint);
                    array.insertPoint(point);
                }
            } else {
                mSpeed = 0.0;
                mSpeedValue.setText("0.0");
            }
            calculateTimeFromStart(dt);
        } else {
            mStartTimestamp = event.timestamp;
        }
        */

    }

    private void addValues(double a) {
        calibratingCycle++;
        calibratingValue += a;
    }

    private double calculateDistance(double speed, double dt) {
        distance += (speed * dt);
        mDistanceValue.setText(round(distance, 2) + "");
        return distance;
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
            case R.id.plus_button:
                if (mYScale < 16) {
                    mYScale++;
                    mYScaleValue.setText(mYScale + "");
                }
                break;
            case R.id.minus_button:
                if (mYScale > 0) {
                    mYScale--;
                    mYScaleValue.setText(mYScale + "");
                }
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
        count = 0;
        mStepCount.setText("0");
        mDistanceValue.setText("0");
        onResume();
    }
}

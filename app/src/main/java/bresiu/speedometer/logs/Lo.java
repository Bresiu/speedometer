package bresiu.speedometer.logs;

import android.util.Log;

public class Lo {

    public static final String TAG = "SENSOR";

    public static void g(String message) {
        Log.d(TAG, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }
}
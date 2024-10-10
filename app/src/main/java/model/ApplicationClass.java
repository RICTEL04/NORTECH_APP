package model;

import android.app.Application;
import android.util.Log;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.onesignal.Continue;

public class ApplicationClass extends Application {

    // Replace with your actual ONESIGNAL_APP_ID
    private static final String ONESIGNAL_APP_ID = "f2624cd6-dc6a-45c8-a899-9d44839dd448";
    public static String userId = ""; // Public static variable to hold user ID

    @Override
    public void onCreate() {
        super.onCreate();

        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

        // Request permission for notifications
        OneSignal.getNotifications().requestPermission(false, Continue.none());

        // Update the public userId variable
        updateUserId(); // Call the method to update userId
    }

    // Method to update the userId
    public void updateUserId() {
        // Get the OneSignal user ID and update the public variable
        String newUserId = OneSignal.getUser().getPushSubscription().getId(); // Get the user ID
        if (newUserId != null) {
            userId = newUserId; // Update the public static variable
            Log.d("OneSignal", "Updated User ID: " + userId); // Log the updated user ID
        } else {
            Log.d("OneSignal", "User ID is null"); // Log if user ID is null
        }
    }
}

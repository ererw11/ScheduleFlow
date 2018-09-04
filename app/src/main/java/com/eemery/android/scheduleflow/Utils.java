package com.eemery.android.scheduleflow;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    // Confirms if the user is signed in, if not sends the user to the sign in page
    public static void confirmSignedIn(Context packageContext, FirebaseUser user) {
        if (user == null) {
            // user is signed out, go to the trip activity to sign in
            Intent intent = SignInActivity.createIntent(packageContext);
            packageContext.startActivity(intent);
        }
    }

    // Formats the Date to MM/DD/YYYY 12:00 AM
    public static String formatDateWithTime(Date date) {
        String dateFormat = "MM/dd/yy h:mm a";
        return DateFormat.format(dateFormat, date).toString();
    }

    public static String createTitleString(Appointment appointment) {
        return appointment.getUserName() + " with " + appointment.getStylist();
    }

    public static int getStylistPosition(String selectedStylist, List<String> styList) {
        Log.i(TAG, selectedStylist);
        Log.i(TAG, String.valueOf(styList.size()));
        for (int i = 0; i < styList.size(); i++) {
            if (styList.get(i).equalsIgnoreCase(selectedStylist)){
                return i;
            }
        }
        // If nothing matches, return 0
        return 0;
    }
}

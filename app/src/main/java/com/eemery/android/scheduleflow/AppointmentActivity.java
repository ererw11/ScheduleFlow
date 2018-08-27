package com.eemery.android.scheduleflow;

import android.content.Context;
import android.content.Intent;

import java.util.UUID;

import androidx.fragment.app.Fragment;

public class AppointmentActivity extends SingleFragmentActivity {

    private static final String EXTRA_APPOINTMENT_ID =
            "com.emery.android.scheduleflow.appointment_id";

    public static Intent newIntent(Context context, UUID appointmentId) {
        Intent intent = new Intent(context, AppointmentActivity.class);
        intent.putExtra(EXTRA_APPOINTMENT_ID, appointmentId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID appointmentId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_APPOINTMENT_ID);
        return AppointmentFragment.newInstance(appointmentId);
    }
}

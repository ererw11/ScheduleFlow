package com.eemery.android.scheduleflow;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class AppointmentListActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AppointmentListActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new AppointmentListFragment();
    }
}

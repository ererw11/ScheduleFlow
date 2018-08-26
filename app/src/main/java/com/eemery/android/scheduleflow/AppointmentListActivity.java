package com.eemery.android.scheduleflow;

import androidx.fragment.app.Fragment;

public class AppointmentListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AppointmentListFragment();
    }
}

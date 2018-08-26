package com.eemery.android.scheduleflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class AppointmentActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new AppointmentFragment();
    }
}

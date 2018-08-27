package com.eemery.android.scheduleflow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class AppointmentPagerActivity extends AppCompatActivity {
    private static final String EXTRA_APPOINTMENT_ID =
            "com.eemery.android.scheduleflow.appointment_id";

    private ViewPager viewPager;
    private List<Appointment> appointments;

    public static Intent newIntent(Context context, UUID appointmentId) {
        Intent intent = new Intent(context, AppointmentPagerActivity.class);
        intent.putExtra(EXTRA_APPOINTMENT_ID, appointmentId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_pager);

        UUID appointmentId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_APPOINTMENT_ID);

        viewPager= findViewById(R.id.appointment_view_pager);

        appointments = CalendarLab.get(this).getAppointmentList();
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Appointment appointment = appointments.get(position);
                return AppointmentFragment.newInstance(appointment.getId());
            }

            @Override
            public int getCount() {
                return appointments.size();
            }
        });

        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId().equals(appointmentId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }
}

package com.eemery.android.scheduleflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppointmentListFragment extends Fragment {

    private RecyclerView appointmentRecyclerView;
    private FloatingActionButton addApointmentFab;

    private AppointmentAdapter adapter;

    public AppointmentListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_appointment_list, container, false);

        appointmentRecyclerView = v.findViewById(R.id.appointment_recycler_view);
        appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        addApointmentFab = v.findViewById(R.id.add_appointment_fab);
        addApointmentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Appointment appointment = new Appointment();
                CalendarLab.get(getActivity()).addApointment(appointment);
                Intent intent = AppointmentPagerActivity
                        .newIntent(getActivity(), appointment.getId());
                startActivity(intent);
            }
        });

        updateUI();

        return v;
    }

    private void updateUI() {
        CalendarLab calendarLab = CalendarLab.get(getActivity());
        List<Appointment> appointments = calendarLab.getAppointmentList();

        if (adapter == null) {
            adapter = new AppointmentAdapter(appointments);
            appointmentRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private class AppointmentHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        private TextView appointmentWithTextView;
        private TextView appointmentDateTextView;
        private Appointment appointment;

        public AppointmentHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_appointment, parent, false));

            appointmentWithTextView = itemView.findViewById(R.id.appointment_with_name);
            appointmentDateTextView = itemView.findViewById(R.id.appointment_date);
            itemView.setOnClickListener(this);
        }

        public void bind(Appointment appointment) {
            this.appointment = appointment;
            appointmentWithTextView.setText(appointment.getAppointmentWith());
            appointmentDateTextView.setText(appointment.getDate().toString());
        }

        @Override
        public void onClick(View view) {
            Intent intent = AppointmentPagerActivity.newIntent(getActivity(), appointment.getId());
            startActivity(intent);
        }
    }

    private class AppointmentAdapter extends RecyclerView.Adapter<AppointmentHolder>  {

        private List<Appointment> appointments;

        public AppointmentAdapter(List<Appointment> appointments) {
            this.appointments = appointments;
        }

        @Override
        public AppointmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new AppointmentHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(AppointmentHolder holder, int position) {
            Appointment appointment = appointments.get(position);
            holder.bind(appointment);
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }
    }
}

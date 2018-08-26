package com.eemery.android.scheduleflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppointmentListFragment extends Fragment {

    private RecyclerView appointmentRecyclerView;
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

        updateUI();

        return v;
    }

    private void updateUI() {
        CalendarLab calendarLab = CalendarLab.get(getActivity());
        List<Appointment> appointments = calendarLab.getAppointmentList();

        adapter = new AppointmentAdapter(appointments);
        appointmentRecyclerView.setAdapter(adapter);
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
            appointmentWithTextView.setText(appointment.getAppointmentFor());
            appointmentDateTextView.setText(appointment.getDate().toString());
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), this.appointment.getNotes() + " clicked!", Toast.LENGTH_SHORT).show();
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

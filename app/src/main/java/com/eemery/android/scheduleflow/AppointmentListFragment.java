package com.eemery.android.scheduleflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppointmentListFragment extends Fragment {

    private static final String TAG = AppointmentListFragment.class.getSimpleName();

    private RecyclerView appointmentRecyclerView;
    private FloatingActionButton addAppointmentFab;

    private FirebaseFirestore firebaseFirestore;

    private AppointmentAdapter adapter;

    public AppointmentListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_appointment_list, container, false);

        appointmentRecyclerView = v.findViewById(R.id.appointment_recycler_view);
        appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        addAppointmentFab = v.findViewById(R.id.add_appointment_fab);
        addAppointmentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Appointment appointment = new Appointment();
                CalendarLab.get(getActivity()).addApointment(appointment);
                Intent intent = AppointmentPagerActivity
                        .newIntent(getActivity(), appointment.getId());
                startActivity(intent);
            }
        });

        acquireAppointmentsFromDb();

        updateUI();

        return v;
    }

    private void acquireAppointmentsFromDb() {

    }

    private void updateUI() {
        CalendarLab calendarLab = CalendarLab.get(getActivity());
        final List<Appointment> appointments = calendarLab.getAppointmentList();

        firebaseFirestore.collection("appointments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // Get Appointment ID and convert back to UUID
                                String idString = document.getId();
                                UUID stringIdToUUID = UUID.fromString(idString);

                                // Get Appointment Date
                                Date appointmentDate = document.getDate("date");

                                // Get Appointment With
                                String appointmentWith = document.getString("appointmentWith");

                                // Get Appointment User
                                String appointmentUser = document.getString("userName");

                                // Get Appointment Notes
                                String appointmentNotes = document.getString("notes");

                                // Create a new Appointment and add all the details
                                Appointment appointmentForList = new Appointment();

                                appointmentForList.setId(stringIdToUUID);
                                appointmentForList.setDate(appointmentDate);
                                appointmentForList.setAppointmentWith(appointmentWith);
                                appointmentForList.setUserName(appointmentUser);
                                appointmentForList.setNotes(appointmentNotes);

                                CalendarLab.get(getActivity()).addApointment(appointmentForList);
                            }

                            // Add the Appointments to the RecyclerView
                            if (adapter == null) {
                                adapter = new AppointmentAdapter(appointments);
                                appointmentRecyclerView.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }

                        } else {
                            // Data was not pulled from Db
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private class AppointmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

    private class AppointmentAdapter extends RecyclerView.Adapter<AppointmentHolder> {

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

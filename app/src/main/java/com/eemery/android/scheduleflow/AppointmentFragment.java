package com.eemery.android.scheduleflow;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

import androidx.fragment.app.Fragment;

public class AppointmentFragment extends Fragment {

    private static final String ARG_APPOINTMENT_ID = "appointment_id";

    private Appointment appointment;
    private TextInputEditText notesEditText;
    private MaterialButton dateButton;

    public static AppointmentFragment newInstance(UUID appointmentId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_APPOINTMENT_ID, appointmentId);

        AppointmentFragment fragment = new AppointmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID appointmentId = (UUID) getArguments().getSerializable(ARG_APPOINTMENT_ID);
        appointment = CalendarLab.get(getActivity()).getAppointment(appointmentId);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_appointment, container, false);

        notesEditText = v.findViewById(R.id.notes_edit_text);
        notesEditText.setText(appointment.getNotes());
        notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                appointment.setNotes(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dateButton = v.findViewById(R.id.appointment_date_button);
        dateButton.setText(appointment.getDate().toString());
        dateButton.setEnabled(false);

        return v;
    }
}

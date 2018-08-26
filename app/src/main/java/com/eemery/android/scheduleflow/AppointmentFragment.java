package com.eemery.android.scheduleflow;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.fragment.app.Fragment;

public class AppointmentFragment extends Fragment {

    private Appointment appointment;
    private TextInputEditText notesEditText;
    private MaterialButton dateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appointment = new Appointment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_appointment, container, false);

        notesEditText = v.findViewById(R.id.notes_edit_text);
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

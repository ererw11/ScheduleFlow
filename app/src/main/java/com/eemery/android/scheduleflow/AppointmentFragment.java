package com.eemery.android.scheduleflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AppointmentFragment extends Fragment {

    private static final String TAG = AppointmentFragment.class.getSimpleName();

    private static final String ARG_APPOINTMENT_ID = "appointment_id";
    private static final String DIALOG_DATE = "dialog_date";

    private static final int REQUEST_DATE = 0;

    private Appointment appointment;
    private TextInputEditText notesEditText;
    private MaterialButton dateButton;
    private MaterialButton submitButton;

    private UUID appointmentId;

    private FirebaseFirestore firebaseFirestore;
    private DocumentReference appointmentDocRef;

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
        appointmentId = (UUID) getArguments().getSerializable(ARG_APPOINTMENT_ID);
        appointment = CalendarLab.get(getActivity()).getAppointment(appointmentId);

        firebaseFirestore = FirebaseFirestore.getInstance();
        appointmentDocRef = firebaseFirestore
                .collection("appointments").document(appointmentId.toString());
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
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(appointment.getDate());
                dialog.setTargetFragment(AppointmentFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(appointmentDocRef);
                String appointmentWith = snapshot.getString("appointmentWith");
                Date appointmentDate = snapshot.getDate("date");
                String appointmentNotes = snapshot.getString("notes");
                String appointmentUserName = snapshot.getString("userName");

                // Check if the data is not empty, if not fill out the fields
                if (TextUtils.isEmpty(appointmentWith)) {
                    Log.i(TAG, "No appointmentWith");
                } else {
                    // Enter appointment with field
                }

                if (TextUtils.isEmpty(appointmentDate.toString())) {
                    Log.i(TAG, "No appointmentDate");
                } else {
                    dateButton.setText(appointmentDate.toString());
                }

                if (TextUtils.isEmpty(appointmentNotes)) {
                    Log.i(TAG, "No appointmentNotes");
                } else {
                    notesEditText.setText(appointmentNotes);
                }

                if (TextUtils.isEmpty(appointmentUserName)) {
                    Log.i(TAG, "No appointmentUserName");
                } else {
                    // Enter username field
                }

                Log.i(TAG, appointmentId.toString());
                Log.i(TAG, appointmentWith);
                Log.i(TAG, appointmentDate.toString());
                Log.i(TAG, appointmentNotes);
                Log.i(TAG, appointmentUserName);

                return null;
            }
        })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Transaction success!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });

        submitButton = v.findViewById(R.id.submit_appointment);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> appointmentToDb = new HashMap<>();
                appointmentToDb.put("userName", appointment.getUserName());
                appointmentToDb.put("appointmentWith", appointment.getAppointmentWith());
                appointmentToDb.put("date", appointment.getDate());
                appointmentToDb.put("notes", appointment.getNotes());

                firebaseFirestore.collection("appointments")
                        .document(appointment.getId().toString())
                        .set(appointmentToDb, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            appointment.setDate(date);
            dateButton.setText(appointment.getDate().toString());
        }
    }
}

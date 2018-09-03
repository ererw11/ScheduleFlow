package com.eemery.android.scheduleflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AppointmentFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = AppointmentFragment.class.getSimpleName();

    private static final String ARG_APPOINTMENT_ID = "appointment_id";
    private static final String DIALOG_DATE = "dialog_date";

    private static final int REQUEST_DATE = 0;

    private Appointment appointment;
    private TextInputEditText notesEditText;
    private MaterialButton dateButton;
    private MaterialButton submitButton;
    private Spinner appointmentSpinner;
    private TextView appointmentStylistTextView;

    private UUID appointmentId;

    private List<String> stylistList;

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
        setHasOptionsMenu(true);
        appointmentId = (UUID) getArguments().getSerializable(ARG_APPOINTMENT_ID);
        appointment = CalendarLab.get(getActivity()).getAppointment(appointmentId);

        firebaseFirestore = FirebaseFirestore.getInstance();
        appointmentDocRef = firebaseFirestore
                .collection("appointments").document(appointmentId.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_appointment, container, false);

        appointmentStylistTextView = v.findViewById(R.id.stylist_text_view);

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
        updateDate();
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

        // Set up the Stylist Spinner
        appointmentSpinner = v.findViewById(R.id.stylist_spinner);
        firebaseFirestore.collection("stylists")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            stylistList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String stylist = document.getString("name");
                                Log.d(TAG, stylist);
                                if (stylist != null) {
                                    stylistList.add(stylist);
                                }
                            }

                            ArrayAdapter<String> stylistAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, stylistList);
                            stylistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            appointmentSpinner.setAdapter(stylistAdapter);
                        }
                    }
                });
        appointmentSpinner.setOnItemSelectedListener(this);

        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(appointmentDocRef);
                String stylist = snapshot.getString("stylist");
                Date appointmentDate = snapshot.getDate("date");
                String appointmentNotes = snapshot.getString("notes");
                String appointmentUserName = snapshot.getString("userName");

                // Check if the data is not empty, if not fill out the fields

                if (TextUtils.isEmpty(appointmentDate.toString())) {
                    Log.i(TAG, "No appointmentDate");
                } else {
                    dateButton.setText(appointmentDate.toString());
                }

                if (TextUtils.isEmpty(stylist)) {
                    Log.i(TAG, "No stylist selected");
                    appointmentSpinner.setVisibility(View.VISIBLE);
                    appointmentStylistTextView.setVisibility(View.GONE);

                } else {
                    // Hide the spinner since the stylist is already chosen
                    appointmentSpinner.setVisibility(View.GONE);
                    appointmentStylistTextView.setVisibility(View.VISIBLE);
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
                Log.i(TAG, stylist);
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
                appointmentToDb.put("stylist", appointment.getStylist());
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
                getActivity().onBackPressed();
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
            updateDate();
        }
    }

    private void updateDate() {
        dateButton.setText(Utils.formatDateWithTime(appointment.getDate()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.appointment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_appointment:
                addEventToCalendar(appointment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addEventToCalendar(Appointment appointment) {
        Intent eventIntent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "Appointment with " + appointment.getStylist())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Some Fake Place")
                .putExtra(CalendarContract.Events.DESCRIPTION, appointment.getNotes())
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, appointment.getDate())
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        startActivity(eventIntent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String stylist = adapterView.getItemAtPosition(i).toString();
        // Confirm that the "Choose a Stylist" is not selected and enable and disable the
        // submit button accordingly
        if (!stylist.equalsIgnoreCase("--Choose a Stylist--")){
            appointment.setStylist(stylist);
            submitButton.setEnabled(true);
        } else {
            submitButton.setEnabled(false);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
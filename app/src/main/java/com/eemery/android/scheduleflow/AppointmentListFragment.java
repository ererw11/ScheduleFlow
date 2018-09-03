package com.eemery.android.scheduleflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("FieldCanBeLocal")
public class AppointmentListFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG = AppointmentListFragment.class.getSimpleName();

    private RecyclerView appointmentRecyclerView;
    private FloatingActionButton addAppointmentFab;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;

    public AppointmentListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Utils.confirmSignedIn(getActivity(), user);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_appointment_list, container, false);

        appointmentRecyclerView = v.findViewById(R.id.appointment_recycler_view);
        appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(appointmentRecyclerView);

        addAppointmentFab = v.findViewById(R.id.add_appointment_fab);
        addAppointmentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Appointment appointment = new Appointment();
                CalendarLab.get(getActivity()).addAppointment(appointment);
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
        firebaseFirestore.collection("appointments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            CalendarLab.get(getActivity()).deleteAppointmentList();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // Get Appointment ID and convert back to UUID
                                String idString = document.getId();
                                UUID stringIdToUUID = UUID.fromString(idString);

                                // Get Appointment Date
                                Date appointmentDate = document.getDate("date");

                                // Get stylist
                                String stylist = document.getString("stylist");

                                // Get Appointment User
                                String appointmentUser = document.getString("userName");

                                // Get Appointment Notes
                                String appointmentNotes = document.getString("notes");

                                // Create a new Appointment and add all the details
                                Appointment appointmentForList = new Appointment();

                                appointmentForList.setId(stringIdToUUID);
                                appointmentForList.setDate(appointmentDate);
                                appointmentForList.setStylist(stylist);
                                appointmentForList.setUserName(appointmentUser);
                                appointmentForList.setNotes(appointmentNotes);

                                CalendarLab.get(getActivity()).addAppointment(appointmentForList);

                                updateUI();
                            }

                        } else {
                            // Data was not pulled from Db
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void updateUI() {
        // Add the Appointments to the RecyclerView
        CalendarLab calendarLab = CalendarLab.get(getActivity());
        List<Appointment> appointments = calendarLab.getAppointmentList();
        appointmentList = appointments;

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.appointment_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                acquireAppointmentsFromDb();
                updateUI();
                return true;
            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(SignInActivity.createIntent(getContext()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AppointmentHolder) {

            // Get the id of the deleted Appointment
            final UUID deletedAppointmentId = appointmentList.get(viewHolder.getAdapterPosition()).getId();

            // Grab the name of the stylist and the date to show on Snackbar
            final String deletedStylist = appointmentList.get(viewHolder.getAdapterPosition()).getStylist();
            final Date deletedDate = appointmentList.get(viewHolder.getAdapterPosition()).getDate();

            // Remove the Appointment from RecyclerView
            adapter.removeAppointment(viewHolder.getAdapterPosition());

            // Remove the Appointment from the database
            firebaseFirestore.collection("appointments").document(deletedAppointmentId.toString())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            // Snackbar confirmation
                            final Snackbar snackbar = Snackbar.make(getView(), "Deleted appointment with " + deletedStylist +
                                    " on " + Utils.formatDateWithTime(deletedDate), Snackbar.LENGTH_LONG);
                            snackbar.setAction("DISMISS", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                            snackbar.show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        }
    }

    public class AppointmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RelativeLayout viewBackground, viewForeground;
        private TextView appointmentWithTextView;
        private TextView appointmentDateTextView;
        private Appointment appointment;


        public AppointmentHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_appointment, parent, false));
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            appointmentWithTextView = itemView.findViewById(R.id.appointment_with_name);
            appointmentDateTextView = itemView.findViewById(R.id.appointment_date);
            itemView.setOnClickListener(this);
        }

        public void bind(Appointment appointment) {
            this.appointment = appointment;
            appointmentWithTextView.setText(appointment.getStylist());
            appointmentDateTextView.setText(Utils.formatDateWithTime(appointment.getDate()));
        }

        @Override
        public void onClick(View view) {
            Intent intent = AppointmentPagerActivity.newIntent(getActivity(), appointment.getId());
            startActivity(intent);
        }
    }

    public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentHolder> {

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

        public void removeAppointment(int position) {
            appointments.remove(position);
            notifyItemRemoved(position);
        }
    }
}

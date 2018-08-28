package com.eemery.android.scheduleflow;

import java.util.Date;
import java.util.UUID;

public class Appointment {

    private UUID id;
    private String userName;
    private String appointmentWith;
    private Date date;
    private String notes;

    public Appointment() {
        this.id = UUID.randomUUID();
        this.userName = "User's Name";
        this.appointmentWith = "Person Name";
        this.date = new Date();
    }

    public UUID getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAppointmentWith() {
        return appointmentWith;
    }

    public void setAppointmentWith(String appointmentWith) {
        this.appointmentWith = appointmentWith;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

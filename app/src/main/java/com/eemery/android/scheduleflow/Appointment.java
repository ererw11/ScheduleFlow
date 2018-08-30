package com.eemery.android.scheduleflow;

import java.util.Date;
import java.util.UUID;

public class Appointment {

    private UUID id;
    private String userName;
    private String stylist;
    private Date date;
    private String notes;

    public Appointment() {
        this.id = UUID.randomUUID();
        this.userName = "User's Name";
        this.date = new Date();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStylist() {
        return stylist;
    }

    public void setStylist(String stylist) {
        this.stylist = stylist;
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

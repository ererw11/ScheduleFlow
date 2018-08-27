package com.eemery.android.scheduleflow;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CalendarLab {

    private static CalendarLab calendarLab;

    private List<Appointment> appointmentList;

    public static CalendarLab get(Context context) {
        if (calendarLab == null) {
            calendarLab = new CalendarLab(context);
        }
        return calendarLab;
    }

    private CalendarLab(Context context) {
        appointmentList = new ArrayList<>();
    }

    public void addApointment(Appointment appointment) {
        appointmentList.add(appointment);
    }

    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public Appointment getAppointment(UUID id) {
        for (Appointment appointment : appointmentList) {
            if (appointment.getId().equals(id)) {
                return appointment;
            }
        }
        return null;
    }


}

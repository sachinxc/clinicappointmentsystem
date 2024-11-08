package models;

import java.time.LocalDate;

public class Appointment {
    private int id; // appointment ID
    private Patient patient;
    private LocalDate appointmentDate;
    private String appointmentTime;
    private Doctor doctor;

    // Constructor
    public Appointment(Patient patient, LocalDate appointmentDate, String appointmentTime, Doctor doctor) {
        this.patient = patient;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.doctor = doctor;
    }

    // Getters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    // Setters
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}

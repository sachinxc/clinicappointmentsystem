package services;

import database.DatabaseHandler;
import models.Appointment;
import models.Doctor;
import models.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    private DatabaseHandler dbHandler;

    public AppointmentService() {
        dbHandler = new DatabaseHandler();
    }

    // Insert a new appointment
    public void bookAppointment(Appointment appointment) {
        dbHandler.insertAppointment(appointment);
    }

    // Delete an appointment by ID
    public void deleteAppointmentById(int id){
        dbHandler.deleteAppointment(id);
    }

    // Fetch appointments by a specific date
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return dbHandler.getAppointmentsByDate(date);
    }

    // method to find appointments by typing the patient's name
    public List<Appointment> findAppointmentsByPatientName(String name) {
        return dbHandler.findAppointmentsByPatientName(name);
    }

    // Find appointment by ID
    public Appointment findAppointmentById(int id) {
        return dbHandler.getAppointmentById(id);
    }

    // Update appointment
    public void updateAppointment(Appointment appointment, String updatedName, String updatedNic, String updatedEmail, String updatedContact,
                                  LocalDate updatedDate, String updatedTimeSlot, Doctor updatedDoctor) {
        // Update patient information through the Patient object
        Patient patient = appointment.getPatient();
        patient.setName(updatedName);
        patient.setNic(updatedNic);
        patient.setEmail(updatedEmail);
        patient.setContactNumber(updatedContact);

        // Update appointment details
        appointment.setAppointmentDate(updatedDate);
        appointment.setAppointmentTime(updatedTimeSlot);
        appointment.setDoctor(updatedDoctor);

        // Update it in the database
        dbHandler.updateAppointment(appointment);
    }

    // Check if the given date is a clinic day eg:Tuesday & Sunday is not a clinic day
    public boolean isClinicDay(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        // Monday (1), Wednesday (3), Friday (5), Saturday (6) are clinic days
        return dayOfWeek == 1 || dayOfWeek == 3 || dayOfWeek == 5 || dayOfWeek == 6;
    }

    // For booking (without current appointment)
    public boolean isAppointmentConflict(LocalDate date, String timeSlot, Doctor doctor) {
        List<Appointment> appointmentsOnDate = dbHandler.getAppointmentsByDate(date);
        for (Appointment appointment : appointmentsOnDate) {
            if (appointment.getDoctor().equals(doctor) && appointment.getAppointmentTime().equals(timeSlot)) {
                return true; // Conflict exists
            }
        }
        return false; // If there is No conflict
    }

    // For updating (if there is already an appointment)
    public boolean isAppointmentConflict(Appointment currentAppointment, LocalDate date, String timeSlot, Doctor doctor) {
        List<Appointment> appointmentsOnDate = dbHandler.getAppointmentsByDate(date);
        for (Appointment appointment : appointmentsOnDate) {
            if (appointment.getId() == currentAppointment.getId()) {
                continue; // Skip current appointment
            }
            if (appointment.getDoctor().equals(doctor) && appointment.getAppointmentTime().equals(timeSlot)) {
                return true; // Conflict exists
            }
        }
        return false; // If there is No conflict
    }

    // Get available time slots for a selected date, skipping past slots if it's today
    public List<String> getAvailableTimeSlots(LocalDate selectedDate) {
        List<String> timeSlots = new ArrayList<>();

        LocalTime now = LocalTime.now();
        boolean isToday = LocalDate.now().equals(selectedDate);

        if (selectedDate != null) {
            switch (selectedDate.getDayOfWeek().getValue()) {
                case 1: // Monday
                    generateTimeSlots(timeSlots, 10, 12, isToday, now); // 10:00 AM - 1:00 PM
                    break;
                case 3: // Wednesday
                    generateTimeSlots(timeSlots, 14, 16, isToday, now); // 2:00 PM - 5:00 PM
                    break;
                case 5: // Friday
                    generateTimeSlots(timeSlots, 16, 19, isToday, now); // 4:00 PM - 8:00 PM
                    break;
                case 6: // Saturday
                    generateTimeSlots(timeSlots, 9, 12, isToday, now); // 9:00 AM - 1:00 PM
                    break;
                default:
                    // Not a clinic day
                    break;
            }
        }

        return timeSlots;
    }

    // Generates session time slots between given start and end hours, skipping past ones if it's today
    private void generateTimeSlots(List<String> timeSlots, int startHour, int endHour, boolean isToday, LocalTime now) {
        for (int hour = startHour; hour <= endHour; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                LocalTime slotTime = LocalTime.of(hour, minute);

                if (isToday && slotTime.isBefore(now)) {
                    continue; // Skips the time slots already passed if it's today
                }

                int nextMinute = (minute + 15) % 60;
                int nextHour = hour + (minute + 15) / 60;

                String startTime = formatTime(hour, minute);
                String endTime = formatTime(nextHour, nextMinute);

                String timeRange = startTime + " - " + endTime;
                timeSlots.add(timeRange);
            }
        }
    }

    // Put the time inta a format which is readable like 10:00am or 02:00pm
    private String formatTime(int hour, int minute) {
        String period = (hour >= 12) ? "pm" : "am";
        int hour12 = (hour == 0) ? 12 : (hour > 12) ? hour - 12 : hour;
        return String.format("%02d:%02d%s", hour12, minute, period);
    }

}


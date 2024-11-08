package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Appointment;
import models.Patient;
import models.Doctor;
import services.AppointmentService;

import java.time.LocalDate;
import java.util.List;

public class ViewAppointmentScreenController {
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextArea appointmentsArea;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;

    private AppointmentService appointmentService;

    public ViewAppointmentScreenController() {
        appointmentService = new AppointmentService();
    }

    @FXML
    public void initialize() {
        searchButton.setOnAction(e -> searchAppointment());
        validateSearchField();
    }

    // Method to limit searchField input to 50 characters
    private void validateSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                // Limit the text to 50 characters
                searchField.setText(newValue.substring(0, 50));
            }
        });
    }

    //Displays appointments for a selected date in the appointmentsArea.
    @FXML
    public void viewAppointments() {
        LocalDate date = datePicker.getValue();
        if (date != null) {
            List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
            appointmentsArea.clear();
            if (appointments.isEmpty()) {
                appointmentsArea.setText("No appointments available for the selected date.");
            } else {
                // Build a formatted string of appointment details
                StringBuilder sb = new StringBuilder();
                for (Appointment appointment : appointments) {
                    Patient patient = appointment.getPatient();
                    Doctor doctor = appointment.getDoctor();

                    sb.append("ID: ").append(appointment.getId()).append("\n")
                            .append("Patient Name: ").append(patient.getName()).append("\n")
                            .append("NIC: ").append(patient.getNic()).append("\n")
                            .append("Contact: ").append(patient.getContactNumber()).append("\n")
                            .append("Email: ").append(patient.getEmail()).append("\n")
                            .append("Date: ").append(appointment.getAppointmentDate()).append("\n")
                            .append("Time: ").append(appointment.getAppointmentTime()).append("\n")
                            .append("Doctor: ").append(doctor.getName()).append("\n\n");
                }
                appointmentsArea.setText(sb.toString());
            }
        } else {
            showAlert("Please select a date.");
        }
    }

    //Searches for appointments by appointment ID or patient name searches.
    @FXML
    public void searchAppointment() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            try {
                int appointmentId = Integer.parseInt(query);
                Appointment appointment = appointmentService.findAppointmentById(appointmentId);
                displayAppointment(appointment);
            } catch (NumberFormatException e) {
                List<Appointment> appointments = appointmentService.findAppointmentsByPatientName(query);
                displayAppointments(appointments);
            }
        } else {
            showAlert("Please enter an appointment ID or patient name.");
        }
    }

    //Displays details of a single appointment.
    private void displayAppointment(Appointment appointment) {
        if (appointment != null) {
            // Format and display appointment details
            StringBuilder sb = new StringBuilder();
            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();

            sb.append("ID: ").append(appointment.getId()).append("\n")
                    .append("Patient Name: ").append(patient.getName()).append("\n")
                    .append("NIC: ").append(patient.getNic()).append("\n")
                    .append("Contact: ").append(patient.getContactNumber()).append("\n")
                    .append("Email: ").append(patient.getEmail()).append("\n")
                    .append("Date: ").append(appointment.getAppointmentDate()).append("\n")
                    .append("Time: ").append(appointment.getAppointmentTime()).append("\n")
                    .append("Doctor: ").append(doctor.getName()).append("\n\n");

            appointmentsArea.setText(sb.toString());
        } else {
            appointmentsArea.setText("No appointment found with the given ID.");
        }
    }

    //Displays a list of appointments based on patient name search.
    private void displayAppointments(List<Appointment> appointments) {
        appointmentsArea.clear();
        if (appointments.isEmpty()) {
            appointmentsArea.setText("No appointments found for the given patient name.");
        } else {
            // Build a formatted string for multiple appointments
            StringBuilder sb = new StringBuilder();
            for (Appointment appointment : appointments) {
                Patient patient = appointment.getPatient();
                Doctor doctor = appointment.getDoctor();

                sb.append("ID: ").append(appointment.getId()).append("\n")
                        .append("Patient Name: ").append(patient.getName()).append("\n")
                        .append("NIC: ").append(patient.getNic()).append("\n")
                        .append("Contact: ").append(patient.getContactNumber()).append("\n")
                        .append("Email: ").append(patient.getEmail()).append("\n")
                        .append("Date: ").append(appointment.getAppointmentDate()).append("\n")
                        .append("Time: ").append(appointment.getAppointmentTime()).append("\n")
                        .append("Doctor: ").append(doctor.getName()).append("\n\n");
            }
            appointmentsArea.setText(sb.toString());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}

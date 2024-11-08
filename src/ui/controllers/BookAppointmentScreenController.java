package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Appointment;
import models.Patient;
import models.Doctor;
import services.AppointmentService;
import services.ValidationService;

import java.time.LocalDate;
import java.util.List;

public class BookAppointmentScreenController {
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Doctor> doctorComboBox;
    @FXML
    private ComboBox<String> timeSlotComboBox;
    @FXML
    private TextField nameField;
    @FXML
    private TextField nicField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField emailField;
    @FXML
    private CheckBox registrationFeeCheckBox;
    @FXML
    private Button bookButton;

    private AppointmentService appointmentService;
    private ValidationService validationService;

    private boolean isResetting = false; // Flag to manage resetting the form without triggering listeners

    // initializing the services.
    public BookAppointmentScreenController() {
        appointmentService = new AppointmentService();
        validationService = new ValidationService();
    }

    @FXML
    public void initialize() {
        // Restrict DatePicker to only allow future clinic days (Mondays, Wednesdays, Fridays, Saturdays)
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Disable past dates and non-clinic days
                if (empty || date.isBefore(LocalDate.now()) || !appointmentService.isClinicDay(date)) {
                    setDisable(true);
                } else {
                    // Highlight clinic days in green (you can define a CSS class like this in your styles)
                    getStyleClass().add("clinic-day");
                }
            }
        });

        // Set event listeners to update available time slots based on user inputs
        timeSlotComboBox.setOnShowing(e -> updateAvailableTimeSlots());
        datePicker.setOnAction(e -> updateAvailableTimeSlots());
        doctorComboBox.setOnAction(e -> updateAvailableTimeSlots());
        timeSlotComboBox.setOnAction(e -> checkForAppointmentConflict());
        bookButton.setOnAction(e -> bookAppointment());

        // Load doctors into ComboBox from the predefined list
        List<Doctor> predefinedDoctors = Doctor.getDoctors();
        doctorComboBox.getItems().addAll(predefinedDoctors);

        addInputRestrictions();  // Add character length restrictions
    }

    // Restricts the input based on character limits when user is typing on the form
    private void addInputRestrictions() {
        // Name field: max 50 characters
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                nameField.setText(newValue.substring(0, 50));
            }
        });

        // NIC field: max 15 characters and must be alphanumeric
        nicField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Remove non-alphanumeric characters
            String filteredValue = newValue.replaceAll("[^a-zA-Z0-9]", "");
            if (filteredValue.length() > 15) {
                filteredValue = filteredValue.substring(0, 15);
            }
            nicField.setText(filteredValue);
        });

        // Contact field: only digits/numbers and max 10 digits
        contactField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Remove non-digit characters
            String filteredValue = newValue.replaceAll("[^\\d]", "");

            // Limit to 10 digits
            if (filteredValue.length() > 10) {
                filteredValue = filteredValue.substring(0, 10);
            }

            // Set the filtered value back to the contact field
            contactField.setText(filteredValue);
        });

        // Email field: max 100 characters
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                emailField.setText(newValue.substring(0, 100));
            }
        });
    }

    // Update available time slots based on selected date and doctor availability.
    private void updateAvailableTimeSlots() {
        if (isResetting) return;  // Skip if resetting
        LocalDate date = datePicker.getValue();
        timeSlotComboBox.getItems().clear();

        if (date != null) {
            if (appointmentService.isClinicDay(date)) {
                doctorComboBox.setDisable(false);
                timeSlotComboBox.setDisable(false);
                nameField.setDisable(false);
                nicField.setDisable(false);
                contactField.setDisable(false);
                emailField.setDisable(false);
                registrationFeeCheckBox.setDisable(false);
                bookButton.setDisable(false);
                List<String> availableSlots = appointmentService.getAvailableTimeSlots(date);
                if (availableSlots.isEmpty()) {
                    // Give an alert if there are no available slots and restrict booking
                    showAlert("No time slots available!");
                    disableAllFields();
                } else {
                    timeSlotComboBox.getItems().addAll(availableSlots);
                }
            } else {
                disableAllFields();
                timeSlotComboBox.getItems().add("Not a clinic day.");
                showAlert("The selected date is not a clinic day. Please select a different date.");
            }
        } else {
            showAlert("Please Select a date first to continue");
        }
    }

    // Disable all form fields to prevent further input
    private void disableAllFields() {
        doctorComboBox.setDisable(true);
        timeSlotComboBox.setDisable(true);
        nameField.setDisable(true);
        nicField.setDisable(true);
        contactField.setDisable(true);
        emailField.setDisable(true);
        registrationFeeCheckBox.setDisable(true);
        bookButton.setDisable(true);
    }

    // Check for appointment conflicts based on selected date, doctor, and time slot
    private void checkForAppointmentConflict() {
        LocalDate date = datePicker.getValue();
        Doctor selectedDoctor = doctorComboBox.getValue();
        String timeSlot = timeSlotComboBox.getValue();

        if (date != null && selectedDoctor != null && timeSlot != null) {
            if (appointmentService.isAppointmentConflict(date, timeSlot, selectedDoctor)) {
                // If there is a conflict, disable all fields except datePicker and timeSlotComboBox
                disableAllFieldsExceptDateAndTime();
                showAlert("This time slot is already booked. Please select a different time.");
            } else {
                // If there is no conflict, enable all fields
                enableAllFields();
            }
        }
    }

    // Disable all fields except DatePicker and TimeSlot ComboBox for conflict handling
    private void disableAllFieldsExceptDateAndTime() {
        nameField.setDisable(true);
        nicField.setDisable(true);
        contactField.setDisable(true);
        emailField.setDisable(true);
        registrationFeeCheckBox.setDisable(true);
        bookButton.setDisable(true);
    }

    // Enable all form fields to allow input
    private void enableAllFields() {
        nameField.setDisable(false);
        nicField.setDisable(false);
        contactField.setDisable(false);
        emailField.setDisable(false);
        registrationFeeCheckBox.setDisable(false);
        bookButton.setDisable(false);
    }

    // Reset the form to its initial state
    private void resetForm() {
        isResetting = true;
        // Clear all fields and slots
        nameField.clear();
        nicField.clear();
        emailField.clear();
        contactField.clear();
        datePicker.setValue(null);
        doctorComboBox.setValue(null);
        timeSlotComboBox.getItems().clear();
        registrationFeeCheckBox.setSelected(false);
        isResetting = false;
    }

    // book an appointment with input validation
    private void bookAppointment() {
        LocalDate date = datePicker.getValue();
        Doctor selectedDoctor = doctorComboBox.getValue();
        String timeSlot = timeSlotComboBox.getValue();
        String name = nameField.getText();
        String nic = nicField.getText();
        String contact = contactField.getText();
        String email = emailField.getText();
        boolean isRegistrationFeePaid = registrationFeeCheckBox.isSelected();

        // Validation checks
        if (date == null || selectedDoctor == null || timeSlot == null || name.isEmpty() || nic.isEmpty() || contact.isEmpty() || email.isEmpty() || !isRegistrationFeePaid) {
            showAlert(!isRegistrationFeePaid ? "Registration fee must be paid to proceed with booking." : "Please fill in all fields.");
            return;
        }

        if (!appointmentService.isClinicDay(date)) {
            showAlert("Selected date is not a clinic day. Please choose another date.");
            return;
        }

        if (!validationService.isValidName(name)) {
            showAlert("Name must be between 2 and 50 characters.");
            return;
        }

        if (!validationService.isValidNIC(nic)) {
            showAlert("NIC number must be between 9 and 15 Alphanumeric characters.");
            return;
        }

        if (!validationService.isValidPhoneNumber(contact)) {
            showAlert("Contact number must be exactly 10 digits.");
            return;
        }

        if (!validationService.isValidEmail(email)) {
            showAlert("Please enter a valid email address.");
            return;
        }

        Patient patient = new Patient(name, nic, contact, email);

        // Confirm again if conflict check was missed
        if (appointmentService.isAppointmentConflict(date, timeSlot, selectedDoctor)) {
            showAlert("This time slot is already booked. Please select a different time.");
            return;
        }

        Appointment appointment = new Appointment(patient, date, timeSlot, selectedDoctor);
        appointmentService.bookAppointment(appointment); //create an appointment if all validation checks are passed

        showAlert("Appointment booked successfully!");
        resetForm();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}

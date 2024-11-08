package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Appointment;
import models.Doctor;
import models.Patient;
import services.AppointmentService;
import services.ValidationService;

import java.time.LocalDate;
import java.util.List;

public class AppointmentManagerScreenController {
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField nicField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField contactField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> timeSlotComboBox;
    @FXML
    private ComboBox<Doctor> doctorComboBox;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Label timeSlotMessageLabel;

    private Appointment currentAppointment;
    private AppointmentService appointmentService = new AppointmentService();
    private ValidationService validationService = new ValidationService();

    @FXML
    public void initialize() {
        disableFieldsOnLoad(true);  // Disable all buttons and fields except id field and load button

        // listing out the doctors
        List<Doctor> predefinedDoctors = Doctor.getDoctors();
        doctorComboBox.getItems().addAll(predefinedDoctors);

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

        timeSlotComboBox.setOnShowing(e -> updateTimeSlots(datePicker.getValue()));

        datePicker.setOnAction(e -> updateTimeSlots(datePicker.getValue()));
        doctorComboBox.setOnAction(e -> updateTimeSlots(datePicker.getValue()));
        timeSlotComboBox.setOnAction(e -> checkForAppointmentConflict()); // Add conflict check for selected time slot

        validateIdField(); // check if user enters a valid appointment id
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

    // Method to allow only numbers and maximum up to 10 digits when entering id
    private void validateIdField() {
        idField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                // Remove non-digit characters
                idField.setText(newValue.replaceAll("[^\\d]", ""));
            } else if (newValue.length() > 10) {
                // Limit to 10 digits
                idField.setText(newValue.substring(0, 10));
            }
        });
    }

    // Loads appointment data based on the ID entered
    @FXML
    private void loadAppointment() {
        try {
            int id = Integer.parseInt(idField.getText());
            currentAppointment = appointmentService.findAppointmentById(id);

            if (currentAppointment != null) {
                // Set patient and appointment details
                Patient patient = currentAppointment.getPatient();
                Doctor doctor = currentAppointment.getDoctor();

                nameField.setText(patient.getName());
                nicField.setText(patient.getNic());
                emailField.setText(patient.getEmail());
                contactField.setText(patient.getContactNumber());
                datePicker.setValue(currentAppointment.getAppointmentDate());
                doctorComboBox.setValue(doctor);

                // Load only available time slots
                updateTimeSlots(currentAppointment.getAppointmentDate());

                // Check if the saved time slot is in the available slots; if not, add it just temporarily
                if (!timeSlotComboBox.getItems().contains(currentAppointment.getAppointmentTime())) {
                    timeSlotComboBox.getItems().add(currentAppointment.getAppointmentTime());
                }
                timeSlotComboBox.setValue(currentAppointment.getAppointmentTime());

                disableFieldsOnLoad(false);

            } else {
                showAlert("No appointment found with ID: " + id);
                resetForm();
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid or empty appointment ID. Please enter a valid number.");
            resetForm();
        }
    }

    // Validates appointment data, checks for conflicts, and updates the appointment after confirmation.
    @FXML
    private void updateAppointment() {
        if (!validateAppointmentDetails()) return;

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to update this appointment with ID: " + currentAppointment.getId() + " ?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.setTitle("Confirm Update");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                LocalDate selectedDate = datePicker.getValue();
                String selectedTimeSlot = timeSlotComboBox.getValue();
                Doctor selectedDoctor = doctorComboBox.getValue();

                if (!appointmentService.isClinicDay(selectedDate)) {
                    showAlert("Cannot update appointment. Selected date is not a clinic day.");
                    return;
                }

                boolean conflict = appointmentService.isAppointmentConflict(currentAppointment, selectedDate, selectedTimeSlot, selectedDoctor);
                if (conflict) {
                    showAlert("The selected time slot is already booked with the doctor. Please choose another.");
                } else {
                    appointmentService.updateAppointment(currentAppointment, nameField.getText(), nicField.getText(), emailField.getText(), contactField.getText(),
                            selectedDate, selectedTimeSlot, selectedDoctor);
                    showAlert("Appointment updated successfully!");
                }
            }
        });
    }

    // Confirms and deletes the currently loaded appointment
    @FXML
    private void deleteAppointment() {
        if (currentAppointment == null) {
            showAlert("No appointment is currently loaded for deletion.");
            return;
        }

        int id = currentAppointment.getId();

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this appointment with ID: " + id + " ?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                appointmentService.deleteAppointmentById(id);
                showAlert("Appointment with ID: " + id + " has been deleted.");
                resetForm();
            }
        });
    }

    // Updates available time slots based on the selected date and doctor.
    private void updateTimeSlots(LocalDate selectedDate) {
        timeSlotComboBox.getItems().clear();

        if ((selectedDate == null && currentAppointment != null) || (selectedDate != null && !appointmentService.isClinicDay(selectedDate))) {
            disableFieldsOnNonClinicDay(true);
            timeSlotComboBox.getItems().add("Not a clinic day.");
            showAlert("Selected date is not a clinic day. Please choose another date.");
        } else {
            disableFieldsOnNonClinicDay(false);
            List<String> timeSlots = appointmentService.getAvailableTimeSlots(selectedDate);

            if (timeSlots.isEmpty()) {
                // show a message if there are no available slots and restrict updating
                disableUpdateButtonOnConflict();
                timeSlotMessageLabel.setText("No slots available");

            } else {
                timeSlotComboBox.getItems().addAll(timeSlots);
                timeSlotMessageLabel.setText(null);
            }
        }
    }

    // Checks if the selected time slot and doctor combination is already booked.
    private void checkForAppointmentConflict() {
        String selectedTimeSlot = timeSlotComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        Doctor selectedDoctor = doctorComboBox.getValue();

        if (selectedTimeSlot != null && selectedDate != null && selectedDoctor != null) {
            boolean conflict = appointmentService.isAppointmentConflict(currentAppointment, selectedDate, selectedTimeSlot, selectedDoctor);
            if (conflict) {
                showAlert("The selected time slot is already booked with the doctor. Please choose another.");
                disableUpdateButtonOnConflict();  // Disable the update button on conflict
            } else {
                enableFields();  // Enable all fields if no conflict
            }
        }
    }

    //This disables the update button
    private void disableUpdateButtonOnConflict() {
        updateButton.setDisable(true);
    }

    private void enableFields() {
        nameField.setDisable(false);
        nicField.setDisable(false);
        emailField.setDisable(false);
        contactField.setDisable(false);
        doctorComboBox.setDisable(false);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    //This disables all fields and buttons except the id field and Load button
    private void disableFieldsOnLoad(boolean disable) {
        nameField.setDisable(disable);
        nicField.setDisable(disable);
        emailField.setDisable(disable);
        contactField.setDisable(disable);
        doctorComboBox.setDisable(disable);
        timeSlotComboBox.setDisable(disable);
        datePicker.setDisable(disable);
        updateButton.setDisable(disable);
        deleteButton.setDisable(disable);
    }

    //This disables the doctor, timeslot & update button in case of non clinic date selected
    private void disableFieldsOnNonClinicDay(boolean disable) {
        doctorComboBox.setDisable(disable);
        timeSlotComboBox.setDisable(disable);
        updateButton.setDisable(disable);
        timeSlotMessageLabel.setText(null);
    }

    //resets the form to its initial state for another appointment to be loaded
    private void resetForm() {
        // Set to null to indicate no active appointment
        currentAppointment = null;
        // Clear all fields and slots
        nameField.clear();
        nicField.clear();
        emailField.clear();
        contactField.clear();
        datePicker.setValue(null);
        doctorComboBox.setValue(null);
        timeSlotComboBox.getItems().clear();
        timeSlotMessageLabel.setText(null);
        // Disable fields and buttons after resetting
        disableFieldsOnLoad(true);
    }

    //check if the user entered data is valid and if not show the respective alerts indicating the mistakes
    private boolean validateAppointmentDetails() {
        if (datePicker.getValue() == null || doctorComboBox.getValue() == null || timeSlotComboBox.getValue() == null ||
                nameField.getText().isEmpty() || nicField.getText().isEmpty() || contactField.getText().isEmpty() || emailField.getText().isEmpty()) {
            showAlert("Please fill in all fields.");
            return false;
        }

        if (validationService.isValidName(nameField.getText())) {
            // Prints a passed statement to indicate successful validation
            System.out.println("Name Validation Passed");
        } else {
            showAlert("Name must be between 2 and 50 characters.");
            return false;
        }

        if (validationService.isValidNIC(nicField.getText())) {
            // Prints a passed statement to indicate successful validation
            System.out.println("NIC Validation Passed");
        } else {
            showAlert("NIC number must be between 9 and 15 Alphanumeric characters.");
            return false;
        }

        if (validationService.isValidPhoneNumber(contactField.getText())) {
            // Prints a passed statement to indicate successful validation
            System.out.println("Phone Number Validation Passed");
        } else {
            showAlert("Contact number must be exactly 10 digits.");
            return false;
        }

        if (validationService.isValidEmail(emailField.getText())) {
            // Prints a passed statement to indicate successful validation
            System.out.println("Email Validation Passed");
        } else {
            showAlert("Please enter a valid email address.");
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.show();
    }
}
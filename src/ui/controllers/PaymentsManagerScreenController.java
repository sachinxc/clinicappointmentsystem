package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Appointment;
import models.Treatment;
import models.Invoice;
import services.PaymentsService;
import services.InvoiceService;
import services.AppointmentService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentsManagerScreenController {

    @FXML
    private CheckBox acneTreatmentCheckBox;
    @FXML
    private CheckBox skinWhiteningCheckBox;
    @FXML
    private CheckBox moleRemovalCheckBox;
    @FXML
    private CheckBox laserTreatmentCheckBox;
    @FXML
    private TextField appointmentIdField;
    @FXML
    private Button calculateButton;
    @FXML
    private Button downloadPdfButton;
    @FXML
    private TextArea invoiceArea;

    private final PaymentsService paymentsService;
    private final InvoiceService invoiceService;
    private final AppointmentService appointmentService;

    public PaymentsManagerScreenController() {
        appointmentService = new AppointmentService();
        paymentsService = new PaymentsService();
        invoiceService = new InvoiceService();
    }

    @FXML
    public void initialize() {
        calculateButton.setOnAction(e -> calculateTotalAndGenerateInvoice());
        downloadPdfButton.setOnAction(e -> downloadPdfInvoice());
        validateAppointmentIdField();
    }

    // Validates the appointmentIdField to only allow numeric input and limit length to 10 digits.
    private void validateAppointmentIdField() {
        appointmentIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                // Remove non-digit characters
                appointmentIdField.setText(newValue.replaceAll("[^\\d]", ""));
            } else if (newValue.length() > 10) {
                // Limit to 10 digits
                appointmentIdField.setText(newValue.substring(0, 10));
            }
        });
    }

    //Calculates the total amount for selected treatments, generates an invoice, and displays it.
    private void calculateTotalAndGenerateInvoice() {
        Optional<Appointment> appointment = getAppointment();
        if (appointment.isEmpty()) {
            return;
        }

        List<Treatment> selectedTreatments = getSelectedTreatments();
        BigDecimal subtotal = paymentsService.calculateSubtotal(selectedTreatments);
        BigDecimal tax = paymentsService.calculateTax(subtotal);
        BigDecimal total = paymentsService.calculateTotal(selectedTreatments);

        //Generate and display the invoice
        Invoice invoice = invoiceService.generateInvoice(
                appointment.get(), selectedTreatments, subtotal, tax, total
        );

        invoiceArea.setText(invoice.formatInvoice());
    }

    //Downloads the generated invoice as a PDF. Shows an alert if no invoice is present.
    private void downloadPdfInvoice() {
        Stage currentStage = (Stage) downloadPdfButton.getScene().getWindow();
        if (invoiceArea.getText().isEmpty()) {
            showAlert("No invoice to download.");
            return;
        }

        Optional<Appointment> appointment = getAppointment();
        if (appointment.isEmpty()) {
            return;
        }

        // Get treatments and generate invoice for PDF download
        List<Treatment> selectedTreatments = getSelectedTreatments();
        BigDecimal subtotal = paymentsService.calculateSubtotal(selectedTreatments);
        BigDecimal tax = paymentsService.calculateTax(subtotal);
        BigDecimal total = paymentsService.calculateTotal(selectedTreatments);

        Invoice invoice = invoiceService.generateInvoice(appointment.get(), selectedTreatments, subtotal, tax, total);

        try {
            invoiceService.downloadInvoiceAsPDF(invoice, currentStage);
            showAlert("PDF invoice downloaded successfully!");
        } catch (Exception e) {
            showAlert("Error while downloading PDF: " + e.getMessage());
        }
    }

    // gets the Appointment object based on the ID entered in appointmentIdField.
    private Optional<Appointment> getAppointment() {
        String appointmentIdText = appointmentIdField.getText();
        if (appointmentIdText.isEmpty()) {
            showAlert("Please enter Appointment ID.");
            return Optional.empty();
        }

        try {
            int appointmentId = Integer.parseInt(appointmentIdText);
            Appointment appointment = appointmentService.findAppointmentById(appointmentId);
            if (appointment == null) {
                showAlert("No appointment found.");
                return Optional.empty();
            }
            return Optional.of(appointment);
        } catch (NumberFormatException e) {
            showAlert("Invalid Appointment ID.");
            return Optional.empty();
        }
    }

    // returns a list of selected treatments based on CheckBox inputs.
    private List<Treatment> getSelectedTreatments() {
        List<Treatment> selectedTreatments = new ArrayList<>();
        if (acneTreatmentCheckBox.isSelected()) selectedTreatments.add(new Treatment("Acne Treatment"));
        if (skinWhiteningCheckBox.isSelected()) selectedTreatments.add(new Treatment("Skin Whitening"));
        if (moleRemovalCheckBox.isSelected()) selectedTreatments.add(new Treatment("Mole Removal"));
        if (laserTreatmentCheckBox.isSelected()) selectedTreatments.add(new Treatment("Laser Treatment"));
        return selectedTreatments;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}

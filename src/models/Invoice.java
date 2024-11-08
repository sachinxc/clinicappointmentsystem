package models;

import java.math.BigDecimal;
import java.util.List;

public class Invoice {

    private final Appointment appointment;
    private final Patient patient;
    private final Doctor doctor;
    private final List<Treatment> selectedTreatments;
    private final BigDecimal subtotal;
    private final BigDecimal tax;
    private final BigDecimal totalAmount;

    public Invoice(Appointment appointment, Patient patient, Doctor doctor,
                   List<Treatment> selectedTreatments, BigDecimal subtotal, BigDecimal tax, BigDecimal totalAmount) {
        this.appointment = appointment;
        this.patient = patient;
        this.doctor = doctor;
        this.selectedTreatments = selectedTreatments;
        this.subtotal = subtotal;
        this.tax = tax;
        this.totalAmount = totalAmount;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public List<Treatment> getSelectedTreatments() {
        return selectedTreatments;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    // Format the invoice as a string
    public String formatInvoice() {
        StringBuilder treatmentDetails = new StringBuilder();

        for (Treatment treatment : selectedTreatments) {
            treatmentDetails.append(treatment.getName())
                    .append(" - ")
                    .append(treatment.getPrice())
                    .append("\n");
        }

        return String.format(
                "Aurora Skin Clinic\n" +
                        "Invoice:\n" +
                        "--------------------------------------------------\n" +
                        "Patient Name: %s\n" +
                        "Patient Email: %s\n" +
                        "Patient Contact: %s\n" +
                        "Appointment Date: %s\n" +
                        "Doctor: %s\n" +
                        "Treatments:\n%s" +
                        "Subtotal: %.2f\n" +
                        "Tax (2.5%%): %.2f\n" +
                        "Registration Fee: %.2f\n" +
                        "Total Amount: %.2f\n" +
                        "--------------------------------------------------\n" +
                        "Thank you for your payment!\n",
                patient.getName(),
                patient.getEmail(),
                patient.getContactNumber(),
                appointment.getAppointmentDate(),
                doctor.getName(),
                treatmentDetails.toString(),
                subtotal,
                tax,
                services.PaymentsService.getRegistrationFee(),
                totalAmount
        );
    }
}

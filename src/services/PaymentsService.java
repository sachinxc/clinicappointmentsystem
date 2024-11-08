package services;

import models.Treatment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PaymentsService {

    // Constant for the fixed registration fee (500.00)
    private static final BigDecimal REGISTRATION_FEE = new BigDecimal("500.00");
    // Constant for tax rate (2.5% of the subtotal)
    private static final BigDecimal TAX_RATE = new BigDecimal("0.025");

    // Getter for REGISTRATION_FEE
    public static BigDecimal getRegistrationFee() {
        return REGISTRATION_FEE;
    }

    // Calculate the total based on selected treatments, add tax, and include the registration fee
    public BigDecimal calculateTotal(List<Treatment> selectedTreatments) {
        BigDecimal subtotal = calculateSubtotal(selectedTreatments);
        BigDecimal tax = calculateTax(subtotal);
        return subtotal.add(tax).add(REGISTRATION_FEE).setScale(2, RoundingMode.HALF_UP);
    }

    // Calculate the subtotal of selected treatments
    public BigDecimal calculateSubtotal(List<Treatment> selectedTreatments) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Treatment treatment : selectedTreatments) {
            subtotal = subtotal.add(treatment.getPrice());
        }
        return subtotal;
    }

    // Calculate tax (2.5% of subtotal)
    public BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}

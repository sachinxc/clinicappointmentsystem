package models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Treatment {

    private String name;
    private BigDecimal price;

    // Map to store the fixed prices of the treatments
    private static final Map<String, BigDecimal> TREATMENT_PRICES = new HashMap<>() {{
        put("Acne Treatment", new BigDecimal("2750.00"));
        put("Skin Whitening", new BigDecimal("7650.00"));
        put("Mole Removal", new BigDecimal("3850.00"));
        put("Laser Treatment", new BigDecimal("12500.00"));
    }};

    public Treatment(String name) {
        this.name = name;
        this.price = TREATMENT_PRICES.get(name); // Assign price based on treatment name
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    // Static method to get the list of available treatments
    public static Map<String, BigDecimal> getAvailableTreatments() {
        return TREATMENT_PRICES;
    }
}

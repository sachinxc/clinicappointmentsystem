package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Doctor {
    private String name;
    private String specialization;

    // List to store all doctors
    private static List<Doctor> doctors = new ArrayList<>();

    // Constructor
    public Doctor(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    // Override toString() method to display correctly in ComboBox
    @Override
    public String toString() {
        return name + " - " + specialization;
    }

    // Override equals() and hashCode() to compare Doctor objects accurately
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return name.equals(doctor.name) && specialization.equals(doctor.specialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, specialization);
    }

    // Method to retrieve all doctors
    public static List<Doctor> getDoctors() {
        return doctors;
    }

    // Method to add more doctors (if needed)
    public static void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }

    // Static initializer to load the two predefined doctors
    static {
        doctors.add(new Doctor("Dr. Kavinda Perera", "Dermatologist"));
        doctors.add(new Doctor("Dr. Viduranga Fernando", "Dermatologist"));
    }
}

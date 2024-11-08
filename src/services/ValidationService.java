package services;

import java.util.regex.Pattern;

public class ValidationService {

    //Validates if the phone number is a 10-digit number
    public boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false; // Null check
        return phone.matches("\\d{10}");
    }

    //Validates if the email is in a proper format
    public boolean isValidEmail(String email) {
        if (email == null) return false; // Null check
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(email).matches();
    }

    //Validates if the nic length is between 9 and 15 alphanumeric characters
    public boolean isValidNIC(String nic) {
        if (nic == null) return false; // Null check
        return nic.matches("[a-zA-Z0-9]+") && nic.length() >= 9 && nic.length() <= 15;
    }

    //Validates if the name length is between 2 and 50 characters
    public boolean isValidName(String name) {
        if (name == null) return false; // Null check
        return name.length() >= 2 && name.length() <= 50;
    }
}

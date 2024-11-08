package models;

public class Patient {
    private String name;
    private String nic;
    private String contactNumber;
    private String email;

    // Constructor
    public Patient(String name, String nic, String contactNumber, String email) {
        this.name = name;
        this.nic = nic;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getNic() {
        return nic;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

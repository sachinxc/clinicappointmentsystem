package database;

import models.Appointment;
import models.Patient;
import models.Doctor;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:sqlite:appointments.db"; // SQLite database URL where the db file is saved
    private Connection connection;

    public DatabaseHandler() {
        connect();
        createTable();
    }

    // Connect to the database
    private void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Create a database with appointments table if it doesn't exist at the start of the program or if no db exist
    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS appointments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "patientName TEXT NOT NULL," +
                "nic TEXT NOT NULL," +
                "contactNumber TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "appointmentDate TEXT NOT NULL," +
                "appointmentTime TEXT NOT NULL," +
                "doctorName TEXT NOT NULL," +
                "doctorSpecialization TEXT NOT NULL" +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            System.out.println("Appointments table created or already exists.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Insert an appointment - this will save the appointment details in the database
    public void insertAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments(patientName, nic, contactNumber, email, appointmentDate, appointmentTime, doctorName, doctorSpecialization) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();

            preparedStatement.setString(1, patient.getName());
            preparedStatement.setString(2, patient.getNic());
            preparedStatement.setString(3, patient.getContactNumber());
            preparedStatement.setString(4, patient.getEmail());
            preparedStatement.setString(5, appointment.getAppointmentDate().toString());
            preparedStatement.setString(6, appointment.getAppointmentTime());
            preparedStatement.setString(7, doctor.getName());
            preparedStatement.setString(8, doctor.getSpecialization());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql;

        // Check if the date is null or not
        if (date == null) {
            sql = "SELECT * FROM appointments";  // If date is null, fetch all appointments
        } else {
            sql = "SELECT * FROM appointments WHERE appointmentDate = ?";  // Fetch appointments for the given date
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (date != null) {
                preparedStatement.setString(1, date.toString());  // Set the date parameter if it's not null
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Patient patient = new Patient(
                        resultSet.getString("patientName"),
                        resultSet.getString("nic"),
                        resultSet.getString("contactNumber"),
                        resultSet.getString("email")
                );
                Doctor doctor = new Doctor(
                        resultSet.getString("doctorName"),
                        resultSet.getString("doctorSpecialization")
                );
                Appointment appointment = new Appointment(
                        patient,
                        LocalDate.parse(resultSet.getString("appointmentDate")),
                        resultSet.getString("appointmentTime"),
                        doctor
                );
                appointment.setId(resultSet.getInt("id")); // Set ID for appointment
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return appointments;
    }

    // Method to find appointments by patient name
    public List<Appointment> findAppointmentsByPatientName(String patientName) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patientName LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + patientName + "%"); // Use LIKE for partial matches
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Patient patient = new Patient(
                        resultSet.getString("patientName"),
                        resultSet.getString("nic"),
                        resultSet.getString("contactNumber"),
                        resultSet.getString("email")
                );
                Doctor doctor = new Doctor(
                        resultSet.getString("doctorName"),
                        resultSet.getString("doctorSpecialization")
                );
                Appointment appointment = new Appointment(
                        patient,
                        LocalDate.parse(resultSet.getString("appointmentDate")),
                        resultSet.getString("appointmentTime"),
                        doctor
                );
                appointment.setId(resultSet.getInt("id")); // Set ID for appointment
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return appointments;
    }



    // This method is to get all appointments
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Patient patient = new Patient(
                        resultSet.getString("patientName"),
                        resultSet.getString("nic"),
                        resultSet.getString("contactNumber"),
                        resultSet.getString("email")
                );
                Doctor doctor = new Doctor(
                        resultSet.getString("doctorName"),
                        resultSet.getString("doctorSpecialization")
                );
                Appointment appointment = new Appointment(
                        patient,
                        LocalDate.parse(resultSet.getString("appointmentDate")),
                        resultSet.getString("appointmentTime"),
                        doctor
                );
                appointment.setId(resultSet.getInt("id")); // Set ID for appointment
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return appointments;
    }

    // Get appointment by ID
    public Appointment getAppointmentById(int id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Patient patient = new Patient(
                        resultSet.getString("patientName"),
                        resultSet.getString("nic"),
                        resultSet.getString("contactNumber"),
                        resultSet.getString("email")
                );
                Doctor doctor = new Doctor(
                        resultSet.getString("doctorName"),
                        resultSet.getString("doctorSpecialization")
                );
                Appointment appointment = new Appointment(
                        patient,
                        LocalDate.parse(resultSet.getString("appointmentDate")),
                        resultSet.getString("appointmentTime"),
                        doctor
                );
                appointment.setId(resultSet.getInt("id")); // Set ID for appointment
                return appointment;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null; // Returns null if no appointment is found
    }

    // Update an appointment in the database
    public void updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET patientName = ?, nic = ?, contactNumber = ?, email = ?, appointmentDate = ?, appointmentTime = ?, doctorName = ?, doctorSpecialization = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Patient patient = appointment.getPatient();
            Doctor doctor = appointment.getDoctor();

            preparedStatement.setString(1, patient.getName());
            preparedStatement.setString(2, patient.getNic());
            preparedStatement.setString(3, patient.getContactNumber());
            preparedStatement.setString(4, patient.getEmail());
            preparedStatement.setString(5, appointment.getAppointmentDate().toString());
            preparedStatement.setString(6, appointment.getAppointmentTime());
            preparedStatement.setString(7, doctor.getName());
            preparedStatement.setString(8, doctor.getSpecialization());
            preparedStatement.setInt(9, appointment.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Delete an appointment from the database
    public void deleteAppointment(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}

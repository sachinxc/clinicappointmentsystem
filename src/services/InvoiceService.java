package services;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Appointment;
import models.Invoice;
import models.Patient;
import models.Doctor;
import models.Treatment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class InvoiceService {

    // Generate an Invoice object based on the appointment, selected treatments, and payment details
    public Invoice generateInvoice(Appointment appointment, List<Treatment> selectedTreatments, BigDecimal subtotal, BigDecimal tax, BigDecimal totalAmount) {
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();

        return new Invoice(appointment, patient, doctor, selectedTreatments, subtotal, tax, totalAmount);
    }

    //This function produces a PDF of the generated invoice using the Apache pdf box library
    public void downloadInvoiceAsPDF(Invoice invoice, Stage stage) {
        if (invoice == null) {
            throw new IllegalStateException("No invoice data to save.");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 700);
                    for (String line : invoice.formatInvoice().split("\n")) {
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -15);
                    }
                    contentStream.endText();
                }
                document.save(file);
            } catch (IOException e) {
                throw new RuntimeException("Error while saving PDF: " + e.getMessage(), e);
            }
        }
    }
}

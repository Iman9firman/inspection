package com.garasi.kita.inspection.RTP.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class InvoiceIdGenerator {

    public static String generateInvoiceId(String branchCode) {
        // Mendapatkan tanggal saat ini
        LocalDate currentDate = LocalDate.now();
        // Format tanggal ke dalam string dengan format "yyyyMMdd"
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Generate 4 digit kode unik
        String uniqueCode = generateRandomNumericString(4);

        // Gabungkan semua komponen untuk membuat ID
        String invoiceId = "INV-" + formattedDate + branchCode + uniqueCode;
        return invoiceId;
    }

    // Fungsi untuk menghasilkan string numerik acak sepanjang n digit
    private static String generateRandomNumericString(int n) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(random.nextInt(10)); // angka random dari 0 hingga 9
        }
        return sb.toString();
    }

}

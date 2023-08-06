package com.garasi.kita.inspection.service;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public interface ExportPdfService {
    ByteArrayInputStream exportReceiptPdf(String kodeBooking);
}
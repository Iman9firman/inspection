package com.garasi.kita.inspection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garasi.kita.inspection.DAO.RepoDao;
import com.garasi.kita.inspection.model.*;
import com.lowagie.text.DocumentException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class ExportPdfServiceImpl implements ExportPdfService {
    private Logger logger = LoggerFactory.getLogger(ExportPdfServiceImpl.class);

    @Autowired
    private RepoDao dao;


    @Value("${path.file.upload}")
    private String pathName;

    @Autowired
    private TemplateEngine templateEngine;

    public ByteArrayInputStream exportReceiptPdf(String kode) {
        HashMap<String, Object> model = new HashMap<>();

        HashMap<Integer, String> noteInspection = new HashMap<>();
        String label[] = {"Inspection", "Data Konsumen", "Data lengkap kendaraan", "Dokumen kendaraan", "Fitur Kendaraan", "Ban", "Dashboard dan electrical", "Instrumen kendaraan", "Interior jok dan trim", "Eksterior body", "Eksterior kaca dan lampu", "Eksterior under body", "Oli dan cairan", "Ruang mesin", "Kelengkapan", "Test drive", "General summary"};


        Inspection inspection = dao.getDataInspection(kode);
        HashMap<Integer, List<InspectionDetailPhoto>> stringListHashMap = new HashMap<>();

        HashMap<String, InspectionDetail> hashMap = new HashMap<>();
        String bebasBanjir = "banjir_no";
        String tabrakan = "tabrakan_no";

        String perawatan = "-";
        String perbaikan = "-";
        String kesimpulan = "-";

        //note

        LinkedHashMap<String, List<String>> notes = new LinkedHashMap<>();

        Integer[] array = {40, 50, 60, 70, 80, 90, 11, 12, 13, 14, 15};
        List<Integer> list = Arrays.asList(array);


        for (InspectionDetail inspectionDetail : dao.getDatainpectionDetailService(kode)) {
            try {

                ValueModel valueModel = new ObjectMapper().readValue(inspectionDetail.getValue().replaceAll("(\r\n|\n)", "</br>"),
                        ValueModel.class);
                String commaSeparatedString = "";
                if (valueModel.getOption() != null) {
                    if (valueModel.getOption().size() > 0) {
                        commaSeparatedString = String.join(", ", valueModel.getOption());
                    }

                    if (valueModel.getValue() != null) {
                        if (list.indexOf(Integer.valueOf(inspectionDetail.getIdField().substring(0, 2))) >= 0) {
                            List<String> listnote = notes.get(inspectionDetail.getIdField().substring(0, 2));
                            if (listnote == null) {
                                listnote = new ArrayList<>();
                            }
                            listnote.add(inspectionDetail.getLabel() + " : " + valueModel.getValue().replace("</br>", "\n"));
                            notes.put(inspectionDetail.getIdField().substring(0, 2), listnote);
                        } else {
                            inspectionDetail.setValue(": " + valueModel.getValue() != null ? valueModel.getValue() : ": -");
                        }
                    }

                } else {
                    if (valueModel.getValue() != null) {
                        commaSeparatedString = valueModel.getValue() != null ? valueModel.getValue() : " - ";
                    }
                }

                if (commaSeparatedString.length() == 0) {
                    commaSeparatedString = "-";
                }

                inspectionDetail.setValue(": " + commaSeparatedString.replace("</br>", "\n"));

            } catch (Exception e) {
                String value = inspectionDetail.getValue().replace("{", "").replace("}", "");
                if (value.length() == 0) {
                    value = "-";
                }

                inspectionDetail.setValue(": " + value);
            }

            hashMap.put(inspectionDetail.getIdField(), inspectionDetail);

            if (inspectionDetail.getIdField().equalsIgnoreCase("170001")) {
                String value = inspectionDetail.getValue().replace(": ", "");
                if (value.equalsIgnoreCase("YA") || value.equalsIgnoreCase("Bekas banjir")) {
                    bebasBanjir = "banjir";
                } else if (value.equalsIgnoreCase("TIDAK") || value.equalsIgnoreCase("Bebas banjir")) {
                    bebasBanjir = "banjir_no";
                }
            }

            if (inspectionDetail.getIdField().equalsIgnoreCase("170002")) {
                String value = inspectionDetail.getValue().replace(": ", "");
                if (value.equalsIgnoreCase("YA") || value.equalsIgnoreCase("Bekas tabrakan")) {
                    tabrakan = "tabrakan";
                } else if (value.equalsIgnoreCase("TIDAK") || value.equalsIgnoreCase("Bebas tabrakan")) {
                    tabrakan = "tabrakan_no";
                }
            }


            if (inspectionDetail.getIdField().equalsIgnoreCase("170003")) {
                perawatan = inspectionDetail.getValue().replace(": ", "");
            }

            if (inspectionDetail.getIdField().equalsIgnoreCase("170004")) {
                perbaikan = inspectionDetail.getValue().replace(": ", "");
            }

            if (inspectionDetail.getIdField().equalsIgnoreCase("170005")) {
                kesimpulan = inspectionDetail.getValue().replace(": ", "");
            }

        }

        model.put("estimasiPerbaikan", perbaikan);
        model.put("estimasiPerawatan", perawatan);
        model.put("kesimpulan", kesimpulan);

        model.put("banjir", bebasBanjir);
        model.put("tabrakan", tabrakan);

        model.put("titleQuestion", label);
        model.put("inspection", inspection);
        model.put("inspectionNote", noteInspection);
        model.put("inspectionDetail", stringListHashMap);
        model.put("hashMap", hashMap);


        ArrayList<String> fdl = new ArrayList<>();
        fdl.add("BPKB;310001");
        fdl.add("STNK;310002");
        fdl.add("Faktur;310003");
        fdl.add(";310004");

        model.put("photoDokumen", listPhoto(kode, fdl));


        ArrayList<String> inl = new ArrayList<>();
        inl.add("Kilometer(rpm 3000);410001");
        inl.add("Interior Depan;410002");
        inl.add("Interior Belakang;410003");
        inl.add("Dashboard;410004");
        inl.add("Bagasi Terbuka;410005");

        model.put("interiorPhoto", listPhoto(kode, inl));

        ArrayList<String> exl = new ArrayList<>();
        exl.add("Tampak Depan;420001");
        exl.add("Tampak Depan Kanan;420002");
        exl.add("Tampak Depan Kiri;420003");
        exl.add("Tampak Atap;420004");
        exl.add("Tampak Belakang;420005");

        String photoCover = "https://www.allianceplast.com/wp-content/uploads/no-image-1024x1024.png";
        for (String fd : exl) {
            for (PhotoItem pp : dao.getDataInspectionDetailPhoto(kode, fd.split(";")[1])) {
                pp.setPath(pathName + "/" + pp.getPath());
                if (fd.split(";")[0].equalsIgnoreCase("Tampak Depan")) {
                    photoCover = pp.getPath();
                }
            }
        }
        model.put("exteriorPhoto", listPhoto(kode, exl));


        model.put("photoCover", photoCover);

        ArrayList<String> llp = new ArrayList<>();
        llp.add("Airbag;400001");
        llp.add("Sistem audio;400002");
        llp.add("Power window;400003");
        llp.add("EPS/Power stering;400004");
        llp.add("Sistem AC;400005");
        llp.add("Central Lock;400006");
        llp.add("Electric Mirror;400007");
        llp.add("Ban kiri depan;500001");
        llp.add("Ban kiri belakang;500002");
        llp.add("Ban kanan depan;500003");
        llp.add("Ban kanan belakang;500004");
        llp.add("Diagnosa OBD Scanner;600001");
        llp.add("Panel;600002");
        llp.add("Setir;600003");
        llp.add("Switch lampu;600004");
        llp.add("Switch wiper;600005");
        llp.add("Panel dashboard;600006");
        llp.add("Lampu plafon;600007");
        llp.add("Rem tangan;700001");
        llp.add("Rem kaki;700002");
        llp.add("Pedal Gas;700003");
        llp.add("Pedal kopling;700004");
        llp.add("Sun visor;700005");
        llp.add("Spion tengah;700006");
        llp.add("Pembuka kap mesin;700007");
        llp.add("Pembuka bagasi;700008");
        llp.add("Pembuka tangki mesin;700009");
        llp.add("Jok depan;800001");
        llp.add("Jok belakang;800002");
        llp.add("Sabuk pengaman;800003");
        llp.add("Console box;800004");
        llp.add("Trim interior;800005");
        llp.add("Bebas banjir;800006");
        llp.add("Kaca film;800007");
        llp.add("Handle pintu;800008");
        llp.add("Plafon;800009");
        llp.add("Karpet dasar;800011");
        llp.add("Bau interior;800012");
        llp.add("Feder kanan;900001");
        llp.add("Pintu depan kanan;900002");
        llp.add("Pintu belakang kanan;900003");
        llp.add("Lisplang kanan;900004");
        llp.add("Quarter panel kanan;900005");
        llp.add("Pintu bagasi;900006");
        llp.add("Bumper belakang;900007");
        llp.add("Quarter panel kiri;900008");
        llp.add("Pilar bagasi tengah;900009");
        llp.add("Pilar radiator atas;900011");
        llp.add("Apron;900012");
        llp.add("Lispang kiri;900013");
        llp.add("Pintu depan kiri;900014");
        llp.add("Pintu belakang kiri;900015");
        llp.add("Feder kiri;900016");
        llp.add("Atap;900017");
        llp.add("Kap mesin;900018");
        llp.add("Grill;900019");
        llp.add("Bumper depan;900021");
        llp.add("Pilar radiator bawah;900022");
        llp.add("Strut tower kiri;900023");
        llp.add("Strut tower kanan;900024");
        llp.add("Daun wipper;110001");
        llp.add("Kaca depan;110002");
        llp.add("Kaca jendela;110003");
        llp.add("Kaca belakang;110004");
        llp.add("Spion;110005");
        llp.add("Lampu depan;110006");
        llp.add("Lampu belakang;110007");
        llp.add("Velg;120001");
        llp.add("Discbrake;120002");
        llp.add("Brake pad ;120003");
        llp.add("Master rem;120004");
        llp.add("Shockbreaker;120005");
        llp.add("Link stabilizer;120006");
        llp.add("Steering rack;120007");
        llp.add("Upper - lower - arm;120008");
        llp.add("Crossmember;120009");
        llp.add("Ball joint;120011");
        llp.add("Knalpot;120012");
        llp.add("Drive shaft;120013");
        llp.add("Oli mesin;130001");
        llp.add("Oli Transmisi AT;130002");
        llp.add("Oli rem;130003");
        llp.add("Oli power steering;130004");
        llp.add("Air radiator;130005");
        llp.add("Cover klep;140001");
        llp.add("Cover timing belt;140002");
        llp.add("Kondisi seal crank shaft;140003");
        llp.add("Transmisi;140004");
        llp.add("Pompa power steering;140005");
        llp.add("Dinamo starter;140006");
        llp.add("Alternator;140007");
        llp.add("Water pump;140008");
        llp.add("Kompressor AC;140009");
        llp.add("Belt;140011");
        llp.add("Fan;140012");
        llp.add("Radiator;140013");
        llp.add("Kondsensor;140014");
        llp.add("Selang;140015");
        llp.add("Kabel;140016");
        llp.add("Getaran mesin;140017");
        llp.add("Suara mesin;140018");
        llp.add("Karter oli;140019");
        llp.add("Gardan;140021");
        llp.add("Kondisi aki;140022");

        model.put("lainnyaPhoto", listPhoto(kode, llp));

        ArrayList<String> sop = new ArrayList<>();
        sop.add(";170006");
        model.put("photoSOP", listPhoto(kode, sop));

        model.put("notes", notes);


        ByteArrayInputStream inputStream = null;
        try (FileInputStream fileInputStream = new FileInputStream(new ClassPathResource("final_reportGKI.docx").getFile())) {

            XWPFDocument doc = new XWPFDocument(fileInputStream);

            for (XWPFTable tables : doc.getTables()) {
                search(tables, model);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            doc.write(outputStream);

            inputStream = new ByteArrayInputStream(outputStream.toByteArray());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    private void search(XWPFTable table, HashMap<String, Object> model) throws Exception {
        if (table.getText().contains("${photoDokumen}") || table.getText().contains("${interiorPhoto}") || table.getText().contains("${exteriorPhoto}") || table.getText().contains("${lainnyaPhoto}") || table.getText().contains("${photoSOP}")) {
            String key = table.getText().trim().replace("$", "").replace("{", "").replace("}", "");
            Object value = model.get(key);
            if (value instanceof List) {


                List<PhotoItem> listnote = (List<PhotoItem>) value;

                double barisPhoto = Math.ceil((double) listnote.size() / 3);
                int nilaiInt = (int) barisPhoto;

                int llas1 = 0;
                int llas2 = 0;
                if (barisPhoto == 0) {
                    XWPFTableRow row = table.getRow(0);
                    XWPFTableCell cell = row.getCell(0);
                    if (cell.getParagraphs().size() > 0) {
                        cell.removeParagraph(0);
                    }
                    XWPFParagraph paragraph = cell.addParagraph();

                    List<XWPFRun> runs = paragraph.getRuns();
                    String[] lines = "Tidak terdapat foto".split("\n");

                    int pos = 0;
                    for (String line : lines) {
                        XWPFRun run;
                        if (runs.size() > 0) {
                            run = runs.get(0);
                            if (pos > 0) {
                                run.addBreak();
                            }
                        } else {
                            run = paragraph.createRun();
                        }

                        run.setText(line, pos);
                        pos++;
                    }

                } else {
                    XWPFTableRow row = table.getRow(0);
                    for (int i = 0; i < nilaiInt; i++) {
                        if (i != 0) {
                            row = table.createRow();
                        }
                        for (int iii = 0; iii < 3; iii++) {
                            if (listnote.size() > llas1) {
                                XWPFTableCell cell = row.getCell(iii);
                                if (cell.getParagraphs().size() > 0) {
                                    cell.removeParagraph(0);
                                }

                                XWPFParagraph paragraph = cell.addParagraph();

                                List<XWPFRun> runs = paragraph.getRuns();
                                String[] lines = listnote.get(llas1).getCaption().split("\n");

                                int pos = 0;
                                for (String line : lines) {
                                    XWPFRun run;
                                    if (runs.size() > 0) {
                                        run = runs.get(0);
                                        if (pos > 0) {
                                            run.addBreak();
                                        }
                                    } else {
                                        run = paragraph.createRun();
                                    }

                                    run.setText(line, pos);
                                    pos++;
                                }
                                llas1++;
                            }
                        }

                        row = table.createRow();
                        for (int ii = 0; ii < 3; ii++) {
                            if (listnote.size() > llas2) {
                                XWPFTableCell cell = row.getCell(ii);
                                if (cell.getParagraphs().size() > 0) {
                                    cell.removeParagraph(0);
                                }

                                XWPFParagraph paragraph = cell.addParagraph();
                                XWPFRun run = paragraph.createRun();

                                File file = new File(listnote.get(llas2).getPath());
                                if (file.exists()) {
                                    FileInputStream imageStream = new FileInputStream(file);
                                    run.addPicture(imageStream, Document.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(125), Units.toEMU(125));
                                    imageStream.close();
                                }

                                llas2++;
                            }
                        }
                    }
                }

            }
        } else {
            List<XWPFTableRow> rows = table.getRows();
            for (int i = 0; i < rows.size(); i++) {
                XWPFTableRow row = rows.get(i);
                for (int j = 0; j < row.getTableCells().size(); j++) {
                    XWPFTableCell cell = row.getCell(j);
                    if (cell.getTables().size() > 0) {
                        searchChild(cell.getTables(), model);
                    } else {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            replace(p, model);
                        }
                    }
                }
            }
        }


    }

    private void searchChild(List<XWPFTable> tables, HashMap<String, Object> model) throws Exception {
        for (XWPFTable nestedTable : tables) {
            for (XWPFTableRow row : nestedTable.getRows()) {
                for (XWPFTableCell nestedCell : row.getTableCells()) {
                    for (XWPFParagraph p : nestedCell.getParagraphs()) {
                        replace(p, model);
                    }
                }
            }
        }
    }

    private void replace(XWPFParagraph p, HashMap<String, Object> model) throws Exception {
        String textDefault = p.getText();
        String text = p.getText();
        if (text == null || text.isEmpty()) {
            text = p.getParagraphText();
        }

        if (text.startsWith("${")) {

            String key = text.replace("$", "").replace("{", "").replace("}", "");
            String keyChild = "";
            if (key.split("\\.").length == 2) {
                keyChild = key.split("\\.")[1];
                key = key.split("\\.")[0];

            }

            Object value = model.get(key);
            if (value instanceof Inspection) {
                Map<String, Object> mm = modelToHashMap(value);
                text = String.valueOf(mm.get(keyChild));
            } else if (value instanceof HashMap) {
                Map<String, Object> mm = (Map<String, Object>) value;
                Object valueChild = mm.get(keyChild);
                if (valueChild instanceof InspectionDetail) {
                    Map<String, Object> mmc = modelToHashMap(valueChild);
                    text = String.valueOf(mmc.get("value") != null ? mmc.get("value") : ": -");
                } else if (valueChild instanceof LinkedHashMap || valueChild instanceof List) {
                    List<String> listnote = (List<String>) valueChild;
                    text = String.join("\n", listnote);
                }
            } else if (value instanceof String) {
                if (text.contains("${photoCover}")) {
                    File file = new File((String) value);
                    if (file.exists()) {
                        List<XWPFRun> runs = p.getRuns();
                        for (XWPFRun r : runs) {
                            r.setText("", 0);
                            File file1 =  new File((String) value);
                            FileInputStream imageStream = new FileInputStream(file1);
                            r.addPicture(imageStream, Document.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(100), Units.toEMU(100));
                            imageStream.close();
                        }
                    }
                } else if (text.contains("${banjir}") || text.contains("${tabrakan}")) {
                    List<XWPFRun> runs = p.getRuns();
                    for (XWPFRun r : runs) {
                        r.setText("", 0);
                        FileInputStream imageStream = new FileInputStream(new ClassPathResource(value + ".png").getFile());
                        r.addPicture(imageStream, Document.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(100), Units.toEMU(100));
                        imageStream.close();
                    }
                } else {
                    text = String.valueOf(value);
                }
            } else {
                text = "";
            }

            if (textDefault.equalsIgnoreCase(text)) {
                text = "";
            }

            if (text.startsWith("http")) {
                List<XWPFRun> runs = p.getRuns();
                for (XWPFRun r : runs) {
                    r.setText("", 0);
                    FileInputStream imageStream = new FileInputStream(new ClassPathResource("logo.png").getFile());
                    r.addPicture(imageStream, Document.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(100), Units.toEMU(100));
                    imageStream.close();
                }

            } else {
                List<XWPFRun> runs = p.getRuns();
                for (int i = runs.size() - 1; i > 0; i--) {
                    p.removeRun(i);
                }
                String[] lines = text.split("\n");

                int pos = 0;
                for (String line : lines) {
                    XWPFRun run = runs.get(0);
                    if (pos > 0) {
                        run.addBreak();
                    }
                    run.setText(line, pos);
                    pos++;
                }
            }
        }
    }

    private List<PhotoItem> listPhoto(String kode, ArrayList<String> fdl) {
        List<PhotoItem> listPhotos = new ArrayList<>();
        for (String fd : fdl) {
            for (PhotoItem pp : dao.getDataInspectionDetailPhoto(kode, fd.split(";")[1])) {
                pp.setPath(pathName + "/" + pp.getPath());
                String title = fd.split(";")[0];
                if (title.length() == 0) {
                    title = pp.getCaption();
                }
                pp.setAndroidPath(title);
                listPhotos.add(pp);
            }
        }

        return listPhotos;
    }

    public static Map<String, Object> modelToHashMap(Object model) {
        Map<String, Object> hashMap = new HashMap<>();
        try {
            Class<?> clazz = model.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(model);
                hashMap.put(fieldName, fieldValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

}


package com.garasi.kita.inspection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garasi.kita.inspection.DAO.RepoDao;
import com.garasi.kita.inspection.model.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

import static org.xhtmlrenderer.test.DocumentDiffTest.height;

@Service
public class ExportReportService {

    @Autowired
    private RepoDao dao;


    @Value("${path.file.upload}")
    private String pathName;

    @Value("${path.file.template}")
    private String pathTemplate;

    @Value("${path.file.output}")
    private String pathOutput;

    @Value("${path.file.img}")
    private String pathImg;

    Logger logger = LoggerFactory.getLogger(ExportReportService.class);

    public void newReportDocV2(String kode) {

        HashMap<String, Object> model = new HashMap<>();

        HashMap<Integer, String> noteInspection = new HashMap<>();
        String label[] = {"Data Konsumen", "Data lengkap kendaraan", "Dokumen kendaraan", "Foto Dokumen", "Foto Wajib Interior", "Foto Wajib Exterior", "Bagian Depan", "Oli dan cairan", "Sisi kiri", "Under Body", "Belakang", "Sisi atas", "Sisi kanan", "Fungsi elektrikal", "Interior", "Test drive", "Kelengkapan", "General summary"};


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
                        List<String> listnote = notes.get(HashmapFormV2.grouping(inspectionDetail.getIdField()));
                        if (listnote == null) {
                            listnote = new ArrayList<>();
                        }
                        listnote.add(inspectionDetail.getLabel() + " : " + valueModel.getValue().replace("</br>", "\n"));
                        notes.put(HashmapFormV2.grouping(inspectionDetail.getIdField()), listnote);
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
        llp.add("Kaca;10400001");
        llp.add("Wiper;10400002");
        llp.add("Kap mesin;10400003");
        llp.add("Bumper;10400004");
        llp.add("Lampu depan;10400005");
        llp.add("Bulkheat;10400006");
        llp.add("Radiator;10400007");
        llp.add("Kompesor AC;10400008");
        llp.add("Alternator;10400009");
        llp.add("Dinamo Starter;10400010");
        llp.add("Water pump;10400011");
        llp.add("Belt;10400012");
        llp.add("Pompa powerstering;10400013");
        llp.add("Cover Valfe;10400014");
        llp.add("cylinder head;10400015");
        llp.add("Cover timing;10400016");
        llp.add("Seal crankshaft;10400017");
        llp.add("Carter oli;10400018");
        llp.add("Transmisi;10400019");
        llp.add("Steering rack;10400020");
        llp.add("Engine Mounting;10400021");
        llp.add("Kondisi Aki;10400022");
        llp.add("Kabel;10400023");
        llp.add("Oli mesin;10500001");
        llp.add("Oli transmisi;10500002");
        llp.add("Air radiator;10500003");
        llp.add("Oli power stering;10500003");
        llp.add("Minyak rem;10500004");
        llp.add("Fender kiri;10600001");
        llp.add("Ban depan kiri;10600002");
        llp.add("Velg depan kiri;10600003");
        llp.add("Shockbreaker depan kiri;10600004");
        llp.add("Rem depan kiri;10600005");
        llp.add("Link stabilizer;10600006");
        llp.add("Tieroad;10600007");
        llp.add("Arm;10600008");
        llp.add("Balljoin;10600009");
        llp.add("Driveshaft kiri;10600010");
        llp.add("Spion kiri;10600011");
        llp.add("Pintu depan kiri;10600012");
        llp.add("Pilar A;10600013");
        llp.add("Pilar B;10600014");
        llp.add("Jok depan kiri;10600015");
        llp.add("Seatbelt;10600016");
        llp.add("Listplang;10600017");
        llp.add("Pintu belakang kiri;10600018");
        llp.add("Pilar C;10600019");
        llp.add("Quarter kiri;10600020");
        llp.add("Shockbreaker belakang kiri;10600021");
        llp.add("Rem belakang kiri;10600022");
        llp.add("Ban belakang kiri;10600023");
        llp.add("velg belakang kiri;10600024");
        llp.add("Kaca sisi kiri;10600025");
        llp.add("Chasis;10700001");
        llp.add("Cross member;10700002");
        llp.add("Tangki bahan bakar;10700003");
        llp.add("Gardan;10700004");
        llp.add("Lantai bagasi;10700005");
        llp.add("Knalpot;10700006");
        llp.add("Lampu belakang;10800001");
        llp.add("Pintu bagasi;10800002");
        llp.add("Pilar bagasi;10800003");
        llp.add("Shock bagasi;10800004");
        llp.add("Bumper belakang;10800005");
        llp.add("Sensor mundur;10800006");
        llp.add("Kaca belakang;10800007");
        llp.add("Atap;10900001");
        llp.add("Antena;10900002");
        llp.add("velg belakang kanan;10110001");
        llp.add("Ban belakang kanan;10110002");
        llp.add("Rem belakang kanan;10110003");
        llp.add("Shockbreaker belakang kanan;10110004");
        llp.add("Quarter kanan;10110005");
        llp.add("Pintu belakang kanan;10110006");
        llp.add("Pilar C;10110007");
        llp.add("Listplang;10110008");
        llp.add("Pintu depan kanan;10110009");
        llp.add("Pilar A;10110010");
        llp.add("Pilar B;10110011");
        llp.add("Seatbelt;10110012");
        llp.add("Jok depan kanan;10110013");
        llp.add("Spion kanan;10110014");
        llp.add("Ban depan kanan;10110015");
        llp.add("Velg depan kanan;10110016");
        llp.add("Sockbreaker depan kanan;10110017");
        llp.add("Rem depan kanan;10110018");
        llp.add("Link stabilizer;10110019");
        llp.add("Tieroad;10110020");
        llp.add("Arm;10110021");
        llp.add("Balljoin;10110022");
        llp.add("Driveshaft kanan;10110023");
        llp.add("Fender kanan;10110024");
        llp.add("Kaca sisi kanan;10110025");
        llp.add("Panel indikator;10120001");
        llp.add("Diangnosa OBD scanner;10120002");
        llp.add("Switch lampu;10120003");
        llp.add("Switch wiper;10120004");
        llp.add("Power window;10120005");
        llp.add("Spion elektrik;10120006");
        llp.add("Lampu plafon;10120007");
        llp.add("Instrumen AC;10120008");
        llp.add("Central Lock;10120009");
        llp.add("Steering Switch;10120011");
        llp.add("Fitur Lainnya;10120012");
        llp.add("Doortrim;10130001");
        llp.add("setir;10130002");
        llp.add("Panel Dashboard;10130003");
        llp.add("Laci;10130004");
        llp.add("Sunvisor;10130005");
        llp.add("Spion tengah;10130006");
        llp.add("Plafon;10130007");
        llp.add("Kisi-kisi Ac;10130008");
        llp.add("Fungsi Ac;10130009");
        llp.add("Audio;10130010");
        llp.add("Console Box;10130011");
        llp.add("Tuas Perseneling;10130012");
        llp.add("Rem Tangan;10130013");
        llp.add("Pedal ;10130014");
        llp.add("Karpet Dasar;10130015");
        llp.add("Jok Belakang;10130016");



        model.put("lainnyaPhoto", listPhoto(kode, llp));

        ArrayList<String> sop = new ArrayList<>();
        sop.add(";170006");
        model.put("photoSOP", listPhoto(kode, sop));

        model.put("notes", notes);

        File file = new File(pathTemplate + "/" + "v2_reportGKI.docx");

        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            XWPFDocument doc = new XWPFDocument(fileInputStream);

            for (XWPFTable tables : doc.getTables()) {
                search(tables, model);
            }

            try (FileOutputStream outputStream = new FileOutputStream(pathTemplate + "/" + kode + ".docx")) {
                logger.info("Sukses DOC >> " + kode);
                doc.write(outputStream);
                dao.updateInspection(kode, 4);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("gagal 1 DOC >> " + kode + ": " + e.getMessage());
                dao.updateInspection(kode, 9);
            }
        } catch (
                IOException e) {
            logger.error("gagal 2 DOC >> " + kode + ": " + e.getMessage());
            dao.updateInspection(kode, 9);
            e.printStackTrace();

        } catch (
                Exception e) {
            dao.updateInspection(kode, 9);
            logger.error("gagal 3 DOC >> " + kode + ": " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void newReportDoc(String kode) {

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

        File file = new File(pathTemplate + "/" + "final_reportGKI.docx");

        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            XWPFDocument doc = new XWPFDocument(fileInputStream);

            for (XWPFTable tables : doc.getTables()) {
                search(tables, model);
            }

            try (FileOutputStream outputStream = new FileOutputStream(pathTemplate + "/" + kode + ".docx")) {
                logger.info("Sukses DOC >> " + kode);
                doc.write(outputStream);
                dao.updateInspection(kode, 4);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("gagal 1 DOC >> " + kode + ": " + e.getMessage());
                dao.updateInspection(kode, 9);
            }
        } catch (
                IOException e) {
            logger.error("gagal 2 DOC >> " + kode + ": " + e.getMessage());
            dao.updateInspection(kode, 9);
            e.printStackTrace();

        } catch (
                Exception e) {
            dao.updateInspection(kode, 9);
            logger.error("gagal 3 DOC >> " + kode + ": " + e.getMessage());
            e.printStackTrace();
        }

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
                                String textCaption = listnote.get(llas1).getCaption();
                                if (table.getText().contains("${photoDokumen}") || table.getText().contains("${interiorPhoto}") || table.getText().contains("${exteriorPhoto}") || table.getText().contains("${photoSOP}")) {
                                    textCaption = listnote.get(llas1).getAndroidPath();
                                } else if (table.getText().contains("${lainnyaPhoto}")) {
                                    textCaption = listnote.get(llas1).getAndroidPath() + " : " + listnote.get(llas1).getCaption();
                                }

                                String[] lines = "-".split("\n");
                                if (textCaption != null) {
                                    lines = textCaption.split("\n");
                                }

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
                                        run.addBreak();
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
                                    addPicture(run, file);
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


    private void addPicture(XWPFRun run, File file) throws IOException, InvalidFormatException {
        FileInputStream imageStream2 = new FileInputStream(file);

        BufferedImage image = ImageIO.read(imageStream2);
        int defaultWidthInPixels = image.getWidth();
        int defaultHeightInPixels = image.getHeight();
        float finalWidthA = 140;
        double percent = finalWidthA / defaultWidthInPixels;

        if (defaultHeightInPixels > defaultWidthInPixels) {
            defaultWidthInPixels = image.getHeight();
            defaultHeightInPixels = image.getWidth();
            percent = finalWidthA / defaultWidthInPixels;

            run.addPicture(rotateImage90Degrees(image), Document.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(defaultWidthInPixels * percent), Units.toEMU(defaultHeightInPixels * percent));
            imageStream2.close();
        } else {
            FileInputStream imageStream = new FileInputStream(file);
            run.addPicture(imageStream, Document.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(defaultWidthInPixels * percent), Units.toEMU(defaultHeightInPixels * percent));
            imageStream.close();
            imageStream2.close();
        }


    }

    public static FileInputStream rotateImage90Degrees(BufferedImage originalImage) throws IOException {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage rotatedImage = new BufferedImage(height, width, originalImage.getType());

        Graphics2D g2d = rotatedImage.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate(height, 0);
        at.rotate(Math.toRadians(90));
        g2d.setTransform(at);
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();

        File tempFile = File.createTempFile("temp-image", "." + ".jpg");
        ImageIO.write(rotatedImage, "jpg", tempFile);

        return new FileInputStream(tempFile);
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
                    File file = new File(String.valueOf(value));
                    if (file.exists()) {
                        List<XWPFRun> runs = p.getRuns();
                        for (XWPFRun r : runs) {
                            r.setText("", 0);
                            addPicture(r, file);
                        }
                    }
                } else if (text.contains("${banjir}") || text.contains("${tabrakan}")) {
                    List<XWPFRun> runs = p.getRuns();
                    for (XWPFRun r : runs) {
                        r.setText("", 0);
                        File file = new File(pathImg + "/" + value + ".png");
                        addPicture(r, file);
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
                    File file = new File(pathImg + "/" + (String) value);
                    addPicture(r, file);
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
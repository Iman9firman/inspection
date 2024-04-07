package com.garasi.kita.inspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garasi.kita.inspection.DAO.RepoDao;
import com.garasi.kita.inspection.model.*;
import com.garasi.kita.inspection.service.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class InspectionController {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    InpectionService inpectionService;

    @Autowired
    InpectionDetailService inpectionDetailService;

    @Autowired
    PhotoItemService photoItemService;

    @Value("${path.file.upload}")
    private String pathName;

    @Value("${path.file.output}")
    private String pathOutput;

    @Value("${path.file.outputpdf}")
    private String pathOutputPdf;

    @Value("${path.file.url}")
    private String pathUrl;

    @Autowired
    private RepoDao dao;

    @Autowired
    private ExportPdfService exportPdfService;

    @GetMapping(value = {"/", "/index"})
    public String homePage(Model model) {
        List<City> city = inpectionService.getCity();
        List<Inspector> inspectors = inpectionService.getInspector();
        inspectors.get(0).setStatus("10");
        inspectors.get(1).setStatus("80");
        inspectors.get(2).setStatus("30");
        inspectors.get(3).setStatus("97");
        inspectors.get(4).setStatus("100");


        for (Inspector is : inspectors) {
            if (Integer.valueOf(is.getStatus()) == 100) {
                is.setDetail("progress-bar bg-success");
            } else if (Integer.valueOf(is.getStatus()) > 90) {
                is.setDetail("progress-bar bg-info");
            } else if (Integer.valueOf(is.getStatus()) > 60) {
                is.setDetail("progress-bar");
            } else if (Integer.valueOf(is.getStatus()) > 20) {
                is.setDetail("progress-bar bg-warning");
            } else if (Integer.valueOf(is.getStatus()) <= 20) {
                is.setDetail("progress-bar bg-danger");
            }
        }

        List<Inspection> inspections = inpectionService.getData();

        model.addAttribute("appName", appName);
        model.addAttribute("inspection", new Inspection());
        model.addAttribute("inspectors", inspectors);
        model.addAttribute("city", city);

        double sukses = 0;
        for (Inspection inspection : inspections) {
            if (Integer.valueOf(inspection.getStatus()) == 1) {
                sukses += 1;
            }
        }

        double total = inspections.size();
        double task = (sukses / total) * 100;
        double pending = total - sukses;

        model.addAttribute("task", (int) task);
        model.addAttribute("pending", (int) pending);
        model.addAttribute("month", new SimpleDateFormat("MMM yyyy").format(new Date()));

        return "index";
    }

    @GetMapping("/inspectionList")
    public String inspectionList(Model model) {
        List<Inspection> inspections = new ArrayList<>();
        model.addAttribute("appName", appName);

        int number = 1;
        for (Inspection inspection : inpectionService.getData()) {
            inspection.setId(number);
            inspections.add(inspection);
            number++;
        }

        model.addAttribute("inspection", inspections);
        model.addAttribute("message", new Message());
        return "inspection_list";
    }

    @Autowired
    private ExportReportService exportReportService;

    @GetMapping("/downloadInspection2")
    public ResponseEntity<ByteArrayResource> downloadFile(HttpServletResponse response, @RequestParam("kode_booking") String kode, @RequestParam("type") String type) throws IOException {
        String path = pathOutput;
        if (type.equalsIgnoreCase("pdf")) {
            path = pathOutputPdf;
        }
        File file = new File(path + "/" + kode + "." + type);
        byte[] data = Files.readAllBytes(file.toPath());
        ByteArrayResource resource = new ByteArrayResource(data);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }


    @GetMapping("/detailInspection")
    public String inspectionDetail(Model model, @RequestParam("kode_booking") String kode) {
        HashMap<Integer, String> noteInspection = new HashMap<>();
        String label[] = {"Inspection", "Data Konsumen", "Data lengkap kendaraan", "Dokumen kendaraan", "Fitur Kendaraan", "Ban", "Dashboard dan electrical", "Instrumen kendaraan", "Interior jok dan trim", "Eksterior body", "Eksterior kaca dan lampu", "Eksterior under body", "Oli dan cairan", "Ruang mesin", "Kelengkapan", "Test drive", "General summary"};

        model.addAttribute("appName", appName);

        Inspection inspection = dao.getDataInspection(kode);
        HashMap<Integer, List<InspectionDetailPhoto>> stringListHashMap = new HashMap<>();

        HashMap<String, InspectionDetail> hashMap = new HashMap<>();
        String bebasBanjir = "logo";
        String tabrakan = "logo";

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

        model.addAttribute("estimasiPerbaikan", perbaikan);
        model.addAttribute("estimasiPerawatan", perawatan);
        model.addAttribute("kesimpulan", kesimpulan);

        model.addAttribute("banjir", bebasBanjir);
        model.addAttribute("tabrakan", tabrakan);

        model.addAttribute("titleQuestion", label);
        model.addAttribute("inspection", inspection);
        model.addAttribute("inspectionNote", noteInspection);
        model.addAttribute("inspectionDetail", stringListHashMap);
        model.addAttribute("hashMap", hashMap);


        ArrayList<String> fdl = new ArrayList<>();
        fdl.add("BPKB;310001");
        fdl.add("STNK;310002");
        fdl.add("Faktur;310003");
        fdl.add(";310004");

        model.addAttribute("photoDokumen", listPhoto(kode, fdl));


        ArrayList<String> inl = new ArrayList<>();
        inl.add("Kilometer(rpm 3000);410001");
        inl.add("Interior Depan;410002");
        inl.add("Interior Belakang;410003");
        inl.add("Dashboard;410004");
        inl.add("Bagasi Terbuka;410005");

        model.addAttribute("interiorPhoto", listPhoto(kode, inl));

        ArrayList<String> exl = new ArrayList<>();
        exl.add("Tampak Depan;420001");
        exl.add("Tampak Depan Kanan;420002");
        exl.add("Tampak Depan Kiri;420003");
        exl.add("Tampak Atap;420004");
        exl.add("Tampak Belakang;420005");

        String photoCover = "https://www.allianceplast.com/wp-content/uploads/no-image-1024x1024.png";
        for (String fd : exl) {
            for (PhotoItem pp : dao.getDataInspectionDetailPhoto(kode, fd.split(";")[1])) {
                pp.setPath(pathUrl + pp.getPath());
                if (fd.split(";")[0].equalsIgnoreCase("Tampak Depan")) {
                    photoCover = pp.getPath();
                }
            }
        }
        model.addAttribute("exteriorPhoto", listPhoto(kode, exl));


        model.addAttribute("photoCover", photoCover);

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

        model.addAttribute("lainnyaPhoto", listPhoto(kode, llp));

        ArrayList<String> sop = new ArrayList<>();
        sop.add(";170006");
        model.addAttribute("photoSOP", listPhoto(kode, sop));

        model.addAttribute("notes", notes);

        return "detail_inspection";
    }


    @PostMapping("/inputCustomer")
    public String greetingSubmit(Model model, @ModelAttribute Inspection inspection) {
        String pattern = "yyyyMMddHHmmss";
        DateFormat df = new SimpleDateFormat(pattern);
        inspection.setKodeBooking(inspection.getNomorPolisi() + "-" + df.format(new Date()));
        inspection.setNomorPolisi(inspection.getNomorPolisi());
        String price = "";
        if (inspection.getPaket().equalsIgnoreCase("Standart")) {
            price = "350000";
        } else if (inspection.getPaket().equalsIgnoreCase("Premium")) {
            price = "400000";
        } else {
            price = "550000";
        }
        inspection.setPrice(price);
        inspection.setCreateAt("admin");

        inpectionService.saveData(inspection);

        return "redirect:/";
    }

    @Autowired
    RestService restService;

    @PostMapping("/inputMessage")
    public String getMessageInput(Model model, @ModelAttribute Message message) {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        Date date = calendar.getTime();
        message.setCreate_at(date);
        String path = pathOutputPdf;

        Path sourceFile = Paths.get(path + "/" + message.getKode_booking() + ".pdf");
        Path targetFile = Paths.get("/var/www/html/" + message.getKode_booking() + ".pdf"); // Ganti dengan jalur file target di direktori /var/www/html

        message.setUrl(targetFile.toString());
        try {
            if (Files.exists(targetFile)) {
                Files.delete(targetFile);
            }

            Files.copy(sourceFile, targetFile);
            inpectionService.saveData(message);
            LocalTime currentTime = LocalTime.now();


            try {
                Thread.sleep(5000);


                String url = "https://chatapps.8x8.com/api/v1/subaccounts/GKI_WhatsApp/messages";
                String urlFile = "http://cms-garasikitaindonesia.com/" + message.getKode_booking() + ".pdf";
                restService.postData(url, contentWA(message.getKode_booking(), message.getParticipant(), urlFile, greetBasedOnTime(currentTime), message.getContent().split(";")[0], message.getContent().split(";")[1], message.getContent().split(";")[2]));

            } catch (InterruptedException e) {
                System.err.println("Terjadi kesalahan dalam menahan eksekusi: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            LoggerFactory.getLogger(InspectionController.class).error("inputMessage>>" + e.getMessage());
        }
        return "redirect:/messageList";
    }

    public static String replacePhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^(08|62)(\\d+)$");
        Matcher matcher = pattern.matcher(phoneNumber);
        if (matcher.matches()) {
            String prefix = matcher.group(1);
            if ("08".equals(prefix)) {
                return phoneNumber.replaceFirst("^08", "+628");
            } else {
                return "+" + phoneNumber;
            }
        } else {
            return phoneNumber;
        }
    }

    public static String greetBasedOnTime(LocalTime currentTime) {
        String greeting;

        // Memeriksa jam saat ini untuk memberikan salam yang sesuai
        if (currentTime.isAfter(LocalTime.of(11, 0)) && currentTime.isBefore(LocalTime.of(14, 0))) {
            greeting = "Siang";
        } else if (currentTime.isAfter(LocalTime.of(14, 59)) && currentTime.isBefore(LocalTime.of(19, 0))) {
            greeting = "Petang";
        } else if (currentTime.isAfter(LocalTime.of(19, 01)) && currentTime.isBefore(LocalTime.of(24, 0))) {
            greeting = "Petang";
        } else {
            greeting = "Pagi";
        }

        return greeting;
    }

    private String contentWA(String id, String participant, String urlFile, String param1, String param2, String param3, String param4) {
        JSONObject headervalue = new JSONObject();
        headervalue.put("type", "document");
        headervalue.put("url", urlFile);

        JSONObject body1 = new JSONObject();
        body1.put("type", "text");
        body1.put("text", param1);

        JSONObject body2 = new JSONObject();
        body2.put("type", "text");
        body2.put("text", param2);

        JSONObject body3 = new JSONObject();
        body3.put("type", "text");
        body3.put("text", param3);

        JSONObject body4 = new JSONObject();
        body4.put("type", "text");
        body4.put("text", param4);

        JSONObject componenHeader = new JSONObject();
        componenHeader.put("type", "header");
        componenHeader.put("parameters", new JSONArray().put(headervalue));

        JSONObject componenBody = new JSONObject();
        componenBody.put("type", "body");
        componenBody.put("parameters", new JSONArray().put(body1).put(body2).put(body3).put(body4));


        JSONArray components = new JSONArray();
        components.put(componenHeader);
        components.put(componenBody);

        JSONObject template = new JSONObject();
        template.put("language", "id");
        template.put("name", "report_review");
        template.put("components", components);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", new JSONObject().put("msisdn", replacePhoneNumber(participant)));
        jsonObject.put("clientMessageId", id);
        jsonObject.put("type", "template");
        jsonObject.put("content", new JSONObject().put("template", template));

        LoggerFactory.getLogger(InspectionController.class).error(jsonObject.toString());

        return jsonObject.toString();
    }

    private List<PhotoItem> listPhoto(String kode, ArrayList<String> fdl) {
        List<PhotoItem> listPhotos = new ArrayList<>();
        for (String fd : fdl) {
            for (PhotoItem pp : dao.getDataInspectionDetailPhoto(kode, fd.split(";")[1])) {
                pp.setPath(pathUrl + pp.getPath());
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

    private HashMap<String, Object> dataDetailInspection(String kode) {
        return null;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/single-file-upload")
    public String handleFileUploadUsingCurl(
            @RequestParam("file") MultipartFile file, @ModelAttribute Message message) throws IOException {

        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            // Handle empty file error
            response.put("error", "File is empty.");
            return "redirect:/inspectionList";
        } else if (!file.getOriginalFilename().endsWith(".pdf")) {
            // Handle invalid file format error
            response.put("error", "Invalid file format. Only .txt files are allowed.");
            return "redirect:/inspectionList";
        } else {
            String sanitizedFileName = sanitizeFileName(file.getOriginalFilename());
            // Check if the sanitized file name is different from the original file name
            if (!sanitizedFileName.equals(file.getOriginalFilename())) {
                // Handle file name with restricted characters error
                response.put("error", "File name contains restricted characters. Please rename the file.");
                return "redirect:/inspectionList";
            }

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
            Date date = calendar.getTime();

            String name = date.getTime() + "_" + file.getOriginalFilename();

            System.out.println(name);


            try {
                StringBuilder fileNames = new StringBuilder();
                Path fileNameAndPath = Paths.get("/var/www/html/", name);
                fileNames.append(file.getOriginalFilename());
                Files.write(fileNameAndPath, file.getBytes());

                message.setCreate_at(date);
                inpectionService.saveData(message);
                LocalTime currentTime = LocalTime.now();

                try {
                    // Menahan eksekusi selama 5 detik (5000 milidetik)
                    Thread.sleep(5000);
                    String url = "https://chatapps.8x8.com/api/v1/subaccounts/GKI_WhatsApp/messages";
                    String urlFile = "http://cms-garasikitaindonesia.com/" + name;
                    restService.postData(url, contentWA(message.getKode_booking(), message.getParticipant(), urlFile, greetBasedOnTime(currentTime), message.getContent().split(";")[0], message.getContent().split(";")[1], message.getContent().split(";")[2]));
                } catch (InterruptedException e) {
                    System.err.println("Terjadi kesalahan dalam menahan eksekusi: " + e.getMessage());
                }


            } catch (Exception e) {
                e.printStackTrace();
                LoggerFactory.getLogger(InspectionController.class).error(e.getMessage());
            }
            return "redirect:/messageList";

        }
    }

    private String sanitizeFileName(String fileName) {
        String restrictedCharacters = "/\\:*?\"<>|";
        String sanitizedFileName = fileName.replaceAll("[" + restrictedCharacters + "]", "_");
        System.out.println(sanitizedFileName);
        return sanitizedFileName;
    }

    @GetMapping("/messageList")
    public String messageList(Model model) {
        List<Message> messages = new ArrayList<>();
        model.addAttribute("appName", appName);

        int number = 1;
        for (Message message : inpectionService.getDataMessage()) {
            message.setId(number);
            messages.add(message);
            number++;
        }

        model.addAttribute("messages", messages);

        return "message_result_list";
    }


}



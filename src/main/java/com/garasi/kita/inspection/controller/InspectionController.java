package com.garasi.kita.inspection.controller;

import com.garasi.kita.inspection.model.*;
import com.garasi.kita.inspection.repositories.InspectionRepository;
import com.garasi.kita.inspection.service.InpectionDetailService;
import com.garasi.kita.inspection.service.InpectionService;
import com.garasi.kita.inspection.service.PhotoItemService;
import com.garasi.kita.inspection.service.UserService;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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

    @Value("${path.file.url}")
    private String pathUrl;


    @GetMapping("/")
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

    @PostMapping("/android")
    public String saveInspectionDetail(@RequestBody Map<String, List<InspectionDetail>> inspectionDetail,
                                       HttpServletRequest request) {
        for (InspectionDetail inspectionDetail1 : inspectionDetail.get("inspectionDetail")) {
            inpectionDetailService.saveData(inspectionDetail1);
        }
        return "success";
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String name,
                                             HttpServletRequest request) {
        return ResponseEntity.ok().body("sukses");
    }

    @PostMapping("/android/photo/{kode_booking}/{id_field}")
    public ResponseEntity<Object> uploadImage(@PathVariable("kode_booking") String
                                                      kodeBooking, @PathVariable("id_field") String idField, @RequestParam String
                                                      androidPath, @RequestParam("image") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String type = ".jpg";
            try {
                type = file.getOriginalFilename().split("\\.")[1];
            } catch (Exception e) {
            }

            String name = new Date().getTime() + "_" + kodeBooking + "_" + idField + "." + type;

            StringBuilder fileNames = new StringBuilder();
            Path fileNameAndPath = Paths.get(pathName, name);
            fileNames.append(file.getOriginalFilename());
            Files.write(fileNameAndPath, file.getBytes());

            PhotoItem photoItem = new PhotoItem();
            photoItem.setIdField(idField);
            photoItem.setKodeBooking(kodeBooking);
            photoItem.setAndroidPath(androidPath);
            photoItem.setPath(pathUrl + name);

            photoItemService.saveData(photoItem);

        } else {
            return ResponseEntity.badRequest().body("file not found");
        }
        return ResponseEntity.accepted().body("success");
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam String name) throws IOException {

        File file = new File(pathName + "/" + name);
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

}

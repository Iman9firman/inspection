package com.garasi.kita.inspection.controller;

import com.garasi.kita.inspection.DAO.RepoDao;
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
import org.springframework.http.HttpStatus;
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

    @Autowired
    private RepoDao dao;


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
        return "inspection_list";
    }

    @GetMapping("/detailInspection")
    public String inspectionDetail(Model model, @RequestParam("kode_booking") String kode) {
        HashMap<Integer, String> noteInspection = new HashMap<>();
        String[] label = {"Inspection", "Data Kendaraan", "DOKUMEN", "FITUR", "Data7", "Data8", "Data9", "Data10", "Data11", "Data12", "Data13", "Data14"};
        model.addAttribute("appName", appName);

        Inspection inspection = dao.getDataInspection(kode);
        HashMap<Integer, List<InspectionDetailPhoto>> stringListHashMap = new HashMap<>();
        List<InspectionDetailPhoto> inspectionDetailList = new ArrayList<>();
        Integer keyCurrent = 0;
        for (InspectionDetail inspectionDetail : dao.getDatainpectionDetailService(kode)) {
            if (keyCurrent != Integer.parseInt(inspectionDetail.getIdField().substring(0, 1))) {
                inspectionDetailList = new ArrayList<>();
                keyCurrent = Integer.parseInt(inspectionDetail.getIdField().substring(0, 1));
            }

            if (Integer.parseInt(inspectionDetail.getIdField().substring(5, 6)) == 0) {
                noteInspection.put(keyCurrent, inspectionDetail.getValue());
            } else {
                InspectionDetailPhoto inspectionDetailPhoto = new InspectionDetailPhoto();
                inspectionDetailPhoto.setId(inspectionDetail.getId());
                inspectionDetailPhoto.setKodeBooking(inspectionDetail.getKodeBooking());
                inspectionDetailPhoto.setIdField(inspectionDetail.getIdField());
                inspectionDetailPhoto.setLabel(inspectionDetail.getLabel());
                inspectionDetailPhoto.setValue(inspectionDetail.getValue());

                inspectionDetailPhoto.setPhoto(dao.getDataInspectionDetailPhoto(inspectionDetail.getKodeBooking(), inspectionDetail.getIdField()));

                inspectionDetailList.add(inspectionDetailPhoto);
            }

            stringListHashMap.put(Integer.parseInt(inspectionDetail.getIdField().substring(0, 1)), inspectionDetailList);

        }

        model.addAttribute("estimasiPerbaikan", "harga perbaikan sama dengan");
        model.addAttribute("estimasiPerawatan", "harga perawatan sama dengan");
        model.addAttribute("kesimpulan", "jadi kesimpulannya adalah");
        model.addAttribute("grade", "A");

        //dummy
        ArrayList<String> photo = new ArrayList<>();
        photo.add("https://1.bp.blogspot.com/-zH4J9gq-zHE/W2HhUs1hq8I/AAAAAAAADNE/0_XZzXNPIMsz5_9tNGoTPeSEz9mpizPxgCEwYBhgL/s1600/130625-F-BH566-591.jpeg");
        photo.add("https://i.ytimg.com/vi/5mQEuso00d4/maxresdefault.jpg");

        model.addAttribute("interiorPhoto", photo);
        model.addAttribute("exteriorPhoto", photo);

        model.addAttribute("titleQuestion", label);
        model.addAttribute("inspection", inspection);
        model.addAttribute("inspectionNote", noteInspection);
        model.addAttribute("inspectionDetail", stringListHashMap);
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


}

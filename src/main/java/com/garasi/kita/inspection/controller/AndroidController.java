package com.garasi.kita.inspection.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garasi.kita.inspection.DAO.RepoDao;
import com.garasi.kita.inspection.model.*;
import com.garasi.kita.inspection.service.ExportReportService;
import com.garasi.kita.inspection.service.InpectionDetailService;
import com.garasi.kita.inspection.service.PhotoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/mobile")
public class AndroidController {

    @Autowired
    InpectionDetailService inpectionDetailService;

    @Autowired
    PhotoItemService photoItemService;

    @Autowired
    ExportReportService exportReportService;

    @Value("${path.file.upload}")
    private String pathName;

    @Value("${path.file.url}")
    private String pathUrl;

    @Autowired
    private RepoDao dao;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam("name") String name,
                                        @RequestParam("pass") String pass, HttpServletRequest request) {
        HashMap<String, String> result = new HashMap<>();
        String password = dao.loginQuery(name, pass);
        if (password == null) {
            result = new HashMap<>();
            result.put("data", "User tidak terdaftar");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            if (pass.equalsIgnoreCase(password)) {
                result = new HashMap<>();
                result.put("data", "success");
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                result = new HashMap<>();
                result.put("data", "Password salah");
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }
    }

    @PostMapping("/getTask")
    public ResponseEntity<Object> getListTask(@RequestParam String name,
                                              HttpServletRequest request) {
        HashMap<String, Object> result = new HashMap<>();
        List<Inspection> listTask = dao.listTask(name);
        result.put("data", listTask);
        result.put("done", dao.taskDone(name));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/getTaskDetail")
    public ResponseEntity<Object> getTaskDetail(@RequestParam String name, @RequestParam String kodebooking,
                                                HttpServletRequest request) throws JsonProcessingException {
        HashMap<String, Object> model = new HashMap<>();

        model.put("inspection", dao.getDataInspection(kodebooking));
        model.put("inspectionDetail", dao.getDatainpectionDetailService(kodebooking));
        model.put("inspectionPhoto", listPhoto(kodebooking));

        return new ResponseEntity<>(model, HttpStatus.OK);
    }


    private List<PhotoItem> listPhoto(String kode) {
        List<PhotoItem> listPhotos = new ArrayList<>();
        for (PhotoItem pp : dao.getDataInspectionDetailPhoto(kode)) {
            listPhotos.add(pp);
        }
        return listPhotos;
    }

    @PostMapping("/gethistory")
    public ResponseEntity<Object> getHistory(@RequestParam String name, @RequestParam String start, @RequestParam String end,
                                             HttpServletRequest request) {
        HashMap<String, List<Inspection>> result = new HashMap<>();
        List<Inspection> listTask = dao.listHistoryTask(name, start, end);
        result.put("data", listTask);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/android")
    public ResponseEntity<Object> saveInspectionDetail(@RequestBody Map<String, List<InspectionDetail>> inspectionDetail,
                                                       HttpServletRequest request) {
        for (InspectionDetail inspectionDetail1 : inspectionDetail.get("inspectionDetail")) {
            dao.updateInspection(inspectionDetail1.getKodeBooking(), 1);

            int update = dao.update(inspectionDetail1);
            if (update == 0) {
                inpectionDetailService.saveData(inspectionDetail1);
            }
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("status", "next");
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/lastPage")
    public ResponseEntity<Object> lastPage(@RequestParam("kode") String kode, HttpServletRequest request) {
        dao.updateInspection(kode, 3);
        exportReportService.newReportDoc(kode);
        HashMap<String, String> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/delete/{kode_booking}/{id_field}")
    public ResponseEntity<Object> deleteFile(@RequestParam String name,
                                             HttpServletRequest request) {
        dao.removePhoto(name);
        HashMap<String, String> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/update/{kode_booking}/{id_field}")
    public ResponseEntity<Object> update(@RequestParam String name, @RequestParam String desc,
                                         HttpServletRequest request) {
        dao.updateCaption(name, desc);
        HashMap<String, String> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/photo/{kode_booking}/{id_field}")
    public ResponseEntity<Object> uploadImage(@PathVariable("kode_booking") String
                                                      kodeBooking, @PathVariable("id_field") String idField, @RequestParam String
                                                      androidPath, @RequestParam("image") MultipartFile file, @RequestParam String caption) throws IOException {
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
            photoItem.setPath(name);
            photoItem.setCaption(caption);

            photoItemService.saveData(photoItem);
            HashMap<String, String> result = new HashMap<>();
            result.put("status", "success");
            result.put("name", name);
            return ResponseEntity.accepted().body(result);
        } else {
            HashMap<String, String> result = new HashMap<>();
            result.put("status", "file not found");
            return ResponseEntity.badRequest().body(result);
        }

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

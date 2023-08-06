package com.garasi.kita.inspection.service;

import com.garasi.kita.inspection.DAO.RepoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RestService {

    @Autowired
    private RepoDao dao;

    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Value("${url.convert}")
    private String urlConvert;

    @Value("${path.file.output}")
    private String pathName;

    @Value("${path.file.outputpdf}")
    private String pathOutput;

    public void generatePdf(String kodeBooking) {
        try {
            String url = urlConvert;

            // create a map for post parameters
            Map<String, Object> map = new HashMap<>();
            map.put("source", pathName + "/" + kodeBooking + ".docx");
            map.put("target", pathOutput);
            map.put("fileName", kodeBooking);

            System.out.println("dox to pdf>1>" + pathName + kodeBooking + ".docx");
            System.out.println("dox to pdf>2>" + pathOutput);
            System.out.println("dox to pdf>3>" + kodeBooking);
            // build the request
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map);

            // send POST request
            ResponseEntity<String> response = this.restTemplate.postForEntity(url, entity, String.class);

            // check response status code
            if (response.getStatusCode() == HttpStatus.OK) {
                dao.updateInspection(kodeBooking, 6);
            } else {
                dao.updateInspection(kodeBooking, 9);
            }
        } catch (HttpStatusCodeException ex) {
            System.out.println(ex.getMessage());
            dao.updateInspection(kodeBooking, 9);
        }

    }
}
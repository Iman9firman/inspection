package com.garasi.kita.inspection.service;

import com.garasi.kita.inspection.DAO.RepoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    Logger logger = LoggerFactory.getLogger(RestService.class);

    public void generatePdf(String kodeBooking) {
        try {
            String url = urlConvert;

            Map<String, Object> map = new HashMap<>();
            map.put("source", pathName + "/" + kodeBooking + ".docx");
            map.put("target", pathOutput);
            map.put("fileName", kodeBooking);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map);
            
            ResponseEntity<String> response = this.restTemplate.postForEntity(url, entity, String.class);

            // check response status code
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("suskes pdf " + kodeBooking);
                dao.updateInspection(kodeBooking, 6);
            } else {
                logger.error("gagal pdf " + kodeBooking);
                dao.updateInspection(kodeBooking, 9);
            }
        } catch (HttpStatusCodeException ex) {
            logger.error("gagal pdf " + ex.getMessage() + "<<" + kodeBooking);
            dao.updateInspection(kodeBooking, 9);
        }

    }
}
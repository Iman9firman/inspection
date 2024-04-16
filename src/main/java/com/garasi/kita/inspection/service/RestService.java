package com.garasi.kita.inspection.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garasi.kita.inspection.DAO.RepoDao;
import com.garasi.kita.inspection.model.ResultDR;
import com.garasi.kita.inspection.model.ResultMessages;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
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

    @Autowired
    InpectionService inpectionService;

    public void postData(String url, String requestBodys, Long idMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("48lvVBKzWJG93ymRcExnKgT5kHky37R6ReVufxUI96s");

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodys, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        String responseBody = responseEntity.getBody();

        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);

        String status = "1";
        String uuid = "-";
        if (statusCode == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                ResultMessages resultMessages = mapper.readValue(responseBody, ResultMessages.class);
                status = resultMessages.getStatus().getState();
                uuid = resultMessages.getUmid();

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            status = "90";
        }

        dao.updateMessageById(idMessage, status, uuid);

        LoggerFactory.getLogger(RestService.class).info(statusCode + ">>" + responseBody);
    }


    public void getDR(String url, String requestBodys) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("48lvVBKzWJG93ymRcExnKgT5kHky37R6ReVufxUI96s");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        String responseBody = responseEntity.getBody();

        String status;
        String uuid = requestBodys;
        if (statusCode == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                ResultDR resultMessages = mapper.readValue(responseBody, ResultDR.class);
                status = resultMessages.getData().get(0).getStatus().getState();
                if (resultMessages.getData().get(0).getStatus().getErrorMessage() != null){
                    status += " - " + resultMessages.getData().get(0).getStatus().getErrorMessage();
                }

                uuid = resultMessages.getData().get(0).getUmid();

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            status = "90";
        }

        dao.updateMessageByUu(status, uuid);

        LoggerFactory.getLogger(RestService.class).info(statusCode + ">>" + responseBody);
    }


}
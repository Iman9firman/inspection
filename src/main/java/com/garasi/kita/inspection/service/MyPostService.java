package com.garasi.kita.inspection.service;

import com.garasi.kita.inspection.controller.InspectionController;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class MyPostService {
    @Autowired
    private RestTemplate restTemplate;

    public void postData(String url, String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("48lvVBKzWJG93ymRcExnKgT5kHky37R6ReVufxUI96s");

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

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

        LoggerFactory.getLogger(MyPostService.class).info(statusCode + ">>" + responseBody);
    }
}
package com.garasi.kita.inspection.service;

import com.garasi.kita.inspection.DAO.RepoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.http.HttpHeaders;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableScheduling
public class SchedullerJob {

    @Autowired
    private RepoDao dao;

    @Autowired
    ExportReportService exportReportService;

    @Autowired
    RestService restService;

    @Scheduled(cron = "*/5 * * * * *")
    public void resetMessageMtRegist() {
        Logger logger = LoggerFactory.getLogger(SchedullerJob.class);
        try {
            List<String> kodebooking = dao.getGenerateReport();
            for (String kb : kodebooking) {
                switch (kb.split(";")[1]) {
                    case "2":
                        logger.info("convertDoc >>" + kb.split(";")[0]);
                        dao.updateInspection(kb.split(";")[0], 3);
                        exportReportService.newReportDoc(kb.split(";")[0]);
                        break;
                    case "4":
                        logger.info("convertPdf >>" + kb.split(";")[0]);
                        System.out.println("dox to pdf");
                        dao.updateInspection(kb.split(";")[0], 5);
                        restService.generatePdf(kb.split(";")[0]);
                        break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}

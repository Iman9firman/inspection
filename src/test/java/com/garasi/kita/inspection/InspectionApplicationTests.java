package com.garasi.kita.inspection;

import com.garasi.kita.inspection.service.ExcelExportService;
import com.garasi.kita.inspection.service.ExportReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class InspectionApplicationTests {
    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private ExportReportService exportReportService;

    @Test
    void contextLoads() throws IOException {
	/*	try {
			//Resource resource=resourceLoader.getResource("template_report.docx");
			File file = new ClassPathResource("template_report.docx").getFile();
			//File file = resource.getFile();
			if (file.exists()){

				System.out.println("ada");
			}else {
				System.out.println("tidak ada");
			}


	} catch (Exception e) {
		e.printStackTrace();
	}*/


        // Ambil data dari database atau sumber lainnya
        List<String> dataList = new ArrayList<>();

        dataList.add("iman1");
        dataList.add("iman2");
        // Panggil metode ekspor data ke Excel
        //    excelExportService.updateDataInExcel(dataList);
        //   excelExportService.addDataToExcel("dari mana datangnya angin kalau bukan karena angin lalu yang bisa menjadikan sama dalam benruk sama");

        //excelExportService.newReportDoc();
        //excelExportService.createExcelWithAutoAdjustHeight();
        //excelExportService.createExcelWithAutoSizedRows();

        exportReportService.newReportDocV2("B2616ZKW-20230806024608");
        //excelExportService.createGridImage();
    }

}
//todo : insert image
//todo : top align
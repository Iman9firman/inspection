package com.garasi.kita.inspection.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class ExcelExportService {

    public void updateDataInExcel(List<String> dataList) {

        try (FileInputStream fileInputStream = new FileInputStream(new ClassPathResource("report_gki.xlsx").getFile())) {
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0); // Assuming the data is on the first sheet (index 0)

            setRow(sheet, 6, 0, "KODE BOOKING : 123123123");

            setRow(sheet, 9, 2, "123123123");
            setRow(sheet, 9, 8, "HITAM");

            setRow(sheet, 10, 2, "HONDA");
            setRow(sheet, 10, 8, "BENSIN");

            setRow(sheet, 18, 4, "salon 500.000 oli mesin + filter oli 500.000 oli transmisi 300.000");

            // Save the changes to the existing file
            try (FileOutputStream outputStream = new FileOutputStream("C:/Users/dartmedia/OneDrive/Documents/update_file-" + new Date().getTime() + ".xlsx")) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors that occur during file read/write
        }
    }

    private void setRow(XSSFSheet sheet, int row, int cell, String val) {
        Row rows = sheet.getRow(row);
        if (rows == null) {
            rows = sheet.createRow(row);
            rows.createCell(cell).setCellValue(val);
        } else {
            rows.getCell(cell).setCellValue(val);
        }

    }

    public void exportDataToExcel(List<String> dataList) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(new ClassPathResource("report_gki.xlsx").getFile())) {
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0); // Asumsikan sheet pertama adalah tempat data ingin dimasukkan

            // Menulis data ke template Excel

            sheet.getRow(4).getCell(6).setCellValue("Tanggal Inspeksi :" + new Date().toString());
            sheet.getRow(10).getCell(3).setCellValue("B 12312 KBB");


            // Simpan data ke file Excel
            try (FileOutputStream outputStream = new FileOutputStream("C:/Users/dartmedia/OneDrive/Documents/output_file-" + new Date().getTime() + ".xlsx")) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Tangani kesalahan yang terjadi saat membaca atau menulis file
        }
    }

    public void addDataToExcel(String newData) {

        try (FileInputStream fileInputStream = new FileInputStream(new ClassPathResource("report_gki.xlsx").getFile())) {
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0); // Assuming the data is on the first sheet (index 0)

            // Find the last used row index
            int lastUsedRowIndex = sheet.getLastRowNum();

            // Create a new row at the end and increment the index
            Row newRow = sheet.createRow(lastUsedRowIndex + 1);

            // Set cell values for the new row
            Cell cell1 = newRow.createCell(0);
            cell1.setCellValue(newData);
            CellStyle cs = workbook.createCellStyle();
            cs.setWrapText(true);   //Wrapping text


            // Continue with other cells as needed

            // Merge cells to create a single cell for the paragraph
            CellRangeAddress mergedRegion = new CellRangeAddress(
                    lastUsedRowIndex + 1, lastUsedRowIndex + 1, 0, 1
            );
            sheet.addMergedRegion(mergedRegion);

            // Create a drawing for the cell
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            //XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 2, lastUsedRowIndex + 1, 6, lastUsedRowIndex + 10);

            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 255, 255, (short) 4, 28, (short) 10, 31);
            anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);

            // Create a textbox in the drawing and add paragraphs with text
            XSSFTextBox textBox = drawing.createTextbox(anchor);
            textBox.addNewTextParagraph().addNewTextRun().setText("This is paragraph 2. sekian kali kita menciba menjadikan sama dan lelah ini setiap kali sama");

            // You can continue adding more paragraphs with different text as needed


            // Save the changes to the existing file
            try (FileOutputStream outputStream = new FileOutputStream("C:/Users/dartmedia/OneDrive/Documents/LLoutput_file-" + new Date().getTime() + ".xlsx")) {
                workbook.write(outputStream);
            }

            int lastColumn = anchor.getCol1() + anchor.getDx1();
            int lastRow = anchor.getRow1() + anchor.getDy1();

            // Print the last position (for demonstration purposes)

            Row newRow2 = sheet.createRow(lastRow);

            // Set cell values for the new row
            Cell cell3 = newRow2.createCell(0);
            cell3.setCellValue("Last Position: Row=" + lastRow);


            Row row = sheet.createRow(40);
            Cell cell = row.createCell(2);
            cell.setCellValue("Use \n with word wrap on to create a new line");


        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors that occur during file read/write
        }
    }


    public void createExcelWithAutoAdjustHeight() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // Create a cell style with wrap text and auto-adjust height properties
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            cellStyle.setVerticalAlignment(VerticalAlignment.TOP); // Align text to the top of the cell

            sheet.autoSizeColumn(0);
            float defaultColoum = ((2 * sheet.getColumnWidthInPixels(0)) / sheet.getDefaultColumnWidth());
            int columnWidthInCharacters = 15;
            sheet.setColumnWidth(0, columnWidthInCharacters * 256);

            // Create rows and cells and set the cell value
            for (int rowIndex = 0; rowIndex < 5; rowIndex++) {
                Row row = sheet.createRow(rowIndex);
                Cell cell = row.createCell(0);
                cell.setCellValue("This is a long text that will be auto-wrapped in the cell.");
                cell.setCellStyle(cellStyle);
                row.setHeightInPoints(defaultColoum);
            }


            // Save the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream("C:/Users/dartmedia/OneDrive/Documents/LLoutput_file-" + new Date().getTime() + ".xlsx")) {
                workbook.write(outputStream);
            }


        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors that occur during file read/write
        }
    }


    public void autoFill() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        String[] rowData = {
                "This is a long text that will cause the row height to adjust automatically.",
                "Short text.",
                "Another long text in this cell."
        };

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP); // Align text to the top of the cell

        for (int rowIndex = 0; rowIndex < rowData.length; rowIndex++) {
            Row row = sheet.createRow(rowIndex);
            Cell cell = row.createCell(0);
            cell.setCellValue(rowData[rowIndex]);
            cell.setCellStyle(cellStyle);

            // Set the row height to a larger value initially (optional)
            //row.setHeightInPoints(30); // You can set any initial height you prefer
        }

        // Enable auto-sizing for each row
        for (int col = 0; col < sheet.getRow(0).getLastCellNum(); col++) {
            sheet.autoSizeColumn(col);
        }

        try (FileOutputStream outputStream = new FileOutputStream("C:/Users/dartmedia/OneDrive/Documents/LLoutput_file-" + new Date().getTime() + ".xlsx")) {
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createExcelWithAutoSizeAndRowHeight() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // Create a cell style with wrap text property
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            cellStyle.setVerticalAlignment(VerticalAlignment.TOP); // Align text to the top of the cell

            // Create rows and cells and set the cell value
            for (int rowIndex = 0; rowIndex < 5; rowIndex++) {
                Row row = sheet.createRow(rowIndex);
                Cell cell = row.createCell(0);
                cell.setCellValue("This is a long text that will be auto-wrapped in the cell.");
                cell.setCellStyle(cellStyle);

                // Auto-adjust the row height based on the cell content
                //sheet.autoSizeColumn(0);
                row.setHeightInPoints((2 * sheet.getColumnWidthInPixels(0)) / sheet.getDefaultColumnWidth());
            }

            // Save the workbook to a file
            // Save the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream("C:/Users/dartmedia/OneDrive/Documents/LLoutput_file-" + new Date().getTime() + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors that occur during file read/write
        }
    }


    public void createGridImage() {
        try (XWPFDocument document = new XWPFDocument()) {
            // Create a new table with 3 rows and 2 columns
            int rows = 4;
            int cols = 3;
            XWPFTable table = document.createTable(rows, cols);

            // Add images and titles to the table cells
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    if (row % 2 == 1) {
                        XWPFTableCell cell = table.getRow(row).getCell(col);
                        XWPFParagraph paragraph = cell.addParagraph();

                        // Add the image to the paragraph
                        XWPFRun run = paragraph.createRun();
                        FileInputStream imageStream = new FileInputStream(new ClassPathResource("1686201435094_111_2.jpg").getFile());
                        run.addPicture(imageStream, Document.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(125), Units.toEMU(125));
                        imageStream.close();

                    } else {
                        String title = "Title " + (row + 1) + ", Column " + (col + 1);
                        XWPFTableCell cell = table.getRow(row).getCell(col);
                        XWPFParagraph paragraph = cell.addParagraph();
                        // Add the title to the paragraph
                        XWPFRun titleRun = paragraph.createRun();
                        titleRun.setText(title);

                    }
                }
            }

            // Save the document to a file
            // Save the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream("C:/Users/dartmedia/OneDrive/Documents/LLoutput_file-" + new Date().getTime() + ".docx")) {
                document.write(outputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int countLines(String text) {
        String[] lines = text.split("\r\n|\r|\n");
        return lines.length;
    }


    public void createExcelWithAutoSizedRows() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // Create a cell style with wrap text property
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);

            // Set the desired column width
            int columnWidthInCharacters = 30;
            sheet.setColumnWidth(0, columnWidthInCharacters * 256); // Multiply by 256 to convert characters to Excel width units

            // Create rows and cells and set the cell value
            for (int rowIndex = 0; rowIndex < 5; rowIndex++) {
                Row row = sheet.createRow(rowIndex);
                Cell cell = row.createCell(0);
                cell.setCellValue("This is a long text that will be auto-wrapped in the cell. This is a long text that will be auto-wrapped in the cell.");
                cell.setCellStyle(cellStyle);

                // Auto-adjust the row height based on the cell content and wrap text
                int numberOfLines = countLines(cell.getStringCellValue());
                row.setHeightInPoints((numberOfLines * sheet.getDefaultRowHeightInPoints()));
            }

            // Save the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream("C:/Users/dartmedia/OneDrive/Documents/LLoutput_file-" + new Date().getTime() + ".xlsx")) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors that occur during file read/write
        }
    }


    public void newReportDoc() {

        try (FileInputStream fileInputStream = new FileInputStream(new ClassPathResource("final_reportGKI.docx").getFile())) {

            XWPFDocument doc = new XWPFDocument(fileInputStream);

            for (XWPFTable table : doc.getTables()) {
                rr(table);

               /* List<XWPFTableRow> rows = table.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    XWPFTableRow row = rows.get(i);
                    for (int j = 0; j < row.getTableCells().size(); j++) {
                        XWPFTableCell cell = row.getCell(j);
                        // cell.setText(replaceValue(cell.getText()));
                        //  cell.addParagraph((XWPFParagraph) replace((XWPFParagraph) cell.getParagraphs()));
                        System.out.println(cell.getText());
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            System.out.println(p.getText());
                            //replace(p);
                        }
                    }
                }*/
            }


          /*  XWPFParagraph newParagraph = doc.createParagraph();
            int lastPosition = doc.getPosOfParagraph(newParagraph);

            System.out.println("1>>>" + lastPosition);
            XWPFTable clonedTable = doc.getTables().get(1);
            XWPFTableRow row = clonedTable.createRow();
            row.getCell(0).setText("Keterangan yang ini bisa dijaikan kendala jika sama");
            row.getCell(1).setText("Keterangan yang ini bisa dijaikan kendala jika samasss");
            row.getCell(2).setText("Keterangan yang ini bisa dijaikan kendala jika sama22");
            clonedTable.addRow(row);
            row.getCell(0).setText("photo0");
            row.getCell(1).setText("photo1");
            row.getCell(2).setText("photo2");

            for (int j = 0; j < row.getTableCells().size(); j++) {
                XWPFTableCell cell = row.getCell(j);
                if (cell.getTables().size() > 0) {
                    rr2(cell.getTables());
                } else {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        replace(p);
                    }
                }
            }


            // Add the cloned table to the document (optional)
            doc.insertTable(lastPosition, clonedTable);

            //Write first Text in the beginning
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            run.setText("Hi");

            int lastPosition2 = doc.getPosOfParagraph(para);
            System.out.println("2>>>" + lastPosition2);
*/
            try (FileOutputStream outputStream = new FileOutputStream("C:/Users/dartmedia/OneDrive/Documents/LLoutput_file-" + new Date().getTime() + ".docx")) {
                doc.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors that occur during file read/write
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static XWPFTable cloneTable(XWPFTable originalTable, XWPFDocument doc) throws Exception {

        int rows = originalTable.getNumberOfRows();
        int cols = originalTable.getRow(0).getTableCells().size();

        // Create a new table with the same number of rows and columns
        XWPFTable clonedTable = doc.createTable(rows, cols);

        // Loop through each cell and copy content
        for (int rowIdx = 0; rowIdx < rows; rowIdx++) {
            XWPFTableRow originalRow = originalTable.getRow(rowIdx);
            XWPFTableRow clonedRow = clonedTable.getRow(rowIdx);

            for (int colIdx = 0; colIdx < cols; colIdx++) {
                XWPFTableCell originalCell = originalRow.getCell(colIdx);
                XWPFTableCell clonedCell = clonedRow.getCell(colIdx);

                // Clear the contents of the cloned cell
                clonedCell.getParagraphs().forEach(p -> p.getRuns().forEach(r -> r.setText("", 0)));

                // Copy the content from the original cell to the cloned cell
                for (XWPFParagraph paragraph : originalCell.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        clonedCell.getParagraphArray(0).createRun().setText(run.getText(0));
                    }
                }
            }
        }

        return clonedTable;
    }


    private String replaceValue(String param) {
        String value = "";
        if (param.equalsIgnoreCase("satu")) {
            value = "iman";
        } else if (param.equalsIgnoreCase("dua")) {
            value = "darimana dapta damsda asdmadsjasd asd a sdasdajsd asd asd asdiasdiasjda dadsasdasidads asdasdjiadsj asidasdasd aisdjads ";
        }
        return value;
    }

    private void replace(XWPFParagraph p) throws Exception {
        HashMap<String, String> value = new HashMap<>();
        value.put("nopol", "B1239ZKK");
        value.put("merk", "HOnda");
        value.put("model", "BRV");
        value.put("transmisi", "AT");
        value.put("tahun", "2012");
        value.put("isiSilinder", "2000");
        value.put("nomorRangka", "MSDNKJ123MDN");
        value.put("warna", "HITAM");
        value.put("bahanBakar", "BENSIN");
        value.put("odometer", "1231200");
        value.put("pajakBerlaku", "2023");
        value.put("kepemilikan", "Perusahaan");
        value.put("pemilik", "1");
        value.put("kmservice", "1123");

        value.put("catatanFiturKendaraan", "Catatan : dari mana datangnnya ini dari mana aja dah");
        value.put("airbag", "ada");
        value.put("powerWindow", "ada");
        value.put("sistemAc", "ada");
        value.put("electrikMirror", "ada");
        value.put("sistemAudio", "ada");
        value.put("esp", "ada");
        value.put("centralLock", "ada");


        value.put("estimasiPerawatan", "ESTIMASI PERAWATAN :\nmobil dalam kondisi baik,tidak ada indikasi tabrak\n" +
                "dan banjir,mesin dan kaki-kaki baik tidak ada oli\n" +
                "yang rembes maupun karet-karet yang sobek,minor\n" +
                "wajar ex pemakaian saja");
        value.put("estimasiPerbaikan", "ESTIMASI PERBAIKAN :\nmobil dalam kondisi baik,tidak ada indikasi tabrak\n" +
                "dan banjir,mesin dan kaki-kaki baik tidak ada oli\n" +
                "yang rembes maupun karet-karet yang sobek,minor\n" +
                "wajar ex pemakaian saja");
        value.put("kesimpulan", "KESIMPULAN :\nmobil dalam kondisi baik,tidak ada indikasi tabrak\n" +
                "dan banjir,mesin dan kaki-kaki baik tidak ada oli\n" +
                "yang rembes maupun karet-karet yang sobek,minor\n" +
                "wajar ex pemakaian saja");


        String text = p.getText();
        if (text == null || text.isEmpty()) {
            text = p.getParagraphText();
        }

        if (text.startsWith("${")) {
            text = value.get(text.replace("$", "").replace("{", "").replace("}", ""));
            List<XWPFRun> runs = p.getRuns();
            for (int i = runs.size() - 1; i > 0; i--) {
                p.removeRun(i);
            }
            XWPFRun run = runs.get(0);
            run.setText(text, 0);
        }


       /* if (text.startsWith("http")){

        }else{

        }*/

       /* if (text.equalsIgnoreCase("$kodeBooking")) {
            text = "KODE BOOKING : 12312313123-KSKDJK";
            List<XWPFRun> runs = p.getRuns();
            for (int i = runs.size() - 1; i > 0; i--) {
                p.removeRun(i);
            }
            XWPFRun run = runs.get(0);
            run.setText(text, 0);

        } else if (text.equalsIgnoreCase("$tanggalInspeksi")) {
            text = "Tanggal inspeksi : 28/07/2023";
            List<XWPFRun> runs = p.getRuns();
            for (int i = runs.size() - 1; i > 0; i--) {
                p.removeRun(i);
            }
            XWPFRun run = runs.get(0);
            run.setText(text, 0);
        } else if (text.equalsIgnoreCase("$imgIcon")) {
            System.out.println("masuk sini icon");
            List<XWPFRun> runs = p.getRuns();
            for (XWPFRun r : runs) {        // Random strings, split results from p.getText()
                r.setText("", 0);           // Clear any raw value
                FileInputStream imageStream = new FileInputStream(new ClassPathResource("1686201435094_111_2.jpg").getFile());
                r.addPicture(imageStream, Document.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(100), Units.toEMU(100));
                imageStream.close();
            }
        }

        Object result = text;
*/
        //      return result;
    }

    private void rr2(List<XWPFTable> tables) throws Exception {
        for (XWPFTable nestedTable : tables) {
            for (XWPFTableRow row : nestedTable.getRows()) {
                for (XWPFTableCell nestedCell : row.getTableCells()) {
                    for (XWPFParagraph p : nestedCell.getParagraphs()) {
                        replace(p);
                    }
                }
            }
        }
    }

    private void rr(XWPFTable table) throws Exception {
        List<XWPFTableRow> rows = table.getRows();
        for (int i = 0; i < rows.size(); i++) {
            XWPFTableRow row = rows.get(i);
            for (int j = 0; j < row.getTableCells().size(); j++) {
                XWPFTableCell cell = row.getCell(j);
                if (cell.getTables().size() > 0) {
                    rr2(cell.getTables());
                } else {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        replace(p);
                    }
                }
            }
        }

    }


        /*model.forEach((k, v) -> {
            if (v instanceof Integer) {
                Integer theV = (Integer) v;
                System.out.println(k + " -> "
                        + String.format("The value is a %s integer: %d", theV > 0 ? "positive" : "negative", theV));
            } else if (v instanceof int[]) {
                int[] theV = (int[]) v;
                System.out.println(k + " -> "
                        + String.format("The value is an array of %d integers: %s", theV.length, Arrays.toString(theV)));
            } else if (v instanceof List) {
                System.out.println(k + "-->>" + list);
            } else {
                System.out.println(k + " -> ");
            }
        });
*/
}
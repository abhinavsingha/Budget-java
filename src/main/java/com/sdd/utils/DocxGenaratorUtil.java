package com.sdd.utils;


import com.sdd.exception.SDDException;
import com.sdd.response.CDAReportResponse;
import com.sdd.response.CDAReportSubResponse;
import com.sdd.response.CbReportResponse;
import com.sdd.response.FilePathResponse;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DocxGenaratorUtil {
    @Autowired
    private TemplateEngine templateEngine;


    @SuppressWarnings("rawtypes")
    public void createDocAllocation(HashMap<String, List<ReportSubModel>> hashMap, String path, FilePathResponse filePathResponse) throws Exception {

        try {


            XWPFDocument document = new XWPFDocument();
            FileOutputStream out = new FileOutputStream(new File(path));

            XWPFTable table = document.createTable();
            table.setWidth("100%");
//                XWPFParagraph para = document.createParagraph();
//                XWPFRun run = para.createRun();


            XWPFTableRow tableRowOne = table.getRow(0);
            XWPFParagraph paragraphtableRowOne = tableRowOne.getCell(0).addParagraph();
            boldText(paragraphtableRowOne.createRun(), 10, "SUB HEAD", true);

            XWPFParagraph paragraphtableRowOne1 = tableRowOne.addNewTableCell().addParagraph();
            boldText(paragraphtableRowOne1.createRun(), 10, "UNIT NAME", true);

            XWPFParagraph paragraphtableRowOne11 = tableRowOne.addNewTableCell().addParagraph();
            boldText(paragraphtableRowOne11.createRun(), 10, filePathResponse.getType() + " (" + filePathResponse.getFinYear() + ") \n" + " ALLOCATION (In " + filePathResponse.getAmountType() + ")", true);

            double grandTotal = 0;
            for (Map.Entry<String, List<ReportSubModel>> entry11 : hashMap.entrySet()) {
                String key11 = entry11.getKey();
                List<ReportSubModel> tabData11 = entry11.getValue();

                XWPFTableRow tableRow = table.createRow();
                tableRow.getCell(0).setText(key11);
                double allAmountData = 0;

                for (Integer i = 0; i < tabData11.size(); i++) {

                    if (i == 0) {
//                            tableRow.getCell(1).setText(tabData11.get(i).getUnit());
                        XWPFParagraph paragraph = tableRow.getCell(1).addParagraph();
                        normalText(paragraph.createRun(), 10, tabData11.get(i).getUnit(), false);

//                            tableRow.getCell(2).setText(tabData11.get(i).getAmount());
                        XWPFParagraph paragraph11 = tableRow.getCell(2).addParagraph();
                        normalText(paragraph11.createRun(), 10, tabData11.get(i).getAmount(), false);

                    } else {
                        XWPFTableRow tableRow11 = table.createRow();
                        tableRow11.getCell(0).setText("");

                        XWPFParagraph paragraph = tableRow11.getCell(1).addParagraph();
                        normalText(paragraph.createRun(), 10, tabData11.get(i).getUnit(), false);

                        XWPFParagraph paragraph11 = tableRow11.getCell(2).addParagraph();
                        normalText(paragraph11.createRun(), 10, tabData11.get(i).getAmount(), false);
                    }

                    allAmountData = allAmountData + Double.parseDouble(tabData11.get(i).getAmount());
                    grandTotal = grandTotal + Double.parseDouble(tabData11.get(i).getAmount());

                    XWPFTableRow latRow = table.createRow();
                    XWPFParagraph total1 = latRow.getCell(1).addParagraph();
                    boldText(total1.createRun(), 10, "Total Amount", true);
                    XWPFParagraph total1111 = latRow.getCell(2).addParagraph();
                    boldText(total1111.createRun(), 10, allAmountData + "", true);

                }
            }


            XWPFTableRow latRow = table.createRow();
            XWPFParagraph total1 = latRow.getCell(0).addParagraph();
            boldText(total1.createRun(), 10, "Grand Total", true);
            XWPFParagraph total1111 = latRow.getCell(2).addParagraph();
            boldText(total1111.createRun(), 10, grandTotal + "", true);


            XWPFParagraph mainParagraph = document.createParagraph();
            mainParagraph = document.createParagraph();
            mainParagraph.createRun().addBreak();
            mainParagraph = document.createParagraph();
            boldText(mainParagraph.createRun(), 10, filePathResponse.getApproveName() + "", true);
            mainParagraph = document.createParagraph();
            normalText(mainParagraph.createRun(), 10, filePathResponse.getApproveRank() + "", true);

            document.write(out);


            out.close();
            document.close();


        } catch (Exception e) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "Error occurred");
        }
    }


    private void boldText(XWPFRun run, int fontSize, String text, boolean bold) {
        run.setFontFamily("Calibre LIght");
        run.setFontSize(fontSize);
        run.setColor("000000");
        run.setText(text);
        run.setBold(bold);

    }

    private void normalText(XWPFRun run, int fontSize, String text, boolean bold) {
        run.setFontFamily("Calibre LIght");
        run.setFontSize(fontSize);
        run.setColor("000000");
        run.setText(text);
        run.setBold(bold);
    }
}

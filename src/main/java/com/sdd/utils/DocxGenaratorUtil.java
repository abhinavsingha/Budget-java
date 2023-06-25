package com.sdd.utils;


import com.sdd.exception.SDDException;
import com.sdd.response.CDAReportResponse;
import com.sdd.response.CDAReportSubResponse;
import com.sdd.response.FilePathResponse;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


import java.io.*;
import java.util.*;
import java.util.List;

@Component
public class DocxGenaratorUtil {


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
                        normalText(paragraph11.createRun(), 10, ConverterUtils.addDecimalPoint(tabData11.get(i).getAmount()), false);

                    } else {
                        XWPFTableRow tableRow11 = table.createRow();
                        tableRow11.getCell(0).setText("");

                        XWPFParagraph paragraph = tableRow11.getCell(1).addParagraph();
                        normalText(paragraph.createRun(), 10, tabData11.get(i).getUnit(), false);

                        XWPFParagraph paragraph11 = tableRow11.getCell(2).addParagraph();
                        normalText(paragraph11.createRun(), 10, ConverterUtils.addDecimalPoint(tabData11.get(i).getAmount()), false);
                    }

                    allAmountData = allAmountData + Double.parseDouble(tabData11.get(i).getAmount());
                    grandTotal = grandTotal + Double.parseDouble(tabData11.get(i).getAmount());

                }

                XWPFTableRow latRow = table.createRow();
                XWPFParagraph total1 = latRow.getCell(1).addParagraph();
                boldText(total1.createRun(), 10, "Total Amount", true);
                XWPFParagraph total1111 = latRow.getCell(2).addParagraph();
                boldText(total1111.createRun(), 10, ConverterUtils.addDecimalPoint(allAmountData + ""), true);

            }


            XWPFTableRow latRow = table.createRow();
            XWPFParagraph total1 = latRow.getCell(0).addParagraph();
            boldText(total1.createRun(), 10, "Grand Total", true);
            XWPFParagraph total1111 = latRow.getCell(2).addParagraph();
            boldText(total1111.createRun(), 10, ConverterUtils.addDecimalPoint(grandTotal + ""), true);


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
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), e.toString());
        }
    }


    @SuppressWarnings("rawtypes")
    public void createDocRecipt(HashMap<String, List<ReportSubModel>> hashMap, String path, FilePathResponse filePathResponse) throws Exception {

        try {


            XWPFDocument document = new XWPFDocument();
            FileOutputStream out = new FileOutputStream(new File(path));

            XWPFTable table = document.createTable();
            table.setWidth("100%");
//                XWPFParagraph para = document.createParagraph();
//                XWPFRun run = para.createRun();


            XWPFTableRow tableRowOne = table.getRow(0);
            XWPFParagraph paragraphtableRowOne = tableRowOne.getCell(0).addParagraph();
            boldText(paragraphtableRowOne.createRun(), 10, "MAJOR/MINOR/SUB HEAD", true);

            XWPFParagraph paragraphtableRowOne1 = tableRowOne.addNewTableCell().addParagraph();
            boldText(paragraphtableRowOne1.createRun(), 10, "DETAILED HEAD", true);

            XWPFParagraph paragraphtableRowOne11 = tableRowOne.addNewTableCell().addParagraph();
            boldText(paragraphtableRowOne11.createRun(), 10, filePathResponse.getType() + " (" + filePathResponse.getFinYear() + ") \n" + " ALLOCATION (In " + filePathResponse.getAmountType() + ")", true);


            XWPFTableRow tableRowOne11 = table.createRow();
            XWPFParagraph paragraphtableRowOne22 = tableRowOne11.getCell(0).addParagraph();
            boldText(paragraphtableRowOne22.createRun(), 10, filePathResponse.getSubHeadKey(), true);

            XWPFParagraph paragraphtableRowOne111 = tableRowOne11.getCell(1).addParagraph();
            boldText(paragraphtableRowOne111.createRun(), 10, filePathResponse.getRevenueOrCapital(), true);

            XWPFParagraph paragraphtableRowOne1111 = tableRowOne11.getCell(2).addParagraph();
            boldText(paragraphtableRowOne1111.createRun(), 10, "", true);


            double grandTotal = 0;
            for (Map.Entry<String, List<ReportSubModel>> entry11 : hashMap.entrySet()) {
                String key11 = entry11.getKey();
                List<ReportSubModel> tabData11 = entry11.getValue();

//                XWPFTableRow tableRow = table.createRow();
//                tableRow.getCell(0).setText(key11);

                double allAmountData = 0;
                for (Integer i = 0; i < tabData11.size(); i++) {

                    XWPFTableRow tableRow11 = table.createRow();
                    tableRow11.getCell(0).setText("");

                    XWPFParagraph paragraph = tableRow11.getCell(1).addParagraph();
                    normalText(paragraph.createRun(), 10, key11, false);

                    XWPFParagraph paragraph11 = tableRow11.getCell(2).addParagraph();
                    normalText(paragraph11.createRun(), 10, ConverterUtils.addDecimalPoint(tabData11.get(i).getAmount()), false);

                    allAmountData = allAmountData + Double.parseDouble(tabData11.get(i).getAmount());
                    grandTotal = grandTotal + Double.parseDouble(tabData11.get(i).getAmount());

                }

            }


            XWPFTableRow latRow = table.createRow();
            XWPFParagraph total1 = latRow.getCell(1).addParagraph();
            boldText(total1.createRun(), 10, "Grand Total" + "(" + filePathResponse.getRevenueOrCapital() + ")", true);
            XWPFParagraph total1111 = latRow.getCell(2).addParagraph();
            boldText(total1111.createRun(), 10, ConverterUtils.addDecimalPoint(grandTotal + ""), true);


            XWPFParagraph mainParagraph = document.createParagraph();
            mainParagraph = document.createParagraph();
            mainParagraph.createRun().addBreak();

            mainParagraph = document.createParagraph();
            boldText(mainParagraph.createRun(), 10, filePathResponse.getApproveName() + "", true);
            mainParagraph.setAlignment(ParagraphAlignment.RIGHT);

            mainParagraph = document.createParagraph();
            normalText(mainParagraph.createRun(), 10, filePathResponse.getApproveRank() + "", true);
            mainParagraph.setAlignment(ParagraphAlignment.RIGHT);


            document.write(out);
            out.close();
            document.close();


        } catch (Exception e) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), e.toString());
        }
    }


    @SuppressWarnings("rawtypes")
    public void createDocConsolidateRecipt(HashMap<String, List<ReportSubModel>> hashMap, String path, FilePathResponse filePathResponse) throws Exception {

        try {


            XWPFDocument document = new XWPFDocument();
            FileOutputStream out = new FileOutputStream(new File(path));

            XWPFTable table = document.createTable();
            table.setWidth("100%");
//                XWPFParagraph para = document.createParagraph();
//                XWPFRun run = para.createRun();


            XWPFTableRow tableRowOne = table.getRow(0);
            XWPFParagraph paragraphtableRowOne = tableRowOne.getCell(0).addParagraph();
            boldText(paragraphtableRowOne.createRun(), 10, "MAJOR/MINOR/SUB HEAD", true);

            XWPFParagraph paragraphtableRowOne1 = tableRowOne.addNewTableCell().addParagraph();
            boldText(paragraphtableRowOne1.createRun(), 10, "DETAILED HEAD", true);

            XWPFParagraph paragraphtableRowOne11 = tableRowOne.addNewTableCell().addParagraph();
            boldText(paragraphtableRowOne11.createRun(), 10, filePathResponse.getType() + " (" + filePathResponse.getFinYear() + ") \n" + " ALLOCATION (In " + filePathResponse.getAmountType() + ")", true);


//            XWPFTableRow tableRowOne11 = table.getRow(0);
//            XWPFParagraph paragraphtableRowOne22 = tableRowOne11.getCell(0).addParagraph();
//            boldText(paragraphtableRowOne22.createRun(), 10, filePathResponse.getSubHeadKey(), true);
//
//            XWPFParagraph paragraphtableRowOne111 = tableRowOne11.addNewTableCell().addParagraph();
//            boldText(paragraphtableRowOne111.createRun(), 10, filePathResponse.getRevenueOrCapital(), true);
//
//            XWPFParagraph paragraphtableRowOne1111 = tableRowOne11.addNewTableCell().addParagraph();
//            boldText(paragraphtableRowOne1111.createRun(), 10, "", true);


            double grandTotal = 0;
            for (Map.Entry<String, List<ReportSubModel>> entry11 : hashMap.entrySet()) {
                String key11 = entry11.getKey();
                List<ReportSubModel> tabData11 = entry11.getValue();

                XWPFTableRow tableRow = table.createRow();
                XWPFParagraph tableView = tableRow.getCell(0).addParagraph();
                boldText(tableView.createRun(), 10, key11, true);

                if (key11.equalsIgnoreCase("2037")) {
                    XWPFParagraph tableView11 = tableRow.getCell(1).addParagraph();
                    boldText(tableView11.createRun(), 10, "REVENUE", true);
                } else {
                    XWPFParagraph tableView22 = tableRow.getCell(1).addParagraph();
                    boldText(tableView22.createRun(), 10, "CAPITAL", true);
                }


                double allAmountData = 0;
                for (Integer i = 0; i < tabData11.size(); i++) {

                    XWPFTableRow tableRow11 = table.createRow();
                    tableRow11.getCell(0).setText("");

                    XWPFParagraph paragraph = tableRow11.getCell(1).addParagraph();
                    normalText(paragraph.createRun(), 10, tabData11.get(i).getBudgetHead().getSubHeadDescr(), false);

                    XWPFParagraph paragraph11 = tableRow11.getCell(2).addParagraph();
                    normalText(paragraph11.createRun(), 10, ConverterUtils.addDecimalPoint(tabData11.get(i).getAmount()), false);


                    allAmountData = allAmountData + Double.parseDouble(tabData11.get(i).getAmount());
                    grandTotal = grandTotal + Double.parseDouble(tabData11.get(i).getAmount());

                }

                if (key11.equalsIgnoreCase("2037")) {

                    XWPFTableRow latRow = table.createRow();
                    XWPFParagraph total1 = latRow.getCell(1).addParagraph();
                    boldText(total1.createRun(), 10, "Total Amount" + "(REVENUE)", true);
                    XWPFParagraph total1111 = latRow.getCell(2).addParagraph();
                    boldText(total1111.createRun(), 10, ConverterUtils.addDecimalPoint(allAmountData + ""), true);

                } else {

                    XWPFTableRow latRow = table.createRow();
                    XWPFParagraph total1 = latRow.getCell(1).addParagraph();
                    boldText(total1.createRun(), 10, "Total Amount" + "(CAPITAL)", true);
                    XWPFParagraph total1111 = latRow.getCell(2).addParagraph();
                    boldText(total1111.createRun(), 10, ConverterUtils.addDecimalPoint(allAmountData + ""), true);
                }
            }


            XWPFTableRow latRow = table.createRow();
            XWPFParagraph total1 = latRow.getCell(1).addParagraph();
            boldText(total1.createRun(), 10, "Grand Total" + "(" + filePathResponse.getRevenueOrCapital() + ")", true);
            XWPFParagraph total1111 = latRow.getCell(2).addParagraph();
            boldText(total1111.createRun(), 10, ConverterUtils.addDecimalPoint(grandTotal + ""), true);


            XWPFParagraph mainParagraph = document.createParagraph();
            mainParagraph = document.createParagraph();
            mainParagraph.createRun().addBreak();

            mainParagraph = document.createParagraph();
            boldText(mainParagraph.createRun(), 10, filePathResponse.getApproveName() + "", true);
            mainParagraph.setAlignment(ParagraphAlignment.RIGHT);

            mainParagraph = document.createParagraph();
            normalText(mainParagraph.createRun(), 10, filePathResponse.getApproveRank() + "", true);
            mainParagraph.setAlignment(ParagraphAlignment.RIGHT);


            document.write(out);
            out.close();
            document.close();


        } catch (Exception e) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), e.toString());
        }
    }


    public void createCdaMainReportDoc(HashMap<String, List<CDAReportResponse>> map, CDAReportSubResponse cadSubReport, String path, Float grandTotal, HashMap<String, String> coloumWiseAmount) throws Exception {

        try {

            XWPFDocument document = new XWPFDocument();
            FileOutputStream out = new FileOutputStream(new File(path));

            CTDocument1 ctDocument = document.getDocument();
            CTBody ctBody = ctDocument.getBody();
            CTSectPr ctSectPr = (ctBody.isSetSectPr()) ? ctBody.getSectPr() : ctBody.addNewSectPr();
            CTPageSz ctPageSz = (ctSectPr.isSetPgSz()) ? ctSectPr.getPgSz() : ctSectPr.addNewPgSz();
            ctPageSz.setOrient(STPageOrientation.LANDSCAPE);

            List<CDAReportResponse> tabData1 = map.get("Sub Head");

            if (tabData1.size() > 12) {
                ctPageSz.setW(java.math.BigInteger.valueOf(Math.round(120 * 1440))); //11 inches
                ctPageSz.setH(java.math.BigInteger.valueOf(Math.round(8.5 * 1440))); //8.5 inches
            }


            XWPFParagraph mainParagraph = document.createParagraph();
            mainParagraph.createRun().addBreak();

            mainParagraph = document.createParagraph();
            mainParagraph.setAlignment(ParagraphAlignment.CENTER);
            boldText(mainParagraph.createRun(), 20, "\n" + "CDA WISE/OBJECT HEAD WISE CONTROL FIGURES FOR " + cadSubReport.getAllocationType() + " " + cadSubReport.getFinYear(), true);
            mainParagraph.createRun().addBreak();

            mainParagraph = document.createParagraph();
            mainParagraph.setAlignment(ParagraphAlignment.CENTER);
            boldText(mainParagraph.createRun(), 20, "REVENUE", true);
            mainParagraph.createRun().addBreak();


            mainParagraph = document.createParagraph();
            mainParagraph.setAlignment(ParagraphAlignment.CENTER);
            boldText(mainParagraph.createRun(), 20, "Major Head " + cadSubReport.getMajorHead() + ". Sub Major Head 00. Minor Head " + cadSubReport.getMinorHead() + ") (In " + cadSubReport.getAmountType() + ")", true);

            mainParagraph.createRun().addBreak();
            mainParagraph.createRun().addBreak();
            mainParagraph.createRun().addBreak();

            XWPFTable table = document.createTable();
            table.setWidth("100%");


            XWPFTableRow tableRowOne = table.getRow(0);
            XWPFParagraph paragraphtableRowOne = tableRowOne.getCell(0).addParagraph();
            boldText(paragraphtableRowOne.createRun(), 10, "object", true);


            for (Integer i = 0; i < tabData1.size(); i++) {
                XWPFParagraph paragraphtableRowOne1 = tableRowOne.addNewTableCell().addParagraph();
                boldText(paragraphtableRowOne1.createRun(), 10, ConverterUtils.addDecimalPoint(tabData1.get(i).getName()), true);
            }

            for (Map.Entry<String, List<CDAReportResponse>> entry : map.entrySet()) {
                String key = entry.getKey();

                if (!key.equalsIgnoreCase("Sub Head")) {
                    List<CDAReportResponse> tabData = entry.getValue();
                    XWPFTableRow tableRow11 = table.createRow();
//                    tableRow11.getCell(0).setText(key);
                    XWPFParagraph paragraphtableRowOne11 = tableRow11.getCell(0).addParagraph();
                    boldText(paragraphtableRowOne11.createRun(), 10, key, true);


                    for (Integer i = 0; i < tabData.size(); i++) {
                        XWPFParagraph paragraph11 = tableRow11.getCell(i + 1).addParagraph();
                        normalText(paragraph11.createRun(), 10, ConverterUtils.addDecimalPoint(tabData.get(i).getName()), false);
                    }
                }
            }


            int ih = 0;
            XWPFTableRow tableRow11 = table.createRow();
            tableRow11.getCell(ih).setText("Grand Total");

//            for (Integer i = 0; i < tabData1.size(); i++) {
//                if (i == (tabData1.size() - 1)) {
//                    } else {
//
//                }
//            }

            for (Map.Entry<String, String> entry : coloumWiseAmount.entrySet()) {
                String tabData = entry.getValue();
                XWPFParagraph paragraph11 = tableRow11.getCell(ih + 1).addParagraph();
                normalText(paragraph11.createRun(), 10, tabData, false);
                ih++;
            }


            XWPFParagraph paragraph11 = tableRow11.getCell(ih + 1).addParagraph();
            normalText(paragraph11.createRun(), 10, ConverterUtils.addDecimalPoint(grandTotal + ""), false);


            document.write(out);
            out.close();
            document.close();

        } catch (Exception e) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), e.toString());
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

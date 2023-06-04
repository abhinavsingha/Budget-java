package com.sdd.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.sdd.response.CDAReportResponse;
import com.sdd.response.CDAReportSubResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sdd.response.FilePathResponse;
import org.springframework.util.Assert;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.List;

@Component
public class PdfGenaratorUtilMain {

    private static final String UTF_8 = "UTF-8";

    @SuppressWarnings("rawtypes")
    public void createPdfAllocation(HashMap<String, List<ReportSubModel>> hashMap, String path, FilePathResponse filePathResponse) throws Exception {

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();


        float[] pointColumnWidths = {150F, 150F, 150F};
        PdfPTable table = new PdfPTable(3);
        table.setWidths(pointColumnWidths);
        table.setSpacingAfter(20);

        table.addCell(boldText("SUB HEAD", 8, 25f));
        table.addCell(boldText("UNIT NAME", 8, 25f));
        table.addCell(boldText(filePathResponse.getType() + " (" + filePathResponse.getFinYear() + ") \n" + " ALLOCATION (In " + filePathResponse.getAmountType() + ")", 8, 25f));

        double grandTotal = 0;
        for (Map.Entry<String, List<ReportSubModel>> entry11 : hashMap.entrySet()) {
            String key11 = entry11.getKey();
            List<ReportSubModel> tabData11 = entry11.getValue();

            table.addCell(normalText(key11, 8, 25f));


            for (Integer i = 0; i < tabData11.size(); i++) {
                double allAmountData = 0;
                if (i == 0) {
                    table.addCell(normalText(tabData11.get(i).getUnit(), 8, 25f));
                    table.addCell(normalText(ConverterUtils.addDecimalPoint(tabData11.get(i).getAmount()), 8, 25f));
                } else {
                    table.addCell(normalText("", 8, 25f));
                    table.addCell(normalText(ConverterUtils.addDecimalPoint(tabData11.get(i).getUnit()), 8, 25f));
                    table.addCell(normalText(ConverterUtils.addDecimalPoint(tabData11.get(i).getAmount()), 8, 25f));
                }
                allAmountData = allAmountData + Double.parseDouble(tabData11.get(i).getAmount());

                table.addCell(boldText("", 8, 25f));
                table.addCell(boldText(ConverterUtils.addDecimalPoint("Total Amount"), 8, 25f));
                table.addCell(boldText(ConverterUtils.addDecimalPoint(allAmountData + ""), 8, 25f));

                grandTotal = grandTotal + Double.parseDouble(tabData11.get(i).getAmount());
            }
        }

        table.addCell(boldText(ConverterUtils.addDecimalPoint("Grand Total"), 8, 25f));
        table.addCell(boldText("", 8, 25f));
        table.addCell(boldText(ConverterUtils.addDecimalPoint(grandTotal + ""), 8, 25f));


        Phrase phrase = new Phrase();
        Font font = new Font(Font.FontFamily.COURIER, 8, Font.BOLD);
        Chunk approverName = new Chunk(""+(filePathResponse.getApproveName() + "\n" + filePathResponse.getApproveRank()), font);
        phrase.add(approverName);
        Paragraph paragraph = new Paragraph();
        paragraph.add(phrase);
        paragraph.setAlignment(Element.ALIGN_BOTTOM);

        document.add(table);
        document.add(paragraph);
        document.close();

    }


    public void createCdaMainReport(HashMap<String, List<CDAReportResponse>> map, CDAReportSubResponse cadSubReport, String path, Float grandTotal) throws Exception {


        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();
        document.newPage();


        Font font = new Font(Font.FontFamily.COURIER, 15, Font.BOLD);
        Chunk header = new Chunk("\n" + "CDA WISE/OBJECT HEAD WISE CONTROL FIGURES FOR " + cadSubReport.getAllocationType() + " " + cadSubReport.getFinYear() + "\n" + "\n", font);
        Paragraph preface = new Paragraph();
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(header);

        Chunk revenue = new Chunk("REVENUE" + "\n" + "\n", font);
        preface.add(revenue);

        Chunk thiredHead = new Chunk("Major Head " + cadSubReport.getMajorHead() + ". Sub Major Head 00. Minor Head " + cadSubReport.getMinorHead() + ") (In " + cadSubReport.getAmountType() + ")" + "\n" + "\n" + "\n" + "\n", font);
        preface.add(thiredHead);


        List<CDAReportResponse> tabData1 = map.get("Sub Head");
//        float[] pointColumnWidths = {50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F};
        PdfPTable table = new PdfPTable(tabData1.size() + 1);
        table.setWidthPercentage(100);
//        table.setWidths(pointColumnWidths);
        table.setSpacingAfter(10);


        table.addCell(boldText("object", 6, 35f));
        for (Integer i = 0; i < tabData1.size(); i++) {
            table.addCell(boldText(tabData1.get(i).getName(), 5, 20f));
        }


        for (Map.Entry<String, List<CDAReportResponse>> entry : map.entrySet()) {
            String key = entry.getKey();

            if (!key.equalsIgnoreCase("Sub Head")) {
                List<CDAReportResponse> tabData = entry.getValue();
                table.addCell(boldText(key, 5, 35f));
                for (Integer i = 0; i < tabData.size(); i++) {
                    table.addCell(normalText(ConverterUtils.addDecimalPoint(tabData.get(i).getName()), 6, 20f));
                }
            }
        }
        table.addCell(boldText("Grand Total", 5, 20f));
        for (Integer i = 0; i < tabData1.size(); i++) {
            if (i == (tabData1.size() - 1)) {
                table.addCell(boldText(ConverterUtils.addDecimalPoint(grandTotal + ""), 6, 20f));
            } else {
                table.addCell(normalText("", 6, 20f));
            }
        }


        document.add(preface);
        document.add(table);
        document.close();

    }


    private PdfPCell boldText(String text, int fontSize, float cellHeight) {
        Phrase phrase = new Phrase();
        Font font = new Font(Font.FontFamily.COURIER, fontSize, Font.BOLD);
        Chunk world = new Chunk(text, font);
        phrase.add(world);
        Paragraph paragraph = new Paragraph();
        paragraph.add(phrase);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setMinimumHeight(cellHeight);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell normalText(String text, int fontSize, float cellHeight) {

        Phrase phrase = new Phrase();
        Font font = new Font(Font.FontFamily.HELVETICA, fontSize, Font.NORMAL);
        Chunk world = new Chunk(text, font);
        phrase.add(world);
        Paragraph paragraph = new Paragraph();
        paragraph.add(phrase);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setMinimumHeight(cellHeight);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

}

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

        Document document = new Document(PageSize.A4);
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

            double allAmountData = 0;
            for (Integer i = 0; i < tabData11.size(); i++) {

                if (i == 0) {
                    table.addCell(normalText(tabData11.get(i).getUnit(), 8, 25f));
                    table.addCell(normalText(tabData11.get(i).getAmount(), 8, 25f));
                } else {
                    table.addCell(normalText("", 8, 25f));
                    table.addCell(normalText(tabData11.get(i).getUnit(), 8, 25f));
                    table.addCell(normalText(tabData11.get(i).getAmount(), 8, 25f));
                }
                allAmountData = allAmountData + Double.parseDouble(tabData11.get(i).getAmount());

                table.addCell(boldText("", 8, 25f));
                table.addCell(boldText("Total Amount", 8, 25f));
                table.addCell(boldText(allAmountData + "", 8, 25f));

                grandTotal = grandTotal + Double.parseDouble(tabData11.get(i).getAmount());
            }
        }

        table.addCell(boldText("Grand Total", 8, 25f));
        table.addCell(boldText("", 8, 25f));
        table.addCell(boldText(grandTotal + "", 8, 25f));


        Phrase phrase = new Phrase();
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
        Chunk approverName = new Chunk(filePathResponse.getApproveName() + "\n" + filePathResponse.getApproveRank(), font);
        phrase.add(new Paragraph("\n" + "\n" + approverName));


        document.add(table);
        document.add(phrase);
        document.close();

    }


    public void createCdaAllMainReport(HashMap<String, List<CDAReportResponse>> map, CDAReportSubResponse cadSubReport, String path, Float grandTotal) throws Exception {


        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();
        document.setPageSize(PageSize.A4.rotate());
        document.newPage();

        Phrase phrase = new Phrase();
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
        Chunk header = new Chunk("CDA WISE/OBJECT HEAD WISE CONTROL FIGURES FOR " + cadSubReport.getAllocationType() + " " + cadSubReport.getFinYear(), font);
        Paragraph preface = new Paragraph();
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(header);

        Chunk revenue = new Chunk("REVENUE", font);
        preface.add(revenue);

        Chunk head = new Chunk("REVENUE", font);
        preface.add("Major Head " + cadSubReport.getMajorHead() + ". Sub Major Head 00. Minor Head " + cadSubReport.getMinorHead() + ") (In " + cadSubReport.getAmountType()+")");


        document.add(phrase);

//        float[] pointColumnWidths = {50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F, 50F};
//        PdfPTable table = new PdfPTable(21);
//        table.setWidths(pointColumnWidths);
//        table.setSpacingAfter(10);


        StringBuilder middle = new StringBuilder();
        int m = 0;
        int size = 0;
        for (Map.Entry<String, List<CDAReportResponse>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<CDAReportResponse> tabData = entry.getValue();
            m++;

            String keyValue = "<tr>" +
                    "<th >" + key + " </th>";
            middle = middle.append(keyValue);

            size = tabData.size();

            StringBuilder amountAppend = new StringBuilder();
            for (Integer i = 0; i < tabData.size(); i++) {
                String data = "<th >" + tabData.get(i).getName() + "</td>";
                amountAppend = amountAppend.append(data);
            }
            middle = middle.append(amountAppend).append("</tr>");

            if (m == 12 || m == 22 || m == 34 || m == 46 || m == 58) {
                middle = middle.append("</thead></table><br><br><br><br><table  class=dcf-table dcf-table-responsive dcf-table-bordered dcf-table-striped dcf-w-100%  border=\"1\"><thead>");
                middle = middle.append("<tr>" +
                        "<th > object </th>");
                List<CDAReportResponse> tabData1 = map.get("Sub Head");
                StringBuilder amountAppend1 = new StringBuilder();
                for (Integer i = 0; i < tabData1.size(); i++) {
                    String data = "<th >" + tabData1.get(i).getName() + "</td>";
                    amountAppend1 = amountAppend1.append(data);
                }
                middle = middle.append(amountAppend1).append("</tr>");

            }
        }

        middle = middle.append("<tr>" +
                "<th > Grand Total </th>");
        for (Integer i = 0; i < size; i++) {
            String data = "<th >" + " " + "</td>";
            middle = middle.append(data);
        }
        middle = middle.append(grandTotal).append("</tr>");


        document.close();
    }



    public void createCdaMainReport( HashMap<String, List<CDAReportResponse>> map, CDAReportSubResponse cadSubReport, String path, Float grandTotal) throws Exception {


        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();
        document.setPageSize(PageSize.A4.rotate());
        document.newPage();

        Phrase phrase = new Phrase();
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
        Chunk header = new Chunk("CDA WISE/OBJECT HEAD WISE CONTROL FIGURES FOR " + cadSubReport.getAllocationType() + " " + cadSubReport.getFinYear(), font);
        Paragraph preface = new Paragraph();
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(header);

        Chunk revenue = new Chunk("REVENUE", font);
        preface.add(revenue);

        Chunk head = new Chunk("REVENUE", font);
        preface.add("Major Head " + cadSubReport.getMajorHead() + ". Sub Major Head 00. Minor Head " + cadSubReport.getMinorHead() + ") (In " + cadSubReport.getAmountType()+")");


        document.add(phrase);



        StringBuilder middle = new StringBuilder();
        int m = 0;
        int size = 0;
        for (Map.Entry<String, List<CDAReportResponse>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<CDAReportResponse> tabData = entry.getValue();
            m++;

            String keyValue = "<tr>" +
                    "<th >" + key + " </th>";
            middle = middle.append(keyValue);

            size = tabData.size();

            StringBuilder amountAppend = new StringBuilder();
            for (Integer i = 0; i < tabData.size(); i++) {
                String data = "<th >" + tabData.get(i).getName() + "</td>";
                amountAppend = amountAppend.append(data);
            }
            middle = middle.append(amountAppend).append("</tr>");

            if (m == 30 || m == 68 || m == 96) {
                middle = middle.append("</thead></table><br><br><br><br><table  class=dcf-table dcf-table-responsive dcf-table-bordered dcf-table-striped dcf-w-100%  border=\"1\"><thead>");
                middle = middle.append("<tr>" +
                        "<th > object </th>");
                List<CDAReportResponse> tabData1 = map.get("Sub Head");
                StringBuilder amountAppend1 = new StringBuilder();
                for (Integer i = 0; i < tabData1.size(); i++) {
                    String data = "<th >" + tabData1.get(i).getName() + "</td>";
                    amountAppend1 = amountAppend1.append(data);
                }
                middle = middle.append(amountAppend1).append("</tr>");

            }
        }

        middle = middle.append("<tr>" +
                "<th > Grand Total </th>");
        for (Integer i = 0; i < size; i++) {
            String data = "<th >" + " " + "</td>";
            middle = middle.append(data);
        }
        middle = middle.append(grandTotal).append("</tr>");
        document.close();

    }


    private PdfPCell boldText(String text, int fontSize, float cellHeight) {
        Phrase phrase = new Phrase();
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, fontSize, Font.BOLD);
        Chunk world = new Chunk(text, font);
        phrase.add(world);
        Paragraph paragraph = new Paragraph();
        paragraph.add(phrase);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setMinimumHeight(cellHeight);
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
        return cell;
    }

}

package com.sdd.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sdd.response.FilePathResponse;

import java.io.*;
import java.util.*;
import java.util.List;

@Component
public class PdfGenaratorUtilMain {

    private static final String UTF_8 = "UTF-8";

    @SuppressWarnings("rawtypes")
    public void createPdfAllocation(HashMap<String, List<ReportSubModel>> hashMap, String path, FilePathResponse filePathResponse) throws Exception {

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream(path));
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

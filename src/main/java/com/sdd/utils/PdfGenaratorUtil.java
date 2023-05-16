package com.sdd.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.PdfWriter;
import com.sdd.response.CDAReportResponse;
import com.sdd.response.CDAReportSubResponse;
import com.sdd.response.CbReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PdfGenaratorUtil {
    @Autowired
    private TemplateEngine templateEngine;

    private static final String UTF_8 = "UTF-8";

    @SuppressWarnings("rawtypes")
    public void createPdfAllocation(String templateName, HashMap<String, List<ReportSubModel>> map, File outputFile) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");
        StringBuilder allHtml = new StringBuilder();

        for (Map.Entry<String, List<ReportSubModel>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<ReportSubModel> tabData = entry.getValue();
            String data = "<tr>" +
                    "<th scope=row class=bbtm>" + key + "</th>";

            Double tototalAmount = 0.0;
            StringBuilder addAllHtml = new StringBuilder();

            for (Integer i = 0; i < tabData.size(); i++) {
                String row = "";
                if (i == 0) {
                    row = "<td>" + tabData.get(i).getUnit() + "</td>" +
                            "<td>" + ConverterUtils.addDecimalPoint(tabData.get(i).getAmount()) + " (In " + tabData.get(i).getAmountType() + ") " + " </td>" +
                            "</tr>";
                } else {
                    row = " <th scope=row class=bbtm> </th>" +
                            "<td>" + tabData.get(i).getUnit() + "</td>" +
                            "<td>" + ConverterUtils.addDecimalPoint(tabData.get(i).getAmount()) + " (In " + tabData.get(i).getAmountType() + ") " + " </td>" +
                            "</tr>";
                }
                tototalAmount = tototalAmount + Double.parseDouble(tabData.get(i).getAmount());
                addAllHtml = addAllHtml.append(row);

            }
            String total = "<tr> <th scope=row></th>" +
                    "<td>TOTAL </td>" +
                    "<td>" + ConverterUtils.addDecimalPoint(tototalAmount+"") + "</td>" +
                    "</tr>";

            String sdfgh = data + addAllHtml + total;
            allHtml = allHtml.append(sdfgh);
        }


        String header = "<!DOCTYPE html>" +
                "<html lang=en>" +
                "<head>" +
                "  <title>Report</title>" +
                "  <meta charset=utf-8>" +
                "  <meta name=viewport content=width=device-width, initial-scale=1>" +
                "  " +
                "<style>" + "td{" +
                "border-bottom: 1px solid transparent !important;" +
                "}" +
                ".wrapper{" +
                "width: 90%;" +
                "margin: 100px auto;" +
                "}" + ":root {" +
                "--bg-table-stripe: #f6f6f5;" +
                "--b-table: #e3e3e2;" +
                "--caption: #242423;" +
                "}" + "table {" +
                "background-color: transparent;" +
                "border-collapse:collapse;" +
                "  font-family: Arial, Helvetica, sans-serif" +
                "}" + "th {" +
                "text-align:left" +
                "}" + ".dcf-txt-center {" +
                "  text-align: center!important" +
                "}" + ".dcf-txt-left {" +
                "  text-align: left!important" +
                "}" + ".dcf-txt-right {" +
                "  text-align: right!important" +
                "}" +
                "" +
                ".dcf-table caption {" +
                "  color: var(--caption);" +
                "  font-size: 1.13em;" +
                "  font-weight: 700;" +
                "  padding-bottom: .56rem" +
                "}" + ".dcf-table thead {" +
                "  font-size: .84em" +
                "}" + ".dcf-table tbody {" +
                "  border-bottom: 1px solid var(--b-table);" +
                "  border-top: 1px solid var(--b-table);" +
                "  font-size: .84em" +
                "}" + ".dcf-table tfoot {" +
                "  font-size: .84em" +
                "}" + ".dcf-table td, .dcf-table th {" +
                "  padding-right: 1.78em" +
                "}" + ".dcf-table-bordered, .dcf-table-bordered td, .dcf-table-bordered th {" +
                "  border: 1px solid var(--b-table)" +
                "}" + ".dcf-table-bordered td, .dcf-table-bordered th, .dcf-table-striped td, .dcf-table-striped th {" +
                "  padding-left: 1em;" +
                "  padding-right: 1em" +
                "}" + ".dcf-table-bordered tr:not(:last-child), .dcf-table-striped tr:not(:last-child) {" +
                "  border-bottom: 1px solid var(--b-table)" +
                "}" + ".dcf-table-striped tbody tr:nth-of-type(2n) {" +
                "  background-color: transparent;" +
                "}" + ".dcf-table thead td, .dcf-table thead th {" +
                "  padding-bottom: .75em;" +
                "  vertical-align: bottom" +
                "}" + ".dcf-table tbody td, .dcf-table tbody th, .dcf-table tfoot td, .dcf-table tfoot th {" +
                "  padding-top: .75em;" +
                "  vertical-align: top" +
                "}" + ".dcf-table tbody td, .dcf-table tbody th {" +
                "  padding-bottom: .75em" +
                "}" + ".dcf-table-bordered thead th {" +
                "  padding-top: 1.33em" +
                "}" + ".dcf-wrapper-table-scroll {" +
                "  overflow-x: auto;" +
                "  -webkit-overflow-scrolling: touch;" +
                "  left: 50%;" +
                "  margin-left: -50vw;" +
                "  margin-right: -50vw;" +
                "  padding-bottom: 1em;" +
                "  position: relative;" +
                "  right: 50%;" +
                "  width: 100vw" +
                "}" + "@media only screen and (max-width:42.09em) {" +
                "  .dcf-table-responsive thead {" +
                "clip: rect(0 0 0 0);" +
                "-webkit-clip-path: inset(50%);" +
                "clip-path: inset(50%);" +
                "height: 1px;" +
                "overflow: hidden;" +
                "position: absolute;" +
                "width: 1px;" +
                "white-space: nowrap" +
                "  }" +
                "  .dcf-table-responsive tr {" +
                "display: block" +
                "  }" +
                "  .dcf-table-responsive td {" +
                "-webkit-column-gap: 3.16vw;" +
                "-moz-column-gap: 3.16vw;" +
                "column-gap: 3.16vw;" +
                "display: grid;" +
                "grid-template-columns: 1fr 2fr;" +
                "text-align: left!important" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered, .dcf-table-responsive.dcf-table-bordered thead th {" +
                "border-width: 0" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered tbody td {" +
                "border-top-width: 0" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered) tbody tr {" +
                "padding-bottom: .75em" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered) tbody td {" +
                "padding-bottom: 0" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered):not(.dcf-table-striped) tbody td {" +
                "padding-right: 0" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered tbody tr:last-child td:last-child {" +
                "border-bottom-width: 0" +
                "  }" +
                "  .dcf-table-responsive tbody td:before {" +
                "content: attr(data-label);" +
                "float: left;" +
                "font-weight: 700;" +
                "padding-right: 1.78em" +
                "  }" +
                "}" + ".dcf-overflow-x-auto {" +
                "  overflow-x: auto!important;" +
                "  -webkit-overflow-scrolling: touch" +
                "}" +
                "" +
                ".dcf-w-100\\% {" +
                "  width: 100%!important;" +
                "}" +
                "" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=wrapper> " +
                "<table class=dcf-table dcf-table-responsive dcf-table-bordered dcf-table-striped dcf-w-100% border=\"1\">" +
                "<thead>" +
                "<tr>" +
                "<th scope=col>SUB HEAD </th>" +
                "<th class=dcf-txt-center scope=col>UNIT NAME</th>" +
                "<th class=dcf-txt-center scope=col>ALLOCATION</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>";


        String closeFooter = "</tbody>" +
                "</table>" +
                "<div>" +
                "</div>" +
                "</div>" + "</body>" +
                "</html>";


        String totalHtml = header + allHtml + closeFooter;
        String xHtml = convertToXhtml(totalHtml);

        ITextRenderer renderer = new ITextRenderer();

        String baseUrl = FileSystems.getDefault().getPath("resources").toUri().toURL().toString();
        baseUrl += "templates/";
        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();
        OutputStream outputStream = new FileOutputStream(outputFile);
        renderer.createPDF(outputStream);
        outputStream.close();

    }


    public void createCdaReport(String templateName, HashMap<String, List<CDAReportResponse>> map, File outputFile) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");


        String totalString = "";

        String xHtml = convertToXhtml(totalString);

        ITextRenderer renderer = new ITextRenderer();
        String baseUrl = FileSystems.getDefault().getPath("resources").toUri().toURL().toString();
        baseUrl += "templates/";

        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();

        OutputStream outputStream = new FileOutputStream(outputFile);
        renderer.createPDF(outputStream);
        outputStream.close();

    }


    public void createCdaAllMainReport(String templateName, HashMap<String, List<CDAReportResponse>> map, CDAReportSubResponse cadSubReport, File outputFile, Float grandTotal) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");


        String header = "<!DOCTYPE html>" +
                "<html lang=en>" +
                "<head>" +
                "  <title>CDA Parking Report</title>" +
                "  <meta charset=utf-8>" +
                "  <meta name=viewport content=width=device-width, initial-scale=1>" +
                "  " +
                "<style type=\"text/css\">" +
                "@page { size: A4 landscape;}" +
                "div.header {" +
                "    display: block; text-align: center; " +
                "    position: running(header);" +
                "}" +
                ".bold{" +
                "font-weight: bold !important;" +
                "}" +
                ".bbtm{" +
                "border-bottom: 1px solid transparent !important;" +
                "}" +
                ".brtm{" +
                "border-right: 1px solid transparent !important;" +
                "}" +
                ".wrapper{" +
                "width: 100%;" +
                "margin: 100px auto;" +
                "}" +
                "" +
                ":root {" +
                "--bg-table-stripe: #f6f6f5;" +
                "--b-table: #e3e3e2;" +
                "--caption: #242423;" +
                "}" +
                "" +
                "table {" +
                "background-color: transparent;" +
                "border-collapse:collapse;" +
                "  font-family: Arial, Helvetica, sans-serif" +
                "}" +
                "" +
                "th {" +
                "text-align:left" +
                "}" +
                "" +
                ".dcf-txt-center {" +
                "  text-align: center!important" +
                "}" +
                "" +
                ".dcf-txt-left {" +
                "  text-align: left!important" +
                "}" +
                "" +
                ".dcf-txt-right {" +
                "  text-align: right!important" +
                "}" +
                "" +
                ".dcf-table caption {" +
                "  color: var(--caption);" +
                "  font-size: 1.13em;" +
                "  font-weight: 700;" +
                "  padding-bottom: .56rem" +
                "}" +
                "" +
                ".dcf-table thead {" +
                "  font-size: .49em" +
                "}" +
                "" +
                ".dcf-table tbody {" +
                "  border-bottom: 1px solid var(--b-table);" +
                "  border-top: 1px solid var(--b-table);" +
                "  font-size: .84em" +
                "}" +
                "" +
                ".dcf-table tfoot {" +
                "  font-size: .84em" +
                "}" +
                "" +
                ".dcf-table td, .dcf-table th {" +
                "  padding-right: 1.78em" +
                "}" +
                "" +
                ".dcf-table-bordered, .dcf-table-bordered td, .dcf-table-bordered th {" +
                "  border: 1px solid var(--b-table)" +
                "}" +
                "" +
                ".dcf-table-bordered td, .dcf-table-bordered th, .dcf-table-striped td, .dcf-table-striped th {" +
                "  padding-left: 1em;" +
                "  padding-right: 1em" +
                "}" +
                "" +
                ".dcf-table-bordered tr:not(:last-child), .dcf-table-striped tr:not(:last-child) {" +
                "  border-bottom: 1px solid var(--b-table)" +
                "}" +
                "" +
                ".dcf-table-striped tbody tr:nth-of-type(2n) {" +
                "  background-color: transparent;" +
                "}" +
                "" +
                ".dcf-table thead td, .dcf-table thead th {" +
                "  padding-bottom: .75em;" +
                "  vertical-align: bottom" +
                "}" +
                "" +
                ".dcf-table tbody td, .dcf-table tbody th, .dcf-table tfoot td, .dcf-table tfoot th {" +
                "  padding-top: .75em;" +
                "  vertical-align: top" +
                "}" +
                "" +
                ".dcf-table tbody td, .dcf-table tbody th {" +
                "  padding-bottom: .75em" +
                "}" +
                "" +
                ".dcf-table-bordered thead th {" +
                "  padding-top: 1.33em" +
                "}" +
                "" +
                ".dcf-wrapper-table-scroll {" +
                "  overflow-x: auto;" +
                "  -webkit-overflow-scrolling: touch;" +
                "  left: 50%;" +
                "  margin-left: -50vw;" +
                "  margin-right: -50vw;" +
                "  padding-bottom: 1em;" +
                "  position: relative;" +
                "  right: 50%;" +
                "  width: 100vw" +
                "}" +
                "" +
                "@media only screen and (max-width:42.09em) {" +
                "  .dcf-table-responsive thead {" +
                "clip: rect(0 0 0 0);" +
                "-webkit-clip-path: inset(50%);" +
                "clip-path: inset(50%);" +
                "height: 1px;" +
                "overflow: hidden;" +
                "position: absolute;" +
                "width: 1px;" +
                "white-space: nowrap" +
                "  }" +
                "  .dcf-table-responsive tr {" +
                "display: block" +
                "  }" +
                "  .dcf-table-responsive td {" +
                "-webkit-column-gap: 3.16vw;" +
                "-moz-column-gap: 3.16vw;" +
                "column-gap: 3.16vw;" +
                "display: grid;" +
                "grid-template-columns: 1fr 2fr;" +
                "text-align: left!important" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered, .dcf-table-responsive.dcf-table-bordered thead th {" +
                "border-width: 0" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered tbody td {" +
                "border-top-width: 0" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered) tbody tr {" +
                "padding-bottom: .75em" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered) tbody td {" +
                "padding-bottom: 0" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered):not(.dcf-table-striped) tbody td {" +
                "padding-right: 0" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered tbody tr:last-child td:last-child {" +
                "border-bottom-width: 0" +
                "  }" +
                "  .dcf-table-responsive tbody td:before {" +
                "content: attr(data-label);" +
                "float: left;" +
                "font-weight: 700;" +
                "padding-right: 1.78em" +
                "  }" +
                "}" +
                "" +
                ".dcf-overflow-x-auto {" +
                "  overflow-x: auto!important;" +
                "  -webkit-overflow-scrolling: touch" +
                "}" +
                "" +
                ".dcf-w-100\\% {" +
                "  width: 100%!important;" +
                "}" +
                "" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=wrapper> " +
                "<h3 style=\"text-align: center\";>CDA WISE/ OBJECT HEAD WISE CONTROL FIGURES FOR " + cadSubReport.getAllocationType() + " " + cadSubReport.getFinYear() + " </h3>" +
                "<h3 style=\"text-align: center\"; >REVENUE</h3>" +
                "<h3 style=\"text-align: center\";>(Major Head " + cadSubReport.getMajorHead() + ". Sub Major Head 00. Minor Head " + cadSubReport.getMinorHead() + ") (In " + cadSubReport.getAmountType() + ")</h3><br>" +
                "" +
                "<table  class=dcf-table dcf-table-responsive dcf-table-bordered dcf-table-striped dcf-w-100%  border=\"1\">" +
                "<thead> ";


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
                List<CDAReportResponse> tabData1 = map.get("object");
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


        String footer = "</thead></table></body>" +
                "</html>";

        String xHtml = convertToXhtml(header + middle + footer);


        ITextRenderer renderer = new ITextRenderer();
        String baseUrl = FileSystems.getDefault().getPath("resources").toUri().toURL().toString();
        baseUrl += "templates/";

        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();

        OutputStream outputStream = new FileOutputStream(outputFile);
        renderer.createPDF(outputStream);
        outputStream.close();

    }

    public void createCdaMainReport(String templateName, HashMap<String, List<CDAReportResponse>> map, CDAReportSubResponse cadSubReport, File outputFile, Float grandTotal) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");


        String header = "<!DOCTYPE html>" +
                "<html lang=en>" +
                "<head>" +
                "  <title>CDA Parking Report</title>" +
                "  <meta charset=utf-8>" +
                "  <meta name=viewport content=width=device-width, initial-scale=1>" +
                "  " +
                "<style type=\"text/css\">" +
                "@page { size: A4 landscape;}" +
                "div.header {" +
                "    display: block; text-align: center; " +
                "    position: running(header);" +
                "}" +
                ".bold{" +
                "font-weight: bold !important;" +
                "}" +
                ".bbtm{" +
                "border-bottom: 1px solid transparent !important;" +
                "}" +
                ".brtm{" +
                "border-right: 1px solid transparent !important;" +
                "}" +
                ".wrapper{" +
                "width: 100%;" +
                "margin: 100px auto;" +
                "}" +
                "" +
                ":root {" +
                "--bg-table-stripe: #f6f6f5;" +
                "--b-table: #e3e3e2;" +
                "--caption: #242423;" +
                "}" +
                "" +
                "table {" +
                "background-color: transparent;" +
                "border-collapse:collapse;" +
                "  font-family: Arial, Helvetica, sans-serif" +
                "}" +
                "" +
                "th {" +
                "text-align:left" +
                "}" +
                "" +
                ".dcf-txt-center {" +
                "  text-align: center!important" +
                "}" +
                "" +
                ".dcf-txt-left {" +
                "  text-align: left!important" +
                "}" +
                "" +
                ".dcf-txt-right {" +
                "  text-align: right!important" +
                "}" +
                "" +
                ".dcf-table caption {" +
                "  color: var(--caption);" +
                "  font-size: 1.13em;" +
                "  font-weight: 700;" +
                "  padding-bottom: .56rem" +
                "}" +
                "" +
                ".dcf-table thead {" +
                "  font-size: .49em" +
                "}" +
                "" +
                ".dcf-table tbody {" +
                "  border-bottom: 1px solid var(--b-table);" +
                "  border-top: 1px solid var(--b-table);" +
                "  font-size: .84em" +
                "}" +
                "" +
                ".dcf-table tfoot {" +
                "  font-size: .84em" +
                "}" +
                "" +
                ".dcf-table td, .dcf-table th {" +
                "  padding-right: 1.78em" +
                "}" +
                "" +
                ".dcf-table-bordered, .dcf-table-bordered td, .dcf-table-bordered th {" +
                "  border: 1px solid var(--b-table)" +
                "}" +
                "" +
                ".dcf-table-bordered td, .dcf-table-bordered th, .dcf-table-striped td, .dcf-table-striped th {" +
                "  padding-left: 1em;" +
                "  padding-right: 1em" +
                "}" +
                "" +
                ".dcf-table-bordered tr:not(:last-child), .dcf-table-striped tr:not(:last-child) {" +
                "  border-bottom: 1px solid var(--b-table)" +
                "}" +
                "" +
                ".dcf-table-striped tbody tr:nth-of-type(2n) {" +
                "  background-color: transparent;" +
                "}" +
                "" +
                ".dcf-table thead td, .dcf-table thead th {" +
                "  padding-bottom: .75em;" +
                "  vertical-align: bottom" +
                "}" +
                "" +
                ".dcf-table tbody td, .dcf-table tbody th, .dcf-table tfoot td, .dcf-table tfoot th {" +
                "  padding-top: .75em;" +
                "  vertical-align: top" +
                "}" +
                "" +
                ".dcf-table tbody td, .dcf-table tbody th {" +
                "  padding-bottom: .75em" +
                "}" +
                "" +
                ".dcf-table-bordered thead th {" +
                "  padding-top: 1.33em" +
                "}" +
                "" +
                ".dcf-wrapper-table-scroll {" +
                "  overflow-x: auto;" +
                "  -webkit-overflow-scrolling: touch;" +
                "  left: 50%;" +
                "  margin-left: -50vw;" +
                "  margin-right: -50vw;" +
                "  padding-bottom: 1em;" +
                "  position: relative;" +
                "  right: 50%;" +
                "  width: 100vw" +
                "}" +
                "" +
                "@media only screen and (max-width:42.09em) {" +
                "  .dcf-table-responsive thead {" +
                "clip: rect(0 0 0 0);" +
                "-webkit-clip-path: inset(50%);" +
                "clip-path: inset(50%);" +
                "height: 1px;" +
                "overflow: hidden;" +
                "position: absolute;" +
                "width: 1px;" +
                "white-space: nowrap" +
                "  }" +
                "  .dcf-table-responsive tr {" +
                "display: block" +
                "  }" +
                "  .dcf-table-responsive td {" +
                "-webkit-column-gap: 3.16vw;" +
                "-moz-column-gap: 3.16vw;" +
                "column-gap: 3.16vw;" +
                "display: grid;" +
                "grid-template-columns: 1fr 2fr;" +
                "text-align: left!important" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered, .dcf-table-responsive.dcf-table-bordered thead th {" +
                "border-width: 0" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered tbody td {" +
                "border-top-width: 0" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered) tbody tr {" +
                "padding-bottom: .75em" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered) tbody td {" +
                "padding-bottom: 0" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered):not(.dcf-table-striped) tbody td {" +
                "padding-right: 0" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered tbody tr:last-child td:last-child {" +
                "border-bottom-width: 0" +
                "  }" +
                "  .dcf-table-responsive tbody td:before {" +
                "content: attr(data-label);" +
                "float: left;" +
                "font-weight: 700;" +
                "padding-right: 1.78em" +
                "  }" +
                "}" +
                "" +
                ".dcf-overflow-x-auto {" +
                "  overflow-x: auto!important;" +
                "  -webkit-overflow-scrolling: touch" +
                "}" +
                "" +
                ".dcf-w-100\\% {" +
                "  width: 100%!important;" +
                "}" +
                "" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=wrapper> " +
                "<h3 style=\"text-align: center\";>CDA WISE/ OBJECT HEAD WISE CONTROL FIGURES FOR " + cadSubReport.getAllocationType() + " " + cadSubReport.getFinYear() + " </h3>" +
                "<h3 style=\"text-align: center\"; >REVENUE</h3>" +
                "<h3 style=\"text-align: center\";>(Major Head " + cadSubReport.getMajorHead() + ". Sub Major Head 00. Minor Head " + cadSubReport.getMinorHead() + ") (In " + cadSubReport.getAmountType() + ")</h3><br>" +
                "" +
                "<table  class=dcf-table dcf-table-responsive dcf-table-bordered dcf-table-striped dcf-w-100%  border=\"1\">" +
                "<thead> ";


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
                List<CDAReportResponse> tabData1 = map.get("object");
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


        String footer = "</thead></table></body>" + "</html>";

        String xHtml = convertToXhtml(header + middle + footer);

        ITextRenderer renderer = new ITextRenderer();
        String baseUrl = FileSystems.getDefault().getPath("resources").toUri().toURL().toString();
        baseUrl += "templates/";

        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();

        OutputStream outputStream = new FileOutputStream(outputFile);
        renderer.createPDF(outputStream);
        outputStream.close();

    }


    public void createPdf(String templateName, HashMap<String, List<ReportSubModel>> map, File outputFile) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");
        StringBuilder allHtml = new StringBuilder();

        for (Map.Entry<String, List<ReportSubModel>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<ReportSubModel> tabData = entry.getValue();
            String data = "<tr>" +
                    "<td data-label=CGCs/RHQs/ADG(P) Directorates at CGHQ class=bold brtm> " + key + " </td>" +
                    "<td class=brtm></td>" +
                    "<td data-label=BE 2022-23 Allocation class=brtm></td>" +
                    "<td></td>" +
                    "</tr>";

            double tototalAmount = 0;
            double revisedAmount = 0;
            StringBuilder addAllHtml = new StringBuilder();

            for (Integer i = 0; i < tabData.size(); i++) {
                Double addAmount = Double.parseDouble(tabData.get(i).getRevisedAmount()) + Double.parseDouble(tabData.get(i).getAmount());


                String row = "<tr>" +
                        "<td data-label=CGCs/RHQs/ADG(P) Directorates at CGHQ>" + tabData.get(i).getUnit() + "</td>" +
                        "<td class=dcf-txt-right>" + tabData.get(i).getAmount() + "</td>" +
                        "<td data-label=BE 2022-23 Allocation>" + tabData.get(i).getRevisedAmount() + "</td>" +
                        "<td class=dcf-txt-right>" + addAmount + "</td>" +
                        "</tr>";
                revisedAmount = revisedAmount + Double.parseDouble(tabData.get(i).getRevisedAmount());
                addAllHtml = addAllHtml.append(row);

            }

            double allAmountData = tototalAmount + revisedAmount;

            String total = "<tr>" +
                    "<td data-label=CGCs/RHQs/ADG(P) Directorates at CGHQ class=bold>TOTAL </td>" +
                    "<td class=dcf-txt-right bold>" + tototalAmount + "</td>" +
                    "<td class=dcf-txt-right bold data-label=BE 2022-23 Allocation>(+)" + revisedAmount + "</td>" +
                    "<td class=dcf-txt-right bold>" + allAmountData + "</td>" +
                    "</tr>";

            String sdfgh = data + addAllHtml + total;
            allHtml = allHtml.append(sdfgh);
        }


        String header = "<!DOCTYPE html>" +
                "<html lang=en>" +
                "<head>" +
                "  <title>Report</title>" +
                "  <meta charset=utf-8>" +
                "  <meta name=viewport content=width=device-width, initial-scale=1>" +
                "  " +
                "<style>" +
                ".bold{" +
                "font-weight: bold !important;" +
                "}" +
                ".bbtm{" +
                "border-bottom: 1px solid transparent !important;" +
                "}" +
                ".brtm{" +
                "border-right: 1px solid transparent !important;" +
                "}" +
                ".wrapper{" +
                "width: 70%;" +
                "margin: 100px auto;" +
                "}" + ":root {" +
                "--bg-table-stripe: #f6f6f5;" +
                "--b-table: #e3e3e2;" +
                "--caption: #242423;" +
                "}" + "table {" +
                "background-color: transparent;" +
                "border-collapse:collapse;" +
                "  font-family: Arial, Helvetica, sans-serif" +
                "}" + "th {" +
                "text-align:left" +
                "}" + ".dcf-txt-center {" +
                "  text-align: center!important" +
                "}" + ".dcf-txt-left {" +
                "  text-align: left!important" +
                "}" + ".dcf-txt-right {" +
                "  text-align: right!important" +
                "}" +
                "" +
                ".dcf-table caption {" +
                "  color: var(--caption);" +
                "  font-size: 1.13em;" +
                "  font-weight: 700;" +
                "  padding-bottom: .56rem" +
                "}" + ".dcf-table thead {" +
                "  font-size: .84em" +
                "}" + ".dcf-table tbody {" +
                "  border-bottom: 1px solid var(--b-table);" +
                "  border-top: 1px solid var(--b-table);" +
                "  font-size: .84em" +
                "}" + ".dcf-table tfoot {" +
                "  font-size: .84em" +
                "}" + ".dcf-table td, .dcf-table th {" +
                "  padding-right: 1.78em" +
                "}" + ".dcf-table-bordered, .dcf-table-bordered td, .dcf-table-bordered th {" +
                "  border: 1px solid var(--b-table)" +
                "}" + ".dcf-table-bordered td, .dcf-table-bordered th, .dcf-table-striped td, .dcf-table-striped th {" +
                "  padding-left: 1em;" +
                "  padding-right: 1em" +
                "}" + ".dcf-table-bordered tr:not(:last-child), .dcf-table-striped tr:not(:last-child) {" +
                "  border-bottom: 1px solid var(--b-table)" +
                "}" + ".dcf-table-striped tbody tr:nth-of-type(2n) {" +
                "  background-color: transparent;" +
                "}" + ".dcf-table thead td, .dcf-table thead th {" +
                "  padding-bottom: .75em;" +
                "  vertical-align: bottom" +
                "}" + ".dcf-table tbody td, .dcf-table tbody th, .dcf-table tfoot td, .dcf-table tfoot th {" +
                "  padding-top: .75em;" +
                "  vertical-align: top" +
                "}" + ".dcf-table tbody td, .dcf-table tbody th {" +
                "  padding-bottom: .75em" +
                "}" + ".dcf-table-bordered thead th {" +
                "  padding-top: 1.33em" +
                "}" + ".dcf-wrapper-table-scroll {" +
                "  overflow-x: auto;" +
                "  -webkit-overflow-scrolling: touch;" +
                "  left: 50%;" +
                "  margin-left: -50vw;" +
                "  margin-right: -50vw;" +
                "  padding-bottom: 1em;" +
                "  position: relative;" +
                "  right: 50%;" +
                "  width: 100vw" +
                "}" + "@media only screen and (max-width:42.09em) {" +
                "  .dcf-table-responsive thead {" +
                "clip: rect(0 0 0 0);" +
                "-webkit-clip-path: inset(50%);" +
                "clip-path: inset(50%);" +
                "height: 1px;" +
                "overflow: hidden;" +
                "position: absolute;" +
                "width: 1px;" +
                "white-space: nowrap" +
                "  }" +
                "  .dcf-table-responsive tr {" +
                "display: block" +
                "  }" +
                "  .dcf-table-responsive td {" +
                "-webkit-column-gap: 3.16vw;" +
                "-moz-column-gap: 3.16vw;" +
                "column-gap: 3.16vw;" +
                "display: grid;" +
                "grid-template-columns: 1fr 2fr;" +
                "text-align: left!important" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered, .dcf-table-responsive.dcf-table-bordered thead th {" +
                "border-width: 0" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered tbody td {" +
                "border-top-width: 0" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered) tbody tr {" +
                "padding-bottom: .75em" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered) tbody td {" +
                "padding-bottom: 0" +
                "  }" +
                "  .dcf-table-responsive:not(.dcf-table-bordered):not(.dcf-table-striped) tbody td {" +
                "padding-right: 0" +
                "  }" +
                "  .dcf-table-responsive.dcf-table-bordered tbody tr:last-child td:last-child {" +
                "border-bottom-width: 0" +
                "  }" +
                "  .dcf-table-responsive tbody td:before {" +
                "content: attr(data-label);" +
                "float: left;" +
                "font-weight: 700;" +
                "padding-right: 1.78em" +
                "  }" +
                "}" + ".dcf-overflow-x-auto {" +
                "  overflow-x: auto!important;" +
                "  -webkit-overflow-scrolling: touch" +
                "}" +
                "" +
                ".dcf-w-100\\% {" +
                "  width: 100%!important;" +
                "}" +
                "" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=wrapper> " +
                "<table class=dcf-table dcf-table-responsive dcf-table-bordered dcf-table-striped dcf-w-100% border=\"1\">" +
                "<thead>" +
                "<tr>" +
                "<th class=dcf-txt-center bbtm scope=col>CGCs/RHQs/ADG(P) Directorates at CGHQ</th>" +
                "<th scope=col></th>" +
                "<th class=dcf-txt-center scope=col>BE 2022-23 Allocation</th>" +
                "<th scope=col></th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>" +
                "<tr>" +
                "<td data-label=CGCs/RHQs/ADG(P) Directorates at CGHQ></td>" +
                "<td class=dcf-txt-center brtm bold>Existing</td>" +
                "<td class=dcf-txt-center brtm bold data-label=BE 2022-23 Allocation>Additional/Withdrawal</td>" +
                "<td class=dcf-txt-center bold>Revised</td>" +
                "</tr>";


        String closeFooter = "</tbody>" +
                "</table>" + "</div>" + "</body>" +
                "</html>";


        String totalHtml = header + allHtml + closeFooter;

        String xHtml = convertToXhtml(totalHtml);

        ITextRenderer renderer = new ITextRenderer();
        String baseUrl = FileSystems.getDefault().getPath("resources").toUri().toURL().toString();
        baseUrl += "templates/";

        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();
        OutputStream outputStream = new FileOutputStream(outputFile);
        renderer.createPDF(outputStream);
        outputStream.close();

    }


    public void createCbReportPdfSample(String templateName, CbReportResponse cbReportResponse, File outputFile) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(cbReportResponse.getCbData().getCbDate());
        Float bill = (Float.parseFloat(cbReportResponse.getCurrentBillAmount()));
        bill = bill * 100 / 118;
        String billFormat = String.format("%.2f", bill);
        bill = Float.parseFloat(billFormat);
        Float gst = (Float.parseFloat(cbReportResponse.getCurrentBillAmount()));
        gst = gst - bill;

        String html = "<!DOCTYPE html>" +
                "<html lang=en>" + "<head>" +
                "<meta charset=UTF-8>" +
                "<meta http-equiv=X-UA-Compatible content=IE=edge>" +
                "<meta name=viewport content=width=device-width, initial-scale=1.0>" +
                "<title>Document</title>" +
                "</head>" +
                "<style>" +
                "body {" +
                "font-size:12px;" +
                "}" +
                ".header {" +
                "text-align: center;" +
                "}" + ".table {" +
                "display: flex;" +
                "}" + ".float {" +
                "float: right;" + "}" +
                ".float-left{\n" +
                "        float: left;\n" +
                "    }" +
                ".auth {" +
                "padding-left: 72px;" +
                "}" + ".table2," +
                ".the," +
                ".them.themed {" +
                "border: 1px solid black;" +
                "border-collapse: collapse;" +
                "}" + ".sign {" +
                "float: right;" +
                "padding-right: 100px;" +
                "padding-top: 50px;" +
                "}" + ".count {" +
                "text-align: center;" +
                "padding-top: 50px;" +
                "}" + ".table3 {" +
                "padding-top: 55px;" +
                "}" + ".tab {" +
                "padding-left: 85px;" +
                "}" +
                ".table4{" +
                "padding-top: 15px;" +
                "}" +
                ".top-bar{\n" +
                "        display: flow-root;\n" +
                "        margin-bottom: 20px;\n" +
                "    }" +

                ".wrap{" +
                "width:80%;" +
                "margin: 100px auto;" +
                "}" +
                " .th {\n" +
                "            text-align:left;\n" +
                "            border: 1px solid #242423 ;\n" +
                "        }\n" +
                "        .td{\n" +
                "            border: 1px solid #242423 ;\n" +
                "        }" +
                "</style>" + "<body>" +
                "    <div class=\"wrap\">\n" +
                "        <div class=\"top-bar\">\n" +
                "        <div class=\"float-left\">Contingent Bill No. <strong>" + cbReportResponse.getCbData().getCbNo() + "</strong></div>\n" +
                "        <div class=\"float\">Dated: <strong>" + date(dateString) + "</strong></div>\n" +
                "        </div>\n" +
                "        <div class=\"header\"> <strong>CONTINGENT BILL</strong></div>" +
                "<div class=expenditure>" +
                "<p>" + cbReportResponse.getOnAccountData() + "</u></b></span></p>" +
                "</div>" +
                "<table class=table1 style=width:100%>" +
                "<tr>" +
                "<td >Total Amount/ Budget alloted</td>" +
                "<td > (INR) " + cbReportResponse.getAllocatedAmount() + "</td>" +
                "  " +
                "</tr>" +
                "<tr>" +
                "<td >Progressive expenditure including this bill</td>" +
                "<td > (INR) " + cbReportResponse.getExpenditureAmount() + "</td>" + "</tr>" +
                "<tr>" +
                "<td >Balance amount</td>" +
                "<td > (INR) " + cbReportResponse.getRemeningAmount() + "</td>" +
                "</tr>" +
                "</table>" + "<br>" + "<div class=authority>" +
                "Authority: (a) " + cbReportResponse.getOnAurthyData() + "" +
                "</div>" +
                "<br>" +
                "<div class=auth>" +
                "(b) " + cbReportResponse.getUnitData().getDescr() + " Sanction No. <span> <b><u>" + cbReportResponse.getAuthorityDetails().getAuthority() + "</u></b> </span>" +
                "</div>" +
                "<br>" +
                "<table class=table2 style=width:100%>" +
                "<tr class=>" +
                "<th class=th>Sr.</th>" +
                "<th class=th>Details of Expenditure</th>" +
                "<th class=th>Amount (in INR)</th>" +
                "</tr>" +
                "<tr class=>" +
                "<td class=td>01</td>" +
                "<td class=td>Expenditure incurred towards quaterly payment for the 3rd otr from 01 Sep 22 to 30" +
                "Nov 22 in respect" +
                "of Hirring of Designer/Developer IT Manpower <span>(Project-SDOT)</span> through <span>" + cbReportResponse.getCbData().getVendorName() + " </span>vibe Invoiice/bill <span>" + cbReportResponse.getCbData().getInvoiceNO() + "</span>Dated <span>" + cbReportResponse.getCbData().getInvoiceDate() + "</span> </td>" +
                "<td class=td> (INR) " + bill.toString() + "</td>" +
                "</tr>" +
                "<tr><td class=td></td><td class = td style=\"text-align: right\">GST 18%</td><td class=td> (INR)" + String.format("%.2f", gst) + "</td></tr>" +
                "<tr><td class=td></td><td class = td style=\"text-align: right\">TOTAL</td><td class=td>  (INR)" + cbReportResponse.getCurrentBillAmount() + "</td></tr>" +
                "<tr>" +
                "<td class=td colspan=3> Amount in words (Rupees  <span><b><u>" + convertDecimaltoString(cbReportResponse.getCurrentBillAmount()) + " only)</u></b></span> (Including GST)</td></tr>" + "</table>" +
                "<br>" +
                "<div class=certify>" +
                "<u>Certify that:-</u>" +
                "<br>" +
                "(a) Items has/have been taken on charge." +
                "<br>" +
                "(b) The rates is/are fair and reasonable.<br>" +
                "(c) The Expebditure incurred is creditable to Major Head " + cbReportResponse.getBudgetHead().getMajorHead() + " Customs, Sub Major Head 00, Minor Head " + cbReportResponse.getBudgetHead().getMinorHead() + ", -" +
                "preventive & other function 06 CG Organisation under Sub Head: <span><b><u>" + cbReportResponse.getBudgetHead().getSubHeadDescr() + ".</u></b></span>Category Code <span><b><u>" + cbReportResponse.getBudgetHead().getBudgetCodeId() + "</u></b></span>" +
                "<br>" +
                "(d) The expenditure has been incurred in the interest of the state." +
                "</div>" + "<div class=sign>" +
                "<div class=div1>" + cbReportResponse.getVerifer().getFullName() + "</div>" + "<div class=div2> " + cbReportResponse.getVerifer().getRank() + "" +
                "</div>" +
                "</div>" + "</div>" +
                "<div class=count>" +
                "<span><u><b>COUNTERSIGNED</b></u></span>" +
                "<table class=table3>" +
                "<tr>" +
                "<th></th>" + "<td class=tab>(" + cbReportResponse.getApprover().getFullName() + ")</td>" + "</tr>" + "<tr>" +
                "<th>Coast Guard Headquaters</th>" + "<td class=tab>Dy Inspector General</td>" + "</tr>" +
                "<tr>" +
                "<th>National Stadium Complex</th>" +
                "<td class=tab>" + cbReportResponse.getApprover().getRank() + "</td>" + "</tr>" +
                "<tr>" +
                "<th>New Delhi-110001</th>" +
                "<td class=tab></td>" + "</tr>" + "</table>" +
                "</div>" + "<table class=table4>" +
                "<tr>" +
                "<th>File No. <span><b><u>" + cbReportResponse.getCbData().getFileID() + "</u></b></span></th>" +
                "</tr>" +
                "<tr>" +
                "<th>Date <span><b><u>" + date(cbReportResponse.getCbData().getFileDate()) + "</u></b></span></th>" + "</tr>" +
                "</table>" +
                "</div>" + "</body>" + "</html>";


        String xHtml = convertToXhtml(html);

        ITextRenderer renderer = new ITextRenderer();
        String baseUrl = FileSystems.getDefault().getPath("resources").toUri().toURL().toString();
        baseUrl += "templates/";

        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();

        OutputStream outputStream = new FileOutputStream(outputFile);
        renderer.createPDF(outputStream);
        outputStream.close();

    }

    public static String date(String dateInput) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = inputFormat.parse(dateInput);
        String output = outputFormat.format(date);

        return output;
    }

    public void createPdfSample(String templateName, Map map, File outputFile) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");

        Context ctx = new Context();
        if (map != null) {
            Iterator itMap = map.entrySet().iterator();
            while (itMap.hasNext()) {
                Map.Entry pair = (Map.Entry) itMap.next();
                ctx.setVariable(pair.getKey().toString(), pair.getValue());
            }
        }

        String renderedHtmlContent = templateEngine.process(templateName, ctx);
        String xHtml = convertToXhtml(renderedHtmlContent);

        ITextRenderer renderer = new ITextRenderer();
        String baseUrl = FileSystems.getDefault().getPath("resources").toUri().toURL().toString();
        baseUrl += "templates/";

        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();

        OutputStream outputStream = new FileOutputStream(outputFile);
        renderer.createPDF(outputStream);
        outputStream.close();

    }


    private String convertToXhtml(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(UTF_8);
        tidy.setOutputEncoding(UTF_8);
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(UTF_8);
    }

    private static String convertDecimaltoString(String str) {
        String words = "";

        float x = Float.parseFloat(str);
        String whole = convertNumberToWords((long) x);
        long y = Long.parseLong(str.substring(str.indexOf('.') + 1));
        String decimal = (convertDecimalToWords(y, str.substring(str.indexOf('.') + 1).length()));
        if (decimal == "")
            return whole;
        else
            words = whole + " point " + decimal;
        return words;
    }

    private static String convertDecimalToWords(long y, int length) {
        String words = "";
        String[] units = {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        while (y != 0) {
            words = words + " " + units[(int) (y / Math.pow(10, length - 1))];
            y = (long) (y % Math.pow(10, length - 1));
            length--;
        }
        return words;
    }

    public static String convertNumberToWords(long number) {
        if (number == 0) {
            return "Zero";
        }
        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        String[] thousands = {"", "Hundred", "Thousand", "Lakh", "Crore"};
        int i = 0;
        String words = "";
        while (number > 0) {
            String str = "";
            long j = number / 10000000;
            if (number >= 10000000) {
                if (number / 10000000 >= 100) {
                    words = convertNumberToWords(number / 10000000) + " " + thousands[4];
                    number = number % 10000000;
                } else {
                    int x = (int) number / 10000000;
                    System.out.println("X:" + x);
                    if (x > 20) {
                        int y = x / 10;
                        str = str + tens[y] + " ";
                        x = x % 10;
                        System.out.println("Y:" + y);

                    }
                    words = words + " " + str + units[x] + " " + thousands[4];
                    number = number % 10000000;
                }
            } else if (number >= 100000) {
                int x = (int) number / 100000;
                System.out.println("X:" + x);
                if (x > 20) {
                    int y = x / 10;
                    str = str + tens[y] + " ";
                    x = x % 10;
                    System.out.println("Y:" + y);
                }
                words = words + " " + str + units[x] + " " + thousands[3];
                number = number % 100000;
            } else if (number >= 1000) {
                int x = (int) number / 1000;
                System.out.println("X:" + x);
                if (x > 20) {
                    int y = x / 10;
                    str = str + tens[y] + " ";
                    x = x % 10;
                    System.out.println("Y:" + y);
                }
                words = words + " " + str + units[x] + " " + thousands[2];
                number = number % 1000;
            } else if (number >= 100) {
                int x = (int) number / 100;
                System.out.println("X:" + x);
                if (x > 20) {
                    int y = x / 10;
                    str = str + tens[y] + " ";
                    x = x % 10;
                    System.out.println("Y:" + y);
                }
                words = words + " " + str + units[x] + " " + thousands[1];
                number = number % 100;
            } else {
                int x = (int) number;
                System.out.println("X:" + x);
                if (x > 20) {
                    int y = x / 10;
                    str = str + tens[y] + " ";
                    x = x % 10;
                    System.out.println("Y:" + y);
                }
                words = words + " " + str + units[x] + " " + thousands[0];
                number = number / 100;
            }
        }
        System.out.println(words);
        return words.trim();
    }
}

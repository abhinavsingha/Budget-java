package com.sdd.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.layout.Document;
import com.sdd.response.DemoReportResponse;
import com.sdd.response.DemoReprotMain;
import com.sdd.service.HelperService;
import org.springframework.stereotype.Service;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.File;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class HelperServiceImpl implements HelperService {

  private static final String UTF_8 = "UTF-8";
  @Autowired private TemplateEngine templateEngine;

  @Override
  public String generatePDFDemo() {

    try {

      List<DemoReportResponse> demoReportResponseList = new ArrayList<>();

      DemoReportResponse d1 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d1);

      DemoReportResponse d2 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d2);

      DemoReportResponse d3 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d3);

      DemoReportResponse d4 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d4);

      DemoReportResponse d5 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d5);

      DemoReportResponse d6 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d6);

      DemoReportResponse d7 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d7);

      DemoReportResponse d8 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d8);

      DemoReportResponse d9 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d9);

      DemoReportResponse d11 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d11);

      DemoReportResponse d12 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d12);

      DemoReportResponse d13 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d13);

      DemoReportResponse d14 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d14);

      DemoReportResponse d15 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d15);

      DemoReportResponse d16 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d16);

      DemoReportResponse d17 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d17);

      DemoReportResponse d18 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d18);

      DemoReportResponse d19 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d19);

      DemoReportResponse d20 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d20);

      DemoReportResponse d21 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d21);

      DemoReportResponse d22 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d22);

      DemoReportResponse d23 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d23);

      DemoReportResponse d24 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d24);

      DemoReportResponse d25 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d25);

      DemoReportResponse d26 =
          new DemoReportResponse("Noida", "Diwakar Chauhan", "Hno 977/2, Ward-19", "Study");
      demoReportResponseList.add(d26);

      DemoReprotMain demoReprotMain = new DemoReprotMain();
      demoReprotMain.setDemoReportResponseList(demoReportResponseList);

      String baseUrl = "";
      baseUrl =
          FileSystems.getDefault().getPath("src", "main", "webapp").toUri().toURL().toString();
      baseUrl = baseUrl.replaceAll("file:/", "");

      String FolderPath = baseUrl.trim();

      String FolderPathProject = FolderPath + "/data/Budget/report/";

      String templateName = "";
      ObjectMapper oMapper = new ObjectMapper();
      Map<String, String> data = new HashMap<String, String>();
      templateName = "demo-report";
      data = oMapper.convertValue(demoReprotMain, Map.class);

      File folder = new File(FolderPathProject);
      if (!folder.exists()) {
        folder.mkdirs();
      }
      String folderPath = FolderPathProject + "4.pdf";
      File file = new File(folderPath);
      createPdf(templateName, data, file);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "success";
  }

  public void createPdf(String templateName, Map map, File outputFile) throws Exception {
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
    String baseUrl =
        FileSystems.getDefault().getPath("src", "main", "resources").toUri().toURL().toString();
    baseUrl += "assets/";
    renderer.setDocumentFromString(xHtml, baseUrl);
    renderer.layout();

    // And finally, we create the PDF:
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
}

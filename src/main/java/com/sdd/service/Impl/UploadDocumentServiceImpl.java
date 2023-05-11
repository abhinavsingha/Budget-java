package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.Document;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.sdd.entities.Authority;
import com.sdd.entities.ContigentBill;
import com.sdd.entities.FileUpload;
import com.sdd.entities.repository.AuthorityRepository;
import com.sdd.entities.repository.ContigentBillRepository;
import com.sdd.entities.repository.FileUploadRepository;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.response.ApiResponse;
import com.sdd.response.UplaodMainFormDocumentsResponse;
import com.sdd.service.UploadDocumentService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.Reader;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class UploadDocumentServiceImpl implements UploadDocumentService {


    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private ContigentBillRepository contigentBillRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private HeaderUtils headerUtils;


    @Override
    public ApiResponse<UplaodMainFormDocumentsResponse> fileUplaod(MultipartFile file) throws IOException {
        UplaodMainFormDocumentsResponse uplaodDocuments = new UplaodMainFormDocumentsResponse();

        File mainFilePath = new File (new File(".").getCanonicalPath() + HelperUtils.LASTFOLDERPATH);
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        if (!mainFilePath.exists()) {
            mainFilePath.mkdirs();
        }

        if (file == null || file.isEmpty()) {
            throw new SDDException(HttpStatus.BAD_REQUEST.value(), "DOCUMENT FILE CAN NOT BE BLANK.");
        }
        String fileExtension1 = getFileExtension(file);
        if (fileExtension1.equalsIgnoreCase(".pdf")) {
        } else {
            throw new SDDException(HttpStatus.BAD_REQUEST.value(), "FILE NOT SUPPORTED ");
        }

        String filename1 = ConverterUtils.getRandomString("");
        File mainPath1 = ConverterUtils.getComplaintPathOnly(fileExtension1, filename1, mainFilePath.getAbsolutePath());
        Path path1 = Paths.get(mainPath1.toString());
        InputStream in1 = new ByteArrayInputStream(file.getBytes());

        try {
            System.out.println(
                    "Number of bytes copied1: "
                            + Files.copy(in1, path1.toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING));

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUpload fileUpload = new FileUpload();
        fileUpload.setUploadID(HelperUtils.getDocumentId());
        fileUpload.setPathURL(HelperUtils.FILEPATH + filename1 + fileExtension1);
        fileUpload.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        fileUpload.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        fileUploadRepository.save(fileUpload);

        uplaodDocuments.setUploadDocId(fileUpload.getUploadID());
        uplaodDocuments.setUploadPathUrl(HelperUtils.FILEPATH + filename1 + fileExtension1);
        uplaodDocuments.setMessage("File upload successfully");

        return ResponseUtils.createSuccessResponse(uplaodDocuments, new TypeReference<UplaodMainFormDocumentsResponse>() {
        });


    }

    @Override
    public ApiResponse<FileUpload> getFilePath(String fileId) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);


        if (fileId == null || fileId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FILE ID CAN NOT BE BLANK");
        }
        FileUpload fileUpload = fileUploadRepository.findByUploadID(fileId);
        return ResponseUtils.createSuccessResponse(fileUpload, new TypeReference<FileUpload>() {
        });
    }


    public String getFileExtension(MultipartFile file) {
        String fileExtention = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        return fileExtention;
    }

    @Override
    public ApiResponse<FileUpload> getApprovedFilePath(String authGoupId,String type) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

        FileUpload fileUpload = new FileUpload();
        if (authGoupId == null || authGoupId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTH_GROUP ID CAN NOT BE BLANK");
        }
        if (type == null || type.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TYPE CAN NOT BE BLANK");
        }
        if(type.equalsIgnoreCase("CB")) {
            List<ContigentBill> cbdata = contigentBillRepository.findByAuthGroupIdAndIsFlag(authGoupId, "0");
            if(cbdata.size()>0) {
                String fileId=cbdata.get(0).getCbFilePath();
                FileUpload fileUp = fileUploadRepository.findByUploadID(fileId);
                if(fileUp!=null) {
                    fileUpload.setPathURL(fileUp.getPathURL());
                }else
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FILE NOT FOUND");
            }else
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "RECORD NOT FOUND");

        }
        else if(type.equalsIgnoreCase("BG") ||type.equalsIgnoreCase("BR")){
            List<Authority> authData=authorityRepository.findByAuthGroupId(authGoupId);
            if(authData.size()>0) {
                String fId=authData.get(0).getDocId();
                FileUpload fileUp = fileUploadRepository.findByUploadID(fId);
                if(fileUp!=null) {
                    fileUpload.setPathURL(fileUp.getPathURL());
                }else
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FILE NOT FOUND");
            }else
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "RECORD NOT FOUND");
        }

        return ResponseUtils.createSuccessResponse(fileUpload, new TypeReference<FileUpload>() {
        });
    }



}



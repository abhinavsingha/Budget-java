package com.sdd.utils;


import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class HelperUtils {
    public static String SYSTEMADMIN = "120";
    public static String UNITADMIN = "119";
    public static String BUDGETMANGER = "118";
    public static String BUDGETAPPROVER = "117";
    public static String CBCREATER = "116";
    public static String CBAPPROVER = "115";
    public static String CBVERIFER = "114";
    public static String VIEWER = "113";
    public static String HEADUNITID = "001321";


    public static String LASTFOLDERPATH = "/webapps/bmsreport";
    public static String FILEPATH = "https://icg.net.in/bmsreport/";

    public static String getDocumentId() {
        return "BM_DOC" + ConverterUtils.getRandomTimeStamp();
    }

    public static String getMangeInboxId() {
        return "IN_ID" + ConverterUtils.getRandomTimeStamp();
    }

    public static Timestamp getCurrentTimeStamp() {
        return new Timestamp(new Date().getTime());
    }


    public static String getAuthorityId() {
        return "AU_ID" + ConverterUtils.getRandomTimeStamp();
    }

    public static String getCdaId() {
        return "CDA_ID" + ConverterUtils.getRandomTimeStamp();
    }

    public static String getAuthorityGroupId() {
        return "AGU_ID" + ConverterUtils.getRandomTimeStamp();
    }


    public static String getContigentId() {
        return "CB_ID" + ConverterUtils.getRandomTimeStamp();
    }

    public static String getAllocationId() {
        return "ALL_ID" + ConverterUtils.getRandomTimeStamp();
    }
    public static String getStateId() {
        return "ST_ID" + ConverterUtils.getRandomTimeStamp();
    }

    public static String getFilterIdId() {
        return "FL_ID" + ConverterUtils.getRandomTimeStamp();
    }

    public static String getUnitRebased() {
        return "UB_ID" + ConverterUtils.getRandomTimeStamp();
    }

    public static String getBudgetAlloctionRefrensId() {
        return "BA_RID" + ConverterUtils.getRandomTimeStamp();
    }

    public static String getBudgetAlloctionId() {
        return "BA_AL" + ConverterUtils.getRandomTimeStamp();
    }

    public static String getBudgetAlloctionReportId() {
        return "BA_RP" + ConverterUtils.getRandomTimeStamp();
    }


    public static String getTransId() {
        return "TN_ID" + ConverterUtils.getRandomTimeStamp();
    }

    public static String getMsgTran() {
        return "MS_ID" + ConverterUtils.getRandomTimeStamp();
    }
}

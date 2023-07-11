package com.sdd.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class ContingentBillSaveRequest {

    private String budgetFinancialYearId;

    private String cbAmount;
    private String cbNumber;
    private String unit;
    private String cbDate;

    private String fileDate;
    private String fileNumber;
    private String docUploadDate;
    private String progressiveAmount;
    private String budgetHeadId;
    private String remark;
    private String vendorName;
    private String invoiceNo;
    private String gst;
    private String invoiceDate;
    private String sectionNumber;
    private String invoiceUploadId;


    private String onAccountOf;
    private String authorityDetails;

    ArrayList<AuthRequest> authList;

    private String contingentBilId;
    private String allocationTypeId;


    private List<CdaTransAllocationRequest> cdaParkingId;




}

package com.sdd.response;

import com.sdd.entities.*;
import com.sdd.request.CdaParkingCrAndDrResponse;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter
public class ContingentBillResponse {


    List<Authority> authoritiesList;

    private String cbId;
    private String allocationId;
    private String cbNo;
    private Timestamp cbDate;
    private BudgetFinancialYear finYear;
    private CgUnit cbUnitId;
    private BudgetHead budgetHeadID;
    private String cbAmount;
    private String status;
    private String isFlag;
    private Timestamp statusDate;
    private String remarks;
    private Timestamp createdOn;
    private Timestamp updatedOn;
    private String vendorName;
    private String invoiceNO;
    private FileUpload invoiceUploadId;
    private String invoiceDate;
    private String fileID;
    private String fileDate;
    private String progressiveAmount;

    private FileUpload cbFilePath;
    private String onAccountOf;
    private String authorityDetails;


    private List<CdaParkingCrAndDrResponse> cdaData;

}

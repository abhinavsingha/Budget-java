package com.sdd.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ContigentBill")
public class ContigentBill {
    @Id
    @Column(name = "CB_ID")
    private String cbId;

    @Column(name = "ALLOCATION_ID")
    private String allocationId;

    @Column(name = "CB_NO")
    private String cbNo;

    @Column(name = "CB_DATE")
    private Timestamp cbDate;

    @Column(name = "FIN_YEAR")
    private String finYear;

    @Column(name = "CB_UNIT_ID")
    private String cbUnitId;

    @Column(name = "BUDGET_HEAD_ID")
    private String budgetHeadID;

    @Column(name = "CB_AMOUNT")
    private String cbAmount;

    @Column(name = "Status")
    private String status;

    @Column(name = "STATUS_DATE")
    private Timestamp statusDate;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;

    @Column(name = "AUTH_GROUP_ID", nullable = false)
    private String authGroupId;

    @Column(name = "VENDOR_NAME", nullable = false)
    private String vendorName;

    @Column(name = "INVOICE_NO", nullable = false)
    private String invoiceNO;

    @Column(name = "INVOICE_DATE", nullable = false)
    private String invoiceDate;

    @Column(name = "FILE_ID", nullable = false)
    private String fileID;

    @Column(name = "FILE_DATE", nullable = false)
    private String fileDate;

    @Column(name = "INVOICE_UPLOAD_ID")
    private String invoiceUploadId;

    @Column(name = "PROGRESSIVE_AMOUNT", nullable = false)
    private String progressiveAmount;

    @Column(name = "ON_ACCOUNT_OF")
    private String onAccountOf;

    @Column(name = "AUTHORITY_DETAILS")
    private String authorityDetails;

    @Column(name = "CB_FILE_PATH")
    private String cbFilePath;

    @Column(name = "GST")
    private String gst;


    @Column(name = "IS_FLAG")
    private String isFlag;

    @Column(name = "IS_UPDATED")
    private String isUpdate;

}

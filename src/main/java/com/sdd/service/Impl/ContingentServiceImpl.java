package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.*;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.request.*;
import com.sdd.response.*;
import com.sdd.service.ContingentService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContingentServiceImpl implements ContingentService {

    @Autowired
    BudgetFinancialYearRepository budgetFinancialYearRepository;

    @Autowired
    BudgetAllocationRepository budgetAllocationRepository;

    @Autowired
    AmountUnitRepository amountUnitRepository;

    @Autowired
    AllocationRepository allocationRepository;

    @Autowired
    SubHeadRepository subHeadRepository;

    @Autowired
    CgUnitRepository cgUnitRepository;

    @Autowired
    FileUploadRepository fileUploadRepository;

    @Autowired
    ContigentBillRepository contigentBillRepository;

    @Autowired
    MangeInboxOutBoxRepository mangeInboxOutBoxRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HeaderUtils headerUtils;

    @Autowired
    private HrDataRepository hrDataRepository;

    @Override
    public ApiResponse<ContingentSaveResponse> saveContingentBill(ArrayList<ContingentBillSaveRequest> contingentBillSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CONTINGENT BILL ENTRY");
        } else {
            if (hrData.getRoleId().contains(HelperUtils.CBCREATER)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CONTINGENT BILL ENTRY");
            }
        }

        ContingentSaveResponse contingentSaveResponse = new ContingentSaveResponse();
        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {

            ContingentBillSaveRequest contingentBillSaveRequest = contingentBillSaveRequestList.get(i);


            if (contingentBillSaveRequest.getBudgetFinancialYearId() == null || contingentBillSaveRequest.getBudgetFinancialYearId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getCbAmount() == null || contingentBillSaveRequest.getCbAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB AMOUNT CAN NOT BE BLANK");
            }


            if (contingentBillSaveRequest.getCbDate() == null || contingentBillSaveRequest.getCbDate().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB DATE CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getUnit() == null || contingentBillSaveRequest.getUnit().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB UNIT ID CAN NOT BE BLANK");
            }
            CgUnit cgFromUnit = cgUnitRepository.findByUnit(contingentBillSaveRequest.getUnit());
            if (cgFromUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CB UNIT ID");
            }

            if (contingentBillSaveRequest.getCbNumber() == null || contingentBillSaveRequest.getCbNumber().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB NUMBER CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getRemark() == null || contingentBillSaveRequest.getRemark().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getInvoiceNo() == null || contingentBillSaveRequest.getInvoiceNo().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVOICE NUMBER NOT BE BLANK");
            }
            if (contingentBillSaveRequest.getInvoiceDate() == null || contingentBillSaveRequest.getInvoiceDate().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVOICE DATE NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getBudgetHeadId() == null || contingentBillSaveRequest.getBudgetHeadId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET HEAD ID NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getProgressiveAmount() == null || contingentBillSaveRequest.getProgressiveAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PROGRESSIVE AMOUNT ID NOT BE BLANK");
            }
            if (contingentBillSaveRequest.getRemark() == null || contingentBillSaveRequest.getRemark().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getDocUploadDate() == null || contingentBillSaveRequest.getDocUploadDate().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT UPLOAD DATE CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getVendorName() == null || contingentBillSaveRequest.getVendorName().isEmpty()) {
                contingentBillSaveRequest.setVendorName("");
            }

            BudgetHead subHeadData = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(contingentBillSaveRequest.getBudgetHeadId());
            if (subHeadData == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID BUDGET HEAD ID ");
            }

            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(contingentBillSaveRequest.getBudgetFinancialYearId());
            if (budgetFinancialYear == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
            }

            ConverterUtils.checkDateIsvalidOrNor(contingentBillSaveRequest.getCbDate());
            ConverterUtils.checkDateIsvalidOrNor(contingentBillSaveRequest.getInvoiceDate());
            ConverterUtils.checkDateIsvalidOrNor(contingentBillSaveRequest.getDocUploadDate());

            for (Integer j = 0; j < contingentBillSaveRequestList.get(i).getAuthList().size(); j++) {

                FileUpload fileUpload = fileUploadRepository.findByUploadID(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDocId());
                if (fileUpload == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DOCUMENT ID ");
                }
                if (contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDocId() == null || contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDocId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT ID CAN NOT BE BLANK");
                }
                if (contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDate() == null || contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDate().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY DATE  CAN NOT BE BLANK");
                }

                CgUnit getAuthUnitId = cgUnitRepository.findByUnit(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthUnitId());
                if (getAuthUnitId == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTHORITY UNIT ID");
                }
                ConverterUtils.checkDateIsvalidOrNor(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDate());

            }

        }


        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {

            double amount = 0;
            List<BudgetAllocation> budgetAloocation = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), contingentBillSaveRequestList.get(i).getBudgetFinancialYearId(), contingentBillSaveRequestList.get(i).getBudgetHeadId(), contingentBillSaveRequestList.get(i).getAllocationTypeId(), "Approved", "0", "0");

            for (Integer m = 0; m < budgetAloocation.size(); m++) {

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAloocation.get(m).getAmountType());
                amount = amount + (Double.parseDouble(budgetAloocation.get(m).getAllocationAmount()) * amountUnit.getAmount());

            }

            double allocationAmount = Double.parseDouble(contingentBillSaveRequestList.get(i).getCbAmount());
            BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(contingentBillSaveRequestList.get(i).getBudgetHeadId());

            if (allocationAmount > amount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB AMOUNT CAN NOT LARGER THAN REMAINING AMOUNT FOR " + budgetHeadId.getSubHeadDescr());
            }


        }


        List<HrData> hrDataList = hrDataRepository.findByUnitIdAndIsActive(hrData.getUnitId(), "1");
        if (hrDataList.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO ROLE ASSIGN FOR THIS UNIT.");
        }

        String approverCbPId = "";
        String veriferCbPId = "";
        for (Integer k = 0; k < hrDataList.size(); k++) {
            HrData findHrData = hrDataList.get(k);
            if (findHrData.getRoleId().contains(HelperUtils.CBVERIFER)) {
                veriferCbPId = findHrData.getPid();
            }
            if (findHrData.getRoleId().contains(HelperUtils.CBAPPROVER)) {
                approverCbPId = findHrData.getPid();
            }
        }

        if (approverCbPId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO CB APPROVE ROLE FOUND THIS UNIT.PLEASE ADD  ROLE FIRST");
        }
        if (veriferCbPId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO CB VERIFIER ROLE FOUND THIS UNIT.PLEASE ADD  ROLE FIRST");
        }


        String authGroupId = HelperUtils.getAuthorityGroupId();
        String toUnitId = "";

        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {

            ContingentBillSaveRequest contingentBillSaveRequest = contingentBillSaveRequestList.get(i);

            for (Integer j = 0; j < contingentBillSaveRequestList.get(i).getAuthList().size(); j++) {


                Authority authoritySaveData = new Authority();
                authoritySaveData.setAuthorityId(HelperUtils.getAuthorityId());
                authoritySaveData.setAuthority(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthority());
                authoritySaveData.setAuthDate(ConverterUtils.convertDateTotimeStamp(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDate()));
                authoritySaveData.setAuthUnit(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthUnitId());
                authoritySaveData.setDocId(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDocId());
                authoritySaveData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                authoritySaveData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                authoritySaveData.setAuthGroupId(authGroupId);
                authoritySaveData.setRemarks(contingentBillSaveRequestList.get(i).getAuthList().get(j).getRemark());
                authorityRepository.save(authoritySaveData);

            }

            ContigentBill contigentBill = new ContigentBill();

            contigentBill.setCbId(HelperUtils.getContigentId());
            contigentBill.setCbNo(contingentBillSaveRequest.getCbNumber());
            contigentBill.setCbAmount(ConverterUtils.addDecimalPoint(contingentBillSaveRequest.getCbAmount()));
            contigentBill.setCbDate(ConverterUtils.convertDateTotimeStamp(contingentBillSaveRequest.getCbDate()));
            contigentBill.setCbUnitId(contingentBillSaveRequest.getUnit());
            toUnitId = contingentBillSaveRequest.getUnit();
            contigentBill.setFinYear(contingentBillSaveRequest.getBudgetFinancialYearId());
            contigentBill.setStatus("Pending");
            contigentBill.setRemarks(contingentBillSaveRequest.getRemark());
            contigentBill.setStatusDate(HelperUtils.getCurrentTimeStamp());
            contigentBill.setAuthGroupId(authGroupId);
            contigentBill.setIsFlag("0");
            contigentBill.setVendorName(contingentBillSaveRequest.getVendorName());

            contigentBill.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            contigentBill.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            contigentBill.setInvoiceNO(contingentBillSaveRequest.getInvoiceNo());
            contigentBill.setBudgetHeadID(contingentBillSaveRequest.getBudgetHeadId());
            contigentBill.setInvoiceDate(contingentBillSaveRequest.getInvoiceDate());
            contigentBill.setFileDate(contingentBillSaveRequest.getFileDate());
            contigentBill.setFileID(contingentBillSaveRequest.getFileNumber());
            contigentBill.setProgressiveAmount(contingentBillSaveRequest.getProgressiveAmount());
            contigentBill.setOnAccountOf(contingentBillSaveRequest.getOnAccountOf());
            contigentBill.setAuthorityDetails(contingentBillSaveRequest.getAuthorityDetails());
            contigentBill.setInvoiceUploadId(contingentBillSaveRequest.getInvoiceUploadId());


            contigentBillRepository.save(contigentBill);

        }


        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {

            double amount = 0;
            List<BudgetAllocation> budgetAllocation = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), contingentBillSaveRequestList.get(i).getBudgetFinancialYearId(), contingentBillSaveRequestList.get(i).getBudgetHeadId(), contingentBillSaveRequestList.get(i).getAllocationTypeId(), "Approved", "0", "0");

            for (Integer m = 0; m < budgetAllocation.size(); m++) {

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocation.get(m).getAmountType());
                amount = amount + (Double.parseDouble(budgetAllocation.get(m).getAllocationAmount()) * amountUnit.getAmount());
            }


            double allocationAmount = Double.parseDouble(contingentBillSaveRequestList.get(i).getCbAmount());

            for (Integer m = 0; m < budgetAllocation.size(); m++) {

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocation.get(m).getAmountType());
                Double reminingBalance = (amount - allocationAmount);

                budgetAllocation.get(m).setBalanceAmount(ConverterUtils.addDecimalPoint((reminingBalance / amountUnit.getAmount()) + ""));
                budgetAllocationRepository.save(budgetAllocation.get(m));

            }

        }


        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Contingent Bill");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(toUnitId);
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setGroupId(authGroupId);
        mangeInboxOutbox.setFromUnit(toUnitId);
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setState("VE");
        mangeInboxOutbox.setIsBgcg("CB");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


//        MessageTran messageTran = new MessageTran();
//        messageTran.setMsgTransId(HelperUtils.getMsgTran());
//        messageTran.setMsgId(saveMangeApi.getMangeInboxId());
//        messageTran.setMsgState("VE");
//        messageTran.setCreatedOn(HelperUtils.getCurrentTimeStamp());
//        messageTran.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//        msgTransRepository.save(messageTran);

        contingentSaveResponse.setMsg("Data Save Successfully");

        return ResponseUtils.createSuccessResponse(contingentSaveResponse, new TypeReference<ContingentSaveResponse>() {
        });
    }

    @Override
    public ApiResponse<ContingentSaveResponse> updateContingentBill(ArrayList<ContingentBillSaveRequest> contingentBillSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE CONTINGENT BILL ENTRY");
        } else {
            if (hrData.getRoleId().contains(HelperUtils.CBCREATER)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE CONTINGENT BILL ENTRY");
            }
        }


        ContingentSaveResponse contingentSaveResponse = new ContingentSaveResponse();

        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {

            ContingentBillSaveRequest contingentBillSaveRequest = contingentBillSaveRequestList.get(i);

            ContigentBill contigentBill = contigentBillRepository.findByCbIdAndIsFlag(contingentBillSaveRequest.getContingentBilId(), "0");
            if (contigentBill == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CONTINGENT BILL ID");
            }

            if (contigentBill.getStatus().equalsIgnoreCase("Pending") || contigentBill.getStatus().equalsIgnoreCase("Rejected")) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "APPROVED CONTINGENT BILL CAN NOT BE UPDATED");
            }


            if (contingentBillSaveRequest.getBudgetFinancialYearId() == null || contingentBillSaveRequest.getBudgetFinancialYearId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getCbAmount() == null || contingentBillSaveRequest.getCbAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB AMOUNT CAN NOT BE BLANK");
            }
            if (contingentBillSaveRequest.getProgressiveAmount() == null || contingentBillSaveRequest.getProgressiveAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PROGRESSIVE AMOUNT CAN NOT BE BLANK");
            }
            if (contingentBillSaveRequest.getCbDate() == null || contingentBillSaveRequest.getCbDate().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB DATE CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getAllocationTypeId() == null || contingentBillSaveRequest.getAllocationTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID UPLOAD ID CAN NOT BE BLANK");
            }

//            if (!(contingentBillSaveRequest.getUnit().equalsIgnoreCase(hrData.getUnitId()))) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB UNIT IS NOT SAME FOR LOGIN UNIT");
//            }

            CgUnit cgFromUnit = cgUnitRepository.findByUnit(contingentBillSaveRequest.getUnit());
            if (cgFromUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CB UNIT ID");
            }


            if (contingentBillSaveRequest.getCbNumber() == null || contingentBillSaveRequest.getCbNumber().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB NUMBER CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getFileNumber() == null || contingentBillSaveRequest.getFileNumber().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FILE NUMBER CAN NOT BE BLANK");
            }


            if (contingentBillSaveRequest.getRemark() == null || contingentBillSaveRequest.getRemark().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getInvoiceNo() == null || contingentBillSaveRequest.getInvoiceNo().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVOICE NUMBER NOT BE BLANK");
            }
            if (contingentBillSaveRequest.getInvoiceDate() == null || contingentBillSaveRequest.getInvoiceDate().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVOICE DATE NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getBudgetHeadId() == null || contingentBillSaveRequest.getBudgetHeadId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET HEAD ID NOT BE BLANK");
            }



            if (contingentBillSaveRequest.getDocUploadDate() == null || contingentBillSaveRequest.getDocUploadDate().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT UPLOAD DATE CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getOnAccountOf() == null || contingentBillSaveRequest.getOnAccountOf().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ON ACCOUNT OFF CAN NOT BE BLANK");
            }

            if (contingentBillSaveRequest.getAuthorityDetails() == null || contingentBillSaveRequest.getAuthorityDetails().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY DETAILS CAN NOT BE BLANK");
            }


            if (contingentBillSaveRequest.getVendorName() == null || contingentBillSaveRequest.getVendorName().isEmpty()) {
                contingentBillSaveRequest.setVendorName("");
            } else {

            }

            BudgetHead subHeadData = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(contingentBillSaveRequest.getBudgetHeadId());
            if (subHeadData == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID BUDGET HEAD ID ");
            }


            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(contingentBillSaveRequest.getBudgetFinancialYearId());
            if (budgetFinancialYear == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
            }


            if (contingentBillSaveRequest.getInvoiceUploadId() == null || contingentBillSaveRequest.getInvoiceUploadId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVOICE UPLOAD ID CAN NOT BE BLANK");
            }


            if (contingentBillSaveRequest.getAllocationTypeId() == null || contingentBillSaveRequest.getAllocationTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID UPLOAD ID CAN NOT BE BLANK");
            }

            FileUpload invoiceUploadIdData = fileUploadRepository.findByUploadID(contingentBillSaveRequest.getInvoiceUploadId());
            if (invoiceUploadIdData == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID INVOICE UPLOAD ID ");
            }

            ConverterUtils.checkDateIsvalidOrNor(contingentBillSaveRequest.getCbDate());
            ConverterUtils.checkDateIsvalidOrNor(contingentBillSaveRequest.getInvoiceDate());
            ConverterUtils.checkDateIsvalidOrNor(contingentBillSaveRequest.getDocUploadDate());

            for (Integer j = 0; j < contingentBillSaveRequestList.get(i).getAuthList().size(); j++) {


                Authority authoritySaveData = authorityRepository.findByAuthorityId(contingentBillSaveRequest.getAuthList().get(j).getAuthorityId());
                if (authoritySaveData == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AUTHORITY ID");
                }

                FileUpload fileUpload = fileUploadRepository.findByUploadID(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDocId());
                if (fileUpload == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DOCUMENT ID ");
                }
                if (contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDocId() == null || contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDocId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT ID CAN NOT BE BLANK");
                }
                if (contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDate() == null || contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDate().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY DATE  CAN NOT BE BLANK");
                }

                CgUnit getAuthUnitId = cgUnitRepository.findByUnit(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthUnitId());
                if (getAuthUnitId == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTHORITY UNIT ID");
                }
                ConverterUtils.checkDateIsvalidOrNor(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDate());

            }


        }


        List<HrData> hrDataList = hrDataRepository.findByUnitIdAndIsActive(hrData.getUnitId(), "1");
        if (hrDataList.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO ROLE ASSIGN FOR THIS UNIT.");
        }

        String approverCbPId = "";
        String veriferCbPId = "";
        for (Integer k = 0; k < hrDataList.size(); k++) {
            HrData findHrData = hrDataList.get(k);
            if (findHrData.getRoleId().contains(HelperUtils.CBVERIFER)) {
                veriferCbPId = findHrData.getPid();
            }
            if (findHrData.getRoleId().contains(HelperUtils.CBAPPROVER)) {
                approverCbPId = findHrData.getPid();
            }
        }

        if (approverCbPId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO CB APPROVE ROLE FOUND THIS UNIT.PLEASE ADD  ROLE FIRST");
        }
        if (veriferCbPId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO CB VERIFIER ROLE FOUND THIS UNIT.PLEASE ADD  ROLE FIRST");
        }

        String authGroupId = "";
        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {


            ContigentBill contigentBill = contigentBillRepository.findByCbIdAndIsFlag(contingentBillSaveRequestList.get(i).getContingentBilId(), "0");

            ContingentBillSaveRequest contingentBillSaveRequest = contingentBillSaveRequestList.get(i);


            for (Integer j = 0; j < contingentBillSaveRequestList.get(i).getAuthList().size(); j++) {
                Authority authoritySaveData = authorityRepository.findByAuthorityId(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthorityId());

                authoritySaveData.setAuthorityId(authoritySaveData.getAuthorityId());
                authoritySaveData.setAuthority(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthority());
                authoritySaveData.setAuthDate(ConverterUtils.convertDateTotimeStamp(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDate()));
                authoritySaveData.setAuthUnit(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthUnitId());
                authoritySaveData.setDocId(contingentBillSaveRequestList.get(i).getAuthList().get(j).getAuthDocId());
                authoritySaveData.setRemarks(contingentBillSaveRequestList.get(i).getAuthList().get(j).getRemark());
                authoritySaveData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                authorityRepository.save(authoritySaveData);

            }

            contigentBill.setCbId(contigentBill.getCbId());
            contigentBill.setCbNo(contingentBillSaveRequest.getCbNumber());
            contigentBill.setCbAmount(ConverterUtils.addDecimalPoint(contingentBillSaveRequest.getCbAmount()));
            contigentBill.setCbDate(ConverterUtils.convertDateTotimeStamp(contingentBillSaveRequest.getCbDate()));
            contigentBill.setCbUnitId(contingentBillSaveRequest.getUnit());
            contigentBill.setFinYear(contingentBillSaveRequest.getBudgetFinancialYearId());
            contigentBill.setStatus("Pending");
            contigentBill.setRemarks(contingentBillSaveRequest.getRemark());
            contigentBill.setStatusDate(HelperUtils.getCurrentTimeStamp());
            contigentBill.setVendorName(contingentBillSaveRequest.getVendorName());
            contigentBill.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            contigentBill.setBudgetHeadID(contingentBillSaveRequest.getBudgetHeadId());

            contigentBill.setOnAccountOf(contingentBillSaveRequest.getOnAccountOf());
            contigentBill.setAuthorityDetails(contingentBillSaveRequest.getAuthorityDetails());
            contigentBill.setInvoiceNO(contingentBillSaveRequest.getInvoiceNo());
            contigentBill.setInvoiceUploadId(contingentBillSaveRequest.getInvoiceUploadId());
            contigentBill.setInvoiceDate(contingentBillSaveRequest.getInvoiceDate());
            contigentBill.setFileDate(contingentBillSaveRequest.getFileDate());
            contigentBill.setFileID(contingentBillSaveRequest.getFileNumber());
            contigentBill.setProgressiveAmount(contingentBillSaveRequest.getProgressiveAmount());
            authGroupId = contigentBill.getAuthGroupId();
            contigentBillRepository.save(contigentBill);
        }


        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {

            double amount = 0;
            List<BudgetAllocation> budgetAloocation = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), contingentBillSaveRequestList.get(i).getBudgetFinancialYearId(), contingentBillSaveRequestList.get(i).getBudgetHeadId(), contingentBillSaveRequestList.get(i).getAllocationTypeId(), "Approved", "0", "0");


            for (Integer m = 0; m < budgetAloocation.size(); m++) {
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAloocation.get(m).getAmountType());
                amount = amount + (Double.parseDouble(budgetAloocation.get(m).getAllocationAmount()) * amountUnit.getAmount());
            }


            double allocationAmount = Double.parseDouble(contingentBillSaveRequestList.get(i).getCbAmount());

            for (Integer m = 0; m < budgetAloocation.size(); m++) {
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAloocation.get(m).getAmountType());
                Double reminingBalance = (amount - allocationAmount);
                budgetAloocation.get(m).setBalanceAmount(ConverterUtils.addDecimalPoint((reminingBalance / amountUnit.getAmount()) + ""));
                budgetAllocationRepository.save(budgetAloocation.get(m));

            }
        }

        MangeInboxOutbox mangeInboxOutbox =  mangeInboxOutBoxRepository.findByGroupIdAndToUnit(authGroupId, hrData.getUnitId());

        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Contingent Bill");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrData.getUnitId());
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setGroupId(authGroupId);
        mangeInboxOutbox.setFromUnit(hrData.getUnitId());
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setState("VE");
        mangeInboxOutbox.setIsBgcg("CB");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);




        contingentSaveResponse.setMsg("Data Update Successfully");
        return ResponseUtils.createSuccessResponse(contingentSaveResponse, new TypeReference<ContingentSaveResponse>() {
        });
    }

    @Override
    public ApiResponse<List<ContingentBillResponse>> getContingentBill() {

        ArrayList<ContingentBillResponse> contingentBillListData = new ArrayList<ContingentBillResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.");
        }

        List<ContigentBill> cbData = contigentBillRepository.findByCbUnitIdAndIsFlag(hrData.getUnitId(), "0");
        if (cbData.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND.");
        }

        for (Integer j = 0; j < cbData.size(); j++) {
            ContingentBillResponse contingentBill = new ContingentBillResponse();
            ContigentBill contigentBill = cbData.get(j);

            List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(contigentBill.getAuthGroupId());

            contingentBill.setCbAmount(ConverterUtils.addDecimalPoint(contigentBill.getCbAmount()));
            contingentBill.setCbDate(contigentBill.getCbDate());
            contingentBill.setCbId(contigentBill.getCbId());
            contingentBill.setCbNo(contigentBill.getCbNo());
            contingentBill.setRemarks(contigentBill.getFileID());
            contingentBill.setStatus(contigentBill.getStatus());
            contingentBill.setStatusDate(contigentBill.getStatusDate());
            contingentBill.setCreatedOn(contigentBill.getCreatedOn());
            contingentBill.setUpdatedOn(contigentBill.getUpdatedOn());
            contingentBill.setVendorName(contigentBill.getVendorName());
            contingentBill.setInvoiceDate(contigentBill.getInvoiceDate());
            contingentBill.setFileID(contigentBill.getFileID());
            contingentBill.setInvoiceNO(contigentBill.getInvoiceNO());
            contingentBill.setCbFilePath(fileUploadRepository.findByUploadID(contigentBill.getCbFilePath()));
            contingentBill.setAuthorityDetails(contigentBill.getAuthorityDetails());
            contingentBill.setOnAccountOf(contigentBill.getOnAccountOf());

            contingentBill.setFileDate(contigentBill.getFileDate());
            contingentBill.setProgressiveAmount(contigentBill.getProgressiveAmount());

            CgUnit cgUnit = cgUnitRepository.findByUnit(contigentBill.getCbUnitId());
            contingentBill.setCbUnitId(cgUnit);

            FileUpload invoiceNoData = fileUploadRepository.findByUploadID(contigentBill.getInvoiceUploadId());

            contingentBill.setInvoiceUploadId(invoiceNoData);

            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(contigentBill.getFinYear());
            contingentBill.setFinYear(budgetFinancialYear);

            contingentBill.setBudgetHeadID(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(contigentBill.getBudgetHeadID()));

            contingentBill.setAuthoritiesList(authoritiesList);
            contingentBillListData.add(contingentBill);
        }

        return ResponseUtils.createSuccessResponse(contingentBillListData, new TypeReference<List<ContingentBillResponse>>() {
        });
    }

    @Override
    public ApiResponse<List<ContingentBillResponse>> getContingentBillGroupId(String groupId) {

        ArrayList<ContingentBillResponse> contingentBillListData = new ArrayList<ContingentBillResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.");
        }

        if (groupId == null || groupId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GROUP ID ID NOT BE BLANK");
        }

        List<ContigentBill> cbData = contigentBillRepository.findByAuthGroupIdAndIsFlag(groupId, "0");
        if (cbData.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND.");
        }

        for (Integer j = 0; j < cbData.size(); j++) {
            ContingentBillResponse contingentBill = new ContingentBillResponse();
            ContigentBill contigentBill = cbData.get(j);

            List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(contigentBill.getAuthGroupId());

            contingentBill.setCbAmount(ConverterUtils.addDecimalPoint(contigentBill.getCbAmount()));
            contingentBill.setCbDate(contigentBill.getCbDate());
            contingentBill.setCbId(contigentBill.getCbId());
            contingentBill.setCbNo(contigentBill.getCbNo());

            if (contigentBill.getCbFilePath() != null) {
                contingentBill.setIsFlag("1");
            } else {
                contingentBill.setIsFlag("0");
            }


            contingentBill.setStatus(contigentBill.getStatus());
            contingentBill.setStatusDate(contigentBill.getStatusDate());
            contingentBill.setRemarks(contigentBill.getRemarks());
            contingentBill.setCreatedOn(contigentBill.getCreatedOn());
            contingentBill.setUpdatedOn(contigentBill.getUpdatedOn());
            contingentBill.setVendorName(contigentBill.getVendorName());
            contingentBill.setInvoiceDate(contigentBill.getInvoiceDate());
            contingentBill.setFileID(contigentBill.getFileID());
            contingentBill.setInvoiceNO(contigentBill.getInvoiceNO());

            contingentBill.setAuthorityDetails(contigentBill.getAuthorityDetails());
            contingentBill.setOnAccountOf(contigentBill.getOnAccountOf());

            contingentBill.setFileDate(contigentBill.getFileDate());
            contingentBill.setProgressiveAmount(contigentBill.getProgressiveAmount());

            contingentBill.setCbFilePath(fileUploadRepository.findByUploadID(contigentBill.getCbFilePath()));

            CgUnit cgUnit = cgUnitRepository.findByUnit(contigentBill.getCbUnitId());
            contingentBill.setCbUnitId(cgUnit);

            FileUpload invoiceNoData = fileUploadRepository.findByUploadID(contigentBill.getInvoiceUploadId());
            contingentBill.setInvoiceUploadId(invoiceNoData);

            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(contigentBill.getFinYear());
            contingentBill.setFinYear(budgetFinancialYear);

            contingentBill.setBudgetHeadID(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(contigentBill.getBudgetHeadID()));
            contingentBill.setAuthoritiesList(authoritiesList);
            contingentBillListData.add(contingentBill);
        }

        return ResponseUtils.createSuccessResponse(contingentBillListData, new TypeReference<List<ContingentBillResponse>>() {
        });
    }

    @Override
    public ApiResponse<ContingentSaveResponse> approveContingentBill(ApproveContigentBillRequest approveContigentBillRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO APPROVE CB");
        } else {
            if (hrData.getRoleId().contains(HelperUtils.CBAPPROVER)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO APPROVE APPROVE CB");
            }
        }

        if (approveContigentBillRequest.getGroupId() == null || approveContigentBillRequest.getGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB GROUP ID NOT BE BLANK");
        }

        if (approveContigentBillRequest.getStatus().equalsIgnoreCase("Rejected") || approveContigentBillRequest.getStatus().equalsIgnoreCase("Approved")) {

        } else {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID STATUS");
        }
        List<ContigentBill> cbData = contigentBillRepository.findByAuthGroupIdAndIsFlag(approveContigentBillRequest.getGroupId(), "0");
        if (cbData.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND.");
        }

//        for (Integer j = 0; j < cbData.size(); j++) {
//            ContigentBill contigentBill = cbData.get(j);
//            if (contigentBill.getStatus().equalsIgnoreCase("Verified")) {
//
//            } else if (contigentBill.getStatus().equalsIgnoreCase("Pending") || contigentBill.getStatus().equalsIgnoreCase("Rejected")) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PENDING OR REJECTED CONTINGENT BILL CAN NOT BE UPDATED.VERIFIED FIRST");
//            } else {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "APPROVED CONTINGENT BILL OR VERIFIED CONTINGENT BILL CAN NOT BE UPDATED");
//            }
//        }


        if (approveContigentBillRequest.getStatus().equalsIgnoreCase("Verified")) {
            if (approveContigentBillRequest.getRemarks() == null || approveContigentBillRequest.getRemarks().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
            }
        }

        String status = "";
        for (Integer j = 0; j < cbData.size(); j++) {
            ContigentBill contigentBill = cbData.get(j);
            contigentBill.setStatus(approveContigentBillRequest.getStatus());
            contigentBill.setRemarks(approveContigentBillRequest.getRemarks());
            contigentBill.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            status = approveContigentBillRequest.getStatus();
            contigentBillRepository.save(contigentBill);
        }


        MangeInboxOutbox mangeInboxOutbox = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(approveContigentBillRequest.getGroupId(), hrData.getUnitId());
        if (mangeInboxOutbox != null) {
            String toUnit = mangeInboxOutbox.getToUnit();
            String fromUnit = mangeInboxOutbox.getFromUnit();

            mangeInboxOutbox.setFromUnit(toUnit);
            mangeInboxOutbox.setToUnit(fromUnit);

            if (status.equalsIgnoreCase("Approved")) {
                mangeInboxOutbox.setState("CR");
                mangeInboxOutbox.setStatus("Fully Approved");
            } else {
                mangeInboxOutbox.setState("AP");
                mangeInboxOutbox.setStatus(approveContigentBillRequest.getStatus());
            }


            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        }
        ContingentSaveResponse contingentSaveResponse = new ContingentSaveResponse();
        contingentSaveResponse.setMsg("Data " + approveContigentBillRequest.getStatus() + " Successfully");
        return ResponseUtils.createSuccessResponse(contingentSaveResponse, new TypeReference<ContingentSaveResponse>() {
        });
    }

    @Override
    public ApiResponse<DefaultResponse> updateFinalStatus(UploadCBRequest approveContigentBillRequest) throws IOException {

        DefaultResponse defaultResponse = new DefaultResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);


        if (approveContigentBillRequest.getGroupId() == null || approveContigentBillRequest.getGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB GROUP ID NOT BE BLANK");
        }
        if (approveContigentBillRequest.getDocId() == null || approveContigentBillRequest.getDocId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT ID NOT BE BLANK");
        }

        FileUpload fileUpload = fileUploadRepository.findByUploadID(approveContigentBillRequest.getDocId());
        if (fileUpload == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DOCUMENT ID.");
        }

        List<ContigentBill> cbData = contigentBillRepository.findByAuthGroupIdAndIsFlag(approveContigentBillRequest.getGroupId(), "0");
        if (cbData.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO CB DATA FOUND.");
        }

        for (Integer j = 0; j < cbData.size(); j++) {
            ContigentBill contigentBill = cbData.get(j);
            if (contigentBill.getStatus().equalsIgnoreCase("Pending") || contigentBill.getStatus().equalsIgnoreCase("Rejected") || contigentBill.getStatus().equalsIgnoreCase("Verified")) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "THIS CONTINGENT BILL NOT BE APPROVED");
            }

            if (contigentBill.getCbFilePath() != null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "THIS CONTINGENT BILL ALREADY UPDATED");
            }

        }

        for (Integer j = 0; j < cbData.size(); j++) {
            ContigentBill contigentBill = cbData.get(j);
            contigentBill.setCbFilePath(approveContigentBillRequest.getDocId());
            contigentBill.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            contigentBillRepository.save(contigentBill);
        }

        defaultResponse.setMsg("Data update successfully");

        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });


    }

    @Override
    public ApiResponse<ContingentSaveResponse> verifyContingentBill(ApproveContigentBillRequest approveContigentBillRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO APPROVE CB");
        } else {
            if (hrData.getRoleId().contains(HelperUtils.CBAPPROVER)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO APPROVE APPROVE CB");
            }
        }

        if (approveContigentBillRequest.getGroupId() == null || approveContigentBillRequest.getGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB GROUP ID NOT BE BLANK");
        }

        if (approveContigentBillRequest.getStatus().equalsIgnoreCase("Rejected") || approveContigentBillRequest.getStatus().equalsIgnoreCase("Pending") || approveContigentBillRequest.getStatus().equalsIgnoreCase("Verified")) {

        } else {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID STATUS");
        }
        List<ContigentBill> cbData = contigentBillRepository.findByAuthGroupIdAndIsFlag(approveContigentBillRequest.getGroupId(), "0");
        if (cbData.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND.");
        }

//        for (Integer j = 0; j < cbData.size(); j++) {
//            ContigentBill contigentBill = cbData.get(j);
//            if (contigentBill.getStatus().equalsIgnoreCase("Approved") || contigentBill.getStatus().equalsIgnoreCase("Verified")) {
//
//            } else {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "APPROVED CONTINGENT BILL AND VERIFIED CONTINGENT BILL CAN NOT BE UPDATED");
//            }
//        }

        String status = "";
        for (Integer j = 0; j < cbData.size(); j++) {
            ContigentBill contigentBill = cbData.get(j);
            contigentBill.setStatus(approveContigentBillRequest.getStatus());
            contigentBill.setRemarks(approveContigentBillRequest.getRemarks());
            contigentBill.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            status = approveContigentBillRequest.getStatus();
            contigentBillRepository.save(contigentBill);
        }


        MangeInboxOutbox mangeInboxOutbox = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(approveContigentBillRequest.getGroupId(), hrData.getUnitId());
        if (mangeInboxOutbox != null) {


            if (status.equalsIgnoreCase("Verified")) {
                mangeInboxOutbox.setState("AP");
            } else if (status.equalsIgnoreCase("Rejected")) {
                mangeInboxOutbox.setState("CR");
            }

            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setStatus(approveContigentBillRequest.getStatus());
            MangeInboxOutbox saveMangeApi = mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        }
        ContingentSaveResponse contingentSaveResponse = new ContingentSaveResponse();
        contingentSaveResponse.setMsg("Data " + approveContigentBillRequest.getStatus() + " Successfully");
        return ResponseUtils.createSuccessResponse(contingentSaveResponse, new TypeReference<ContingentSaveResponse>() {
        });
    }

}

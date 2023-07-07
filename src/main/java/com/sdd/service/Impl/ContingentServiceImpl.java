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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    CdaParkingTransRepository cdaParkingTransRepository;

    @Autowired
    CdaParkingCrAndDrRepository parkingCrAndDrRepository;

    @Autowired
    CdaParkingRepository cdaParkingRepository;

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
    @Transactional
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

            if (contingentBillSaveRequest.getGst() == null || contingentBillSaveRequest.getGst().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GST CAN NOT BE BLANK");
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


            for (Integer m = 0; m < contingentBillSaveRequestList.get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                if (cdaParkingTrans == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                }
                if (contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaAmount() == null || contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                }
            }

            double remainingCdaParkingAmount = 0;
            double parkingAmount = 0;
            for (Integer m = 0; m < contingentBillSaveRequestList.get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                remainingCdaParkingAmount = remainingCdaParkingAmount + Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();
                parkingAmount = Double.parseDouble(contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaAmount());

            }


            if (parkingAmount > remainingCdaParkingAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS GREATER THAN CDA REMAINING AMOUNT");
            }


        }


//        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {
//
//            double amount = 0;
//            List<BudgetAllocation> budgetAloocation = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), contingentBillSaveRequestList.get(i).getBudgetFinancialYearId(), contingentBillSaveRequestList.get(i).getBudgetHeadId(), contingentBillSaveRequestList.get(i).getAllocationTypeId(), "Approved", "0", "0");
//
//            for (Integer m = 0; m < budgetAloocation.size(); m++) {
//
//                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAloocation.get(m).getAmountType());
//                amount = amount + (Double.parseDouble(budgetAloocation.get(m).getAllocationAmount()) * amountUnit.getAmount());
//
//            }
//
//            double allocationAmount = Double.parseDouble(contingentBillSaveRequestList.get(i).getCbAmount());
//            BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(contingentBillSaveRequestList.get(i).getBudgetHeadId());
//
//            if (allocationAmount > amount) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB AMOUNT CAN NOT LARGER THAN REMAINING AMOUNT FOR " + budgetHeadId.getSubHeadDescr());
//            }
//
//
//        }


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
            contigentBill.setIsUpdate("0");
            contigentBill.setGst(contingentBillSaveRequest.getGst());
            contigentBill.setVendorName(contingentBillSaveRequest.getVendorName());


            List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
            contigentBill.setAllocationTypeId(allocationType.get(0).getAllocTypeId());


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


            ContigentBill saveData = contigentBillRepository.save(contigentBill);


            for (Integer m = 0; m < contingentBillSaveRequest.getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaParkingId(), "0");

                CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
                cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                cdaParkingCrAndDr.setCdaParkingTrans(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaParkingId());
                cdaParkingCrAndDr.setFinYearId(saveData.getFinYear());
                cdaParkingCrAndDr.setBudgetHeadId(saveData.getBudgetHeadID());
                cdaParkingCrAndDr.setGinNo(cdaParkingTrans.getGinNo());
                cdaParkingCrAndDr.setUnitId(hrData.getUnitId());
                cdaParkingCrAndDr.setAuthGroupId(saveData.getAuthGroupId());
                cdaParkingCrAndDr.setAmount(ConverterUtils.addDecimalPoint(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaAmount()));
                cdaParkingCrAndDr.setIscrdr("DR");
                cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setAllocTypeId("");
                cdaParkingCrAndDr.setIsFlag("0");
                cdaParkingCrAndDr.setIsRevision(0);
                cdaParkingCrAndDr.setTransactionId(saveData.getCbId());
                cdaParkingCrAndDr.setAmountType(null);

                parkingCrAndDrRepository.save(cdaParkingCrAndDr);


            }

        }


        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {


            for (Integer m = 0; m < contingentBillSaveRequestList.get(i).getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();
                double parkingAmount = Double.parseDouble(contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaAmount());

                double bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }

//
//        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {
//
//            double amount = 0;
//            List<BudgetAllocation> budgetAllocation = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), contingentBillSaveRequestList.get(i).getBudgetFinancialYearId(), contingentBillSaveRequestList.get(i).getBudgetHeadId(), contingentBillSaveRequestList.get(i).getAllocationTypeId(), "Approved", "0", "0");
//
//            for (Integer m = 0; m < budgetAllocation.size(); m++) {
//
//                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocation.get(m).getAmountType());
//                amount = amount + (Double.parseDouble(budgetAllocation.get(m).getAllocationAmount()) * amountUnit.getAmount());
//            }
//
//
//            double allocationAmount = Double.parseDouble(contingentBillSaveRequestList.get(i).getCbAmount());
//
//            for (Integer m = 0; m < budgetAllocation.size(); m++) {
//
//                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocation.get(m).getAmountType());
//                Double reminingBalance = (amount - allocationAmount);
//
////                budgetAllocation.get(m).setBalanceAmount(ConverterUtils.addDecimalPoint((reminingBalance / amountUnit.getAmount()) + ""));
//                budgetAllocationRepository.save(budgetAllocation.get(m));
//
//            }
//
//        }

        CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());

        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Contingent Bill");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(toUnitId);
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setType(cgUnit.getDescr());
        mangeInboxOutbox.setGroupId(authGroupId);
        mangeInboxOutbox.setFromUnit(toUnitId);
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setState("VE");
        mangeInboxOutbox.setIsBgcg("CB");
        mangeInboxOutbox.setIsRevision(0);
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
    @Transactional
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

            if (contingentBillSaveRequest.getContingentBilId() == null || contingentBillSaveRequest.getContingentBilId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "OLD CB ID CAN NOT BE BLANK");
            }

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


            ContigentBill contigentBill1 = contigentBillRepository.findByCbIdAndIsFlag(contingentBillSaveRequest.getContingentBilId(), "0");

            if (contigentBill1 == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID OLD CB ID CAN NOT BE BLANK");
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
            if (contingentBillSaveRequest.getGst() == null || contingentBillSaveRequest.getGst().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GST CAN NOT BE BLANK");
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

            for (Integer m = 0; m < contingentBillSaveRequestList.get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                if (cdaParkingTrans == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                }
                if (contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaAmount() == null || contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                }
            }


            double remainingCdaParkingAmount = 0;
            double parkingAmount = 0;
            for (Integer m = 0; m < contingentBillSaveRequestList.get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                remainingCdaParkingAmount = remainingCdaParkingAmount + Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();
                parkingAmount = Double.parseDouble(contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaAmount());

            }
            if (parkingAmount > remainingCdaParkingAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS GREATER THAN CDA REMAINING AMOUNT");
            }
        }


        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {

            for (Integer n = 0; n < contingentBillSaveRequestList.get(i).getCdaParkingId().size(); n++) {

                if (contingentBillSaveRequestList.get(i).getCdaParkingId().get(n).getCdaParkingId() == null || contingentBillSaveRequestList.get(i).getCdaParkingId().get(n).getCdaParkingId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA PARKING ID CAN NOT BE BLANK");
                }

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingId(contingentBillSaveRequestList.get(i).getCdaParkingId().get(n).getCdaParkingId());
                if (cdaParkingTrans == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID.");
                }


                if (contingentBillSaveRequestList.get(i).getCdaParkingId().get(n).getCdaAmount() == null || contingentBillSaveRequestList.get(i).getCdaParkingId().get(n).getCdaAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                }

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
//        String toUnitId = "";
        String authGroupId = "";
        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {

            ContigentBill contigentBill1 = contigentBillRepository.findByCbIdAndIsFlag(contingentBillSaveRequestList.get(i).getContingentBilId(), "0");
            contigentBill1.setIsUpdate("1");
            authGroupId = contigentBill1.getAuthGroupId();

            if (contigentBill1 != null) {

                CdaParkingCrAndDr cdaParkingCrAndDr = parkingCrAndDrRepository.findByCdaCrdrIdAndIsFlagAndIsRevision(contigentBill1.getCbId(), "0", 0);
                CdaParkingTrans cdaParkingTrans11 = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans11.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans11.getRemainingCdaAmount()) * cadAmountUnit.getAmount();
                double parkingAmount = Double.parseDouble(cdaParkingCrAndDr.getAmount());

                double bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
                cdaParkingTrans11.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                cdaParkingTransRepository.save(cdaParkingTrans11);

                cdaParkingCrAndDr.setIsFlag("1");
                parkingCrAndDrRepository.save(cdaParkingCrAndDr);
                contigentBillRepository.save(contigentBill1);
            }


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

            ContigentBill contigentBill = new ContigentBill();

            contigentBill.setCbId(HelperUtils.getContigentId());
            contigentBill.setCbNo(contingentBillSaveRequest.getCbNumber());
            contigentBill.setCbAmount(ConverterUtils.addDecimalPoint(contingentBillSaveRequest.getCbAmount()));
            contigentBill.setCbDate(ConverterUtils.convertDateTotimeStamp(contingentBillSaveRequest.getCbDate()));
            contigentBill.setCbUnitId(contingentBillSaveRequest.getUnit());
//            toUnitId = contingentBillSaveRequest.getUnit();
            contigentBill.setFinYear(contingentBillSaveRequest.getBudgetFinancialYearId());
            contigentBill.setStatus("Pending");
            contigentBill.setRemarks(contingentBillSaveRequest.getRemark());
            contigentBill.setStatusDate(HelperUtils.getCurrentTimeStamp());
            contigentBill.setAuthGroupId(authGroupId);
            contigentBill.setIsFlag("0");
            contigentBill.setIsUpdate("0");
            contigentBill.setGst(contingentBillSaveRequest.getGst());
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


            ContigentBill saveData = contigentBillRepository.save(contigentBill);


            for (Integer m = 0; m < contingentBillSaveRequest.getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaParkingId(), "0");

                CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
                cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                cdaParkingCrAndDr.setCdaParkingTrans(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaParkingId());
                cdaParkingCrAndDr.setFinYearId(saveData.getFinYear());
                cdaParkingCrAndDr.setBudgetHeadId(saveData.getBudgetHeadID());
                cdaParkingCrAndDr.setGinNo(cdaParkingTrans.getGinNo());
                cdaParkingCrAndDr.setUnitId(hrData.getUnitId());
                cdaParkingCrAndDr.setAuthGroupId(saveData.getAuthGroupId());
                cdaParkingCrAndDr.setAmount(ConverterUtils.addDecimalPoint(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaAmount()));
                cdaParkingCrAndDr.setIscrdr("DR");
                cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setAllocTypeId("");
                cdaParkingCrAndDr.setIsFlag("0");
                cdaParkingCrAndDr.setIsRevision(0);
                cdaParkingCrAndDr.setTransactionId(saveData.getCbId());
                cdaParkingCrAndDr.setAmountType(null);

                parkingCrAndDrRepository.save(cdaParkingCrAndDr);
            }

        }


        for (Integer i = 0; i < contingentBillSaveRequestList.size(); i++) {

            for (Integer m = 0; m < contingentBillSaveRequestList.get(i).getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();
                double parkingAmount = Double.parseDouble(contingentBillSaveRequestList.get(i).getCdaParkingId().get(m).getCdaAmount());

                double bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }

        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(authGroupId, hrData.getUnitId());

        if (mangeInboxOutboxList.size() > 0) {

            MangeInboxOutbox mangeInboxOutbox = mangeInboxOutboxList.get(0);

            mangeInboxOutbox.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
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

        }

        contingentSaveResponse.setMsg("Data Update Successfully");
        return ResponseUtils.createSuccessResponse(contingentSaveResponse, new TypeReference<ContingentSaveResponse>() {
        });
    }

    @Override
    @Transactional
    public ApiResponse<List<ContingentBillResponse>> getContingentBill() {

        ArrayList<ContingentBillResponse> contingentBillListData = new ArrayList<ContingentBillResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.");
        }

        List<ContigentBill> cbData = contigentBillRepository.findByCbUnitIdAndIsFlagAndIsUpdate(hrData.getUnitId(), "0", "0");
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

            contingentBill.setStatus(contigentBill.getStatus());
            contingentBill.setStatusDate(contigentBill.getStatusDate());
            contingentBill.setCreatedOn(contigentBill.getCreatedOn());
            contingentBill.setUpdatedOn(contigentBill.getUpdatedOn());
            contingentBill.setVendorName(contigentBill.getVendorName());
            contingentBill.setInvoiceDate(contigentBill.getInvoiceDate());
            contingentBill.setRemarks(contigentBill.getRemarks());
            contingentBill.setFileID(contigentBill.getFileID());
            contingentBill.setGst(contigentBill.getGst());
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

            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(contigentBill.getCbId(), "0", 0);
            List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
            for (Integer m = 0; m < cdaCrDrTransData.size(); m++) {
                CdaParkingCrAndDr cdaParkingCrAndDr = cdaCrDrTransData.get(m);

                CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));

                CdaParkingTrans cdaTransData = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");

                if (cdaTransData != null) {
                    cgUnitResponse.setRemainingAmount(ConverterUtils.addDecimalPoint(cdaTransData.getRemainingCdaAmount()));
                    cgUnitResponse.setAllocationAmount(ConverterUtils.addDecimalPoint(cdaTransData.getTotalParkingAmount()));
                    cgUnitResponse.setAmountTypeMain(amountUnitRepository.findByAmountTypeId(cdaTransData.getAmountType()));

                    data.add(cgUnitResponse);
                }
            }

            contingentBill.setCdaData(data);


            contingentBillListData.add(contingentBill);
        }

        return ResponseUtils.createSuccessResponse(contingentBillListData, new TypeReference<List<ContingentBillResponse>>() {
        });
    }

    @Override
    @Transactional
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
            contingentBill.setGst(contigentBill.getGst());
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

            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(contigentBill.getCbId(), "0", 0);
            List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
            for (Integer m = 0; m < cdaCrDrTransData.size(); m++) {
                CdaParkingCrAndDr cdaParkingCrAndDr = cdaCrDrTransData.get(m);

                CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));

                CdaParkingTrans cdaTransData = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                cgUnitResponse.setRemainingAmount(ConverterUtils.addDecimalPoint(cdaTransData.getRemainingCdaAmount()));
                cgUnitResponse.setAllocationAmount(ConverterUtils.addDecimalPoint(cdaTransData.getTotalParkingAmount()));
                cgUnitResponse.setAmountTypeMain(amountUnitRepository.findByAmountTypeId(cdaTransData.getAmountType()));

                data.add(cgUnitResponse);
            }

            contingentBill.setCdaData(data);

            contingentBillListData.add(contingentBill);
        }

        return ResponseUtils.createSuccessResponse(contingentBillListData, new TypeReference<List<ContingentBillResponse>>() {
        });
    }

    @Override
    @Transactional
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


        if (approveContigentBillRequest.getCdaParkingId() == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA DATA CAN NOT BE BLANK");
        }


        for (Integer i = 0; i < approveContigentBillRequest.getCdaParkingId().size(); i++) {

            if (approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId() == null || approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA CRDR PARKING ID CAN NOT BE BLANK");
            }

            if (approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount() == null || approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB AMOUNT CAN NOT BE BLANK");
            }


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
            if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {
                contigentBill.setIsFlag("1");
            } else {
                contigentBill.setIsFlag("0");
            }
            contigentBillRepository.save(contigentBill);
        }

        if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {


            for (Integer i = 0; i < approveContigentBillRequest.getCdaParkingId().size(); i++) {

                CdaParkingCrAndDr cdaParkingCrAndDr = parkingCrAndDrRepository.findByCdaCrdrIdAndIsFlagAndIsRevision(approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId(), "0", 0);

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                double parkingAmount = Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount());

                double bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }

        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(approveContigentBillRequest.getGroupId(), hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {

            MangeInboxOutbox mangeInboxOutbox = mangeInboxOutboxList.get(0);

            String toUnit = mangeInboxOutbox.getToUnit();
            String fromUnit = mangeInboxOutbox.getFromUnit();

            mangeInboxOutbox.setFromUnit(toUnit);
            mangeInboxOutbox.setToUnit(fromUnit);
            mangeInboxOutbox.setState("CR");
            if (status.equalsIgnoreCase("Approved")) {
                mangeInboxOutbox.setStatus("Approved");
                mangeInboxOutbox.setIsApproved("0");
            } else {
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
    @Transactional
    public ApiResponse<DefaultResponse> updateFinalStatus(UploadCBRequest approveContigentBillRequest) throws IOException {

        DefaultResponse defaultResponse = new DefaultResponse();
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


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(approveContigentBillRequest.getGroupId(), hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {

            MangeInboxOutbox mangeInboxOutbox = mangeInboxOutboxList.get(0);

            String toUnit = mangeInboxOutbox.getToUnit();
            String fromUnit = mangeInboxOutbox.getFromUnit();

            mangeInboxOutbox.setFromUnit(toUnit);
            mangeInboxOutbox.setToUnit(fromUnit);
            mangeInboxOutbox.setStatus("Fully Approved");
            mangeInboxOutbox.setIsApproved("1");
            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutBoxRepository.save(mangeInboxOutbox);

        }

        defaultResponse.setMsg("Data update successfully");

        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });


    }

    @Override
    @Transactional
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


        for (Integer i = 0; i < approveContigentBillRequest.getCdaParkingId().size(); i++) {

            if (approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId() == null || approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA CRDR ID CAN NOT BE BLANK");
            }

//            CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingId(approveContigentBillRequest.getCdaParkingId().get(i).getCdaParkingId());
//            if (cdaParkingTrans == null) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID.");
//            }

            if (approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount() == null || approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB AMOUNT CAN NOT BE BLANK");
            }


        }


        String status = "";
        for (Integer j = 0; j < cbData.size(); j++) {
            ContigentBill contigentBill = cbData.get(j);
            contigentBill.setStatus(approveContigentBillRequest.getStatus());
            contigentBill.setRemarks(approveContigentBillRequest.getRemarks());
            contigentBill.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            status = approveContigentBillRequest.getStatus();
            if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {
                contigentBill.setIsFlag("1");
            } else {
                contigentBill.setIsFlag("0");
            }
            contigentBillRepository.save(contigentBill);


        }


        if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {


            for (Integer i = 0; i < approveContigentBillRequest.getCdaParkingId().size(); i++) {

                CdaParkingCrAndDr cdaParkingCrAndDr = parkingCrAndDrRepository.findByCdaCrdrIdAndIsFlagAndIsRevision(approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId(), "0", 0);

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                double parkingAmount = Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount());

                double bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }

        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(approveContigentBillRequest.getGroupId(), hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {

            MangeInboxOutbox mangeInboxOutbox = mangeInboxOutboxList.get(0);

            if (status.equalsIgnoreCase("Verified")) {
                mangeInboxOutbox.setState("AP");
            } else if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {
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

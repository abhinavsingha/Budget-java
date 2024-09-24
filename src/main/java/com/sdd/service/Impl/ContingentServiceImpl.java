package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.*;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
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

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

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
    CurrentStateRepository currentStateRepository;


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
    TransferBCbBillRepository transferBCbBillRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    HeaderUtils headerUtils;

    @Autowired
    HrDataRepository hrDataRepository;
    private List<CdaParkingCrAndDr> cdaCrDrTransData;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<ContingentSaveResponse> saveContingentBill(ArrayList<ContingentBillSaveRequest> contingentBillSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CONTINGENT BILL ENTRY");
        }

        ContingentSaveResponse contingentSaveResponse = new ContingentSaveResponse();
        for (ContingentBillSaveRequest contingentBillSaveRequest : contingentBillSaveRequestList) {

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

//            if (contingentBillSaveRequest.getCbNumber() == null || contingentBillSaveRequest.getCbNumber().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB NUMBER CAN NOT BE BLANK");
//            }

            if (contingentBillSaveRequest.getSectionNumber() == null || contingentBillSaveRequest.getSectionNumber().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SECTION NUMBER CAN NOT BE BLANK");
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
//            if (contingentBillSaveRequest.getRemark() == null || contingentBillSaveRequest.getRemark().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
//            }

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



            for (int j = 0; j < contingentBillSaveRequest.getAuthList().size(); j++) {

                FileUpload fileUpload = fileUploadRepository.findByUploadID(contingentBillSaveRequest.getAuthList().get(j).getAuthDocId());
                if (fileUpload == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DOCUMENT ID ");
                }
                if (contingentBillSaveRequest.getAuthList().get(j).getAuthDocId() == null || contingentBillSaveRequest.getAuthList().get(j).getAuthDocId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT ID CAN NOT BE BLANK");
                }
                if (contingentBillSaveRequest.getAuthList().get(j).getAuthDate() == null || contingentBillSaveRequest.getAuthList().get(j).getAuthDate().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY DATE  CAN NOT BE BLANK");
                }

                CgUnit getAuthUnitId = cgUnitRepository.findByUnit(contingentBillSaveRequest.getAuthList().get(j).getAuthUnitId());
                if (getAuthUnitId == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTHORITY UNIT ID");
                }
                ConverterUtils.checkDateIsvalidOrNor(contingentBillSaveRequest.getAuthList().get(j).getAuthDate());

            }

            if (contingentBillSaveRequest.getCdaParkingId().isEmpty()|| contingentBillSaveRequest.getCdaParkingId()==null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA Parking id not found please refresh and try again.");
            }
            for (int m = 0; m < contingentBillSaveRequest.getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaParkingId(), "0");
                if (cdaParkingTrans == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                }
                if (contingentBillSaveRequest.getCdaParkingId().get(m).getCdaAmount() == null || contingentBillSaveRequest.getCdaParkingId().get(m).getCdaAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                }
            }

            double remainingCdaParkingAmount = 0;
            double parkingAmount = 0;
            for (int m = 0; m < contingentBillSaveRequest.getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                remainingCdaParkingAmount = ConverterUtils.doubleSum(remainingCdaParkingAmount, Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount());
                parkingAmount = ConverterUtils.addDoubleValue(Double.parseDouble(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaAmount()));

            }

            if (parkingAmount > remainingCdaParkingAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS GREATER THAN CDA REMAINING AMOUNT");
            }

        }


        for (ContingentBillSaveRequest billSaveRequest : contingentBillSaveRequestList) {

            //int sectionNumber = Integer.parseInt(billSaveRequest.getSectionNumber());
            List<ContigentBill> lastContigentBill = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndFinYear(hrData.getUnitId(), billSaveRequest.getBudgetHeadId(), billSaveRequest.getBudgetFinancialYearId());
            Collections.sort(lastContigentBill, Comparator.comparing(ContigentBill::getCreatedOn).reversed());


            //ContigentBill checkExistingData = contigentBillRepository.findByCbUnitIdAndSectionNumberAndBudgetHeadIDAndFinYear(hrData.getUnitId(), sectionNumber + "", billSaveRequest.getBudgetHeadId(),billSaveRequest.getBudgetFinancialYearId());
//            if (checkExistingData != null) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DATA ALREADY FOUND THIS SANCTION NUMBER");
//            }

            if (!lastContigentBill.isEmpty()) {
                Timestamp lastCbDate = lastContigentBill.get(0).getCbDate();
                Timestamp currentDate = ConverterUtils.convertDateTotimeStamp(billSaveRequest.getCbDate());

                long dayDiffer = ConverterUtils.timeDifferTimeStamp(lastCbDate, currentDate);
                if (dayDiffer >= 1) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB DATE IS OLDER THAN LAST CB BILL.");
                }
            }
        }


        List<HrData> hrDataList = hrDataRepository.findByUnitIdAndIsActive(hrData.getUnitId(), "1");
        if (hrDataList.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO ROLE ASSIGN FOR THIS UNIT.");
        }

        String approverCbPId = "";
        String veriferCbPId = "";
        for (HrData findHrData : hrDataList) {
            if (findHrData.getRoleId() != null) {
                if (findHrData.getRoleId().contains(HelperUtils.CBVERIFER)) {
                    veriferCbPId = findHrData.getPid();
                }
                if (findHrData.getRoleId().contains(HelperUtils.CBAPPROVER)) {
                    approverCbPId = findHrData.getPid();
                }
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


        for (ContingentBillSaveRequest contingentBillSaveRequest : contingentBillSaveRequestList) {

            for (int j = 0; j < contingentBillSaveRequest.getAuthList().size(); j++) {
                Authority authoritySaveData = new Authority();
                authoritySaveData.setAuthorityId(HelperUtils.getAuthorityId());
                authoritySaveData.setAuthority(contingentBillSaveRequest.getAuthList().get(j).getAuthority());
                authoritySaveData.setAuthDate(ConverterUtils.convertDateTotimeStamp(contingentBillSaveRequest.getAuthList().get(j).getAuthDate()));
                authoritySaveData.setAuthUnit(contingentBillSaveRequest.getAuthList().get(j).getAuthUnitId());
                authoritySaveData.setDocId(contingentBillSaveRequest.getAuthList().get(j).getAuthDocId());
                authoritySaveData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                authoritySaveData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                authoritySaveData.setAuthGroupId(authGroupId);
                authoritySaveData.setRemarks(contingentBillSaveRequest.getAuthList().get(j).getRemark());
                authorityRepository.save(authoritySaveData);
            }

            ContigentBill contigentBill = new ContigentBill();

            contigentBill.setCbId(HelperUtils.getContigentId());

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
            contigentBill.setSectionNumber(contingentBillSaveRequest.getSectionNumber());
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
            contigentBill.setCreatedBy(hrData.getPid());


            double allocationAmount = 0;
            List<CdaParkingTrans> cdaAmountList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(contingentBillSaveRequest.getBudgetFinancialYearId(), contingentBillSaveRequest.getBudgetHeadId(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());
            for (CdaParkingTrans parkingTrans : cdaAmountList) {
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(parkingTrans.getAmountType());
                allocationAmount = ConverterUtils.doubleSum(allocationAmount, (Double.parseDouble(parkingTrans.getRemainingCdaAmount()) * amountUnit.getAmount()));
            }

            List<ContigentBill> subHeadContigentBill = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsUpdateAndIsFlag(contingentBillSaveRequest.getUnit(), contingentBillSaveRequest.getBudgetFinancialYearId(), contingentBillSaveRequest.getBudgetHeadId(), "0", "0");

            double totalBill = 0;
            for (ContigentBill bill : subHeadContigentBill) {
                totalBill = ConverterUtils.doubleSum(totalBill, Double.parseDouble(bill.getCbAmount()));
            }
            allocationAmount = ConverterUtils.doubleSum(allocationAmount, totalBill);
            contigentBill.setAllocatedAmount(allocationAmount + "");
            CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());
            BudgetHead head = subHeadRepository.findByBudgetCodeId(contingentBillSaveRequestList.get(0).getBudgetHeadId());

            String finYear = (budgetFinancialYearRepository.findBySerialNo(contingentBillSaveRequest.getBudgetFinancialYearId())).getFinYear();
            List<ContigentBill> bills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadID(contigentBill.getCbUnitId(), contigentBill.getFinYear(), contigentBill.getBudgetHeadID());
            String cbNo = cgUnit.getCgUnitShort() + "/CB/" + head.getSubheadShort() + '/' + (bills.size() + 1) + '/' + finYear;

            contigentBill.setCbNo(cbNo);
            ContigentBill saveData = contigentBillRepository.save(contigentBill);


            for (int m = 0; m < contingentBillSaveRequest.getCdaParkingId().size(); m++) {


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


        for (ContingentBillSaveRequest contingentBillSaveRequest : contingentBillSaveRequestList) {


            for (int m = 0; m < contingentBillSaveRequest.getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount());/// * cadAmountUnit.getAmount();
                double parkingAmount = Double.parseDouble(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaAmount()) / cadAmountUnit.getAmount();

                double bakiPesa = ConverterUtils.doubleMinus(remainingCdaParkingAmount, parkingAmount);// / cadAmountUnit.getAmount();
                bakiPesa = (ConverterUtils.addDoubleValue(bakiPesa));
                cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }


//        CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());
        BudgetHead head = subHeadRepository.findByBudgetCodeId(contingentBillSaveRequestList.get(0).getBudgetHeadId());
        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
        mangeInboxOutbox.setIsRebase("0");
        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Contingent Bill");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(toUnitId);
        mangeInboxOutbox.setStatus("Pending");
//        mangeInboxOutbox.setType(cgUnit.getDescr());
        mangeInboxOutbox.setType(head.getSubHeadDescr());
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


        contingentSaveResponse.setMsg("Data Save Successfully");

        return ResponseUtils.createSuccessResponse(contingentSaveResponse, new TypeReference<ContingentSaveResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<ContingentSaveResponse> transferCbBill(TransferCbBill transferCbBill) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CONTINGENT BILL ENTRY");
        }

        ContingentSaveResponse contingentSaveResponse = new ContingentSaveResponse();

        List<HrData> hrDataList = hrDataRepository.findByUnitIdAndIsActive(hrData.getUnitId(), "1");
        if (hrDataList.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO ROLE ASSIGN FOR THIS UNIT.");
        }
        if (transferCbBill.getOldUserId().equalsIgnoreCase(transferCbBill.getNewUserId())) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NEW USER OR OLD USER CAN NOT BE SAME.");
        }

        HrData oldUser = hrDataRepository.findByPid(transferCbBill.getOldUserId());
        if (oldUser == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO USER FIND FOR THIS OLD PID.");
        }

        HrData newUser = hrDataRepository.findByPid(transferCbBill.getNewUserId());
        if (newUser == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO USER FIND FOR THIS NEW PID.");
        }

        List<MangeInboxOutbox> findNewCbBill = mangeInboxOutBoxRepository.findByCreaterpIdAndToUnit(transferCbBill.getNewUserId(), hrData.getUnitId());
        List<MangeInboxOutbox> findOldCbBill = mangeInboxOutBoxRepository.findByCreaterpIdAndToUnit(transferCbBill.getOldUserId(), hrData.getUnitId());

        if (!findOldCbBill.isEmpty()) {
            for (MangeInboxOutbox mangeInboxOutbox : findOldCbBill) {

                mangeInboxOutbox.setCreaterpId(transferCbBill.getNewUserId());
                mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                mangeInboxOutBoxRepository.save(mangeInboxOutbox);


                TransferContingentBillHistory transferContingentBillHistory = new TransferContingentBillHistory();
                transferContingentBillHistory.setCbId(HelperUtils.getTransferBillId());
                transferContingentBillHistory.setCbNo(mangeInboxOutbox.getGroupId());
                transferContingentBillHistory.setOldUserId(transferCbBill.getOldUserId());
                transferContingentBillHistory.setNewUserPid(transferCbBill.getNewUserId());
                transferContingentBillHistory.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                transferContingentBillHistory.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                transferContingentBillHistory.setRemarks("CB Transfer");
                transferBCbBillRepository.save(transferContingentBillHistory);


            }
            contingentSaveResponse.setMsg("CB BILL TRANSFER AND USER DEACTIVATED SUCCESSFULLY");
        } else {
            contingentSaveResponse.setMsg("NO BILL FOUND AND USER ROLE DEACTIVATED SUCCESSFULLY");
        }

        if (!hrData.getRoleId().contains(HelperUtils.UNITADMIN)) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER DID NOT ASSIGN CB CREATE ROLE. PLEASE CONTACT ADMINISTER");
        }

        String[] newRoleIdData = oldUser.getRoleId().split(",");
        List<String> list = new ArrayList<String>(Arrays.asList(newRoleIdData));
        list.remove(HelperUtils.CBCREATER);

        String newRoleId = "";


        for (int i = list.size() - 1; i >= 0; i--) {
            String newRoleIdDatum = list.get(i);
            newRoleId = newRoleIdDatum + "," + newRoleId;
        }
//        for (String newRoleIdDatum : list) {
//            newRoleId = newRoleIdDatum + "," + newRoleId;
//        }
        oldUser.setRoleId(newRoleId);
        if (newRoleId == null || newRoleId.equalsIgnoreCase("") || newRoleId.isEmpty()) {
            oldUser.setIsActive("0");
        }
        hrDataRepository.save(oldUser);

        return ResponseUtils.createSuccessResponse(contingentSaveResponse, new TypeReference<ContingentSaveResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<ContingentSaveResponse> updateContingentBill(ArrayList<ContingentBillSaveRequest> contingentBillSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE CONTINGENT BILL ENTRY");
        }


        ContingentSaveResponse contingentSaveResponse = new ContingentSaveResponse();

        for (ContingentBillSaveRequest contingentBillSaveRequest : contingentBillSaveRequestList) {

            if (contingentBillSaveRequest.getContingentBilId() == null || contingentBillSaveRequest.getContingentBilId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "OLD CB ID CAN NOT BE BLANK");
            }

            ContigentBill contigentBill = contigentBillRepository.findByCbId(contingentBillSaveRequest.getContingentBilId());
            if (contigentBill == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CONTINGENT BILL ID");
            }

            if (!(contigentBill.getStatus().equalsIgnoreCase("Rejected") || contigentBill.getStatus().equalsIgnoreCase("Reject"))) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB BILL NOT IN REJECTED STATE");
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

            if (contingentBillSaveRequest.getSectionNumber() == null || contingentBillSaveRequest.getSectionNumber().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SANCTION NUMBER CAN NOT BE BLANK");
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


//            if (contingentBillSaveRequest.getRemark() == null || contingentBillSaveRequest.getRemark().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
//            }

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

            for (Integer j = 0; j < contingentBillSaveRequest.getAuthList().size(); j++) {


                Authority authoritySaveData = authorityRepository.findByAuthorityId(contingentBillSaveRequest.getAuthList().get(j).getAuthorityId());
                if (authoritySaveData == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AUTHORITY ID");
                }

                FileUpload fileUpload = fileUploadRepository.findByUploadID(contingentBillSaveRequest.getAuthList().get(j).getAuthDocId());
                if (fileUpload == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DOCUMENT ID ");
                }
                if (contingentBillSaveRequest.getAuthList().get(j).getAuthDocId() == null || contingentBillSaveRequest.getAuthList().get(j).getAuthDocId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT ID CAN NOT BE BLANK");
                }
                if (contingentBillSaveRequest.getAuthList().get(j).getAuthDate() == null || contingentBillSaveRequest.getAuthList().get(j).getAuthDate().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY DATE  CAN NOT BE BLANK");
                }

                CgUnit getAuthUnitId = cgUnitRepository.findByUnit(contingentBillSaveRequest.getAuthList().get(j).getAuthUnitId());
                if (getAuthUnitId == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTHORITY UNIT ID");
                }
                ConverterUtils.checkDateIsvalidOrNor(contingentBillSaveRequest.getAuthList().get(j).getAuthDate());

            }

            for (Integer m = 0; m < contingentBillSaveRequest.getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaParkingId(), "0");
                if (cdaParkingTrans == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                }
                if (contingentBillSaveRequest.getCdaParkingId().get(m).getCdaAmount() == null || contingentBillSaveRequest.getCdaParkingId().get(m).getCdaAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                }
            }


            double remainingCdaParkingAmount = 0;
            double parkingAmount = 0;
            for (int m = 0; m < contingentBillSaveRequest.getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                remainingCdaParkingAmount = ConverterUtils.doubleSum(remainingCdaParkingAmount, Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount());
                parkingAmount = ConverterUtils.addDoubleValue(Double.parseDouble(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaAmount()));

            }
            if (parkingAmount > remainingCdaParkingAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BILL AMOUNT IS GREATER THAN CDA REMAINING AMOUNT");
            }
        }


        for (ContingentBillSaveRequest billSaveRequest : contingentBillSaveRequestList) {

            for (int n = 0; n < billSaveRequest.getCdaParkingId().size(); n++) {

                if (billSaveRequest.getCdaParkingId().get(n).getCdaParkingId() == null || billSaveRequest.getCdaParkingId().get(n).getCdaParkingId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA PARKING ID CAN NOT BE BLANK");
                }

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingId(billSaveRequest.getCdaParkingId().get(n).getCdaParkingId());
                if (cdaParkingTrans == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID.");
                }


                if (billSaveRequest.getCdaParkingId().get(n).getCdaAmount() == null || billSaveRequest.getCdaParkingId().get(n).getCdaAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                }

            }

        }


        List<HrData> hrDataList = hrDataRepository.findByUnitIdAndIsActive(hrData.getUnitId(), "1");
        if (hrDataList.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO ROLE ASSIGN FOR THIS UNIT.");
        }

        String approverCbPId = "";
        String veriferCbPId = "";
        for (HrData findHrData : hrDataList) {
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

        for (int i = 0; i < contingentBillSaveRequestList.size(); i++) {

            ContingentBillSaveRequest contingentBillSaveRequest = contingentBillSaveRequestList.get(i);
            ContigentBill contigentBill = contigentBillRepository.findByCbId(contingentBillSaveRequest.getContingentBilId());

            List<CdaParkingCrAndDr> cRdRdata = parkingCrAndDrRepository.findByAuthGroupId(contigentBill.getAuthGroupId());
            for (int c = 0; c < cRdRdata.size(); c++) {
                parkingCrAndDrRepository.delete(cRdRdata.get(i));
            }
        }


        String authGroupIdD = "";

        for (ContingentBillSaveRequest contingentBillSaveRequest : contingentBillSaveRequestList) {

            for (int j = 0; j < contingentBillSaveRequest.getAuthList().size(); j++) {
                Authority authoritySaveData = authorityRepository.findByAuthorityId(contingentBillSaveRequest.getAuthList().get(j).getAuthorityId());

                authoritySaveData.setAuthorityId(authoritySaveData.getAuthorityId());
                authoritySaveData.setAuthority(contingentBillSaveRequest.getAuthList().get(j).getAuthority());
                authoritySaveData.setAuthDate(ConverterUtils.convertDateTotimeStamp(contingentBillSaveRequest.getAuthList().get(j).getAuthDate()));
                authoritySaveData.setAuthUnit(contingentBillSaveRequest.getAuthList().get(j).getAuthUnitId());
                authoritySaveData.setDocId(contingentBillSaveRequest.getAuthList().get(j).getAuthDocId());
                authoritySaveData.setRemarks(contingentBillSaveRequest.getAuthList().get(j).getRemark());
                authoritySaveData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                authorityRepository.save(authoritySaveData);
            }

            ContigentBill contigentBill = contigentBillRepository.findByCbId(contingentBillSaveRequest.getContingentBilId());


            authGroupIdD = contigentBill.getAuthGroupId();
            contigentBill.setCbId(contigentBill.getCbId());
            contigentBill.setCbAmount(ConverterUtils.addDecimalPoint(contingentBillSaveRequest.getCbAmount()));
            contigentBill.setCbDate(ConverterUtils.convertDateTotimeStamp(contingentBillSaveRequest.getCbDate()));
            contigentBill.setStatus("Pending");
            contigentBill.setRemarks(contingentBillSaveRequest.getRemark());
            contigentBill.setStatusDate(HelperUtils.getCurrentTimeStamp());
            contigentBill.setIsFlag("0");
            contigentBill.setIsUpdate("0");
            contigentBill.setGst(contingentBillSaveRequest.getGst());
            contigentBill.setVendorName(contingentBillSaveRequest.getVendorName());

            List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
            contigentBill.setAllocationTypeId(allocationType.get(0).getAllocTypeId());
            contigentBill.setCreatedBy(hrData.getPid());
            contigentBill.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            contigentBill.setInvoiceNO(contingentBillSaveRequest.getInvoiceNo());
            contigentBill.setInvoiceDate(contingentBillSaveRequest.getInvoiceDate());
            contigentBill.setFileDate(contingentBillSaveRequest.getFileDate());
            contigentBill.setFileID(contingentBillSaveRequest.getFileNumber());
            contigentBill.setProgressiveAmount(contingentBillSaveRequest.getProgressiveAmount());
            contigentBill.setOnAccountOf(contingentBillSaveRequest.getOnAccountOf());
            contigentBill.setAuthorityDetails(contingentBillSaveRequest.getAuthorityDetails());
            contigentBill.setInvoiceUploadId(contingentBillSaveRequest.getInvoiceUploadId());

            double allocationAmount = 0;
            List<CdaParkingTrans> cdaAmountList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(contingentBillSaveRequest.getBudgetFinancialYearId(), contingentBillSaveRequest.getBudgetHeadId(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());
            for (CdaParkingTrans parkingTrans : cdaAmountList) {
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(parkingTrans.getAmountType());
                allocationAmount = ConverterUtils.doubleSum(allocationAmount, (Double.parseDouble(parkingTrans.getRemainingCdaAmount()) * amountUnit.getAmount()));
            }

            List<ContigentBill> subHeadContigentBill = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsUpdateAndIsFlag(contingentBillSaveRequest.getUnit(), contingentBillSaveRequest.getBudgetFinancialYearId(), contingentBillSaveRequest.getBudgetHeadId(), "0", "0");

            double totalBill = 0;
            for (ContigentBill bill : subHeadContigentBill) {
                totalBill = ConverterUtils.doubleSum(totalBill, Double.parseDouble(bill.getCbAmount()));
            }
            //allocationAmount = allocationAmount + totalBill;
//            contigentBill.setAllocatedAmount(ConverterUtils.addDecimalPoint(allocationAmount + ""));


            ContigentBill saveData = contigentBillRepository.save(contigentBill);


            for (int m = 0; m < contingentBillSaveRequest.getCdaParkingId().size(); m++) {
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


        for (ContingentBillSaveRequest contingentBillSaveRequest : contingentBillSaveRequestList) {

            for (int m = 0; m < contingentBillSaveRequest.getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = ConverterUtils.addDoubleValue(Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()));//* cadAmountUnit.getAmount()
                double parkingAmount = ConverterUtils.addDoubleValue(Double.parseDouble(contingentBillSaveRequest.getCdaParkingId().get(m).getCdaAmount()) / cadAmountUnit.getAmount());

                double bakiPesa = ConverterUtils.doubleMinus(remainingCdaParkingAmount, parkingAmount);
//                double bakiPesa = ConverterUtils.doubleMinus(remainingCdaParkingAmount , parkingAmount) / cadAmountUnit.getAmount();
                bakiPesa = (ConverterUtils.addDoubleValue(bakiPesa));
                cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(authGroupIdD, hrData.getUnitId());

        if (mangeInboxOutboxList.size() > 0) {

            for (MangeInboxOutbox mangeInboxOutbox : mangeInboxOutboxList) {
                mangeInboxOutbox.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                mangeInboxOutbox.setRemarks("Contingent Bill");
                mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                mangeInboxOutbox.setStatus("Pending");
                mangeInboxOutbox.setRoleId(hrData.getRoleId());
                mangeInboxOutbox.setCreaterpId(hrData.getPid());
                mangeInboxOutbox.setIsFlag("1");
                mangeInboxOutbox.setState("VE");
                mangeInboxOutbox.setIsBgcg("CB");
                mangeInboxOutbox.setIsArchive("0");
                mangeInboxOutbox.setIsApproved("0");
                mangeInboxOutbox.setIsRevision(0);

                mangeInboxOutBoxRepository.save(mangeInboxOutbox);
            }

        }

        contingentSaveResponse.setMsg("Data Update Successfully");
        return ResponseUtils.createSuccessResponse(contingentSaveResponse, new TypeReference<ContingentSaveResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<ContingentBillResponse>> getContingentBill() {

        ArrayList<ContingentBillResponse> contingentBillListData = new ArrayList<ContingentBillResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.");
        }
        List<ContigentBill> cbData;
        String rollId = hrData.getRoleId();
        if (rollId.startsWith("116")) {
            cbData = contigentBillRepository.findByCbUnitIdAndCreatedBy(hrData.getUnitId(), hrData.getPid());
        } else {
            cbData = contigentBillRepository.findByCbUnitId(hrData.getUnitId());
        }
        if (cbData.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND.");
        }

        for (ContigentBill contigentBill : cbData) {
            ContingentBillResponse contingentBill = new ContingentBillResponse();
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
            contingentBill.setAllocatedAmount(contigentBill.getAllocatedAmount());

            CgUnit cgUnit = cgUnitRepository.findByUnit(contigentBill.getCbUnitId());
            contingentBill.setCbUnitId(cgUnit);

            FileUpload invoiceNoData = fileUploadRepository.findByUploadID(contigentBill.getInvoiceUploadId());

            contingentBill.setInvoiceUploadId(invoiceNoData);

            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(contigentBill.getFinYear());
            contingentBill.setFinYear(budgetFinancialYear);

            contingentBill.setBudgetHeadID(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(contigentBill.getBudgetHeadID()));

            contingentBill.setAuthoritiesList(authoritiesList);

            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionId(contigentBill.getCbId());


            List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), budgetFinancialYear.getSerialNo(), contigentBill.getBudgetHeadID(), contigentBill.getAllocationTypeId(), "Approved", "0", "0");
            double allocationAmount = 0;
            for (BudgetAllocation budgetAllocationsDetali : budgetAllocationsDetalis) {

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetali.getAmountType());
                allocationAmount = ConverterUtils.doubleSum(allocationAmount, (Double.parseDouble(budgetAllocationsDetali.getAllocationAmount()) * amountUnit.getAmount()));

            }


            List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
            for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaCrDrTransData) {
                CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));

                CdaParkingTrans cdaTransData = cdaParkingTransRepository.findByCdaParkingId(cdaParkingCrAndDr.getCdaParkingTrans());

                if (cdaTransData != null) {
                    cgUnitResponse.setRemainingAmount(cdaTransData.getRemainingCdaAmount());
                    cgUnitResponse.setAllocationAmount(allocationAmount + "");
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
    @Transactional(rollbackFor = {Exception.class})
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

        List<ContigentBill> cbData = contigentBillRepository.findByAuthGroupId(groupId);
        if (cbData.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND.");
        }

        for (ContigentBill contigentBill : cbData) {
            ContingentBillResponse contingentBill = new ContingentBillResponse();
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
            contingentBill.setAllocatedAmount(contigentBill.getAllocatedAmount());
            contingentBill.setFileDate(contigentBill.getFileDate());
            contingentBill.setProgressiveAmount(contigentBill.getProgressiveAmount());
            contingentBill.setAllocatedAmount(contigentBill.getAllocatedAmount());
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

            List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), budgetFinancialYear.getSerialNo(), contigentBill.getBudgetHeadID(), contigentBill.getAllocationTypeId(), "Approved", "0", "0");
            double allocationAmount = 0;
            for (BudgetAllocation budgetAllocationsDetali : budgetAllocationsDetalis) {

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetali.getAmountType());
                allocationAmount = ConverterUtils.doubleSum(allocationAmount, (Double.parseDouble(budgetAllocationsDetali.getAllocationAmount()) * amountUnit.getAmount()));

            }

            List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
            for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaCrDrTransData) {
                CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));

                CdaParkingTrans cdaTransData = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                cgUnitResponse.setRemainingAmount(ConverterUtils.addDecimalPoint(cdaTransData.getRemainingCdaAmount()));
                cgUnitResponse.setAllocationAmount("" + allocationAmount);
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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<ContingentBillResponse>> getCountRejectedBil() {

        ArrayList<ContingentBillResponse> contingentBillListData = new ArrayList<ContingentBillResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.");
        }


        List<ContigentBill> cbData = contigentBillRepository.findByCbUnitIdAndStatus(hrData.getUnitId(), "Rejected");
        if (cbData.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND.");
        }


        return ResponseUtils.createSuccessResponse(contingentBillListData, new TypeReference<List<ContingentBillResponse>>() {
        });
    }

    @Override
    public ApiResponse<ContigentSectionResp> getMaxSectionNumber(MaxNumberRequest budgetHeadId) {
        ContigentSectionResp contingentBillListData = new ContigentSectionResp();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.");
        }

        if (budgetHeadId.getBudgetId() == null || budgetHeadId.getBudgetId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET HEAD ID CAN NOT BE BLANK");
        }

        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
        if (allocationType.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID.");
        }

        BudgetFinancialYear budgetFinancialYear;
        CurrntStateType stateList1 = currentStateRepository.findByTypeAndIsFlag("FINYEAR", "1");
        if (stateList1 == null) {
            budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo("01");

        } else {
            budgetFinancialYear =
                    budgetFinancialYearRepository.findBySerialNo(stateList1.getStateId());

        }

        int maxNumber = 1;
        List<ContigentBill> masNumberList = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadID(hrData.getUnitId(), budgetFinancialYear.getSerialNo(), budgetHeadId.getBudgetId());
        if (masNumberList.isEmpty()) {
            contingentBillListData.setSectionNumber("1");
        } else {
//  //          for (ContigentBill contigentBill : masNumberList) {
//      // pahle se babd tha
//     //           int number = Integer.parseInt(contigentBill.getSectionNumber());
//       //         if (number > maxNumber) {
//       //             maxNumber = number;
//      //          }
//      //      }
            contingentBillListData.setSectionNumber((masNumberList.size() + 1) + "");
        }

        return ResponseUtils.createSuccessResponse(contingentBillListData, new TypeReference<ContigentSectionResp>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<ContingentSaveResponse> approveContingentBill(ApproveContigentBillRequest approveContigentBillRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO APPROVE CB");
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


        if (approveContigentBillRequest.getCdaParkingId() == null || approveContigentBillRequest.getCdaParkingId().size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA DATA CAN NOT BE BLANK");
        }


        for (int i = 0; i < approveContigentBillRequest.getCdaParkingId().size(); i++) {

            if (approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId() == null || approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA CRDR PARKING ID CAN NOT BE BLANK");
            }

            if (approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount() == null || approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB AMOUNT CAN NOT BE BLANK");
            }

        }

        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
//        List<CdaParkingTrans> checkCdaParkingPreviewsData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitId(cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());
//        if (checkCdaParkingPreviewsData.isEmpty()) {
//            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO CDA FOUND.PLEASE FILL CDA FIRST");
//        }


        if (approveContigentBillRequest.getStatus().equalsIgnoreCase("Verified")) {
            if (approveContigentBillRequest.getRemarks() == null || approveContigentBillRequest.getRemarks().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
            }
        }

        String status = "";
        for (ContigentBill contigentBill : cbData) {
            contigentBill.setStatus(approveContigentBillRequest.getStatus());
            contigentBill.setRemarks(approveContigentBillRequest.getRemarks());
            contigentBill.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            contigentBill.setApproved_by(hrData.getPid());
            status = approveContigentBillRequest.getStatus();
            if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {
                contigentBill.setIsFlag("1");
            } else {
                contigentBill.setIsFlag("0");
            }
            contigentBillRepository.save(contigentBill);
        }

        if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {


            List<CdaParkingTrans> cdaParkingExitData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitId(cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());
            List<BudgetAllocation> budgetAllocationData = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), allocationType.get(0).getAllocTypeId(), "Approved", "0", "0");

            String amountTypeID = "";

            if (!cdaParkingExitData.isEmpty()) {
                amountTypeID = cdaParkingExitData.get(0).getAmountType();
            } else {
                for (int g = 0; g < budgetAllocationData.size(); g++) {
                    amountTypeID = budgetAllocationData.get(g).getAmountType();
                }
            }


            for (int i = 0; i < approveContigentBillRequest.getCdaParkingId().size(); i++) {

                CdaParkingCrAndDr cdaParkingCrAndDr = parkingCrAndDrRepository.findByCdaCrdrIdAndIsFlagAndIsRevision(approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId(), "0", 0);
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingId(cdaParkingCrAndDr.getCdaParkingTrans());

                if (cdaParkingTrans == null) {
                    List<CdaParkingTrans> cdaParkingPreviewsData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), cdaParkingCrAndDr.getGinNo(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());

                    if (cdaParkingPreviewsData.isEmpty()) {

                        CdaParkingTrans newCdaTrans = new CdaParkingTrans();
                        BeanUtils.copyProperties(cdaParkingTrans, newCdaTrans);
                        AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(amountTypeID);

                        newCdaTrans.setAmountType(amountTypeID);
                        newCdaTrans.setIsFlag("0");
                        cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTrans.setRemarks("CDA UPDATE IF ALLOCATION IS DIFFERENT AND CB REJECT21");
                        newCdaTrans.setAllocTypeId(allocationType.get(0).getAllocTypeId());

                        newCdaTrans.setRemainingCdaAmount("" + ConverterUtils.addDoubleValue(Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount()) / cdaAmountUnit.getAmount()));
                        newCdaTrans.setRemarks("CDA CREATED.CB BILL REJECTED");

                        cdaParkingTransRepository.save(newCdaTrans);


                    } else {

                        CdaParkingTrans cdaParkingTransMain = cdaParkingPreviewsData.get(0);

                        AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingPreviewsData.get(0).getAmountType());
                        double remainingCdaParkingAmount = Double.parseDouble(cdaParkingPreviewsData.get(0).getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                        double parkingAmount = Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount());

                        double bakiPesa = ConverterUtils.doubleSum(remainingCdaParkingAmount, parkingAmount) / cadAmountUnit.getAmount();
                        bakiPesa = (ConverterUtils.addDoubleValue(bakiPesa));
                        cdaParkingTransMain.setRemainingCdaAmount(bakiPesa + "");
                        ///cdaParkingTransMain.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                        cdaParkingTransMain.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTransMain.setRemarks("CDA UPDATE IF ALLOCATION IS DIFFERENT AND CB REJECT1");
                        cdaParkingTransRepository.save(cdaParkingTransMain);

                    }
                } else {

                    if (cdaParkingTrans.getAllocTypeId().equalsIgnoreCase(allocationType.get(0).getAllocTypeId())) {

                        if (cdaParkingTrans.getIsFlag().equalsIgnoreCase("1")) {

                            List<CdaParkingTrans> cdaParkingPreviewsData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), cdaParkingTrans.getGinNo(), "0", cdaParkingTrans.getAllocTypeId(), hrData.getUnitId());

                            if (cdaParkingPreviewsData.isEmpty()) {

                                CdaParkingTrans newCdaTrans = new CdaParkingTrans();
                                BeanUtils.copyProperties(cdaParkingTrans, newCdaTrans);
                                AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(amountTypeID);

                                newCdaTrans.setAmountType(amountTypeID);
                                newCdaTrans.setIsFlag("0");

                                newCdaTrans.setRemainingCdaAmount("" + ConverterUtils.addDoubleValue(Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount()) / cdaAmountUnit.getAmount()));

                                newCdaTrans.setRemarks("CDA CREATED.CB BILL REJECTED");

                                cdaParkingTransRepository.save(newCdaTrans);


                            } else {

                                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingPreviewsData.get(0).getAmountType());
                                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                                double parkingAmount = Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount());

                                double bakiPesa = ConverterUtils.doubleSum(remainingCdaParkingAmount, parkingAmount) / cadAmountUnit.getAmount();
                                //cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                                bakiPesa = (ConverterUtils.addDoubleValue(bakiPesa));
                                cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                                cdaParkingTransRepository.save(cdaParkingTrans);

                            }


                        } else {

                            if (cdaParkingTrans.getAllocTypeId().equalsIgnoreCase(allocationType.get(0).getAllocTypeId())) {
                                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
                                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                                double parkingAmount = Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount());

                                double bakiPesa = ConverterUtils.doubleSum(remainingCdaParkingAmount, parkingAmount) / cadAmountUnit.getAmount();
                                //cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                                bakiPesa = (ConverterUtils.addDoubleValue(bakiPesa));
                                cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                                cdaParkingTransRepository.save(cdaParkingTrans);

                            }
                        }


                    } else {

                        List<CdaParkingTrans> cdaParkingPreviewsData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), cdaParkingTrans.getGinNo(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());

                        if (cdaParkingPreviewsData.isEmpty()) {

                            CdaParkingTrans newCdaTrans = new CdaParkingTrans();
                            BeanUtils.copyProperties(cdaParkingTrans, newCdaTrans);
                            AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(amountTypeID);

                            newCdaTrans.setAmountType(amountTypeID);
                            newCdaTrans.setIsFlag("0");
                            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParkingTrans.setRemarks("CDA UPDATE IF ALLOCATION IS DIFFERENT AND CB REJECT21");
                            newCdaTrans.setAllocTypeId(allocationType.get(0).getAllocTypeId());

                            newCdaTrans.setRemainingCdaAmount("" + ConverterUtils.addDoubleValue(Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount()) / cdaAmountUnit.getAmount()));
                            newCdaTrans.setRemarks("CDA CREATED.CB BILL REJECTED");

                            cdaParkingTransRepository.save(newCdaTrans);


                        } else {

                            CdaParkingTrans cdaParkingTransMain = cdaParkingPreviewsData.get(0);

                            AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingPreviewsData.get(0).getAmountType());
                            double remainingCdaParkingAmount = Double.parseDouble(cdaParkingPreviewsData.get(0).getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                            double parkingAmount = Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount());

                            double bakiPesa = ConverterUtils.doubleSum(remainingCdaParkingAmount, parkingAmount) / cadAmountUnit.getAmount();
                            bakiPesa = (ConverterUtils.addDoubleValue(bakiPesa));
                            cdaParkingTransMain.setRemainingCdaAmount(bakiPesa + "");
                            //cdaParkingTransMain.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                            cdaParkingTransMain.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParkingTransMain.setRemarks("CDA UPDATE IF ALLOCATION IS DIFFERENT AND CB REJECT1");
                            cdaParkingTransRepository.save(cdaParkingTransMain);

                        }

                    }
                }

            }

        }


        if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {

            for (int i = 0; i < approveContigentBillRequest.getCdaParkingId().size(); i++) {
                CdaParkingCrAndDr cdaParkingCrAndDr = parkingCrAndDrRepository.findByCdaCrdrIdAndIsFlagAndIsRevision(approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId(), "0", 0);
                cdaParkingCrAndDr.setIsFlag("1");
                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setRemark("REJECT CB BILL");

                parkingCrAndDrRepository.save(cdaParkingCrAndDr);
            }

            for (ContigentBill contigintBillData : cbData) {
                List<CdaParkingCrAndDr> cdaParkingCrAndDrsList = parkingCrAndDrRepository.findByTransactionIdAndIsFlag(contigintBillData.getCbId(), "0");

                for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaParkingCrAndDrsList) {
                    cdaParkingCrAndDr.setIsFlag("1");
                    cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    cdaParkingCrAndDr.setRemark("REJECT CB BILL");
                    parkingCrAndDrRepository.save(cdaParkingCrAndDr);
                }
            }
        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(approveContigentBillRequest.getGroupId(), hrData.getUnitId());
        if (!mangeInboxOutboxList.isEmpty()) {

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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> updateFinalStatus(UploadCBRequest approveContigentBillRequest) throws IOException {

        DefaultResponse defaultResponse = new DefaultResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO APPROVE CB");
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

        for (ContigentBill contigentBill : cbData) {
            if (contigentBill.getStatus().equalsIgnoreCase("Pending") || contigentBill.getStatus().equalsIgnoreCase("Rejected") || contigentBill.getStatus().equalsIgnoreCase("Verified")) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "THIS CONTINGENT BILL NOT BE APPROVED");
            }

            if (contigentBill.getCbFilePath() != null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "THIS CONTINGENT BILL ALREADY UPDATED");
            }

        }

        for (ContigentBill contigentBill : cbData) {
            contigentBill.setCbFilePath(approveContigentBillRequest.getDocId());
            contigentBill.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            contigentBillRepository.save(contigentBill);
        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(approveContigentBillRequest.getGroupId(), hrData.getUnitId());
        if (!mangeInboxOutboxList.isEmpty()) {

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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<ContingentSaveResponse> verifyContingentBill(ApproveContigentBillRequest approveContigentBillRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO APPROVE CB");
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


        for (int i = 0; i < approveContigentBillRequest.getCdaParkingId().size(); i++) {

            if (approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId() == null || approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA CRDR ID CAN NOT BE BLANK");
            }

            if (approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount() == null || approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CB AMOUNT CAN NOT BE BLANK");
            }
        }


        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
//        List<CdaParkingTrans> checkCdaParkingPreviewsData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitId(cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());
//        if (checkCdaParkingPreviewsData.isEmpty()) {
//            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO CDA FOUND.PLEASE FILL CDA FIRST");
//        }


        String status = "";
        for (ContigentBill contigentBill : cbData) {
            contigentBill.setStatus(approveContigentBillRequest.getStatus());
            contigentBill.setRemarks(approveContigentBillRequest.getRemarks());
            contigentBill.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            status = approveContigentBillRequest.getStatus();
            contigentBill.setVerifiedBy(hrData.getPid());
            if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {
                contigentBill.setIsFlag("1");
            } else {
                contigentBill.setIsFlag("0");
            }
            contigentBillRepository.save(contigentBill);
        }


        if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {


            List<CdaParkingTrans> cdaParkingExitData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitId(cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());
            List<BudgetAllocation> budgetAllocationData = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), allocationType.get(0).getAllocTypeId(), "Approved", "0", "0");

            String amountTypeID = "";

            if (!cdaParkingExitData.isEmpty()) {
                amountTypeID = cdaParkingExitData.get(0).getAmountType();
            } else {
                for (int g = 0; g < budgetAllocationData.size(); g++) {
                    amountTypeID = budgetAllocationData.get(g).getAmountType();
                }
            }


            for (int i = 0; i < approveContigentBillRequest.getCdaParkingId().size(); i++) {

                CdaParkingCrAndDr cdaParkingCrAndDr = parkingCrAndDrRepository.findByCdaCrdrIdAndIsFlagAndIsRevision(approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId(), "0", 0);
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");

                if (cdaParkingTrans == null) {
                    List<CdaParkingTrans> cdaParkingPreviewsData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), cdaParkingCrAndDr.getGinNo(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());

                    if (cdaParkingPreviewsData.isEmpty()) {

                        CdaParkingTrans newCdaTrans = new CdaParkingTrans();
                        BeanUtils.copyProperties(cdaParkingTrans, newCdaTrans);
                        AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(amountTypeID);

                        newCdaTrans.setAmountType(amountTypeID);
                        newCdaTrans.setIsFlag("0");
                        cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTrans.setRemarks("CDA UPDATE IF ALLOCATION IS DIFFERENT AND CB REJECT21");
                        newCdaTrans.setAllocTypeId(allocationType.get(0).getAllocTypeId());

                        newCdaTrans.setRemainingCdaAmount("" + ConverterUtils.addDoubleValue(Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount()) / cdaAmountUnit.getAmount()));
                        newCdaTrans.setRemarks("CDA CREATED.CB BILL REJECTED");

                        cdaParkingTransRepository.save(newCdaTrans);


                    } else {

                        CdaParkingTrans cdaParkingTransMain = cdaParkingPreviewsData.get(0);

                        AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingPreviewsData.get(0).getAmountType());
                        double remainingCdaParkingAmount = Double.parseDouble(cdaParkingPreviewsData.get(0).getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                        double parkingAmount = Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount());

                        double bakiPesa = ConverterUtils.doubleSum(remainingCdaParkingAmount, parkingAmount) / cadAmountUnit.getAmount();
                        bakiPesa = (ConverterUtils.addDoubleValue(bakiPesa));
                        cdaParkingTransMain.setRemainingCdaAmount(bakiPesa + "");
                        ///cdaParkingTransMain.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                        cdaParkingTransMain.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTransMain.setRemarks("CDA UPDATE IF ALLOCATION IS DIFFERENT AND CB REJECT1");
                        cdaParkingTransRepository.save(cdaParkingTransMain);

                    }
                } else {

                    if (cdaParkingTrans.getAllocTypeId().equalsIgnoreCase(allocationType.get(0).getAllocTypeId())) {

                        if (cdaParkingTrans.getIsFlag().equalsIgnoreCase("1")) {

                            List<CdaParkingTrans> cdaParkingPreviewsData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), cdaParkingTrans.getGinNo(), "0", cdaParkingTrans.getAllocTypeId(), hrData.getUnitId());

                            if (cdaParkingPreviewsData.isEmpty()) {

                                CdaParkingTrans newCdaTrans = new CdaParkingTrans();
                                BeanUtils.copyProperties(cdaParkingTrans, newCdaTrans);
                                AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(amountTypeID);

                                newCdaTrans.setAmountType(amountTypeID);
                                newCdaTrans.setIsFlag("0");

                                newCdaTrans.setRemainingCdaAmount("" + ConverterUtils.addDoubleValue(Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount()) / cdaAmountUnit.getAmount()));
                                newCdaTrans.setRemarks("CDA CREATED.CB BILL REJECTED");

                                cdaParkingTransRepository.save(newCdaTrans);


                            } else {

                                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingPreviewsData.get(0).getAmountType());
                                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                                double parkingAmount = Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount());

                                double bakiPesa = ConverterUtils.doubleSum(remainingCdaParkingAmount, parkingAmount) / cadAmountUnit.getAmount();
                                // cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                                bakiPesa = (ConverterUtils.addDoubleValue(bakiPesa));
                                cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                                cdaParkingTransRepository.save(cdaParkingTrans);

                            }


                        } else {

                            if (cdaParkingTrans.getAllocTypeId().equalsIgnoreCase(allocationType.get(0).getAllocTypeId())) {
                                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
                                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                                double parkingAmount = Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount());

                                double bakiPesa = ConverterUtils.doubleSum(remainingCdaParkingAmount, parkingAmount) / cadAmountUnit.getAmount();
                                bakiPesa = (ConverterUtils.addDoubleValue(bakiPesa));
                                cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                                //cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                                cdaParkingTransRepository.save(cdaParkingTrans);

                            }
                        }


                    } else {

                        List<CdaParkingTrans> cdaParkingPreviewsData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(cbData.get(0).getFinYear(), cbData.get(0).getBudgetHeadID(), cdaParkingTrans.getGinNo(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());

                        if (cdaParkingPreviewsData.isEmpty()) {

                            CdaParkingTrans newCdaTrans = new CdaParkingTrans();
                            BeanUtils.copyProperties(cdaParkingTrans, newCdaTrans);
                            AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(amountTypeID);

                            newCdaTrans.setAmountType(amountTypeID);
                            newCdaTrans.setIsFlag("0");
                            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParkingTrans.setRemarks("CDA UPDATE IF ALLOCATION IS DIFFERENT AND CB REJECT21");
                            newCdaTrans.setAllocTypeId(allocationType.get(0).getAllocTypeId());

                            newCdaTrans.setRemainingCdaAmount("" + ConverterUtils.addDoubleValue(Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount()) / cdaAmountUnit.getAmount()));
                            newCdaTrans.setRemarks("CDA CREATED.CB BILL REJECTED");

                            cdaParkingTransRepository.save(newCdaTrans);


                        } else {

                            CdaParkingTrans cdaParkingTransMain = cdaParkingPreviewsData.get(0);

                            AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingPreviewsData.get(0).getAmountType());
                            double remainingCdaParkingAmount = Double.parseDouble(cdaParkingPreviewsData.get(0).getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                            double parkingAmount = Double.parseDouble(approveContigentBillRequest.getCdaParkingId().get(i).getAllocatedAmount());

                            double bakiPesa = ConverterUtils.doubleSum(remainingCdaParkingAmount, parkingAmount) / cadAmountUnit.getAmount();
                            bakiPesa = (ConverterUtils.addDoubleValue(bakiPesa));
                            cdaParkingTransMain.setRemainingCdaAmount(bakiPesa + "");
                            ///cdaParkingTransMain.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                            cdaParkingTransMain.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParkingTransMain.setRemarks("CDA UPDATE IF ALLOCATION IS DIFFERENT AND CB REJECT1");
                            cdaParkingTransRepository.save(cdaParkingTransMain);

                        }

                    }
                }

            }

        }

        if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {

            for (int i = 0; i < approveContigentBillRequest.getCdaParkingId().size(); i++) {
                CdaParkingCrAndDr cdaParkingCrAndDr = parkingCrAndDrRepository.findByCdaCrdrIdAndIsFlagAndIsRevision(approveContigentBillRequest.getCdaParkingId().get(i).getCdacrDrId(), "0", 0);
                cdaParkingCrAndDr.setIsFlag("1");
                parkingCrAndDrRepository.save(cdaParkingCrAndDr);
            }


            for (ContigentBill contigintBillData : cbData) {
                List<CdaParkingCrAndDr> cdaParkingCrAndDrsList = parkingCrAndDrRepository.findByTransactionIdAndIsFlag(contigintBillData.getCbId(), "0");

                for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaParkingCrAndDrsList) {
                    cdaParkingCrAndDr.setIsFlag("1");
                    parkingCrAndDrRepository.save(cdaParkingCrAndDr);
                }
            }


        }

        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(approveContigentBillRequest.getGroupId(), hrData.getUnitId());
        if (!mangeInboxOutboxList.isEmpty()) {

            MangeInboxOutbox mangeInboxOutbox = mangeInboxOutboxList.get(0);

            if (status.equalsIgnoreCase("Verified")) {
                mangeInboxOutbox.setState("AP");
            } else if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Reject")) {
                mangeInboxOutbox.setState("CR");
            }

            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setStatus(approveContigentBillRequest.getStatus());
            mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        }
        ContingentSaveResponse contingentSaveResponse = new ContingentSaveResponse();
        contingentSaveResponse.setMsg("Data " + approveContigentBillRequest.getStatus() + " Successfully");
        return ResponseUtils.createSuccessResponse(contingentSaveResponse, new TypeReference<ContingentSaveResponse>() {
        });
    }

}

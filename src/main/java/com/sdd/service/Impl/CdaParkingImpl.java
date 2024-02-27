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
import com.sdd.service.CdaParkingService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Service
public class CdaParkingImpl implements CdaParkingService {

    @Autowired
    CgUnitRepository cgUnitRepository;

    @Autowired
    MangeInboxOutBoxRepository mangeInboxOutBoxRepository;

    @Autowired
    private HrDataRepository hrDataRepository;

    @Autowired
    AmountUnitRepository amountUnitRepository;

    @Autowired
    BudgetFinancialYearRepository budgetFinancialYearRepository;

    @Autowired
    CdaParkingRepository cdaParkingRepository;

    @Autowired
    CdaParkingTransRepository cdaParkingTransRepository;

    @Autowired
    CdaUpdateHistoryRepository cdaUpdateHistoryRepository;

    @Autowired
    CdaParkingCrAndDrRepository parkingCrAndDrRepository;

    @Autowired
    AllocationRepository allocationRepository;

    @Autowired
    BudgetAllocationDetailsRepository budgetAllocationDetailsRepository;

    @Autowired
    BudgetAllocationRepository budgetAllocationRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HeaderUtils headerUtils;

    @Autowired
    SubHeadRepository subHeadRepository;

    @Autowired
    ContigentBillRepository contigentBillRepository;


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> saveCdaParkingData(CDARequest cdaRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CDA PARKING");
        }

        double cadTotalAmount = 0;
        double totalAmount = 0;
        for (int i = 0; i < cdaRequest.getCdaRequest().size(); i++) {


            if (cdaRequest.getCdaRequest().get(i).getBudgetFinancialYearId() == null || cdaRequest.getCdaRequest().get(i).getBudgetFinancialYearId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR ID NOT BE BLANK");
            }


            if (cdaRequest.getCdaRequest().get(i).getAllocationTypeID() == null || cdaRequest.getCdaRequest().get(i).getAllocationTypeID().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount() == null || cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AVAILABLE AMOUNT CAN NOT BE BLANK");
            }


            if (cdaRequest.getCdaRequest().get(i).getGinNo() == null || cdaRequest.getCdaRequest().get(i).getGinNo().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GIN NUMBER CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getAuthGroupId() == null || cdaRequest.getCdaRequest().get(i).getAuthGroupId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTH GROUP ID CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getTransactionId() == null || cdaRequest.getCdaRequest().get(i).getTransactionId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TRANSACTION ID CAN NOT BE BLANK");
            }


            if (cdaRequest.getCdaRequest().get(i).getAmountTypeId() == null || cdaRequest.getCdaRequest().get(i).getAmountTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID CAN NOT BE BLANK   key:-amountTypeId");
            }


            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaRequest.getCdaRequest().get(i).getAmountTypeId());
            if (amountUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
            }

            cadTotalAmount = cadTotalAmount + Double.parseDouble(cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount()) * amountUnit.getAmount();

            CdaParking ginNumber = cdaParkingRepository.findByGinNo(cdaRequest.getCdaRequest().get(i).getGinNo());
            if (ginNumber == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID GIN NUMBER.");
            }

            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(cdaRequest.getCdaRequest().get(i).getBudgetFinancialYearId());
            if (budgetFinancialYear == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
            }

            AllocationType allocationType = allocationRepository.findByAllocTypeId(cdaRequest.getCdaRequest().get(i).getAllocationTypeID());
            if (allocationType == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
            }

            List<CdaParkingTrans> cdaParkingTransListData = cdaParkingTransRepository.findByTransactionId(cdaRequest.getCdaRequest().get(i).getTransactionId());
            if (!cdaParkingTransListData.isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA DATA ALREADY SAVE OR REVISED ALLOCATION.");
            }

        }


        for (int b = 0; b < cdaRequest.getCdaRequest().size(); b++) {
            List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByAuthGroupIdAndTransactionIdAndIsFlag(cdaRequest.getCdaRequest().get(b).getAuthGroupId(), cdaRequest.getCdaRequest().get(b).getTransactionId(), "0");
            if (!cdaParkingTransList.isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DATA ALREADY SAVE.YOU CAN NOT CHANGE NOW.");
            }
        }


        boolean data = checkDuplicateData(cdaRequest.getCdaRequest());
        if (data) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DUPLICATE CDA FOUND.PLEASE CHECK");
        }


        String groupId = "";

        for (int i = 0; i < cdaRequest.getCdaRequest().size(); i++) {
            CdaParkingTrans cdaParkingTrans = new CdaParkingTrans();

            cdaParkingTrans.setCdaParkingId(HelperUtils.getCdaId());
            cdaParkingTrans.setFinYearId(cdaRequest.getCdaRequest().get(i).getBudgetFinancialYearId());
            //cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount()));
            cdaParkingTrans.setRemainingCdaAmount(cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount());
            cdaParkingTrans.setBudgetHeadId(cdaRequest.getCdaRequest().get(i).getBudgetHeadId());
            cdaParkingTrans.setRemarks(cdaRequest.getCdaRequest().get(i).getRemark());
            cdaParkingTrans.setGinNo(cdaRequest.getCdaRequest().get(i).getGinNo());
            cdaParkingTrans.setUnitId(hrData.getUnitId());
            cdaParkingTrans.setIsFlag("0");
            cdaParkingTrans.setRemarks("NEW CDA");
            cdaParkingTrans.setAmountType(cdaRequest.getCdaRequest().get(i).getAmountTypeId());
            cdaParkingTrans.setTransactionId(cdaRequest.getCdaRequest().get(i).getTransactionId());
            cdaParkingTrans.setAllocTypeId(cdaRequest.getCdaRequest().get(i).getAllocationTypeID());
            cdaParkingTrans.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingTrans.setAuthGroupId(cdaRequest.getCdaRequest().get(i).getAuthGroupId());
            groupId = (cdaRequest.getCdaRequest().get(i).getAuthGroupId());
            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());

            CdaParkingTrans saveCdaData = cdaParkingTransRepository.save(cdaParkingTrans);


            CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
            cdaParkingCrAndDr.setCdaParkingTrans(saveCdaData.getCdaParkingId());
            cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
            cdaParkingCrAndDr.setFinYearId(saveCdaData.getFinYearId());
            cdaParkingCrAndDr.setBudgetHeadId(saveCdaData.getBudgetHeadId());
            cdaParkingCrAndDr.setGinNo(saveCdaData.getGinNo());
            cdaParkingCrAndDr.setUnitId(saveCdaData.getUnitId());
            cdaParkingCrAndDr.setAuthGroupId(saveCdaData.getAuthGroupId());
            cdaParkingCrAndDr.setAmount(saveCdaData.getRemainingCdaAmount());
            cdaParkingCrAndDr.setIscrdr("CR");
            cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingCrAndDr.setAllocTypeId(saveCdaData.getAllocTypeId());
            cdaParkingCrAndDr.setIsFlag("0");
            cdaParkingCrAndDr.setRemark("NEW CDA");
            cdaParkingCrAndDr.setTransactionId(saveCdaData.getTransactionId());
            cdaParkingCrAndDr.setAmountType(saveCdaData.getAmountType());
            cdaParkingCrAndDr.setIsRevision(0);
            parkingCrAndDrRepository.save(cdaParkingCrAndDr);
        }


        if (!hrData.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
            String subHead = "";
            for (int i = 0; i < cdaRequest.getCdaRequest().size(); i++) {
                subHead = cdaRequest.getCdaRequest().get(i).getBudgetHeadId();
            }

            BudgetHead budgetHead = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(subHead);

            MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
            mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
            mangeInboxOutbox.setRemarks("CDA added by " + hrData.getUnit() + " in " + budgetHead.getSubHeadDescr());
            mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setToUnit(HelperUtils.HEADUNITID);
            mangeInboxOutbox.setFromUnit(hrData.getUnitId());
            mangeInboxOutbox.setGroupId(groupId);
            mangeInboxOutbox.setType("CDA added by " + hrData.getUnit() + " in " + budgetHead.getSubHeadDescr());
            mangeInboxOutbox.setRoleId(hrData.getRoleId());
            mangeInboxOutbox.setCreaterpId(hrData.getPid());
            mangeInboxOutbox.setState("CR");
            mangeInboxOutbox.setApproverpId(hrData.getPid());
            mangeInboxOutbox.setIsFlag("1");
            mangeInboxOutbox.setIsArchive("0");
            mangeInboxOutbox.setIsRebase("0");
            mangeInboxOutbox.setIsApproved("0");
            mangeInboxOutbox.setIsRevision(0);
            mangeInboxOutbox.setStatus("Fully Approved");
            mangeInboxOutbox.setIsBgcg("CDAI");

            mangeInboxOutBoxRepository.save(mangeInboxOutbox);

        }


        boolean allCda = true;
        List<MangeInboxOutbox> inboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(groupId, hrData.getUnitId());
        if (!inboxOutboxList.isEmpty()) {
            List<BudgetAllocation> budgetAllocationList;

            if (hrData.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
                budgetAllocationList = budgetAllocationRepository.findByAuthGroupIdAndIsFlag(groupId, "0");
            } else {
                budgetAllocationList = budgetAllocationRepository.findByAuthGroupIdAndIsFlagAndToUnit(groupId, "0", hrData.getUnitId());
            }


            for (BudgetAllocation budgetAllocation : budgetAllocationList) {
                if (Double.parseDouble(budgetAllocation.getAllocationAmount()) == 0) {
                    continue;
                }
                List<CdaParkingTrans> cdaList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocation.getAllocationId(), "0");
                if (cdaList.isEmpty()) {
                    allCda = false;
                }
            }
        }

        if (allCda && !inboxOutboxList.isEmpty()) {
            for (MangeInboxOutbox inboxData : inboxOutboxList) {
                inboxData.setIsApproved("1");
                inboxData.setIsFlag("1");
                mangeInboxOutBoxRepository.save(inboxData);
            }
        }


        DefaultResponse defaultResponse = new DefaultResponse();
        defaultResponse.setMsg("CDA Data Save successfully");

        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> saveCdaParkingDataForRebase(CDARequest cdaRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CDA PARKING");
        }

        if (cdaRequest.getTotalExpenditure() == null || cdaRequest.getTotalExpenditure().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TOTAL EXPENDITURE CAN NOT BE BLANK");
        }
        double expendture = Double.parseDouble(cdaRequest.getTotalExpenditure());

        double cadTotalAmount = 0;
        double totalAmount = 0;
        for (Integer i = 0; i < cdaRequest.getCdaRequest().size(); i++) {


            if (cdaRequest.getCdaRequest().get(i).getBudgetFinancialYearId() == null || cdaRequest.getCdaRequest().get(i).getBudgetFinancialYearId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR ID NOT BE BLANK");
            }


            if (cdaRequest.getCdaRequest().get(i).getAllocationTypeID() == null || cdaRequest.getCdaRequest().get(i).getAllocationTypeID().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount() == null || cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AVAILABLE AMOUNT CAN NOT BE BLANK");
            }


            if (cdaRequest.getCdaRequest().get(i).getGinNo() == null || cdaRequest.getCdaRequest().get(i).getGinNo().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GIN NUMBER CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getAuthGroupId() == null || cdaRequest.getCdaRequest().get(i).getAuthGroupId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTH GROUP ID CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getTransactionId() == null || cdaRequest.getCdaRequest().get(i).getTransactionId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TRANSACTION ID CAN NOT BE BLANK");
            }


            if (cdaRequest.getCdaRequest().get(i).getAmountTypeId() == null || cdaRequest.getCdaRequest().get(i).getAmountTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID CAN NOT BE BLANK   key:-amountTypeId");
            }


            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaRequest.getCdaRequest().get(i).getAmountTypeId());
            if (amountUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
            }

            BudgetAllocation budgetAllocation = budgetAllocationRepository.findByAllocationId(cdaRequest.getCdaRequest().get(i).getTransactionId());
            if (budgetAllocation == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TRANSACTION ID");
            }


            cadTotalAmount = cadTotalAmount + Double.parseDouble(cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount()) * amountUnit.getAmount();

            CdaParking ginNumber = cdaParkingRepository.findByGinNo(cdaRequest.getCdaRequest().get(i).getGinNo());
            if (ginNumber == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID GIN NUMBER.");
            }

            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(cdaRequest.getCdaRequest().get(i).getBudgetFinancialYearId());
            if (budgetFinancialYear == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
            }


            AllocationType allocationType = allocationRepository.findByAllocTypeId(cdaRequest.getCdaRequest().get(i).getAllocationTypeID());
            if (allocationType == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
            }


            List<CdaParkingTrans> cdaParkingTransListData = cdaParkingTransRepository.findByTransactionId(cdaRequest.getCdaRequest().get(i).getTransactionId());
            if (cdaParkingTransListData.size() > 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA DATA ALREADY SAVE OR REVISED ALLOCATION.");
            }

        }


        for (Integer b = 0; b < cdaRequest.getCdaRequest().size(); b++) {
            List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByAuthGroupIdAndTransactionIdAndIsFlag(cdaRequest.getCdaRequest().get(b).getAuthGroupId(), cdaRequest.getCdaRequest().get(b).getTransactionId(), "0");
            if (cdaParkingTransList.size() > 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DATA ALREADY SAVE.YOU CAN NOT CHANGE NOW.");
            }
        }


        boolean data = checkDuplicateData(cdaRequest.getCdaRequest());
        if (data) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DUPLICATE CDA FOUND.PLEASE CHECK");
        }


        String groupId = "";

        for (Integer i = 0; i < cdaRequest.getCdaRequest().size(); i++) {
            CdaParkingTrans cdaParkingTrans = new CdaParkingTrans();

            cdaParkingTrans.setCdaParkingId(HelperUtils.getCdaId());
            cdaParkingTrans.setFinYearId(cdaRequest.getCdaRequest().get(i).getBudgetFinancialYearId());
            //cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount()));
            cdaParkingTrans.setRemainingCdaAmount(cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount());
            cdaParkingTrans.setBudgetHeadId(cdaRequest.getCdaRequest().get(i).getBudgetHeadId());
            cdaParkingTrans.setRemarks(cdaRequest.getCdaRequest().get(i).getRemark());
            cdaParkingTrans.setGinNo(cdaRequest.getCdaRequest().get(i).getGinNo());
            cdaParkingTrans.setUnitId(hrData.getUnitId());
            cdaParkingTrans.setIsFlag("0");
            cdaParkingTrans.setAmountType(cdaRequest.getCdaRequest().get(i).getAmountTypeId());
            cdaParkingTrans.setTransactionId(cdaRequest.getCdaRequest().get(i).getTransactionId());
            cdaParkingTrans.setAllocTypeId(cdaRequest.getCdaRequest().get(i).getAllocationTypeID());
            cdaParkingTrans.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingTrans.setAuthGroupId(cdaRequest.getCdaRequest().get(i).getAuthGroupId());
            groupId = (cdaRequest.getCdaRequest().get(i).getAuthGroupId());
            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());

            CdaParkingTrans saveCdaData = cdaParkingTransRepository.save(cdaParkingTrans);


            CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
            cdaParkingCrAndDr.setCdaParkingTrans(saveCdaData.getCdaParkingId());
            cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
            cdaParkingCrAndDr.setFinYearId(saveCdaData.getFinYearId());
            cdaParkingCrAndDr.setBudgetHeadId(saveCdaData.getBudgetHeadId());
            cdaParkingCrAndDr.setGinNo(saveCdaData.getGinNo());
            cdaParkingCrAndDr.setUnitId(saveCdaData.getUnitId());
            cdaParkingCrAndDr.setAuthGroupId(saveCdaData.getAuthGroupId());
            cdaParkingCrAndDr.setIscrdr("CR");
            cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingCrAndDr.setAllocTypeId(saveCdaData.getAllocTypeId());
            cdaParkingCrAndDr.setIsFlag("0");
            cdaParkingCrAndDr.setTransactionId(saveCdaData.getTransactionId());
            cdaParkingCrAndDr.setAmountType(saveCdaData.getAmountType());
            cdaParkingCrAndDr.setIsRevision(0);
            parkingCrAndDrRepository.save(cdaParkingCrAndDr);
        }


        boolean allCda = true;
        List<MangeInboxOutbox> inboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(groupId, hrData.getUnitId());
        if (inboxOutboxList.size() > 0) {
            List<BudgetAllocation> budgetAllocationList;

            if (hrData.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
                budgetAllocationList = budgetAllocationRepository.findByAuthGroupIdAndIsFlag(groupId, "0");
            } else {
                budgetAllocationList = budgetAllocationRepository.findByAuthGroupIdAndIsFlagAndToUnit(groupId, "0", hrData.getUnitId());
            }


            for (Integer i = 0; i < budgetAllocationList.size(); i++) {
                BudgetAllocation budgetAllocation = budgetAllocationList.get(i);

                if (Double.parseDouble(budgetAllocation.getAllocationAmount()) == 0) {
                    continue;
                }

                List<CdaParkingTrans> cdaList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocation.getAllocationId(), "0");
                if (cdaList.size() == 0) {
                    allCda = false;
                }
            }
        }

        if (allCda && inboxOutboxList.size() > 0) {
            for (Integer m = 0; m < inboxOutboxList.size(); m++) {
                MangeInboxOutbox inboxData = inboxOutboxList.get(m);
                inboxData.setIsApproved("1");
                inboxData.setIsFlag("1");
                mangeInboxOutBoxRepository.save(inboxData);
            }
        }


        DefaultResponse defaultResponse = new DefaultResponse();
        defaultResponse.setMsg("CDA Data Save successfully");

        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<CdaParkingTransResponse> getCdaData(String groupId) {
        CdaParkingTransResponse mainResponse = new CdaParkingTransResponse();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CDA PARKING");
        }
        List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByAuthGroupIdAndIsFlag(groupId, "0");
        List<CdaParkingTransSubResponse> cdaParkingTransList = new ArrayList<CdaParkingTransSubResponse>();

        for (Integer i = 0; i < cdaParkingTrans.size(); i++) {
            CdaParkingTransSubResponse cdaParkingTransResponse = new CdaParkingTransSubResponse();
            cdaParkingTransResponse.setCdaParkingId(cdaParkingTrans.get(i).getAuthGroupId());
            cdaParkingTransResponse.setFinYearId(budgetFinancialYearRepository.findBySerialNo(cdaParkingTrans.get(i).getFinYearId()));
            cdaParkingTransResponse.setBudgetHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(cdaParkingTrans.get(i).getBudgetHeadId()));
            cdaParkingTransResponse.setRemarks(cdaParkingTrans.get(i).getRemarks());
            cdaParkingTransResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingTrans.get(i).getGinNo()));
            cdaParkingTransResponse.setRemainingCdaAmount(cdaParkingTrans.get(i).getRemainingCdaAmount());
            cdaParkingTransResponse.setUpdatedOn(cdaParkingTrans.get(i).getUpdatedOn());
            cdaParkingTransResponse.setUnitId(cdaParkingTrans.get(i).getUnitId());
            cdaParkingTransResponse.setTransactionId(cdaParkingTrans.get(i).getTransactionId());
            cdaParkingTransResponse.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(i).getAmountType()));
            cdaParkingTransResponse.setCreatedOn(cdaParkingTrans.get(i).getCreatedOn());
            cdaParkingTransResponse.setAuthGroupId(cdaParkingTrans.get(i).getAuthGroupId());
            cdaParkingTransList.add(cdaParkingTransResponse);
        }


        mainResponse.setCdaParking(cdaParkingTransList);
        return ResponseUtils.createSuccessResponse(mainResponse, new TypeReference<CdaParkingTransResponse>() {
        });

    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<CdaParkingTransResponse> getCdaDataUnit(String groupId, String unitId) {
        CdaParkingTransResponse mainResponse = new CdaParkingTransResponse();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CDA PARKING");
        }
        List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByAuthGroupIdAndIsFlagAndUnitId(groupId, "0", unitId);
        List<CdaParkingTransSubResponse> cdaParkingTransList = new ArrayList<CdaParkingTransSubResponse>();

        for (Integer i = 0; i < cdaParkingTrans.size(); i++) {
            CdaParkingTransSubResponse cdaParkingTransResponse = new CdaParkingTransSubResponse();
            cdaParkingTransResponse.setCdaParkingId(cdaParkingTrans.get(i).getAuthGroupId());
            cdaParkingTransResponse.setFinYearId(budgetFinancialYearRepository.findBySerialNo(cdaParkingTrans.get(i).getFinYearId()));
            cdaParkingTransResponse.setBudgetHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(cdaParkingTrans.get(i).getBudgetHeadId()));
            cdaParkingTransResponse.setRemarks(cdaParkingTrans.get(i).getRemarks());
            cdaParkingTransResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingTrans.get(i).getGinNo()));
            cdaParkingTransResponse.setRemainingCdaAmount(cdaParkingTrans.get(i).getRemainingCdaAmount());
            cdaParkingTransResponse.setUpdatedOn(cdaParkingTrans.get(i).getUpdatedOn());
            cdaParkingTransResponse.setUnitId(cdaParkingTrans.get(i).getUnitId());
            cdaParkingTransResponse.setTransactionId(cdaParkingTrans.get(i).getTransactionId());
            cdaParkingTransResponse.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(i).getAmountType()));
            cdaParkingTransResponse.setCreatedOn(cdaParkingTrans.get(i).getCreatedOn());
            cdaParkingTransResponse.setAuthGroupId(cdaParkingTrans.get(i).getAuthGroupId());
            cdaParkingTransList.add(cdaParkingTransResponse);
        }


        mainResponse.setCdaParking(cdaParkingTransList);
        return ResponseUtils.createSuccessResponse(mainResponse, new TypeReference<CdaParkingTransResponse>() {
        });

    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<CdaHistoryResponse> getCdaHistoryData(String groupId, String unitId) {
        CdaHistoryResponse mainResponse = new CdaHistoryResponse();
        List<CdaParkingHistoryDto> oldCda = new ArrayList<CdaParkingHistoryDto>();
        List<CdaParkingHistoryDto> newCda = new ArrayList<CdaParkingHistoryDto>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CDA PARKING");
        }

        List<CdaParkingUpdateHistory> cdaParkingUpdateHistoryList = cdaUpdateHistoryRepository.findByAuthGroupIdAndUnitId(groupId, unitId);

        for (CdaParkingUpdateHistory cdaParkingUpdateHistory : cdaParkingUpdateHistoryList) {

            CdaParkingHistoryDto cdaParkingTransResponse = new CdaParkingHistoryDto();
            cdaParkingTransResponse.setCdaParkingUpdateId(cdaParkingUpdateHistory.getCdaParkingUpdateId());
            cdaParkingTransResponse.setOldAmount(ConverterUtils.addDecimalPoint(cdaParkingUpdateHistory.getOldAmount() + ""));//round off added by deewan
            cdaParkingTransResponse.setOldGinNo(cdaParkingRepository.findByGinNo(cdaParkingUpdateHistory.getOldGinNo()));
            cdaParkingTransResponse.setNewAmount(ConverterUtils.addDecimalPoint(cdaParkingUpdateHistory.getNewAmount() + ""));//round off added by deewan
            cdaParkingTransResponse.setNewGinNo(cdaParkingRepository.findByGinNo(cdaParkingUpdateHistory.getNewGinNo()));
            cdaParkingTransResponse.setUnitId(cgUnitRepository.findByUnit(cdaParkingUpdateHistory.getUnitId()));
            cdaParkingTransResponse.setAuthGroupId(cdaParkingUpdateHistory.getAuthGroupId());
            cdaParkingTransResponse.setCreatedOn(cdaParkingUpdateHistory.getCreatedOn() + "");
            cdaParkingTransResponse.setUpdatedOn(cdaParkingUpdateHistory.getUpdatedOn() + "");
            cdaParkingTransResponse.setUpdatedBy(hrDataRepository.findByUserName(cdaParkingUpdateHistory.getUpdatedBy()));
            cdaParkingTransResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingUpdateHistory.getAmountType()));
            cdaParkingTransResponse.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(cdaParkingUpdateHistory.getSubHead()));

            if (cdaParkingUpdateHistory.getNewGinNo() == null || cdaParkingUpdateHistory.getNewAmount() == null) {
                oldCda.add(cdaParkingTransResponse);
            } else {
                newCda.add(cdaParkingTransResponse);
            }
        }
        mainResponse.setNewCda(newCda);
        mainResponse.setOldCda(oldCda);
        return ResponseUtils.createSuccessResponse(mainResponse, new TypeReference<CdaHistoryResponse>() {
        });

    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<CdaParkingTransResponse> getAllCdaData(CDARequest cdaRequest) {
        CdaParkingTransResponse mainResponse = new CdaParkingTransResponse();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CDA PARKING");
        }
        List<AuthorityTableResponse> authorityTableList = new ArrayList<AuthorityTableResponse>();

        if (cdaRequest.getBudgetHeadId() == null || cdaRequest.getBudgetHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }

        if (cdaRequest.getFinancialYearId() == null || cdaRequest.getFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
        }

        if (cdaRequest.getAllocationTypeId() == null || cdaRequest.getAllocationTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID CAN NOT BE BLANK");
        }


        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(cdaRequest.getFinancialYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        AllocationType allocationType = allocationRepository.findByAllocTypeId(cdaRequest.getAllocationTypeId());
        if (allocationType == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
        }


        List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(cdaRequest.getFinancialYearId(), cdaRequest.getBudgetHeadId(), hrData.getUnitId(), cdaRequest.getAllocationTypeId(), "0");

        List<CdaParkingTransSubResponse> cdaParkingTransList = new ArrayList<CdaParkingTransSubResponse>();

        for (Integer i = 0; i < cdaParkingTrans.size(); i++) {
            CdaParkingTransSubResponse cdaParkingTransResponse = new CdaParkingTransSubResponse();
            cdaParkingTransResponse.setCdaParkingId(cdaParkingTrans.get(i).getAuthGroupId());
            cdaParkingTransResponse.setFinYearId(budgetFinancialYearRepository.findBySerialNo(cdaParkingTrans.get(i).getFinYearId()));
            cdaParkingTransResponse.setBudgetHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(cdaParkingTrans.get(i).getBudgetHeadId()));
            cdaParkingTransResponse.setRemarks(cdaParkingTrans.get(i).getRemarks());
            cdaParkingTransResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingTrans.get(i).getGinNo()));
            cdaParkingTransResponse.setAllocationType(allocationRepository.findByAllocTypeId(cdaParkingTrans.get(i).getAllocTypeId()));
            cdaParkingTransResponse.setRemainingCdaAmount(cdaParkingTrans.get(i).getRemainingCdaAmount());
            cdaParkingTransResponse.setUpdatedOn(cdaParkingTrans.get(i).getUpdatedOn());
            cdaParkingTransResponse.setTransactionId(cdaParkingTrans.get(i).getTransactionId());
            cdaParkingTransResponse.setCreatedOn(cdaParkingTrans.get(i).getCreatedOn());
            cdaParkingTransResponse.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(i).getAmountType()));
            cdaParkingTransResponse.setAuthGroupId(cdaParkingTrans.get(i).getAuthGroupId());
            cdaParkingTransResponse.setUnitId(cdaParkingTrans.get(i).getUnitId());
            cdaParkingTransList.add(cdaParkingTransResponse);

            cdaParkingTransResponse.setAuthList(authorityTableList);
        }

        mainResponse.setCdaParking(cdaParkingTransList);
        return ResponseUtils.createSuccessResponse(mainResponse, new TypeReference<CdaParkingTransResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<CdaParking>> getCdaUnitList() {

        List<CdaParking> cdaParkingTrans = cdaParkingRepository.findAll();

        return ResponseUtils.createSuccessResponse(cdaParkingTrans, new TypeReference<List<CdaParking>>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> updateCdaParkingData(CDARequest cdaRequest) throws Exception {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CDA PARKING");
        }


        boolean data = checkDuplicateData(cdaRequest.getCdaRequest());
        if (data) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DUPLICATE CDA FOUND.PLEASE CHECK");
        }

        DefaultResponse defaultResponse = new DefaultResponse();

        String budgetHedaid = "";
        double cadTotalAmount = 0;

        for (int i = 0; i < cdaRequest.getCdaRequest().size(); i++) {


            if (cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount() == null || cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AVAILABLE AMOUNT CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getTransactionId() == null || cdaRequest.getCdaRequest().get(i).getTransactionId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TRANSACTION ID AMOUNT CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getGinNo() == null || cdaRequest.getCdaRequest().get(i).getGinNo().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GIN NUMBER CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getBudgetHeadId() == null || cdaRequest.getCdaRequest().get(i).getBudgetHeadId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET HEAD ID CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getAmountTypeId() == null || cdaRequest.getCdaRequest().get(i).getAmountTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID CAN NOT BE BLANK   key:-amountTypeId");
            }

            BudgetAllocation budgetAllocation = budgetAllocationRepository.findByAllocationId(cdaRequest.getCdaRequest().get(i).getTransactionId());
            if (budgetAllocation == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TRANSACTION ID");
            }


            if (cdaRequest.getCdaRequest().get(i).getTransactionId() == null || cdaRequest.getCdaRequest().get(i).getTransactionId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TRANSACTION ID CAN NOT BE BLANK");
            }

            if (cdaRequest.getCdaRequest().get(i).getAmountTypeId() == null || cdaRequest.getCdaRequest().get(i).getAmountTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID CAN NOT BE BLANK   key:-amountTypeId");
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaRequest.getCdaRequest().get(i).getAmountTypeId());
            if (amountUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
            }

            cadTotalAmount = cadTotalAmount + Double.parseDouble(cdaRequest.getCdaRequest().get(i).getAvailableParkingAmount()) * amountUnit.getAmount();


            CdaParking ginNumber = cdaParkingRepository.findByGinNo(cdaRequest.getCdaRequest().get(i).getGinNo());
            if (ginNumber == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID GIN NUMBER.");
            }


            budgetHedaid = cdaRequest.getCdaRequest().get(i).getBudgetHeadId();


            AllocationType allocationType = allocationRepository.findByAllocTypeId(cdaRequest.getCdaRequest().get(i).getAllocationTypeID());
            if (allocationType == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ");
            }

            List<AllocationType> allocationTypeCurrent = allocationRepository.findByIsFlag("1");
            if (allocationTypeCurrent.isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE.00L");
            }

            if (!(allocationType.getAllocTypeId().equalsIgnoreCase(allocationTypeCurrent.get(0).getAllocTypeId()))) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID DID NOT SAME");
            }

        }


        List<CdaParkingTrans> cdaParkingTransData = cdaParkingTransRepository.findByAuthGroupIdAndBudgetHeadIdAndIsFlag(cdaRequest.getAuthGroupId(), budgetHedaid, "0");
        List<CdaParkingCrAndDr> cdaParkingIsCrDr = parkingCrAndDrRepository.findByAuthGroupIdAndBudgetHeadIdAndIsFlagAndIsRevision(cdaRequest.getAuthGroupId(), budgetHedaid, "0", 0);



        String cdaUpdateAuthGroupId = HelperUtils.getAuthorityGroupId();
        ArrayList<CdaParkingUpdateHistory> cdaParkingUpdateHistoryList = new ArrayList<CdaParkingUpdateHistory>();


        for (CdaParkingTrans cdaParking : cdaParkingTransData) {
            cdaParking.setIsFlag("1");
            cdaParking.setRemarks("CDA UPDATE");
            cdaParkingTransRepository.save(cdaParking);


            CdaParkingUpdateHistory cdaParkingUpdateHistory = new CdaParkingUpdateHistory();
            cdaParkingUpdateHistory.setCdaParkingUpdateId(HelperUtils.getUpdateCDAId());
            cdaParkingUpdateHistory.setOldAmount(cdaParking.getRemainingCdaAmount());
            cdaParkingUpdateHistory.setOldGinNo(cdaParking.getGinNo());
            cdaParkingUpdateHistory.setUnitId(hrData.getUnitId());
            cdaParkingUpdateHistory.setAuthGroupId(cdaUpdateAuthGroupId);
            cdaParkingUpdateHistory.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingUpdateHistory.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingUpdateHistory.setAmountType(cdaParking.getAmountType());
            cdaParkingUpdateHistory.setUpdatedBy(hrData.getPid());
            cdaParkingUpdateHistory.setSubHead(cdaParking.getBudgetHeadId());

            cdaParkingUpdateHistoryList.add(cdaParkingUpdateHistory);
        }

        for (CdaParkingCrAndDr cdaParking : cdaParkingIsCrDr) {
            cdaParking.setIsFlag("1");
            parkingCrAndDrRepository.save(cdaParking);

        }


        String authGroupId = HelperUtils.getCdaId();
        checkCdaInsertOrNot(cdaRequest.getCdaRequest(), authGroupId, hrData, cdaUpdateAuthGroupId, cdaParkingUpdateHistoryList);


        defaultResponse.setMsg("CDA data update successfully");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }

    public void checkCdaInsertOrNot(List<CdaSubRequest> cdaRequest, String authGroupId, HrData hrData,
                                    String cdaUpdateAuthGroupId, ArrayList<CdaParkingUpdateHistory> cdaParkingUpdateHistoryList) {


        for (CdaSubRequest cdaSubRequest : cdaRequest) {

            CdaParkingTrans cdaParkingTrans = new CdaParkingTrans();
            cdaParkingTrans.setCdaParkingId(HelperUtils.getCdaId());
            cdaParkingTrans.setFinYearId(cdaSubRequest.getBudgetFinancialYearId());
            cdaParkingTrans.setBudgetHeadId(cdaSubRequest.getBudgetHeadId());
            cdaParkingTrans.setGinNo(cdaSubRequest.getGinNo());
            cdaParkingTrans.setUnitId(hrData.getUnitId());
            cdaParkingTrans.setIsFlag("0");
            cdaParkingTrans.setAmountType(cdaSubRequest.getAmountTypeId());
            cdaParkingTrans.setAllocTypeId(cdaSubRequest.getAllocationTypeID());
            cdaParkingTrans.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingTrans.setAuthGroupId(authGroupId);
            cdaParkingTrans.setTransactionId(cdaSubRequest.getTransactionId());
            cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(cdaSubRequest.getAvailableParkingAmount()));
            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingTrans.setRemarks("CDA UPDATE for is flag 0");

            CdaParkingTrans saveCdaData = cdaParkingTransRepository.save(cdaParkingTrans);

            CdaParkingUpdateHistory cdaParkingUpdateHistory = new CdaParkingUpdateHistory();
            cdaParkingUpdateHistory.setCdaParkingUpdateId(HelperUtils.getUpdateCDAId());
            cdaParkingUpdateHistory.setNewAmount(saveCdaData.getRemainingCdaAmount());
            cdaParkingUpdateHistory.setNewGinNo(saveCdaData.getGinNo());
            cdaParkingUpdateHistory.setUnitId(hrData.getUnitId());
            cdaParkingUpdateHistory.setAuthGroupId(cdaUpdateAuthGroupId);
            cdaParkingUpdateHistory.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingUpdateHistory.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingUpdateHistory.setAmountType(saveCdaData.getAmountType());
            cdaParkingUpdateHistory.setUpdatedBy(hrData.getPid());
            cdaParkingUpdateHistory.setSubHead(saveCdaData.getBudgetHeadId());
            cdaParkingUpdateHistoryList.add(cdaParkingUpdateHistory);

            CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
            cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
            cdaParkingCrAndDr.setFinYearId(saveCdaData.getFinYearId());
            cdaParkingCrAndDr.setBudgetHeadId(saveCdaData.getBudgetHeadId());
            cdaParkingCrAndDr.setGinNo(saveCdaData.getGinNo());
            cdaParkingCrAndDr.setUnitId(saveCdaData.getUnitId());
            cdaParkingCrAndDr.setAuthGroupId(authGroupId);
            cdaParkingCrAndDr.setAmount(saveCdaData.getRemainingCdaAmount());
            cdaParkingCrAndDr.setIscrdr("CR");
            cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingCrAndDr.setAllocTypeId(cdaSubRequest.getAmountTypeId());
            cdaParkingCrAndDr.setIsFlag("0");
            cdaParkingCrAndDr.setIsRevision(0);
            cdaParkingCrAndDr.setTransactionId(saveCdaData.getTransactionId());
            cdaParkingCrAndDr.setAmountType(saveCdaData.getAmountType());

            parkingCrAndDrRepository.save(cdaParkingCrAndDr);


            if (!hrData.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
                String subHead = "";
                for (CdaParkingUpdateHistory cdaParkingUpdateHistoryData : cdaParkingUpdateHistoryList) {
                    subHead = cdaParkingUpdateHistoryData.getSubHead();
                    cdaUpdateHistoryRepository.save(cdaParkingUpdateHistory);
                }


                BudgetHead budgetHead = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(subHead);
                MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
                mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
                mangeInboxOutbox.setRemarks("CDA update by " + hrData.getUnit() + " in " + budgetHead.getSubHeadDescr());
                mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                mangeInboxOutbox.setToUnit(HelperUtils.HEADUNITID);
                mangeInboxOutbox.setFromUnit(hrData.getUnitId());
                mangeInboxOutbox.setGroupId(cdaUpdateAuthGroupId);
                mangeInboxOutbox.setType("CDA update by " + hrData.getUnit() + " in " + budgetHead.getSubHeadDescr());
                mangeInboxOutbox.setRoleId(hrData.getRoleId());
                mangeInboxOutbox.setCreaterpId(hrData.getPid());
                mangeInboxOutbox.setState("CR");
                mangeInboxOutbox.setApproverpId(hrData.getPid());
                mangeInboxOutbox.setIsFlag("1");
                mangeInboxOutbox.setIsArchive("0");
                mangeInboxOutbox.setIsRebase("0");
                mangeInboxOutbox.setIsApproved("0");
                mangeInboxOutbox.setIsRevision(0);
                mangeInboxOutbox.setStatus("Fully Approved");
                mangeInboxOutbox.setIsBgcg("CDA");

                mangeInboxOutBoxRepository.save(mangeInboxOutbox);

            }
        }

        List<CdaParkingTrans> newCdaEntry = cdaParkingTransRepository.findByAuthGroupIdAndIsFlag(authGroupId, "0");
        if (newCdaEntry.isEmpty()) {
            checkCdaInsertOrNot(cdaRequest, authGroupId, hrData, cdaUpdateAuthGroupId, cdaParkingUpdateHistoryList);
        }
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<ReabseCdaParkingResponse> getOldCdaDataForRebase(CDARequestReBase cdaRequest) {
        ReabseCdaParkingResponse mainResponse = new ReabseCdaParkingResponse();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CDA PARKING");
        }

        if (cdaRequest.getBudgetHeadId() == null || cdaRequest.getBudgetHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }

        if (cdaRequest.getFinancialYearId() == null || cdaRequest.getFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
        }

        if (cdaRequest.getAllocationTypeId() == null || cdaRequest.getAllocationTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID CAN NOT BE BLANK");
        }

        if (cdaRequest.getAmountType() == null || cdaRequest.getAmountType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID CAN NOT BE BLANK");
        }

        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaRequest.getAmountType());
        if (amountUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT UNIT ID");
        }

        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(cdaRequest.getFinancialYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        AllocationType allocationType = allocationRepository.findByAllocTypeId(cdaRequest.getAllocationTypeId());
        if (allocationType == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
        }


        HashMap<String, CdaParkingTransSubResponse> subHeadData = new LinkedHashMap<>();
        List<ContigentBill> contigentBills = contigentBillRepository.findByFinYearAndBudgetHeadIDAndIsUpdateAndIsFlagAndCbUnitId(cdaRequest.getFinancialYearId(), cdaRequest.getBudgetHeadId(), "0", "0", hrData.getUnitId());


        for (ContigentBill contigentBill : contigentBills) {
            List<CdaParkingCrAndDr> cdaParkingCrAndDrsList = parkingCrAndDrRepository.findByTransactionIdAndIsFlag(contigentBill.getCbId(), "0");


            for (CdaParkingCrAndDr cdaParkingCrAndDrs : cdaParkingCrAndDrsList) {
                CdaParking cdaName = cdaParkingRepository.findByGinNo(cdaParkingCrAndDrs.getGinNo());
                if (subHeadData.containsKey(cdaName.getCdaName())) {
                    CdaParkingTransSubResponse cdaParkingTransSubResponses = subHeadData.get(cdaName.getCdaName());

                    double totalBillAmount = Double.parseDouble(cdaParkingCrAndDrs.getAmount()) / amountUnit.getAmount();


                    double totalParking = Double.parseDouble(cdaParkingTransSubResponses.getTotalParkingAmount());
                    double totalRemenig = Double.parseDouble(cdaParkingTransSubResponses.getRemainingCdaAmount());


                    cdaParkingTransSubResponses.setTotalParkingAmount(ConverterUtils.addDecimalPoint(totalBillAmount + totalParking + ""));
                    cdaParkingTransSubResponses.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(totalBillAmount + totalRemenig + ""));
                    subHeadData.put(cdaName.getCdaName(), cdaParkingTransSubResponses);


                } else {
                    double totalBillAmount = Double.parseDouble(cdaParkingCrAndDrs.getAmount());

                    double totalBill = totalBillAmount / amountUnit.getAmount();

                    CdaParkingTransSubResponse cdaParkingTransResponse = new CdaParkingTransSubResponse();

                    cdaParkingTransResponse.setFinYearId(budgetFinancialYearRepository.findBySerialNo(cdaParkingCrAndDrs.getFinYearId()));
                    cdaParkingTransResponse.setBudgetHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(cdaParkingCrAndDrs.getBudgetHeadId()));
                    cdaParkingTransResponse.setGinNo(cdaName);
                    cdaParkingTransResponse.setAllocationType(allocationRepository.findByAllocTypeId(cdaParkingCrAndDrs.getAllocTypeId()));

                    cdaParkingTransResponse.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(totalBill + ""));
                    cdaParkingTransResponse.setTotalParkingAmount(ConverterUtils.addDecimalPoint(totalBill + ""));

                    cdaParkingTransResponse.setUpdatedOn(cdaParkingCrAndDrs.getUpdatedOn());
                    cdaParkingTransResponse.setTransactionId(cdaParkingCrAndDrs.getTransactionId());
                    cdaParkingTransResponse.setCreatedOn(cdaParkingCrAndDrs.getCreatedOn());
                    cdaParkingTransResponse.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDrs.getAmountType()));
                    cdaParkingTransResponse.setAuthGroupId(cdaParkingCrAndDrs.getAuthGroupId());
                    cdaParkingTransResponse.setUnitId(cdaParkingCrAndDrs.getUnitId());

                    subHeadData.put(cdaName.getCdaName(), cdaParkingTransResponse);
                }

            }
        }

        mainResponse.setSubHeadData(subHeadData);
        return ResponseUtils.createSuccessResponse(mainResponse, new TypeReference<ReabseCdaParkingResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<CdaAndAllocationDataResponse> getAllBillCdaAndAllocationSummery(CdaAndAllocationDataRequest cdaRequest) {
        CdaAndAllocationDataResponse mainResponse = new CdaAndAllocationDataResponse();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CDA PARKING");
        }

        if (cdaRequest.getBudgetHeadId() == null || cdaRequest.getBudgetHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }

        if (cdaRequest.getFinancialYearId() == null || cdaRequest.getFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
        }


        if (cdaRequest.getAmountType() == null || cdaRequest.getAmountType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID CAN NOT BE BLANK");
        }

        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaRequest.getAmountType());
        if (amountUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT UNIT ID");
        }

        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(cdaRequest.getFinancialYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        AllocationType allocationType = allocationRepository.findByAllocTypeId(cdaRequest.getAllocationTypeId());
        if (allocationType == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
        }

        double totalExpWithAllocation = 0;
        String currentUnitId = hrData.getUnitId();
        List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
        unitList.remove(cgUnitRepository.findByUnit(currentUnitId));
        List<BudgetAllocation> budgetAllocationData = new ArrayList<BudgetAllocation>();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        for (CgUnit cgUnit : unitList) {
            List<BudgetAllocation> dataBudget = budgetAllocationRepository.findByToUnitAndFromUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(cgUnit.getUnit(), hrData.getUnitId(), cdaRequest.getFinancialYearId(), cdaRequest.getBudgetHeadId(), cdaRequest.getAllocationTypeId(), "0", "0", "Approved");
            for (BudgetAllocation budgetAllocation : dataBudget) {
                double allocationAMount = Double.parseDouble(budgetAllocation.getAllocationAmount());
                if (allocationAMount > 0) {
                    AmountUnit amountUnit1 = amountUnitRepository.findByAmountTypeId(budgetAllocation.getAmountType());
                    budgetAllocationData.add(budgetAllocation);
                    totalExpWithAllocation = totalExpWithAllocation + (allocationAMount * amountUnit1.getAmount());
                }
            }
        }
        mainResponse.setBudgetAllocationData(budgetAllocationData);

        HashMap<String, CdaParkingTransSubResponse> subHeadData = new LinkedHashMap<>();
        List<ContigentBill> contingentBills = contigentBillRepository.findByFinYearAndBudgetHeadIDAndIsUpdateAndIsFlagAndCbUnitId(cdaRequest.getFinancialYearId(), cdaRequest.getBudgetHeadId(), "0", "0", hrData.getUnitId());

        for (ContigentBill contigentBill : contingentBills) {
            List<CdaParkingCrAndDr> cdaParkingCrAndDrsList = parkingCrAndDrRepository.findByTransactionIdAndIsFlag(contigentBill.getCbId(), "0");


            for (CdaParkingCrAndDr cdaParkingCrAndDrs : cdaParkingCrAndDrsList) {
                CdaParking cdaName = cdaParkingRepository.findByGinNo(cdaParkingCrAndDrs.getGinNo());
                if (subHeadData.containsKey(cdaName.getCdaName())) {
                    CdaParkingTransSubResponse cdaParkingTransSubResponses = subHeadData.get(cdaName.getCdaName());

                    double totalBillAmount = Double.parseDouble(cdaParkingCrAndDrs.getAmount()) / amountUnit.getAmount();

                    double totalParking = Double.parseDouble(cdaParkingTransSubResponses.getTotalParkingAmount());
                    double totalRemenig = Double.parseDouble(cdaParkingTransSubResponses.getRemainingCdaAmount());

                    cdaParkingTransSubResponses.setTotalParkingAmount(ConverterUtils.addDecimalPoint(totalBillAmount + totalParking + ""));
                    cdaParkingTransSubResponses.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(totalBillAmount + totalRemenig + ""));
                    subHeadData.put(cdaName.getCdaName(), cdaParkingTransSubResponses);


                } else {
                    double totalBillAmount = Double.parseDouble(cdaParkingCrAndDrs.getAmount());

                    double totalBill = totalBillAmount / amountUnit.getAmount();

                    CdaParkingTransSubResponse cdaParkingTransResponse = new CdaParkingTransSubResponse();

                    cdaParkingTransResponse.setFinYearId(budgetFinancialYearRepository.findBySerialNo(cdaParkingCrAndDrs.getFinYearId()));
                    cdaParkingTransResponse.setBudgetHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(cdaParkingCrAndDrs.getBudgetHeadId()));
                    cdaParkingTransResponse.setGinNo(cdaName);
                    cdaParkingTransResponse.setAllocationType(allocationRepository.findByAllocTypeId(cdaParkingCrAndDrs.getAllocTypeId()));

                    cdaParkingTransResponse.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(totalBill + ""));
                    cdaParkingTransResponse.setTotalParkingAmount(ConverterUtils.addDecimalPoint(totalBill + ""));

                    cdaParkingTransResponse.setUpdatedOn(cdaParkingCrAndDrs.getUpdatedOn());
                    cdaParkingTransResponse.setTransactionId(cdaParkingCrAndDrs.getTransactionId());
                    cdaParkingTransResponse.setCreatedOn(cdaParkingCrAndDrs.getCreatedOn());
                    cdaParkingTransResponse.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDrs.getAmountType()));
                    cdaParkingTransResponse.setAuthGroupId(cdaParkingCrAndDrs.getAuthGroupId());
                    cdaParkingTransResponse.setUnitId(cdaParkingCrAndDrs.getUnitId());

                    subHeadData.put(cdaName.getCdaName(), cdaParkingTransResponse);
                }

            }
        }

        mainResponse.setSubHeadData(subHeadData);
        mainResponse.setTotalExpWithAllocation(totalExpWithAllocation);
        return ResponseUtils.createSuccessResponse(mainResponse, new TypeReference<CdaAndAllocationDataResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<ReabseCdaParkingResponse> getCheckExpForUnit(CDARequestReBase cdaRequest) {
        ReabseCdaParkingResponse mainResponse = new ReabseCdaParkingResponse();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE CDA PARKING");
        }

        if (cdaRequest.getBudgetHeadId() == null || cdaRequest.getBudgetHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }

        if (cdaRequest.getFinancialYearId() == null || cdaRequest.getFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
        }

        if (cdaRequest.getAllocationTypeId() == null || cdaRequest.getAllocationTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID CAN NOT BE BLANK");
        }

        if (cdaRequest.getAmountType() == null || cdaRequest.getAmountType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID CAN NOT BE BLANK");
        }

        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaRequest.getAmountType());
        if (amountUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT UNIT ID");
        }

        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(cdaRequest.getFinancialYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        AllocationType allocationType = allocationRepository.findByAllocTypeId(cdaRequest.getAllocationTypeId());
        if (allocationType == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
        }


        HashMap<String, CdaParkingTransSubResponse> subHeadData = new LinkedHashMap<>();


        List<ContigentBill> contigentBills = contigentBillRepository.findByFinYearAndBudgetHeadIDAndIsUpdateAndIsFlagAndCbUnitId(cdaRequest.getFinancialYearId(), cdaRequest.getBudgetHeadId(), "0", "0", hrData.getUnitId());

        double totalBill = 0;
        for (Integer i = 0; i < contigentBills.size(); i++) {
            ContigentBill contigentBill = contigentBills.get(i);
            List<CdaParkingCrAndDr> cdaParkingCrAndDrsList = parkingCrAndDrRepository.findByTransactionIdAndIsFlag(contigentBill.getCbId(), "0");


            for (Integer v = 0; v < cdaParkingCrAndDrsList.size(); v++) {
                CdaParkingCrAndDr cdaParkingCrAndDrs = cdaParkingCrAndDrsList.get(v);
                CdaParking cdaName = cdaParkingRepository.findByGinNo(cdaParkingCrAndDrs.getGinNo());
                if (subHeadData.containsKey(cdaName.getCdaName())) {
                    CdaParkingTransSubResponse cdaParkingTransSubResponses = subHeadData.get(cdaParkingCrAndDrs.getGinNo());

                    double totalBillAmount = totalBill + Double.parseDouble(contigentBill.getCbAmount()) / amountUnit.getAmount();

                    totalBill = totalBill + totalBillAmount / amountUnit.getAmount();
                    cdaParkingTransSubResponses.setTotalParkingAmount(ConverterUtils.addDecimalPoint(totalBill + ""));
                    cdaParkingTransSubResponses.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(totalBill + ""));
                    subHeadData.put(cdaName.getCdaName(), cdaParkingTransSubResponses);


                } else {
                    double totalBillAmount = Double.parseDouble(contigentBill.getCbAmount());

                    totalBill = totalBillAmount / amountUnit.getAmount();

                    CdaParkingTransSubResponse cdaParkingTransResponse = new CdaParkingTransSubResponse();

                    cdaParkingTransResponse.setFinYearId(budgetFinancialYearRepository.findBySerialNo(cdaParkingCrAndDrs.getFinYearId()));
                    cdaParkingTransResponse.setBudgetHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(cdaParkingCrAndDrs.getBudgetHeadId()));
                    cdaParkingTransResponse.setGinNo(cdaName);
                    cdaParkingTransResponse.setAllocationType(allocationRepository.findByAllocTypeId(cdaParkingCrAndDrs.getAllocTypeId()));

                    cdaParkingTransResponse.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(totalBill + ""));
                    cdaParkingTransResponse.setTotalParkingAmount(ConverterUtils.addDecimalPoint(totalBill + ""));

                    cdaParkingTransResponse.setUpdatedOn(cdaParkingCrAndDrs.getUpdatedOn());
                    cdaParkingTransResponse.setTransactionId(cdaParkingCrAndDrs.getTransactionId());
                    cdaParkingTransResponse.setCreatedOn(cdaParkingCrAndDrs.getCreatedOn());
                    cdaParkingTransResponse.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDrs.getAmountType()));
                    cdaParkingTransResponse.setAuthGroupId(cdaParkingCrAndDrs.getAuthGroupId());
                    cdaParkingTransResponse.setUnitId(cdaParkingCrAndDrs.getUnitId());

                    subHeadData.put(cdaName.getCdaName(), cdaParkingTransResponse);
                }

            }
        }

        mainResponse.setSubHeadData(subHeadData);
        return ResponseUtils.createSuccessResponse(mainResponse, new TypeReference<ReabseCdaParkingResponse>() {
        });
    }


    public boolean checkDuplicateData(List<CdaSubRequest> cdaRequest) {
        Set<String> s = new HashSet<String>();

        for (CdaSubRequest name : cdaRequest) {
            if (s.add(name.getGinNo()) == false)
                return true;
        }
        return false;
    }


}

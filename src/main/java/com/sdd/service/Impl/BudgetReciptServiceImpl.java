package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.*;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.request.BudgetReciptSaveRequest;
import com.sdd.request.BudgetReciptUpdateRequest;
import com.sdd.request.CdaSubRequest;
import com.sdd.response.*;
import com.sdd.service.BudgetReciptService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BudgetReciptServiceImpl implements BudgetReciptService {


    @Autowired
    CdaParkingCrAndDrRepository parkingCrAndDrRepository;

    @Autowired
    BudgetAllocationRepository budgetAllocationRepository;

    @Autowired
    CdaParkingRepository cdaParkingRepository;

    @Autowired
    AmountUnitRepository amountUnitRepository;

    @Autowired
    MangeInboxOutBoxRepository mangeInboxOutBoxRepository;

    @Autowired
    HrDataRepository hrDataRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    AllocationRepository allocationRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    HeaderUtils headerUtils;


    @Autowired
    BudgetFinancialYearRepository budgetFinancialYearRepository;

    @Autowired
    SubHeadRepository subHeadRepository;


    @Autowired
    CdaParkingTransRepository cdaParkingTransRepository;


    @Autowired
    FileUploadRepository fileUploadRepository;

    @Autowired
    CgUnitRepository cgUnitRepository;

    @Autowired
    BudgetAllocationDetailsRepository budgetAllocationDetailsRepository;


    @Autowired
    CurrentStateRepository currentStateRepository;


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetReciptListResponse> budgetRecipetSave(BudgetReciptSaveRequest budgetReciptSaveRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET RECEIPT");
        } else {
            if (hrData.getUnitId().contains(HelperUtils.HEADUNITID)) {
            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET RECEIPT");
            }
        }


        if (budgetReciptSaveRequest.getSubHeadType() == null || budgetReciptSaveRequest.getSubHeadType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD TYPE CAN NOT BE BLANK");
        }

        if (budgetReciptSaveRequest.getMajorMinerHead() == null || budgetReciptSaveRequest.getMajorMinerHead().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "MAJOR OR MINER HEAD CAN NOT BE BLANK");
        }

        if (budgetReciptSaveRequest.getBudgetFinancialYearId() == null || budgetReciptSaveRequest.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
        }

        if (budgetReciptSaveRequest.getAmountTypeId() == null || budgetReciptSaveRequest.getAmountTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE CAN NOT BE BLANK");
        }

        if (budgetReciptSaveRequest.getAllocationType() == null || budgetReciptSaveRequest.getAllocationType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE CAN NOT BE BLANK");
        }


        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetReciptSaveRequest.getBudgetFinancialYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        if (budgetReciptSaveRequest.getAuthListData() == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AUTHORITY DATA");
        }

        if (budgetReciptSaveRequest.getAuthListData().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AUTHORITY DATA ");
        }


        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetReciptSaveRequest.getAmountTypeId());
        if (amountUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
        }


        for (int j = 0; j < budgetReciptSaveRequest.getAuthListData().size(); j++) {

            FileUpload fileUpload = fileUploadRepository.findByUploadID(budgetReciptSaveRequest.getAuthListData().get(j).getAuthDocId());
            if (fileUpload == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DOCUMENT ID ");
            }
            if (budgetReciptSaveRequest.getAuthListData().get(j).getAuthDocId() == null || budgetReciptSaveRequest.getAuthListData().get(j).getAuthDocId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT ID CAN NOT BE BLANK");
            }
            if (budgetReciptSaveRequest.getAuthListData().get(j).getAuthDate() == null || budgetReciptSaveRequest.getAuthListData().get(j).getAuthDate().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY DATE  CAN NOT BE BLANK");
            }

            CgUnit getAuthUnitId = cgUnitRepository.findByUnit(budgetReciptSaveRequest.getAuthListData().get(j).getAuthUnitId());
            if (getAuthUnitId == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTHORITY UNIT ID");
            }
            ConverterUtils.checkDateIsvalidOrNor(budgetReciptSaveRequest.getAuthListData().get(j).getAuthDate());

        }


        for (int j = 0; j < budgetReciptSaveRequest.getReceiptSubRequests().size(); j++) {

            if (budgetReciptSaveRequest.getReceiptSubRequests().get(j).getBudgetHeadId() == null || budgetReciptSaveRequest.getReceiptSubRequests().get(j).getBudgetHeadId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET HEAD ID NOT BE BLANK");
            }

            if (budgetReciptSaveRequest.getReceiptSubRequests().get(j).getAllocationAmount() == null || budgetReciptSaveRequest.getReceiptSubRequests().get(j).getAllocationAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT CAN NOT BE BLANK");
            }

            BudgetHead subHeadData = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getBudgetHeadId());
            if (subHeadData == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID BUDGET HEAD ID ");
            }
        }


        if (budgetReciptSaveRequest.getReceiptSubRequests().size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DATA.PLEASE CHECK");
        }


        CgUnit budgetHeadUit = cgUnitRepository.findByUnit("001321");
        if (budgetHeadUit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO BUDGET UNIT ID");
        }


        AllocationType saveAllocationTypeMain = allocationRepository.findByAllocDescAndIsFlagAndFinYearAndMajorMinerHeadAndSubHeadType(budgetReciptSaveRequest.getAllocationType(), "1", budgetReciptSaveRequest.getBudgetFinancialYearId(), budgetReciptSaveRequest.getMajorMinerHead(), budgetReciptSaveRequest.getSubHeadType());
        if (saveAllocationTypeMain != null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION NAME ALREADY USE.PLEASE CHANGE");
        }

        AllocationType saveAllocationType = allocationRepository.findByAllocDescAndIsFlagAndFinYear(budgetReciptSaveRequest.getAllocationType(), "1", budgetReciptSaveRequest.getBudgetFinancialYearId());

        if (saveAllocationType == null) {

            List<AllocationType> allocationTypes = allocationRepository.findByIsFlag("1");
            for (AllocationType allocationTypeData : allocationTypes) {
                allocationTypeData.setIsFlag("0");
                allocationRepository.save(allocationTypeData);
            }

            AllocationType allocationType = new AllocationType();
            allocationType.setAllocTypeId(HelperUtils.getAllocationTypeId());
            allocationType.setAllocType(budgetReciptSaveRequest.getAllocationType());
            allocationType.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            allocationType.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            allocationType.setFinYear(budgetReciptSaveRequest.getBudgetFinancialYearId());
            allocationType.setAllocDesc(budgetReciptSaveRequest.getAllocationType());
            allocationType.setRemarks(budgetReciptSaveRequest.getAllocationType());
            allocationType.setSubHeadType(budgetReciptSaveRequest.getSubHeadType());
            allocationType.setMajorMinerHead(budgetReciptSaveRequest.getMajorMinerHead());
            allocationType.setIsFlag("1");
            saveAllocationType = allocationRepository.save(allocationType);
        }

        String authGroupId = HelperUtils.getAuthorityGroupId();

        for (int j = 0; j < budgetReciptSaveRequest.getAuthListData().size(); j++) {

            Authority authoritySaveData = new Authority();
            authoritySaveData.setAuthorityId(HelperUtils.getAuthorityId());
            authoritySaveData.setAuthority(budgetReciptSaveRequest.getAuthListData().get(j).getAuthority());
            authoritySaveData.setAuthDate(ConverterUtils.convertDateTotimeStamp(budgetReciptSaveRequest.getAuthListData().get(j).getAuthDate()));
            authoritySaveData.setAuthUnit(budgetReciptSaveRequest.getAuthListData().get(j).getAuthUnitId());
            authoritySaveData.setDocId(budgetReciptSaveRequest.getAuthListData().get(j).getAuthDocId());
            authoritySaveData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            authoritySaveData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            authoritySaveData.setAuthGroupId(authGroupId);
            authoritySaveData.setRemarks(budgetReciptSaveRequest.getAuthListData().get(j).getRemark());
            authorityRepository.save(authoritySaveData);

        }

        double amount = 0;
        String allocationTypeData = "";
        for (int j = 0; j < budgetReciptSaveRequest.getReceiptSubRequests().size(); j++) {

            BudgetHead subHeadData = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getBudgetHeadId());

            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();

            budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getAllocationAmount()));
            budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAllocTypeId(saveAllocationType.getAllocTypeId());
            allocationTypeData = saveAllocationType.getAllocTypeId();
            budgetAllocationDetails.setFinYear(budgetReciptSaveRequest.getBudgetFinancialYearId());
            budgetAllocationDetails.setFromUnit("000000");
            budgetAllocationDetails.setToUnit(budgetHeadUit.getUnit());
            budgetAllocationDetails.setSubHead(subHeadData.getBudgetCodeId());
            budgetAllocationDetails.setStatus("Approved");
            budgetAllocationDetails.setIsTYpe("M");
            budgetAllocationDetails.setUnallocatedAmount("0");
            budgetAllocationDetails.setIsBudgetRevision("0");
            budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAuthGroupId(authGroupId);
            budgetAllocationDetails.setRemarks("");
            budgetAllocationDetails.setPurposeCode("");
            budgetAllocationDetails.setRevisedAmount("0.0000");
            budgetAllocationDetails.setIsDelete("0");
            budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
            budgetAllocationDetails.setUserId(hrData.getPid());
            budgetAllocationDetails.setAmountType(amountUnit.getAmountTypeId());
            budgetAllocationDetails.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
            amount = amount + Double.parseDouble(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getAllocationAmount());
            budgetAllocationDetailsRepository.save(budgetAllocationDetails);

        }

        List<CurrntStateType> stateList = currentStateRepository.findByIsFlag("1");
        for (CurrntStateType currntStateType : stateList) {
            currntStateType.setIsFlag("0");
            currentStateRepository.save(currntStateType);
        }


        CurrntStateType currntStateType = new CurrntStateType();
        currntStateType.setCurrentStateId(HelperUtils.getStateId());
        currntStateType.setStateId(budgetFinancialYear.getSerialNo());
        currntStateType.setStateName(budgetFinancialYear.getFinYear());
        currntStateType.setIsFlag("1");
        currntStateType.setType("FINYEAR");
        currntStateType.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        currntStateType.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        currentStateRepository.save(currntStateType);


        for (int j = 0; j < budgetReciptSaveRequest.getReceiptSubRequests().size(); j++) {

            BudgetAllocation budgetAllocation = new BudgetAllocation();
            budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
            budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
            budgetAllocation.setFinYear(budgetReciptSaveRequest.getBudgetFinancialYearId());
            budgetAllocation.setToUnit(budgetHeadUit.getUnit());
            budgetAllocation.setSubHead(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getBudgetHeadId());
            budgetAllocation.setAllocationTypeId(saveAllocationType.getAllocTypeId());
            budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getAllocationAmount()));
            budgetAllocation.setUnallocatedAmount("0");
            budgetAllocation.setIsFlag("0");
            budgetAllocation.setUnallocatedAmount("0");
            budgetAllocation.setIsTYpe("R");
            budgetAllocation.setIsBudgetRevision("0");
            budgetAllocation.setRevisedAmount("0");
            budgetAllocation.setUserId(hrData.getPid());
            budgetAllocation.setStatus("Approved");
            budgetAllocation.setAuthGroupId(authGroupId);
            budgetAllocation.setAmountType(amountUnit.getAmountTypeId());
            budgetAllocationRepository.save(budgetAllocation);

        }


        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
        mangeInboxOutbox.setIsRebase("0");
        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Budget Receipt");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(budgetHeadUit.getUnit());
        mangeInboxOutbox.setGroupId(authGroupId);
        mangeInboxOutbox.setFromUnit("000000");
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setApproverpId(hrData.getPid());
        mangeInboxOutbox.setStatus("Fully Approved");
        mangeInboxOutbox.setState("CR");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setAllocationType(allocationTypeData);
        mangeInboxOutbox.setIsFlag("0");
        mangeInboxOutbox.setType("Mod Receipt");
        mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint(amount + ""));
        mangeInboxOutbox.setIsBgcg("BR");
        mangeInboxOutbox.setIsRevision(0);
        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        //Send Return Data

        List<AuthorityTableResponse> authorityTableList = new ArrayList<AuthorityTableResponse>();
        BudgetReciptListResponse budgetAllocationResponse = new BudgetReciptListResponse();
        List<BudgetReciptListSubResponse> budgetAllocationList = new ArrayList<BudgetReciptListSubResponse>();
        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByFromUnitAndIsDeleteAndIsBudgetRevision("000000", "0", "0");
        for (BudgetAllocationDetails budgetAllocationSubReport : budgetAllocations) {

            BudgetReciptListSubResponse budgetAllocationReport = new BudgetReciptListSubResponse();
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setAllocationId(budgetAllocationSubReport.getAllocationId());

            budgetAllocationReport.setTransactionId(budgetAllocationSubReport.getTransactionId());
            budgetAllocationReport.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
            budgetAllocationReport.setStatus(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setPurposeCode(budgetAllocationSubReport.getPurposeCode());
            budgetAllocationReport.setRemarks(budgetAllocationSubReport.getRemarks());
            budgetAllocationReport.setRefTransactionId(budgetAllocationSubReport.getRefTransactionId());
            budgetAllocationReport.setUserId(budgetAllocationSubReport.getUserId());
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            budgetAllocationReport.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());

            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
            budgetAllocationReport.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocTypeId()));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));

            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getTransactionId(), "0");
            if (!cdaParkingList.isEmpty()) {
                budgetAllocationReport.setIsCdaParked("1");
            } else {
                budgetAllocationReport.setIsCdaParked("0");
            }

            List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(budgetAllocationSubReport.getAuthGroupId());

            if (!authoritiesList.isEmpty()) {

                for (Authority authority : authoritiesList) {
                    AuthorityTableResponse authorityTableResponse = new AuthorityTableResponse();
                    BeanUtils.copyProperties(authority, authorityTableResponse);
                    authorityTableList.clear();
                    authorityTableList.add(authorityTableResponse);

                    FileUpload fileUploadData = fileUploadRepository.findByUploadID(authority.getDocId());
                    authorityTableResponse.setDocId(fileUploadData);
                }
            }

            budgetAllocationReport.setAuthList(authorityTableList);
            budgetAllocationList.add(budgetAllocationReport);

        }

        budgetAllocationResponse.setBudgetResponseist(budgetAllocationList);


        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<BudgetReciptListResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<ContingentSaveResponse> updateRecipetSave(BudgetReciptUpdateRequest budgetReciptSaveRequest) {
        ContingentSaveResponse budgetReciptResponse = new ContingentSaveResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET RECEIPT");
        } else {
            if (!hrData.getUnitId().contains(HelperUtils.HEADUNITID)) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET RECEIPT");
            }
        }


        if (budgetReciptSaveRequest.getBudgetFinancialYearId() == null || budgetReciptSaveRequest.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
        }


        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetReciptSaveRequest.getBudgetFinancialYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }


        if (budgetReciptSaveRequest.getBudgetHeadId() == null || budgetReciptSaveRequest.getBudgetHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET HEAD ID NOT BE BLANK");
        }


        if (budgetReciptSaveRequest.getAllocationAmount() == null || budgetReciptSaveRequest.getAllocationAmount().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT CAN NOT BE BLANK");
        }


        BudgetHead subHeadData = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetReciptSaveRequest.getBudgetHeadId());
        if (subHeadData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID BUDGET HEAD ID ");
        }

        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");


        List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetReciptSaveRequest.getBudgetFinancialYearId(), budgetReciptSaveRequest.getBudgetHeadId(), "0", allocationType.get(0).getAllocTypeId(), hrData.getUnitId());
        if (!cdaParkingList.isEmpty()) {

            boolean data = checkDuplicateData(budgetReciptSaveRequest.getCdaRequest());
            if (data) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DUPLICATE CDA FOUND.PLEASE CHECK");
            }

            if (budgetReciptSaveRequest.getAlterAmount() == null || budgetReciptSaveRequest.getAlterAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALTER AMOUNT NOT BE BLANK");
            }

            if (Double.parseDouble(budgetReciptSaveRequest.getAlterAmount()) == 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT NOT CHANGED");
            }


            for (int i = 0; i < budgetReciptSaveRequest.getCdaRequest().size(); i++) {

                if (budgetReciptSaveRequest.getCdaRequest().get(i).getBudgetFinancialYearId() == null || budgetReciptSaveRequest.getCdaRequest().get(i).getBudgetFinancialYearId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR ID NOT BE BLANK");
                }

                if (budgetReciptSaveRequest.getCdaRequest().get(i).getAllocationTypeID() == null || budgetReciptSaveRequest.getCdaRequest().get(i).getAllocationTypeID().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID CAN NOT BE BLANK");
                }

                if (budgetReciptSaveRequest.getCdaRequest().get(i).getAvailableParkingAmount() == null || budgetReciptSaveRequest.getCdaRequest().get(i).getAvailableParkingAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AVAILABLE AMOUNT CAN NOT BE BLANK");
                }

                if (budgetReciptSaveRequest.getCdaRequest().get(i).getGinNo() == null || budgetReciptSaveRequest.getCdaRequest().get(i).getGinNo().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GIN NUMBER CAN NOT BE BLANK");
                }

                if (budgetReciptSaveRequest.getCdaRequest().get(i).getAuthGroupId() == null || budgetReciptSaveRequest.getCdaRequest().get(i).getAuthGroupId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTH GROUP ID CAN NOT BE BLANK");
                }

                if (budgetReciptSaveRequest.getCdaRequest().get(i).getTransactionId() == null || budgetReciptSaveRequest.getCdaRequest().get(i).getTransactionId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TRANSACTION ID CAN NOT BE BLANK");
                }

                if (budgetReciptSaveRequest.getCdaRequest().get(i).getAmountTypeId() == null || budgetReciptSaveRequest.getCdaRequest().get(i).getAmountTypeId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID CAN NOT BE BLANK   key:-amountTypeId");
                }

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetReciptSaveRequest.getCdaRequest().get(i).getAmountTypeId());
                if (amountUnit == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
                }

                CdaParking ginNumber = cdaParkingRepository.findByGinNo(budgetReciptSaveRequest.getCdaRequest().get(i).getGinNo());
                if (ginNumber == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID GIN NUMBER.");
                }

                BudgetFinancialYear budgetFinancialYearData = budgetFinancialYearRepository.findBySerialNo(budgetReciptSaveRequest.getCdaRequest().get(i).getBudgetFinancialYearId());
                if (budgetFinancialYearData == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
                }

                AllocationType cdaAllocationType = allocationRepository.findByAllocTypeId(budgetReciptSaveRequest.getCdaRequest().get(i).getAllocationTypeID());
                if (cdaAllocationType == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
                }
            }
        }

        double rememningAmount = 0;
        for (CdaParkingTrans cdaParkingTrans : cdaParkingList) {
            AmountUnit amountUnitData = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
            rememningAmount = rememningAmount + (Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * amountUnitData.getAmount());
        }

        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetReciptSaveRequest.getAmountTypeId());
        if (amountUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
        }

        if (budgetReciptSaveRequest.getAlterAmount() != null) {
            double alterAmount = Double.parseDouble(budgetReciptSaveRequest.getAlterAmount()) * amountUnit.getAmount();
            if (alterAmount+rememningAmount < 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NOT HAVE ENOUGH BALANCE TO UPDATE CDA.");
            }
        }


        List<BudgetAllocation> budgetAllocationData = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(HelperUtils.HEADUNITID, budgetReciptSaveRequest.getBudgetFinancialYearId(), budgetReciptSaveRequest.getBudgetHeadId(), budgetReciptSaveRequest.getAllocationTypeId(), "Approved", "0", "0");
        if (budgetAllocationData.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND");
        }


        List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(HelperUtils.HEADUNITID, budgetReciptSaveRequest.getBudgetFinancialYearId(), budgetReciptSaveRequest.getBudgetHeadId(), budgetReciptSaveRequest.getAllocationTypeId(), "Approved", "0", "0");

        for (BudgetAllocationDetails budgetAllocationDetails : budgetAllocationDetailsList) {
            budgetAllocationDetails.setIsDelete("1");
            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(budgetAllocationDetails);

            BudgetAllocationDetails newData = new BudgetAllocationDetails();
            BeanUtils.copyProperties(budgetAllocationDetails, newData);
            newData.setTransactionId(HelperUtils.getBudgetAllocationTypeId());
            newData.setAllocationAmount(budgetReciptSaveRequest.getAllocationAmount());
            newData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            newData.setIsDelete("0");
            newData.setIsTYpe("Receipt Alter");
            newData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(newData);
        }


        for (BudgetAllocation budgetAllocationDatum : budgetAllocationData) {
            budgetAllocationDatum.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDatum.setIsFlag("1");
            budgetAllocationRepository.save(budgetAllocationDatum);

            BudgetAllocation newData = new BudgetAllocation();
            BeanUtils.copyProperties(budgetAllocationDatum, newData);
            newData.setIsFlag("0");
            newData.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
            newData.setAllocationAmount(budgetReciptSaveRequest.getAllocationAmount());
            newData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            newData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            newData.setIsTYpe("Receipt Alter");
            budgetAllocationRepository.save(newData);
        }


        List<CdaParkingCrAndDr> cdaCrDrTransData11 = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(budgetReciptSaveRequest.getBudgetFinancialYearId(), budgetReciptSaveRequest.getBudgetHeadId(), "0", budgetReciptSaveRequest.getAllocationTypeId(), hrData.getUnitId(), 0);
        for (CdaParkingCrAndDr cdaParkingTrans : cdaCrDrTransData11) {
            cdaParkingTrans.setIsFlag("1");
            cdaParkingTrans.setRemark("Receipt Alter");
            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            parkingCrAndDrRepository.save(cdaParkingTrans);
        }


        for (CdaParkingTrans cdaParkingTrans : cdaParkingList) {
            cdaParkingTrans.setIsFlag("1");
            cdaParkingTrans.setRemarks("Receipt Alter");
            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingTransRepository.save(cdaParkingTrans);
        }


        if (budgetReciptSaveRequest.getCdaRequest() != null) {
            for (int i = 0; i < budgetReciptSaveRequest.getCdaRequest().size(); i++) {

                CdaParkingTrans cdaParkingTrans = new CdaParkingTrans();

                cdaParkingTrans.setCdaParkingId(HelperUtils.getCdaId());
                cdaParkingTrans.setFinYearId(budgetReciptSaveRequest.getCdaRequest().get(i).getBudgetFinancialYearId());
                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getCdaRequest().get(i).getAvailableParkingAmount()));
                cdaParkingTrans.setBudgetHeadId(budgetReciptSaveRequest.getCdaRequest().get(i).getBudgetHeadId());
                cdaParkingTrans.setRemarks(budgetReciptSaveRequest.getCdaRequest().get(i).getRemark());
                cdaParkingTrans.setGinNo(budgetReciptSaveRequest.getCdaRequest().get(i).getGinNo());
                cdaParkingTrans.setUnitId(hrData.getUnitId());
                cdaParkingTrans.setIsFlag("0");
                cdaParkingTrans.setAmountType(budgetReciptSaveRequest.getCdaRequest().get(i).getAmountTypeId());
                cdaParkingTrans.setTransactionId(budgetReciptSaveRequest.getCdaRequest().get(i).getTransactionId());
                cdaParkingTrans.setAllocTypeId(budgetReciptSaveRequest.getCdaRequest().get(i).getAllocationTypeID());
                cdaParkingTrans.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingTrans.setAuthGroupId(budgetReciptSaveRequest.getCdaRequest().get(i).getAuthGroupId());
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
                cdaParkingCrAndDr.setTransactionId(saveCdaData.getTransactionId());
                cdaParkingCrAndDr.setAmountType(saveCdaData.getAmountType());
                cdaParkingCrAndDr.setIsRevision(0);
                parkingCrAndDrRepository.save(cdaParkingCrAndDr);
            }
        }


        List<AuthorityTableResponse> authorityTableList = new ArrayList<AuthorityTableResponse>();
        BudgetReciptListResponse budgetAllocationResponse = new BudgetReciptListResponse();
        List<BudgetReciptListSubResponse> budgetAllocationList = new ArrayList<BudgetReciptListSubResponse>();


        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByFromUnitAndIsDeleteAndIsBudgetRevision("000000", "0", "0");
        for (BudgetAllocationDetails budgetAllocationSubReport : budgetAllocations) {

            BudgetReciptListSubResponse budgetAllocationReport = new BudgetReciptListSubResponse();
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setAllocationId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setTransactionId(budgetAllocationSubReport.getTransactionId());
            budgetAllocationReport.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
            budgetAllocationReport.setStatus(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setPurposeCode(budgetAllocationSubReport.getPurposeCode());
            budgetAllocationReport.setRemarks(budgetAllocationSubReport.getRemarks());
            budgetAllocationReport.setRefTransactionId(budgetAllocationSubReport.getRefTransactionId());
            budgetAllocationReport.setUserId(budgetAllocationSubReport.getUserId());
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            budgetAllocationReport.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());

            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
            budgetAllocationReport.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocTypeId()));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));

            List<CdaParkingTrans> cdaParkingListData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", budgetAllocationSubReport.getAllocTypeId(), hrData.getUnitId());
            if (!cdaParkingListData.isEmpty()) {
                budgetAllocationReport.setIsCdaParked("1");
            } else {
                budgetAllocationReport.setIsCdaParked("0");
            }
            budgetAllocationReport.setCdaParkingListData(cdaParkingListData);

            List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            if (!authoritiesList.isEmpty()) {

                for (Authority authority : authoritiesList) {
                    AuthorityTableResponse authorityTableResponse = new AuthorityTableResponse();
                    BeanUtils.copyProperties(authority, authorityTableResponse);
                    authorityTableList.clear();
                    authorityTableList.add(authorityTableResponse);

                    FileUpload fileUploadData = fileUploadRepository.findByUploadID(authority.getDocId());
                    authorityTableResponse.setDocId(fileUploadData);
                }
            }

            budgetAllocationReport.setAuthList(authorityTableList);
            budgetAllocationList.add(budgetAllocationReport);
        }

        budgetAllocationResponse.setBudgetResponseist(budgetAllocationList);
        budgetReciptResponse.setMsg("DATA UPDATE SUCCESSFULLY");

        return ResponseUtils.createSuccessResponse(budgetReciptResponse, new TypeReference<ContingentSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetReciptListResponse> getBudgetRecipt() {


        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        List<AuthorityTableResponse> authorityTableList = new ArrayList<AuthorityTableResponse>();
        BudgetReciptListResponse budgetAllocationResponse = new BudgetReciptListResponse();
        List<BudgetReciptListSubResponse> budgetAllocationList = new ArrayList<BudgetReciptListSubResponse>();
        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByFromUnitAndIsDeleteAndIsBudgetRevision("000000", "0", "0");


        for (BudgetAllocationDetails budgetAllocationSubReport : budgetAllocations) {

            BudgetReciptListSubResponse budgetAllocationReport = new BudgetReciptListSubResponse();
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setAllocationId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setTransactionId(budgetAllocationSubReport.getTransactionId());
            budgetAllocationReport.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
            budgetAllocationReport.setStatus(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setPurposeCode(budgetAllocationSubReport.getPurposeCode());
            budgetAllocationReport.setRemarks(budgetAllocationSubReport.getRemarks());
            budgetAllocationReport.setRefTransactionId(budgetAllocationSubReport.getRefTransactionId());
            budgetAllocationReport.setUserId(budgetAllocationSubReport.getUserId());
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            budgetAllocationReport.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());
            budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());
            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
            budgetAllocationReport.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocTypeId()));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));


//            List<CdaParkingTrans> cdaParkingListData = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getTransactionId(), "0");
//            if (!cdaParkingListData.isEmpty()) {
//                budgetAllocationReport.setIsCdaParked("1");
//            } else {
//                budgetAllocationReport.setIsCdaParked("0");
//            }

            List<CdaParkingTrans> cdaParkingListData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", budgetAllocationSubReport.getAllocTypeId(), hrData.getUnitId());
            if (!cdaParkingListData.isEmpty()) {
                budgetAllocationReport.setIsCdaParked("1");
            } else {
                budgetAllocationReport.setIsCdaParked("0");
            }
            budgetAllocationReport.setCdaParkingListData(cdaParkingListData);


            List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            if (!authoritiesList.isEmpty()) {

                for (Authority authority : authoritiesList) {
                    AuthorityTableResponse authorityTableResponse = new AuthorityTableResponse();
                    BeanUtils.copyProperties(authority, authorityTableResponse);
                    authorityTableList.clear();
                    authorityTableList.add(authorityTableResponse);

                    FileUpload fileUploadData = fileUploadRepository.findByUploadID(authority.getDocId());
                    authorityTableResponse.setDocId(fileUploadData);
                }
            }

            budgetAllocationReport.setAuthList(authorityTableList);
            budgetAllocationList.add(budgetAllocationReport);

        }

        Collections.sort(budgetAllocationList, new Comparator<BudgetReciptListSubResponse>() {
            public int compare(BudgetReciptListSubResponse v1, BudgetReciptListSubResponse v2) {
                return v1.getSubHead().getSerialNumber().compareTo(v2.getSubHead().getSerialNumber());
            }
        });

        budgetAllocationResponse.setBudgetResponseist(budgetAllocationList);


        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<BudgetReciptListResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<AllBudgetRevisionResponse> getBudgetReciptFilter(BudgetReciptSaveRequest
                                                                                budgetReciptSaveRequest) {
        AllBudgetRevisionResponse budgetAllocationResponse = new AllBudgetRevisionResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION LOGIN AGAIN.");
        }

        if (budgetReciptSaveRequest.getBudgetFinancialYearId() == null || budgetReciptSaveRequest.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
        }


        if (budgetReciptSaveRequest.getMajorHeadId() == null || budgetReciptSaveRequest.getMajorHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "MAJOR HEAD ID CAN NOT BE BLANK");
        }


        List<AllocationType> allocationTypes = allocationRepository.findByFinYear(budgetReciptSaveRequest.getBudgetFinancialYearId());
        List<BudgetRecioptDemoResponse> budgetListData = new ArrayList<BudgetRecioptDemoResponse>();
        String authgroupId = "";
        if (allocationTypes.isEmpty()) {
            BudgetRecioptDemoResponse budgetMainData = new BudgetRecioptDemoResponse();
            List<BudgetHead> majorData = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(budgetReciptSaveRequest.getMajorHeadId(), budgetReciptSaveRequest.getBudgetHeadType());
            List<BudgetRecioptSubDemoResponse> budgetData = new ArrayList<BudgetRecioptSubDemoResponse>();
            for (BudgetHead majorDatum : majorData) {

                BudgetRecioptSubDemoResponse budgetRecioptDemoResponse = new BudgetRecioptSubDemoResponse();
                budgetRecioptDemoResponse.setBudgetHead(majorDatum);
                budgetData.add(budgetRecioptDemoResponse);
            }
            budgetMainData.setData(budgetData);
            budgetListData.add(budgetMainData);
        } else {

            for (AllocationType allocationType : allocationTypes) {
                List<BudgetHead> majorData = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(budgetReciptSaveRequest.getMajorHeadId(), budgetReciptSaveRequest.getBudgetHeadType());
                List<BudgetRecioptSubDemoResponse> budgetData = new ArrayList<BudgetRecioptSubDemoResponse>();
                BudgetRecioptDemoResponse budgetMainData = new BudgetRecioptDemoResponse();
                for (BudgetHead majorDatum : majorData) {
                    List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByFromUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision("000000", budgetReciptSaveRequest.getBudgetFinancialYearId(), majorDatum.getBudgetCodeId(), allocationType.getAllocTypeId(), "Approved", "0", "0");

                    if (budgetAllocations.size() <= 0) {
                        BudgetRecioptSubDemoResponse budgetRecioptDemoResponse = new BudgetRecioptSubDemoResponse();
                        budgetRecioptDemoResponse.setBudgetHead(majorDatum);
                        budgetRecioptDemoResponse.setAllocationType(allocationType);
                        budgetData.add(budgetRecioptDemoResponse);

                    } else {

                        BudgetHead budgetHead = subHeadRepository.findByBudgetCodeIdAndSubHeadTypeIdOrderBySerialNumberAsc(budgetAllocations.get(0).getSubHead(), budgetReciptSaveRequest.getBudgetHeadType());

                        BudgetRecioptSubDemoResponse budgetRecioptDemoResponse = new BudgetRecioptSubDemoResponse();
                        budgetRecioptDemoResponse.setBudgetHead(budgetHead);
                        authgroupId = budgetAllocations.get(0).getAuthGroupId();
                        budgetRecioptDemoResponse.setBudgetAllocations(budgetAllocations);
                        budgetRecioptDemoResponse.setAllocationType(allocationType);
                        budgetMainData.setAllocationType(allocationType);
                        budgetData.add(budgetRecioptDemoResponse);

                    }
                }

                budgetMainData.setData(budgetData);
                budgetListData.add(budgetMainData);
            }

        }


        List<BudgetRecioptDemoResponse> budgetMainDataResponse = new ArrayList<BudgetRecioptDemoResponse>();

        for (BudgetRecioptDemoResponse budgetListDatum : budgetListData) {
            BudgetRecioptDemoResponse budgetRecioptDemoResponse = new BudgetRecioptDemoResponse();
            budgetRecioptDemoResponse.setData(budgetListDatum.getData());
            budgetRecioptDemoResponse.setAllocationType(budgetListDatum.getAllocationType());
            budgetMainDataResponse.add(budgetRecioptDemoResponse);
        }


        List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(authgroupId);
        budgetAllocationResponse.setAuthList(authoritiesList);
        budgetAllocationResponse.setBudgetData(budgetMainDataResponse);


        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<AllBudgetRevisionResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<CgUnit> getModData() {

        CgUnit cgUnit = cgUnitRepository.findByUnit("000000");
        return ResponseUtils.createSuccessResponse(cgUnit, new TypeReference<CgUnit>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<CdaParking>> getAllCda() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET RECEIPT");
        }
        List<CdaParking> totalCdaParkingAmount = new ArrayList<>();


        CdaParking cdaParking = new CdaParking();
        cdaParking.setGinNo("112233");
        cdaParking.setCdaName("Unit Parking");

        CdaParking cdaParking1122 = new CdaParking();
        cdaParking1122.setGinNo("123456");
        cdaParking1122.setCdaName("ALL CDA");


        CdaParking cdaParking11 = new CdaParking();
        cdaParking11.setGinNo("112244");
        cdaParking11.setCdaName("Mumbai CDA");

        totalCdaParkingAmount.add(cdaParking);
        totalCdaParkingAmount.add(cdaParking1122);
        totalCdaParkingAmount.add(cdaParking11);

        List<CdaParking> allCda = cdaParkingRepository.findAll();
        for (CdaParking parking : allCda) {
            if (!(parking.getGinNo().equalsIgnoreCase("200201"))) {
                totalCdaParkingAmount.add(parking);
            }
        }
        return ResponseUtils.createSuccessResponse(totalCdaParkingAmount, new TypeReference<List<CdaParking>>() {
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

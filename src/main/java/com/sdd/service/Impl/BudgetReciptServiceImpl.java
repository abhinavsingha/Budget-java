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
import com.sdd.response.*;
import com.sdd.service.BudgetReciptService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BudgetReciptServiceImpl implements BudgetReciptService {


    @Autowired
    BudgetAllocationRepository budgetAllocationRepository;

    @Autowired
    private AmountUnitRepository amountUnitRepository;


    @Autowired
    MangeInboxOutBoxRepository mangeInboxOutBoxRepository;

    @Autowired
    private HrDataRepository hrDataRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    AllocationRepository allocationRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HeaderUtils headerUtils;


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


        if (budgetReciptSaveRequest.getBudgetFinancialYearId() == null || budgetReciptSaveRequest.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
        }

        if (budgetReciptSaveRequest.getAmountTypeId() == null || budgetReciptSaveRequest.getAmountTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID CAN NOT BE BLANK");
        }


        AllocationType allocationType = allocationRepository.findByAllocTypeId(budgetReciptSaveRequest.getAllocationTypeId());
        if (allocationType == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID ");
        }


        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetReciptSaveRequest.getBudgetFinancialYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        if (budgetReciptSaveRequest.getAuthListData() == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AUTHORITY DATA");
        }

        if (budgetReciptSaveRequest.getAuthListData().size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AUTHORITY DATA ");
        }


        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetReciptSaveRequest.getAmountTypeId());
        if (amountUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
        }


        for (Integer j = 0; j < budgetReciptSaveRequest.getAuthListData().size(); j++) {

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


        for (Integer j = 0; j < budgetReciptSaveRequest.getReceiptSubRequests().size(); j++) {

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

//        for (Integer c = 0; c < budgetReciptSaveRequest.getReceiptSubRequests().size(); c++) {
//
//            List<BudgetAllocationDetails> data = budgetAllocationDetailsRepository.findByFromUnitAndFinYearAndAllocTypeIdAndSubHead("000000", budgetReciptSaveRequest.getBudgetFinancialYearId(), budgetReciptSaveRequest.getAllocationTypeId(), budgetReciptSaveRequest.getReceiptSubRequests().get(c).getBudgetHeadId());
//
//            if (data.size() > 0) {
//                for (Integer j = 0; j < data.size(); j++) {
//                    budgetAllocationDetailsRepository.delete(data.get(j));
//                }
//            }
//        }

        for (Integer i = 0; i < budgetReciptSaveRequest.getReceiptSubRequests().size(); i++) {

            List<BudgetAllocation> budgetAloocation = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(hrData.getUnitId(), budgetReciptSaveRequest.getBudgetFinancialYearId(), budgetReciptSaveRequest.getReceiptSubRequests().get(i).getBudgetHeadId(), budgetReciptSaveRequest.getAllocationTypeId(), "Approved", "0");

            if (budgetAloocation.size() > 0) {
                BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(budgetReciptSaveRequest.getReceiptSubRequests().get(i).getBudgetHeadId());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DATA ALREADY SAVE FOR " + budgetHeadId.getSubHeadDescr());
            }

        }

        CgUnit budgetHeadUit = cgUnitRepository.findByUnit("001321");
        if (budgetHeadUit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO BUDGET UNIT ID");
        }


        String authGroupId = HelperUtils.getAuthorityGroupId();

        for (Integer j = 0; j < budgetReciptSaveRequest.getAuthListData().size(); j++) {

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
        for (Integer j = 0; j < budgetReciptSaveRequest.getReceiptSubRequests().size(); j++) {

            BudgetHead subHeadData = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getBudgetHeadId());

            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();

            budgetAllocationDetails.setAllocationId(HelperUtils.getAllocationId());
            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getAllocationAmount()));
            budgetAllocationDetails.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getAllocationAmount()));
            budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAllocTypeId(budgetReciptSaveRequest.getAllocationTypeId());
            allocationTypeData = budgetReciptSaveRequest.getAllocationTypeId();
            budgetAllocationDetails.setFinYear(budgetReciptSaveRequest.getBudgetFinancialYearId());
            budgetAllocationDetails.setFromUnit("000000");
            budgetAllocationDetails.setToUnit(budgetHeadUit.getUnit());
            budgetAllocationDetails.setSubHead(subHeadData.getBudgetCodeId());
            budgetAllocationDetails.setStatus("Approved");
            budgetAllocationDetails.setIsBudgetRevision("0");
            budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAuthGroupId(authGroupId);
            budgetAllocationDetails.setRemarks("");
            budgetAllocationDetails.setPurposeCode("");
            budgetAllocationDetails.setRevisedAmount("0.0000");
            budgetAllocationDetails.setIsDelete("0");

            budgetAllocationDetails.setRefTransactionId(HelperUtils.getAllocationId());
            budgetAllocationDetails.setUserId(hrData.getPid());
            budgetAllocationDetails.setAmountType(amountUnit.getAmountTypeId());
            budgetAllocationDetails.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
            amount = amount + Double.parseDouble(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getAllocationAmount());
            budgetAllocationDetailsRepository.save(budgetAllocationDetails);

        }

//        List<CurrntStateType> stateList = currentStateRepository.findAll();
//        for (Integer j = 0; j < stateList.size(); j++) {
//            CurrntStateType currntStateType = stateList.get(j);
//            currntStateType.setIsFlag("0");
//            currentStateRepository.save(currntStateType);
//        }
//
//
//        CurrntStateType currntStateType = new CurrntStateType();
//        currntStateType.setCurrentStateId(HelperUtils.getStateId());
//        currntStateType.setStateId(stateId);
//        currntStateType.setStateName(stateName);
//        currntStateType.setIsFlag("1");
//        currntStateType.setCreatedOn(HelperUtils.getCurrentTimeStamp());
//        currntStateType.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//        currentStateRepository.save(currntStateType);


        for (Integer j = 0; j < budgetReciptSaveRequest.getReceiptSubRequests().size(); j++) {

            BudgetAllocation budgetAllocation = new BudgetAllocation();
            budgetAllocation.setAllocationId(HelperUtils.getAllocationId());
            budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocation.setRefTransId(HelperUtils.getAllocationId());
            budgetAllocation.setFinYear(budgetReciptSaveRequest.getBudgetFinancialYearId());
            budgetAllocation.setToUnit(budgetHeadUit.getUnit());
            budgetAllocation.setSubHead(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getBudgetHeadId());
            budgetAllocation.setAllocationTypeId(budgetReciptSaveRequest.getAllocationTypeId());
            budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getAllocationAmount()));
            budgetAllocation.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getReceiptSubRequests().get(j).getAllocationAmount()));
            budgetAllocation.setUnallocatedAmount("0");
            budgetAllocation.setIsFlag("0");
            budgetAllocation.setIsBudgetRevision("0");
            budgetAllocation.setRevisedAmount("0");
            budgetAllocation.setUserId(hrData.getPid());
            budgetAllocation.setStatus("Approved");
            budgetAllocation.setAuthGroupId(authGroupId);
            budgetAllocation.setAmountType(amountUnit.getAmountTypeId());
            budgetAllocationRepository.save(budgetAllocation);

        }


        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

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
        mangeInboxOutbox.setStatus("Approved");
        mangeInboxOutbox.setState("CR");
        mangeInboxOutbox.setAllocationType(allocationTypeData);
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint(amount + ""));
        mangeInboxOutbox.setIsBgcg("BR");
        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        //Send Return Data

        List<AuthorityTableResponse> authorityTableList = new ArrayList<AuthorityTableResponse>();
        BudgetReciptListResponse budgetAllocationResponse = new BudgetReciptListResponse();
        List<BudgetReciptListSubResponse> budgetAllocationList = new ArrayList<BudgetReciptListSubResponse>();
        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByFromUnitAndIsDelete("000000", "0");
        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

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

            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocations.get(i).getTransactionId(), "0");;
            if (cdaParkingList.size() > 0) {
                budgetAllocationReport.setIsCdaParked("1");
            } else {
                budgetAllocationReport.setIsCdaParked("0");
            }

            List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(budgetAllocationSubReport.getAuthGroupId());

            if (authoritiesList.size() > 0) {

                for (Integer m = 0; m < authoritiesList.size(); m++) {
                    AuthorityTableResponse authorityTableResponse = new AuthorityTableResponse();
                    BeanUtils.copyProperties(authoritiesList.get(m), authorityTableResponse);
                    authorityTableList.clear();
                    authorityTableList.add(authorityTableResponse);

                    FileUpload fileUploadData = fileUploadRepository.findByUploadID(authoritiesList.get(m).getDocId());
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
    public ApiResponse<ContingentSaveResponse> updateRecipetSave(BudgetReciptUpdateRequest budgetReciptSaveRequest) {
        ContingentSaveResponse budgetReciptResponse = new ContingentSaveResponse();
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

        List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(budgetReciptSaveRequest.getBudgetFinancialYearId(), budgetReciptSaveRequest.getBudgetHeadId(), hrData.getUnitId(), "0");

        if (cdaParkingList.size() > 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA ALREADY PARKED.CAN NOT UPDATE AFTER CDA PARKING");
        }

        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetReciptSaveRequest.getAmountTypeId());
        if (amountUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
        }


        List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDelete(HelperUtils.HEADUNITID, budgetReciptSaveRequest.getBudgetFinancialYearId(), budgetReciptSaveRequest.getBudgetHeadId(), budgetReciptSaveRequest.getAllocationTypeId(), "Approved", "0");
        for (Integer m = 0; m < budgetAllocationDetailsList.size(); m++) {
            BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsList.get(m);
            budgetAllocationDetails.setIsDelete("1");
            budgetAllocationDetailsRepository.save(budgetAllocationDetails);
        }


        List<BudgetAllocation> budgetAllocationData = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(HelperUtils.HEADUNITID, budgetReciptSaveRequest.getBudgetFinancialYearId(), budgetReciptSaveRequest.getBudgetHeadId(), budgetReciptSaveRequest.getAllocationTypeId(), "Approved", "0");
        if (budgetAllocationData.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND");
        }


        String refTransID = "";
        String authGroupId = "";


        for (Integer m = 0; m < budgetAllocationData.size(); m++) {
            budgetAllocationData.get(m).setIsFlag("1");
            authGroupId = budgetAllocationData.get(m).getAuthGroupId();
            refTransID = budgetAllocationData.get(m).getRefTransId();
            budgetAllocationRepository.save(budgetAllocationData.get(m));
        }



        BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
        budgetAllocationDetails.setAllocationId(HelperUtils.getAllocationId());
        budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getAllocationAmount()));
        budgetAllocationDetails.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getAllocationAmount()));
        budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
        budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
        budgetAllocationDetails.setAllocTypeId(budgetReciptSaveRequest.getAllocationTypeId());
        budgetAllocationDetails.setFinYear(budgetReciptSaveRequest.getBudgetFinancialYearId());
        budgetAllocationDetails.setFromUnit("000000");
        budgetAllocationDetails.setToUnit(HelperUtils.HEADUNITID);
        budgetAllocationDetails.setSubHead(budgetReciptSaveRequest.getBudgetHeadId());
        budgetAllocationDetails.setStatus("Approved");
        budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        budgetAllocationDetails.setAuthGroupId(authGroupId);
        budgetAllocationDetails.setRemarks("");
        budgetAllocationDetails.setAmountType(amountUnit.getAmountTypeId());
        budgetAllocationDetails.setPurposeCode("");
        budgetAllocationDetails.setIsDelete("0");
        budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        budgetAllocationDetails.setRefTransactionId(refTransID);
        budgetAllocationDetails.setUserId(hrData.getPid());
        budgetAllocationDetails.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
        budgetAllocationDetails.setRevisedAmount("0");
        budgetAllocationDetailsRepository.save(budgetAllocationDetails);


        BudgetAllocation budgetAllocation = new BudgetAllocation();
        budgetAllocation.setAllocationId(HelperUtils.getAllocationId());
        budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
        budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        budgetAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        budgetAllocation.setRefTransId(HelperUtils.getAllocationId());
        budgetAllocation.setFinYear(budgetReciptSaveRequest.getBudgetFinancialYearId());
        budgetAllocation.setToUnit(hrData.getUnitId());
        budgetAllocation.setSubHead(budgetReciptSaveRequest.getBudgetHeadId());
        budgetAllocation.setAllocationTypeId(budgetReciptSaveRequest.getAllocationTypeId());
        budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getAllocationAmount()));
        budgetAllocation.setUnallocatedAmount("0");
        budgetAllocation.setIsFlag("0");
        budgetAllocation.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetReciptSaveRequest.getAllocationAmount()));
        budgetAllocation.setUserId(hrData.getPid());
        budgetAllocation.setStatus("Approved");
        budgetAllocation.setAuthGroupId(authGroupId);
        budgetAllocation.setAmountType(amountUnit.getAmountTypeId());
        budgetAllocationRepository.save(budgetAllocation);


        //Send Return Data

        List<AuthorityTableResponse> authorityTableList = new ArrayList<AuthorityTableResponse>();
        BudgetReciptListResponse budgetAllocationResponse = new BudgetReciptListResponse();
        List<BudgetReciptListSubResponse> budgetAllocationList = new ArrayList<BudgetReciptListSubResponse>();


        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByFromUnitAndIsDelete("000000", "0");
        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

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

            List<CdaParkingTrans> cdaParkingListData = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(budgetAllocations.get(i).getFinYear(), budgetAllocations.get(i).getSubHead(), hrData.getUnitId(), "0");
            if (cdaParkingListData.size() > 0) {
                budgetAllocationReport.setIsCdaParked("1");
            } else {
                budgetAllocationReport.setIsCdaParked("0");
            }

            List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            if (authoritiesList.size() > 0) {

                for (Integer m = 0; m < authoritiesList.size(); m++) {
                    AuthorityTableResponse authorityTableResponse = new AuthorityTableResponse();
                    BeanUtils.copyProperties(authoritiesList.get(m), authorityTableResponse);
                    authorityTableList.clear();
                    authorityTableList.add(authorityTableResponse);

                    FileUpload fileUploadData = fileUploadRepository.findByUploadID(authoritiesList.get(m).getDocId());
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
    public ApiResponse<BudgetReciptListResponse> getBudgetRecipt() {


        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {

        }
        List<AuthorityTableResponse> authorityTableList = new ArrayList<AuthorityTableResponse>();
        BudgetReciptListResponse budgetAllocationResponse = new BudgetReciptListResponse();
        List<BudgetReciptListSubResponse> budgetAllocationList = new ArrayList<BudgetReciptListSubResponse>();
        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByFromUnitAndIsDelete("000000", "0");
        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

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

            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
            budgetAllocationReport.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocTypeId()));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));


            List<CdaParkingTrans> cdaParkingListData = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocations.get(i).getTransactionId(), "0");
            if (cdaParkingListData.size() > 0) {
                budgetAllocationReport.setIsCdaParked("1");
            } else {
                budgetAllocationReport.setIsCdaParked("0");
            }


            List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            if (authoritiesList.size() > 0) {

                for (Integer m = 0; m < authoritiesList.size(); m++) {
                    AuthorityTableResponse authorityTableResponse = new AuthorityTableResponse();
                    BeanUtils.copyProperties(authoritiesList.get(m), authorityTableResponse);
                    authorityTableList.clear();
                    authorityTableList.add(authorityTableResponse);

                    FileUpload fileUploadData = fileUploadRepository.findByUploadID(authoritiesList.get(m).getDocId());
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
    public ApiResponse<AllBudgetRevisionResponse> getBudgetReciptFilter(BudgetReciptSaveRequest budgetReciptSaveRequest) {
        AllBudgetRevisionResponse budgetAllocationResponse = new AllBudgetRevisionResponse();

        String authgroupId = "";
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION LOGIN AGAIN.");
        } else {

        }

        if (budgetReciptSaveRequest.getBudgetFinancialYearId() == null || budgetReciptSaveRequest.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
        }


        if (budgetReciptSaveRequest.getMajorHeadId() == null || budgetReciptSaveRequest.getMajorHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "MAJOR HEAD ID CAN NOT BE BLANK");
        }


        if (budgetReciptSaveRequest.getBudgetHeadType() == null || budgetReciptSaveRequest.getBudgetHeadType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET HEAD TYPE CAN NOT BE BLANK");
        }


        HashMap<String, BudgetRecioptDemoResponse> hashMap = new HashMap<>();


        List<BudgetHead> majorData = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(budgetReciptSaveRequest.getMajorHeadId(), budgetReciptSaveRequest.getBudgetHeadType());


        Collections.sort(majorData, new Comparator<BudgetHead>() {
            public int compare(BudgetHead v1, BudgetHead v2) {
                return v1.getSerialNumber().compareTo(v2.getSerialNumber());
            }
        });

        for (Integer l = 0; l < majorData.size(); l++) {
            List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByFromUnitAndFinYearAndSubHeadAndIsDelete("000000", budgetReciptSaveRequest.getBudgetFinancialYearId(), majorData.get(l).getBudgetCodeId(), "0");

            if (budgetAllocations.size() <= 0) {
                BudgetRecioptDemoResponse budgetRecioptDemoResponse = new BudgetRecioptDemoResponse();
                budgetRecioptDemoResponse.setBudgetHead(majorData.get(l));
                hashMap.put(majorData.get(l).getSubHeadDescr(), budgetRecioptDemoResponse);

            } else {
                for (Integer i = 0; i < budgetAllocations.size(); i++) {

                    BudgetHead budgetHead = subHeadRepository.findByBudgetCodeIdAndSubHeadTypeIdOrderBySerialNumberAsc(budgetAllocations.get(i).getSubHead(), budgetReciptSaveRequest.getBudgetHeadType());

                    if (hashMap.containsKey(budgetHead.getSubHeadDescr())) {
                        BudgetRecioptDemoResponse budgetRecioptDemoResponse = hashMap.get(budgetHead.getSubHeadDescr());

                        if (budgetAllocations.get(i).getAllocTypeId().equalsIgnoreCase("ALL_101")) {
                            budgetRecioptDemoResponse.setBe(budgetAllocations.get(i).getAllocationAmount());
                        } else if (budgetAllocations.get(i).getAllocTypeId().equalsIgnoreCase("ALL_102")) {
                            budgetRecioptDemoResponse.setRe(budgetAllocations.get(i).getAllocationAmount());
                        } else if (budgetAllocations.get(i).getAllocTypeId().equalsIgnoreCase("ALL_105")) {
                            budgetRecioptDemoResponse.setMa(budgetAllocations.get(i).getAllocationAmount());
                        } else if (budgetAllocations.get(i).getAllocTypeId().equalsIgnoreCase("ALL_107")) {
                            budgetRecioptDemoResponse.setSg(budgetAllocations.get(i).getAllocationAmount());
                        } else if (budgetAllocations.get(i).getAllocTypeId().equalsIgnoreCase("ALL_106")) {
                            budgetRecioptDemoResponse.setVoa(budgetAllocations.get(i).getAllocationAmount());
                        }
                        budgetRecioptDemoResponse.setTransactionId(budgetAllocations.get(0).getTransactionId());
                        budgetRecioptDemoResponse.setBudgetHead(budgetHead);

                        hashMap.put(budgetHead.getSubHeadDescr(), budgetRecioptDemoResponse);


                    } else {

                        BudgetRecioptDemoResponse budgetRecioptDemoResponse = new BudgetRecioptDemoResponse();

                        if (budgetAllocations.get(i).getAllocTypeId().equalsIgnoreCase("ALL_101")) {
                            budgetRecioptDemoResponse.setBe(budgetAllocations.get(i).getAllocationAmount());
                        } else if (budgetAllocations.get(i).getAllocTypeId().equalsIgnoreCase("ALL_102")) {
                            budgetRecioptDemoResponse.setRe(budgetAllocations.get(i).getAllocationAmount());
                        } else if (budgetAllocations.get(i).getAllocTypeId().equalsIgnoreCase("ALL_105")) {
                            budgetRecioptDemoResponse.setMa(budgetAllocations.get(i).getAllocationAmount());
                        } else if (budgetAllocations.get(i).getAllocTypeId().equalsIgnoreCase("ALL_107")) {
                            budgetRecioptDemoResponse.setSg(budgetAllocations.get(i).getAllocationAmount());
                        } else if (budgetAllocations.get(i).getAllocTypeId().equalsIgnoreCase("ALL_106")) {
                            budgetRecioptDemoResponse.setVoa(budgetAllocations.get(i).getAllocationAmount());
                        }
                        budgetRecioptDemoResponse.setTransactionId(budgetAllocations.get(0).getTransactionId());
                        budgetRecioptDemoResponse.setBudgetHead(budgetHead);
                        authgroupId = budgetAllocations.get(i).getAuthGroupId();
                        hashMap.put(budgetHead.getSubHeadDescr(), budgetRecioptDemoResponse);

                    }
                }
            }


        }


        List<BudgetRecioptDemoResponse> budgetData = new ArrayList<BudgetRecioptDemoResponse>();
        for (Map.Entry<String, BudgetRecioptDemoResponse> entry : hashMap.entrySet()) {
            BudgetRecioptDemoResponse tabData = entry.getValue();
            budgetData.add(tabData);

        }


        Collections.sort(budgetData, new Comparator<BudgetRecioptDemoResponse>() {
            public int compare(BudgetRecioptDemoResponse v1, BudgetRecioptDemoResponse v2) {
                return v1.getBudgetHead().getSerialNumber().compareTo(v2.getBudgetHead().getSerialNumber());
            }
        });


        List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(authgroupId);

        budgetAllocationResponse.setAuthList(authoritiesList);
        budgetAllocationResponse.setLoda(null);
        budgetAllocationResponse.setBudgetData(budgetData);


        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<AllBudgetRevisionResponse>() {
        });
    }

    @Override
    public ApiResponse<CgUnit> getModData() {

        CgUnit cgUnit = cgUnitRepository.findByUnit("000000");
        return ResponseUtils.createSuccessResponse(cgUnit, new TypeReference<CgUnit>() {
        });
    }


}

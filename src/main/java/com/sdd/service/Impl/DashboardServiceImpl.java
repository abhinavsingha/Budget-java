package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sdd.entities.*;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.request.DashBoardRequest;
import com.sdd.request.DashExpResquest;
import com.sdd.response.*;
import com.sdd.service.DashBoardService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashBoardService {

    @Autowired
    CurrentStateRepository currentStateRepository;

    @Autowired
    AmountUnitRepository amountUnitRepository;

    @Autowired
    AllocationRepository allocationRepository;

    @Autowired
    MangeInboxOutBoxRepository mangeInboxOutBoxRepository;

    @Autowired
    SubHeadRepository subHeadRepository;

    @Autowired
    CgUnitRepository cgUnitRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    BudgetFinancialYearRepository budgetFinancialYearRepository;

    @Autowired
    BudgetAllocationDetailsRepository budgetAllocationDetailsRepository;

    @Autowired
    BudgetAllocationRepository budgetAllocationRepository;

    @Autowired
    private ContigentBillRepository contigentBillRepository;

    @Autowired
    private HeaderUtils headerUtils;

    @Autowired
    private CdaParkingTransRepository cdaParkingTransRepository;

    @Autowired
    private HrDataRepository hrDataRepository;

    @Override
    public ApiResponse<DashBoardResponse> getDashBoardData(DashBoardRequest dashBoardRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        DashBoardResponse dashBoardResponse = new DashBoardResponse();
        HrData hrDataCheck =
                hrDataRepository.findByUserNameAndIsActive(
                        currentLoggedInUser.getPreferred_username(), "1");
        List<InboxOutBoxResponse> defaultResponse = new ArrayList<InboxOutBoxResponse>();

//        List<BudgetAllocationDetails> budgetList = new ArrayList<>();

        if (hrDataCheck == null) {
            hrDataCheck = getAuthorization(currentLoggedInUser.getPreferred_username());
            if (hrDataCheck == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.");
            }
        }

//        if (dashBoardRequest.getToUnitId() == null || dashBoardRequest.getToUnitId().isEmpty()) {
//            budgetList = budgetAllocationDetailsRepository.findAll();
//        } else {
//            CgUnit cgFromUnit = cgUnitRepository.findByUnit(dashBoardRequest.getToUnitId());
//            if (cgFromUnit == null) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
//            } else {
//                budgetList =
//                        budgetAllocationDetailsRepository.findByToUnitAndIsDeleteAndIsBudgetRevision(
//                                dashBoardRequest.getToUnitId(), "0", "0");
//            }
//        }

//        if (dashBoardRequest.getBudgetFinancialYearId() == null
//                || dashBoardRequest.getBudgetFinancialYearId().isEmpty()) {
//
//        } else {
//            BudgetFinancialYear budgetFinancialYear =
//                    budgetFinancialYearRepository.findBySerialNo(dashBoardRequest.getBudgetFinancialYearId());
//            if (budgetFinancialYear == null) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
//            } else {
//                budgetList =
//                        budgetAllocationDetailsRepository.findByToUnitAndFinYearAndIsDeleteAndIsBudgetRevision(
//                                dashBoardRequest.getToUnitId(),
//                                dashBoardRequest.getBudgetFinancialYearId(),
//                                "0",
//                                "0");
//            }
//        }

        HradataResponse hradataResponse = new HradataResponse();

        if (hrDataCheck == null) {
            BeanUtils.copyProperties(hrDataCheck, hradataResponse);
            hradataResponse.setFullName(currentLoggedInUser.getName());
            String[] getRoleData = "113".split(",");

            List<Role> setAllRole = new ArrayList<>();
            for (Integer n = 0; n < getRoleData.length; n++) {
                Role getRole = roleRepository.findByRoleId(getRoleData[n]);
                if (getRole == null) {
                    getRole = roleRepository.findByRoleId("113");
                }
                setAllRole.add(getRole);
            }
            hradataResponse.setRole(setAllRole);

        } else {

            BeanUtils.copyProperties(hrDataCheck, hradataResponse);

            hrDataCheck.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            hrDataCheck.setUpdatedOn(HelperUtils.getCurrentTimeStamp());

            String[] getRoleData = null;
            if (hrDataCheck.getRoleId() == null) {
                getRoleData = "113".split(",");
            } else {
                getRoleData = hrDataCheck.getRoleId().split(",");
            }

            List<Role> setAllRole = new ArrayList<>();
            for (Integer n = 0; n < getRoleData.length; n++) {
                Role getRole = roleRepository.findByRoleId(getRoleData[n]);
                if (getRole == null) {
                    getRole = roleRepository.findByRoleId("113");
                }
                setAllRole.add(getRole);
            }
            hradataResponse.setRole(setAllRole);
        }


        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
        if (allocationType.size() > 0) {
            dashBoardResponse.setAllocationType(allocationType.get(0));
        }


        CurrntStateType stateList1 = currentStateRepository.findByTypeAndIsFlag("FINYEAR", "1");
        if (stateList1 == null) {
            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo("01");
            dashBoardResponse.setBudgetFinancialYear(budgetFinancialYear);
        } else {
            BudgetFinancialYear budgetFinancialYear =
                    budgetFinancialYearRepository.findBySerialNo(stateList1.getStateId());
            dashBoardResponse.setBudgetFinancialYear(budgetFinancialYear);
        }

        dashBoardResponse.setUserDetails(hradataResponse);

        List<MangeInboxOutbox> inboxOutboxesList = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> arrpovedLis = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> archiveLis = new ArrayList<MangeInboxOutbox>();

        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {

        }

        List<MangeInboxOutbox> inboxList = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> outBoxList = new ArrayList<MangeInboxOutbox>();

        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER)) {

            arrpovedLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));
            archiveLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));


            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "BG", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("AP")) {
                    MangeInboxOutbox data = inboxOutboxesList.get(i);
                    inboxList.add(data);

                } else {
                    MangeInboxOutbox data = inboxOutboxesList.get(i);
                    outBoxList.add(data);
                }
            }
        }

        else if (getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {

            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");

//            inboxOutboxesList = mangeInboxOutBoxRepository.findByInboxDataForAllRole(hrDataCheck.getUnitId(), "0", "0", "BG", "BR");
            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0", "0", dataIscgBg);

            arrpovedLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));
            archiveLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));


            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {
                if (inboxOutboxesList.get(i).getToUnit().equalsIgnoreCase(hrDataCheck.getUnitId())) {

                    if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {
                        MangeInboxOutbox data = inboxOutboxesList.get(i);
                        inboxList.add(data);

                    } else {
                        MangeInboxOutbox data = inboxOutboxesList.get(i);
                        outBoxList.add(data);
                    }
                }
            }
        }

        else if (getCurrentRole.contains(HelperUtils.CBCREATER)) {
            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB", "0", "0");


            arrpovedLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));
            archiveLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));


            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {
                    MangeInboxOutbox data = inboxOutboxesList.get(i);
                    inboxList.add(data);

                } else {
                    MangeInboxOutbox data = inboxOutboxesList.get(i);
                    outBoxList.add(data);
                }
            }
        }

        else if (getCurrentRole.contains(HelperUtils.CBVERIFER)) {

            arrpovedLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));
            archiveLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));

            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("VE")) {
                    MangeInboxOutbox data = inboxOutboxesList.get(i);
                    inboxList.add(data);

                } else {
                    MangeInboxOutbox data = inboxOutboxesList.get(i);
                    outBoxList.add(data);
                }
            }
        }

        else if (getCurrentRole.contains(HelperUtils.CBAPPROVER)) {

            arrpovedLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));
            archiveLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));

            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("AP")) {
                    MangeInboxOutbox data = inboxOutboxesList.get(i);
                    inboxList.add(data);

                } else {
                    MangeInboxOutbox data = inboxOutboxesList.get(i);
                    outBoxList.add(data);
                }
            }
        }


        HashMap<String, CgUnit> cgunitData = new LinkedHashMap<>();
        HashMap<String, BudgetHead> subHeadData = new LinkedHashMap<>();

        List<BudgetAllocation> budgetAllocation =
                budgetAllocationRepository.findByToUnitAndIsFlagAndIsBudgetRevision(
                        hrDataCheck.getUnitId(), "0", "0");

        for (int i = 0; i < budgetAllocation.size(); i++) {

            BudgetHead budgetHeadId =
                    subHeadRepository.findByBudgetCodeId(budgetAllocation.get(i).getSubHead());
            if (budgetHeadId == null) {
                throw new SDDException(
                        HttpStatus.UNAUTHORIZED.value(), "INVALID SUB HEAD TYPE ID.CONTACT YOUR ADMINISTRATOR");
            }

            CgUnit cgToUnitData = cgUnitRepository.findByUnit(budgetAllocation.get(i).getToUnit());

            cgunitData.put(cgToUnitData.getUnit(), cgToUnitData);
            subHeadData.put(budgetHeadId.getSubHeadTypeId(), budgetHeadId);
        }

//        UnitWiseExpenditueResponse unitWiseExpenditueResponse = new UnitWiseExpenditueResponse();
//        List<String> unit = new ArrayList<String>();
//        List<String> unitByAllocationAmount = new ArrayList<String>();
//        List<String> unitByExpendureAmount = new ArrayList<String>();
//
//        for (Map.Entry<String, CgUnit> entry : cgunitData.entrySet()) {
//            CgUnit unitData = entry.getValue();
//
//            List<BudgetAllocation> budgetAllocationList =
//                    budgetAllocationRepository.findByToUnitAndFinYearAndIsFlagAndIsBudgetRevision(
//                            unitData.getUnit(), "01", "0", "0");
//            double allocationAmount = 0;
//            for (int i = 0; i < budgetAllocationList.size(); i++) {
//                allocationAmount =
//                        allocationAmount
//                                + Double.parseDouble(budgetAllocationList.get(i).getAllocationAmount());
//            }
//
//            double expenditure = 0;
//            List<ContigentBill> cbExpendure =
//                    contigentBillRepository.findByCbUnitIdAndIsFlagAndIsUpdate(unitData.getUnit(), "0", "0");
//
//            for (Integer i = 0; i < cbExpendure.size(); i++) {
//                expenditure = expenditure + Double.parseDouble(cbExpendure.get(i).getCbAmount());
//            }
//
//            unit.add(unitData.getCgUnitShort());
//            unitByAllocationAmount.add(allocationAmount + "");
//            unitByExpendureAmount.add(expenditure + "");
//        }
//
//        unitWiseExpenditueResponse.setUnitWise(unit);
//        unitWiseExpenditueResponse.setExpenditureUnit(unitByExpendureAmount);
//        unitWiseExpenditueResponse.setAllocatedUnit(unitByAllocationAmount);
//
//
//        List<String> subHeadWise = new ArrayList<String>();

//        SubHeadWiseExpenditueResponse subHeadWiseExpenditueResponse =
//                new SubHeadWiseExpenditueResponse();
//        List<String> subhead = new ArrayList<String>();
//        List<String> allocatedSubHead = new ArrayList<String>();
//        List<String> expenditureSubHead = new ArrayList<String>();
//
//        for (Map.Entry<String, BudgetHead> entry : subHeadData.entrySet()) {
//            String key = entry.getKey();
//            BudgetHead subHeadD = entry.getValue();
//
//            List<BudgetAllocation> budgetAllocationList =
//                    budgetAllocationRepository.findByToUnitAndFinYearAndIsFlagAndIsBudgetRevision(
//                            hrDataCheck.getUnit(), "01", "1", "0");
//            double allocationAmount = 0;
//            for (int i = 0; i < budgetAllocationList.size(); i++) {
//                allocationAmount =
//                        allocationAmount
//                                + Double.parseDouble(budgetAllocationList.get(i).getAllocationAmount());
//            }
//
//            double expenditure = 0;
//            List<ContigentBill> cbExpendure =
//                    contigentBillRepository.findByBudgetHeadIDAndIsFlagAndIsUpdate(subHeadD.getBudgetCodeId(), "0", "0");
//
//            for (Integer i = 0; i < cbExpendure.size(); i++) {
//                expenditure = expenditure + Double.parseDouble(cbExpendure.get(i).getCbAmount());
//            }
//
////            subHeadWise.add(subHeadD.getSubheadShort());
//            allocatedSubHead.add(allocationAmount + "");
//            expenditureSubHead.add(expenditure + "");
//        }

//        subHeadWiseExpenditueResponse.setSubhead(subHeadWise);
//        subHeadWiseExpenditueResponse.setAllocatedSubHead(allocatedSubHead);
//        subHeadWiseExpenditueResponse.setExpenditureSubHead(expenditureSubHead);

//        dashBoardResponse.setSubHeadWiseExpenditure(subHeadWiseExpenditueResponse);

//        List<DashBoardSubResponse> dashBoardList = new ArrayList<DashBoardSubResponse>();
//        for (int i = 0; i < budgetList.size(); i++) {
//            DashBoardSubResponse dashBoardData = new DashBoardSubResponse();
//
//            dashBoardData.setLastCBDate(budgetList.get(i).getAllocationDate());
//            dashBoardData.setAllocatedAmount(budgetList.get(i).getAllocationAmount());
//            dashBoardData.setFinancialYearId(
//                    budgetFinancialYearRepository.findBySerialNo(budgetList.get(i).getFinYear()));
//            dashBoardData.setSubHead(
//                    subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(
//                            budgetList.get(i).getSubHead()));
//            dashBoardData.setUnit(cgUnitRepository.findByUnit(budgetList.get(i).getToUnit()));
//            dashBoardData.setStatus(budgetList.get(i).getStatus());
//            dashBoardData.setAuthGroupId(budgetList.get(i).getAuthGroupId());
//            dashBoardList.add(dashBoardData);
//        }

        dashBoardResponse.setInbox(inboxList.size() + "");
        dashBoardResponse.setApproved(arrpovedLis.size() + "");
        dashBoardResponse.setArchived(archiveLis.size() + "");
        dashBoardResponse.setOutBox(outBoxList.size() + "");
        return ResponseUtils.createSuccessResponse(
                dashBoardResponse, new TypeReference<DashBoardResponse>() {
                });
    }

    @Override
    public ApiResponse<UiResponse> getUiData(String roleId) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        DashBoardResponse dashBoardResponse = new DashBoardResponse();
        HrData hrDataCheck =
                hrDataRepository.findByUserNameAndIsActive(
                        currentLoggedInUser.getPreferred_username(), "1");
        List<InboxOutBoxResponse> defaultResponse = new ArrayList<InboxOutBoxResponse>();
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.");
        }

        StringBuilder roledata = new StringBuilder();

        String[] getRoleData = hrDataCheck.getRoleId().split(",");
        for (Integer n = 0; n < getRoleData.length; n++) {
            if (!(getRoleData[n].equalsIgnoreCase(roleId))) {
                roledata.append(getRoleData[n]).append(",");
            }
        }
        String allRole = roleId + "," + roledata;
        hrDataCheck.setRoleId(allRole);
        hrDataRepository.save(hrDataCheck);

        UiResponse uiData = new UiResponse();
        if (roleId == null || roleId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ROLE ID CAN NOT BE BLANK");
        }

        //        if (roleId.equalsIgnoreCase(HelperUtils.SYSTEMADMIN)) {
        //            uiData.setName();
        //            uiData.setUiKey();
        //        } else if (roleId.equalsIgnoreCase(HelperUtils.UNITADMIN)) {
        //            uiData.setName();
        //            uiData.setUiKey();
        //        } else if (roleId.equalsIgnoreCase(HelperUtils.BUDGETMANGER)) {
        //            uiData.setName();
        //            uiData.setUiKey();
        //        } else if (roleId.equalsIgnoreCase(HelperUtils.BUDGETAPPROVER)) {
        //            uiData.setName();
        //            uiData.setUiKey();
        //        } else if (roleId.equalsIgnoreCase(HelperUtils.CBCREATER)) {
        //            uiData.setName();
        //            uiData.setUiKey();
        //        } else if (roleId.equalsIgnoreCase(HelperUtils.CBAPPROVER)) {
        //            uiData.setName();
        //            uiData.setUiKey();
        //        } else if (roleId.equalsIgnoreCase(HelperUtils.VIEWER)) {
        //            uiData.setName();
        //            uiData.setUiKey();
        //        }

        return ResponseUtils.createSuccessResponse(uiData, new TypeReference<UiResponse>() {
        });
    }

    @Override
    public ApiResponse<DashBoardResponse> showAllData() {
        DashBoardResponse dashBoardResponse = new DashBoardResponse();

        return ResponseUtils.createSuccessResponse(
                dashBoardResponse, new TypeReference<DashBoardResponse>() {
                });
    }

    @Override
    public ApiResponse<List<AmountUnit>> getAllAmountUnit() {
        List<AmountUnit> amountUnit = amountUnitRepository.findAll();
        return ResponseUtils.createSuccessResponse(
                amountUnit, new TypeReference<List<AmountUnit>>() {
                });
    }

    @Override
    public ApiResponse<DashBoardResponse> updateInboxOutBox() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        DashBoardResponse dashBoardResponse = new DashBoardResponse();
        HrData hrDataCheck =
                hrDataRepository.findByUserNameAndIsActive(
                        currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {

            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }

        HradataResponse hradataResponse = new HradataResponse();

        if (hrDataCheck == null) {
            BeanUtils.copyProperties(hrDataCheck, hradataResponse);
            hradataResponse.setFullName(currentLoggedInUser.getName());
            String[] getRoleData = "113".split(",");

            List<Role> setAllRole = new ArrayList<>();
            for (Integer n = 0; n < getRoleData.length; n++) {
                Role getRole = roleRepository.findByRoleId(getRoleData[n]);
                if (getRole == null) {
                    getRole = roleRepository.findByRoleId("113");
                }
                setAllRole.add(getRole);
            }
            hradataResponse.setRole(setAllRole);

        } else {

            BeanUtils.copyProperties(hrDataCheck, hradataResponse);

            hrDataCheck.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            hrDataCheck.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            String[] getRoleData = null;
            if (hrDataCheck.getRoleId() == null) {
                getRoleData = "113".split(",");
            } else {
                getRoleData = hrDataCheck.getRoleId().split(",");
            }
            List<Role> setAllRole = new ArrayList<>();
            for (Integer n = 0; n < getRoleData.length; n++) {
                Role getRole = roleRepository.findByRoleId(getRoleData[n]);
                if (getRole == null) {
                    getRole = roleRepository.findByRoleId("113");
                }
                setAllRole.add(getRole);
            }
            hradataResponse.setRole(setAllRole);
        }


        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
        if (allocationType.size() > 0) {
            dashBoardResponse.setAllocationType(allocationType.get(0));
        }


        CurrntStateType stateList1 = currentStateRepository.findByTypeAndIsFlag("FINYEAR", "1");
        if (stateList1 == null) {
            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo("01");
            dashBoardResponse.setBudgetFinancialYear(budgetFinancialYear);
        } else {
            BudgetFinancialYear budgetFinancialYear =
                    budgetFinancialYearRepository.findBySerialNo(stateList1.getStateId());
            dashBoardResponse.setBudgetFinancialYear(budgetFinancialYear);
        }

        dashBoardResponse.setUserDetails(hradataResponse);
        List<MangeInboxOutbox> inboxOutboxesList = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> archiveboxesList = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> approvedboxesList = new ArrayList<MangeInboxOutbox>();
        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {
        }
        List<InboxOutBoxSubResponse> inboxList = new ArrayList<InboxOutBoxSubResponse>();
        List<InboxOutBoxSubResponse> outBoxList = new ArrayList<InboxOutBoxSubResponse>();

        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER)) {

            approvedboxesList.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));
            archiveboxesList.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));


            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "BG", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("AP")) {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);

                    inboxList.add(data);

                } else {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);

                    outBoxList.add(data);
                }
            }
        } else if (getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {
            approvedboxesList.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));
            archiveboxesList.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));

//            inboxOutboxesList = mangeInboxOutBoxRepository.findByInboxDataForAllRole(hrDataCheck.getUnitId(), "0", "0", "BG", "BR");
            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");

//            inboxOutboxesList = mangeInboxOutBoxRepository.findByInboxDataForAllRole(hrDataCheck.getUnitId(), "0", "0", "BG", "BR");
            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0", "0", dataIscgBg);


            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {
                if (inboxOutboxesList.get(i).getToUnit().equalsIgnoreCase(hrDataCheck.getUnitId())) {

                    if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {
                        InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                        MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                        inboxList.add(data);
                    } else {
                        InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                        MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                        outBoxList.add(data);
                    }
                }
            }
        } else if (getCurrentRole.contains(HelperUtils.CBCREATER)) {
            approvedboxesList.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));
            archiveboxesList.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));


            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {
                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    inboxList.add(data);
                } else {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    outBoxList.add(data);
                }
            }
        } else if (getCurrentRole.contains(HelperUtils.CBVERIFER)) {

            approvedboxesList.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));
            archiveboxesList.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));


            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {
                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("VE")) {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    inboxList.add(data);
                } else {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    outBoxList.add(data);
                }
            }
        } else if (getCurrentRole.contains(HelperUtils.CBAPPROVER)) {

            approvedboxesList.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));
            archiveboxesList.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));


            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {
                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("AP")) {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    inboxList.add(data);
                } else {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    outBoxList.add(data);
                }
            }
        }


        dashBoardResponse.setInbox(inboxList.size() + "");
        dashBoardResponse.setOutBox(outBoxList.size() + "");
        dashBoardResponse.setArchived(archiveboxesList.size() + "");
        dashBoardResponse.setApproved(approvedboxesList.size() + "");
        return ResponseUtils.createSuccessResponse(
                dashBoardResponse, new TypeReference<DashBoardResponse>() {
                });
    }

    @Override
    public ApiResponse<List<DashBoardExprnditureResponse>> getSubHeadWiseExpenditureByUnitIdFinYearIdAllocationTypeIdSubHeadTypeId(String unitId, String finYearId, String subHeadTypeId, String allocationTypeId,String amountTypeId) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        List<DashBoardExprnditureResponse> dashBoardExprnditureResponseList = new ArrayList<DashBoardExprnditureResponse>();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }

        if (finYearId == null || finYearId.isEmpty()) {
            return ResponseUtils.createFailureResponse(dashBoardExprnditureResponseList, new TypeReference<List<DashBoardExprnditureResponse>>() {
            }, "FINANCIAL YEAR CAN NOT BE NULL OR EMPTY", HttpStatus.OK.value());
        }
        if (unitId == null || unitId.isEmpty()) {
            return ResponseUtils.createFailureResponse(dashBoardExprnditureResponseList, new TypeReference<List<DashBoardExprnditureResponse>>() {
            }, "UNIT ID CAN NOT BE NULL OR EMPTY", HttpStatus.OK.value());
        }

        if (subHeadTypeId == null || subHeadTypeId.isEmpty()) {
            return ResponseUtils.createFailureResponse(dashBoardExprnditureResponseList, new TypeReference<List<DashBoardExprnditureResponse>>() {
            }, "SUBHEAD TYPE  CAN NOT BE NULL OR EMPTY", HttpStatus.OK.value());
        }
        if (allocationTypeId == null || allocationTypeId.isEmpty()) {
            return ResponseUtils.createFailureResponse(dashBoardExprnditureResponseList, new TypeReference<List<DashBoardExprnditureResponse>>() {
            }, "ALLOCATION TYPE ID CAN NOT BE NULL OR EMPTY", HttpStatus.OK.value());
        }

        if (amountTypeId == null || amountTypeId.isEmpty()) {
            return ResponseUtils.createFailureResponse(dashBoardExprnditureResponseList, new TypeReference<List<DashBoardExprnditureResponse>>() {
            }, "AMAOUNT TYPE ID CAN NOT BE NULL OR EMPTY", HttpStatus.OK.value());
        }
        try {


            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(finYearId);

            CgUnit cgUnit = cgUnitRepository.findByUnit(unitId);

            AmountUnit amountObj = amountUnitRepository.findByAmountTypeId(amountTypeId);
            Double reqAmount = amountObj.getAmount();
            String amountIn = amountObj.getAmountType().toUpperCase();

            List<BudgetHead> budgetHeadList = subHeadRepository.findBySubHeadTypeIdOrderBySerialNumberAsc(subHeadTypeId);

            for (BudgetHead val : budgetHeadList) {
                String subHeadId = val.getBudgetCodeId();
                BudgetHead bHead = subHeadRepository.findByBudgetCodeId(subHeadId);
                List<BudgetAllocation> reportDetails = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevision(unitId, finYearId,subHeadId,  allocationTypeId, "0");
                if (reportDetails.size() <= 0) {
                    continue;
                }
                Double amount = 0.0;
                Double amountUnit=0.0;
                Double finAmount=0.0;
                Double eAmount = 0.0;
                Double expnAmount=0.0;
                Double allAmount = 0.0;

                for (Integer r = 0; r < reportDetails.size(); r++) {

                    DashBoardExprnditureResponse dashBoardExprnditureResponse = new DashBoardExprnditureResponse();
                    amount = Double.valueOf(reportDetails.get(r).getAllocationAmount());
                    String amountType=reportDetails.get(r).getAmountType();
                    AmountUnit amountObjs = amountUnitRepository.findByAmountTypeId(amountType);
                    Double amountUnits = amountObjs.getAmount();

                    String uid = reportDetails.get(r).getToUnit();
                    finAmount = amount*amountUnits/reqAmount;
                    List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike("%" + uid + "%");

                    double totalbill = 0.0;
                    Timestamp lastCvDate;
                    String cbD = "";

/*                    if (unitList.size() > 0) {
                        for (CgUnit unitss : unitList) {
                            String subUnit = unitss.getUnit();
                            List<ContigentBill> expenditure = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdate(subUnit, finYearId, subHeadId,allocationTypeId, "0");

                            if (expenditure.size() > 0) {
                                double totalAmount = 0.0;
                                for (ContigentBill bill : expenditure) {
                                    totalAmount += Double.parseDouble(bill.getCbAmount());
                                    if(bill.getCbDate()!=null){
                                        lastCvDate=bill.getCbDate();
                                        SimpleDateFormat id = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                                        SimpleDateFormat od = new SimpleDateFormat("dd-MMMM-yyyy");
                                        Date dateC = null;
                                        try {
                                            dateC = id.parse(lastCvDate.toString());
                                        } catch (ParseException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                        cbD = od.format(dateC);
                                    }else
                                        cbD="";
                                }
                                totalbill += totalAmount;
                            }
                        }
                        DecimalFormat decimalFormat = new DecimalFormat("#");
                        String cbAmount = decimalFormat.format(totalbill);
                        eAmount = Double.parseDouble(cbAmount);
                    }*/
                    double totalCda=0.0;
                    double remCdaBal=0.0;
                    List<CdaParkingTrans> cdaDetail = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYearId, subHeadId, uid, allocationTypeId, "0");

                    double rqUnit=0.0;
                    if (cdaDetail.size() > 0) {
                        for (int j = 0; j < cdaDetail.size(); j++) {

                            totalCda += Double.parseDouble(cdaDetail.get(j).getTotalParkingAmount());
                            remCdaBal += Double.parseDouble(cdaDetail.get(j).getRemainingCdaAmount());
                            AmountUnit hdamtUnit = amountUnitRepository.findByAmountTypeId(cdaDetail.get(0).getAmountType());
                            rqUnit=hdamtUnit.getAmount();
                        }
                    }
                    double cdaTotal=totalCda*rqUnit/reqAmount;
                    double cdaRming=remCdaBal*rqUnit/reqAmount;
                    List<ContigentBill> expenditure = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdate(uid, finYearId, subHeadId,allocationTypeId, "0");
                    double totalAmount = 0.0;
                    if (expenditure.size() > 0) {
                        for (ContigentBill bill : expenditure) {
                            totalAmount += Double.parseDouble(bill.getCbAmount());
                            if(bill.getCbDate()!=null){
                                lastCvDate=bill.getCbDate();
                                SimpleDateFormat id = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                                SimpleDateFormat od = new SimpleDateFormat("dd-MMMM-yyyy");
                                Date dateC = id.parse(lastCvDate.toString());
                                cbD = od.format(dateC);
                            }else
                                cbD="";
                        }
                    }

                    DecimalFormat decimalFormat = new DecimalFormat("#");
                    String cbAmount = decimalFormat.format(totalAmount);
                    eAmount = Double.parseDouble(cbAmount);

                    //eAmount = totalAmount + totalbill;
                    double expAmount=eAmount/reqAmount;

                    dashBoardExprnditureResponse.setCgUnit(cgUnit);
                    dashBoardExprnditureResponse.setBudgetFinancialYear(budgetFinancialYear);
                    dashBoardExprnditureResponse.setBudgetHead(bHead);
                    dashBoardExprnditureResponse.setAllocatedAmount(String.format("%1$0,1.4f", new BigDecimal(cdaTotal)));
                    dashBoardExprnditureResponse.setExpenditureAmount(String.format("%1$0,1.4f", new BigDecimal(expAmount)));
                    if(cdaTotal!=0)
                        dashBoardExprnditureResponse.setPerAmount(String.format("%1$0,1.4f", new BigDecimal(expAmount*100/cdaTotal)));
                    else
                        dashBoardExprnditureResponse.setPerAmount(String.format("%1$0,1.4f", new BigDecimal(0.0)));
                    dashBoardExprnditureResponse.setLastCBDate(cbD);
                    dashBoardExprnditureResponse.setBalAmount(String.format("%1$0,1.4f", new BigDecimal(cdaRming)));
                    dashBoardExprnditureResponse.setAmountIn(amountIn);
                    dashBoardExprnditureResponseList.add(dashBoardExprnditureResponse);

                }

            }
            return ResponseUtils.createSuccessResponse(
                    dashBoardExprnditureResponseList,
                    new TypeReference<List<DashBoardExprnditureResponse>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
            throw new SDDException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server error.");
        }
    }
    @Override
    public ApiResponse<List<SubHeadWiseExpResp>> getDashBordSubHeadwiseExpenditure(DashExpResquest dashExpResquest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        List<SubHeadWiseExpResp> resp = new ArrayList<SubHeadWiseExpResp>();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }

        if (dashExpResquest.getSubHeadId() == null || dashExpResquest.getSubHeadId().isEmpty()) {
            return ResponseUtils.createFailureResponse(resp, new TypeReference<List<SubHeadWiseExpResp>>() {
            }, "SUBHEAD ID  CAN NOT BE NULL OR EMPTY", HttpStatus.OK.value());
        }
        if (dashExpResquest.getFinYearId() == null || dashExpResquest.getFinYearId().isEmpty()) {
            return ResponseUtils.createFailureResponse(resp, new TypeReference<List<SubHeadWiseExpResp>>() {
            }, "FIN YEAR ID  CAN NOT BE NULL OR EMPTY", HttpStatus.OK.value());
        }

        if (dashExpResquest.getAllocationTypeId() == null || dashExpResquest.getAllocationTypeId().isEmpty()) {
            return ResponseUtils.createFailureResponse(resp, new TypeReference<List<SubHeadWiseExpResp>>() {
            }, "ALLOCATION TYPE ID  CAN NOT BE NULL OR EMPTY", HttpStatus.OK.value());
        }
        if (dashExpResquest.getAmounttypeId() == null || dashExpResquest.getAmounttypeId().isEmpty()) {
            return ResponseUtils.createFailureResponse(resp, new TypeReference<List<SubHeadWiseExpResp>>() {
            }, "AMOUNT TYPE ID   CAN NOT BE NULL OR EMPTY", HttpStatus.OK.value());
        }

        String finYearId=dashExpResquest.getFinYearId();
        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(finYearId);
        String subHeadId=dashExpResquest.getSubHeadId();
        BudgetHead bHead = subHeadRepository.findByBudgetCodeId(subHeadId);
        String amounttypeId=dashExpResquest.getAmounttypeId();
        String allocationTypeId=dashExpResquest.getAllocationTypeId();
        AllocationType allockData = allocationRepository.findByAllocTypeId(allocationTypeId);
        AmountUnit hdamtUnits = amountUnitRepository.findByAmountTypeId(amounttypeId);
        double reqAmount=hdamtUnits.getAmount();
        try{
            List<CgUnit> ulist1 = cgUnitRepository.findByBudGroupUnitLike("%" + hrData.getUnitId() + "%");
            List<CgUnit> ulist=ulist1.stream().filter(e->e.getIsActive().equalsIgnoreCase("1")).collect(Collectors.toList());
            if (ulist.size()<=0) {
                return ResponseUtils.createFailureResponse(resp, new TypeReference<List<SubHeadWiseExpResp>>() {
                }, "UNIT NOT FOUND", HttpStatus.OK.value());
            }

            for (int j = 0; j < ulist.size(); j++) {
                String uid=ulist.get(j).getUnit();
                CgUnit cgUnit = cgUnitRepository.findByUnit(uid);
                List<BudgetAllocation> budgetAllocationsDetalis1 = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(subHeadId, uid, finYearId, allocationTypeId, "0");
                double totalCda=0.0;
                double remCdaBal=0.0;
                List<CdaParkingTrans> cdaDetail = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYearId, subHeadId, uid, allocationTypeId, "0");
                double rqUnit=0.0;
                if (cdaDetail.size() > 0) {
                    for (int k = 0; k < cdaDetail.size(); k++) {
                        totalCda += Double.parseDouble(cdaDetail.get(k).getTotalParkingAmount());
                        remCdaBal += Double.parseDouble(cdaDetail.get(k).getRemainingCdaAmount());
                        AmountUnit hdamtUnit = amountUnitRepository.findByAmountTypeId(cdaDetail.get(0).getAmountType());
                        rqUnit=hdamtUnit.getAmount();
                    }
                }
                double cdaTotal=totalCda*rqUnit/reqAmount;
                double cdaRming=remCdaBal*rqUnit/reqAmount;

                List<ContigentBill> expenditure = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdate(uid, finYearId, subHeadId,allocationTypeId, "0");
                double totalAmount = 0.0;
                Timestamp lastCvDate;
                String cbD = "";
                if (expenditure.size() > 0) {
                    for (ContigentBill bill : expenditure) {
                        totalAmount += Double.parseDouble(bill.getCbAmount());
                        if(bill.getCbDate()!=null){
                            lastCvDate=bill.getCbDate();
                            SimpleDateFormat id = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                            SimpleDateFormat od = new SimpleDateFormat("dd-MMMM-yyyy");
                            Date dateC = id.parse(lastCvDate.toString());
                            cbD = od.format(dateC);
                        }else
                            cbD="";
                    }
                }
                double expAmount=totalAmount/reqAmount;
                SubHeadWiseExpResp subResp = new SubHeadWiseExpResp();
                subResp.setUnitName(cgUnit.getDescr());
                subResp.setFinYear(budgetFinancialYear.getFinYear());
                subResp.setAllocType(allockData.getAllocDesc());
                subResp.setAmountIn(hdamtUnits.getAmountType());
                subResp.setAllocatedAmount(String.format("%1$0,1.4f", new BigDecimal(cdaTotal)));
                subResp.setExpenditureAmount(String.format("%1$0,1.4f", new BigDecimal(expAmount)));
                subResp.setBalAmount(String.format("%1$0,1.4f", new BigDecimal(cdaRming)));
                if(cdaTotal!=0)
                    subResp.setPerAmount(String.format("%1$0,1.4f", new BigDecimal(((cdaTotal-cdaRming)*100)/cdaTotal)));
                else
                    subResp.setPerAmount(String.format("%1$0,1.4f", new BigDecimal(0.0)));
                subResp.setLastCBDate(cbD);
                resp.add(subResp);

            }

            return ResponseUtils.createSuccessResponse(resp, new TypeReference<List<SubHeadWiseExpResp>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new SDDException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server error.");
        }
    }

    public HrData getAuthorization(String userInfo) {

        HttpResponse response = null;

        String userInfoData =
                "http://172.18.3.150:8080/cghrdata/getAllData/getAllOfficerData/" + userInfo;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(userInfoData));
            response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            // Getting the response body.
            if (statusCode == 200) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new GsonBuilder().create();
                GwtUserDataResponse exampleData = gson.fromJson(responseBody, GwtUserDataResponse.class);
                return exampleData.getResponse();
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}

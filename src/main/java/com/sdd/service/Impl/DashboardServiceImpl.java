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
import java.math.RoundingMode;
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
        } else if (getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {

            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");
            dataIscgBg.add("RR");
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
        } else if (getCurrentRole.contains(HelperUtils.CBCREATER)) {
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
        } else if (getCurrentRole.contains(HelperUtils.CBVERIFER)) {

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
        } else if (getCurrentRole.contains(HelperUtils.CBAPPROVER)) {

            arrpovedLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));
            archiveLis.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));

            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("AP")) {
                    MangeInboxOutbox data = inboxOutboxesList.get(i);
                    inboxList.add(data);

                } else if (inboxOutboxesList.get(i).getStatus().equalsIgnoreCase("Approved") && inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {
                    MangeInboxOutbox data = inboxOutboxesList.get(i);
                    outBoxList.add(data);
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
            dataIscgBg.add("RR");

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
                } else if (inboxOutboxesList.get(i).getStatus().equalsIgnoreCase("Approved") && inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    outBoxList.add(data);
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
    public ApiResponse<List<DashBoardExprnditureResponse>> getSubHeadWiseExpenditureByUnitIdFinYearIdAllocationTypeIdSubHeadTypeId(String unitId, String finYearId, String subHeadTypeId, String allocationTypeId, String amountTypeId, String majorHead) {
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

        if (majorHead == null || majorHead.isEmpty()) {
            return ResponseUtils.createFailureResponse(dashBoardExprnditureResponseList, new TypeReference<List<DashBoardExprnditureResponse>>() {
            }, "MAJOR HEAD  CAN NOT BE NULL OR EMPTY", HttpStatus.OK.value());
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

            List<BudgetHead> budgetHeadList = subHeadRepository.findByMajorHeadOrderBySerialNumberAsc(majorHead);
            List<CgUnit> unitList1 = cgUnitRepository.findByBudGroupUnitLike("%" + unitId + "%");
            List<CgUnit> unitList=unitList1.stream().filter(e->!e.getUnit().equalsIgnoreCase(unitId)).collect(Collectors.toList());
            DashBoardExprnditureResponse respn = new DashBoardExprnditureResponse();
            List<GrTotalObjResp> grResp = new ArrayList<GrTotalObjResp>();
            double sumAlloc = 0.0;
            double sumExp = 0.0;
            double sumBal = 0.0;
            double perBal = 0.0;
            for (BudgetHead val : budgetHeadList) {
                String subHeadId = val.getBudgetCodeId();
                BudgetHead bHead = subHeadRepository.findByBudgetCodeId(subHeadId);
                List<BudgetAllocation> reportDetails = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unitId, finYearId, subHeadId, allocationTypeId, "Approved", "0", "0");
                if (reportDetails.size() <= 0) {
                    continue;
                }
                double amount = 0.0;
                double finAmount = 0.0;
                double eAmount = 0.0;

                for (Integer r = 0; r < reportDetails.size(); r++) {

                    GrTotalObjResp dashBoardExprnditureResponse = new GrTotalObjResp();
                    amount = Double.valueOf(reportDetails.get(r).getAllocationAmount());
                    String amountType = reportDetails.get(r).getAmountType();
                    AmountUnit amountObjs = amountUnitRepository.findByAmountTypeId(amountType);
                    Double amountUnits = amountObjs.getAmount();

                    String uid = reportDetails.get(r).getToUnit();
                    finAmount = amount * amountUnits / reqAmount;

                    double totalbill = 0.0;
                    Timestamp lastCvDate;
                    String cbD = "";

                    if (unitList.size() > 0) {
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
                    }
                    List<ContigentBill> expenditure = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdateAndIsFlag(unitId, finYearId, subHeadId, allocationTypeId, "0", "0");
                    double totalAmount = 0.0;
                    if (expenditure.size() > 0) {
                        for (ContigentBill bill : expenditure) {
                            totalAmount += Double.parseDouble(bill.getCbAmount());
                            if (bill.getCbDate() != null) {
                                lastCvDate = bill.getCbDate();
                                SimpleDateFormat id = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                                SimpleDateFormat od = new SimpleDateFormat("dd-MMMM-yyyy");
                                Date dateC = id.parse(lastCvDate.toString());
                                cbD = od.format(dateC);
                            } else
                                cbD = "";
                        }
                    }
                    double perAmnt = 0.0;
                    eAmount = totalAmount + totalbill;
                    double expAmount = eAmount / reqAmount;


                    double remCdaBal = 0.0;
                    List<CdaParkingTrans> cdaDetail = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYearId, subHeadId, unitId, allocationTypeId, "0");
                    if (cdaDetail.size() > 0) {
                        for (int j = 0; j < cdaDetail.size(); j++) {
                            AmountUnit hdamtUnit = amountUnitRepository.findByAmountTypeId(cdaDetail.get(0).getAmountType());
                            double rqUnit = hdamtUnit.getAmount();
                            if (cdaDetail.get(j).getRemainingCdaAmount() == null) {
                                remCdaBal=0.0;
                            }else{
                                double cdaBal=Double.parseDouble(cdaDetail.get(j).getRemainingCdaAmount());
                                remCdaBal += cdaBal * rqUnit;
                            }
                        }
                    }

                    double cdaRmingSelf = remCdaBal / reqAmount;


                    double remCdaBalSub = 0.0;
                    for (CgUnit unitS : unitList) {
                        String subUnits = unitS.getUnit();
                        List<CdaParkingTrans> cdaDetailSub = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYearId, subHeadId, subUnits, allocationTypeId, "0");
                        if (cdaDetailSub.size() > 0) {
                            double cdaRmBal=0.0;
                            for (int k = 0; k < cdaDetailSub.size(); k++) {
                                AmountUnit hdamtUnit = amountUnitRepository.findByAmountTypeId(cdaDetailSub.get(0).getAmountType());
                                double rqUnitSub = hdamtUnit.getAmount();
                                if (cdaDetailSub.get(k).getRemainingCdaAmount() == null) {
                                    cdaRmBal +=0.0;
                                }else{
                                    double cdaRm=Double.parseDouble(cdaDetailSub.get(k).getRemainingCdaAmount());
                                    cdaRmBal += cdaRm * rqUnitSub;
                                }
                            }
                            remCdaBalSub += cdaRmBal;
                        }
                    }
                    double cdaRmingSub = remCdaBalSub/ reqAmount;

                    double sumCdaRmng=cdaRmingSub+cdaRmingSelf;

                    dashBoardExprnditureResponse.setCgUnit(cgUnit);
                    dashBoardExprnditureResponse.setBudgetFinancialYear(budgetFinancialYear);
                    dashBoardExprnditureResponse.setBudgetHead(bHead);
                    dashBoardExprnditureResponse.setAllocatedAmount(String.format("%1$0,1.4f", new BigDecimal(finAmount)));
                    dashBoardExprnditureResponse.setExpenditureAmount(String.format("%1$0,1.4f", new BigDecimal(expAmount)));
                    if (finAmount != 0) {
                        perAmnt = (expAmount * 100) / finAmount;
                        dashBoardExprnditureResponse.setPerAmount(String.format("%1$0,1.2f", new BigDecimal(perAmnt)));
                    } else {
                        perAmnt = 0.0;
                        dashBoardExprnditureResponse.setPerAmount(String.format("%1$0,1.2f", new BigDecimal(0.0)));
                    }
                    dashBoardExprnditureResponse.setLastCBDate(cbD);
                    dashBoardExprnditureResponse.setBalAmount(String.format("%1$0,1.4f", new BigDecimal(sumCdaRmng)));
                    dashBoardExprnditureResponse.setAmountIn(amountIn);
                    grResp.add(dashBoardExprnditureResponse);

                    sumAlloc += finAmount;
                    sumExp += expAmount;
                    sumBal += sumCdaRmng;
                    perBal += perAmnt;
                }
            }
            double per = 0.0;
            if (sumAlloc != 0) {
                per = sumExp * 100 / sumAlloc;
            } else {
                per = 0.0;
            }
            try {
                respn.setPerBal(ConverterUtils.addDecimal2Point(per + ""));
            } catch (Exception e) {
                respn.setPerBal(String.valueOf(0.00));
            }

            respn.setSumAlloc(ConverterUtils.addDecimalPoint(sumAlloc + ""));
            respn.setSumExp(ConverterUtils.addDecimalPoint(sumExp + ""));
            respn.setSumBal(ConverterUtils.addDecimalPoint(sumBal + ""));
            respn.setGrTotalObjResp(grResp);
            dashBoardExprnditureResponseList.add(respn);
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

        String finYearId = dashExpResquest.getFinYearId();
        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(finYearId);
        String subHeadId = dashExpResquest.getSubHeadId();
        BudgetHead bHead = subHeadRepository.findByBudgetCodeId(subHeadId);
        String amounttypeId = dashExpResquest.getAmounttypeId();
        String allocationTypeId = dashExpResquest.getAllocationTypeId();
        AllocationType allockData = allocationRepository.findByAllocTypeId(allocationTypeId);
        AmountUnit hdamtUnits = amountUnitRepository.findByAmountTypeId(amounttypeId);
        double reqAmount = hdamtUnits.getAmount();
        try {

            boolean headunit;
            List<CgUnit> subUnit = cgUnitRepository.findByBudGroupUnitLike("%" + hrData.getUnitId() + "%");
            if (subUnit.size() == 0) {
                headunit = false;
            } else {
                headunit = true;
            }
            List<BudgetAllocation> budgetAllocToUnit;
            if (headunit == true) {
                budgetAllocToUnit = budgetAllocationRepository.findBySubHeadAndFromUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(subHeadId, hrData.getUnitId(), finYearId, allocationTypeId, "0", "0", "Approved");
            } else {
                budgetAllocToUnit = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(subHeadId, hrData.getUnitId(), finYearId, allocationTypeId, "0", "0", "Approved");
            }
/*            if (headunit == true) {
                List<BudgetAllocation> budgetAllocToUnits = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(subHeadId, hrData.getUnitId(), finYearId, allocationTypeId, "0", "0", "Approved");
                budgetAllocToUnit.addAll(budgetAllocToUnits);
            }*/
            double sumAlloc = 0.0;
            double sumExp = 0.0;
            double sumBal = 0.0;
            SubHeadWiseExpResp obj = new SubHeadWiseExpResp();
            List<GrTotalObj> grResp = new ArrayList<GrTotalObj>();
            if (budgetAllocToUnit.size() > 0) {
                for (int j = 0; j < budgetAllocToUnit.size(); j++) {
                    String uid = budgetAllocToUnit.get(j).getToUnit();
                    List<CgUnit> unitList1 = cgUnitRepository.findByBudGroupUnitLike("%" + uid + "%");
                    List<CgUnit> unitList=unitList1.stream().filter(e->!e.getUnit().equalsIgnoreCase(uid)).collect(Collectors.toList());

//                    double totalCdaSub = 0.0;
                    double remCdaBalSub = 0.0;
                    //double rqUnitSub = 0.0;
                    for (CgUnit unitS : unitList) {
                        String subUnits = unitS.getUnit();
                        List<CdaParkingTrans> cdaDetailSub = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYearId, subHeadId, subUnits, allocationTypeId, "0");
                        for (int k = 0; k < cdaDetailSub.size(); k++) {
                            AmountUnit hdamtUnit = amountUnitRepository.findByAmountTypeId(cdaDetailSub.get(0).getAmountType());
                            double rqUnitSub = hdamtUnit.getAmount();
//                            if (cdaDetailSub.get(k).getTotalParkingAmount() == null) {
//                                totalCdaSub+=0.0;
//                            }else{
//                                totalCdaSub += Double.parseDouble(cdaDetailSub.get(k).getTotalParkingAmount());
//                            }
                            if (cdaDetailSub.get(k).getRemainingCdaAmount() == null) {
                                remCdaBalSub=0.0;
                            }else{
                                double remcda=Double.parseDouble(cdaDetailSub.get(k).getRemainingCdaAmount());
                                remCdaBalSub += remcda * rqUnitSub;
                            }

                        }
                    }
                    //double cdaTotalSub = totalCdaSub * rqUnitSub / reqAmount;
                    double cdaRmingSub = remCdaBalSub/ reqAmount;

                    double remCdaBal = 0.0;
                    //double cdaUnit = 0.0;
                    List<CdaParkingTrans> cdaDetail = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYearId, subHeadId, uid, allocationTypeId, "0");
                    if (cdaDetail.size() > 0) {
                        for (int k = 0; k < cdaDetail.size(); k++) {
                            AmountUnit hdamtUnit = amountUnitRepository.findByAmountTypeId(cdaDetail.get(0).getAmountType());
                            double cdaUnit = hdamtUnit.getAmount();

                            if (cdaDetail.get(k).getRemainingCdaAmount() == null) {
                                remCdaBal=0.0;
                            }else{
                                double remCd=Double.parseDouble(cdaDetail.get(k).getRemainingCdaAmount());
                                remCdaBal += remCd * cdaUnit;
                            }
                        }
                    }
                    double cdaRmingAmunt = remCdaBal/ reqAmount;

                    double sumCdaRemaining=cdaRmingSub+cdaRmingAmunt;
                    String cbD = "";
                    double totalbill = 0.0;
                    Timestamp lastCvDate;
                    if (unitList.size() > 0) {
                        for (CgUnit unitss : unitList) {
                            String subUnits = unitss.getUnit();
                            List<ContigentBill> expenditure = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdate(subUnits, finYearId, subHeadId,allocationTypeId, "0");

                            if (expenditure.size() > 0) {
                                double totalAmount = 0.0;
                                for (ContigentBill bill : expenditure) {
                                    totalAmount += Double.parseDouble(bill.getCbAmount());
                                    if (bill.getCbDate() != null) {
                                        lastCvDate = bill.getCbDate();
                                        SimpleDateFormat id = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                                        SimpleDateFormat od = new SimpleDateFormat("dd-MMMM-yyyy");
                                        Date dateC = id.parse(lastCvDate.toString());
                                        cbD = od.format(dateC);
                                    } else
                                        cbD = "";
                                }
                                totalbill += totalAmount;
                            }
                        }
                    }
                    List<ContigentBill> expenditure = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdateAndIsFlag(uid, finYearId, subHeadId, allocationTypeId, "0", "0");
                    double totalExpAmount = 0.0;
                    if (expenditure.size() > 0) {
                        for (ContigentBill bill : expenditure) {
                            totalExpAmount += Double.parseDouble(bill.getCbAmount());
                            if (bill.getCbDate() != null) {
                                lastCvDate = bill.getCbDate();
                                SimpleDateFormat id = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                                SimpleDateFormat od = new SimpleDateFormat("dd-MMMM-yyyy");
                                Date dateC = id.parse(lastCvDate.toString());
                                cbD = od.format(dateC);
                            } else
                                cbD = "";
                        }
                    }
                    double expAmount = (totalExpAmount+totalbill) / reqAmount;

                    //double expAmnt=0.0;
                    //double alocAmnt = 0.0;
                    //double sumCdaRemainingBal=0.0;
                    AmountUnit alloc = amountUnitRepository.findByAmountTypeId(budgetAllocToUnit.get(j).getAmountType());
                    double allocAmntUnit = alloc.getAmount();
                    double alocAmnts = Double.parseDouble(budgetAllocToUnit.get(j).getAllocationAmount());
                    double alocAmnt = alocAmnts * allocAmntUnit / reqAmount;
                    double expAmnt=expAmount;
                    double sumCdaRemainingBal=sumCdaRemaining;

/*                    if (headunit == true && hrData.getUnitId().equalsIgnoreCase(uid)) {
                        expAmnt=totalExpAmount/ reqAmount;
                        alocAmnt = cdaRmingAmunt + (totalExpAmount/ reqAmount);
                        sumCdaRemainingBal=cdaRmingAmunt;
                        if(expAmnt ==0)
                            cbD="";
                    } else {
                        double alocAmnts = Double.parseDouble(budgetAllocToUnit.get(j).getAllocationAmount());
                        alocAmnt = alocAmnts * allocAmntUnit / reqAmount;
                        expAmnt=expAmount;
                        sumCdaRemainingBal=sumCdaRemaining;
                    }*/

                    double perAmnt = 0.0;
                    if (alocAmnt != 0) {
                        perAmnt = (expAmnt * 100) / alocAmnt;
                    } else {
                        perAmnt = 0.0;
                    }

                    CgUnit cgUnit = cgUnitRepository.findByUnit(uid);
                    GrTotalObj subResp = new GrTotalObj();
                    subResp.setUnitName(cgUnit.getDescr());
                    subResp.setFinYear(budgetFinancialYear.getFinYear());
                    subResp.setAllocType(allockData.getAllocDesc());
                    subResp.setAmountIn(hdamtUnits.getAmountType());
                    subResp.setAllocatedAmount(String.format("%1$0,1.4f", alocAmnt));
                    subResp.setExpenditureAmount(String.format("%1$0,1.4f", expAmnt));
                    subResp.setBalAmount(String.format("%1$0,1.4f", sumCdaRemainingBal));
                    subResp.setPerAmount(String.format("%1$0,1.2f", perAmnt));
                    subResp.setLastCBDate(cbD);
                    grResp.add(subResp);

                    sumAlloc += alocAmnt;
                    sumExp += expAmnt;
                    sumBal += sumCdaRemainingBal;
                }


            }
            double grPer = 0.0;
            if (sumAlloc != 0) {
                grPer = (sumExp * 100) / sumAlloc;
            } else {
                grPer = 0.0;
            }
            obj.setGrTotalObj(grResp);
            obj.setSumAlloc(ConverterUtils.addDecimalPoint(sumAlloc + ""));
            obj.setSumExp(ConverterUtils.addDecimalPoint(sumExp + ""));
            obj.setSumBal(ConverterUtils.addDecimalPoint(sumBal + ""));
            obj.setPerBal(ConverterUtils.addDecimal2Point(grPer + ""));
            resp.add(obj);

            return ResponseUtils.createSuccessResponse(resp, new TypeReference<List<SubHeadWiseExpResp>>() {
            });
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

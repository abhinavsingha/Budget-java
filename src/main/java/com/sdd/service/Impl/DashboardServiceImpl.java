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
import com.sdd.response.*;
import com.sdd.service.DashBoardService;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        List<BudgetAllocationDetails> budgetList = new ArrayList<>();

        if (hrDataCheck == null) {
            hrDataCheck = getAuthorization(currentLoggedInUser.getPreferred_username());
            if (hrDataCheck == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.");
            }
        }

        if (dashBoardRequest.getToUnitId() == null || dashBoardRequest.getToUnitId().isEmpty()) {
            budgetList = budgetAllocationDetailsRepository.findAll();
        } else {
            CgUnit cgFromUnit = cgUnitRepository.findByUnit(dashBoardRequest.getToUnitId());
            if (cgFromUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
            } else {
                budgetList =
                        budgetAllocationDetailsRepository.findByToUnitAndIsDelete(
                                dashBoardRequest.getToUnitId(), "0");
            }
        }

        if (dashBoardRequest.getBudgetFinancialYearId() == null
                || dashBoardRequest.getBudgetFinancialYearId().isEmpty()) {

        } else {
            BudgetFinancialYear budgetFinancialYear =
                    budgetFinancialYearRepository.findBySerialNo(dashBoardRequest.getBudgetFinancialYearId());
            if (budgetFinancialYear == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
            } else {
                budgetList =
                        budgetAllocationDetailsRepository.findByToUnitAndFinYearAndIsDelete(
                                dashBoardRequest.getToUnitId(), dashBoardRequest.getBudgetFinancialYearId(), "0");
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

        CurrntStateType stateList = currentStateRepository.findByIsFlag("1");
        if (stateList == null) {
            AllocationType allocationType = allocationRepository.findByAllocTypeId("ALL_101");
            dashBoardResponse.setAllocationType(allocationType);
        } else {
            AllocationType allocationType =
                    allocationRepository.findByAllocTypeId(stateList.getStateId());
            dashBoardResponse.setAllocationType(allocationType);
        }

        dashBoardResponse.setUserDetails(hradataResponse);

        List<MangeInboxOutbox> inboxOutboxesList = new ArrayList<MangeInboxOutbox>();

        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {

        }

        List<InboxOutBoxSubResponse> inboxList = new ArrayList<InboxOutBoxSubResponse>();
        List<InboxOutBoxSubResponse> outBoxList = new ArrayList<InboxOutBoxSubResponse>();

        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER)) {

            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "BG");
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
            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgOrIsBgcgOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "BG", "BR");
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
            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB");
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
            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB");
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
        } else if (getCurrentRole.contains(HelperUtils.CBVERIFER)) {
            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB");
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

        HashMap<String, CgUnit> cgunitData = new LinkedHashMap<>();
        HashMap<String, BudgetHead> subHeadData = new LinkedHashMap<>();

        List<BudgetAllocation> budgetAllocation =
                budgetAllocationRepository.findByToUnitAndIsFlag(hrDataCheck.getUnitId(), "1");

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

        UnitWiseExpenditueResponse unitWiseExpenditueResponse = new UnitWiseExpenditueResponse();
        List<String> unit = new ArrayList<String>();
        List<String> unitByAllocationAmount = new ArrayList<String>();
        List<String> unitByExpendureAmount = new ArrayList<String>();

        for (Map.Entry<String, CgUnit> entry : cgunitData.entrySet()) {
            CgUnit unitData = entry.getValue();

            List<BudgetAllocation> budgetAllocationList =
                    budgetAllocationRepository.findByToUnitAndFinYearAndIsFlag(unitData.getUnit(), "01", "1");
            double allocationAmount = 0;
            for (int i = 0; i < budgetAllocationList.size(); i++) {
                allocationAmount =
                        allocationAmount
                                + Double.parseDouble(budgetAllocationList.get(i).getAllocationAmount());
            }

            double expenditure = 0;
            List<ContigentBill> cbExpendure =
                    contigentBillRepository.findByCbUnitIdAndIsFlag(unitData.getUnit(), "0");

            for (Integer i = 0; i < cbExpendure.size(); i++) {
                expenditure = expenditure + Double.parseDouble(cbExpendure.get(i).getCbAmount());
            }

            unit.add(unitData.getCgUnitShort());
            unitByAllocationAmount.add(allocationAmount + "");
            unitByExpendureAmount.add(expenditure + "");
        }

        unitWiseExpenditueResponse.setUnitWise(unit);
        unitWiseExpenditueResponse.setExpenditureUnit(unitByAllocationAmount);
        unitWiseExpenditueResponse.setAllocatedUnit(unitByExpendureAmount);

        dashBoardResponse.setUnitWiseExpenditure(unitWiseExpenditueResponse);

        List<String> subHeadWise = new ArrayList<String>();

        SubHeadWiseExpenditueResponse subHeadWiseExpenditueResponse =
                new SubHeadWiseExpenditueResponse();
        List<String> subhead = new ArrayList<String>();
        List<String> allocatedSubHead = new ArrayList<String>();
        List<String> expenditureSubHead = new ArrayList<String>();

        for (Map.Entry<String, BudgetHead> entry : subHeadData.entrySet()) {
            String key = entry.getKey();
            BudgetHead subHeadD = entry.getValue();

            List<BudgetAllocation> budgetAllocationList =
                    budgetAllocationRepository.findByToUnitAndFinYearAndIsFlag(
                            hrDataCheck.getUnit(), "01", "1");
            double allocationAmount = 0;
            for (int i = 0; i < budgetAllocationList.size(); i++) {
                allocationAmount =
                        allocationAmount
                                + Double.parseDouble(budgetAllocationList.get(i).getAllocationAmount());
            }

            double expenditure = 0;
            List<ContigentBill> cbExpendure =
                    contigentBillRepository.findByBudgetHeadIDAndIsFlag(subHeadD.getBudgetCodeId(), "0");

            for (Integer i = 0; i < cbExpendure.size(); i++) {
                expenditure = expenditure + Double.parseDouble(cbExpendure.get(i).getCbAmount());
            }

            subHeadWise.add(subHeadD.getSubheadShort());
            allocatedSubHead.add(allocationAmount + "");
            expenditureSubHead.add(expenditure + "");
        }

        subHeadWiseExpenditueResponse.setSubhead(subHeadWise);
        subHeadWiseExpenditueResponse.setAllocatedSubHead(allocatedSubHead);
        subHeadWiseExpenditueResponse.setExpenditureSubHead(expenditureSubHead);

        dashBoardResponse.setSubHeadWiseExpenditure(subHeadWiseExpenditueResponse);

        List<DashBoardSubResponse> dashBoardList = new ArrayList<DashBoardSubResponse>();
        for (int i = 0; i < budgetList.size(); i++) {
            DashBoardSubResponse dashBoardData = new DashBoardSubResponse();

            dashBoardData.setLastCBDate(budgetList.get(i).getAllocationDate());
            dashBoardData.setAllocatedAmount(budgetList.get(i).getAllocationAmount());
            dashBoardData.setFinancialYearId(
                    budgetFinancialYearRepository.findBySerialNo(budgetList.get(i).getFinYear()));
            dashBoardData.setSubHead(
                    subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(
                            budgetList.get(i).getSubHead()));
            dashBoardData.setUnit(cgUnitRepository.findByUnit(budgetList.get(i).getToUnit()));
            dashBoardData.setStatus(budgetList.get(i).getStatus());
            dashBoardData.setAuthGroupId(budgetList.get(i).getAuthGroupId());
            dashBoardList.add(dashBoardData);
        }

        dashBoardResponse.setInbox(inboxList.size() + "");
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

        CurrntStateType stateList = currentStateRepository.findByIsFlag("1");
        if (stateList == null) {
            AllocationType allocationType = allocationRepository.findByAllocTypeId("ALL_101");
            dashBoardResponse.setAllocationType(allocationType);
        } else {
            AllocationType allocationType =
                    allocationRepository.findByAllocTypeId(stateList.getStateId());
            dashBoardResponse.setAllocationType(allocationType);
        }
        dashBoardResponse.setUserDetails(hradataResponse);
        List<MangeInboxOutbox> inboxOutboxesList = new ArrayList<MangeInboxOutbox>();
        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {
        }
        List<InboxOutBoxSubResponse> inboxList = new ArrayList<InboxOutBoxSubResponse>();
        List<InboxOutBoxSubResponse> outBoxList = new ArrayList<InboxOutBoxSubResponse>();

        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER)) {

            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "BG");
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
            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgOrIsBgcgOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "BG", "BR");
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
            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB");
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
            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB");
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
            inboxOutboxesList =
                    mangeInboxOutBoxRepository.findByToUnitAndIsBgcgOrderByCreatedOnDesc(
                            hrDataCheck.getUnitId(), "CB");
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
        return ResponseUtils.createSuccessResponse(
                dashBoardResponse, new TypeReference<DashBoardResponse>() {
                });
    }

    @Override
    public ApiResponse<List<DashBoardExprnditureResponse>>
    getSubHeadWiseExpenditureByUnitIdFinYearIdAllocationTypeIdSubHeadTypeId(
            String unitId, String finYearId, String subHeadTypeId, String allocationTypeId) {
        try {
            List<DashBoardExprnditureResponse> dashBoardExprnditureResponseList = new ArrayList<>();

            BudgetFinancialYear budgetFinancialYear =
                    budgetFinancialYearRepository.findBySerialNo(finYearId);
            CgUnit cgUnit = cgUnitRepository.findByUnit(unitId);

            List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike(unitId);
            List<String> unitIds = new ArrayList<>();
            for (CgUnit cgUnit1 : unitList) {
                unitIds.add(cgUnit1.getUnit());
            }

            List<BudgetHead> budgetHeadList =
                    subHeadRepository.findBySubHeadTypeIdOrderBySerialNumberAsc(subHeadTypeId);

            for (BudgetHead budgetHead : budgetHeadList) {
                DashBoardExprnditureResponse dashBoardExprnditureResponse =
                        new DashBoardExprnditureResponse();
                dashBoardExprnditureResponse.setBudgetFinancialYear(budgetFinancialYear);
                dashBoardExprnditureResponse.setCgUnit(cgUnit);
                dashBoardExprnditureResponse.setBudgetHead(budgetHead);

                List<BudgetAllocation> budgetAllocationList =
                        budgetAllocationRepository
                                .findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(
                                        unitId,
                                        finYearId,
                                        budgetHead.getBudgetCodeId(),
                                        allocationTypeId,
                                        "Approved",
                                        "1");

                if (budgetAllocationList.size() > 0) {
                    dashBoardExprnditureResponse.setAllocatedAmount(
                            budgetAllocationList.get(0).getAllocationAmount());
                } else {
                    dashBoardExprnditureResponse.setAllocatedAmount(0.0 + "");
                }

                List<ContigentBill> contigentBillList =
                        contigentBillRepository
                                .findByCbUnitIdInAndFinYearAndBudgetHeadIDAndAllocationIdOrderByCbDateDesc(
                                        unitIds, finYearId, budgetHead.getBudgetCodeId(), allocationTypeId);

                if (contigentBillList.size() > 0) {
                    Double amount = 0.0;
                    for (ContigentBill contigentBill : contigentBillList) {
                        amount = amount + Double.parseDouble(contigentBill.getCbAmount());
                    }
                    dashBoardExprnditureResponse.setExpenditureAmount(amount + "");
                    dashBoardExprnditureResponse.setLastCBDate(contigentBillList.get(0).getCbDate() + "");
                    dashBoardExprnditureResponseList.add(dashBoardExprnditureResponse);
                } else {
                    dashBoardExprnditureResponse.setExpenditureAmount(0.0 + "");
                    dashBoardExprnditureResponse.setLastCBDate("");
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

        //        return null;
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

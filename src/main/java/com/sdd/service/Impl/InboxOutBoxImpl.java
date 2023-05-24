package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.*;
//import com.sdd.entities.MessageTran;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.request.RebaseBudgetHistory;
import com.sdd.response.*;
import com.sdd.service.InboxOutBoxService;
import com.sdd.service.MangeUserService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class InboxOutBoxImpl implements InboxOutBoxService {

    @Autowired
    HrDataRepository hrDataRepository;

    @Autowired
    CurrentStateRepository currentStateRepository;


    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CgUnitRepository cgUnitRepository;

    @Autowired
    MangeInboxOutBoxRepository mangeInboxOutBoxRepository;

    @Autowired
    AllocationRepository allocationRepository;

    @Autowired
    BudgetFinancialYearRepository budgetFinancialYearRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HeaderUtils headerUtils;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    private BudgetAllocationDetailsRepository budgetAllocationDetailsRepository;

    @Autowired
    private AmountUnitRepository amountUnitRepository;

    @Autowired
    private SubHeadRepository subHeadRepository;

    @Autowired
    private BudgetAllocationRepository budgetAllocationRepository;


    @Override
    public ApiResponse<InboxOutBoxResponse> getInboxList() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        InboxOutBoxResponse inboxOutBoxResponse = new InboxOutBoxResponse();

        List<InboxOutBoxSubResponse> inboxList = new ArrayList<InboxOutBoxSubResponse>();
        List<InboxOutBoxSubResponse> approvedList = new ArrayList<InboxOutBoxSubResponse>();
        List<InboxOutBoxSubResponse> outBoxList = new ArrayList<InboxOutBoxSubResponse>();
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }


        List<MangeInboxOutbox> inboxOutboxesList = new ArrayList<MangeInboxOutbox>();

        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {

        }

        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER)) {

            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "BG", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("AP")) {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    data.setGroupId(mangeInboxOutbox.getGroupId());
                    data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                    data.setRemarks(mangeInboxOutbox.getRemarks());
                    data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                    data.setAmount(mangeInboxOutbox.getAmount());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    inboxList.add(data);

                } else {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    data.setGroupId(mangeInboxOutbox.getGroupId());
                    data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                    data.setRemarks(mangeInboxOutbox.getRemarks());
                    data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                    data.setAmount(mangeInboxOutbox.getAmount());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    outBoxList.add(data);
                }

            }
        } else if (getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {
            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "BG", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getToUnit().equalsIgnoreCase(hrDataCheck.getUnitId())) {

                    if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {
                        InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                        MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                        data.setGroupId(mangeInboxOutbox.getGroupId());
                        data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                        data.setRemarks(mangeInboxOutbox.getRemarks());
                        data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                        data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                        data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                        data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                        data.setAmount(mangeInboxOutbox.getAmount());
                        data.setType(mangeInboxOutbox.getType());
                        data.setStatus(mangeInboxOutbox.getStatus());
                        data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                        inboxList.add(data);

                    } else {
                        InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                        MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                        data.setGroupId(mangeInboxOutbox.getGroupId());
                        data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                        data.setRemarks(mangeInboxOutbox.getRemarks());
                        data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                        data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                        data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                        data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                        data.setAmount(mangeInboxOutbox.getAmount());
                        data.setType(mangeInboxOutbox.getType());
                        data.setStatus(mangeInboxOutbox.getStatus());
                        data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                        outBoxList.add(data);
                    }

                }

            }
        } else if (getCurrentRole.contains(HelperUtils.CBCREATER)) {
            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    data.setGroupId(mangeInboxOutbox.getGroupId());
                    data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                    data.setRemarks(mangeInboxOutbox.getRemarks());
                    data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                    data.setAmount(mangeInboxOutbox.getAmount());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    inboxList.add(data);

                } else {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    data.setGroupId(mangeInboxOutbox.getGroupId());
                    data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                    data.setRemarks(mangeInboxOutbox.getRemarks());
                    data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                    data.setAmount(mangeInboxOutbox.getAmount());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    outBoxList.add(data);
                }

            }
        } else if (getCurrentRole.contains(HelperUtils.CBVERIFER)) {
            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("VE")) {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    data.setGroupId(mangeInboxOutbox.getGroupId());
                    data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                    data.setRemarks(mangeInboxOutbox.getRemarks());
                    data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                    data.setAmount(mangeInboxOutbox.getAmount());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    inboxList.add(data);

                } else {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    data.setGroupId(mangeInboxOutbox.getGroupId());
                    data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                    data.setRemarks(mangeInboxOutbox.getRemarks());
                    data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                    data.setAmount(mangeInboxOutbox.getAmount());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    outBoxList.add(data);
                }

            }
        } else if (getCurrentRole.contains(HelperUtils.CBAPPROVER)) {
            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("AP")) {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    data.setGroupId(mangeInboxOutbox.getGroupId());
                    data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                    data.setRemarks(mangeInboxOutbox.getRemarks());
                    data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                    data.setAmount(mangeInboxOutbox.getAmount());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    inboxList.add(data);

                } else {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    data.setGroupId(mangeInboxOutbox.getGroupId());
                    data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                    data.setRemarks(mangeInboxOutbox.getRemarks());
                    data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                    data.setAmount(mangeInboxOutbox.getAmount());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    outBoxList.add(data);
                }

            }
        }


        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
        if (allocationType.size() > 0) {
            inboxOutBoxResponse.setAllocationType(allocationType.get(0));
        }

        CurrntStateType stateList1 = currentStateRepository.findByTypeAndIsFlag("FINYEAR", "1");
        if (stateList1 == null) {
            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo("01");
            inboxOutBoxResponse.setBudgetFinancialYear(budgetFinancialYear);
        } else {
            BudgetFinancialYear budgetFinancialYear =
                    budgetFinancialYearRepository.findBySerialNo(stateList1.getStateId());
            inboxOutBoxResponse.setBudgetFinancialYear(budgetFinancialYear);
        }


        List<MangeInboxOutbox> approvedListUnitWise = mangeInboxOutBoxRepository.findByToUnitAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1");
        for (Integer i = 0; i < approvedListUnitWise.size(); i++) {
            InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
            MangeInboxOutbox mangeInboxOutbox = approvedListUnitWise.get(i);

            data.setGroupId(mangeInboxOutbox.getGroupId());
            data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
            data.setRemarks(mangeInboxOutbox.getRemarks());
            data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
            data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
            data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
            data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
            data.setAmount(mangeInboxOutbox.getAmount());
            data.setType(mangeInboxOutbox.getType());
            data.setStatus(mangeInboxOutbox.getStatus());
            data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

            approvedList.add(data);
        }
        inboxOutBoxResponse.setApprovedList(approvedList);
        inboxOutBoxResponse.setInboxList(inboxList);
        inboxOutBoxResponse.setOutList(outBoxList);


        return ResponseUtils.createSuccessResponse(inboxOutBoxResponse, new TypeReference<InboxOutBoxResponse>() {
        });
    }

    @Override
    public ApiResponse<List<InboxOutBoxResponse>> getOutBoxList() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        List<InboxOutBoxResponse> defaultResponse = new ArrayList<InboxOutBoxResponse>();
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.");
        }


        List<MangeInboxOutbox> inboxOutboxesList = new ArrayList<MangeInboxOutbox>();

        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {

        }

        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER)) {
            inboxOutboxesList = mangeInboxOutBoxRepository.findByFromUnitAndIsBgcgOrderByCreatedOnAsc(hrDataCheck.getUnitId(), "BG");

        } else if (getCurrentRole.contains(HelperUtils.CBAPPROVER)) {
            inboxOutboxesList = mangeInboxOutBoxRepository.findByFromUnitAndIsBgcgOrderByCreatedOnAsc(hrDataCheck.getUnitId(), "CB");

        } else if (getCurrentRole.contains(HelperUtils.CBVERIFER)) {
            inboxOutboxesList = mangeInboxOutBoxRepository.findByFromUnitAndIsBgcgOrderByCreatedOnAsc(hrDataCheck.getUnitId(), "CB");

        } else if (getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {
            inboxOutboxesList = mangeInboxOutBoxRepository.findByFromUnitAndIsBgcgOrderByCreatedOnAsc(hrDataCheck.getUnitId(), "BG");

        } else if (getCurrentRole.contains(HelperUtils.CBCREATER) || getCurrentRole.contains(HelperUtils.CBCREATER)) {
            inboxOutboxesList = mangeInboxOutBoxRepository.findByFromUnitAndIsBgcgOrderByCreatedOnAsc(hrDataCheck.getUnitId(), "CB");

        }


//        for (Integer i = 0; i < inboxOutboxesList.size(); i++) {
//            MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
//
//            InboxOutBoxResponse data = new InboxOutBoxResponse();
//            data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
//            data.setGroupId(mangeInboxOutbox.getGroupId());
//            data.setAmount(mangeInboxOutbox.getAmount());
//                    data.setType(mangeInboxOutbox.getType());
//            data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));
//            data.setRemarks(mangeInboxOutbox.getRemarks());
//            data.setStatus(mangeInboxOutbox.getStatus());
//            data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
//            data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
//            data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
//            data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
//
//
//            defaultResponse.add(data);
//
//
//        }

        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<List<InboxOutBoxResponse>>() {
        });
    }

    @Override
    public ApiResponse<InboxOutBoxResponse> readMessage(String msgId) {
        InboxOutBoxResponse defaultResponse = new InboxOutBoxResponse();
        if (msgId == null || msgId.isEmpty()) {

        }
        MangeInboxOutbox inboxOutboxesList = mangeInboxOutBoxRepository.findByMangeInboxId(msgId);
        if (inboxOutboxesList != null) {
            inboxOutboxesList.setStatus("0");
            mangeInboxOutBoxRepository.save(inboxOutboxesList);
        }


//        defaultResponse.setMsg("Message update successfully");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<InboxOutBoxResponse>() {
        });
    }

    @Override
    public ApiResponse<List<ApprovedResponse>> getApprovedList() {
        List<ApprovedResponse> responce = new ArrayList<ApprovedResponse>();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }
        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {
        }
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        if (month < 4) {
            year--;
        }
        String financialYear = String.format("%d-%02d", year, (year + 1) % 100);
        String[] parts = financialYear.split("-");
        int sysFinyr = Integer.parseInt(parts[1]);

/*        CgUnit cgUnit = cgUnitRepository.findByUnit(hrDataCheck.getUnitId());
        if (cgUnit == null) {
            return ResponseUtils.createFailureResponse(responce,  new TypeReference<List<ApprovedResponse>>() {
            }, "USER UNIT IS INVALID.PLEASE CHECK", HttpStatus.OK.value());
        }
        String dBunit = cgUnit.getDescr();
        List<CgUnit> units = new ArrayList<>();
        if (dBunit.equalsIgnoreCase("D(Budget)")) {
            units = cgUnitRepository.findAllByOrderByDescrAsc();
        } else {
            if (hrDataCheck.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
                units = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());
            } else {
                units = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getUnit());
            }
        }
        if (units.size() <= 0) {
            return ResponseUtils.createFailureResponse(responce,  new TypeReference<List<ApprovedResponse>>() {
            }, "UNIT NOT FOUND", HttpStatus.OK.value());
        }*/


        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER) || getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {
            List<MangeInboxOutbox> inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndStatus(hrDataCheck.getUnitId(), "BG", "Fully Approved");
            if (inboxOutboxesList.size() > 0) {
                for (Integer i = 0; i < inboxOutboxesList.size(); i++) {
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    String authGId=mangeInboxOutbox.getGroupId();
                    List<BudgetAllocation> allocationData = budgetAllocationRepository.findByAuthGroupIdAndIsDelete(authGId, "0");                    if (allocationData.size() > 0) {
                        BudgetFinancialYear finYear = budgetFinancialYearRepository.findBySerialNo(allocationData.get(0).getFinYear());
                        String[] part = finYear.getFinYear().split("-");
                        int dbFinyr = Integer.parseInt(part[1]);
                        if (financialYear.equalsIgnoreCase(finYear.getFinYear()) || sysFinyr < dbFinyr) {
                            ApprovedResponse data = new ApprovedResponse();
                            data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));
                            data.setType(mangeInboxOutbox.getType());
                            data.setSubmissionDate(mangeInboxOutbox.getCreatedOn());
                            data.setApprovedDate(mangeInboxOutbox.getUpdatedOn());
                            data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                            data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                            data.setStatus(mangeInboxOutbox.getStatus());
                            data.setRemarks(mangeInboxOutbox.getRemarks());
                            data.setGroupId(mangeInboxOutbox.getGroupId());
                            data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                            data.setAmount(mangeInboxOutbox.getAmount());
                            responce.add(data);
                        }
                    }
                }
            }
        }
        return ResponseUtils.createSuccessResponse(responce, new TypeReference<List<ApprovedResponse>>() {
        });
    }

    @Override
    public ApiResponse<List<ApprovedResponse>> getArchivedList() {
        List<ApprovedResponse> responce = new ArrayList<ApprovedResponse>();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }
        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {
        }
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        if (month < 4) {
            year--;
        }
        String financialYear = String.format("%d-%02d", year, (year + 1) % 100);
        String[] parts = financialYear.split("-");
        int sysFinyr = Integer.parseInt(parts[1]);

/*        CgUnit cgUnit = cgUnitRepository.findByUnit(hrDataCheck.getUnitId());
        if (cgUnit == null) {
            return ResponseUtils.createFailureResponse(responce,  new TypeReference<List<ApprovedResponse>>() {
            }, "USER UNIT IS INVALID.PLEASE CHECK", HttpStatus.OK.value());
        }
        String dBunit = cgUnit.getDescr();
        List<CgUnit> units = new ArrayList<>();
        if (dBunit.equalsIgnoreCase("D(Budget)")) {
            units = cgUnitRepository.findAllByOrderByDescrAsc();
        } else {
            if (hrDataCheck.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
                units = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());
            } else {
                units = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getUnit());
            }
        }
        if (units.size() <= 0) {
            return ResponseUtils.createFailureResponse(responce,  new TypeReference<List<ApprovedResponse>>() {
            }, "UNIT NOT FOUND", HttpStatus.OK.value());
        }*/


        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER) || getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {
            List<MangeInboxOutbox> inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndStatus(hrDataCheck.getUnitId(), "BG", "Fully Approved");
            if (inboxOutboxesList.size() > 0) {
                for (Integer i = 0; i < inboxOutboxesList.size(); i++) {
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    String authGId=mangeInboxOutbox.getGroupId();
                    List<BudgetAllocation> allocationData = budgetAllocationRepository.findByAuthGroupIdAndIsDelete(authGId, "0");                    if (allocationData.size() > 0) {
                        BudgetFinancialYear finYear = budgetFinancialYearRepository.findBySerialNo(allocationData.get(0).getFinYear());
                        String[] part = finYear.getFinYear().split("-");
                        int dbFinyr = Integer.parseInt(part[1]);
                        if (!financialYear.equalsIgnoreCase(finYear.getFinYear()) || sysFinyr > dbFinyr) {
                            ApprovedResponse data = new ApprovedResponse();
                            data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));
                            data.setType(mangeInboxOutbox.getType());
                            data.setSubmissionDate(mangeInboxOutbox.getCreatedOn());
                            data.setApprovedDate(mangeInboxOutbox.getUpdatedOn());
                            data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                            data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                            data.setStatus(mangeInboxOutbox.getStatus());
                            data.setRemarks(mangeInboxOutbox.getRemarks());
                            data.setGroupId(mangeInboxOutbox.getGroupId());
                            data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                            data.setAmount(mangeInboxOutbox.getAmount());
                            responce.add(data);
                        }
                    }
                }
            }
        }
        return ResponseUtils.createSuccessResponse(responce, new TypeReference<List<ApprovedResponse>>() {
        });
    }

    @Override
    public ApiResponse<List<ArchivedResponse>> getApprovedListData(String groupId) {
        List<ArchivedResponse> responce = new ArrayList<ArchivedResponse>();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }
        if (groupId == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GROUP ID CAN NOT BE BLANK OR EMPTY");
        }
        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {
        }
        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER) || getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {
            List<BudgetAllocation> inboxOutboxesList = budgetAllocationRepository.findByAuthGroupIdAndIsDelete(groupId, "0");
            if (inboxOutboxesList.size() > 0) {
                for (Integer i = 0; i < inboxOutboxesList.size(); i++) {
                    BudgetAllocation mangeInboxOutbox = inboxOutboxesList.get(i);
                    ArchivedResponse data = new ArchivedResponse();
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationTypeId()));
                    data.setFinancialYear(budgetFinancialYearRepository.findBySerialNo(mangeInboxOutbox.getFinYear()));
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    //data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setAllocationAmount(mangeInboxOutbox.getAllocationAmount());
                    data.setBalAmount("0.0000");
                    data.setApprovedAmount("0.0000");
                    data.setReceiptAmount("0.0000");
                    data.setAmountType(amountUnitRepository.findByAmountTypeId(mangeInboxOutbox.getAmountType()));
                    data.setStatus(mangeInboxOutbox.getStatus());
                    //data.setRemarks(mangeInboxOutbox.getRemarks());
                    data.setSubmissionDate(mangeInboxOutbox.getCreatedOn());
                    data.setApprovedDate(mangeInboxOutbox.getUpdatedOn());
                    data.setGroupId(mangeInboxOutbox.getAuthGroupId());
                    data.setBudgetHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(mangeInboxOutbox.getSubHead()));
                    responce.add(data);
                }
            }
        }
        return ResponseUtils.createSuccessResponse(responce, new TypeReference<List<ArchivedResponse>>() {
        });
    }

}

package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.*;
//import com.sdd.entities.MessageTran;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.request.CdaParkingCrAndDrResponse;
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
    CdaParkingTransRepository cdaParkingTransRepository;

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

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }

        InboxOutBoxResponse inboxOutBoxResponse = new InboxOutBoxResponse();

        List<InboxOutBoxSubResponse> inboxList = new ArrayList<InboxOutBoxSubResponse>();
        List<InboxOutBoxSubResponse> approvedList = new ArrayList<InboxOutBoxSubResponse>();
        List<InboxOutBoxSubResponse> archivedList = new ArrayList<InboxOutBoxSubResponse>();
        List<InboxOutBoxSubResponse> outBoxList = new ArrayList<InboxOutBoxSubResponse>();
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }


        List<MangeInboxOutbox> inboxOutboxesList = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> archiveMain = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> approvedMain = new ArrayList<MangeInboxOutbox>();

        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {

        }

        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER)) {
            approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));
            archiveMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));


            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");
            dataIscgBg.add("RR");

            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0", "0", dataIscgBg);


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
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {
                        List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());
                        Boolean isCda = true;
                        for (Integer m = 0; m < budgetAllocations.size(); m++) {

                            BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                            if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                                continue;
                            }
                            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                            if (cdaParkingList.size() > 0) {
                                data.setIsCda(isCda);
                            } else {
                                isCda = false;
                                data.setIsCda(isCda);
                            }
                            data.setBudgetAllocation(budgetAllocationSubReport);
                        }
                        data.setIsCda(isCda);

                    }
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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {
                        List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());
                        Boolean isCda = true;
                        for (Integer m = 0; m < budgetAllocations.size(); m++) {

                            BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                            if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                                continue;
                            }
                            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                            if (cdaParkingList.size() > 0) {
                                data.setIsCda(isCda);
                            } else {
                                isCda = false;
                                data.setIsCda(isCda);
                            }
                            data.setBudgetAllocation(budgetAllocationSubReport);
                        }
                        data.setIsCda(isCda);
                    }
                    outBoxList.add(data);
                }

            }
        }
        else if (getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {
//            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "BG", "0", "0");

            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");
            dataIscgBg.add("RR");
            dataIscgBg.add("UR");
            dataIscgBg.add("CDA");
            dataIscgBg.add("SBG");
            dataIscgBg.add("CDAI");

            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0", "0", dataIscgBg);

            approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));
            archiveMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));


            for (MangeInboxOutbox inboxOutbox : inboxOutboxesList) {

                if (inboxOutbox.getToUnit().equalsIgnoreCase(hrDataCheck.getUnitId())) {

                    if (inboxOutbox.getState().equalsIgnoreCase("CR")) {
                        InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                        MangeInboxOutbox mangeInboxOutbox = inboxOutbox;
                        data.setGroupId(mangeInboxOutbox.getGroupId());
                        data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                        data.setRemarks(mangeInboxOutbox.getRemarks());
                        data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                        data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                        data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                        data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                        data.setAmount(mangeInboxOutbox.getAmount());
                        data.setType(mangeInboxOutbox.getType());
                        data.setIsRebase(mangeInboxOutbox.getIsRebase());
                        data.setIsRevision(mangeInboxOutbox.getIsRevision());
                        data.setStatus(mangeInboxOutbox.getStatus());
                        data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                        if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {

                            List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());
                            Boolean isCda = true;
                            for (Integer m = 0; m < budgetAllocations.size(); m++) {

                                BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                                if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                                    continue;
                                }

                                List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                                if (cdaParkingList.size() > 0) {
                                    data.setIsCda(isCda);
                                } else {
                                    isCda = false;
                                    data.setIsCda(isCda);
                                }
                                data.setBudgetAllocation(budgetAllocationSubReport);
                            }
                            data.setIsCda(isCda);
                        }
                        inboxList.add(data);

                    } else {
                        InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                        MangeInboxOutbox mangeInboxOutbox = inboxOutbox;
                        data.setGroupId(mangeInboxOutbox.getGroupId());
                        data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                        data.setRemarks(mangeInboxOutbox.getRemarks());
                        data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                        data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                        data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                        data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                        data.setAmount(mangeInboxOutbox.getAmount());
                        data.setType(mangeInboxOutbox.getType());
                        data.setIsRebase(mangeInboxOutbox.getIsRebase());
                        data.setIsRevision(mangeInboxOutbox.getIsRevision());
                        data.setStatus(mangeInboxOutbox.getStatus());

                        if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {

                            List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());
                            Boolean isCda = true;
                            for (Integer m = 0; m < budgetAllocations.size(); m++) {

                                BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                                if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                                    continue;
                                }

                                List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                                if (cdaParkingList.size() > 0) {
                                    data.setIsCda(isCda);
                                } else {
                                    isCda = false;
                                    data.setIsCda(isCda);
                                }
                                data.setBudgetAllocation(budgetAllocationSubReport);
                            }
                            data.setIsCda(isCda);
                        }
                        outBoxList.add(data);
                    }

                }

            }
        }
        else if (getCurrentRole.contains(HelperUtils.CBCREATER)) {
            approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedAndCreaterpIdOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1",hrDataCheck.getPid()));
            archiveMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndCreaterpIdOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1",hrDataCheck.getPid()));


            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedAndCreaterpIdOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "0", "0",hrDataCheck.getPid());
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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    outBoxList.add(data);
                }

            }
        } else if (getCurrentRole.contains(HelperUtils.CBVERIFER)) {
            approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));
            archiveMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));


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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    outBoxList.add(data);
                }

            }
        } else if (getCurrentRole.contains(HelperUtils.CBAPPROVER)) {
            approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));
            archiveMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));


            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("AP")) {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);
                    data.setGroupId(mangeInboxOutbox.getGroupId());
                    data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                    data.setRemarks(mangeInboxOutbox.getRemarks());
                    data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                    data.setAmount(mangeInboxOutbox.getAmount());
                    data.setType(mangeInboxOutbox.getType());
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    inboxList.add(data);

                } else if (inboxOutboxesList.get(i).getStatus().equalsIgnoreCase("Approved") && inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {
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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setType(mangeInboxOutbox.getType());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    outBoxList.add(data);
                } else {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);

                    if(!mangeInboxOutbox.getStatus().equalsIgnoreCase("Rejected")){
                        data.setGroupId(mangeInboxOutbox.getGroupId());
                        data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                        data.setRemarks(mangeInboxOutbox.getRemarks());
                        data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                        data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                        data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                        data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                        data.setAmount(mangeInboxOutbox.getAmount());
                        data.setType(mangeInboxOutbox.getType());
                        data.setIsRebase(mangeInboxOutbox.getIsRebase());
                        data.setIsRevision(mangeInboxOutbox.getIsRevision());
                        data.setStatus(mangeInboxOutbox.getStatus());
                        data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                        outBoxList.add(data);
                    }
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


        for (Integer i = 0; i < approvedMain.size(); i++) {
            InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
            MangeInboxOutbox mangeInboxOutbox = approvedMain.get(i);


            data.setGroupId(mangeInboxOutbox.getGroupId());
            data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
            data.setRemarks(mangeInboxOutbox.getRemarks());
            data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
            data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
            data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
            data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
            data.setAmount(mangeInboxOutbox.getAmount());
            data.setType(mangeInboxOutbox.getType());
            data.setIsRebase(mangeInboxOutbox.getIsRebase());
            data.setIsRevision(mangeInboxOutbox.getIsRevision());
            data.setStatus(mangeInboxOutbox.getStatus());
            data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));
            if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {


                List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());

                Boolean isCda = true;
                for (Integer m = 0; m < budgetAllocations.size(); m++) {

                    BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                    if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                        continue;
                    }

                    List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                    if (cdaParkingList.size() > 0) {
                        data.setIsCda(isCda);
                    } else {
                        isCda = false;
                        data.setIsCda(isCda);
                    }
                    data.setBudgetAllocation(budgetAllocationSubReport);
                }

                data.setIsCda(isCda);
            }
            approvedList.add(data);
        }

        for (Integer i = 0; i < archiveMain.size(); i++) {
            InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
            MangeInboxOutbox mangeInboxOutbox = archiveMain.get(i);

            data.setGroupId(mangeInboxOutbox.getGroupId());
            data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
            data.setRemarks(mangeInboxOutbox.getRemarks());
            data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
            data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
            data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
            data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
            data.setAmount(mangeInboxOutbox.getAmount());
            data.setIsRevision(mangeInboxOutbox.getIsRevision());
            data.setType(mangeInboxOutbox.getType());
            data.setIsRebase(mangeInboxOutbox.getIsRebase());
            data.setStatus(mangeInboxOutbox.getStatus());
            data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));
            if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {

                List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());
                Boolean isCda = true;
                for (Integer m = 0; m < budgetAllocations.size(); m++) {

                    BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                    if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                        continue;
                    }

                    List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                    if (cdaParkingList.size() > 0) {
                        data.setIsCda(isCda);
                    } else {
                        isCda = false;
                        data.setIsCda(isCda);
                    }
                    data.setBudgetAllocation(budgetAllocationSubReport);
                }
                data.setIsCda(isCda);
            }
            archivedList.add(data);
        }


        inboxOutBoxResponse.setApprovedList(approvedList);
        inboxOutBoxResponse.setInboxList(inboxList);
        inboxOutBoxResponse.setOutList(outBoxList);
        inboxOutBoxResponse.setArchivedList(archivedList);


        return ResponseUtils.createSuccessResponse(inboxOutBoxResponse, new TypeReference<InboxOutBoxResponse>() {
        });
    }









    @Override
    public ApiResponse<InboxOutBoxResponse> getInboxListMain() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }

        InboxOutBoxResponse inboxOutBoxResponse = new InboxOutBoxResponse();
        List<InboxOutBoxSubResponse> inboxList = new ArrayList<InboxOutBoxSubResponse>();
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

            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");
            dataIscgBg.add("RR");


            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0", "0", dataIscgBg);


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
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {
                        List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());
                        Boolean isCda = true;
                        for (Integer m = 0; m < budgetAllocations.size(); m++) {

                            BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                            if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                                continue;
                            }
                            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                            if (cdaParkingList.size() > 0) {
                                data.setIsCda(isCda);
                            } else {
                                isCda = false;
                                data.setIsCda(isCda);
                            }
                            data.setBudgetAllocation(budgetAllocationSubReport);
                        }
                        data.setIsCda(isCda);

                    }
                    inboxList.add(data);

                }

            }
        }
        else if (getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {

            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");
            dataIscgBg.add("RR");
            dataIscgBg.add("UR");
            dataIscgBg.add("CDA");
            dataIscgBg.add("SBG");
            dataIscgBg.add("CDAI");
            dataIscgBg.add("BGR");

            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0", "0", dataIscgBg);


            for (MangeInboxOutbox inboxOutbox : inboxOutboxesList) {

                if (inboxOutbox.getToUnit().equalsIgnoreCase(hrDataCheck.getUnitId())) {

                    if (inboxOutbox.getState().equalsIgnoreCase("CR")) {
                        InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                        MangeInboxOutbox mangeInboxOutbox = inboxOutbox;
                        data.setGroupId(mangeInboxOutbox.getGroupId());
                        data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                        data.setRemarks(mangeInboxOutbox.getRemarks());
                        data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                        data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                        data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                        data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                        data.setAmount(mangeInboxOutbox.getAmount());
                        data.setType(mangeInboxOutbox.getType());
                        data.setIsRebase(mangeInboxOutbox.getIsRebase());
                        data.setIsRevision(mangeInboxOutbox.getIsRevision());
                        data.setStatus(mangeInboxOutbox.getStatus());
                        data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                        if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {

                            List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());
                            Boolean isCda = true;
                            for (Integer m = 0; m < budgetAllocations.size(); m++) {

                                BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                                if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                                    continue;
                                }

                                List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                                if (cdaParkingList.size() > 0) {
                                    data.setIsCda(isCda);
                                } else {
                                    isCda = false;
                                    data.setIsCda(isCda);
                                }
                                data.setBudgetAllocation(budgetAllocationSubReport);
                            }
                            data.setIsCda(isCda);
                        }
                        inboxList.add(data);

                    }

                }

            }
        }
        else if (getCurrentRole.contains(HelperUtils.CBCREATER)) {

            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedAndCreaterpIdOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "0", "0",hrDataCheck.getPid());
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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    inboxList.add(data);

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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    inboxList.add(data);

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
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                    data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                    data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                    data.setAmount(mangeInboxOutbox.getAmount());
                    data.setType(mangeInboxOutbox.getType());
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    inboxList.add(data);

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

        inboxOutBoxResponse.setInboxList(inboxList);

        return ResponseUtils.createSuccessResponse(inboxOutBoxResponse, new TypeReference<InboxOutBoxResponse>() {
        });
    }



    @Override
    public ApiResponse<InboxOutBoxResponse> getOutboxListMain() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }

        InboxOutBoxResponse inboxOutBoxResponse = new InboxOutBoxResponse();

        List<InboxOutBoxSubResponse> outBoxList = new ArrayList<InboxOutBoxSubResponse>();
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }


        List<MangeInboxOutbox> inboxOutboxesList = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> archiveMain = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> approvedMain = new ArrayList<MangeInboxOutbox>();

        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {

        }

        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER)) {

            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");
            dataIscgBg.add("RR");

            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0", "0", dataIscgBg);


            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("AP")) {

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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setType(mangeInboxOutbox.getType());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {
                        List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());
                        Boolean isCda = true;
                        for (Integer m = 0; m < budgetAllocations.size(); m++) {

                            BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                            if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                                continue;
                            }
                            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                            if (cdaParkingList.size() > 0) {
                                data.setIsCda(isCda);
                            } else {
                                isCda = false;
                                data.setIsCda(isCda);
                            }
                            data.setBudgetAllocation(budgetAllocationSubReport);
                        }
                        data.setIsCda(isCda);
                    }
                    outBoxList.add(data);
                }

            }
        }
        else if (getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {
//            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "BG", "0", "0");

            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");
            dataIscgBg.add("RR");
            dataIscgBg.add("UR");
            dataIscgBg.add("CDA");
            dataIscgBg.add("SBG");
            dataIscgBg.add("CDAI");

            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0", "0", dataIscgBg);


            for (MangeInboxOutbox inboxOutbox : inboxOutboxesList) {

                if (inboxOutbox.getToUnit().equalsIgnoreCase(hrDataCheck.getUnitId())) {

                    if (inboxOutbox.getState().equalsIgnoreCase("CR")) {


                    } else {
                        InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                        MangeInboxOutbox mangeInboxOutbox = inboxOutbox;
                        data.setGroupId(mangeInboxOutbox.getGroupId());
                        data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                        data.setRemarks(mangeInboxOutbox.getRemarks());
                        data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                        data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                        data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                        data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                        data.setAmount(mangeInboxOutbox.getAmount());
                        data.setType(mangeInboxOutbox.getType());
                        data.setIsRebase(mangeInboxOutbox.getIsRebase());
                        data.setIsRevision(mangeInboxOutbox.getIsRevision());
                        data.setStatus(mangeInboxOutbox.getStatus());

                        if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {

                            List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());
                            Boolean isCda = true;
                            for (Integer m = 0; m < budgetAllocations.size(); m++) {

                                BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                                if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                                    continue;
                                }

                                List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                                if (cdaParkingList.size() > 0) {
                                    data.setIsCda(isCda);
                                } else {
                                    isCda = false;
                                    data.setIsCda(isCda);
                                }
                                data.setBudgetAllocation(budgetAllocationSubReport);
                            }
                            data.setIsCda(isCda);
                        }
                        outBoxList.add(data);
                    }

                }

            }
        }
        else if (getCurrentRole.contains(HelperUtils.CBCREATER)) {

            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedAndCreaterpIdOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "0", "0",hrDataCheck.getPid());
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {


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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    outBoxList.add(data);
                }

            }
        } else if (getCurrentRole.contains(HelperUtils.CBVERIFER)) {


            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "0", "0");
            for (Integer i = 0; i < inboxOutboxesList.size(); i++) {

                if (inboxOutboxesList.get(i).getState().equalsIgnoreCase("VE")) {


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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
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


                } else if (inboxOutboxesList.get(i).getStatus().equalsIgnoreCase("Approved") && inboxOutboxesList.get(i).getState().equalsIgnoreCase("CR")) {
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
                    data.setIsRebase(mangeInboxOutbox.getIsRebase());
                    data.setType(mangeInboxOutbox.getType());
                    data.setIsRevision(mangeInboxOutbox.getIsRevision());
                    data.setStatus(mangeInboxOutbox.getStatus());
                    data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                    outBoxList.add(data);
                } else {
                    InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
                    MangeInboxOutbox mangeInboxOutbox = inboxOutboxesList.get(i);

                    if(!mangeInboxOutbox.getStatus().equalsIgnoreCase("Rejected")){
                        data.setGroupId(mangeInboxOutbox.getGroupId());
                        data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
                        data.setRemarks(mangeInboxOutbox.getRemarks());
                        data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
                        data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
                        data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
                        data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
                        data.setAmount(mangeInboxOutbox.getAmount());
                        data.setType(mangeInboxOutbox.getType());
                        data.setIsRebase(mangeInboxOutbox.getIsRebase());
                        data.setIsRevision(mangeInboxOutbox.getIsRevision());
                        data.setStatus(mangeInboxOutbox.getStatus());
                        data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));

                        outBoxList.add(data);
                    }
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



        inboxOutBoxResponse.setOutList(outBoxList);


        return ResponseUtils.createSuccessResponse(inboxOutBoxResponse, new TypeReference<InboxOutBoxResponse>() {
        });
    }


    @Override
    public ApiResponse<InboxOutBoxResponse> getApprovedListMain() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }

        InboxOutBoxResponse inboxOutBoxResponse = new InboxOutBoxResponse();

        List<InboxOutBoxSubResponse> approvedList = new ArrayList<InboxOutBoxSubResponse>();
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }


      //  List<MangeInboxOutbox> inboxOutboxesList = new ArrayList<MangeInboxOutbox>();
       // List<MangeInboxOutbox> archiveMain = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> approvedMain = new ArrayList<MangeInboxOutbox>();

        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {

        }

        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER)) {



            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");
            dataIscgBg.add("RR");
           // approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));
            approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0","1",dataIscgBg));

            //inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0", "0", dataIscgBg);

        }
        else if (getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {
//            inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "BG", "0", "0");

            List<String> dataIscgBg = new ArrayList<>();
            dataIscgBg.add("BG");
            dataIscgBg.add("BR");
            dataIscgBg.add("RR");
            dataIscgBg.add("UR");
            dataIscgBg.add("CDA");
            dataIscgBg.add("SBG");
            dataIscgBg.add("CDAI");

           // inboxOutboxesList = mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0", "0", dataIscgBg);
            approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "0","1",dataIscgBg));

            //approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));

        }
        else if (getCurrentRole.contains(HelperUtils.CBCREATER)) {
            approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedAndCreaterpIdOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1",hrDataCheck.getPid()));

        } else if (getCurrentRole.contains(HelperUtils.CBVERIFER)) {
            approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));

        } else if (getCurrentRole.contains(HelperUtils.CBAPPROVER)) {
            approvedMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));

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


        for (Integer i = 0; i < approvedMain.size(); i++) {
            InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
            MangeInboxOutbox mangeInboxOutbox = approvedMain.get(i);


            data.setGroupId(mangeInboxOutbox.getGroupId());
            data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
            data.setRemarks(mangeInboxOutbox.getRemarks());
            data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
            data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
            data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
            data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
            data.setAmount(mangeInboxOutbox.getAmount());
            data.setType(mangeInboxOutbox.getType());
            data.setIsRebase(mangeInboxOutbox.getIsRebase());
            data.setIsRevision(mangeInboxOutbox.getIsRevision());
            data.setStatus(mangeInboxOutbox.getStatus());
            data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));
            if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {


                List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());

                Boolean isCda = true;
                for (Integer m = 0; m < budgetAllocations.size(); m++) {

                    BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                    if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                        continue;
                    }

                    List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                    if (cdaParkingList.size() > 0) {
                        data.setIsCda(isCda);
                    } else {
                        isCda = false;
                        data.setIsCda(isCda);
                    }
                    data.setBudgetAllocation(budgetAllocationSubReport);
                }

                data.setIsCda(isCda);
            }
            approvedList.add(data);
        }

        inboxOutBoxResponse.setApprovedList(approvedList);


        return ResponseUtils.createSuccessResponse(inboxOutBoxResponse, new TypeReference<InboxOutBoxResponse>() {
        });
    }


    @Override
    public ApiResponse<InboxOutBoxResponse> getArchivedListMain() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }

        InboxOutBoxResponse inboxOutBoxResponse = new InboxOutBoxResponse();
        List<InboxOutBoxSubResponse> archivedList = new ArrayList<InboxOutBoxSubResponse>();
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }


        List<MangeInboxOutbox> inboxOutboxesList = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> archiveMain = new ArrayList<MangeInboxOutbox>();
        List<MangeInboxOutbox> approvedMain = new ArrayList<MangeInboxOutbox>();

        String getCurrentRole = "";
        try {
            getCurrentRole = hrDataCheck.getRoleId().split(",")[0];
        } catch (Exception e) {

        }

        if (getCurrentRole.contains(HelperUtils.BUDGETAPPROVER)) {
            archiveMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));

        }
        else if (getCurrentRole.contains(HelperUtils.BUDGETMANGER)) {

            archiveMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "1"));

        }
        else if (getCurrentRole.contains(HelperUtils.CBCREATER)) {
            archiveMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveAndCreaterpIdOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1",hrDataCheck.getPid()));

        } else if (getCurrentRole.contains(HelperUtils.CBVERIFER)) {
            archiveMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));

        } else if (getCurrentRole.contains(HelperUtils.CBAPPROVER)) {
            archiveMain.addAll(mangeInboxOutBoxRepository.findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(hrDataCheck.getUnitId(), "CB", "1"));
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



        for (Integer i = 0; i < archiveMain.size(); i++) {
            InboxOutBoxSubResponse data = new InboxOutBoxSubResponse();
            MangeInboxOutbox mangeInboxOutbox = archiveMain.get(i);

            data.setGroupId(mangeInboxOutbox.getGroupId());
            data.setMangeInboxId(mangeInboxOutbox.getMangeInboxId());
            data.setRemarks(mangeInboxOutbox.getRemarks());
            data.setIsBgOrCg(mangeInboxOutbox.getIsBgcg());
            data.setToUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getToUnit()));
            data.setFromUnit(cgUnitRepository.findByUnit(mangeInboxOutbox.getFromUnit()));
            data.setCreatedOn(mangeInboxOutbox.getCreatedOn());
            data.setAmount(mangeInboxOutbox.getAmount());
            data.setIsRevision(mangeInboxOutbox.getIsRevision());
            data.setType(mangeInboxOutbox.getType());
            data.setIsRebase(mangeInboxOutbox.getIsRebase());
            data.setStatus(mangeInboxOutbox.getStatus());
            data.setAllocationType(allocationRepository.findByAllocTypeId(mangeInboxOutbox.getAllocationType()));
            if (mangeInboxOutbox.getIsBgcg().equalsIgnoreCase("BR")) {

                List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(mangeInboxOutbox.getGroupId(), hrDataCheck.getUnitId());
                Boolean isCda = true;
                for (Integer m = 0; m < budgetAllocations.size(); m++) {

                    BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(m);

                    if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                        continue;
                    }

                    List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");
                    if (cdaParkingList.size() > 0) {
                        data.setIsCda(isCda);
                    } else {
                        isCda = false;
                        data.setIsCda(isCda);
                    }
                    data.setBudgetAllocation(budgetAllocationSubReport);
                }
                data.setIsCda(isCda);
            }
            archivedList.add(data);
        }

        inboxOutBoxResponse.setArchivedList(archivedList);


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
            List<BudgetAllocation> inboxOutboxesList = budgetAllocationRepository.findByAuthGroupIdAndToUnit(groupId, hrDataCheck.getUnitId());
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


    @Override
    public ApiResponse<ArchivedResponse> updateMsgStatusMain(String msgId) {
        ArchivedResponse responce = new ArchivedResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }
        if (msgId == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GROUP ID CAN NOT BE BLANK OR EMPTY");
        }

        MangeInboxOutbox mangeInboxOutbox = mangeInboxOutBoxRepository.findByMangeInboxId(msgId);
        if (mangeInboxOutbox != null) {
            mangeInboxOutbox.setIsApproved("1");
            mangeInboxOutbox.setStatus("Fully Approved");
            mangeInboxOutBoxRepository.save(mangeInboxOutbox);
        }

        return ResponseUtils.createSuccessResponse(responce, new TypeReference<ArchivedResponse>() {
        });
    }


    @Override
    public ApiResponse<ArchivedResponse> moveToArchive(String msgId) {
        ArchivedResponse responce = new ArchivedResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        }
        if (msgId == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GROUP ID CAN NOT BE BLANK OR EMPTY");
        }

        MangeInboxOutbox mangeInboxOutbox = mangeInboxOutBoxRepository.findByMangeInboxId(msgId);
        if (mangeInboxOutbox != null) {
            mangeInboxOutbox.setIsArchive("1");
            //mangeInboxOutbox.setStatus("Fully Approved");
            mangeInboxOutBoxRepository.save(mangeInboxOutbox);
        }

        return ResponseUtils.createSuccessResponse(responce, new TypeReference<ArchivedResponse>() {
        });
    }

}

package com.sdd.service.Impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.*;
import com.sdd.entities.BudgetAllocationReport;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.request.*;
import com.sdd.response.*;
import com.sdd.service.BudgetAllocationService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class BudgetAllocationServiceImpl implements BudgetAllocationService {
    @Autowired
    CgStationRepository cgStationRepository;

    @Autowired
    CdaParkingTransRepository cdaParkingTransRepository;


    @Autowired
    SubHeadTypeRepository subHeadTypeRepository;


    @Autowired
    ContigentBillRepository contigentBillRepository;


    @Autowired
    AmountUnitRepository amountUnitRepository;


    @Autowired
    MangeInboxOutBoxRepository mangeInboxOutBoxRepository;

    @Autowired
    private HrDataRepository hrDataRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HeaderUtils headerUtils;

    @Autowired
    BudgetFinancialYearRepository budgetFinancialYearRepository;

    @Autowired
    SubHeadRepository subHeadRepository;

    @Autowired
    CgUnitRepository cgUnitRepository;

    @Autowired
    AllocationRepository allocationRepository;

    @Autowired
    BudgetAllocationRepository budgetAllocationRepository;

    @Autowired
    BudgetAllocationReportRepository budgetAllocationReportRepository;

    @Autowired
    BudgetAllocationDetailsRepository budgetAllocationDetailsRepository;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    AuthorityRepository authorityRepository;


    @Override
    public ApiResponse<List<BudgetFinancialYear>> getBudgetFinYear() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

        List<BudgetFinancialYear> budgetFinYearData = budgetFinancialYearRepository.findAllByOrderByFinYearAsc();

        return ResponseUtils.createSuccessResponse(budgetFinYearData, new TypeReference<List<BudgetFinancialYear>>() {
        });
    }

    @Override
    public ApiResponse<List<AllocationType>> getAllocationAllData() {

        String token = headerUtils.getTokeFromHeader();
        List<AllocationType> allocationRepositoryData = allocationRepository.findAll();


        return ResponseUtils.createSuccessResponse(allocationRepositoryData, new TypeReference<List<AllocationType>>() {
        });
    }

    @Override
    public ApiResponse<List<AllocationType>> getAllocationType() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

        List<AllocationType> allocationRepositoryData = allocationRepository.findAll();
        List<AllocationType> minList = new ArrayList<AllocationType>();


        for (Integer i = 0; i < allocationRepositoryData.size(); i++) {

            if (allocationRepositoryData.get(i).getAllocDesc().equalsIgnoreCase("DELETE") || allocationRepositoryData.get(i).getAllocDesc().equalsIgnoreCase("UPDATE")) {

            } else {
                minList.add(allocationRepositoryData.get(i));
            }
        }

        return ResponseUtils.createSuccessResponse(minList, new TypeReference<List<AllocationType>>() {
        });
    }


    @Override
    public ApiResponse<List<BudgetHead>> getSubHeadsData() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION.LOGIN AGAIN");
        }

        List<BudgetHead> subHeadsData = subHeadRepository.findAllByOrderBySerialNumberAsc();
        Collections.sort(subHeadsData, new Comparator<BudgetHead>() {
            public int compare(BudgetHead v1, BudgetHead v2) {
                return v1.getSerialNumber().compareTo(v2.getSerialNumber());
            }
        });

        return ResponseUtils.createSuccessResponse(subHeadsData, new TypeReference<List<BudgetHead>>() {
        });
    }


    @Override
    public ApiResponse<List<BudgetHeadResponse>> getSubHeadListWithAmount(BudgetHeadRequest budgetHeadRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION.LOGIN AGAIN");
        }

        if (budgetHeadRequest.getFinYearId() == null || budgetHeadRequest.getFinYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY UNIT ID CAN NOT BE BLANK");
        }

        if (budgetHeadRequest.getMajorHead() == null || budgetHeadRequest.getMajorHead().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY UNIT ID CAN NOT BE BLANK");
        }

        if (budgetHeadRequest.getAllocationType() == null || budgetHeadRequest.getAllocationType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY UNIT ID CAN NOT BE BLANK");
        }

        if (budgetHeadRequest.getSubHeadType() == null || budgetHeadRequest.getSubHeadType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY UNIT ID CAN NOT BE BLANK");
        }

        List<BudgetHead> subHeadsData = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(budgetHeadRequest.getMajorHead(), budgetHeadRequest.getSubHeadType());

        Collections.sort(subHeadsData, new Comparator<BudgetHead>() {
            public int compare(BudgetHead v1, BudgetHead v2) {
                return v1.getSerialNumber().compareTo(v2.getSerialNumber());
            }
        });


        List<BudgetHeadResponse> budgetListWithAmount = new ArrayList<>();

        for (Integer i = 0; i < subHeadsData.size(); i++) {
            BudgetHeadResponse budgetHeadResponse = new BudgetHeadResponse();
            BeanUtils.copyProperties(subHeadsData.get(i), budgetHeadResponse);
            String amountType = "0";
            double amount = 0;
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDelete(hrDataCheck.getUnitId(), budgetHeadRequest.getFinYearId(), subHeadsData.get(i).getBudgetCodeId(), budgetHeadRequest.getAllocationType(), "Approved", "0");
            for (Integer b = 0; b < budgetAllocationDetailsList.size(); b++) {
                amountType = budgetAllocationDetailsList.get(b).getAmountType();
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(amountType);
                amount = amount + Double.parseDouble(budgetAllocationDetailsList.get(b).getAllocationAmount()) * amountUnit.getAmount();
            }
            budgetHeadResponse.setTotalAmount(ConverterUtils.addDecimalPoint(amount + ""));
            budgetListWithAmount.add(budgetHeadResponse);
        }

        return ResponseUtils.createSuccessResponse(budgetListWithAmount, new TypeReference<List<BudgetHeadResponse>>() {
        });
    }


    @Override
    public ApiResponse<BudgetResponseWithToken> getSubHeads() {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);


        BudgetResponseWithToken mainDataToSer = new BudgetResponseWithToken();
        ArrayList<BudgetHead> mainData = new ArrayList<BudgetHead>();
        List<BudgetHead> subHeadsData = subHeadRepository.findAllByOrderBySerialNumberAsc();
        Collections.sort(subHeadsData, new Comparator<BudgetHead>() {
            public int compare(BudgetHead v1, BudgetHead v2) {
                return v1.getSerialNumber().compareTo(v2.getSerialNumber());
            }
        });


        HashMap<String, BudgetHead> removeDuplicate = new HashMap<String, BudgetHead>();


        for (Integer i = 0; i < subHeadsData.size(); i++) {
            removeDuplicate.put(subHeadsData.get(i).getMajorHead(), subHeadsData.get(i));
        }

        for (HashMap.Entry<String, BudgetHead> entry : removeDuplicate.entrySet()) {
            mainData.add(removeDuplicate.get(entry.getKey()));
        }
        mainDataToSer.setSubHead(mainData);

        return ResponseUtils.createSuccessResponse(mainDataToSer, new TypeReference<BudgetResponseWithToken>() {
        });

    }

    @Override
    public ApiResponse<List<CgUnitResponse>> getCgUnitData() {

        List<CgUnitResponse> cgUnitResponseList = new ArrayList<CgUnitResponse>();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION.LOGIN AGAIN");
        }

        HrData hrData = hrDataRepository.findByPidAndIsActive(hrDataCheck.getPid(), "1");
        CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());
        if (cgUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER UNIT IS INVALID.PLEASE CHECK");
        }

        List<CgUnit> unitDataList = new ArrayList<>();
        if (hrData.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
            unitDataList = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());
        } else {
            unitDataList = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getUnit());
        }


        for (Integer n = 0; n < unitDataList.size(); n++) {
            CgUnitResponse cgUnitResponse = new CgUnitResponse();

            if (unitDataList.get(n).getUnit().equalsIgnoreCase(HelperUtils.HEADUNITID)) {

            } else {
                BeanUtils.copyProperties(unitDataList.get(n), cgUnitResponse);
                CgStation cgStation = null;
                if (unitDataList.get(n).getStationId() == null) {
                } else {
                    cgStation = cgStationRepository.findByStationId(unitDataList.get(n).getStationId());
                }
                cgUnitResponse.setCgStation(cgStation);
                cgUnitResponseList.add(cgUnitResponse);
            }
        }

        return ResponseUtils.createSuccessResponse(cgUnitResponseList, new TypeReference<List<CgUnitResponse>>() {
        });
    }

    @Override
    public ApiResponse<List<CgUnitResponse>> getCgUnitDataForBudgetRecipt() {
        List<CgUnitResponse> cgUnitResponseList = new ArrayList<CgUnitResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION.LOGIN AGAIN");
        }

        HrData hrData = hrDataRepository.findByPidAndIsActive(hrDataCheck.getPid(), "1");
        String cuurentRole = hrData.getRoleId().split(",")[0];
        CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());


        if (cuurentRole.equalsIgnoreCase(HelperUtils.SYSTEMADMIN)) {
            List<CgUnit> unitDataList = cgUnitRepository.findByPurposeCodeOrPurposeCodeOrderByDescrAsc("0", "1");
            for (Integer n = 0; n < unitDataList.size(); n++) {


                if (unitDataList.get(n).getPurposeCode().equalsIgnoreCase("0") || unitDataList.get(n).getPurposeCode().equalsIgnoreCase("1")) {
                    CgUnitResponse cgUnitResponse = new CgUnitResponse();
                    BeanUtils.copyProperties(unitDataList.get(n), cgUnitResponse);
                    CgStation cgStation = null;
                    if (unitDataList.get(n).getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unitDataList.get(n).getStationId());
                    }
                    cgUnitResponse.setCgStation(cgStation);
                    cgUnitResponseList.add(cgUnitResponse);
                }

            }

        } else if (cuurentRole.equalsIgnoreCase(HelperUtils.UNITADMIN)) {
            List<CgUnit> unitDataList = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());

            for (Integer n = 0; n < unitDataList.size(); n++) {
                CgUnitResponse cgUnitResponse = new CgUnitResponse();
                if (unitDataList.get(n).getPurposeCode().equalsIgnoreCase("0")) {
                    BeanUtils.copyProperties(unitDataList.get(n), cgUnitResponse);
                    CgStation cgStation = null;
                    if (unitDataList.get(n).getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unitDataList.get(n).getStationId());
                    }
                    cgUnitResponse.setCgStation(cgStation);
                    cgUnitResponseList.add(cgUnitResponse);
                } else if (unitDataList.get(n).getPurposeCode().equalsIgnoreCase("1")) {
                    BeanUtils.copyProperties(unitDataList.get(n), cgUnitResponse);
                    CgStation cgStation = null;
                    if (unitDataList.get(n).getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unitDataList.get(n).getStationId());
                    }
                    cgUnitResponse.setCgStation(cgStation);
                    cgUnitResponseList.add(cgUnitResponse);
                }

            }
        }

        return ResponseUtils.createSuccessResponse(cgUnitResponseList, new TypeReference<List<CgUnitResponse>>() {
        });
    }


    @Override
    public ApiResponse<BudgetAllocationResponse> budgetAllocationReport(BudgetAllocationReportRequest budgetAllocationReportRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }
        BudgetAllocationResponse budgetAllocationResponse = new BudgetAllocationResponse();
        if (budgetAllocationReportRequest.getBudgetFinancialYearId() == null || budgetAllocationReportRequest.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY UNIT ID CAN NOT BE BLANK");
        }

        if (budgetAllocationReportRequest.getToUnitId() == null || budgetAllocationReportRequest.getToUnitId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO UNIT ID CAN NOT BE BLANK");
        }

        CgUnit cgFromUnit = cgUnitRepository.findByUnit(budgetAllocationReportRequest.getToUnitId());
        if (cgFromUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
        }

        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetAllocationReportRequest.getBudgetFinancialYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        List<BudgetAllocationSubResponse> budgetAllocationList = new ArrayList<BudgetAllocationSubResponse>();


        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndIsDeleteAndStatusOrStatus(budgetAllocationReportRequest.getToUnitId(), budgetAllocationReportRequest.getBudgetFinancialYearId(), "0", "Pending", "Rejected");
        String authgroupId = "";
        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

            BudgetAllocationSubResponse budgetAllocationReportData = new BudgetAllocationSubResponse();
            budgetAllocationReportData.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReportData.setAllocationId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReportData.setTransactionId(budgetAllocationSubReport.getTransactionId());
            budgetAllocationReportData.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
            budgetAllocationReportData.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getBalanceAmount()));
            budgetAllocationReportData.setStatus(budgetAllocationSubReport.getStatus());
            budgetAllocationReportData.setPurposeCode(budgetAllocationSubReport.getPurposeCode());
            budgetAllocationReportData.setRemarks(budgetAllocationSubReport.getRemarks());
            budgetAllocationReportData.setRefTransactionId(budgetAllocationSubReport.getRefTransactionId());
            budgetAllocationReportData.setUserId(budgetAllocationSubReport.getUserId());
            budgetAllocationReportData.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReportData.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            budgetAllocationReportData.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReportData.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());
            authgroupId = budgetAllocationSubReport.getAuthGroupId();
            budgetAllocationReportData.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReportData.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReportData.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            budgetAllocationReportData.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
            budgetAllocationReportData.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocTypeId()));
            budgetAllocationReportData.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));

            budgetAllocationList.add(budgetAllocationReportData);

        }

        List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(authgroupId);

        budgetAllocationResponse.setAuthList(authoritiesList);
        budgetAllocationResponse.setBudgetResponseist(budgetAllocationList);


        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<BudgetAllocationResponse>() {
        });

    }

    @Override
    public ApiResponse<List<BudgetHead>> getSubHeadsDataByMajorHead(BudgetHeadRequest majorHead) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        if (majorHead.getMajorHead() == null || majorHead.getMajorHead().isEmpty()) {
            throw new SDDException(HttpStatus.BAD_REQUEST.value(), "MAJOR HEAD ID CAN NOT BLANK");
        }

        if (majorHead.getBudgetHeadType() == null || majorHead.getBudgetHeadType().isEmpty()) {
            throw new SDDException(HttpStatus.BAD_REQUEST.value(), "BUDGET HEAD TYPE ID CAN NOT BLANK");
        }


        List<BudgetHead> subHeadsData = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(majorHead.getMajorHead(), majorHead.getBudgetHeadType());
        Collections.sort(subHeadsData, new Comparator<BudgetHead>() {
            public int compare(BudgetHead v1, BudgetHead v2) {
                return v1.getSerialNumber().compareTo(v2.getSerialNumber());
            }
        });


        return ResponseUtils.createSuccessResponse(subHeadsData, new TypeReference<List<BudgetHead>>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationSaveResponse> updateBudgetAllocationSubHeadWise(BudgetAllocationUpdateRequest budgetAllocationSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {
            if (hrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
            }
        }

        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();

//        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
//
//
//            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemark() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemark().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
//            }
//
//            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getTransactionId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getTransactionId().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TRANSACTION ID CAN NOT BE BLANK");
//            }

        if (budgetAllocationSaveRequestList.getTransactionId() == null || budgetAllocationSaveRequestList.getTransactionId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TRANSACTION ID CAN NOT BE BLANK");
        }

        if (budgetAllocationSaveRequestList.getAmount() == null || budgetAllocationSaveRequestList.getAmount().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT CAN NOT BE BLANK");
        }


        BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionIdAndIsDelete(budgetAllocationSaveRequestList.getTransactionId(), "0");

        if (budgetAllocationDetails == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TRANSACTION ID");
        }


//            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET FINANCIAL ID ID CAN NOT BE BLANK");
//            }
//
//            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemark() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemark().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
//            }

//            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID ID CAN NOT BE BLANK");
//            }
//
//            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO UNIT CAN NOT BE BLANK");
//            }
//
//            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
//            if (budgetFinancialYear == null) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
//            }
//
//            CgUnit cgToUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
//            if (cgToUnit == null) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
//            }
//
//
//            AllocationType allocationType = allocationRepository.findByAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
//            if (allocationType == null) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
//            }
//
//        }


//        for (Integer k = 0; k < budgetAllocationSaveRequestList.getAuthRequests().size(); k++) {
//
//            String authertyId = budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthorityId();
//
//
//            if (authertyId == null || authertyId.isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY ID CAN NOT BE BLANK");
//            }
//
//            Authority authority = authorityRepository.findByAuthorityId(authertyId);
//
//
//            if (authority == null) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AUTHORITY ID");
//            }
//
//            if (budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDocId() == null || budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDocId().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT ID CAN NOT BE BLANK");
//            }
//
//            if (budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthority() == null || budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthority().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY CAN NOT BE BLANK");
//            }
//
//            if (budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDate() == null || budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDate().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY DATE CAN NOT BE BLANK");
//            }
//
//            ConverterUtils.checkDateIsvalidOrNor(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDate());
//
//
//            if (budgetAllocationSaveRequestList.getAuthRequests().get(k).getRemark() == null || budgetAllocationSaveRequestList.getAuthRequests().get(k).getRemark().isEmpty()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
//            }
//
//
//            CgUnit chekUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthUnitId());
//            if (chekUnit == null) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
//            }
//
//            FileUpload fileUpload = fileUploadRepository.findByUploadID(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDocId());
//            if (fileUpload == null) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DOCUMENT ID ");
//            }
//        }


//        String authGrouPid = "";
//        for (Integer k = 0; k < budgetAllocationSaveRequestList.getAuthRequests().size(); k++) {
//
//
//            Authority authority = authorityRepository.findByAuthorityId(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthorityId());
//
//            authority.setAuthorityId(authority.getAuthorityId());
//            authority.setAuthority(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthority());
//            authority.setAuthDate(ConverterUtils.convertDateTotimeStamp(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDate()));
//            authority.setDocId(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDocId());
//            authority.setAuthUnit(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthUnitId());
//            authority.setCreatedOn(authority.getCreatedOn());
//            authority.setAuthGroupId(authority.getAuthGroupId());
//            authority.setRemarks(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthUnitId());
//            authority.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//
//            authorityRepository.save(authority);
//
//        }


        budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getAmount()));
        budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        budgetAllocationDetails.setStatus("Pending");
        budgetAllocationDetailsRepository.save(budgetAllocationDetails);


        response.setMsg("Data Update successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationSaveResponse> updateBudgetAllocationUnitWise(BudgetAllocationUpdateRequest budgetAllocationSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {
            if (hrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
            }
        }


        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();

        if (budgetAllocationSaveRequestList.getTransactionId() == null || budgetAllocationSaveRequestList.getTransactionId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TRANSACTION ID CAN NOT BE BLANK");
        }

        if (budgetAllocationSaveRequestList.getAmount() == null || budgetAllocationSaveRequestList.getAmount().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT CAN NOT BE BLANK");
        }

        BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionIdAndIsDelete(budgetAllocationSaveRequestList.getTransactionId(), "0");

        if (budgetAllocationDetails == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TRANSACTION ID");
        }


        budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getAmount()));
        budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        budgetAllocationDetails.setStatus("Pending");
        budgetAllocationDetailsRepository.save(budgetAllocationDetails);


        response.setMsg("Data Update successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationResponse> getBudgetAllocationData() {

        List<BudgetAllocationSubResponse> budgetAllocationList = new ArrayList<BudgetAllocationSubResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByToUnitAndIsDelete(hrData.getToDate(), "0");
//        List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByToUnitAndFinYear(budgetAllocationReportRequest.getToUnitId(), budgetAllocationReportRequest.getBudgetFinancialYearId());
        String authgroupId = "";
        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

            BudgetAllocationSubResponse budgetAllocationReport = new BudgetAllocationSubResponse();
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setAllocationId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setTransactionId(budgetAllocationSubReport.getTransactionId());
            budgetAllocationReport.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
            budgetAllocationReport.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getBalanceAmount()));
            budgetAllocationReport.setStatus(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setPurposeCode(budgetAllocationSubReport.getPurposeCode());
            budgetAllocationReport.setRemarks(budgetAllocationSubReport.getRemarks());
            budgetAllocationReport.setRefTransactionId(budgetAllocationSubReport.getRefTransactionId());
            budgetAllocationReport.setUserId(budgetAllocationSubReport.getUserId());
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            budgetAllocationReport.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());
            authgroupId = budgetAllocationSubReport.getAuthGroupId();
            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
            budgetAllocationReport.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocTypeId()));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));

            budgetAllocationList.add(budgetAllocationReport);

        }

        Collections.sort(budgetAllocationList, new Comparator<BudgetAllocationSubResponse>() {
            public int compare(BudgetAllocationSubResponse v1, BudgetAllocationSubResponse v2) {
                return v1.getSubHead().getSerialNumber().compareTo(v2.getSubHead().getSerialNumber());
            }
        });

        BudgetAllocationResponse budgetAllocationResponse = new BudgetAllocationResponse();

        List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(authgroupId);

        budgetAllocationResponse.setAuthList(authoritiesList);
        budgetAllocationResponse.setBudgetResponseist(budgetAllocationList);


        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<BudgetAllocationResponse>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationResponse> getBudgetAllocationDataGroupId(String groupId) {
        List<BudgetAllocationSubResponse> budgetAllocationList = new ArrayList<BudgetAllocationSubResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }
        if (groupId == null || groupId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GROUP ID CAN NOT BE BLANK");
        }

        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(groupId, "0");

        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

            BudgetAllocationSubResponse budgetAllocationReport = new BudgetAllocationSubResponse();
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


            double allocationAmount = 0;
            AmountUnit remeningBalanceUnit = null;
            List<BudgetAllocation> modBudgetAllocations = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(hrData.getUnitId(), budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), budgetAllocationSubReport.getAllocTypeId(), "Approved", "0");
            for (Integer m = 0; m < modBudgetAllocations.size(); m++) {
                remeningBalanceUnit = amountUnitRepository.findByAmountTypeId(modBudgetAllocations.get(m).getAmountType());
                allocationAmount = allocationAmount + (Double.parseDouble(modBudgetAllocations.get(m).getBalanceAmount()));

            }


            budgetAllocationReport.setBalanceAmount(ConverterUtils.addDecimalPoint(allocationAmount + ""));
            budgetAllocationReport.setRemeningBalanceUnit(remeningBalanceUnit);


            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), budgetAllocationSubReport.getToUnit(), "0");
            if (cdaParkingList.size() > 0) {
                budgetAllocationReport.setIsCDAparking("1");
                budgetAllocationReport.setCdaList(cdaParkingList);
            } else {
                budgetAllocationReport.setIsCDAparking("0");
                budgetAllocationReport.setCdaList(cdaParkingList);
            }

            budgetAllocationList.add(budgetAllocationReport);

        }


        Collections.sort(budgetAllocationList, new Comparator<BudgetAllocationSubResponse>() {
            public int compare(BudgetAllocationSubResponse v1, BudgetAllocationSubResponse v2) {
                return v1.getSubHead().getSerialNumber().compareTo(v2.getSubHead().getSerialNumber());
            }
        });


        BudgetAllocationResponse budgetAllocationResponse = new BudgetAllocationResponse();

        List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(groupId);

        budgetAllocationResponse.setAuthList(authoritiesList);
        budgetAllocationResponse.setBudgetResponseist(budgetAllocationList);


        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<BudgetAllocationResponse>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationResponse> getAllRevisionGroupId(String groupId) {
        List<BudgetAllocationSubResponse> budgetAllocationList = new ArrayList<BudgetAllocationSubResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }
        if (groupId == null || groupId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GROUP ID CAN NOT BE BLANK");
        }

        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(groupId, "0");

        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

            BudgetAllocationSubResponse budgetAllocationReport = new BudgetAllocationSubResponse();
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


            double allocationAmount = 0;
            AmountUnit remeningBalanceUnit = null;
            double revisedAmountUnit = 0;
            remeningBalanceUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType());
            allocationAmount = allocationAmount + (Double.parseDouble(budgetAllocationSubReport.getBalanceAmount()));
            revisedAmountUnit = revisedAmountUnit + (Double.parseDouble(budgetAllocationSubReport.getRevisedAmount()));


            budgetAllocationReport.setBalanceAmount(ConverterUtils.addDecimalPoint(allocationAmount + ""));
            budgetAllocationReport.setRevisedAmount(ConverterUtils.addDecimalPoint(revisedAmountUnit + ""));
            budgetAllocationReport.setRemeningBalanceUnit(remeningBalanceUnit);


            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), budgetAllocationSubReport.getToUnit(), "0");
            if (cdaParkingList.size() > 0) {
                budgetAllocationReport.setIsCDAparking("1");
                budgetAllocationReport.setCdaList(cdaParkingList);
            } else {
                budgetAllocationReport.setIsCDAparking("0");
                budgetAllocationReport.setCdaList(cdaParkingList);
            }

            budgetAllocationList.add(budgetAllocationReport);

        }


        Collections.sort(budgetAllocationList, new Comparator<BudgetAllocationSubResponse>() {
            public int compare(BudgetAllocationSubResponse v1, BudgetAllocationSubResponse v2) {
                return v1.getSubHead().getSerialNumber().compareTo(v2.getSubHead().getSerialNumber());
            }
        });


        BudgetAllocationResponse budgetAllocationResponse = new BudgetAllocationResponse();

        List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(groupId);

        budgetAllocationResponse.setAuthList(authoritiesList);
        budgetAllocationResponse.setBudgetResponseist(budgetAllocationList);


        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<BudgetAllocationResponse>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationResponse> getApprovedBudgetData() {
        List<BudgetAllocationSubResponse> budgetAllocationList = new ArrayList<BudgetAllocationSubResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }


        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByFromUnitAndIsDeleteAndStatus(hrData.getUnitId(), "0", "Approved");

        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

            BudgetAllocationSubResponse budgetAllocationReport = new BudgetAllocationSubResponse();
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

            budgetAllocationList.add(budgetAllocationReport);

        }

        Collections.sort(budgetAllocationList, new Comparator<BudgetAllocationSubResponse>() {
            public int compare(BudgetAllocationSubResponse v1, BudgetAllocationSubResponse v2) {
                return v1.getSubHead().getSerialNumber().compareTo(v2.getSubHead().getSerialNumber());
            }
        });


        BudgetAllocationResponse budgetAllocationResponse = new BudgetAllocationResponse();
        budgetAllocationResponse.setBudgetResponseist(budgetAllocationList);

        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<BudgetAllocationResponse>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetRevision(BudgetAllocationSaveUnitRequest budgetAllocationSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE RIVISION BUDUGET");
        }

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationSubRequest budgetAllocationSaveRequest = budgetAllocationSaveRequestList.getBudgetRequest().get(i);
            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR CAN NOT BE BLANK");
            }
            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO UNIT CAN NOT BE BLANK");
            }
            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID ID CAN NOT BE BLANK");
            }
            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT CAN NOT BE BLANK");
            }
            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REVISED AMOUNT CAN NOT BE BLANK");
            }
            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID ID CAN NOT BE BLANK");
            }
            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID NOT BE BLANK");
            }


            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
            if (budgetFinancialYear == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
            }

            CgUnit cgToUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
            if (cgToUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
            }


            AllocationType allocationType = allocationRepository.findByAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
            if (allocationType == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
            if (amountUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
            }

        }


//        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
//
//            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getIsAllocated().equalsIgnoreCase("1")) {
//                List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDelete(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Approved", "0");
//
//                for (Integer m = 0; m < budgetAllocationDetailsList.size(); m++) {
//                    if (budgetAllocationDetailsList.get(m).getAllocTypeId().equalsIgnoreCase(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId())) {
//                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "OLD ALLOCATION TYPE AND REVISION ALLOCATION TYPE CAN NOT BE SAME");
//                    }
//                }
//            }
//        }




//        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
//            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getIsAllocated().equalsIgnoreCase("1")) {
//                String allocationTypeId = "";
//                if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId().equalsIgnoreCase(("ALL_106"))) {
//                    allocationTypeId = "ALL_101";
//                } else if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId().equalsIgnoreCase(("ALL_107"))) {
//                    allocationTypeId = "ALL_102";
//                }
//                List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDelete(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), allocationTypeId, "Approved", "0");
//
//                for (Integer m = 0; m < budgetAllocationDetailsList.size(); m++) {
//
//                    BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsList.get(m);
//                    budgetAllocationDetails.setIsDelete("1");
//                    budgetAllocationDetailsRepository.save(budgetAllocationDetails);
//                }
//            }
//        }


        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getIsAllocated().equalsIgnoreCase("1")) {
                BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                double totalBalanceAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()) + Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount());

                budgetAllocationDetails.setAllocationId(HelperUtils.getAllocationId());
                budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount() + ""));
                budgetAllocationDetails.setBalanceAmount(ConverterUtils.addDecimalPoint(totalBalanceAmount + ""));
                budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                budgetAllocationDetails.setAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
                budgetAllocationDetails.setFinYear(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
                budgetAllocationDetails.setFromUnit(hrData.getUnitId());
                budgetAllocationDetails.setToUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
                budgetAllocationDetails.setSubHead(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId());
                budgetAllocationDetails.setStatus("Pending");
                budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocationDetails.setAuthGroupId(authGrouPid);
                budgetAllocationDetails.setRemarks(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemark());
                budgetAllocationDetails.setIsDelete("0");
                budgetAllocationDetails.setRevisedAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount()));
                budgetAllocationDetails.setRefTransactionId(refTransID);
                budgetAllocationDetails.setUserId(hrData.getPid());
                budgetAllocationDetails.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
                budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());

                budgetAllocationDetailsRepository.save(budgetAllocationDetails);
            } else {
                List<BudgetAllocation> budgetAllocationDetailsList = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Approved", "0");
                AmountUnit allocationAmountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
                for (Integer m = 0; m < budgetAllocationDetailsList.size(); m++) {
                    BudgetAllocation budgetAllocationDetails = budgetAllocationDetailsList.get(m);
                    AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationDetails.getAmountType());

                    double remineningBalance = Double.parseDouble(budgetAllocationDetails.getBalanceAmount()) * amountUnit.getAmount();

                    remineningBalance = remineningBalance + Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount()) * allocationAmountUnit.getAmount();

                    budgetAllocationDetails.setBalanceAmount(ConverterUtils.addDecimalPoint(remineningBalance / amountUnit.getAmount() + ""));
                    budgetAllocationDetails.setRevisedAmount("" + Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount()));
                    budgetAllocationRepository.save(budgetAllocationDetails);
                }
            }

        }


        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Budget Revision");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrData.getUnitId());
        mangeInboxOutbox.setFromUnit(hrData.getUnitId());
        mangeInboxOutbox.setGroupId(authGrouPid);
        mangeInboxOutbox.setApproverpId("");
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setState("AP");
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Budget Revision save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }

    @Override
    public ApiResponse<List<SubHeadVotedOrChargedType>> getSubHeadType() {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        List<SubHeadVotedOrChargedType> subType = subHeadTypeRepository.findAll();
        return ResponseUtils.createSuccessResponse(subType, new TypeReference<List<SubHeadVotedOrChargedType>>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationResponse> getAllGroupIdAndUnitId(String groupId) {
        List<BudgetAllocationSubResponse> budgetAllocationList = new ArrayList<BudgetAllocationSubResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }
        if (groupId == null || groupId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "GROUP ID CAN NOT BE BLANK");
        }

        List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndIsFlagAndToUnit(groupId, "0", hrData.getUnitId());

        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(i);

            BudgetAllocationSubResponse budgetAllocationReport = new BudgetAllocationSubResponse();
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setAllocationId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setTransactionId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
            budgetAllocationReport.setStatus(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setRemarks(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setRefTransactionId(budgetAllocationSubReport.getRefTransId());
            budgetAllocationReport.setUserId(budgetAllocationSubReport.getUserId());
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            budgetAllocationReport.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());
            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            budgetAllocationReport.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocationTypeId()));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));


            double allocationAmount = 0;
            AmountUnit remeningBalanceUnit = null;
            List<BudgetAllocation> modBudgetAllocations = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(hrData.getUnitId(), budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), budgetAllocationSubReport.getAllocationTypeId(), "Approved", "0");
            for (Integer m = 0; m < modBudgetAllocations.size(); m++) {

                remeningBalanceUnit = amountUnitRepository.findByAmountTypeId(modBudgetAllocations.get(m).getAmountType());
                allocationAmount = allocationAmount + (Double.parseDouble(modBudgetAllocations.get(m).getBalanceAmount()));

            }
            budgetAllocationReport.setRemeningBalanceUnit(remeningBalanceUnit);
            budgetAllocationReport.setBalanceAmount(ConverterUtils.addDecimalPoint(allocationAmount + ""));


            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), budgetAllocationSubReport.getToUnit(), "0");
            if (cdaParkingList.size() > 0) {
                budgetAllocationReport.setIsCDAparking("1");
                budgetAllocationReport.setCdaList(cdaParkingList);
            } else {
                budgetAllocationReport.setIsCDAparking("0");
                budgetAllocationReport.setCdaList(cdaParkingList);
            }

            budgetAllocationList.add(budgetAllocationReport);

        }

        Collections.sort(budgetAllocationList, new Comparator<BudgetAllocationSubResponse>() {
            public int compare(BudgetAllocationSubResponse v1, BudgetAllocationSubResponse v2) {
                return v1.getSubHead().getSerialNumber().compareTo(v2.getSubHead().getSerialNumber());
            }
        });


        BudgetAllocationResponse budgetAllocationResponse = new BudgetAllocationResponse();

        List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(groupId);

        budgetAllocationResponse.setAuthList(authoritiesList);
        budgetAllocationResponse.setBudgetResponseist(budgetAllocationList);


        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<BudgetAllocationResponse>() {
        });
    }

    @Override
    public ApiResponse<List<BudgetReviResp>> getBudgetRevisionData(BudgetReviReq budgetRivRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        List<BudgetReviResp> budgetRevision = new ArrayList<BudgetReviResp>();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO GET RIVISION DATA");
        }
        if (budgetRivRequest.getBudgetFinancialYearId() == null || budgetRivRequest.getBudgetFinancialYearId().isEmpty()) {
            return ResponseUtils.createFailureResponse(budgetRevision, new TypeReference<List<BudgetReviResp>>() {
            }, "FINANCIAL YEAR ID CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (budgetRivRequest.getSubHead() == null || budgetRivRequest.getSubHead().isEmpty()) {
            return ResponseUtils.createFailureResponse(budgetRevision, new TypeReference<List<BudgetReviResp>>() {
            }, "SUB HEAD ID CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (budgetRivRequest.getAllocTypeId() == null || budgetRivRequest.getAllocTypeId().isEmpty()) {
            return ResponseUtils.createFailureResponse(budgetRevision, new TypeReference<List<BudgetReviResp>>() {
            }, "ALLOCATION TYPE0 CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());
        if (cgUnit == null) {
            return ResponseUtils.createFailureResponse(budgetRevision, new TypeReference<List<BudgetReviResp>>() {
            }, "USER UNIT IS INVALID.PLEASE CHECK", HttpStatus.OK.value());
        }

        List<CgUnit> unit = new ArrayList<>();
        if (hrData.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
            unit = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());
        } else {
            unit = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getUnit());
        }
        if (unit.size() <= 0) {
            return ResponseUtils.createFailureResponse(budgetRevision, new TypeReference<List<BudgetReviResp>>() {
            }, "UNIT NOT FOUND", HttpStatus.OK.value());
        }

        for (Integer i = 0; i < unit.size(); i++) {

            List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(unit.get(i).getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), budgetRivRequest.getAllocTypeId(), "Approved", "0");
            for (Integer m = 0; m < budgetAllocationsDetalis.size(); m++) {
                BudgetReviResp res = new BudgetReviResp();
                res.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetalis.get(m).getToUnit()));
                res.setAllocationAmount(budgetAllocationsDetalis.get(m).getAllocationAmount());
                res.setBalAmount(budgetAllocationsDetalis.get(m).getBalanceAmount());
                res.setUnloackedAmount(budgetAllocationsDetalis.get(m).getUnallocatedAmount());
                res.setStatus(budgetAllocationsDetalis.get(m).getStatus());
                res.setFlag(budgetAllocationsDetalis.get(m).getIsFlag());
                res.setAmountType(amountUnitRepository.findByAmountTypeId(budgetAllocationsDetalis.get(m).getAmountType()));
                budgetRevision.add(res);
            }

        }


        if (budgetRevision.size() <= 1) {
            budgetRevision.clear();
            return ResponseUtils.createFailureResponse(budgetRevision, new TypeReference<List<BudgetReviResp>>() {
            }, "RECORD NOT FOUND", HttpStatus.OK.value());
        }
        return ResponseUtils.createSuccessResponse(budgetRevision, new TypeReference<List<BudgetReviResp>>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationSaveResponse> budgetDelete(BudgetDeleteRequest budgetDeleteRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {
            if (hrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
            }
        }

        if (budgetDeleteRequest.getUnitId() == null || budgetDeleteRequest.getUnitId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT ID CAN NOT BE BLANK");
        }

        if (budgetDeleteRequest.getTransactionId() == null || budgetDeleteRequest.getTransactionId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT ID CAN NOT BE BLANK");
        }

        CgUnit cgToUnit = cgUnitRepository.findByUnit(budgetDeleteRequest.getUnitId());
        if (cgToUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
        }


        BudgetAllocationDetails allocationDetails = budgetAllocationDetailsRepository.findByTransactionIdAndIsDelete(budgetDeleteRequest.getTransactionId(), "0");
        if (allocationDetails == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TRANSACTION ID");
        }

        if (allocationDetails.getIsDelete().equalsIgnoreCase("1")) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "Data already deleted");
        }

        allocationDetails.setIsDelete("1");
        allocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());

        budgetAllocationDetailsRepository.save(allocationDetails);

        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();
        response.setMsg("Data Delete Successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationSaveResponse> approveBudgetOrReject(BudgetApproveRequest budgetApproveRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        if (budgetApproveRequest.getAuthGroupId() == null || budgetApproveRequest.getAuthGroupId().isEmpty()) {

            if (budgetApproveRequest.getTransactionId() == null || budgetApproveRequest.getTransactionId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO TRANSACTION ID ID");
            }

        }


        if (budgetApproveRequest.getStatus() == null || budgetApproveRequest.getStatus().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "STATUS CAN NOT BE BLANK");
        }

        if (budgetApproveRequest.getAuthGroupId() == null || budgetApproveRequest.getAuthGroupId().isEmpty()) {
            BudgetAllocationDetails allocationDetails = budgetAllocationDetailsRepository.findByTransactionIdAndIsDelete(budgetApproveRequest.getAuthGroupId(), "0");
            if (allocationDetails == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
            }


            if (allocationDetails.getStatus().equalsIgnoreCase("Pending")) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "APPROVED BUDGET  BILL CAN NOT BE UPDATED");
            }

            if (allocationDetails.getIsDelete().equalsIgnoreCase("1")) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DATA ALREADY DELETED");
            }


            if (budgetApproveRequest.getStatus().contains("Rejected") || budgetApproveRequest.getStatus().contains("Approved")) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID STATUS");
            }

            if (budgetApproveRequest.getStatus().equalsIgnoreCase("Rejected")) {
                if (budgetApproveRequest.getRemarks() == null || budgetApproveRequest.getRemarks().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
                }
            }


            String status = "";


            if (budgetApproveRequest.getStatus().equalsIgnoreCase("Approved")) {
                BudgetAllocationReport budgetReport = new BudgetAllocationReport();

                budgetReport.setBgId(HelperUtils.getBudgetAlloctionReportId());

                budgetReport.setAllocationDate(ConverterUtils.convertDate(allocationDetails.getAllocationDate()));


                BudgetHead subHeadData = subHeadRepository.findByBudgetCodeId(allocationDetails.getSubHead());
                budgetReport.setSubHeadDes(subHeadData.getSubHeadDescr());

                AllocationType allocationType = allocationRepository.findByAllocTypeId(allocationDetails.getAllocTypeId());
                budgetReport.setAllocationType(allocationType.getAllocDesc());

                budgetReport.setRevisedAmount(allocationDetails.getRevisedAmount());

                BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(allocationDetails.getFinYear());
                budgetReport.setFinYearDes(budgetFinancialYear.getFinYear());

                CgUnit cgToUnit = cgUnitRepository.findByUnit(allocationDetails.getToUnit());
                budgetReport.setUnitName(cgToUnit.getDescr());
                budgetReport.setIsFlag("0");
                budgetReport.setSubHeadId(allocationDetails.getSubHead());
                budgetReport.setAllocationTypeId(allocationDetails.getAllocTypeId());
                budgetReport.setFinYearId(allocationDetails.getFinYear());
                budgetReport.setTotalAmount(ConverterUtils.addDecimalPoint(allocationDetails.getAllocationAmount()));
                budgetReport.setUnitId(allocationDetails.getToUnit());
                budgetReport.setAuthGroupId(allocationDetails.getAuthGroupId());
                budgetReport.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                budgetReport.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                budgetReport.setAmountType(allocationDetails.getAmountType());
                budgetReport.setRemark(budgetApproveRequest.getRemarks());
                budgetAllocationReportRepository.save(budgetReport);
            }

            BudgetAllocationDetails allocationData = allocationDetails;
            status = budgetApproveRequest.getStatus();
            allocationData.setRemarks(budgetApproveRequest.getRemarks());
            allocationData.setStatus(budgetApproveRequest.getStatus());
            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocationData);


            if (budgetApproveRequest.getStatus().equalsIgnoreCase("Approved")) {

                BudgetAllocation budgetAllocation = new BudgetAllocation();
                budgetAllocation.setAllocationId(HelperUtils.getAllocationId());
                budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                budgetAllocation.setIsFlag("0");
                budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocation.setRefTransId(allocationDetails.getRefTransactionId());
                budgetAllocation.setFinYear(allocationDetails.getFinYear());
                budgetAllocation.setToUnit(allocationDetails.getToUnit());
                budgetAllocation.setSubHead(allocationDetails.getSubHead());
                budgetAllocation.setAllocationTypeId(allocationDetails.getAllocTypeId());
                budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(allocationDetails.getAllocationAmount()));
                budgetAllocation.setUnallocatedAmount("0");
                budgetAllocation.setBalanceAmount(ConverterUtils.addDecimalPoint(allocationDetails.getAllocationAmount()));
                budgetAllocation.setUserId(allocationDetails.getUserId());
                budgetAllocation.setStatus("Pending");
                budgetAllocation.setAmountType(allocationDetails.getAmountType());
                budgetAllocation.setAuthGroupId(allocationDetails.getAuthGroupId());

                budgetAllocationRepository.save(budgetAllocation);

            }


            MangeInboxOutbox mangeInboxOutbox = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
            if (mangeInboxOutbox != null) {
                String toUnit = mangeInboxOutbox.getToUnit();
                String fromUnit = mangeInboxOutbox.getFromUnit();
                mangeInboxOutbox.setFromUnit(toUnit);
                mangeInboxOutbox.setToUnit(fromUnit);
                mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                mangeInboxOutbox.setStatus(budgetApproveRequest.getStatus());
                if (status.equalsIgnoreCase("Approved")) {
                    mangeInboxOutbox.setState("CR");
                } else {
                    mangeInboxOutbox.setState("AP");
                }

                mangeInboxOutBoxRepository.save(mangeInboxOutbox);

            }

        } else {
            List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(budgetApproveRequest.getAuthGroupId(), "0");
            if (allocationDetails.size() == 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
            }

            for (Integer i = 0; i < allocationDetails.size(); i++) {

                if (allocationDetails.get(i).getStatus().equalsIgnoreCase("Pending")) {

                } else {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "APPROVED BUDGET  BILL CAN NOT BE UPDATED");
                }

                if (allocationDetails.get(i).getIsDelete().equalsIgnoreCase("1")) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DATA ALREADY DELETED");
                }
            }


            if (budgetApproveRequest.getStatus().contains("Rejected") || budgetApproveRequest.getStatus().contains("Approved")) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID STATUS");
            }

            if (budgetApproveRequest.getStatus().equalsIgnoreCase("Rejected")) {
                if (budgetApproveRequest.getRemarks() == null || budgetApproveRequest.getRemarks().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
                }
            }


            String status = "";

            for (Integer i = 0; i < allocationDetails.size(); i++) {

                if (budgetApproveRequest.getStatus().equalsIgnoreCase("Approved")) {
                    BudgetAllocationReport budgetReport = new BudgetAllocationReport();

                    budgetReport.setBgId(HelperUtils.getBudgetAlloctionReportId());

                    budgetReport.setAllocationDate(ConverterUtils.convertDate(allocationDetails.get(i).getAllocationDate()));


                    BudgetHead subHeadData = subHeadRepository.findByBudgetCodeId(allocationDetails.get(i).getSubHead());
                    budgetReport.setSubHeadDes(subHeadData.getSubHeadDescr());

                    AllocationType allocationType = allocationRepository.findByAllocTypeId(allocationDetails.get(i).getAllocTypeId());
                    budgetReport.setAllocationType(allocationType.getAllocDesc());


                    budgetReport.setRevisedAmount(allocationDetails.get(i).getRevisedAmount());


                    BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(allocationDetails.get(i).getFinYear());
                    budgetReport.setFinYearDes(budgetFinancialYear.getFinYear());

                    CgUnit cgToUnit = cgUnitRepository.findByUnit(allocationDetails.get(i).getToUnit());
                    budgetReport.setUnitName(cgToUnit.getDescr());
                    budgetReport.setIsFlag("0");
                    budgetReport.setSubHeadId(allocationDetails.get(i).getSubHead());
                    budgetReport.setAllocationTypeId(allocationDetails.get(i).getAllocTypeId());
                    budgetReport.setFinYearId(allocationDetails.get(i).getFinYear());
                    budgetReport.setTotalAmount(ConverterUtils.addDecimalPoint(allocationDetails.get(i).getAllocationAmount()));
                    budgetReport.setUnitId(allocationDetails.get(i).getToUnit());
                    budgetReport.setAuthGroupId(allocationDetails.get(i).getAuthGroupId());
                    budgetReport.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetReport.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetReport.setAmountType(allocationDetails.get(i).getAmountType());
                    budgetReport.setRemark(budgetApproveRequest.getRemarks());
                    budgetAllocationReportRepository.save(budgetReport);
                }

                BudgetAllocationDetails allocationData = allocationDetails.get(i);
                status = budgetApproveRequest.getStatus();
                allocationData.setRemarks(budgetApproveRequest.getRemarks());
                allocationData.setStatus(budgetApproveRequest.getStatus());
                allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocationDetailsRepository.save(allocationData);


                if (budgetApproveRequest.getStatus().equalsIgnoreCase("Approved")) {

                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getAllocationId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(allocationDetails.get(i).getRefTransactionId());
                    budgetAllocation.setFinYear(allocationDetails.get(i).getFinYear());
                    budgetAllocation.setToUnit(allocationDetails.get(i).getToUnit());
                    budgetAllocation.setSubHead(allocationDetails.get(i).getSubHead());
                    budgetAllocation.setAllocationTypeId(allocationDetails.get(i).getAllocTypeId());
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(allocationDetails.get(i).getAllocationAmount()));
                    budgetAllocation.setUnallocatedAmount("0");
                    budgetAllocation.setBalanceAmount(ConverterUtils.addDecimalPoint(allocationDetails.get(i).getAllocationAmount()));
                    budgetAllocation.setUserId(allocationDetails.get(i).getUserId());
                    budgetAllocation.setStatus("Pending");
                    budgetAllocation.setAmountType(allocationDetails.get(i).getAmountType());
                    budgetAllocation.setAuthGroupId(allocationDetails.get(i).getAuthGroupId());

                    budgetAllocationRepository.save(budgetAllocation);

                }


            }

            MangeInboxOutbox mangeInboxOutbox = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
            if (mangeInboxOutbox != null) {
                String toUnit = mangeInboxOutbox.getToUnit();
                String fromUnit = mangeInboxOutbox.getFromUnit();
                mangeInboxOutbox.setFromUnit(toUnit);
                mangeInboxOutbox.setToUnit(fromUnit);
                mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                mangeInboxOutbox.setStatus(budgetApproveRequest.getStatus());
                if (status.equalsIgnoreCase("Approved")) {
                    mangeInboxOutbox.setState("CR");
                } else {
                    mangeInboxOutbox.setState("AP");
                }

                mangeInboxOutBoxRepository.save(mangeInboxOutbox);

            }
        }


        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();
        response.setMsg("Data " + budgetApproveRequest.getStatus() + " Successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    public ApiResponse<BudgetAllocationSaveResponse> approveRivisonBudgetOrReject(BudgetApproveRequest budgetApproveRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        if (budgetApproveRequest.getAuthGroupId() == null || budgetApproveRequest.getAuthGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }


        if (budgetApproveRequest.getStatus() == null || budgetApproveRequest.getStatus().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "STATUS CAN NOT BE BLANK");
        }


        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(budgetApproveRequest.getAuthGroupId(), "0");


        if (allocationDetails.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }

        for (Integer i = 0; i < allocationDetails.size(); i++) {

            if (allocationDetails.get(i).getStatus().equalsIgnoreCase("Pending")) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "APPROVED BUDGET  BILL CAN NOT BE UPDATED");
            }

            if (allocationDetails.get(i).getIsDelete().equalsIgnoreCase("1")) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DATA ALREADY DELETED");
            }
        }


        if (budgetApproveRequest.getStatus().contains("Rejected") || budgetApproveRequest.getStatus().contains("Approved")) {

        } else {

            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID STATUS");
        }

        if (budgetApproveRequest.getStatus().equalsIgnoreCase("Rejected")) {
            if (budgetApproveRequest.getRemarks() == null || budgetApproveRequest.getRemarks().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
            }
        }

        for (Integer i = 0; i < allocationDetails.size(); i++) {
            String allocationTypeId = "";
            BudgetAllocationDetails budgetAllocationDetails = allocationDetails.get(i);
            if (budgetAllocationDetails.getAllocTypeId().equalsIgnoreCase(("ALL_106"))) {
                allocationTypeId = "ALL_101";
            } else if (budgetAllocationDetails.getAllocTypeId().equalsIgnoreCase(("ALL_107"))) {
                allocationTypeId = "ALL_102";
            }

            List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(budgetAllocationDetails.getToUnit(), budgetAllocationDetails.getFinYear(), budgetAllocationDetails.getSubHead(), allocationTypeId, "Approved", "0");
            if (data.size() == 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DATA.PLEASE CHECK YOUR ADMINISTRATOR.01");
            }


            List<BudgetAllocationReport> dataReport = budgetAllocationReportRepository.findByUnitIdAndFinYearIdAndAllocationTypeIdAndSubHeadIdAndIsFlag(allocationDetails.get(i).getToUnit(), allocationDetails.get(i).getFinYear(), allocationTypeId, allocationDetails.get(i).getSubHead(), "0");
            if (dataReport.size() == 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DATA.PLEASE CHECK YOUR ADMINISTRATOR.02");
            }

        }


        String status = "";

        for (Integer i = 0; i < allocationDetails.size(); i++) {

            if (budgetApproveRequest.getStatus().equalsIgnoreCase("Approved")) {
                String allocationTypeId = "";
                if (allocationDetails.get(i).getAllocTypeId().equalsIgnoreCase(("ALL_106"))) {
                    allocationTypeId = "ALL_101";
                } else if (allocationDetails.get(i).getAllocTypeId().equalsIgnoreCase(("ALL_107"))) {
                    allocationTypeId = "ALL_102";
                }

                List<BudgetAllocationReport> data = budgetAllocationReportRepository.findByUnitIdAndFinYearIdAndAllocationTypeIdAndSubHeadIdAndIsFlag(allocationDetails.get(i).getToUnit(), allocationDetails.get(i).getFinYear(), allocationTypeId, allocationDetails.get(i).getSubHead(), "0");
                for (Integer m = 0; m < data.size(); m++) {
                    data.get(m).setIsFlag("1");
                    budgetAllocationReportRepository.save(data.get(m));
                }


                BudgetAllocationReport budgetReport = new BudgetAllocationReport();

                budgetReport.setBgId(HelperUtils.getBudgetAlloctionReportId());
                budgetReport.setAllocationDate(ConverterUtils.convertDate(allocationDetails.get(i).getAllocationDate()));

                BudgetHead subHeadData = subHeadRepository.findByBudgetCodeId(allocationDetails.get(i).getSubHead());
                budgetReport.setSubHeadDes(subHeadData.getSubHeadDescr());

                AllocationType allocationType = allocationRepository.findByAllocTypeId(allocationDetails.get(i).getAllocTypeId());
                budgetReport.setAllocationType(allocationType.getAllocDesc());

                budgetReport.setRevisedAmount(ConverterUtils.addDecimalPoint(allocationDetails.get(i).getRevisedAmount()));

                BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(allocationDetails.get(i).getFinYear());
                budgetReport.setFinYearDes(budgetFinancialYear.getFinYear());

                CgUnit cgToUnit = cgUnitRepository.findByUnit(allocationDetails.get(i).getToUnit());
                budgetReport.setUnitName(cgToUnit.getDescr());
                budgetReport.setIsFlag("0");
                budgetReport.setRevisedAmount(ConverterUtils.addDecimalPoint(allocationDetails.get(i).getRevisedAmount()));

                budgetReport.setSubHeadId(allocationDetails.get(i).getSubHead());
                budgetReport.setAllocationTypeId(allocationDetails.get(i).getAllocTypeId());
                budgetReport.setFinYearId(allocationDetails.get(i).getFinYear());
                budgetReport.setTotalAmount(ConverterUtils.addDecimalPoint(allocationDetails.get(i).getAllocationAmount()));
                budgetReport.setUnitId(allocationDetails.get(i).getToUnit());
                budgetReport.setAuthGroupId(allocationDetails.get(i).getAuthGroupId());
                budgetReport.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                budgetReport.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                budgetReport.setAmountType(allocationDetails.get(i).getAmountType());
                budgetReport.setRemark(budgetApproveRequest.getRemarks());
                budgetAllocationReportRepository.save(budgetReport);
            }

            BudgetAllocationDetails allocationData = allocationDetails.get(i);
            status = budgetApproveRequest.getStatus();
            allocationData.setRemarks(budgetApproveRequest.getRemarks());
            allocationData.setStatus(budgetApproveRequest.getStatus());
            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocationData);


            if (budgetApproveRequest.getStatus().equalsIgnoreCase("Approved")) {

                String allocationTypeId = "";
                if (allocationDetails.get(i).getAllocTypeId().equalsIgnoreCase(("ALL_106"))) {
                    allocationTypeId = "ALL_101";
                } else if (allocationDetails.get(i).getAllocTypeId().equalsIgnoreCase(("ALL_107"))) {
                    allocationTypeId = "ALL_102";
                }

                List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(allocationDetails.get(i).getToUnit(), allocationDetails.get(i).getFinYear(), allocationDetails.get(i).getSubHead(), allocationTypeId, "Approved", "0");
                for (Integer m = 0; m < data.size(); m++) {
                    data.get(m).setIsFlag("1");
                    budgetAllocationRepository.save(data.get(m));
                }


                BudgetAllocation budgetAllocation = new BudgetAllocation();
                budgetAllocation.setAllocationId(HelperUtils.getAllocationId());
                budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                budgetAllocation.setIsFlag("0");
                budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocation.setRefTransId(allocationDetails.get(i).getRefTransactionId());
                budgetAllocation.setFinYear(allocationDetails.get(i).getFinYear());
                budgetAllocation.setToUnit(allocationDetails.get(i).getToUnit());
                budgetAllocation.setSubHead(allocationDetails.get(i).getSubHead());
                budgetAllocation.setAllocationTypeId(allocationDetails.get(i).getAllocTypeId());
                budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(allocationDetails.get(i).getAllocationAmount()));
                budgetAllocation.setUnallocatedAmount("0.0000");

                double amount = Double.parseDouble(allocationDetails.get(i).getAllocationAmount()) + Double.parseDouble(allocationDetails.get(i).getRevisedAmount());
                budgetAllocation.setBalanceAmount(ConverterUtils.addDecimalPoint(amount + ""));
                budgetAllocation.setUserId(allocationDetails.get(i).getUserId());
                budgetAllocation.setStatus("Pending");
                budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(allocationDetails.get(i).getRevisedAmount()));
                budgetAllocation.setAmountType(allocationDetails.get(i).getAmountType());
                budgetAllocation.setAuthGroupId(allocationDetails.get(i).getAuthGroupId());

                budgetAllocationRepository.save(budgetAllocation);

            }


        }

        MangeInboxOutbox mangeInboxOutbox = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
        if (mangeInboxOutbox != null) {
            String toUnit = mangeInboxOutbox.getToUnit();
            String fromUnit = mangeInboxOutbox.getFromUnit();
            mangeInboxOutbox.setFromUnit(toUnit);
            mangeInboxOutbox.setToUnit(fromUnit);
            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setStatus(budgetApproveRequest.getStatus());
            if (status.equalsIgnoreCase("Approved")) {
                mangeInboxOutbox.setState("CR");
            } else {
                mangeInboxOutbox.setState("AP");
            }

            mangeInboxOutBoxRepository.save(mangeInboxOutbox);

        }


        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();
        response.setMsg("Data " + budgetApproveRequest.getStatus() + " Successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    public ApiResponse<AvilableFundResponse> findAvailableAmount(GetAmountRequest budgetHeadId) {
        AvilableFundResponse response = new AvilableFundResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }


        CgUnit cgUnitData = cgUnitRepository.findByUnit(hrData.getUnitId());


        if (budgetHeadId.getBudgetHeadId() == null || budgetHeadId.getBudgetHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }


        BudgetHead subHeadData = subHeadRepository.findByBudgetCodeId(budgetHeadId.getBudgetHeadId());
        if (subHeadData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), " INVALID SUB HEAD ID .");
        }

        List<BudgetAllocation> modBudgetAllocations = budgetAllocationRepository.findByToUnitAndSubHeadAndIsFlag(hrData.getUnitId(), budgetHeadId.getBudgetHeadId(), "0");

        AmountUnit amountUnit = null;
        if (modBudgetAllocations.size() == 0) {
            response.setFundAvailable("0");
        } else {
            double balanceAmount = 0;
            double allocationAmount = 0;
            for (Integer i = 0; i < modBudgetAllocations.size(); i++) {

                amountUnit = amountUnitRepository.findByAmountTypeId(modBudgetAllocations.get(i).getAmountType());
                balanceAmount = balanceAmount + Double.parseDouble(modBudgetAllocations.get(i).getBalanceAmount());
                allocationAmount = allocationAmount + Double.parseDouble(modBudgetAllocations.get(i).getAllocationAmount());
            }
            response.setFundAvailable(ConverterUtils.addDecimalPoint(balanceAmount + ""));
            response.setFundallocated(ConverterUtils.addDecimalPoint(allocationAmount + ""));
            response.setAmountUnit(amountUnit);
        }

        response.setUnitName(cgUnitData.getCgUnitShort());


        List<ContigentBill> cbExpendure = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlag(hrData.getUnitId(), budgetHeadId.getBudgetHeadId(), "0");
        if (cbExpendure.size() == 0) {
            response.setExpenditure("0.0000");
        } else {
            double expenditure = 0;
            for (Integer i = 0; i < cbExpendure.size(); i++) {
                expenditure = expenditure + Double.parseDouble(cbExpendure.get(i).getCbAmount());
            }
            response.setExpenditure(ConverterUtils.addDecimalPoint(expenditure + ""));
        }


        return ResponseUtils.createSuccessResponse(response, new TypeReference<AvilableFundResponse>() {
        });
    }

    @Override
    public ApiResponse<AvilableFundResponse> getAvailableFundData() {
        AvilableFundResponse response = new AvilableFundResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }


        CgUnit cgUnitData = cgUnitRepository.findByUnit(hrData.getUnitId());
        if (cgUnitData == null) {
            throw new SDDException(HttpStatus.BAD_REQUEST.value(), "INVALID CG_UNIT DATA");
        }

        response.setUnitName(cgUnitData.getCgUnitShort());

        List<BudgetAllocationDetails> modBudgetAllocations = budgetAllocationDetailsRepository.findByToUnitAndIsDelete(HelperUtils.HEADUNITID, "0");
        if (modBudgetAllocations.size() == 0) {
            response.setFundAvailable("0");
        } else {
            double allocationAmount = 0;
            response.setFundAvailable("0");
            for (Integer i = 0; i < modBudgetAllocations.size(); i++) {
                allocationAmount = allocationAmount + Double.parseDouble(modBudgetAllocations.get(i).getAllocationAmount());
            }
            response.setFundAvailable(ConverterUtils.addDecimalPoint(allocationAmount + ""));
        }

        response.setUnitName(cgUnitData.getCgUnitShort());

        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByToUnitAndIsDelete(hrData.getUnitId(), "0");
        if (budgetAllocations.size() == 0) {

            response.setPreviousAllocation("0.0000");
            response.setUnallocatedAmount("0.0000");

        } else {
            double previousAmount = 0;
            response.setFundAvailable("0.0000");
            for (Integer i = 0; i < budgetAllocations.size(); i++) {
                previousAmount = previousAmount + Double.parseDouble(budgetAllocations.get(i).getAllocationAmount());
            }
            response.setFundAvailable(ConverterUtils.addDecimalPoint(previousAmount + ""));
            response.setUnallocatedAmount("0.0000");
        }

        return ResponseUtils.createSuccessResponse(response, new TypeReference<AvilableFundResponse>() {
        });
    }

    @Override
    public ApiResponse<AvilableFundResponse> getAvailableFundFindByUnitIdAndFinYearId(GetAvilableFundRequest getAvilableFundRequest) {
        AvilableFundResponse response = new AvilableFundResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }


        if (getAvilableFundRequest.getFinYearId() == null || getAvilableFundRequest.getFinYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET FINANCIAL ID CAN NOT BE BLANK");
        }

        if (getAvilableFundRequest.getAllocationTypeId() == null || getAvilableFundRequest.getAllocationTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID CAN NOT BE BLANK");
        }

        if (getAvilableFundRequest.getSubHeadId() == null || getAvilableFundRequest.getSubHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }

        CgUnit checkUnitData = cgUnitRepository.findByUnit(hrData.getUnitId());
        if (checkUnitData == null) {
            throw new SDDException(HttpStatus.BAD_REQUEST.value(), "INVALID CG_UNIT DATA");
        }

        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(getAvilableFundRequest.getFinYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        BudgetHead subHeadData = subHeadRepository.findByBudgetCodeId(getAvilableFundRequest.getSubHeadId());
        if (subHeadData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SUB HEAD ID");

        }


        response.setUnitName(checkUnitData.getCgUnitShort());


        List<BudgetAllocation> modBudgetAllocations = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(hrData.getUnitId(), getAvilableFundRequest.getFinYearId(), getAvilableFundRequest.getSubHeadId(), getAvilableFundRequest.getAllocationTypeId(), "Approved", "0");
        if (modBudgetAllocations.size() == 0) {
            response.setFundAvailable("0");
        } else {
            double allocationAmount = 0;

            for (Integer m = 0; m < modBudgetAllocations.size(); m++) {

                response.setFundAvailable("0.0000");
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(modBudgetAllocations.get(m).getAmountType());
                allocationAmount = allocationAmount + (Double.parseDouble(modBudgetAllocations.get(m).getBalanceAmount()) * amountUnit.getAmount());
                response.setFundAvailable(allocationAmount + "");

            }
        }


        return ResponseUtils.createSuccessResponse(response, new TypeReference<AvilableFundResponse>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetAllocationSubHeadWise(BudgetAllocationSaveRequest budgetAllocationSaveRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {
            if (hrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
            }
        }
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();


        if (budgetAllocationSaveRequest.getBudgetRequest().size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND. PLEASE TRY AGAIN");
        }


        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDelete(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId(), "0");
            if (budgetAllocationDetailsList.size() > 0) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationDetailsList.get(i).getToUnit());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALREADY ALLOCATED " + cgUnit.getDescr() + " . CAN NOT ASSIGN AGAIN");
            }
        }

        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


            if (budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId() == null || budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID ID CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount() == null || budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId() == null || budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET FINANCIAL ID ID CAN NOT BE BLANK");
            }


            if (budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId() == null || budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID ID CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId() == null || budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO UNIT CAN NOT BE BLANK");
            }

            BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId());
            if (budgetHeadId == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SUB HEAD ID");
            }

            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId());
            if (budgetFinancialYear == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
            }

            CgUnit cgToUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId());
            if (cgToUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
            }

            AllocationType allocationType = allocationRepository.findByAllocTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId());
            if (allocationType == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());
            if (amountUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
            }


            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId(), hrData.getUnitId(), "0");

            if (cdaParkingList.size() == 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PLEASE ADD CDA IN BUDGET RECEIPT.THAN TRY AGAIN");
            }

            List<BudgetAllocationDetails> budgetAloocation = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDelete(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId(), "0");
            if (budgetAloocation.size() > 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT ALREADY ALLOCATED.PLEASE DO REVISION OR CHECK YOUR INBOX/OUTBOX");

            }
        }


        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            double amount = 0;
            List<BudgetAllocation> budgetAloocation = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(hrData.getUnitId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId(), "Approved", "0");
            for (Integer m = 0; m < budgetAloocation.size(); m++) {
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAloocation.get(m).getAmountType());
                amount = amount + Double.parseDouble(budgetAloocation.get(m).getAllocationAmount()) * amountUnit.getAmount();
            }

            AmountUnit amountUnitMain = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());

            double allocationAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount()) * amountUnitMain.getAmount();
            BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId());

            if (allocationAmount > amount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT CAN NOT LARGER THAN REMAINING AMOUNT FOR " + budgetHeadId.getSubHeadDescr());
            }

        }


        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
            budgetAllocationDetails.setAllocationId(HelperUtils.getAllocationId());
            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAllocTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId());
            budgetAllocationDetails.setFinYear(budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId());
            budgetAllocationDetails.setFromUnit(hrData.getUnitId());
            budgetAllocationDetails.setToUnit(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId());
            budgetAllocationDetails.setSubHead(budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId());
            budgetAllocationDetails.setStatus("Pending");
            budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAuthGroupId(authGrouPid);
            budgetAllocationDetails.setRemarks(budgetAllocationSaveRequest.getBudgetRequest().get(i).getRemark());
            budgetAllocationDetails.setIsDelete("0");
            budgetAllocationDetails.setIsBudgetRevision("0");
            budgetAllocationDetails.setRevisedAmount("0.0000");
            budgetAllocationDetails.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setRefTransactionId(refTransID);
            budgetAllocationDetails.setUserId(hrData.getPid());
            budgetAllocationDetails.setAmountType(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());
            budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());

            budgetAllocationDetailsRepository.save(budgetAllocationDetails);
        }


        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            double amount = 0;
            List<BudgetAllocation> budgetAloocation = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(hrData.getUnitId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId(), "Approved", "0");
            for (Integer m = 0; m < budgetAloocation.size(); m++) {
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAloocation.get(m).getAmountType());
                amount = amount + (Double.parseDouble(budgetAloocation.get(m).getAllocationAmount()) * amountUnit.getAmount());
            }


            AmountUnit allocationUNit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());
            double allocationAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount()) * allocationUNit.getAmount();

            for (Integer m = 0; m < budgetAloocation.size(); m++) {

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAloocation.get(m).getAmountType());
                Double reminingBalance = (amount - allocationAmount);

                budgetAloocation.get(m).setBalanceAmount(ConverterUtils.addDecimalPoint((reminingBalance / amountUnit.getAmount()) + ""));
                budgetAllocationRepository.save(budgetAloocation.get(m));

            }

        }


        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Budget Allocation SubHead Wise");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrData.getUnitId());
        mangeInboxOutbox.setFromUnit(hrData.getUnitId());
        mangeInboxOutbox.setGroupId(authGrouPid);
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setState("AP");
        mangeInboxOutbox.setApproverpId("");
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setIsBgcg("BG");

        MangeInboxOutbox saveMangeApi = mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Data save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetAllocationUnitWise(BudgetAllocationSaveUnitRequest budgetAllocationSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {
            if (hrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
            }
        }

        if (budgetAllocationSaveRequestList.getBudgetRequest().size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND. PLEASE TRY AGAIN");

        }
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();


//        boolean data = checkDuplicateData(budgetAllocationSaveRequestList.getBudgetRequest());
//        if (data) {
//            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DUPLICATE DATA FOUND. PLEASE CHECK");
//
//        }

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDelete(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "0");
            if (budgetAllocationDetailsList.size() > 0) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationDetailsList.get(i).getToUnit());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALREADY ALLOCATED " + cgUnit.getDescr() + " . CAN NOT ASSIGN AGAIN");
            }
        }


        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID ID CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT CAN NOT BE BLANK");
            }


            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET FINANCIAL ID ID CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID ID CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO UNIT CAN NOT BE BLANK");
            }

            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
            if (budgetFinancialYear == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
            }

            CgUnit cgToUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
            if (cgToUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
            }
            AllocationType allocationType = allocationRepository.findByAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
            if (allocationType == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
            }
            BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId());
            if (budgetHeadId == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SUB HEAD ID");
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
            if (amountUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
            }


            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), hrData.getUnitId(), "0");

            if (cdaParkingList.size() <= 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PLEASE ADD CDA IN BUDGET RECEIPT.THAN TRY AGAIN");
            }

            List<BudgetAllocationDetails> budgetAloocation = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDelete(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "0");
            if (budgetAloocation.size() > 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT ALREADY ALLOCATED.PLEASE DO REVISION OR CHECK YOUR INBOX/OUTBOX");

            }
        }


        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            double amount = 0;
            List<BudgetAllocation> budgetAloocation = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(hrData.getUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Approved", "0");
            for (Integer m = 0; m < budgetAloocation.size(); m++) {

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAloocation.get(m).getAmountType());
                amount = amount + (Double.parseDouble(budgetAloocation.get(m).getAllocationAmount()) * amountUnit.getAmount());

            }

            AmountUnit allovcationAmountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());

            double allocationAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()) * allovcationAmountUnit.getAmount();
            BudgetHead budgetHeadIddata = subHeadRepository.findByBudgetCodeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId());

            if (allocationAmount > amount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT CAN NOT LARGER THAN REMAINING AMOUNT FOR " + budgetHeadIddata.getSubHeadDescr());
            }

        }


        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
            budgetAllocationDetails.setAllocationId(HelperUtils.getAllocationId());
            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
            budgetAllocationDetails.setFinYear(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
            budgetAllocationDetails.setFromUnit(hrData.getUnitId());
            budgetAllocationDetails.setToUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
            budgetAllocationDetails.setSubHead(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId());
            budgetAllocationDetails.setStatus("Pending");
            budgetAllocationDetails.setRemarks(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemark());
            budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAuthGroupId(authGrouPid);
            budgetAllocationDetails.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setRefTransactionId(refTransID);
            budgetAllocationDetails.setUserId(hrData.getPid());
            budgetAllocationDetails.setIsDelete("0");
            budgetAllocationDetails.setIsBudgetRevision("0");
            budgetAllocationDetails.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
            budgetAllocationDetails.setRevisedAmount("0.0000");
            budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());

            budgetAllocationDetailsRepository.save(budgetAllocationDetails);

        }


//remainingBalance


        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            double amount = 0;
            List<BudgetAllocation> budgetAloocation = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlag(hrData.getUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Approved", "0");

            for (Integer m = 0; m < budgetAloocation.size(); m++) {
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAloocation.get(m).getAmountType());
                amount = amount + (Double.parseDouble(budgetAloocation.get(m).getBalanceAmount()) * amountUnit.getAmount());
            }


            AmountUnit allocationUNit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
            double allocationAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()) * allocationUNit.getAmount();

            for (Integer m = 0; m < budgetAloocation.size(); m++) {
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAloocation.get(m).getAmountType());
                Double reminingBalance = (amount - allocationAmount);

                budgetAloocation.get(m).setBalanceAmount(ConverterUtils.addDecimalPoint((reminingBalance / amountUnit.getAmount()) + ""));
                budgetAllocationRepository.save(budgetAloocation.get(m));

            }

        }


        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Budget Allocation Unit Wise");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrData.getUnitId());
        mangeInboxOutbox.setFromUnit(hrData.getUnitId());
        mangeInboxOutbox.setGroupId(authGrouPid);
        mangeInboxOutbox.setApproverpId("");
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setState("AP");
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Data save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    public ApiResponse<List<FindBudgetResponse>> findBudgetAllocationFinYearAndUnit(FindBudgetRequest findBudgetRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

        List<FindBudgetResponse> findBudgetResponse = new ArrayList<FindBudgetResponse>();

        if (findBudgetRequest.getFinYearId() == null || findBudgetRequest.getFinYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR ID  CAN NOT BE BLANK");
        }

        if (findBudgetRequest.getToUnitId() == null || findBudgetRequest.getToUnitId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO UNIT ID CAN NOT BE BLANK");
        }


        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(findBudgetRequest.getFinYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        CgUnit cgUnit = cgUnitRepository.findByUnit(findBudgetRequest.getToUnitId());
        if (cgUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CG UNIT ID");
        }

        List<BudgetAllocationDetails> budgetAllocationsDetalis = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndIsDelete(findBudgetRequest.getToUnitId(), findBudgetRequest.getFinYearId(), "0");

        for (Integer i = 0; i < budgetAllocationsDetalis.size(); i++) {

            FindBudgetResponse authirtyResponse = new FindBudgetResponse();

            AllocationType allocationType = allocationRepository.findByAllocTypeId(budgetAllocationsDetalis.get(i).getAllocTypeId());
            BudgetHead subHeadData = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationsDetalis.get(i).getSubHead());
            CgUnit cgUnitData = cgUnitRepository.findByUnit(budgetAllocationsDetalis.get(i).getToUnit());

            authirtyResponse.setAllocationType(allocationType);
            authirtyResponse.setBudgetAllocationsDetalis(budgetAllocationsDetalis.get(i));
            authirtyResponse.setSubHead(subHeadData);
            authirtyResponse.setUnit(cgUnitData);
            authirtyResponse.setRemark(budgetAllocationsDetalis.get(i).getRemarks());
            authirtyResponse.setAmount(ConverterUtils.addDecimalPoint(budgetAllocationsDetalis.get(i).getAllocationAmount()));

//            Authority authority =  authorityRepository.findByAuthorityId(budgetAllocationsDetalis.get(i).getAuthId());
//
//            AuthirtyType authirtyType = authorityTypeRepository.findByAuthorityTypeId(authority.getAuthTypeId());
//            FileUpload fileUpload = fileUploadRepository.findByUploadID(authority.getDocId());


            findBudgetResponse.add(authirtyResponse);

        }

        return ResponseUtils.createSuccessResponse(findBudgetResponse, new TypeReference<List<FindBudgetResponse>>() {
        });
    }


    @Override
    public ApiResponse<List<BudgetRevisionResponse>> getBudgetRevisionData(BudgetAllocationReportRequest budgetAllocationReportRequest) {


        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        if (budgetAllocationReportRequest.getBudgetFinancialYearId() == null || budgetAllocationReportRequest.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR ID CAN NOT BE BLANK");
        }


        if (budgetAllocationReportRequest.getSubHead() == null || budgetAllocationReportRequest.getSubHead().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }

        BudgetHead subHeadData = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationReportRequest.getSubHead());
        if (subHeadData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SUB HEAD ID");
        }


        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetAllocationReportRequest.getBudgetFinancialYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        List<BudgetRevisionResponse> budgetRevision = new ArrayList<BudgetRevisionResponse>();

        List<BudgetAllocationDetails> budgetAllocationsDetalis = budgetAllocationDetailsRepository.findBySubHeadAndFinYearAndIsDeleteAndStatus(budgetAllocationReportRequest.getSubHead(), budgetAllocationReportRequest.getBudgetFinancialYearId(), "0", "Approved");


        HashMap<String, List<BudgetRevisionResponse>> hashMap = new HashMap<String, List<BudgetRevisionResponse>>();

        for (Integer i = 0; i < budgetAllocationsDetalis.size(); i++) {


            if (hashMap.containsKey(budgetAllocationsDetalis.get(i).getToUnit())) {
                List<BudgetRevisionResponse> reportMaindata = hashMap.get(budgetAllocationsDetalis.get(i).getToUnit());
                BudgetRevisionResponse budgetRevisionResponse = new BudgetRevisionResponse();
                budgetRevisionResponse.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetalis.get(i).getToUnit()));
                budgetRevisionResponse.setExistingAmount(ConverterUtils.addDecimalPoint(budgetAllocationsDetalis.get(i).getAllocationAmount()));

                reportMaindata.add(budgetRevisionResponse);
                hashMap.put(budgetAllocationsDetalis.get(i).getToUnit(), reportMaindata);
            } else {
                List<BudgetRevisionResponse> reportMaindata = new ArrayList<BudgetRevisionResponse>();
                BudgetRevisionResponse budgetRevisionResponse = new BudgetRevisionResponse();
                budgetRevisionResponse.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetalis.get(i).getToUnit()));
                budgetRevisionResponse.setExistingAmount(ConverterUtils.addDecimalPoint(budgetAllocationsDetalis.get(i).getAllocationAmount()));

                reportMaindata.add(budgetRevisionResponse);
                hashMap.put(budgetAllocationsDetalis.get(i).getToUnit(), reportMaindata);
            }

        }

        double totalAmount = 0;
        for (Map.Entry<String, List<BudgetRevisionResponse>> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            List<BudgetRevisionResponse> tabData = entry.getValue();

            BudgetRevisionResponse budgetRevisionData = new BudgetRevisionResponse();
            budgetRevisionData.setUnit(cgUnitRepository.findByUnit(key));


            for (Integer a = 0; a < tabData.size(); a++) {
                totalAmount = totalAmount + Double.parseDouble(tabData.get(a).getExistingAmount());
            }
            budgetRevisionData.setExistingAmount(totalAmount + "");
            budgetRevision.add(budgetRevisionData);
        }

        return ResponseUtils.createSuccessResponse(budgetRevision, new TypeReference<List<BudgetRevisionResponse>>() {
        });
    }

    @Override
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetRevisonData(BudgetAllocationSaveUnitRequest budgetAllocationSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {
            if (hrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
            }
        }


        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationSubRequest budgetAllocationSaveRequest = budgetAllocationSaveRequestList.getBudgetRequest().get(i);


            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID ID CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT CAN NOT BE BLANK");
            }


            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET FINANCIAL ID ID CAN NOT BE BLANK");
            }


            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID ID CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO UNIT CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REVISED AMOUNT CAN NOT BE BLANK");
            }

            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
            if (budgetFinancialYear == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
            }

            CgUnit cgToUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
            if (cgToUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
            }


            AllocationType allocationType = allocationRepository.findByAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
            if (allocationType == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
            }

        }


        for (Integer k = 0; k < budgetAllocationSaveRequestList.getAuthRequests().size(); k++) {


            if (budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDocId() == null || budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDocId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT ID CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthority() == null || budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthority().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY CAN NOT BE BLANK");
            }

            if (budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDate() == null || budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDate().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY DATE CAN NOT BE BLANK");
            }

            ConverterUtils.checkDateIsvalidOrNor(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDate());

            CgUnit chekUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthUnitId());
            if (chekUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
            }

            FileUpload fileUpload = fileUploadRepository.findByUploadID(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDocId());
            if (fileUpload == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DOCUMENT ID ");
            }
        }


        String authGrouPid = HelperUtils.getAuthorityGroupId();
        for (Integer k = 0; k < budgetAllocationSaveRequestList.getAuthRequests().size(); k++) {

            Authority authority = new Authority();
            authority.setAuthority(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthority());
            authority.setAuthorityId(HelperUtils.getAuthorityId());
            authority.setAuthDate(ConverterUtils.convertDateTotimeStamp(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDate()));
            authority.setDocId(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthDocId());
            authority.setAuthUnit(budgetAllocationSaveRequestList.getAuthRequests().get(k).getAuthUnitId());
            authority.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            authority.setAuthGroupId(authGrouPid);
            authority.setRemarks(budgetAllocationSaveRequestList.getAuthRequests().get(k).getRemark());
            authority.setUpdatedOn(HelperUtils.getCurrentTimeStamp());

            authorityRepository.save(authority);
        }

        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getAuthRequests().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
            budgetAllocationDetails.setAllocationId(HelperUtils.getAllocationId());
            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()));

            budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
            budgetAllocationDetails.setFinYear(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
            budgetAllocationDetails.setFromUnit(hrData.getUnitId());
            budgetAllocationDetails.setToUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
            budgetAllocationDetails.setSubHead(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId());
            budgetAllocationDetails.setStatus("Pending");
            budgetAllocationDetails.setRemarks(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemark());
            budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAuthGroupId(authGrouPid);
            budgetAllocationDetails.setRevisedAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount()));
            budgetAllocationDetails.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount() + budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount()));
            budgetAllocationDetails.setRefTransactionId(refTransID);
            budgetAllocationDetails.setUserId(hrData.getPid());
            budgetAllocationDetails.setIsDelete("0");
            budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());

            budgetAllocationDetailsRepository.save(budgetAllocationDetails);

        }


        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Budget Revision Unit Wise");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrData.getUnitId());
        mangeInboxOutbox.setGroupId(authGrouPid);
        mangeInboxOutbox.setFromUnit(hrData.getUnitId());
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Budget Revision save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }

    @Override
    public ApiResponse<List<CgUnitResponse>> getCgUnitDataWithPurposeCode() {
        List<CgUnitResponse> cgUnitResponseList = new ArrayList<CgUnitResponse>();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION.LOGIN AGAIN");
        }

        HrData hrData = hrDataRepository.findByPidAndIsActive(hrDataCheck.getPid(), "1");
        String cuurentRole = hrData.getRoleId().split(",")[0];
        CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());

        if (cuurentRole.contains(HelperUtils.UNITADMIN)) {

            CgUnit cgUnit1 = cgUnitRepository.findByUnit(hrDataCheck.getUnitId());
            CgUnitResponse cgUnitResponse = new CgUnitResponse();
            BeanUtils.copyProperties(cgUnit1, cgUnitResponse);
            CgStation cgStation = null;
            if (cgUnit1.getStationId() == null) {
            } else {
                cgStation = cgStationRepository.findByStationId(cgUnit1.getStationId());
            }
            cgUnitResponse.setCgStation(cgStation);
            cgUnitResponseList.add(cgUnitResponse);

            return ResponseUtils.createSuccessResponse(cgUnitResponseList, new TypeReference<List<CgUnitResponse>>() {
            });
        }


        if (cuurentRole.equalsIgnoreCase(HelperUtils.SYSTEMADMIN)) {
            List<CgUnit> unitDataList = cgUnitRepository.findByPurposeCodeOrPurposeCodeOrderByDescrAsc("0", "1");
            for (Integer n = 0; n < unitDataList.size(); n++) {


                if (unitDataList.get(n).getPurposeCode().equalsIgnoreCase("0") || unitDataList.get(n).getPurposeCode().equalsIgnoreCase("1")) {
                    CgUnitResponse cgUnitResponse = new CgUnitResponse();
                    BeanUtils.copyProperties(unitDataList.get(n), cgUnitResponse);
                    CgStation cgStation = null;
                    if (unitDataList.get(n).getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unitDataList.get(n).getStationId());
                    }
                    cgUnitResponse.setCgStation(cgStation);
                    cgUnitResponseList.add(cgUnitResponse);
                }

            }

        } else if (cuurentRole.equalsIgnoreCase(HelperUtils.UNITADMIN)) {
            List<CgUnit> unitDataList = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());

            for (Integer n = 0; n < unitDataList.size(); n++) {
                CgUnitResponse cgUnitResponse = new CgUnitResponse();
                if (unitDataList.get(n).getPurposeCode().equalsIgnoreCase("0")) {
                    BeanUtils.copyProperties(unitDataList.get(n), cgUnitResponse);
                    CgStation cgStation = null;
                    if (unitDataList.get(n).getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unitDataList.get(n).getStationId());
                    }
                    cgUnitResponse.setCgStation(cgStation);
                    cgUnitResponseList.add(cgUnitResponse);
                } else if (unitDataList.get(n).getPurposeCode().equalsIgnoreCase("1")) {
                    BeanUtils.copyProperties(unitDataList.get(n), cgUnitResponse);
                    CgStation cgStation = null;
                    if (unitDataList.get(n).getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unitDataList.get(n).getStationId());
                    }
                    cgUnitResponse.setCgStation(cgStation);
                    cgUnitResponseList.add(cgUnitResponse);
                }

            }
        }


//        String roleId = hrData.getRoleId();
//        if (roleId == null) {
//            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ROLE NOT ASSIGN THIS USER");
//        }
//
//        List<CgUnit> cgUnitData = cgUnitRepository.findAllByOrderByDescrAsc();
//
//
//        for (Integer n = 0; n < cgUnitData.size(); n++) {
//            CgUnitResponse cgUnitResponse = new CgUnitResponse();
//
//            if (!(cgUnitData.get(n).getUnit().equalsIgnoreCase("000000"))) {
//
//
//                if (hrData.getRoleId() == null || hrData.getRoleId().isEmpty() || hrData.getIsActive() == null || hrData.getIsActive().equalsIgnoreCase("0")) {
//
//                } else {
//                    String[] getRoleData = hrData.getRoleId().split(",");
//                    String userRole = getRoleData[0];
//
//                    for (Integer i = 0; i < getRoleData.length; i++) {
//
//                        if (userRole.equalsIgnoreCase(HelperUtils.SYSTEMADMIN)) {
//                            if (cgUnitData.get(n).getPurposeCode().equalsIgnoreCase("0") || cgUnitData.get(n).getPurposeCode().equalsIgnoreCase("1")) {
//                                BeanUtils.copyProperties(cgUnitData.get(n), cgUnitResponse);
//                                CgStation cgStation = null;
//                                if (cgUnitData.get(n).getStationId() == null) {
//                                } else {
//                                    cgStation = cgStationRepository.findByStationId(cgUnitData.get(n).getStationId());
//                                }
//                                cgUnitResponse.setCgStation(cgStation);
//                                cgUnitResponseList.add(cgUnitResponse);
//                            }
//                        } else if (userRole.equalsIgnoreCase(HelperUtils.UNITADMIN)) {
//
//                            CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());
//                            if (cgUnit == null) {
//                                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO UNIT MAPPING FOUND THIS USER");
//                            }
//
//                            if (cgUnit.getPurposeCode().equalsIgnoreCase("0")) {
//
////                                ALL BUDGET UNIT
//                                if (cgUnitData.get(n).getPurposeCode().equalsIgnoreCase("0")) {
//                                    BeanUtils.copyProperties(cgUnitData.get(n), cgUnitResponse);
//                                    CgStation cgStation = null;
//                                    if (cgUnitData.get(n).getStationId() == null) {
//                                    } else {
//                                        cgStation = cgStationRepository.findByStationId(cgUnitData.get(n).getStationId());
//                                    }
//                                    cgUnitResponse.setCgStation(cgStation);
//                                    cgUnitResponseList.add(cgUnitResponse);
//                                }
//
//                            } else if (cgUnit.getPurposeCode().equalsIgnoreCase("1")) {
////                                ALL CB USER
//                                if (cgUnitData.get(n).getPurposeCode().equalsIgnoreCase("1")) {
//                                    BeanUtils.copyProperties(cgUnitData.get(n), cgUnitResponse);
//                                    CgStation cgStation = null;
//                                    if (cgUnitData.get(n).getStationId() == null) {
//                                    } else {
//                                        cgStation = cgStationRepository.findByStationId(cgUnitData.get(n).getStationId());
//                                    }
//                                    cgUnitResponse.setCgStation(cgStation);
//                                    cgUnitResponseList.add(cgUnitResponse);
//                                }
//                            }
//
//                        }
//                    }
//                }
//
//
//            }
//
//        }

        return ResponseUtils.createSuccessResponse(cgUnitResponseList, new TypeReference<List<CgUnitResponse>>() {
        });
    }

    @Override
    public ApiResponse<DefaultResponse> saveAuthData(AuthRequest authRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION.LOGIN AGAIN");
        }
        DefaultResponse defaultResponse = new DefaultResponse();

        if (authRequest.getAuthDate() == null || authRequest.getAuthDate().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY DATE CAN NOT BE BLANK");
        }

        if (authRequest.getAuthDocId() == null || authRequest.getAuthDocId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOC ID CAN NOT BE BLANK");
        }


        if (authRequest.getAuthGroupId() == null || authRequest.getAuthGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTH GROUP CAN NOT BE BLANK");
        }


        if (authRequest.getAuthUnitId() == null || authRequest.getAuthUnitId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTH UNIT ID CAN NOT BE BLANK");
        }


        List<Authority> authoritiesList = authorityRepository.findByAuthGroupId(authRequest.getAuthGroupId());

        if (authoritiesList.size() > 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTH DATA ALREADY UPDATE.NOW YOU CAN NOT UPDATED.");
        }


        List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(authRequest.getAuthGroupId(), "0");
//        for (Integer i = 0; i < budgetAllocationDetailsList.size(); i++) {
//
//            List<HrData> hrDataList = hrDataRepository.findByUnitIdAndIsActive(budgetAllocationDetailsList.get(i).getToUnit(), "1");
//            if (hrDataList.size() == 0) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO ROLE ASSIGN FOR THIS UNIT.");
//            }
//
//            boolean dataApproverOrCreatrer = false;
//            for (Integer k = 0; k < hrDataList.size(); k++) {
//                HrData findHrData = hrDataList.get(k);
//                if (findHrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {
//                    dataApproverOrCreatrer = true;
//                }
//            }
//
//            if (dataApproverOrCreatrer == false) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO BUDGET MANGER FOUND THIS UNIT.PLEASE ADD BOTH ROLE FIRST");
//            }
//
//        }


        Authority authority = new Authority();
        authority.setAuthorityId(HelperUtils.getAuthorityId());
        authority.setAuthority(authRequest.getAuthority());
        authority.setAuthDate(ConverterUtils.convertDateTotimeStamp(authRequest.getAuthDate()));
        authority.setDocId(authRequest.getAuthDocId());
        authority.setAuthUnit(authRequest.getAuthUnitId());
        authority.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        authority.setAuthGroupId(authRequest.getAuthGroupId());
        authority.setRemarks(authRequest.getAuthUnitId());
        authority.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        authorityRepository.save(authority);


        List<BudgetAllocation> budgetAllocationsList = budgetAllocationRepository.findByAuthGroupIdAndIsFlag(authRequest.getAuthGroupId(), "0");

        for (Integer i = 0; i < budgetAllocationDetailsList.size(); i++) {

            BudgetAllocation budgetAllocationData = budgetAllocationsList.get(i);
            budgetAllocationData.setStatus("Approved");
            budgetAllocationRepository.save(budgetAllocationData);

        }

        String authgroupid = authRequest.getAuthGroupId();

        for (Integer i = 0; i < budgetAllocationDetailsList.size(); i++) {
            MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

//            List<HrData> hrDataList = hrDataRepository.findByUnitIdAndIsActive(budgetAllocationDetailsList.get(i).getToUnit(), "1");
//
//            String approverPId11 = "";
//            for (Integer k = 0; k < hrDataList.size(); k++) {
//                HrData findHrData = hrDataList.get(k);
//                if (findHrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {
//                    approverPId11 = findHrData.getPid();
//                }
//
//            }


            mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
            mangeInboxOutbox.setRemarks("Budget Receipt");
            mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setToUnit(budgetAllocationDetailsList.get(i).getToUnit());
            mangeInboxOutbox.setGroupId(authgroupid);
            mangeInboxOutbox.setFromUnit(hrDataCheck.getUnitId());
            mangeInboxOutbox.setRoleId(hrDataCheck.getRoleId());
            mangeInboxOutbox.setCreaterpId(hrDataCheck.getPid());
            mangeInboxOutbox.setApproverpId("");
            mangeInboxOutbox.setStatus("Approved");
            mangeInboxOutbox.setAllocationType(budgetAllocationDetailsList.get(i).getAllocTypeId());
            mangeInboxOutbox.setIsFlag("1");
            mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint(budgetAllocationDetailsList.get(i).getAllocationAmount() + ""));
            mangeInboxOutbox.setIsBgcg("BR");
            mangeInboxOutbox.setState("CR");
            authgroupid = budgetAllocationDetailsList.get(i).getAuthGroupId();

            mangeInboxOutBoxRepository.save(mangeInboxOutbox);
        }


        MangeInboxOutbox mangeInboxOutbox11 = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(authgroupid, hrDataCheck.getUnitId());
        if (mangeInboxOutbox11 != null) {
            mangeInboxOutbox11.setStatus("Fully Approved");
            mangeInboxOutBoxRepository.save(mangeInboxOutbox11);

        }

        defaultResponse.setMsg("DATA SAVE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    public boolean checkDuplicateData(List<BudgetAllocationSubRequest> budgetRequest) {
        Set<String> s = new HashSet<String>();

        for (BudgetAllocationSubRequest name : budgetRequest) {
            if (s.add(name.getToUnitId()) == false)
                return true;
        }
        return false;
    }

}


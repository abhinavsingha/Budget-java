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
import com.sdd.service.BudgetAllocationService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetAllocationServiceImpl implements BudgetAllocationService {
    @Autowired
    CgStationRepository cgStationRepository;

    @Autowired
    CdaParkingTransRepository cdaParkingTransRepository;

    @Autowired
    BudgetRevisionRepository budgetRevisionRepository;

    @Autowired
    CdaParkingCrAndDrRepository parkingCrAndDrRepository;

    @Autowired
    CdaParkingRepository cdaParkingRepository;


    @Autowired
    SubHeadTypeRepository subHeadTypeRepository;

    @Autowired
    CurrentStateRepository currentStateRepository;


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
    BudgetAllocationDetailsRepository budgetAllocationDetailsRepository;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    AuthorityRepository authorityRepository;


    @Override
    @Transactional(rollbackFor = {Exception.class})

    public ApiResponse<List<BudgetFinancialYear>> getBudgetFinYear() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }
        List<BudgetFinancialYear> budgetFinYearData = budgetFinancialYearRepository.findAllByOrderByFinYearAsc();

        return ResponseUtils.createSuccessResponse(budgetFinYearData, new TypeReference<List<BudgetFinancialYear>>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<AllocationType>> getAllocationAllData() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }
        List<AllocationType> allocationRepositoryData = allocationRepository.findAll();

        return ResponseUtils.createSuccessResponse(allocationRepositoryData, new TypeReference<List<AllocationType>>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<AllocationType>> getAllocationByFinYear(String finyear) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }
        if (finyear == null || finyear.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FIN YEAR ID CAN NOT BE BLANK");
        }
        List<AllocationType> allocationRepositoryData = allocationRepository.findByFinYear(finyear);
        return ResponseUtils.createSuccessResponse(allocationRepositoryData, new TypeReference<List<AllocationType>>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> updateAllocation(AllocationType allocationType) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }
        DefaultResponse defaultResponse = new DefaultResponse();

        if (allocationType.getAllocTypeId() == null || allocationType.getAllocTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID CAN NOT BE BLANK");
        }

        if (allocationType.getAllocDesc() == null || allocationType.getAllocDesc().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMARK CAN NOT BE BLANK");
        }

        AllocationType allocationTypeData = allocationRepository.findByAllocTypeId(allocationType.getAllocTypeId());
        if (allocationTypeData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
        }
        allocationTypeData.setAllocType(allocationType.getAllocDesc());
        allocationTypeData.setAllocDesc(allocationType.getAllocDesc());
        allocationTypeData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());

        allocationRepository.save(allocationTypeData);

        defaultResponse.setMsg("DATA UPDATE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<AllocationType>> getAllocationType() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }
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
    @Transactional(rollbackFor = {Exception.class})
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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<BudgetHeadResponse>> getSubHeadListWithAmount(BudgetHeadRequest budgetHeadRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }

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
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(hrDataCheck.getUnitId(), budgetHeadRequest.getFinYearId(), subHeadsData.get(i).getBudgetCodeId(), budgetHeadRequest.getAllocationType(), "Approved", "0", "0");
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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetResponseWithToken> getSubHeads() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN .LOGIN AGAIN");
        }

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
    @Transactional(rollbackFor = {Exception.class})
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
            unitDataList = cgUnitRepository.findAll();
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

        if (cgUnitResponseList.contains(cgUnit)) {

        } else {
            CgUnitResponse cgUnitResponse = new CgUnitResponse();
            BeanUtils.copyProperties(cgUnit, cgUnitResponse);
            cgUnitResponseList.add(cgUnitResponse);
        }

        return ResponseUtils.createSuccessResponse(cgUnitResponseList, new TypeReference<List<CgUnitResponse>>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<CgUnitResponse>> getCgUnitWithoutMOD() {

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


                if (!unitDataList.get(n).getDescr().equalsIgnoreCase("MOD")) {
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
    @Transactional(rollbackFor = {Exception.class})
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
    @Transactional(rollbackFor = {Exception.class})
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


        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndIsDeleteAndStatusOrStatusAndIsBudgetRevision(budgetAllocationReportRequest.getToUnitId(), budgetAllocationReportRequest.getBudgetFinancialYearId(), "0", "Pending", "Rejected", "0");
        String authgroupId = "";
        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

            BudgetAllocationSubResponse budgetAllocationReportData = new BudgetAllocationSubResponse();
            budgetAllocationReportData.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReportData.setAllocationId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReportData.setTransactionId(budgetAllocationSubReport.getTransactionId());
            budgetAllocationReportData.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
//            budgetAllocationReportData.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getBalanceAmount()));
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
    @Transactional(rollbackFor = {Exception.class})
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
    @Transactional(rollbackFor = {Exception.class})
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


        BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionIdAndIsDeleteAndIsBudgetRevision(budgetAllocationSaveRequestList.getTransactionId(), "0", "0");

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
        budgetAllocationDetails.setPrevInitial("0");
        budgetAllocationDetails.setPrevAllocAmount("0");
        budgetAllocationDetailsRepository.save(budgetAllocationDetails);


        response.setMsg("Data Update successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
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

        BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionIdAndIsDeleteAndIsBudgetRevision(budgetAllocationSaveRequestList.getTransactionId(), "0", "0");

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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationResponse> getBudgetAllocationData() {

        List<BudgetAllocationSubResponse> budgetAllocationList = new ArrayList<BudgetAllocationSubResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByToUnitAndIsDeleteAndIsBudgetRevision(hrData.getToDate(), "0", "0");
//        List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByToUnitAndFinYear(budgetAllocationReportRequest.getToUnitId(), budgetAllocationReportRequest.getBudgetFinancialYearId());
        String authgroupId = "";
        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

            BudgetAllocationSubResponse budgetAllocationReport = new BudgetAllocationSubResponse();
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setAllocationId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setTransactionId(budgetAllocationSubReport.getTransactionId());
            budgetAllocationReport.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
//            budgetAllocationReport.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getBalanceAmount()));
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
    @Transactional(rollbackFor = {Exception.class})
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

        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByAuthGroupId(groupId);
//        if (budgetAllocations.size() == 0) {
//            budgetAllocations = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(groupId, "1");
//        }


        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);

            if (budgetAllocationSubReport.getPrevInitial() == null) {
                if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                    continue;
                }
            } else {
                if (budgetAllocationSubReport.getIsBudgetRevision().equalsIgnoreCase("1") || budgetAllocationSubReport.getPrevInitial().equalsIgnoreCase("1")) {

                } else {
                    if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                        continue;
                    }
                }
            }

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
            budgetAllocationReport.setIsTYpe(budgetAllocationSubReport.getIsTYpe());
            budgetAllocationReport.setIsBudgetRevision(budgetAllocationSubReport.getIsBudgetRevision());
            budgetAllocationReport.setIsFlag(budgetAllocationSubReport.getIsDelete());
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());
            budgetAllocationReport.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());

            if (budgetAllocationSubReport.getRevisedAmount() == null) {
                budgetAllocationReport.setRevisedAmount("0");
            } else {
                budgetAllocationReport.setRevisedAmount(budgetAllocationSubReport.getRevisedAmount());
            }


            budgetAllocationReport.setReturnRemarks(budgetAllocationSubReport.getReturnRemarks());
            budgetAllocationReport.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());
            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));

            try {

                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit());
                if (cgUnit == null) {
                    budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
                } else {
                    budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
                }
            } catch (Exception e) {
                budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            }


            budgetAllocationReport.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocTypeId()));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));


            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(budgetAllocationSubReport.getTransactionId(), "0", 0);
            if (cdaCrDrTransData.size() > 0) {
                List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
                for (Integer m = 0; m < cdaCrDrTransData.size(); m++) {
                    CdaParkingCrAndDr cdaParkingCrAndDr = cdaCrDrTransData.get(m);

                    CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                    BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                    cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));

                    CdaParkingTrans cdaTransData = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                    if (cdaTransData != null) {
                        cgUnitResponse.setRemainingAmount(ConverterUtils.addDecimalPoint(cdaTransData.getRemainingCdaAmount()));
                        cgUnitResponse.setAmountTypeMain(amountUnitRepository.findByAmountTypeId(cdaTransData.getAmountType()));
                    }
                    data.add(cgUnitResponse);
                }
                budgetAllocationReport.setCdaData(data);
            } else {
                List<CdaParkingCrAndDr> cdaCrDrTransData11 = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(budgetAllocationReport.getFinYear().getSerialNo(), budgetAllocationReport.getSubHead().getBudgetCodeId(), "0", budgetAllocationReport.getAllocTypeId().getAllocTypeId(), budgetAllocationReport.getToUnit().getUnit(), 0);
                List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
                for (Integer m = 0; m < cdaCrDrTransData11.size(); m++) {
                    CdaParkingCrAndDr cdaParkingCrAndDr = cdaCrDrTransData11.get(m);

                    CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                    BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                    cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));

                    CdaParkingTrans cdaTransData = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                    if (cdaTransData != null) {
                        cgUnitResponse.setRemainingAmount(ConverterUtils.addDecimalPoint(cdaTransData.getRemainingCdaAmount()));
                        cgUnitResponse.setAmountTypeMain(amountUnitRepository.findByAmountTypeId(cdaTransData.getAmountType()));
                    }
                    data.add(cgUnitResponse);
                }
                budgetAllocationReport.setCdaData(data);
            }


            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");

            if (cdaParkingList.size() > 0) {
                if (cdaParkingList.size() > 0) {
                    budgetAllocationReport.setIsCDAparking("1");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                } else {
                    budgetAllocationReport.setIsCDAparking("0");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                }

            } else {
                List<CdaParkingTrans> cdaParkingList11 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", budgetAllocationSubReport.getAllocTypeId(), budgetAllocationSubReport.getToUnit());

                if (cdaParkingList11.size() > 0) {
                    budgetAllocationReport.setIsCDAparking("1");
                    budgetAllocationReport.setCdaList(cdaParkingList11);
                } else {
                    budgetAllocationReport.setIsCDAparking("0");
                    budgetAllocationReport.setCdaList(cdaParkingList11);
                }


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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationResponse> getAllSubHeadList() {
        List<BudgetAllocationSubResponse> budgetAllocationList = new ArrayList<BudgetAllocationSubResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }


        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
        if (allocationType.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE CAN NOT FIND.");

        }

        BudgetFinancialYear budgetFinancialYear;
        CurrntStateType stateList1 = currentStateRepository.findByTypeAndIsFlag("FINYEAR", "1");
        if (stateList1 == null) {
            budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo("01");
        } else {
            budgetFinancialYear =
                    budgetFinancialYearRepository.findBySerialNo(stateList1.getStateId());
        }

        List<BudgetAllocation> budgetAllocations = new ArrayList<BudgetAllocation>();
        List<BudgetHead> budgetHead = subHeadRepository.findAll();

        for (Integer i = 0; i < budgetHead.size(); i++) {

            List<BudgetAllocation> budgetAllocations11 = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(budgetHead.get(i).getBudgetCodeId(), hrData.getUnitId(), budgetFinancialYear.getSerialNo(), allocationType.get(0).getAllocTypeId(), "0", "0", "Approved");

            budgetAllocations.addAll(budgetAllocations11);
        }

        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(i);
            if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                continue;
            }

            BudgetAllocationSubResponse budgetAllocationReport = new BudgetAllocationSubResponse();
            budgetAllocationReport.setAllocationId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
            budgetAllocationReport.setStatus(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setUserId(budgetAllocationSubReport.getUserId());
            budgetAllocationReport.setIsTYpe(budgetAllocationSubReport.getIsTYpe());
            budgetAllocationReport.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());

            if (budgetAllocationSubReport.getRevisedAmount() == null) {
                budgetAllocationReport.setRevisedAmount("0");
            } else {
                budgetAllocationReport.setRevisedAmount(budgetAllocationSubReport.getRevisedAmount());
            }

            budgetAllocationReport.setReturnRemarks(budgetAllocationSubReport.getReturnRemarks());
            budgetAllocationReport.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());
            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));

            try {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit());
                if (cgUnit == null) {
                    budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
                } else {
                    budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
                }
            } catch (Exception e) {
                budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            }


            budgetAllocationReport.setAllocTypeId(allocationType.get(0));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));


            List<CgUnit> subUnitList = cgUnitRepository.findBySubUnitOrderByDescrAsc(hrData.getUnitId());

            double totalAllocationAmount = 0;
            double expAmount = 0;
            for (Integer c = 0; c < subUnitList.size(); c++) {
                List<BudgetAllocation> budgetAllocationListASD = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(budgetAllocations.get(i).getSubHead(), subUnitList.get(c).getUnit(), budgetAllocations.get(i).getFinYear(), budgetAllocations.get(i).getAllocationTypeId(), "0", "0");
                List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdateAndIsFlag(subUnitList.get(c).getUnit(), budgetAllocations.get(i).getFinYear(), budgetAllocations.get(i).getSubHead(), budgetAllocations.get(i).getAllocationTypeId(), "0", "0");

                for (Integer m = 0; m < contigentBills.size(); m++) {
                    ContigentBill contigentBill = contigentBills.get(m);
                    expAmount = expAmount + Double.parseDouble(contigentBill.getCbAmount());
                }
                for (Integer m = 0; m < budgetAllocationListASD.size(); m++) {
                    BudgetAllocation budgetAllocation = budgetAllocationListASD.get(m);
                    totalAllocationAmount = totalAllocationAmount + Double.parseDouble(budgetAllocation.getAllocationAmount()) * amountUnitRepository.findByAmountTypeId(budgetAllocation.getAmountType()).getAmount();
                }
            }

            totalAllocationAmount = (expAmount + totalAllocationAmount) / amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()).getAmount();
            if (totalAllocationAmount == 0) {
                budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());
            } else {
                budgetAllocationReport.setUnallocatedAmount((Double.parseDouble(budgetAllocationSubReport.getUnallocatedAmount()) - totalAllocationAmount) + "");
            }


            List<CdaParkingTrans> cdaParkingList11 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", allocationType.get(0).getAllocTypeId(), budgetAllocationSubReport.getToUnit());
            if (cdaParkingList11.size() == 0) {

                cdaParkingList11 = cdaParkingList11;
            }

            List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
            for (Integer m = 0; m < cdaParkingList11.size(); m++) {
                CdaParkingTrans cdaParkingCrAndDr = cdaParkingList11.get(m);

                CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));
                cgUnitResponse.setRemainingAmount(ConverterUtils.addDecimalPoint(cdaParkingCrAndDr.getRemainingCdaAmount()));

                data.add(cgUnitResponse);
            }
            budgetAllocationReport.setCdaData(data);


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
    @Transactional(rollbackFor = {Exception.class})
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

        if (budgetAllocations.size() == 0) {
            budgetAllocations = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(groupId, "1");
        }


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
            budgetAllocationReport.setReturnRemarks(budgetAllocationSubReport.getReturnRemarks());
            budgetAllocationReport.setRefTransactionId(budgetAllocationSubReport.getRefTransactionId());
            budgetAllocationReport.setUserId(budgetAllocationSubReport.getUserId());
            budgetAllocationReport.setIsTYpe(budgetAllocationSubReport.getIsTYpe());
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getAllocationDate());
            budgetAllocationReport.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            budgetAllocationReport.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setRevisedAmount(budgetAllocationSubReport.getRevisedAmount());
            budgetAllocationReport.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());
            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
            budgetAllocationReport.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocTypeId()));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));
            budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());


            if (hrData.getUnitId().equalsIgnoreCase(budgetAllocations.get(i).getToUnit())) {
                List<CdaRevisionData> cdaRevisionData = budgetRevisionRepository.findByAuthGroupIdAndToUnitId(groupId, budgetAllocations.get(i).getToUnit());
                budgetAllocationReport.setAllocationAmount(cdaRevisionData.get(0).getRemainingAmount());

            } else {
                List<CdaRevisionData> cdaRevisionData = budgetRevisionRepository.findByAuthGroupIdAndToUnitId(groupId, budgetAllocations.get(i).getToUnit());
                budgetAllocationReport.setAllocationAmount(cdaRevisionData.get(0).getRemainingAmount());
            }

//
//            List<CgUnit> subUnitList = cgUnitRepository.findBySubUnitOrderByDescrAsc(hrData.getUnitId());
//
//            double totalAllocationAmount = 0;
//            for (Integer c = 0; c < subUnitList.size(); c++) {
//                List<BudgetAllocation> budgetAllocationListASD = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(budgetAllocationReport.getSubHead().getBudgetCodeId(), subUnitList.get(i).getUnit(), budgetAllocationReport.getFinYear().getSerialNo(), budgetAllocationReport.getAllocTypeId().getAllocTypeId(), "0", "0");
//                for (Integer m = 0; m < budgetAllocationListASD.size(); m++) {
//                    BudgetAllocation budgetAllocation = budgetAllocationListASD.get(m);
//                    totalAllocationAmount = totalAllocationAmount + Double.parseDouble(budgetAllocation.getAllocationAmount()) * budgetAllocationReport.getAmountUnit().getAmount();
//                }
//            }
//
//            if (totalAllocationAmount == 0) {
            budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());
//            } else {
//                budgetAllocationReport.setUnallocatedAmount((Double.parseDouble(budgetAllocationSubReport.getUnallocatedAmount()) - totalAllocationAmount) + "");
//
//            }

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
        List<BudgetAllocationSubResponse> oldRevision = new ArrayList<BudgetAllocationSubResponse>();

        budgetAllocationResponse.setOldBudgetRevision(oldRevision);


        return ResponseUtils.createSuccessResponse(budgetAllocationResponse, new TypeReference<BudgetAllocationResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationResponse> getApprovedBudgetData() {
        List<BudgetAllocationSubResponse> budgetAllocationList = new ArrayList<BudgetAllocationSubResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }


        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByFromUnitAndIsDeleteAndStatusAndIsBudgetRevision(hrData.getUnitId(), "0", "Approved", "0");

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
            budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());
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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetRevision(BudgetAllocationSaveUnitRequest budgetAllocationSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE RIVISION BUDUGET");
        }

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

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

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {

                BudgetAllocationSubRequest revisonData = budgetAllocationSaveRequestList.getBudgetRequest().get(i);

                List<CdaTransAllocationRequest> cdaData = revisonData.getCdaParkingId();

                for (Integer m = 0; m < cdaData.size(); m++) {

                    if (cdaData.get(m).getCdaParkingId() == null || cdaData.get(m).getCdaParkingId().isEmpty()) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA ID CAN NOT BE BLANK");
                    }

                    CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaData.get(m).getCdaParkingId(), "0");
                    if (cdaParkingTrans == null) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                    }

                    if (cdaData.get(m).getCdaAmount() == null || cdaData.get(m).getCdaAmount().isEmpty()) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                    }
                }

            }


            List<BudgetAllocationDetails> checkBudgetRevisionExist = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Pending", "0", "1");
            List<BudgetAllocationDetails> checkBudgetAllocationPending = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Pending", "0", "0");
            if (checkBudgetRevisionExist.size() > 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET REVISION FOR THIS UNIT CURRENTLY NOT APPROVED. PLEASE APPROVED PREVIOUS REVISION");
            }

            if (checkBudgetAllocationPending.size() > 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALLOCATION FOR THIS UNIT CURRENTLY NOT APPROVED. PLEASE APPROVED PREVIOUS ALLOCATION");
            }


        }


        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String type = "";
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();


//            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {
            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setRevisedAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount()));

//            } else {
//
//                double amount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount());
//                double amountRevisd =Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount());
//                budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint((amount+amountRevisd)+""));
//                budgetAllocationDetails.setRevisedAmount(ConverterUtils.addDecimalPoint(amountRevisd+""));
//
//
//            }


            budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
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
            budgetAllocationDetails.setIsTYpe("R");
            budgetAllocationDetails.setIsBudgetRevision("1");
            budgetAllocationDetails.setRefTransactionId(refTransID);
            budgetAllocationDetails.setUserId(hrData.getPid());
            budgetAllocationDetails.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
            budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
            type = budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId();
            BudgetAllocationDetails saveData = budgetAllocationDetailsRepository.save(budgetAllocationDetails);


            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
//                if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");

                CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
                cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                cdaParkingCrAndDr.setCdaParkingTrans(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId());
                cdaParkingCrAndDr.setFinYearId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
                cdaParkingCrAndDr.setBudgetHeadId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId());
                cdaParkingCrAndDr.setGinNo(cdaParkingTrans.getGinNo());
                cdaParkingCrAndDr.setUnitId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
                cdaParkingCrAndDr.setAuthGroupId(authGrouPid);
                cdaParkingCrAndDr.setAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()));
                cdaParkingCrAndDr.setIscrdr("DR");
                cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
                cdaParkingCrAndDr.setIsFlag("0");
                cdaParkingCrAndDr.setIsRevision(1);
                cdaParkingCrAndDr.setTransactionId(saveData.getTransactionId());
                cdaParkingCrAndDr.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());

                parkingCrAndDrRepository.save(cdaParkingCrAndDr);
//                }
            }
        }


        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {

                BudgetAllocationSubRequest revisonData = budgetAllocationSaveRequestList.getBudgetRequest().get(i);
                for (Integer m = 0; m < revisonData.getCdaParkingId().size(); m++) {

                    CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(revisonData.getCdaParkingId().get(m).getCdaParkingId(), "0");
                    AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                    double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                    AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(revisonData.getAmountTypeId());
                    double parkingAmount = Double.parseDouble(revisonData.getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                    double bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();

                    cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                    cdaParkingTransRepository.save(cdaParkingTrans);
                }

            } else {
                List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "0");

                for (Integer k = 0; k < cdaParkingTransList.size(); k++) {

                    CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(k);
                    cdaParkingTrans.setIsFlag("1");
                    cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    cdaParkingTransRepository.save(cdaParkingTrans);

                    List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(cdaParkingTrans.getFinYearId(), cdaParkingTrans.getBudgetHeadId(), cdaParkingTrans.getGinNo(), "0", cdaParkingTrans.getAllocTypeId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), 0);
                    for (Integer q = 0; q < cdaParkingCrAndDr.size(); q++) {
                        CdaParkingCrAndDr cddata = cdaParkingCrAndDr.get(q);
                        cddata.setIsFlag("1");
                        parkingCrAndDrRepository.save(cddata);
                    }


                }
            }
        }


        BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(type);

        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Budget Revision");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrData.getUnitId());
        mangeInboxOutbox.setFromUnit(hrData.getUnitId());
        mangeInboxOutbox.setGroupId(authGrouPid);
        mangeInboxOutbox.setApproverpId("");
        mangeInboxOutbox.setType(budgetHeadId.getSubHeadDescr());
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setState("AP");
        mangeInboxOutbox.setIsRebase("0");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setIsFlag("0");
        mangeInboxOutbox.setIsRevision(1);
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Budget Revision save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetRevision2(BudgetAllocationSaveUnitRequest budgetAllocationSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE RIVISION BUDUGET");
        }

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

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
            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemainingAmount() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemainingAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMAINING AMOUNT NOT BE BLANK");
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

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {
                BudgetAllocationSubRequest revisonData = budgetAllocationSaveRequestList.getBudgetRequest().get(i);
                List<CdaTransAllocationRequest> cdaData = revisonData.getCdaParkingId();

                for (Integer m = 0; m < cdaData.size(); m++) {

                    if (cdaData.get(m).getCdaParkingId() == null || cdaData.get(m).getCdaParkingId().isEmpty()) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA ID CAN NOT BE BLANK");
                    }

                    CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaData.get(m).getCdaParkingId(), "0");
                    if (cdaParkingTrans == null) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                    }

                    if (cdaData.get(m).getCdaAmount() == null || cdaData.get(m).getCdaAmount().isEmpty()) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                    }
                }
            }

            List<BudgetAllocationDetails> checkBudgetRevisionExist = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Pending", "0", "1");
            List<BudgetAllocationDetails> checkBudgetAllocationPending = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Pending", "0", "0");
            if (checkBudgetRevisionExist.size() > 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET REVISION FOR THIS UNIT CURRENTLY NOT APPROVED. PLEASE APPROVED PREVIOUS REVISION");
            }

            if (checkBudgetAllocationPending.size() > 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALLOCATION FOR THIS UNIT CURRENTLY NOT APPROVED. PLEASE APPROVED PREVIOUS ALLOCATION");
            }
        }


        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String type = "";
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setRevisedAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount()));
            budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
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
            budgetAllocationDetails.setIsTYpe("R");
            budgetAllocationDetails.setIsBudgetRevision("1");
            budgetAllocationDetails.setRefTransactionId(refTransID);
            budgetAllocationDetails.setUserId(hrData.getPid());
            budgetAllocationDetails.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
            budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
            type = budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId();
            budgetAllocationDetailsRepository.save(budgetAllocationDetails);


        }


        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
            BudgetAllocationSubRequest revisonData = budgetAllocationSaveRequestList.getBudgetRequest().get(i);


            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {

                for (Integer m = 0; m < revisonData.getCdaParkingId().size(); m++) {

                    CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(revisonData.getCdaParkingId().get(m).getCdaParkingId(), "0");

                    CdaRevisionData cdaRevisionData = new CdaRevisionData();
                    cdaRevisionData.setCdaRrId(HelperUtils.getRevisionId());
                    cdaRevisionData.setFinYearId(revisonData.getBudgetFinanciaYearId());
                    cdaRevisionData.setBudgetHeadId(revisonData.getSubHeadId());
                    cdaRevisionData.setToUnitId(hrData.getUnitId());
                    cdaRevisionData.setFromUnitId(hrData.getUnitId());
                    cdaRevisionData.setAmount(revisonData.getCdaParkingId().get(m).getCdaAmount());
                    cdaRevisionData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    cdaRevisionData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    cdaRevisionData.setAllocTypeId(revisonData.getAllocationTypeId());
                    cdaRevisionData.setAuthGroupId(authGrouPid);
                    cdaRevisionData.setIsFlag("0");
                    cdaRevisionData.setAllocationAmount(revisonData.getAmount());
                    cdaRevisionData.setAmountType(revisonData.getAmountTypeId());
                    cdaRevisionData.setCdaTransId(cdaParkingTrans.getCdaParkingId());
                    cdaRevisionData.setIsSelf("1");
                    cdaRevisionData.setRemainingAmount(revisonData.getRemainingAmount());
                    budgetRevisionRepository.save(cdaRevisionData);

                }

            } else {

                CdaRevisionData cdaRevisionData = new CdaRevisionData();
                cdaRevisionData.setCdaRrId(HelperUtils.getRevisionId());
                cdaRevisionData.setFinYearId(revisonData.getBudgetFinanciaYearId());
                cdaRevisionData.setBudgetHeadId(revisonData.getSubHeadId());
                cdaRevisionData.setToUnitId(revisonData.getToUnitId());
                cdaRevisionData.setFromUnitId(hrData.getUnitId());
                cdaRevisionData.setAmount(revisonData.getRevisedAmount());
                cdaRevisionData.setAllocationAmount(revisonData.getAmount());
                cdaRevisionData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                cdaRevisionData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaRevisionData.setAllocTypeId(revisonData.getAllocationTypeId());
                cdaRevisionData.setAuthGroupId(authGrouPid);
                cdaRevisionData.setIsFlag("0");
                cdaRevisionData.setAmountType(revisonData.getAmountTypeId());
                cdaRevisionData.setAllocationAmount(revisonData.getAmount());
                cdaRevisionData.setRemainingAmount(revisonData.getRemainingAmount());
                cdaRevisionData.setCdaTransId(null);
                cdaRevisionData.setIsSelf("0");

                budgetRevisionRepository.save(cdaRevisionData);
            }
        }


        BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(type);

        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Budget Revision");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrData.getUnitId());
        mangeInboxOutbox.setFromUnit(hrData.getUnitId());
        mangeInboxOutbox.setGroupId(authGrouPid);
        mangeInboxOutbox.setApproverpId("");
        mangeInboxOutbox.setType(budgetHeadId.getSubHeadDescr());
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setState("AP");
        mangeInboxOutbox.setIsRebase("0");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setIsFlag("0");
        mangeInboxOutbox.setIsRevision(1);
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Budget Revision save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetRevision3(BudgetAllocationSaveUnitRequest budgetAllocationSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE RIVISION BUDUGET");
        }

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

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
            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemainingAmount() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemainingAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REMAINING AMOUNT NOT BE BLANK");
            }

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getIsAutoAssignAllocation() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getIsAutoAssignAllocation().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "IS AUTO ASSIGN NOT BE BLANK");
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

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {
                BudgetAllocationSubRequest revisonData = budgetAllocationSaveRequestList.getBudgetRequest().get(i);
                List<CdaTransAllocationRequest> cdaData = revisonData.getCdaParkingId();

                for (Integer m = 0; m < cdaData.size(); m++) {

                    if (cdaData.get(m).getCdaParkingId() == null || cdaData.get(m).getCdaParkingId().isEmpty()) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA ID CAN NOT BE BLANK");
                    }

                    CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaData.get(m).getCdaParkingId(), "0");
                    if (cdaParkingTrans == null) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                    }

                    if (cdaData.get(m).getCdaAmount() == null || cdaData.get(m).getCdaAmount().isEmpty()) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                    }
                }
            }

//            List<BudgetAllocationDetails> checkBudgetRevisionExist = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Pending", "0", "1");
            List<BudgetAllocationDetails> checkBudgetAllocationPending = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Pending", "0", "0");
//            if (checkBudgetRevisionExist.size() > 0) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET REVISION FOR THIS UNIT CURRENTLY NOT APPROVED. PLEASE APPROVED PREVIOUS REVISION");
//            }

            if (checkBudgetAllocationPending.size() > 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALLOCATION FOR THIS UNIT CURRENTLY NOT APPROVED. PLEASE APPROVED PREVIOUS ALLOCATION");
            }
        }


        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String type = "";
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            if (!(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getIsAutoAssignAllocation().equalsIgnoreCase("1"))) {

                BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()));
                budgetAllocationDetails.setRevisedAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount()));
                budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
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
                budgetAllocationDetails.setIsTYpe("R");
                budgetAllocationDetails.setIsBudgetRevision("1");
                budgetAllocationDetails.setRefTransactionId(refTransID);
                budgetAllocationDetails.setUserId(hrData.getPid());
                budgetAllocationDetails.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
                budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                type = budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId();
                budgetAllocationDetailsRepository.save(budgetAllocationDetails);

            }
        }


        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
            BudgetAllocationSubRequest revisonData = budgetAllocationSaveRequestList.getBudgetRequest().get(i);

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {

                for (Integer m = 0; m < revisonData.getCdaParkingId().size(); m++) {

                    if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getIsAutoAssignAllocation().equalsIgnoreCase("0")) {

                        CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(revisonData.getCdaParkingId().get(m).getCdaParkingId(), "0");
                        CdaRevisionData cdaRevisionData = new CdaRevisionData();
                        cdaRevisionData.setCdaRrId(HelperUtils.getRevisionId());
                        cdaRevisionData.setFinYearId(revisonData.getBudgetFinanciaYearId());
                        cdaRevisionData.setBudgetHeadId(revisonData.getSubHeadId());
                        cdaRevisionData.setToUnitId(hrData.getUnitId());
                        cdaRevisionData.setFromUnitId(hrData.getUnitId());
                        cdaRevisionData.setAmount(revisonData.getCdaParkingId().get(m).getCdaAmount());
                        cdaRevisionData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaRevisionData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaRevisionData.setAllocTypeId(revisonData.getAllocationTypeId());
                        cdaRevisionData.setAuthGroupId(authGrouPid);
                        cdaRevisionData.setIsFlag("0");
                        cdaRevisionData.setReviserAmount(revisonData.getRevisedAmountMain());
                        cdaRevisionData.setAllocationAmount(revisonData.getAmount());
                        cdaRevisionData.setAmountType(revisonData.getAmountTypeId());
                        cdaRevisionData.setCdaTransId(cdaParkingTrans.getCdaParkingId());
                        cdaRevisionData.setIsSelf("1");
                        cdaRevisionData.setIsAutoAssignAllocation(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getIsAutoAssignAllocation());
                        cdaRevisionData.setRemainingAmount(revisonData.getRemainingAmount());
                        budgetRevisionRepository.save(cdaRevisionData);
                    } else {

                        CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(revisonData.getCdaParkingId().get(m).getCdaParkingId(), "0");
                        CdaRevisionData cdaRevisionData = new CdaRevisionData();
                        cdaRevisionData.setCdaRrId(HelperUtils.getRevisionId());
                        cdaRevisionData.setFinYearId(revisonData.getBudgetFinanciaYearId());
                        cdaRevisionData.setBudgetHeadId(revisonData.getSubHeadId());
                        cdaRevisionData.setToUnitId(hrData.getUnitId());
                        cdaRevisionData.setFromUnitId(hrData.getUnitId());
                        cdaRevisionData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaRevisionData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaRevisionData.setAllocTypeId(revisonData.getAllocationTypeId());
                        cdaRevisionData.setAuthGroupId(authGrouPid);
                        cdaRevisionData.setIsFlag("0");
                        cdaRevisionData.setAmount(revisonData.getRevisedAmount());
                        cdaRevisionData.setReviserAmount(revisonData.getRevisedAmountMain());
                        cdaRevisionData.setAllocationAmount(revisonData.getAmount());
                        cdaRevisionData.setAmountType(revisonData.getAmountTypeId());
                        cdaRevisionData.setCdaTransId(cdaParkingTrans.getCdaParkingId());
                        cdaRevisionData.setIsSelf("1");
                        cdaRevisionData.setIsComplete("0");
                        cdaRevisionData.setIsAutoAssignAllocation(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getIsAutoAssignAllocation());
                        cdaRevisionData.setRemainingAmount(revisonData.getRemainingAmount());
                        budgetRevisionRepository.save(cdaRevisionData);
                    }


                }

            } else {

                CdaRevisionData cdaRevisionData = new CdaRevisionData();
                cdaRevisionData.setCdaRrId(HelperUtils.getRevisionId());
                cdaRevisionData.setFinYearId(revisonData.getBudgetFinanciaYearId());
                cdaRevisionData.setBudgetHeadId(revisonData.getSubHeadId());
                cdaRevisionData.setToUnitId(revisonData.getToUnitId());
                cdaRevisionData.setFromUnitId(hrData.getUnitId());
                cdaRevisionData.setAmount(revisonData.getRevisedAmount());
                cdaRevisionData.setAllocationAmount(revisonData.getAmount());
                cdaRevisionData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                cdaRevisionData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaRevisionData.setAllocTypeId(revisonData.getAllocationTypeId());
                cdaRevisionData.setAuthGroupId(authGrouPid);
                cdaRevisionData.setIsFlag("0");
                cdaRevisionData.setReviserAmount(revisonData.getRevisedAmountMain());
                cdaRevisionData.setIsAutoAssignAllocation(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getIsAutoAssignAllocation());
                cdaRevisionData.setAmountType(revisonData.getAmountTypeId());
                cdaRevisionData.setRemainingAmount(revisonData.getRemainingAmount());
                cdaRevisionData.setCdaTransId(null);
                cdaRevisionData.setIsSelf("0");
                cdaRevisionData.setIsComplete("0");

                budgetRevisionRepository.save(cdaRevisionData);
            }
        }


        BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(type);

        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Budget Revision");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrData.getUnitId());
        mangeInboxOutbox.setFromUnit(hrData.getUnitId());
        mangeInboxOutbox.setGroupId(authGrouPid);
        mangeInboxOutbox.setApproverpId("");
        mangeInboxOutbox.setType(budgetHeadId.getSubHeadDescr());
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setState("AP");
        mangeInboxOutbox.setIsRebase("0");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setIsFlag("0");
        mangeInboxOutbox.setIsRevision(1);
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Budget Revision save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
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
    @Transactional(rollbackFor = {Exception.class})
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

        List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(groupId, hrData.getUnitId());


        if (budgetAllocations.size() == 0) {
            budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(groupId, hrData.getUnitId());
        }


//      ABhi Termrealy hai
//        List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(groupId, hrData.getUnitId());

        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(i);

            if (budgetAllocationSubReport.getPrevInitial() == null) {
                if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                    continue;
                }
            } else {
                if (budgetAllocationSubReport.getIsBudgetRevision().equalsIgnoreCase("1") || budgetAllocationSubReport.getPrevInitial().equalsIgnoreCase("1")) {

                } else {
                    if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                        continue;
                    }
                }
            }

            BudgetAllocationSubResponse budgetAllocationReport = new BudgetAllocationSubResponse();
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setAllocationId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setTransactionId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());
            budgetAllocationReport.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
            budgetAllocationReport.setStatus(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setRemarks(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setReturnRemarks(budgetAllocationSubReport.getReturnRemarks());
            budgetAllocationReport.setRefTransactionId(budgetAllocationSubReport.getRefTransId());
            budgetAllocationReport.setUserId(budgetAllocationSubReport.getUserId());
            budgetAllocationReport.setIsFlag(budgetAllocationSubReport.getIsFlag());
            budgetAllocationReport.setIsTYpe(budgetAllocationSubReport.getIsTYpe());
            budgetAllocationReport.setIsBudgetRevision(budgetAllocationSubReport.getIsBudgetRevision());
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());

            if (budgetAllocationSubReport.getRevisedAmount() == null) {
                budgetAllocationReport.setRevisedAmount("0");
            } else {
                budgetAllocationReport.setRevisedAmount(budgetAllocationSubReport.getRevisedAmount());
            }


            budgetAllocationReport.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());
            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));


            try {

                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit());
                if (cgUnit == null) {
                    budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
                } else {
                    budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
                }

//                budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
            } catch (Exception e) {
                budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            }

            budgetAllocationReport.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocationTypeId()));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));


            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(budgetAllocationSubReport.getAllocationId(), "0", 0);
            if (cdaCrDrTransData.size() > 0) {
                List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
                for (Integer m = 0; m < cdaCrDrTransData.size(); m++) {
                    CdaParkingCrAndDr cdaParkingCrAndDr = cdaCrDrTransData.get(m);

                    CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                    BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                    cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));

                    CdaParkingTrans cdaTransData = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                    if (cdaTransData != null) {
                        cgUnitResponse.setRemainingAmount(ConverterUtils.addDecimalPoint(cdaTransData.getRemainingCdaAmount()));
//                        cgUnitResponse.setAllocationAmount(ConverterUtils.addDecimalPoint(cdaTransData.getTotalParkingAmount()));
                        cgUnitResponse.setAmountTypeMain(amountUnitRepository.findByAmountTypeId(cdaTransData.getAmountType()));
                    }
                    data.add(cgUnitResponse);
                }
                budgetAllocationReport.setCdaData(data);
            } else {
                List<CdaParkingCrAndDr> cdaCrDrTransData11 = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(budgetAllocationReport.getFinYear().getSerialNo(), budgetAllocationReport.getSubHead().getBudgetCodeId(), "0", budgetAllocationReport.getAllocTypeId().getAllocTypeId(), budgetAllocationReport.getToUnit().getUnit(), 0);
                List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
                for (Integer m = 0; m < cdaCrDrTransData11.size(); m++) {
                    CdaParkingCrAndDr cdaParkingCrAndDr = cdaCrDrTransData11.get(m);

                    CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                    BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                    cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));

                    CdaParkingTrans cdaTransData = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                    if (cdaTransData != null) {
                        cgUnitResponse.setRemainingAmount(ConverterUtils.addDecimalPoint(cdaTransData.getRemainingCdaAmount()));
//                        cgUnitResponse.setAllocationAmount(ConverterUtils.addDecimalPoint(cdaTransData.getTotalParkingAmount()));
                        cgUnitResponse.setAmountTypeMain(amountUnitRepository.findByAmountTypeId(cdaTransData.getAmountType()));
                    }
                    data.add(cgUnitResponse);
                }
                budgetAllocationReport.setCdaData(data);
            }


            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");

            if (cdaParkingList.size() > 0) {
                if (cdaParkingList.size() > 0) {
                    budgetAllocationReport.setIsCDAparking("1");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                } else {
                    budgetAllocationReport.setIsCDAparking("0");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                }

            } else {
                List<CdaParkingTrans> cdaParkingList11 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", budgetAllocationSubReport.getAllocationTypeId(), budgetAllocationSubReport.getToUnit());

                if (cdaParkingList11.size() > 0) {
                    budgetAllocationReport.setIsCDAparking("1");
                    budgetAllocationReport.setCdaList(cdaParkingList11);
                } else {
                    budgetAllocationReport.setIsCDAparking("0");
                    budgetAllocationReport.setCdaList(cdaParkingList11);
                }

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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationResponse> getAllGroupIdAndUnitIdRevisionCase(String groupId) {
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

        List<BudgetAllocation> budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnitAndIsFlag(groupId, hrData.getUnitId(), "0");

        if (budgetAllocations.size() == 0) {
            budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnitAndIsFlag(groupId, hrData.getUnitId(), "1");
        }

        for (Integer i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocation budgetAllocationSubReport = budgetAllocations.get(i);
            if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                continue;
            }

            BudgetAllocationSubResponse budgetAllocationReport = new BudgetAllocationSubResponse();
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setAllocationId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setTransactionId(budgetAllocationSubReport.getAllocationId());
            budgetAllocationReport.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSubReport.getAllocationAmount()));
            budgetAllocationReport.setStatus(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setRemarks(budgetAllocationSubReport.getStatus());
            budgetAllocationReport.setReturnRemarks(budgetAllocationSubReport.getReturnRemarks());
            budgetAllocationReport.setRefTransactionId(budgetAllocationSubReport.getRefTransId());
            budgetAllocationReport.setUserId(budgetAllocationSubReport.getUserId());
            budgetAllocationReport.setIsTYpe(budgetAllocationSubReport.getIsTYpe());
            budgetAllocationReport.setAllocationDate(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setAuthGroupId(budgetAllocationSubReport.getAuthGroupId());
            budgetAllocationReport.setIsFlag(budgetAllocationSubReport.getIsFlag());
            budgetAllocationReport.setIsBudgetRevision(budgetAllocationSubReport.getIsBudgetRevision());
            budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());

            if (budgetAllocationSubReport.getRevisedAmount() == null) {
                budgetAllocationReport.setRevisedAmount("0");
            } else {
                budgetAllocationReport.setRevisedAmount(budgetAllocationSubReport.getRevisedAmount());
            }


            budgetAllocationReport.setCreatedOn(budgetAllocationSubReport.getCreatedOn());
            budgetAllocationReport.setUpdatedOn(budgetAllocationSubReport.getUpdatedOn());
            budgetAllocationReport.setFinYear(budgetFinancialYearRepository.findBySerialNo(budgetAllocationSubReport.getFinYear()));
            budgetAllocationReport.setAmountUnit(amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()));
            budgetAllocationReport.setToUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));


            List<CgUnit> subUnitList = cgUnitRepository.findBySubUnitOrderByDescrAsc(hrData.getUnitId());

            double totalAllocationAmount = 0;
            double expAmount = 0;
            for (Integer c = 0; c < subUnitList.size(); c++) {
                List<BudgetAllocation> budgetAllocationListASD = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(budgetAllocations.get(i).getSubHead(), subUnitList.get(c).getUnit(), budgetAllocations.get(i).getFinYear(), budgetAllocations.get(i).getAllocationTypeId(), "0", "0");
                List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdateAndIsFlag(subUnitList.get(c).getUnit(), budgetAllocations.get(i).getFinYear(), budgetAllocations.get(i).getSubHead(), budgetAllocations.get(i).getAllocationTypeId(), "0", "0");

                for (Integer m = 0; m < contigentBills.size(); m++) {
                    ContigentBill contigentBill = contigentBills.get(m);
                    expAmount = expAmount + Double.parseDouble(contigentBill.getCbAmount());
                }


                for (Integer m = 0; m < budgetAllocationListASD.size(); m++) {
                    BudgetAllocation budgetAllocation = budgetAllocationListASD.get(m);
                    totalAllocationAmount = totalAllocationAmount + Double.parseDouble(budgetAllocation.getAllocationAmount()) * amountUnitRepository.findByAmountTypeId(budgetAllocation.getAmountType()).getAmount();
                }
            }

            totalAllocationAmount = (expAmount + totalAllocationAmount) / amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()).getAmount();

            if (totalAllocationAmount == 0) {
                budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());
            } else {
                budgetAllocationReport.setUnallocatedAmount((Double.parseDouble(budgetAllocationSubReport.getUnallocatedAmount()) - totalAllocationAmount) + "");
            }

            try {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit());
                if (cgUnit == null) {
                    budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
                } else {
                    budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getFromUnit()));
                }

            } catch (Exception e) {
                budgetAllocationReport.setFromUnit(cgUnitRepository.findByUnit(budgetAllocationSubReport.getToUnit()));
            }


            budgetAllocationReport.setAllocTypeId(allocationRepository.findByAllocTypeId(budgetAllocationSubReport.getAllocationTypeId()));
            budgetAllocationReport.setSubHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationSubReport.getSubHead()));


            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(budgetAllocationSubReport.getAllocationId(), "0", 0);
            if (cdaCrDrTransData.size() > 0) {
                List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
                for (Integer m = 0; m < cdaCrDrTransData.size(); m++) {
                    CdaParkingCrAndDr cdaParkingCrAndDr = cdaCrDrTransData.get(m);

                    CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                    BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                    cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));

                    CdaParkingTrans cdaTransData = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                    if (cdaTransData != null) {
                        cgUnitResponse.setRemainingAmount(ConverterUtils.addDecimalPoint(cdaTransData.getRemainingCdaAmount()));
//                        cgUnitResponse.setAllocationAmount(ConverterUtils.addDecimalPoint(cdaTransData.getTotalParkingAmount()));
                        cgUnitResponse.setAmountTypeMain(amountUnitRepository.findByAmountTypeId(cdaTransData.getAmountType()));
                    }
                    data.add(cgUnitResponse);
                }
                budgetAllocationReport.setCdaData(data);
            } else {
                List<CdaParkingCrAndDr> cdaCrDrTransData11 = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(budgetAllocationReport.getFinYear().getSerialNo(), budgetAllocationReport.getSubHead().getBudgetCodeId(), "0", budgetAllocationReport.getAllocTypeId().getAllocTypeId(), budgetAllocationReport.getToUnit().getUnit(), 0);
                List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
                for (Integer m = 0; m < cdaCrDrTransData11.size(); m++) {
                    CdaParkingCrAndDr cdaParkingCrAndDr = cdaCrDrTransData11.get(m);

                    CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                    BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                    cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));

                    CdaParkingTrans cdaTransData = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
                    if (cdaTransData != null) {
                        cgUnitResponse.setRemainingAmount(ConverterUtils.addDecimalPoint(cdaTransData.getRemainingCdaAmount()));
//                        cgUnitResponse.setAllocationAmount(ConverterUtils.addDecimalPoint(cdaTransData.getTotalParkingAmount()));
                        cgUnitResponse.setAmountTypeMain(amountUnitRepository.findByAmountTypeId(cdaTransData.getAmountType()));
                    }
                    data.add(cgUnitResponse);
                }
                budgetAllocationReport.setCdaData(data);
            }


            List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationSubReport.getAllocationId(), "0");

            if (cdaParkingList.size() > 0) {
                if (cdaParkingList.size() > 0) {
                    budgetAllocationReport.setIsCDAparking("1");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                } else {
                    budgetAllocationReport.setIsCDAparking("0");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                }

            } else {
                List<CdaParkingTrans> cdaParkingList11 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", budgetAllocationSubReport.getAllocationTypeId(), budgetAllocationSubReport.getToUnit());

                if (cdaParkingList11.size() > 0) {
                    budgetAllocationReport.setIsCDAparking("1");
                    budgetAllocationReport.setCdaList(cdaParkingList11);
                } else {
                    budgetAllocationReport.setIsCDAparking("0");
                    budgetAllocationReport.setCdaList(cdaParkingList11);
                }

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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<BudgetReviResp>> getBudgetRevisionData(BudgetReviReq budgetRivRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        List<BudgetReviResp> budgetRevision = new ArrayList<BudgetReviResp>();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO GET RIVISION DATA");
        }
        if (budgetRivRequest.getBudgetFinancialYearId() == null || budgetRivRequest.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR ID CAN NOT BE BLANK");

        }
        if (budgetRivRequest.getSubHead() == null || budgetRivRequest.getSubHead().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");

        }
        if (budgetRivRequest.getAllocTypeId() == null || budgetRivRequest.getAllocTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE0 CAN NOT BE BLANK");

        }
        CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());
        if (cgUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER UNIT IS INVALID.PLEASE CHECK");
        }


        List<CgUnit> allUnit = new ArrayList<>();
        List<CgUnit> unit = new ArrayList<>();
        if (hrData.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
            allUnit = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());
        } else {
            allUnit.add(cgUnitRepository.findByUnit(hrData.getUnitId()));
            allUnit.addAll(cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getUnit()));
        }
        if (allUnit.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT NOT FOUND");
        }

        unit.addAll(removeDuplicates(allUnit));


        for (Integer i = 0; i < unit.size(); i++) {

            List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unit.get(i).getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), budgetRivRequest.getAllocTypeId(), "Approved", "0", "0");

            for (Integer m = 0; m < budgetAllocationsDetalis.size(); m++) {
                BudgetReviResp res = new BudgetReviResp();
                res.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetalis.get(m).getToUnit()));

                double totalAmount = Double.parseDouble(budgetAllocationsDetalis.get(m).getAllocationAmount());
                res.setAllocationAmount(totalAmount + "");
                List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(unit.get(i).getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");

                double expendure = 0;
                for (Integer k = 0; k < contigentBills.size(); k++) {
                    expendure = expendure + Double.parseDouble(contigentBills.get(k).getCbAmount());
                }

                res.setExpenditureAmount(expendure + "");
                res.setStatus(budgetAllocationsDetalis.get(m).getStatus());
                res.setRevisedAmount(budgetAllocationsDetalis.get(m).getRevisedAmount());
                res.setFlag(budgetAllocationsDetalis.get(m).getIsFlag());
                AmountUnit amountType = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetalis.get(m).getAmountType());
                res.setAmountType(amountType);

                List<CdaFilterData> data = new ArrayList<>();

                double totalRemening = 0;

                List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationsDetalis.get(m).getAllocationId(), "0");
                if (cdaParkingTrans.size() > 0) {
                    for (Integer b = 0; b < cdaParkingTrans.size(); b++) {
                        CdaParkingTrans cdaParkingCrAndDr = cdaParkingTrans.get(b);

                        CdaFilterData cgUnitResponse = new CdaFilterData();
                        BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                        cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                        AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());
                        cgUnitResponse.setAmountType(cdaAmountUnit);

                        totalRemening = totalRemening + (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount());
                        data.add(cgUnitResponse);
                    }
                    res.setRemainingAmount((totalRemening / amountType.getAmount()) + "");
                } else {
                    double totalAllocation = 0;
                    BudgetAllocation budgetAllocation = budgetAllocationsDetalis.get(m);
                    List<CgUnit> unitSubList = cgUnitRepository.findBySubUnitOrderByDescrAsc(unit.get(i).getUnit());
                    for (Integer w = 0; w < unitSubList.size(); w++) {

                        List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFromUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(unitSubList.get(w).getUnit(), unit.get(i).getUnit(), budgetAllocation.getFinYear(), budgetAllocation.getSubHead(), budgetAllocation.getAllocationTypeId(), "0", "0", "Approved");
                        for (Integer q = 0; q < reData1.size(); q++) {
                            AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(reData1.get(q).getAmountType());
                            totalAllocation = totalAllocation + (Double.parseDouble(reData1.get(q).getAllocationAmount()) * subAmountType.getAmount());
                        }
                    }
                    res.setRemainingAmount(ConverterUtils.addDecimalPoint(Double.parseDouble(budgetAllocationsDetalis.get(m).getAllocationAmount()) - ((totalAllocation + expendure) / amountType.getAmount()) + ""));
                }

                res.setCdaTransData(data);
                budgetRevision.add(res);
            }

        }


        if (budgetRevision.size() <= 1) {
            budgetRevision.clear();
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO RECORD FOUND.");
        }
        return ResponseUtils.createSuccessResponse(budgetRevision, new TypeReference<List<BudgetReviResp>>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<BudgetReviResp>> getBudgetRevisionData33(BudgetReviReq budgetRivRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        List<BudgetReviResp> budgetRevision = new ArrayList<BudgetReviResp>();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO GET RIVISION DATA");
        }
        if (budgetRivRequest.getBudgetFinancialYearId() == null || budgetRivRequest.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR ID CAN NOT BE BLANK");

        }
        if (budgetRivRequest.getSubHead() == null || budgetRivRequest.getSubHead().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");

        }
        if (budgetRivRequest.getAllocTypeId() == null || budgetRivRequest.getAllocTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE0 CAN NOT BE BLANK");

        }
        CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());
        if (cgUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER UNIT IS INVALID.PLEASE CHECK");
        }


        List<CgUnit> allUnit = new ArrayList<>();
        List<CgUnit> unit = new ArrayList<>();
        if (hrData.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
            allUnit = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());
        } else {
            allUnit.add(cgUnitRepository.findByUnit(hrData.getUnitId()));
            allUnit.addAll(cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getUnit()));
        }

        if (allUnit.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT NOT FOUND");
        }

        List<CgUnit> dataMain = removeDuplicates(allUnit);
        unit.clear();
        unit.addAll(dataMain);

        for (Integer i = 0; i < unit.size(); i++) {

            if (hrData.getUnitId().equalsIgnoreCase(unit.get(i).getUnit())) {

                List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unit.get(i).getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), budgetRivRequest.getAllocTypeId(), "Approved", "0", "0");

                for (Integer m = 0; m < budgetAllocationsDetalis.size(); m++) {
                    BudgetReviResp res = new BudgetReviResp();
                    res.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetalis.get(m).getToUnit()));

                    AmountUnit amountType = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetalis.get(m).getAmountType());
                    res.setAmountType(amountType);


                    double expendure = 0;
                    double avilabaleAmount = 0;

                    String currentUnitId = unit.get(i).getUnit();
                    List<CgUnit> unitList1 = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
                    List<CgUnit> unitList = unitList1.stream().filter(e -> !e.getUnit().equalsIgnoreCase(currentUnitId)).collect(Collectors.toList());
                    unitList.add(cgUnitRepository.findByUnit(unit.get(i).getUnit()));

                    List<CgUnit> validUmit = removeDuplicates(unitList);
                    unitList.clear();
                    unitList.addAll(validUmit);


                    for (Integer d = 0; d < unitList.size(); d++) {
                        double avilabaleTotalAmount = 0;

                        List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(unitList.get(d).getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                        for (Integer k = 0; k < contigentBills.size(); k++) {
                            expendure = expendure + Double.parseDouble(contigentBills.get(k).getCbAmount());
                        }

                        List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), "0", budgetAllocationsDetalis.get(m).getAllocationTypeId(), unitList.get(d).getUnit());
                        if (cdaParkingList.size() > 0) {

                            for (Integer f = 0; f < cdaParkingList.size(); f++) {
                                CdaParkingTrans cdaParkingCrAndDr = cdaParkingList.get(f);
                                AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());

                                avilabaleTotalAmount = avilabaleTotalAmount + (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount());
                            }
                            avilabaleAmount = avilabaleAmount + avilabaleTotalAmount / amountType.getAmount();
                        } else {
                            double totalAllocation = 0;
//                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFromUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(currentUnitId, unit.get(i).getUnit(), budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), budgetAllocationsDetalis.get(m).getAllocationTypeId(), "0", "0", "Approved");
                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unitList.get(d).getUnit(), budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), budgetAllocationsDetalis.get(m).getAllocationTypeId(), "Approved", "0", "0");

                            for (Integer q = 0; q < reData1.size(); q++) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(reData1.get(q).getAmountType());
                                totalAllocation = totalAllocation + (Double.parseDouble(reData1.get(q).getAllocationAmount()) * subAmountType.getAmount());
                            }
                            avilabaleAmount = avilabaleAmount + ((totalAllocation) / amountType.getAmount());
                        }
                    }

                    res.setRemainingAmount(ConverterUtils.addDecimalPoint((avilabaleAmount - expendure / amountType.getAmount()) + ""));
                    res.setExpenditureAmount(expendure + "");
                    res.setStatus(budgetAllocationsDetalis.get(m).getStatus());
                    res.setRevisedAmount(budgetAllocationsDetalis.get(m).getRevisedAmount());
                    res.setFlag(budgetAllocationsDetalis.get(m).getIsFlag());


                    res.setAllocationAmount(avilabaleAmount + expendure / amountType.getAmount() + "");


                    List<CdaFilterData> data = new ArrayList<>();
                    double totalRemening = 0;

                    List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationsDetalis.get(m).getAllocationId(), "0");
                    if (cdaParkingTrans.size() > 0) {
                        for (Integer b = 0; b < cdaParkingTrans.size(); b++) {
                            CdaParkingTrans cdaParkingCrAndDr = cdaParkingTrans.get(b);

                            CdaFilterData cgUnitResponse = new CdaFilterData();
                            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                            AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());
                            cgUnitResponse.setAmountType(cdaAmountUnit);

                            totalRemening = totalRemening + (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount());
                            data.add(cgUnitResponse);
                        }

                    } else {
                        double totalAllocation = 0;
                        BudgetAllocation budgetAllocation = budgetAllocationsDetalis.get(m);
                        List<CgUnit> unitSubList = cgUnitRepository.findBySubUnitOrderByDescrAsc(unit.get(i).getUnit());
                        for (Integer w = 0; w < unitSubList.size(); w++) {

                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unit.get(i).getUnit(), budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), budgetAllocationsDetalis.get(m).getAllocationTypeId(), "Approved", "0", "0");

                            for (Integer q = 0; q < reData1.size(); q++) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(reData1.get(q).getAmountType());
                                totalAllocation = totalAllocation + (Double.parseDouble(reData1.get(q).getAllocationAmount()) * subAmountType.getAmount());
                            }
                        }
                    }
                    res.setCdaTransData(data);
                    budgetRevision.add(res);
                }


            } else {

                List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unit.get(i).getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), budgetRivRequest.getAllocTypeId(), "Approved", "0", "0");

                for (Integer m = 0; m < budgetAllocationsDetalis.size(); m++) {
                    BudgetReviResp res = new BudgetReviResp();
                    res.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetalis.get(m).getToUnit()));

                    AmountUnit amountType = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetalis.get(m).getAmountType());
                    res.setAmountType(amountType);

                    String currentUnitId = unit.get(i).getUnit();
                    List<CgUnit> unitList1 = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
                    List<CgUnit> unitList = unitList1.stream().filter(e -> !e.getUnit().equalsIgnoreCase(currentUnitId)).collect(Collectors.toList());
                    unitList.add(cgUnitRepository.findByUnit(unit.get(i).getUnit()));

                    List<CgUnit> validUmit = removeDuplicates(unitList);
                    unitList.clear();
                    unitList.addAll(validUmit);

                    double expendure = 0;
                    double avilabaleAmount = 0;
                    for (Integer d = 0; d < unitList.size(); d++) {
                        double avilabaleTotalAmount = 0;
                        List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(unitList.get(d).getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                        for (Integer k = 0; k < contigentBills.size(); k++) {
                            expendure = expendure + Double.parseDouble(contigentBills.get(k).getCbAmount());
                        }

                        List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), "0", budgetAllocationsDetalis.get(m).getAllocationTypeId(), unitList.get(d).getUnit());
                        if (cdaParkingList.size() > 0) {

                            for (Integer f = 0; f < cdaParkingList.size(); f++) {
                                CdaParkingTrans cdaParkingCrAndDr = cdaParkingList.get(f);
                                AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());

                                avilabaleTotalAmount = avilabaleTotalAmount + (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount());

                            }
                            avilabaleAmount = avilabaleAmount + avilabaleTotalAmount / amountType.getAmount();
                        } else {
                            double totalAllocation = 0;
                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unitList.get(d).getUnit(), budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), budgetAllocationsDetalis.get(m).getAllocationTypeId(), "Approved", "0", "0");

                            for (Integer q = 0; q < reData1.size(); q++) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(reData1.get(q).getAmountType());
                                totalAllocation = totalAllocation + (Double.parseDouble(reData1.get(q).getAllocationAmount()) * subAmountType.getAmount());
                            }
//                        avilabaleAmount = avilabaleAmount + (Double.parseDouble(budgetAllocationsDetalis.get(m).getAllocationAmount()) - ((totalAllocation - expendure) / amountType.getAmount()));
                            avilabaleAmount = avilabaleAmount + ((totalAllocation) / amountType.getAmount());
                        }
                    }

                    res.setRemainingAmount(ConverterUtils.addDecimalPoint(avilabaleAmount - expendure / amountType.getAmount() + ""));
                    res.setExpenditureAmount(expendure + "");
                    res.setStatus(budgetAllocationsDetalis.get(m).getStatus());
                    res.setRevisedAmount(budgetAllocationsDetalis.get(m).getRevisedAmount());
                    res.setFlag(budgetAllocationsDetalis.get(m).getIsFlag());
                    res.setAllocationAmount(((avilabaleAmount + expendure / amountType.getAmount())) + "");

                    List<CdaFilterData> data = new ArrayList<>();

                    double totalRemening = 0;

                    List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationsDetalis.get(m).getAllocationId(), "0");
                    if (cdaParkingTrans.size() > 0) {
                        for (Integer b = 0; b < cdaParkingTrans.size(); b++) {
                            CdaParkingTrans cdaParkingCrAndDr = cdaParkingTrans.get(b);

                            CdaFilterData cgUnitResponse = new CdaFilterData();
                            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                            AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());
                            cgUnitResponse.setAmountType(cdaAmountUnit);

                            totalRemening = totalRemening + (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount());
                            data.add(cgUnitResponse);
                        }

                    } else {
                        double totalAllocation = 0;
                        BudgetAllocation budgetAllocation = budgetAllocationsDetalis.get(m);
                        List<CgUnit> unitSubList = cgUnitRepository.findBySubUnitOrderByDescrAsc(unit.get(i).getUnit());
                        for (Integer w = 0; w < unitSubList.size(); w++) {
                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unit.get(i).getUnit(), budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), budgetAllocationsDetalis.get(m).getAllocationTypeId(), "Approved", "0", "0");

                            for (Integer q = 0; q < reData1.size(); q++) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(reData1.get(q).getAmountType());
                                totalAllocation = totalAllocation + (Double.parseDouble(reData1.get(q).getAllocationAmount()) * subAmountType.getAmount());
                            }
                        }
                    }
                    res.setCdaTransData(data);
                    budgetRevision.add(res);
                }


            }


        }


        if (budgetRevision.size() <= 1) {
            budgetRevision.clear();
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO RECORD FOUND.");
        }
        return ResponseUtils.createSuccessResponse(budgetRevision, new TypeReference<List<BudgetReviResp>>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<BudgetReviResp>> getBudgetRevisionData3(BudgetReviReq budgetRivRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        List<BudgetReviResp> budgetRevision = new ArrayList<BudgetReviResp>();

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO GET RIVISION DATA");
        }
        if (budgetRivRequest.getBudgetFinancialYearId() == null || budgetRivRequest.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR ID CAN NOT BE BLANK");

        }
        if (budgetRivRequest.getSubHead() == null || budgetRivRequest.getSubHead().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");

        }
        if (budgetRivRequest.getAllocTypeId() == null || budgetRivRequest.getAllocTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE0 CAN NOT BE BLANK");

        }
        CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());
        if (cgUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER UNIT IS INVALID.PLEASE CHECK");
        }


        List<CgUnit> allUnit = new ArrayList<>();
        List<CgUnit> unit = new ArrayList<>();
        if (hrData.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
            allUnit = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());
        } else {
            allUnit.add(cgUnitRepository.findByUnit(hrData.getUnitId()));
            allUnit.addAll(cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getUnit()));
        }

        if (allUnit.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT NOT FOUND");
        }

        List<CgUnit> dataMain = removeDuplicates(allUnit);
        unit.clear();
        unit.addAll(dataMain);

        for (Integer i = 0; i < unit.size(); i++) {

            if (unit.get(i).getUnit().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
                List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unit.get(i).getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), budgetRivRequest.getAllocTypeId(), "Approved", "0", "0");

                for (Integer m = 0; m < budgetAllocationsDetalis.size(); m++) {
                    BudgetReviResp res = new BudgetReviResp();
                    res.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetalis.get(m).getToUnit()));

                    AmountUnit amountType = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetalis.get(m).getAmountType());
                    res.setAmountType(amountType);

                    double expendure = 0;
                    double selfExpendure = 0;
                    double avilabaleAmount = 0;
                    String currentUnitId = unit.get(i).getUnit();

                    List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(currentUnitId, budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                    for (Integer k = 0; k < contigentBills.size(); k++) {
                        expendure = expendure + Double.parseDouble(contigentBills.get(k).getCbAmount());
                    }


                    List<ContigentBill> selfContigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(currentUnitId, budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                    for (Integer k = 0; k < selfContigentBills.size(); k++) {
                        selfExpendure = selfExpendure + Double.parseDouble(contigentBills.get(k).getCbAmount());
                    }


                    double totalAllocationT = 0;
                    List<BudgetAllocation> reData2 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(currentUnitId, budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), budgetAllocationsDetalis.get(m).getAllocationTypeId(), "Approved", "0", "0");

                    for (Integer q = 0; q < reData2.size(); q++) {
                        AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(reData2.get(q).getAmountType());
                        totalAllocationT = totalAllocationT + (Double.parseDouble(reData2.get(q).getAllocationAmount()) * subAmountType.getAmount());
                    }
                    avilabaleAmount = avilabaleAmount + ((totalAllocationT) / amountType.getAmount());


                    res.setRemainingAmount(ConverterUtils.addDecimalPoint((avilabaleAmount - expendure / amountType.getAmount()) + ""));
                    res.setExpenditureAmount(expendure + "");
                    res.setSelfExpenditureAmount(selfExpendure + "");
                    res.setStatus(budgetAllocationsDetalis.get(m).getStatus());
                    res.setRevisedAmount(budgetAllocationsDetalis.get(m).getRevisedAmount());
                    res.setFlag(budgetAllocationsDetalis.get(m).getIsFlag());

                    res.setAllocationAmount(avilabaleAmount + "");

                    List<CdaFilterData> data = new ArrayList<>();
                    double totalRemening = 0;

                    List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationsDetalis.get(m).getAllocationId(), "0");
                    if (cdaParkingTrans.size() > 0) {
                        for (Integer b = 0; b < cdaParkingTrans.size(); b++) {
                            CdaParkingTrans cdaParkingCrAndDr = cdaParkingTrans.get(b);

                            CdaFilterData cgUnitResponse = new CdaFilterData();
                            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                            AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());
                            cgUnitResponse.setAmountType(cdaAmountUnit);

                            totalRemening = totalRemening + (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount());
                            data.add(cgUnitResponse);
                        }

                    } else {
                        double totalAllocation = 0;
                        BudgetAllocation budgetAllocation = budgetAllocationsDetalis.get(m);
                        List<CgUnit> unitSubList = cgUnitRepository.findBySubUnitOrderByDescrAsc(unit.get(i).getUnit());
                        for (Integer w = 0; w < unitSubList.size(); w++) {

                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unit.get(i).getUnit(), budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), budgetAllocationsDetalis.get(m).getAllocationTypeId(), "Approved", "0", "0");

                            for (Integer q = 0; q < reData1.size(); q++) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(reData1.get(q).getAmountType());
                                totalAllocation = totalAllocation + (Double.parseDouble(reData1.get(q).getAllocationAmount()) * subAmountType.getAmount());
                            }
                        }
                    }
                    res.setCdaTransData(data);
                    budgetRevision.add(res);
                }

            } else {

//                String currentUnitId = unit.get(i).getUnit();
                List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike("%" + unit.get(i).getUnit() + "%");
                unitList.add(cgUnitRepository.findByUnit(unit.get(i).getUnit()));
                List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unit.get(i).getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), budgetRivRequest.getAllocTypeId(), "Approved", "0", "0");

                for (Integer m = 0; m < budgetAllocationsDetalis.size(); m++) {
                    BudgetReviResp res = new BudgetReviResp();
                    res.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetalis.get(m).getToUnit()));

                    AmountUnit amountType = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetalis.get(m).getAmountType());
                    res.setAmountType(amountType);

                    double expendure = 0;
                    double selfExpendure = 0;
                    double avilabaleAmount = 0;
                    String currentUnitId = unit.get(i).getUnit();

                    for (Integer f = 0; f < unitList.size(); f++) {
                        List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(unitList.get(f).getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                        for (Integer k = 0; k < contigentBills.size(); k++) {
                            expendure = expendure + Double.parseDouble(contigentBills.get(k).getCbAmount());
                        }
                    }


                    List<ContigentBill> selfContigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(currentUnitId, budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                    for (Integer k = 0; k < selfContigentBills.size(); k++) {
                        selfExpendure = selfExpendure + Double.parseDouble(selfContigentBills.get(k).getCbAmount());
                    }


                    double totalAllocationT = 0;
                    List<BudgetAllocation> reData2 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(currentUnitId, budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), budgetAllocationsDetalis.get(m).getAllocationTypeId(), "Approved", "0", "0");

                    for (Integer q = 0; q < reData2.size(); q++) {
                        AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(reData2.get(q).getAmountType());
                        totalAllocationT = totalAllocationT + (Double.parseDouble(reData2.get(q).getAllocationAmount()) * subAmountType.getAmount());
                    }
                    avilabaleAmount = avilabaleAmount + ((totalAllocationT) / amountType.getAmount());


                    res.setRemainingAmount(ConverterUtils.addDecimalPoint((avilabaleAmount - expendure / amountType.getAmount()) + ""));
                    res.setExpenditureAmount(expendure + "");
                    res.setSelfExpenditureAmount(selfExpendure + "");
                    res.setStatus(budgetAllocationsDetalis.get(m).getStatus());
                    res.setRevisedAmount(budgetAllocationsDetalis.get(m).getRevisedAmount());
                    res.setFlag(budgetAllocationsDetalis.get(m).getIsFlag());

                    res.setAllocationAmount(avilabaleAmount + "");

                    List<CdaFilterData> data = new ArrayList<>();
                    double totalRemening = 0;

                    List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationsDetalis.get(m).getAllocationId(), "0");
                    if (cdaParkingTrans.size() > 0) {
                        for (Integer b = 0; b < cdaParkingTrans.size(); b++) {
                            CdaParkingTrans cdaParkingCrAndDr = cdaParkingTrans.get(b);

                            CdaFilterData cgUnitResponse = new CdaFilterData();
                            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                            AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());
                            cgUnitResponse.setAmountType(cdaAmountUnit);

                            totalRemening = totalRemening + (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount());
                            data.add(cgUnitResponse);
                        }

                    } else {
                        double totalAllocation = 0;
                        BudgetAllocation budgetAllocation = budgetAllocationsDetalis.get(m);
                        List<CgUnit> unitSubList = cgUnitRepository.findBySubUnitOrderByDescrAsc(unit.get(i).getUnit());
                        for (Integer w = 0; w < unitSubList.size(); w++) {

                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unit.get(i).getUnit(), budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), budgetAllocationsDetalis.get(m).getAllocationTypeId(), "Approved", "0", "0");

                            for (Integer q = 0; q < reData1.size(); q++) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(reData1.get(q).getAmountType());
                                totalAllocation = totalAllocation + (Double.parseDouble(reData1.get(q).getAllocationAmount()) * subAmountType.getAmount());
                            }
                        }
                    }
                    res.setCdaTransData(data);
                    budgetRevision.add(res);
                }
            }


        }


        if (budgetRevision.size() <= 1) {
            budgetRevision.clear();
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO RECORD FOUND.");
        }
        return ResponseUtils.createSuccessResponse(budgetRevision, new TypeReference<List<BudgetReviResp>>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
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


        BudgetAllocationDetails allocationDetails = budgetAllocationDetailsRepository.findByTransactionIdAndIsDeleteAndIsBudgetRevision(budgetDeleteRequest.getTransactionId(), "0", "0");
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
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> budgetApprove(BudgetApproveRequest budgetApproveRequest) {

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


        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDeleteAndIsBudgetRevision(budgetApproveRequest.getAuthGroupId(), "0", "0");
        if (allocationDetails.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
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


            BudgetAllocationDetails allocationData = allocationDetails.get(i);
            status = budgetApproveRequest.getStatus();
            allocationData.setReturnRemarks(budgetApproveRequest.getRemarks());
            allocationData.setStatus(budgetApproveRequest.getStatus());
            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocationData);


            BudgetAllocation budgetAllocation = new BudgetAllocation();
            budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
            budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocation.setIsFlag("0");
            budgetAllocation.setIsTYpe(allocationDetails.get(i).getIsTYpe());
            budgetAllocation.setIsBudgetRevision("0");
            budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocation.setRefTransId(allocationDetails.get(i).getRefTransactionId());
            budgetAllocation.setFinYear(allocationDetails.get(i).getFinYear());
            budgetAllocation.setToUnit(allocationDetails.get(i).getToUnit());
            budgetAllocation.setFromUnit(hrData.getUnitId());
            budgetAllocation.setSubHead(allocationDetails.get(i).getSubHead());
            budgetAllocation.setAllocationTypeId(allocationDetails.get(i).getAllocTypeId());
            budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(allocationDetails.get(i).getAllocationAmount()));
            budgetAllocation.setUnallocatedAmount("0");
            budgetAllocation.setRevisedAmount("0");
            budgetAllocation.setPrevAllocAmount("0");
            budgetAllocation.setPrevInitial("0");
            budgetAllocation.setUserId(allocationDetails.get(i).getUserId());
            budgetAllocation.setStatus("Pending");
            budgetAllocation.setAmountType(allocationDetails.get(i).getAmountType());
            budgetAllocation.setAuthGroupId(allocationDetails.get(i).getAuthGroupId());

            budgetAllocationRepository.save(budgetAllocation);

        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {
            for (Integer m = 0; m < mangeInboxOutboxList.size(); m++) {

                try {
                    MangeInboxOutbox mangeInboxOutbox = mangeInboxOutboxList.get(m);

                    String toUnit = mangeInboxOutbox.getToUnit();
                    String fromUnit = mangeInboxOutbox.getFromUnit();
                    mangeInboxOutbox.setFromUnit(toUnit);
                    mangeInboxOutbox.setToUnit(fromUnit);
                    mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setStatus(budgetApproveRequest.getStatus());
                    mangeInboxOutbox.setState("CR");
                    mangeInboxOutbox.setIsApproved("0");
                    mangeInboxOutbox.setIsArchive("0");
                    mangeInboxOutbox.setIsFlag("0");

                    mangeInboxOutBoxRepository.save(mangeInboxOutbox);
                } catch (Exception e) {

                }
            }
        }


        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();
        response.setMsg("Data " + budgetApproveRequest.getStatus() + " Successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> budgetReject(BudgetApproveRequest budgetApproveRequest) {

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

        if (budgetApproveRequest.getCdaParkingId() == null || budgetApproveRequest.getCdaParkingId().isEmpty() || budgetApproveRequest.getCdaParkingId().size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA LIST CAN NOT BE BLANK");
        }


        for (Integer i = 0; i < budgetApproveRequest.getCdaParkingId().size(); i++) {

            if (budgetApproveRequest.getCdaParkingId().get(i).getCdacrDrId() == null || budgetApproveRequest.getCdaParkingId().get(i).getCdacrDrId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA CRDR ID CAN NOT BE BLANK");
            }
            CdaParkingCrAndDr cdaParkingTrans = parkingCrAndDrRepository.findByCdaCrdrIdAndIsFlagAndIsRevision(budgetApproveRequest.getCdaParkingId().get(i).getCdacrDrId(), "0", 0);
            if (cdaParkingTrans == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID.");
            }
            if (budgetApproveRequest.getCdaParkingId().get(i).getAllocatedAmountType() == null || budgetApproveRequest.getCdaParkingId().get(i).getAllocatedAmountType().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT TYPE CAN NOT BE BLANK");
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetApproveRequest.getCdaParkingId().get(i).getAllocatedAmountType());
            if (amountUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID.CDA");
            }
            if (budgetApproveRequest.getCdaParkingId().get(i).getAllocatedAmount() == null || budgetApproveRequest.getCdaParkingId().get(i).getAllocatedAmount().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT CAN NOT BE BLANK");
            }
        }
//

        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDeleteAndIsBudgetRevision(budgetApproveRequest.getAuthGroupId(), "0", "0");
        if (allocationDetails.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
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


            BudgetAllocationDetails allocationData = allocationDetails.get(i);
            status = budgetApproveRequest.getStatus();
            allocationData.setReturnRemarks(budgetApproveRequest.getRemarks());
            allocationData.setStatus(budgetApproveRequest.getStatus());
            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocationData);

            BudgetFinancialYear finYear = budgetFinancialYearRepository.findBySerialNo(allocationData.getFinYear());

            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(allocationDetails.get(i).getTransactionId(), "0", 0);
            for (Integer t = 0; t < cdaCrDrTransData.size(); t++) {

                List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(finYear.getSerialNo(), allocationData.getSubHead(), cdaCrDrTransData.get(t).getGinNo(), "0", allocationData.getAllocTypeId(), hrData.getUnitId());

                for (Integer x = 0; x < cdaParkingTransList.size(); x++) {

                    CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(x);

                    AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                    double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                    AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(allocationData.getAmountType());
                    double parkingAmount = Double.parseDouble(cdaCrDrTransData.get(t).getAmount()) * amountUnit.getAmount();

                    double bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
                    cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                    cdaParkingTransRepository.save(cdaParkingTrans);

                }
            }

        }


//        if (!budgetApproveRequest.getStatus().equalsIgnoreCase("Approved")) {
//            for (Integer i = 0; i < budgetApproveRequest.getCdaParkingId().size(); i++) {
//
//                CdaParkingCrAndDr cdaParkingCrAndDr = parkingCrAndDrRepository.findByCdaCrdrIdAndIsFlag(budgetApproveRequest.getCdaParkingId().get(i).getCdacrDrId(), "0");
//
//
//
//                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaParkingCrAndDr.getCdaParkingTrans(), "0");
//                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
//
//                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();
//
//                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetApproveRequest.getCdaParkingId().get(i).getAllocatedAmountType());
//                double parkingAmount = Double.parseDouble(cdaParkingCrAndDr.getAmount()) * amountUnit.getAmount();
//
//                double bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
//                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
//                cdaParkingTransRepository.save(cdaParkingTrans);
//            }
//        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {
            for (Integer m = 0; m < mangeInboxOutboxList.size(); m++) {

                try {
                    MangeInboxOutbox mangeInboxOutbox = mangeInboxOutboxList.get(m);

                    String toUnit = mangeInboxOutbox.getToUnit();
                    String fromUnit = mangeInboxOutbox.getFromUnit();
                    mangeInboxOutbox.setFromUnit(toUnit);
                    mangeInboxOutbox.setToUnit(fromUnit);
                    mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setStatus(budgetApproveRequest.getStatus());
                    mangeInboxOutbox.setState("CR");
                    mangeInboxOutbox.setIsApproved("0");
                    mangeInboxOutbox.setIsArchive("0");
                    mangeInboxOutbox.setIsFlag("0");

                    mangeInboxOutBoxRepository.save(mangeInboxOutbox);
                } catch (Exception e) {

                }
            }
        }


        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();
        response.setMsg("Data " + budgetApproveRequest.getStatus() + " Successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> approveRivisonBudgetOrReject(BudgetApproveRequest budgetApproveRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }


        if (budgetApproveRequest.getStatus() == null || budgetApproveRequest.getStatus().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "STATUS CAN NOT BE BLANK");
        }


        if (budgetApproveRequest.getAuthGroupId() == null || budgetApproveRequest.getAuthGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }


        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(budgetApproveRequest.getAuthGroupId(), "0");


        if (allocationDetails.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
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

            BudgetAllocationDetails budgetAllocationDetails = allocationDetails.get(i);
            List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(budgetAllocationDetails.getToUnit(), budgetAllocationDetails.getFinYear(), budgetAllocationDetails.getSubHead(), budgetAllocationDetails.getAllocTypeId(), "Approved", "0", "0");
            if (data.size() == 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DATA.PLEASE CHECK YOUR ADMINISTRATOR.01");
            }
        }


        String status = "";

        for (Integer i = 0; i < allocationDetails.size(); i++) {


            if (!(budgetApproveRequest.getStatus().equalsIgnoreCase("Approved"))) {

                BudgetAllocationDetails allocationData = allocationDetails.get(i);
                status = budgetApproveRequest.getStatus();
                allocationData.setStatus(budgetApproveRequest.getStatus());
                allocationData.setReturnRemarks(budgetApproveRequest.getRemarks());
                allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocationDetailsRepository.save(allocationData);

                List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(allocationDetails.get(i).getTransactionId(), "0", 1);
                if (allocationDetails.get(i).getToUnit().equalsIgnoreCase(hrData.getUnitId())) {

                    BudgetFinancialYear finYear = budgetFinancialYearRepository.findBySerialNo(allocationData.getFinYear());


                    for (Integer t = 0; t < cdaCrDrTransData.size(); t++) {

                        List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(finYear.getSerialNo(), allocationData.getSubHead(), cdaCrDrTransData.get(t).getGinNo(), "0", allocationData.getAllocTypeId(), hrData.getUnitId());
                        for (Integer x = 0; x < cdaParkingTransList.size(); x++) {

                            CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(x);

                            AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                            double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(allocationData.getAmountType());
                            double parkingAmount = Double.parseDouble(cdaCrDrTransData.get(t).getAmount()) * amountUnit.getAmount();

                            double bakiPesa = 0;
                            if (parkingAmount < 0) {
                                bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
                            } else {
                                bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
                            }


                            cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                            cdaParkingTransRepository.save(cdaParkingTrans);

                        }
                    }
                }

//                else if (cdaCrDrTransData.size() > 0) {
//
//                    BudgetFinancialYear finYear = budgetFinancialYearRepository.findBySerialNo(allocationData.getFinYear());
//
//
//                    for (Integer t = 0; t < cdaCrDrTransData.size(); t++) {
//
//                        List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(finYear.getSerialNo(), allocationData.getSubHead(), cdaCrDrTransData.get(t).getGinNo(), "0", allocationData.getAllocTypeId(), hrData.getUnitId());
//                        for (Integer x = 0; x < cdaParkingTransList.size(); x++) {
//
//                            CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(x);
//
//                            AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
//
//                            double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();
//
//                            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(allocationData.getAmountType());
//                            double parkingAmount = Double.parseDouble(cdaCrDrTransData.get(t).getAmount()) * amountUnit.getAmount();
//
//                            double bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
//                            cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
////                        cdaParkingTransRepository.save(cdaParkingTrans);
//
//                        }
//                    }
//
//                }
//

                else {

                    List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(allocationDetails.get(i).getFinYear(), allocationDetails.get(i).getSubHead(), allocationDetails.get(i).getToUnit(), allocationDetails.get(i).getAllocTypeId(), "1");

                    for (Integer k = 0; k < cdaParkingTransList.size(); k++) {

                        CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(k);
                        cdaParkingTrans.setIsFlag("0");
                        cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTransRepository.save(cdaParkingTrans);

                        List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(cdaParkingTrans.getFinYearId(), cdaParkingTrans.getBudgetHeadId(), cdaParkingTrans.getGinNo(), "0", cdaParkingTrans.getAllocTypeId(), allocationData.getToUnit(), 0);
                        for (Integer q = 0; q < cdaParkingCrAndDr.size(); q++) {
                            CdaParkingCrAndDr cddata = cdaParkingCrAndDr.get(q);
                            cddata.setIsFlag("1");
                            parkingCrAndDrRepository.save(cddata);
                        }

                    }
                }


//                if (allocationDetails.get(i).getToUnit().equalsIgnoreCase(hrData.getUnitId())) {
//
//                    double totalAmount = 0;
//                    double revisedAmount = 0;
//                    List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(allocationDetails.get(i).getToUnit(), allocationDetails.get(i).getFinYear(), allocationDetails.get(i).getSubHead(), allocationDetails.get(i).getAllocTypeId(), "Approved", "0", "0");
//                    for (Integer m = 0; m < data.size(); m++) {
//                        data.get(m).setIsFlag("1");
//                        totalAmount = totalAmount + Double.parseDouble(data.get(m).getAllocationAmount());
//                        revisedAmount = revisedAmount + Double.parseDouble(data.get(m).getRevisedAmount());
//                        data.get(m).setIsBudgetRevision("1");
//                        budgetAllocationRepository.save(data.get(m));
//                    }
//
////                    revisedAmount = revisedAmount + Double.parseDouble(allocationData.getRevisedAmount());
//
////                    BudgetAllocation budgetAllocation = new BudgetAllocation();
////                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
////                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
////                    budgetAllocation.setIsFlag("0");
////                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
////                    budgetAllocation.setRefTransId(allocationDetails.get(i).getRefTransactionId());
////                    budgetAllocation.setFinYear(allocationDetails.get(i).getFinYear());
////                    budgetAllocation.setToUnit(allocationDetails.get(i).getToUnit());
////                    budgetAllocation.setFromUnit(hrData.getUnitId());
////                    budgetAllocation.setSubHead(allocationDetails.get(i).getSubHead());
////                    budgetAllocation.setAllocationTypeId(allocationDetails.get(i).getAllocTypeId());
////                    budgetAllocation.setIsBudgetRevision("0");
////                    budgetAllocation.setUnallocatedAmount("0.0000");
////                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(totalAmount + ""));
////                    budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(revisedAmount + ""));
////                    budgetAllocation.setUserId(allocationDetails.get(i).getUserId());
////                    budgetAllocation.setStatus("Pending");
////                    budgetAllocation.setAmountType(allocationDetails.get(i).getAmountType());
////                    budgetAllocation.setAuthGroupId(allocationDetails.get(i).getAuthGroupId());
//
////                    budgetAllocationRepository.save(budgetAllocation);
//
//                } else {
//                    double totalAmount = 0;
//                    double revisedAmount = 0;
//                    List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(allocationDetails.get(i).getToUnit(), allocationDetails.get(i).getFinYear(), allocationDetails.get(i).getSubHead(), allocationDetails.get(i).getAllocTypeId(), "Approved", "0", "0");
//                    for (Integer m = 0; m < data.size(); m++) {
//                        totalAmount = totalAmount + Double.parseDouble(data.get(m).getAllocationAmount());
//                        revisedAmount = revisedAmount + Double.parseDouble(data.get(m).getRevisedAmount());
//                    }
//
//                    revisedAmount = revisedAmount + Double.parseDouble(allocationData.getRevisedAmount());
//
//                    BudgetAllocation budgetAllocation = new BudgetAllocation();
//                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
//                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
//                    budgetAllocation.setIsFlag("1");
//                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
//                    budgetAllocation.setRefTransId(allocationDetails.get(i).getRefTransactionId());
//                    budgetAllocation.setFinYear(allocationDetails.get(i).getFinYear());
//                    budgetAllocation.setToUnit(allocationDetails.get(i).getToUnit());
//                    budgetAllocation.setFromUnit(hrData.getUnitId());
//                    budgetAllocation.setSubHead(allocationDetails.get(i).getSubHead());
//                    budgetAllocation.setAllocationTypeId(allocationDetails.get(i).getAllocTypeId());
//                    budgetAllocation.setIsBudgetRevision("1");
//                    budgetAllocation.setUnallocatedAmount("0.0000");
//                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(totalAmount + ""));
//                    budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(revisedAmount + ""));
//                    budgetAllocation.setUserId(allocationDetails.get(i).getUserId());
//                    budgetAllocation.setStatus("Pending");
//                    budgetAllocation.setAmountType(allocationDetails.get(i).getAmountType());
//                    budgetAllocation.setAuthGroupId(allocationDetails.get(i).getAuthGroupId());
//
//                    budgetAllocationRepository.save(budgetAllocation);
//                }

            }

        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {
            for (Integer m = 0; m < mangeInboxOutboxList.size(); m++) {

                try {
                    MangeInboxOutbox mangeInboxOutbox = mangeInboxOutboxList.get(m);

                    mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setStatus(budgetApproveRequest.getStatus());

                    mangeInboxOutbox.setState("CR");
                    mangeInboxOutbox.setIsArchive("0");
                    mangeInboxOutbox.setIsApproved("0");

                    mangeInboxOutBoxRepository.save(mangeInboxOutbox);
                } catch (Exception e) {

                }
            }
        }


        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();
        response.setMsg("Data " + budgetApproveRequest.getStatus() + " Successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> approveRivisonBudgetOrReject3(BudgetApproveRequest budgetApproveRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        if (budgetApproveRequest.getStatus() == null || budgetApproveRequest.getStatus().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "STATUS CAN NOT BE BLANK");
        }
        if (budgetApproveRequest.getAuthGroupId() == null || budgetApproveRequest.getAuthGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }


        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(budgetApproveRequest.getAuthGroupId(), "0");
        if (allocationDetails.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
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

            BudgetAllocationDetails budgetAllocationDetails = allocationDetails.get(i);
            List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(budgetAllocationDetails.getToUnit(), budgetAllocationDetails.getFinYear(), budgetAllocationDetails.getSubHead(), budgetAllocationDetails.getAllocTypeId(), "Approved", "0", "0");
            if (data.size() == 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DATA.PLEASE CHECK YOUR ADMINISTRATOR.01");
            }
        }


        String status = "";

        for (Integer i = 0; i < allocationDetails.size(); i++) {
            if (!(budgetApproveRequest.getStatus().equalsIgnoreCase("Approved"))) {

                status = budgetApproveRequest.getStatus();
                BudgetAllocationDetails allocationData = allocationDetails.get(i);
                allocationData.setStatus(budgetApproveRequest.getStatus());
                allocationData.setIsDelete("1");
                allocationData.setIsBudgetRevision("1");
                allocationData.setStatus(budgetApproveRequest.getStatus());
                allocationData.setReturnRemarks(budgetApproveRequest.getRemarks());
                allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocationDetailsRepository.save(allocationData);

            }
        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {
            for (Integer m = 0; m < mangeInboxOutboxList.size(); m++) {

                try {
                    MangeInboxOutbox mangeInboxOutbox = mangeInboxOutboxList.get(m);

                    mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setStatus(budgetApproveRequest.getStatus());
                    if (status.equalsIgnoreCase("Rejected")) {
                        mangeInboxOutbox.setState("CR");
                        mangeInboxOutbox.setIsArchive("1");
                        mangeInboxOutbox.setIsApproved("0");
                    } else {
                        mangeInboxOutbox.setState("CR");
                        mangeInboxOutbox.setIsArchive("0");
                        mangeInboxOutbox.setIsApproved("0");
                    }
                    mangeInboxOutBoxRepository.save(mangeInboxOutbox);
                } catch (Exception e) {

                }
            }
        }

        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();
        response.setMsg("Data " + budgetApproveRequest.getStatus() + " Successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> approveRivisonBudgetOrReject2(BudgetApproveRequest budgetApproveRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        if (budgetApproveRequest.getStatus() == null || budgetApproveRequest.getStatus().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "STATUS CAN NOT BE BLANK");
        }
        if (budgetApproveRequest.getAuthGroupId() == null || budgetApproveRequest.getAuthGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }


        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(budgetApproveRequest.getAuthGroupId(), "0");
        if (allocationDetails.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
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

            BudgetAllocationDetails budgetAllocationDetails = allocationDetails.get(i);
            List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(budgetAllocationDetails.getToUnit(), budgetAllocationDetails.getFinYear(), budgetAllocationDetails.getSubHead(), budgetAllocationDetails.getAllocTypeId(), "Approved", "0", "0");
            if (data.size() == 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DATA.PLEASE CHECK YOUR ADMINISTRATOR.01");
            }
        }


        String status = "";

        for (Integer i = 0; i < allocationDetails.size(); i++) {
            if (!(budgetApproveRequest.getStatus().equalsIgnoreCase("Approved"))) {

                status = budgetApproveRequest.getStatus();
                BudgetAllocationDetails allocationData = allocationDetails.get(i);
                allocationData.setStatus(budgetApproveRequest.getStatus());
                allocationData.setIsDelete("1");
                allocationData.setIsBudgetRevision("1");
                allocationData.setStatus(budgetApproveRequest.getStatus());
                allocationData.setReturnRemarks(budgetApproveRequest.getRemarks());
                allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocationDetailsRepository.save(allocationData);

            }
        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {
            for (Integer m = 0; m < mangeInboxOutboxList.size(); m++) {

                try {
                    MangeInboxOutbox mangeInboxOutbox = mangeInboxOutboxList.get(m);

                    mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setStatus(budgetApproveRequest.getStatus());
                    if (status.equalsIgnoreCase("Rejected")) {
                        mangeInboxOutbox.setState("CR");
                        mangeInboxOutbox.setIsArchive("1");
                        mangeInboxOutbox.setIsApproved("0");
                    } else {
                        mangeInboxOutbox.setState("CR");
                        mangeInboxOutbox.setIsArchive("0");
                        mangeInboxOutbox.setIsApproved("0");
                    }
                    mangeInboxOutBoxRepository.save(mangeInboxOutbox);
                } catch (Exception e) {

                }
            }
        }

        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();
        response.setMsg("Data " + budgetApproveRequest.getStatus() + " Successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<AvilableFundResponse> findAvailableAmount(GetAmountRequest budgetHeadId) {
        AvilableFundResponse response = new AvilableFundResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }


        if (budgetHeadId.getBudgetHeadId() == null || budgetHeadId.getBudgetHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }


        if (budgetHeadId.getUnitId() == null || budgetHeadId.getUnitId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT ID CAN NOT BE BLANK");
        }
        CgUnit cgUnitData = cgUnitRepository.findByUnit(budgetHeadId.getUnitId());
        if (cgUnitData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID UNIT ID.");
        }


        if (budgetHeadId.getBudgetFinancialYearId() == null || budgetHeadId.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FIN YEAR ID CAN NOT BE BLANK key:-budgetFinancialYearId");
        }

        BudgetHead subHeadData = subHeadRepository.findByBudgetCodeId(budgetHeadId.getBudgetHeadId());
        if (subHeadData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), " INVALID SUB HEAD ID .");
        }

        AllocationType allocationTypeData = null;
        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
        if (allocationType.size() > 0) {
            allocationTypeData = allocationType.get(0);
        }

        List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetHeadId.getBudgetFinancialYearId(), budgetHeadId.getBudgetHeadId(), budgetHeadId.getUnitId(), allocationTypeData.getAllocTypeId(), "0");

        AmountUnit amountUnit = null;
        if (cdaParkingTrans.size() == 0) {
            response.setFundAvailable("0");
        } else {
            double balanceAmount = 0;
            for (Integer i = 0; i < cdaParkingTrans.size(); i++) {
                amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(i).getAmountType());
                balanceAmount = balanceAmount + Double.parseDouble(cdaParkingTrans.get(i).getRemainingCdaAmount());
            }
            response.setFundAvailable(ConverterUtils.addDecimalPoint(balanceAmount + ""));
            response.setAmountUnit(amountUnit);
        }

        response.setUnitName(cgUnitData.getCgUnitShort());


        double totalAllocationT = 0;
        List<BudgetAllocation> reData2 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), budgetHeadId.getBudgetFinancialYearId(), budgetHeadId.getBudgetHeadId(), allocationTypeData.getAllocTypeId(), "Approved", "0", "0");

        for (Integer q = 0; q < reData2.size(); q++) {
            AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(reData2.get(q).getAmountType());
            totalAllocationT = totalAllocationT + (Double.parseDouble(reData2.get(q).getAllocationAmount()) * subAmountType.getAmount());
        }

        response.setCbAllocationAMount(totalAllocationT + "");


        List<ContigentBill> cbExpendure = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(budgetHeadId.getUnitId(), budgetHeadId.getBudgetHeadId(), "0", "0");
        if (cbExpendure.size() == 0) {
            response.setExpenditure("0.0000");
        } else {
            double expenditure = 0;
            for (Integer i = 0; i < cbExpendure.size(); i++) {
                expenditure = expenditure + Double.parseDouble(cbExpendure.get(i).getCbAmount());
            }
            response.setExpenditure(ConverterUtils.addDecimalPoint(expenditure + ""));
        }

        List<CdaFilterData> data = new ArrayList<>();
        for (Integer m = 0; m < cdaParkingTrans.size(); m++) {
            CdaParkingTrans cdaParkingCrAndDr = cdaParkingTrans.get(m);

            CdaFilterData cgUnitResponse = new CdaFilterData();
            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
            cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));
            data.add(cgUnitResponse);
        }
        response.setCdaParkingTrans(data);

        return ResponseUtils.createSuccessResponse(response, new TypeReference<AvilableFundResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<AvilableFundResponse> getAvailableFundCB(GetAmountRequest budgetHeadId) {
        AvilableFundResponse response = new AvilableFundResponse();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }


        if (budgetHeadId.getBudgetHeadId() == null || budgetHeadId.getBudgetHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }


        if (budgetHeadId.getUnitId() == null || budgetHeadId.getUnitId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT ID CAN NOT BE BLANK");
        }
        CgUnit cgUnitData = cgUnitRepository.findByUnit(budgetHeadId.getUnitId());
        if (cgUnitData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID UNIT ID.");
        }


        if (budgetHeadId.getBudgetFinancialYearId() == null || budgetHeadId.getBudgetFinancialYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FIN YEAR ID CAN NOT BE BLANK key:-budgetFinancialYearId");
        }

        BudgetHead subHeadData = subHeadRepository.findByBudgetCodeId(budgetHeadId.getBudgetHeadId());
        if (subHeadData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), " INVALID SUB HEAD ID .");
        }

        AllocationType allocationTypeData = null;
        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
        if (allocationType.size() > 0) {
            allocationTypeData = allocationType.get(0);
        }

        List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetHeadId.getBudgetFinancialYearId(), budgetHeadId.getBudgetHeadId(), budgetHeadId.getUnitId(), allocationTypeData.getAllocTypeId(), "0");


        AmountUnit amountUnit = null;
        if (cdaParkingTrans.size() == 0) {
            response.setFundAvailable("0");
        } else {
            double balanceAmount = 0;
//            double allocationAmount = 0;
            for (Integer i = 0; i < cdaParkingTrans.size(); i++) {

                amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(i).getAmountType());
                balanceAmount = balanceAmount + Double.parseDouble(cdaParkingTrans.get(i).getRemainingCdaAmount());
//                allocationAmount = allocationAmount + Double.parseDouble(cdaParkingTrans.get(i).getTotalParkingAmount());
            }
            response.setFundAvailable(ConverterUtils.addDecimalPoint(balanceAmount + ""));
//            response.setFundallocated(ConverterUtils.addDecimalPoint(allocationAmount + ""));
            response.setAmountUnit(amountUnit);
        }

//        List<ContigentBill> subHeadContigentBill = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdateAndIsFlag(budgetHeadId.getUnitId(), budgetHeadId.getBudgetFinancialYearId(), budgetHeadId.getBudgetHeadId(), allocationTypeData.getAllocTypeId(), "0", "0");
//
//        double totalBill = 0;
//        for (Integer k = 0; k < subHeadContigentBill.size(); k++) {
//            totalBill = totalBill + Double.parseDouble(subHeadContigentBill.get(k).getCbAmount());
//        }
//        totalBill = Double.parseDouble(response.getFundAvailable()) + totalBill;
//
//
//        response.setFundAvailable(ConverterUtils.addDecimalPoint(totalBill + ""));
        response.setUnitName(cgUnitData.getCgUnitShort());


        List<ContigentBill> cbExpendure = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(budgetHeadId.getUnitId(), budgetHeadId.getBudgetHeadId(), "0", "0");
        if (cbExpendure.size() == 0) {
            response.setExpenditure("0.0000");
        } else {
            double expenditure = 0;
            for (Integer i = 0; i < cbExpendure.size(); i++) {
                expenditure = expenditure + Double.parseDouble(cbExpendure.get(i).getCbAmount());
            }
            response.setExpenditure(ConverterUtils.addDecimalPoint(expenditure + ""));
        }

        List<CdaFilterData> data = new ArrayList<>();
        for (Integer m = 0; m < cdaParkingTrans.size(); m++) {
            CdaParkingTrans cdaParkingCrAndDr = cdaParkingTrans.get(m);

            CdaFilterData cgUnitResponse = new CdaFilterData();
            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
            cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));
            data.add(cgUnitResponse);
        }
        response.setCdaParkingTrans(data);

        return ResponseUtils.createSuccessResponse(response, new TypeReference<AvilableFundResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
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


        AllocationType allocationTypeData = null;
        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
        if (allocationType.size() > 0) {
            allocationTypeData = allocationType.get(0);
        }

        BudgetFinancialYear budgetFinancialYear = null;
        CurrntStateType stateList1 = currentStateRepository.findByTypeAndIsFlag("FINYEAR", "1");
        if (stateList1 == null) {
            budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo("01");

        } else {
            budgetFinancialYear =
                    budgetFinancialYearRepository.findBySerialNo(stateList1.getStateId());
        }


        List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetFinancialYear.getSerialNo(), HelperUtils.HEADUNITID, allocationTypeData.getAllocTypeId(), "0");


//        List<BudgetAllocationDetails> modBudgetAllocations = budgetAllocationDetailsRepository.findByToUnitAndIsDeleteAndIsBudgetRevision(HelperUtils.HEADUNITID, "0", "0");
        if (cdaParkingTrans.size() == 0) {
            response.setFundAvailable("0");
        } else {
            double allocationAmount = 0;
            response.setFundAvailable("0");
            for (Integer i = 0; i < cdaParkingTrans.size(); i++) {
                allocationAmount = allocationAmount + Double.parseDouble(cdaParkingTrans.get(i).getRemainingCdaAmount());
            }
            response.setFundAvailable(ConverterUtils.addDecimalPoint(allocationAmount + ""));
        }

        response.setUnitName(cgUnitData.getCgUnitShort());


        List<CdaParkingTrans> cdaParkingTransD = cdaParkingTransRepository.findByFinYearIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetFinancialYear.getSerialNo(), hrData.getUnitId(), allocationTypeData.getAllocTypeId(), "0");

//        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByToUnitAndIsDeleteAndIsBudgetRevision(hrData.getUnitId(), "0", "0");
        if (cdaParkingTransD.size() == 0) {

            response.setPreviousAllocation("0.0000");
            response.setUnallocatedAmount("0.0000");

        } else {
            double previousAmount = 0;
            response.setFundAvailable("0.0000");
            for (Integer i = 0; i < cdaParkingTransD.size(); i++) {
                previousAmount = previousAmount + Double.parseDouble(cdaParkingTransD.get(i).getRemainingCdaAmount());
            }
            response.setFundAvailable(ConverterUtils.addDecimalPoint(previousAmount + ""));
            response.setUnallocatedAmount("0.0000");
        }


        List<CdaFilterData> data = new ArrayList<>();
        for (Integer m = 0; m < cdaParkingTrans.size(); m++) {
            CdaParkingTrans cdaParkingCrAndDr = cdaParkingTrans.get(m);

            CdaFilterData cgUnitResponse = new CdaFilterData();
            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
            cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));
            data.add(cgUnitResponse);
        }
        response.setCdaParkingTrans(data);

        return ResponseUtils.createSuccessResponse(response, new TypeReference<AvilableFundResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
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


        AllocationType allocationTypeData = null;

        List<AllocationType> allocationType = allocationRepository.findByIsFlag("1");
        if (allocationType.size() > 0) {
            allocationTypeData = allocationType.get(0);
        }
        List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(getAvilableFundRequest.getFinYearId(), getAvilableFundRequest.getSubHeadId(), hrData.getUnitId(), allocationTypeData.getAllocTypeId(), "0");


        response.setUnitName(checkUnitData.getCgUnitShort());


//        List<BudgetAllocation> modBudgetAllocations = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), getAvilableFundRequest.getFinYearId(), getAvilableFundRequest.getSubHeadId(), getAvilableFundRequest.getAllocationTypeId(), "Approved", "0", "0");
        if (cdaParkingTrans.size() == 0) {
            response.setFundAvailable("0");
        } else {
            double allocationAmount = 0;

            for (Integer m = 0; m < cdaParkingTrans.size(); m++) {

                response.setFundAvailable("0.0000");
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(m).getAmountType());
                allocationAmount = allocationAmount + (Double.parseDouble(cdaParkingTrans.get(m).getRemainingCdaAmount()) * amountUnit.getAmount());
                response.setFundAvailable(ConverterUtils.addDecimalPoint(allocationAmount + ""));

            }
        }


        List<CdaFilterData> data = new ArrayList<>();
        for (Integer m = 0; m < cdaParkingTrans.size(); m++) {
            CdaParkingTrans cdaParkingCrAndDr = cdaParkingTrans.get(m);

            CdaFilterData cgUnitResponse = new CdaFilterData();
            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
            cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));
            data.add(cgUnitResponse);
        }


        response.setCdaParkingTrans(data);

        return ResponseUtils.createSuccessResponse(response, new TypeReference<AvilableFundResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetAllocationSubHeadWise(BudgetAllocationSaveRequest budgetAllocationSaveRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {
            if (!hrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");

            }
        }
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();


        if (budgetAllocationSaveRequest.getBudgetRequest().size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND. PLEASE TRY AGAIN");
        }
        List<String> dataIscgBg = new ArrayList<>();
        dataIscgBg.add("Pending");
        dataIscgBg.add("Approved");

        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatusIn(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId(), "0", "0", dataIscgBg);
            if (budgetAllocationDetailsList.size() > 0) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALREADY ALLOCATED " + cgUnit.getDescr() + " . CAN NOT ASSIGN AGAIN");
            }
        }

        boolean isAmountExits = true;
        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            double amount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount());
            if (amount > 0) {
                isAmountExits = false;
            }
        }
        if (isAmountExits) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ATLEAST ADD ONE NON ZERO ALLOCATION ENTRY");
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


            for (Integer m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                if (budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId() == null || budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA ID CAN NOT BE BLANK");
                }

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                if (cdaParkingTrans == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                }
                if (budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount() == null || budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                }

            }

            double remainingCdaParkingAmount = 0;
            double allocationAmount = 0;
            for (Integer m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");


                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
                remainingCdaParkingAmount = remainingCdaParkingAmount + remainingCdaParkingAmount + (Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount());
                allocationAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount()) * amountUnit.getAmount();

            }

            if (allocationAmount > remainingCdaParkingAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS GREATER THAN CDA REMAINING AMOUNT");
            }

        }


        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            BudgetAllocationSubRequest budgetAllocationSubRequest = budgetAllocationSaveRequest.getBudgetRequest().get(i);
            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndFinYear(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0", budgetAllocationSubRequest.getBudgetFinanciaYearId());

            double totalCbAmount = 0;
            for (Integer f = 0; f < contigentBills.size(); f++) {
                totalCbAmount = totalCbAmount + Double.parseDouble(contigentBills.get(f).getCbAmount());
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSubRequest.getAmountTypeId());
            double allocationAmount = Double.parseDouble(budgetAllocationSubRequest.getAmount()) * amountUnit.getAmount();

            if (totalCbAmount > allocationAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS SMALLER THAN CDA UNIT EXPENDITURE AMOUNT");
            }
        }


        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


            for (Integer m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                if (parkingAmount > remainingCdaParkingAmount) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION NOT COMPLETE.AMOUNT MISMATCH");
                }


//                double bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
//                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
//                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }


        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String type = "";
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
            budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
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
            budgetAllocationDetails.setIsTYpe("S");
            budgetAllocationDetails.setUnallocatedAmount("0");
            budgetAllocationDetails.setIsBudgetRevision("0");
            budgetAllocationDetails.setRevisedAmount("0.0000");
//            budgetAllocationDetails.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setRefTransactionId(refTransID);
            budgetAllocationDetails.setUserId(hrData.getPid());
            budgetAllocationDetails.setAmountType(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());
            budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
            type = budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId();
            BudgetAllocationDetails saveData = budgetAllocationDetailsRepository.save(budgetAllocationDetails);


            for (Integer m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");

                CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
                cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                cdaParkingCrAndDr.setCdaParkingTrans(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId());
                cdaParkingCrAndDr.setFinYearId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId());
                cdaParkingCrAndDr.setBudgetHeadId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId());
                cdaParkingCrAndDr.setGinNo(cdaParkingTrans.getGinNo());
                cdaParkingCrAndDr.setUnitId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId());
                cdaParkingCrAndDr.setAuthGroupId(authGrouPid);
                cdaParkingCrAndDr.setAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()));
                cdaParkingCrAndDr.setIscrdr("DR");
                cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setAllocTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId());
                cdaParkingCrAndDr.setIsFlag("0");
                cdaParkingCrAndDr.setIsRevision(0);
                cdaParkingCrAndDr.setTransactionId(saveData.getTransactionId());
                cdaParkingCrAndDr.setAmountType(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());

                parkingCrAndDrRepository.save(cdaParkingCrAndDr);

            }

        }


        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


            for (Integer m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                double bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
                cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }


        BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(type);

        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("Budget Allocation SubHead Wise");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrData.getUnitId());
        mangeInboxOutbox.setFromUnit(hrData.getUnitId());
        mangeInboxOutbox.setGroupId(authGrouPid);
        mangeInboxOutbox.setType(budgetHeadId.getSubHeadDescr());
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setState("AP");
        mangeInboxOutbox.setApproverpId("");
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsRebase("0");
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setIsRevision(0);
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Data save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetAllocationUnitWise(BudgetAllocationSaveUnitRequest budgetAllocationSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {
            if (!hrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");

            }
        }

        if (budgetAllocationSaveRequestList.getBudgetRequest().size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND. PLEASE TRY AGAIN");

        }
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();
        List<String> dataIscgBg = new ArrayList<>();
        dataIscgBg.add("Pending");
        dataIscgBg.add("Approved");

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatusIn(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "0", "0", dataIscgBg);
            if (budgetAllocationDetailsList.size() > 0) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALREADY ALLOCATED " + cgUnit.getDescr() + " . CAN NOT ASSIGN AGAIN");
            }
        }


        boolean isAmountExits = true;
        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            double amount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount());
            if (amount > 0) {
                isAmountExits = false;
            }
        }
        if (isAmountExits) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ATLEAST ADD ONE NON ZERO ALLOCATION ENTRY");
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


            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
            if (amountUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
            }


            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA ID CAN NOT BE BLANK");
                }


                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                if (cdaParkingTrans == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                }
                if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                }
            }

            double remainingCdaParkingAmount = 0;
            double allocationAmount = 0;
            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
                remainingCdaParkingAmount = remainingCdaParkingAmount + Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();
                allocationAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()) * amountUnit.getAmount();
            }

            if (allocationAmount > remainingCdaParkingAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS GREATER THAN CDA REMAINING AMOUNT");
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


            double totalAMount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount());

            if (totalAMount > 0) {
                List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), hrData.getUnitId(), "0");
                if (cdaParkingList.size() <= 0) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PLEASE ADD CDA IN BUDGET RECEIPT.THAN TRY AGAIN");
                }
            }

        }


        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationSubRequest budgetAllocationSubRequest = budgetAllocationSaveRequestList.getBudgetRequest().get(i);
//            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0");
            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndFinYear(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0", budgetAllocationSubRequest.getBudgetFinanciaYearId());

            double totalCbAmount = 0;
            for (Integer f = 0; f < contigentBills.size(); f++) {
                totalCbAmount = totalCbAmount + Double.parseDouble(contigentBills.get(f).getCbAmount());
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSubRequest.getAmountTypeId());
            double allocationAmount = Double.parseDouble(budgetAllocationSubRequest.getAmount()) * amountUnit.getAmount();

            if (totalCbAmount > allocationAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS SMALLER THAN CDA UNIT EXPENDITURE AMOUNT");
            }
        }


        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                if (parkingAmount > remainingCdaParkingAmount) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION NOT COMPLETE.AMOUNT MISMATCH");
                }


//                double bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
//                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
//                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }


        String type = "";
        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
            budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
            budgetAllocationDetails.setFinYear(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
            budgetAllocationDetails.setFromUnit(hrData.getUnitId());
            budgetAllocationDetails.setToUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
            budgetAllocationDetails.setSubHead(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId());
            budgetAllocationDetails.setStatus("Pending");
            budgetAllocationDetails.setUnallocatedAmount("0");
            budgetAllocationDetails.setRemarks(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRemark());
            budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setAuthGroupId(authGrouPid);
//            budgetAllocationDetails.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setRefTransactionId(refTransID);
            budgetAllocationDetails.setUserId(hrData.getPid());
            budgetAllocationDetails.setIsDelete("0");
            budgetAllocationDetails.setIsTYpe("U");
            budgetAllocationDetails.setIsBudgetRevision("0");
            budgetAllocationDetails.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
            budgetAllocationDetails.setRevisedAmount("0.0000");
            budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
            type = budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId();
            BudgetAllocationDetails saveData = budgetAllocationDetailsRepository.save(budgetAllocationDetails);


            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");

                CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
                cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                cdaParkingCrAndDr.setCdaParkingTrans(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId());
                cdaParkingCrAndDr.setFinYearId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
                cdaParkingCrAndDr.setBudgetHeadId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId());
                cdaParkingCrAndDr.setGinNo(cdaParkingTrans.getGinNo());
                cdaParkingCrAndDr.setUnitId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
                cdaParkingCrAndDr.setAuthGroupId(authGrouPid);
                cdaParkingCrAndDr.setAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()));
                cdaParkingCrAndDr.setIscrdr("DR");
                cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
                cdaParkingCrAndDr.setIsFlag("0");
                cdaParkingCrAndDr.setIsRevision(0);
                cdaParkingCrAndDr.setTransactionId(saveData.getTransactionId());
                cdaParkingCrAndDr.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());

                parkingCrAndDrRepository.save(cdaParkingCrAndDr);

            }

        }


        //remainingBalance
        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                double bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
                cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }


        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

        CgUnit cgToUnit = cgUnitRepository.findByUnit(type);
        if (cgToUnit != null) {
            mangeInboxOutbox.setType(cgToUnit.getDescr());
        }

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
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsRevision(0);
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setIsRebase("0");
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Data save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetAllocationSubHeadWiseEdit(BudgetAllocationSaveRequestEdit budgetAllocationSaveRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {
            if (!hrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");

            }
        }
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();

        if (budgetAllocationSaveRequest.getMsgId() == null || budgetAllocationSaveRequest.getMsgId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "MSG ID CAN NOT BE BLANK");
        }

        MangeInboxOutbox mangeInboxOutboxCheck = mangeInboxOutBoxRepository.findByMangeInboxId(budgetAllocationSaveRequest.getMsgId());
        if (mangeInboxOutboxCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID MSG ID");
        }


        if (budgetAllocationSaveRequest.getBudgetRequest().size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND. PLEASE TRY AGAIN");
        }
        List<String> dataIscgBg = new ArrayList<>();
        dataIscgBg.add("Pending");
        dataIscgBg.add("Approved");

        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatusIn(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId(), "0", "0", dataIscgBg);
            if (budgetAllocationDetailsList.size() > 0) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId());
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


            for (Integer m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                if (budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId() == null || budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA ID CAN NOT BE BLANK");
                }

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                if (cdaParkingTrans == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                }
                if (budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount() == null || budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                }

            }

            double remainingCdaParkingAmount = 0;
            double allocationAmount = 0;
            for (Integer m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");


                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
                remainingCdaParkingAmount = remainingCdaParkingAmount + remainingCdaParkingAmount + (Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount());
                allocationAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount()) * amountUnit.getAmount();

            }

            if (allocationAmount > remainingCdaParkingAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS GREATER THAN CDA REMAINING AMOUNT");
            }

        }


        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            BudgetAllocationSubRequestEdit budgetAllocationSubRequest = budgetAllocationSaveRequest.getBudgetRequest().get(i);
//            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0");
            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndFinYear(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0", budgetAllocationSubRequest.getBudgetFinanciaYearId());

            double totalCbAmount = 0;
            for (Integer f = 0; f < contigentBills.size(); f++) {
                totalCbAmount = totalCbAmount + Double.parseDouble(contigentBills.get(f).getCbAmount());
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSubRequest.getAmountTypeId());
            double allocationAmount = Double.parseDouble(budgetAllocationSubRequest.getAmount()) * amountUnit.getAmount();

            if (totalCbAmount > allocationAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS SMALLER THAN CDA UNIT EXPENDITURE AMOUNT");
            }
        }


        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


            for (Integer m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                if (parkingAmount > remainingCdaParkingAmount) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION NOT COMPLETE.AMOUNT MISMATCH");
                }


            }

        }


        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getTransactionId());
            if (budgetAllocationDetails == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TRANSACTION ID");
            }

        }


        String authGrouPid = "";
        String type = "";
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getTransactionId());
            authGrouPid = budgetAllocationDetails.getAuthGroupId();

            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setStatus("Pending");
            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setIsDelete("0");
            budgetAllocationDetails.setAmountType(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());

            budgetAllocationDetails.setUnallocatedAmount("0");
            budgetAllocationDetails.setIsTYpe("S");
            budgetAllocationDetails.setRevisedAmount("0.0000");
            type = budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId();
            BudgetAllocationDetails saveData = budgetAllocationDetailsRepository.save(budgetAllocationDetails);


            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(budgetAllocationSaveRequest.getBudgetRequest().get(i).getTransactionId(), "0", 0);


            for (Integer m = 0; m < cdaCrDrTransData.size(); m++) {

                CdaParkingCrAndDr cdaParkingCrAndDr = cdaCrDrTransData.get(m);
                cdaParkingCrAndDr.setIsFlag("2");
                parkingCrAndDrRepository.save(cdaParkingCrAndDr);

            }


            for (Integer m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");


                CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
                cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                cdaParkingCrAndDr.setCdaParkingTrans(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId());
                cdaParkingCrAndDr.setFinYearId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId());
                cdaParkingCrAndDr.setBudgetHeadId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId());
                cdaParkingCrAndDr.setGinNo(cdaParkingTrans.getGinNo());
                cdaParkingCrAndDr.setUnitId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId());
                cdaParkingCrAndDr.setAuthGroupId(authGrouPid);
                cdaParkingCrAndDr.setAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()));
                cdaParkingCrAndDr.setIscrdr("DR");
                cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setAllocTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId());
                cdaParkingCrAndDr.setIsFlag("0");
                cdaParkingCrAndDr.setIsRevision(0);
                cdaParkingCrAndDr.setTransactionId(saveData.getTransactionId());
                cdaParkingCrAndDr.setAmountType(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());

                parkingCrAndDrRepository.save(cdaParkingCrAndDr);

            }

        }


        for (Integer i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


            for (Integer m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                double bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
                cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }


        BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(type);

        MangeInboxOutbox mangeInboxOutbox = mangeInboxOutBoxRepository.findByMangeInboxId(budgetAllocationSaveRequest.getMsgId());

        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setState("AP");
        mangeInboxOutbox.setApproverpId("");
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setIsRevision(0);
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);

        response.setMsg("Data save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BudgetAllocationSaveResponse> saveBudgetAllocationUnitWiseEdit(BudgetAllocationSaveUnitRequestEdit budgetAllocationSaveRequestList) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        } else {
            if (!hrData.getRoleId().contains(HelperUtils.BUDGETMANGER)) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");

            }
        }

        if (budgetAllocationSaveRequestList.getBudgetRequest().size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND. PLEASE TRY AGAIN");

        }
        BudgetAllocationSaveResponse response = new BudgetAllocationSaveResponse();
        List<String> dataIscgBg = new ArrayList<>();
        dataIscgBg.add("Pending");
        dataIscgBg.add("Approved");

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatusIn(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "0", "0", dataIscgBg);
            if (budgetAllocationDetailsList.size() > 0) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALREADY ALLOCATED " + cgUnit.getDescr() + " . CAN NOT ASSIGN AGAIN");
            }
        }

        if (budgetAllocationSaveRequestList.getMsgId() == null || budgetAllocationSaveRequestList.getMsgId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "MSG ID CAN NOT BE BLANK");
        }
        MangeInboxOutbox mangeInboxOutboxCheck = mangeInboxOutBoxRepository.findByMangeInboxId(budgetAllocationSaveRequestList.getMsgId());
        if (mangeInboxOutboxCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID MSG ID");
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


            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
            if (amountUnit == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT TYPE ID");
            }


            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA ID CAN NOT BE BLANK");
                }


                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                if (cdaParkingTrans == null) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                }
                if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount() == null || budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount().isEmpty()) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                }
            }

            double remainingCdaParkingAmount = 0;
            double allocationAmount = 0;
            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
                remainingCdaParkingAmount = remainingCdaParkingAmount + Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();
                allocationAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()) * amountUnit.getAmount();
            }

            if (allocationAmount > remainingCdaParkingAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS GREATER THAN CDA REMAINING AMOUNT");
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


            double totalAMount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount());

            if (totalAMount > 0) {
                List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), hrData.getUnitId(), "0");
                if (cdaParkingList.size() <= 0) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PLEASE ADD CDA IN BUDGET RECEIPT.THAN TRY AGAIN");
                }
            }

        }

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getTransactionId());
            if (budgetAllocationDetails == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TRANSACTION ID");
            }
        }


        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationSubRequestEdit budgetAllocationSubRequest = budgetAllocationSaveRequestList.getBudgetRequest().get(i);
//            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0");
            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndFinYear(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0", budgetAllocationSubRequest.getBudgetFinanciaYearId());

            double totalCbAmount = 0;
            for (Integer f = 0; f < contigentBills.size(); f++) {
                totalCbAmount = totalCbAmount + Double.parseDouble(contigentBills.get(f).getCbAmount());
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSubRequest.getAmountTypeId());
            double allocationAmount = Double.parseDouble(budgetAllocationSubRequest.getAmount()) * amountUnit.getAmount();

            if (totalCbAmount > allocationAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS SMALLER THAN CDA UNIT EXPENDITURE AMOUNT");
            }
        }


        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                if (parkingAmount > remainingCdaParkingAmount) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION NOT COMPLETE.AMOUNT MISMATCH");
                }
            }
        }


        String type = "";
        String amountType = "";
        String authGrouPid = "";
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getTransactionId());
            authGrouPid = budgetAllocationDetails.getAuthGroupId();
            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setStatus("Pending");

            budgetAllocationDetails.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());

            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setIsDelete("0");
            budgetAllocationDetails.setUnallocatedAmount("0");
            budgetAllocationDetails.setIsTYpe("U");
            type = budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId();
            BudgetAllocationDetails saveData = budgetAllocationDetailsRepository.save(budgetAllocationDetails);


            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getTransactionId(), "0", 0);

            for (Integer m = 0; m < cdaCrDrTransData.size(); m++) {

                CdaParkingCrAndDr cdaParkingCrAndDr = cdaCrDrTransData.get(m);
                cdaParkingCrAndDr.setIsFlag("2");
                parkingCrAndDrRepository.save(cdaParkingCrAndDr);

            }


            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");

                CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
                cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                cdaParkingCrAndDr.setCdaParkingTrans(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId());
                cdaParkingCrAndDr.setFinYearId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId());
                cdaParkingCrAndDr.setBudgetHeadId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId());
                cdaParkingCrAndDr.setGinNo(cdaParkingTrans.getGinNo());
                cdaParkingCrAndDr.setUnitId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
                cdaParkingCrAndDr.setAuthGroupId(authGrouPid);
                cdaParkingCrAndDr.setAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()));
                cdaParkingCrAndDr.setIscrdr("DR");
                cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingCrAndDr.setAllocTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId());
                cdaParkingCrAndDr.setIsFlag("0");
                cdaParkingCrAndDr.setIsRevision(0);
                cdaParkingCrAndDr.setTransactionId(saveData.getTransactionId());
                cdaParkingCrAndDr.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());

                parkingCrAndDrRepository.save(cdaParkingCrAndDr);

            }

        }


        //remainingBalance
        for (Integer i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            for (Integer m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                double bakiPesa = (remainingCdaParkingAmount - parkingAmount) / cadAmountUnit.getAmount();
                cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                cdaParkingTransRepository.save(cdaParkingTrans);
            }

        }


        MangeInboxOutbox mangeInboxOutbox = mangeInboxOutBoxRepository.findByMangeInboxId(budgetAllocationSaveRequestList.getMsgId());

        mangeInboxOutbox.setRemarks("Budget Allocation Unit Wise");
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setState("AP");
        mangeInboxOutbox.setAmount("AP");
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsRevision(0);
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Data save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
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

        List<BudgetAllocationDetails> budgetAllocationsDetalis = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndIsDeleteAndIsBudgetRevision(findBudgetRequest.getToUnitId(), findBudgetRequest.getFinYearId(), "0", "0");

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
    @Transactional(rollbackFor = {Exception.class})
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

        List<BudgetAllocationDetails> budgetAllocationsDetalis = budgetAllocationDetailsRepository.findBySubHeadAndFinYearAndIsDeleteAndStatusAndIsBudgetRevision(budgetAllocationReportRequest.getSubHead(), budgetAllocationReportRequest.getBudgetFinancialYearId(), "0", "Approved", "0");


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
    @Transactional(rollbackFor = {Exception.class})
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
            budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
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
//            budgetAllocationDetails.setBalanceAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount() + budgetAllocationSaveRequestList.getBudgetRequest().get(i).getRevisedAmount()));
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
        mangeInboxOutbox.setIsRebase("0");
        mangeInboxOutbox.setFromUnit(hrData.getUnitId());
        mangeInboxOutbox.setRoleId(hrData.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrData.getPid());
        mangeInboxOutbox.setStatus("Pending");
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsRevision(0);
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setIsBgcg("BG");

        mangeInboxOutBoxRepository.save(mangeInboxOutbox);


        response.setMsg("Budget Revision save successfully");
        return ResponseUtils.createSuccessResponse(response, new TypeReference<BudgetAllocationSaveResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
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
    @Transactional(rollbackFor = {Exception.class})
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


        List<BudgetAllocation> budgetAllocationsList = budgetAllocationRepository.findByAuthGroupIdAndIsFlagAndIsBudgetRevision(authRequest.getAuthGroupId(), "0", "0");

        for (Integer i = 0; i < budgetAllocationsList.size(); i++) {

            BudgetAllocation budgetAllocationData = budgetAllocationsList.get(i);
            budgetAllocationData.setStatus("Approved");
            budgetAllocationData.setPrevInitial("0");
            budgetAllocationData.setPrevAllocAmount("0");
            budgetAllocationRepository.save(budgetAllocationData);

        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(authRequest.getAuthGroupId(), hrDataCheck.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {
            for (Integer m = 0; m < mangeInboxOutboxList.size(); m++) {
                MangeInboxOutbox mangeInboxOutbox11 = mangeInboxOutboxList.get(m);

                mangeInboxOutbox11.setStatus("Fully Approved");
                mangeInboxOutbox11.setIsApproved("1");
                mangeInboxOutBoxRepository.save(mangeInboxOutbox11);

            }
        }


        HashMap<String, BudgetAllocation> totalUnit = new HashMap<String, BudgetAllocation>();
        for (Integer i = 0; i < budgetAllocationsList.size(); i++) {
            if (Double.parseDouble(budgetAllocationsList.get(i).getAllocationAmount()) > 0 || Double.parseDouble(budgetAllocationsList.get(i).getAllocationAmount()) != 0) {
                totalUnit.put(budgetAllocationsList.get(i).getToUnit(), budgetAllocationsList.get(i));
            }
        }

        for (Map.Entry<String, BudgetAllocation> entry : totalUnit.entrySet()) {
            String key = entry.getKey();
            BudgetAllocation tabData = entry.getValue();

            MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

            mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
            mangeInboxOutbox.setRemarks("Budget Receipt");
            mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setToUnit(key);

            List<MangeInboxOutbox> inboxList = mangeInboxOutBoxRepository.findByGroupId(tabData.getAuthGroupId());

            if (inboxList.size() > 0) {
                mangeInboxOutbox.setType(inboxList.get(0).getType());
            }

            mangeInboxOutbox.setGroupId(tabData.getAuthGroupId());
            mangeInboxOutbox.setFromUnit(hrDataCheck.getUnitId());
            mangeInboxOutbox.setRoleId(hrDataCheck.getRoleId());
            mangeInboxOutbox.setCreaterpId(hrDataCheck.getPid());
            mangeInboxOutbox.setApproverpId("");
            mangeInboxOutbox.setIsRebase("0");
            mangeInboxOutbox.setStatus("Fully Approved");
            mangeInboxOutbox.setAllocationType(tabData.getAllocationTypeId());
            mangeInboxOutbox.setIsFlag("1");
            mangeInboxOutbox.setIsArchive("0");
            mangeInboxOutbox.setIsApproved("0");
            mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint(tabData.getAllocationAmount() + ""));
            mangeInboxOutbox.setIsBgcg("BR");
            mangeInboxOutbox.setState("CR");
            mangeInboxOutbox.setIsRevision(0);

            mangeInboxOutBoxRepository.save(mangeInboxOutbox);


            Authority authorityMain = new Authority();
            authorityMain.setAuthorityId(HelperUtils.getAuthorityId());
            authorityMain.setAuthority(authRequest.getAuthority());
            authorityMain.setAuthDate(ConverterUtils.convertDateTotimeStamp(authRequest.getAuthDate()));
            authorityMain.setDocId(authRequest.getAuthDocId());
            authorityMain.setAuthUnit(authRequest.getAuthUnitId());
            authorityMain.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            authorityMain.setAuthGroupId(tabData.getAuthGroupId());
            authorityMain.setRemarks(authRequest.getAuthUnitId());
            authorityMain.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            authorityRepository.save(authorityMain);


        }


        defaultResponse.setMsg("DATA SAVE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> saveAuthDataRevision(AuthRequest authRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
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

        List<BudgetAllocation> budgetAllocationsListData = new ArrayList<>();

//        List<BudgetAllocation> budgetAllocationsList = budgetAllocationRepository.findByAuthGroupIdAndIsFlagAndIsBudgetRevision(authRequest.getAuthGroupId(), "1", "1");
//        if (budgetAllocationsList.size() <= 0) {
//            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND.");
//        }

        if (authRequest.getAuthGroupId() == null || authRequest.getAuthGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }


        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(authRequest.getAuthGroupId(), "0");


        if (allocationDetails.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }

        for (Integer i = 0; i < allocationDetails.size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = allocationDetails.get(i);
            List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(budgetAllocationDetails.getToUnit(), budgetAllocationDetails.getFinYear(), budgetAllocationDetails.getSubHead(), budgetAllocationDetails.getAllocTypeId(), "Approved", "0", "0");
            if (data.size() == 0) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DATA.PLEASE CHECK YOUR ADMINISTRATOR.01");
            }
        }


        for (Integer i = 0; i < allocationDetails.size(); i++) {

            BudgetAllocationDetails allocationData = allocationDetails.get(i);

            allocationData.setStatus("Approved");
            allocationData.setPrevInitial("0");
            allocationData.setPrevAllocAmount("0");
            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocationData);


            if (allocationDetails.get(i).getToUnit().equalsIgnoreCase(hrData.getUnitId())) {

                double totalAmount = 0;
                double revisedAmount = 0;
                List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(allocationDetails.get(i).getToUnit(), allocationDetails.get(i).getFinYear(), allocationDetails.get(i).getSubHead(), allocationDetails.get(i).getAllocTypeId(), "Approved", "0", "0");
                for (Integer m = 0; m < data.size(); m++) {
                    data.get(m).setIsFlag("0");
//                    totalAmount = totalAmount + Double.parseDouble(data.get(m).getAllocationAmount());

                    AmountUnit amountType = amountUnitRepository.findByAmountTypeId(data.get(m).getAmountType());
                    AmountUnit convertedAmount = amountUnitRepository.findByAmountTypeId(allocationDetails.get(i).getAmountType());


                    revisedAmount = revisedAmount + (Double.parseDouble(allocationDetails.get(i).getRevisedAmount()) * amountType.getAmount());
                    data.get(m).setIsBudgetRevision("0");

                    revisedAmount = revisedAmount / convertedAmount.getAmount();
                    budgetAllocationRepository.save(data.get(m));
                }

            } else {
                double totalAmount = 0;
                double revisedAmount = 0;
                List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(allocationDetails.get(i).getToUnit(), allocationDetails.get(i).getFinYear(), allocationDetails.get(i).getSubHead(), allocationDetails.get(i).getAllocTypeId(), "Approved", "0", "0");
                for (Integer m = 0; m < data.size(); m++) {

                    AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(data.get(m).getAmountType());

                    totalAmount = totalAmount + (Double.parseDouble(data.get(m).getAllocationAmount()) * amountUnit.getAmount());
                }

                revisedAmount = Double.parseDouble(allocationData.getRevisedAmount());

                AmountUnit convertedAmount = amountUnitRepository.findByAmountTypeId(allocationData.getAmountType());

                totalAmount = totalAmount / convertedAmount.getAmount();


//                BudgetAllocationDetails budgetAllocationDetails = allocationDetails.get(i);
//                List<BudgetAllocation> budgetAllocationMainData = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(budgetAllocationDetails.getToUnit(), budgetAllocationDetails.getFinYear(), budgetAllocationDetails.getSubHead(), budgetAllocationDetails.getAllocTypeId(), "Approved", "0", "0");
                for (Integer y = 0; y < data.size(); y++) {

                    BudgetAllocation budgetAllocationRevision = data.get(y);
                    budgetAllocationRevision.setIsBudgetRevision("1");
                    budgetAllocationRevision.setIsFlag("1");
                    budgetAllocationRepository.save(budgetAllocationRevision);

                }

                BudgetAllocation budgetAllocation = new BudgetAllocation();
                budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                budgetAllocation.setIsFlag("0");
                budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocation.setRefTransId(allocationDetails.get(i).getRefTransactionId());
                budgetAllocation.setFinYear(allocationDetails.get(i).getFinYear());
                budgetAllocation.setToUnit(allocationDetails.get(i).getToUnit());
                budgetAllocation.setFromUnit(hrData.getUnitId());
                budgetAllocation.setSubHead(allocationDetails.get(i).getSubHead());
                budgetAllocation.setIsTYpe(allocationDetails.get(i).getIsTYpe());
                budgetAllocation.setAllocationTypeId(allocationDetails.get(i).getAllocTypeId());
                budgetAllocation.setIsBudgetRevision("0");
                budgetAllocation.setUnallocatedAmount("0");
                budgetAllocation.setUnallocatedAmount("0.0000");
                budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint((revisedAmount + totalAmount) + ""));
                budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(revisedAmount + ""));
                budgetAllocation.setUserId(allocationDetails.get(i).getUserId());
                budgetAllocation.setStatus("Approved");
                budgetAllocation.setAmountType(allocationDetails.get(i).getAmountType());
                budgetAllocation.setAuthGroupId(allocationDetails.get(i).getAuthGroupId());

                budgetAllocationRepository.save(budgetAllocation);
                budgetAllocationsListData.add(budgetAllocation);

            }


        }


//        for (Integer i = 0; i < budgetAllocationsList.size(); i++) {
//
//            BudgetAllocation budgetAllocationData = budgetAllocationsList.get(i);
//            budgetAllocationData.setStatus("Approved");
//            budgetAllocationRepository.save(budgetAllocationData);
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


        String authgroupid = authRequest.getAuthGroupId();
        List<MangeInboxOutbox> inboxList = mangeInboxOutBoxRepository.findByGroupId(authgroupid);


        HashMap<String, BudgetAllocation> totalUnit = new HashMap<String, BudgetAllocation>();
        for (Integer i = 0; i < budgetAllocationsListData.size(); i++) {
            totalUnit.put(budgetAllocationsListData.get(i).getToUnit(), budgetAllocationsListData.get(i));
        }

        for (Map.Entry<String, BudgetAllocation> entry : totalUnit.entrySet()) {
            String key = entry.getKey();
            BudgetAllocation tabData = entry.getValue();

            MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

            mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
            mangeInboxOutbox.setRemarks("Budget Receipt");
            mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setToUnit(key);

            if (inboxList.size() > 0) {
                mangeInboxOutbox.setType(inboxList.get(0).getType());
            }


            mangeInboxOutbox.setGroupId(authgroupid);
            mangeInboxOutbox.setIsRebase("0");
            mangeInboxOutbox.setFromUnit(hrData.getUnitId());
            mangeInboxOutbox.setRoleId(hrData.getRoleId());
            mangeInboxOutbox.setCreaterpId(hrData.getPid());
            mangeInboxOutbox.setApproverpId("");
            mangeInboxOutbox.setStatus("Approved");
            mangeInboxOutbox.setAllocationType(tabData.getAllocationTypeId());
            mangeInboxOutbox.setIsFlag("1");
            mangeInboxOutbox.setIsArchive("0");
            mangeInboxOutbox.setIsApproved("0");
            mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint(tabData.getAllocationAmount() + ""));
            mangeInboxOutbox.setIsBgcg("BR");
            mangeInboxOutbox.setState("CR");
            mangeInboxOutbox.setIsRevision(1);
            authgroupid = tabData.getAuthGroupId();

            mangeInboxOutBoxRepository.save(mangeInboxOutbox);

        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(authgroupid, hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {
            for (Integer m = 0; m < mangeInboxOutboxList.size(); m++) {
                try {
                    MangeInboxOutbox mangeInboxOutbox11 = mangeInboxOutboxList.get(m);

                    mangeInboxOutbox11.setStatus("Fully Approved");
                    mangeInboxOutbox11.setIsApproved("1");
                    mangeInboxOutBoxRepository.save(mangeInboxOutbox11);

                } catch (Exception e) {

                }
            }
        }

        defaultResponse.setMsg("DATA SAVE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> saveAuthDataRevision2(AuthRequest authRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
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

        List<BudgetAllocation> budgetAllocationsListData = new ArrayList<>();

        if (authRequest.getAuthGroupId() == null || authRequest.getAuthGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }


        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(authRequest.getAuthGroupId(), "0");

        if (allocationDetails.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AUTH GROUP ID");
        }


        for (Integer i = 0; i < allocationDetails.size(); i++) {
            BudgetAllocationDetails allocationData = allocationDetails.get(i);
            allocationData.setStatus("Approved");
            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocationData);
        }


        List<CdaRevisionData> revisionData = budgetRevisionRepository.findByAuthGroupId(authRequest.getAuthGroupId());

        for (Integer z = 0; z < revisionData.size(); z++) {

            if (revisionData.get(z).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingId(revisionData.get(z).getCdaTransId());
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(revisionData.get(z).getAmountType());
                double parkingAmount = Double.parseDouble(revisionData.get(z).getAmount()) * amountUnit.getAmount();

                double bakiPesa = 0;
                if (parkingAmount < 0) {
                    bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
                } else {
                    bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
                }

                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                cdaParkingTransRepository.save(cdaParkingTrans);

            } else {

                List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(revisionData.get(z).getToUnitId(), revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getAllocTypeId(), "Approved", "0", "0");
                for (Integer y = 0; y < data.size(); y++) {

                    BudgetAllocation budgetAllocationRevision = data.get(y);
                    budgetAllocationRevision.setIsBudgetRevision("1");
                    budgetAllocationRevision.setIsFlag("1");
                    budgetAllocationRepository.save(budgetAllocationRevision);
                }


                double totalAmount = Double.parseDouble(revisionData.get(z).getRemainingAmount()) + Double.parseDouble(revisionData.get(z).getAmount());
                double revisedAmount = Double.parseDouble(revisionData.get(z).getAmount());

                BudgetAllocation budgetAllocation = new BudgetAllocation();
                budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                budgetAllocation.setIsFlag("0");
                budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                budgetAllocation.setFinYear(revisionData.get(z).getFinYearId());
                budgetAllocation.setToUnit(revisionData.get(z).getToUnitId());
                budgetAllocation.setFromUnit(hrData.getUnitId());
                budgetAllocation.setSubHead(revisionData.get(z).getBudgetHeadId());
                budgetAllocation.setIsTYpe("REVISION");
                budgetAllocation.setAllocationTypeId(revisionData.get(z).getAllocTypeId());
                budgetAllocation.setIsBudgetRevision("0");
                budgetAllocation.setUnallocatedAmount("0");
                budgetAllocation.setUnallocatedAmount("0.0000");
                budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(totalAmount + ""));
                budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(revisedAmount + ""));
                budgetAllocation.setUserId(hrData.getPid());
                budgetAllocation.setStatus("Approved");

                budgetAllocation.setAmountType(revisionData.get(z).getAmountType());
                budgetAllocation.setAuthGroupId(revisionData.get(z).getAuthGroupId());

                budgetAllocationRepository.save(budgetAllocation);
                budgetAllocationsListData.add(budgetAllocation);

                List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getToUnitId(), revisionData.get(z).getAllocTypeId(), "0");

                for (Integer k = 0; k < cdaParkingTransList.size(); k++) {
                    CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(k);
                    cdaParkingTrans.setIsFlag("1");
                    cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    cdaParkingTransRepository.save(cdaParkingTrans);
                }
                List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), "0", revisionData.get(z).getAllocTypeId(), revisionData.get(z).getToUnitId(), 0);
                for (Integer q = 0; q < cdaParkingCrAndDr.size(); q++) {
                    CdaParkingCrAndDr cddata = cdaParkingCrAndDr.get(q);
                    cddata.setIsFlag("1");
                    cddata.setIsRevision(0);
                    parkingCrAndDrRepository.save(cddata);
                }
            }
        }


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


        String authgroupid = authRequest.getAuthGroupId();
        List<MangeInboxOutbox> inboxList = mangeInboxOutBoxRepository.findByGroupId(authgroupid);


        HashMap<String, BudgetAllocation> totalUnit = new HashMap<String, BudgetAllocation>();
        for (Integer i = 0; i < budgetAllocationsListData.size(); i++) {
            totalUnit.put(budgetAllocationsListData.get(i).getToUnit(), budgetAllocationsListData.get(i));
        }

        for (Map.Entry<String, BudgetAllocation> entry : totalUnit.entrySet()) {
            String key = entry.getKey();
            BudgetAllocation tabData = entry.getValue();

            MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

            mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
            mangeInboxOutbox.setRemarks("Budget Receipt");
            mangeInboxOutbox.setIsRebase("0");
            mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setToUnit(key);

            if (inboxList.size() > 0) {
                mangeInboxOutbox.setType(inboxList.get(0).getType());
            }


            mangeInboxOutbox.setGroupId(authgroupid);
            mangeInboxOutbox.setIsRebase("0");
            mangeInboxOutbox.setFromUnit(hrData.getUnitId());
            mangeInboxOutbox.setRoleId(hrData.getRoleId());
            mangeInboxOutbox.setCreaterpId(hrData.getPid());
            mangeInboxOutbox.setApproverpId("");
            mangeInboxOutbox.setStatus("Fully Approved");
            mangeInboxOutbox.setAllocationType(tabData.getAllocationTypeId());
            mangeInboxOutbox.setIsFlag("1");
            mangeInboxOutbox.setIsArchive("0");
            mangeInboxOutbox.setIsApproved("0");
            mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint(tabData.getAllocationAmount() + ""));
            mangeInboxOutbox.setIsBgcg("BR");
            mangeInboxOutbox.setState("CR");
            mangeInboxOutbox.setIsRevision(1);
            authgroupid = tabData.getAuthGroupId();

            mangeInboxOutBoxRepository.save(mangeInboxOutbox);


            Authority authority11 = new Authority();
            authority11.setAuthorityId(HelperUtils.getAuthorityId());
            authority11.setAuthority(authRequest.getAuthority());
            authority11.setAuthDate(ConverterUtils.convertDateTotimeStamp(authRequest.getAuthDate()));
            authority11.setDocId(authRequest.getAuthDocId());
            authority11.setAuthUnit(authRequest.getAuthUnitId());
            authority11.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            authority11.setAuthGroupId(authgroupid);
            authority11.setRemarks(authRequest.getAuthUnitId());
            authority11.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            authorityRepository.save(authority11);

        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(authgroupid, hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {
            for (Integer m = 0; m < mangeInboxOutboxList.size(); m++) {
                try {
                    MangeInboxOutbox mangeInboxOutbox11 = mangeInboxOutboxList.get(m);

                    mangeInboxOutbox11.setStatus("Fully Approved");
                    mangeInboxOutbox11.setIsApproved("1");
                    mangeInboxOutBoxRepository.save(mangeInboxOutbox11);

                } catch (Exception e) {

                }
            }
        }

        defaultResponse.setMsg("DATA SAVE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> saveAuthDataRevision3(AuthRequest authRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        if (hrData == null) {
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

        List<BudgetAllocation> budgetAllocationsListData = new ArrayList<>();

        if (authRequest.getAuthGroupId() == null || authRequest.getAuthGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }


        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(authRequest.getAuthGroupId(), "0");

        if (allocationDetails.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AUTH GROUP ID");
        }

        for (Integer i = 0; i < allocationDetails.size(); i++) {
            BudgetAllocationDetails allocationData = allocationDetails.get(i);
            allocationData.setStatus("Approved");
            allocationData.setPrevInitial("1");
            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocationData);
        }


        List<CdaRevisionData> revisionData = budgetRevisionRepository.findByAuthGroupIdAndIsAutoAssignAllocation(authRequest.getAuthGroupId(), "0");

        for (Integer z = 0; z < revisionData.size(); z++) {

            if (revisionData.get(z).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingId(revisionData.get(z).getCdaTransId());
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(revisionData.get(z).getAmountType());
                double parkingAmount = Double.parseDouble(revisionData.get(z).getAmount()) * amountUnit.getAmount();

                double bakiPesa = 0;
                if (parkingAmount < 0) {
                    bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
                } else {
                    bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
                }

                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                cdaParkingTransRepository.save(cdaParkingTrans);

            } else {


                if (Double.parseDouble(revisionData.get(z).getAmount()) > 0) {

                    double totalAmount = Double.parseDouble(revisionData.get(z).getRemainingAmount()) + Double.parseDouble(revisionData.get(z).getAmount());
                    double revisedAmount = Double.parseDouble(revisionData.get(z).getAmount());

                    double allocationAmount = 0;
                    List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(revisionData.get(z).getToUnitId(), revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getAllocTypeId(), "Approved", "0", "0");
                    for (Integer y = 0; y < data.size(); y++) {
                        BudgetAllocation budgetAllocationRevision = data.get(y);
                        budgetAllocationRevision.setIsBudgetRevision("1");
                        budgetAllocationRevision.setIsFlag("1");

                        budgetAllocationRevision.setPrevAllocAmount(budgetAllocationRevision.getAllocationAmount() + "");
                        budgetAllocationRevision.setAllocationAmount(totalAmount + "");

                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationRevision.getAmountType());
                        if (budgetAllocationRevision.getPrevInitial().equalsIgnoreCase("1")) {
                            allocationAmount = allocationAmount + Double.parseDouble(budgetAllocationRevision.getPrevAllocAmount()) * amountUnit.getAmount();
                        } else {
                            allocationAmount = allocationAmount + Double.parseDouble(budgetAllocationRevision.getAllocationAmount()) * amountUnit.getAmount();
                        }
                        budgetAllocationRepository.save(budgetAllocationRevision);
                    }


                    AmountUnit actualAmount = amountUnitRepository.findByAmountTypeId(revisionData.get(z).getAmountType());
                    double mainAmountRemening = allocationAmount / actualAmount.getAmount();

                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setFinYear(revisionData.get(z).getFinYearId());
                    budgetAllocation.setToUnit(revisionData.get(z).getToUnitId());
                    budgetAllocation.setFromUnit(hrData.getUnitId());
                    budgetAllocation.setSubHead(revisionData.get(z).getBudgetHeadId());
                    budgetAllocation.setIsTYpe("REVISION");
                    budgetAllocation.setAllocationTypeId(revisionData.get(z).getAllocTypeId());
                    budgetAllocation.setIsBudgetRevision("0");
                    budgetAllocation.setUnallocatedAmount("0");
                    budgetAllocation.setUnallocatedAmount("0.0000");
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(totalAmount + ""));
                    budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(revisedAmount + ""));
                    budgetAllocation.setUserId(hrData.getPid());
                    budgetAllocation.setStatus("Approved");
                    budgetAllocation.setPrevInitial("1");
                    budgetAllocation.setPrevAllocAmount(mainAmountRemening + "");
                    budgetAllocation.setAmountType(revisionData.get(z).getAmountType());
                    budgetAllocation.setAuthGroupId(revisionData.get(z).getAuthGroupId());


                    budgetAllocationRepository.save(budgetAllocation);
                    budgetAllocationsListData.add(budgetAllocation);

                    List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getToUnitId(), revisionData.get(z).getAllocTypeId(), "0");

                    for (Integer k = 0; k < cdaParkingTransList.size(); k++) {
                        CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(k);
                        cdaParkingTrans.setIsFlag("1");
                        cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTransRepository.save(cdaParkingTrans);
                    }

                    List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), "0", revisionData.get(z).getAllocTypeId(), revisionData.get(z).getToUnitId(), 0);
                    for (Integer q = 0; q < cdaParkingCrAndDr.size(); q++) {
                        CdaParkingCrAndDr cddata = cdaParkingCrAndDr.get(q);
                        cddata.setIsFlag("1");
                        cddata.setIsRevision(1);
                        parkingCrAndDrRepository.save(cddata);
                    }


                } else {

                    String currentUnitId = revisionData.get(z).getToUnitId();
                    List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
                    unitList.add(cgUnitRepository.findByUnit(currentUnitId));

                    for (Integer d = 0; d < unitList.size(); d++) {


                        List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(unitList.get(d).getUnit(), revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getAllocTypeId(), "Approved", "0", "0");
                        for (Integer y = 0; y < budgetAllocationDetailsList.size(); y++) {
                            BudgetAllocationDetails budgetAllocationRevision = budgetAllocationDetailsList.get(y);
//                            budgetAllocationRevision.setIsTYpe("REVISION");
//                            budgetAllocationRevision.setPrevInitial("1");
//                            budgetAllocationRevision.setPrevAllocAmount(budgetAllocationRevision.getAllocationAmount() + "");
//
//                            budgetAllocationRevision.setAllocationAmount("0");
//                            budgetAllocationRevision.setIsDelete("1");
                            budgetAllocationRevision.setIsBudgetRevision("1");
                            budgetAllocationDetailsRepository.save(budgetAllocationRevision);


                        }

                        List<BudgetAllocation> dataBudget = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unitList.get(d).getUnit(), revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getAllocTypeId(), "Approved", "0", "0");
                        for (Integer y = 0; y < dataBudget.size(); y++) {
                            BudgetAllocation budgetAllocationRevision = dataBudget.get(y);
                            budgetAllocationRevision.setIsTYpe("REVISION1");

//                            if(budgetAllocationRevision.getPrevInitial().equalsIgnoreCase("1")){
//                                budgetAllocationRevision.setPrevInitial("1");
//                                budgetAllocationRevision.setAllocationAmount("0");
//                            }else{
//                                budgetAllocationRevision.setPrevInitial("1");
//                                budgetAllocationRevision.setPrevAllocAmount(budgetAllocationRevision.getAllocationAmount() + "");
//                                budgetAllocationRevision.setAllocationAmount("0");
//                            }

//                            budgetAllocationRevision.setIsFlag("1");
                            budgetAllocationRevision.setIsBudgetRevision("1");
                            budgetAllocationRepository.save(budgetAllocationRevision);
                        }

                        List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), unitList.get(d).getUnit(), revisionData.get(z).getAllocTypeId(), "0");

                        for (Integer k = 0; k < cdaParkingTransList.size(); k++) {
                            CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(k);
                            cdaParkingTrans.setIsFlag("1");
                            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParkingTransRepository.save(cdaParkingTrans);
                        }

                        List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), "0", revisionData.get(z).getAllocTypeId(), unitList.get(d).getUnit(), 0);
                        for (Integer q = 0; q < cdaParkingCrAndDr.size(); q++) {
                            CdaParkingCrAndDr cddata = cdaParkingCrAndDr.get(q);
                            cddata.setIsFlag("1");
                            cddata.setIsRevision(1);
                            parkingCrAndDrRepository.save(cddata);
                        }
                    }
                }
            }
        }


        ArrayList<String> unitList = new ArrayList<>();
        List<CdaRevisionData> revisionRemainingAmountSend = budgetRevisionRepository.findByAuthGroupIdAndIsAutoAssignAllocation(authRequest.getAuthGroupId(), "1");
        for (Integer v = 0; v < revisionRemainingAmountSend.size(); v++) {

            if (revisionRemainingAmountSend.get(v).getCdaTransId() == null) {

                double data = Double.parseDouble(revisionRemainingAmountSend.get(v).getAmount());
                if (data > 0) {


                    List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(revisionRemainingAmountSend.get(v).getToUnitId(), revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), revisionRemainingAmountSend.get(v).getAllocTypeId(), "Approved", "0", "0");
                    for (Integer y = 0; y < budgetAllocationDetailsList.size(); y++) {
                        BudgetAllocationDetails budgetAllocationRevision = budgetAllocationDetailsList.get(y);
                        budgetAllocationRevision.setIsBudgetRevision("1");
                        budgetAllocationRevision.setIsTYpe("REVISION1");
//                        budgetAllocationRevision.setIsDelete("1");
//                        budgetAllocationRevision.setPrevInitial("1");
//                        budgetAllocationRevision.setPrevAllocAmount(budgetAllocationRevision.getAllocationAmount() + "");
//                        budgetAllocationRevision.setAllocationAmount(revisionRemainingAmountSend.get(v).getAmount() + "");

                        budgetAllocationDetailsRepository.save(budgetAllocationRevision);
                    }


                    double allocationAmount = 0;
                    List<BudgetAllocation> dataBudget = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(revisionRemainingAmountSend.get(v).getToUnitId(), revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), revisionRemainingAmountSend.get(v).getAllocTypeId(), "Approved", "0", "0");
                    for (Integer y = 0; y < dataBudget.size(); y++) {
                        BudgetAllocation budgetAllocationRevision = dataBudget.get(y);
                        budgetAllocationRevision.setIsBudgetRevision("1");
//                        budgetAllocationRevision.setIsFlag("1");
                        budgetAllocationRevision.setIsTYpe("REVISION1");

//                        budgetAllocationRevision.setPrevInitial("1");
//                        budgetAllocationRevision.setPrevAllocAmount(revisionRemainingAmountSend.get(v).getAllocationAmount() + "");
//                        budgetAllocationRevision.setAllocationAmount(ConverterUtils.addDecimalPoint(revisionRemainingAmountSend.get(v).getAmount() + ""));

                        budgetAllocationRepository.save(budgetAllocationRevision);

                    }


                    String authGroupId = HelperUtils.getAuthorityGroupId();
                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setFinYear(revisionRemainingAmountSend.get(v).getFinYearId());
                    budgetAllocation.setToUnit(revisionRemainingAmountSend.get(v).getToUnitId());
                    budgetAllocation.setFromUnit(hrData.getUnitId());
                    budgetAllocation.setSubHead(revisionRemainingAmountSend.get(v).getBudgetHeadId());
                    budgetAllocation.setIsTYpe("REVISION");
                    budgetAllocation.setAllocationTypeId(revisionRemainingAmountSend.get(v).getAllocTypeId());
                    budgetAllocation.setIsBudgetRevision("0");
                    budgetAllocation.setUnallocatedAmount("0");
                    budgetAllocation.setUnallocatedAmount("0.0000");
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(revisionRemainingAmountSend.get(v).getAmount() + ""));
                    budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(revisionRemainingAmountSend.get(v).getAmount() + ""));
                    budgetAllocation.setUserId(hrData.getPid());
                    budgetAllocation.setStatus("Approved");
                    budgetAllocation.setPrevInitial("1");
                    budgetAllocation.setPrevAllocAmount("");

                    budgetAllocation.setAmountType(revisionRemainingAmountSend.get(v).getAmountType());
                    budgetAllocation.setAuthGroupId(authGroupId);


                    List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), revisionRemainingAmountSend.get(v).getToUnitId(), revisionRemainingAmountSend.get(v).getAllocTypeId(), "0");

                    for (Integer k = 0; k < cdaParkingTransList.size(); k++) {
                        CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(k);
                        cdaParkingTrans.setIsFlag("1");
                        cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTransRepository.save(cdaParkingTrans);
                    }

                    List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), "0", revisionRemainingAmountSend.get(v).getAllocTypeId(), revisionRemainingAmountSend.get(v).getToUnitId(), 0);
                    for (Integer q = 0; q < cdaParkingCrAndDr.size(); q++) {
                        CdaParkingCrAndDr cddata = cdaParkingCrAndDr.get(q);
                        cddata.setIsFlag("1");
                        cddata.setIsRevision(1);
                        parkingCrAndDrRepository.save(cddata);
                    }


                    budgetAllocationRepository.save(budgetAllocation);
                    unitList.add(revisionRemainingAmountSend.get(v).getToUnitId());

                    String authgroupid = authRequest.getAuthGroupId();
                    List<MangeInboxOutbox> inboxList = mangeInboxOutBoxRepository.findByGroupId(authgroupid);

                    MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

                    mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
                    mangeInboxOutbox.setRemarks("Budget Receipt");
                    mangeInboxOutbox.setIsRebase("0");
                    mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setToUnit(revisionRemainingAmountSend.get(v).getToUnitId());

                    if (inboxList.size() > 0) {
                        mangeInboxOutbox.setType(inboxList.get(0).getType());
                    }

                    mangeInboxOutbox.setGroupId(authGroupId);
                    mangeInboxOutbox.setIsRebase("0");
                    mangeInboxOutbox.setFromUnit(hrData.getUnitId());
                    mangeInboxOutbox.setRoleId(hrData.getRoleId());
                    mangeInboxOutbox.setCreaterpId(hrData.getPid());
                    mangeInboxOutbox.setApproverpId("");
                    mangeInboxOutbox.setStatus("Fully Approved");
                    mangeInboxOutbox.setAllocationType(revisionRemainingAmountSend.get(v).getAllocTypeId());
                    mangeInboxOutbox.setIsFlag("1");
                    mangeInboxOutbox.setIsArchive("0");
                    mangeInboxOutbox.setIsApproved("0");
                    mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint(revisionRemainingAmountSend.get(v).getAmount() + ""));
                    mangeInboxOutbox.setIsBgcg("BR");
                    mangeInboxOutbox.setState("CR");
                    mangeInboxOutbox.setIsRevision(1);

                    mangeInboxOutBoxRepository.save(mangeInboxOutbox);

                }

            }


        }

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


        String authgroupid = authRequest.getAuthGroupId();
        List<MangeInboxOutbox> inboxList = mangeInboxOutBoxRepository.findByGroupId(authgroupid);


        HashMap<String, BudgetAllocation> totalUnit = new HashMap<String, BudgetAllocation>();

        for (Integer i = 0; i < budgetAllocationsListData.size(); i++) {
            if (!unitList.contains(budgetAllocationsListData.get(i))) {
                totalUnit.put(budgetAllocationsListData.get(i).getToUnit(), budgetAllocationsListData.get(i));
            }
        }

        for (Map.Entry<String, BudgetAllocation> entry : totalUnit.entrySet()) {
            String key = entry.getKey();
            BudgetAllocation tabData = entry.getValue();

            MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

            mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
            mangeInboxOutbox.setRemarks("Budget Receipt");
            mangeInboxOutbox.setIsRebase("0");
            mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setToUnit(key);

            if (inboxList.size() > 0) {
                mangeInboxOutbox.setType(inboxList.get(0).getType());
            }

            mangeInboxOutbox.setGroupId(authgroupid);
            mangeInboxOutbox.setIsRebase("0");
            mangeInboxOutbox.setFromUnit(hrData.getUnitId());
            mangeInboxOutbox.setRoleId(hrData.getRoleId());
            mangeInboxOutbox.setCreaterpId(hrData.getPid());
            mangeInboxOutbox.setApproverpId("");
            mangeInboxOutbox.setStatus("Fully Approved");
            mangeInboxOutbox.setAllocationType(tabData.getAllocationTypeId());
            mangeInboxOutbox.setIsFlag("1");
            mangeInboxOutbox.setIsArchive("0");
            mangeInboxOutbox.setIsApproved("0");
            mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint(tabData.getAllocationAmount() + ""));
            mangeInboxOutbox.setIsBgcg("BR");
            mangeInboxOutbox.setState("CR");
            mangeInboxOutbox.setIsRevision(1);
            authgroupid = tabData.getAuthGroupId();

            mangeInboxOutBoxRepository.save(mangeInboxOutbox);


            Authority authority11 = new Authority();
            authority11.setAuthorityId(HelperUtils.getAuthorityId());
            authority11.setAuthority(authRequest.getAuthority());
            authority11.setAuthDate(ConverterUtils.convertDateTotimeStamp(authRequest.getAuthDate()));
            authority11.setDocId(authRequest.getAuthDocId());
            authority11.setAuthUnit(authRequest.getAuthUnitId());
            authority11.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            authority11.setAuthGroupId(authgroupid);
            authority11.setRemarks(authRequest.getAuthUnitId());
            authority11.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            authorityRepository.save(authority11);

        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(authgroupid, hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {
            for (Integer m = 0; m < mangeInboxOutboxList.size(); m++) {
                try {
                    MangeInboxOutbox mangeInboxOutbox11 = mangeInboxOutboxList.get(m);

                    mangeInboxOutbox11.setStatus("Fully Approved");
                    mangeInboxOutbox11.setIsApproved("1");
                    mangeInboxOutBoxRepository.save(mangeInboxOutbox11);

                } catch (Exception e) {

                }
            }
        }

        defaultResponse.setMsg("DATA SAVE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> saveAuthDataRevision33(AuthRequest authRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");


        String mainOnlyViewAuthGroup = HelperUtils.getAuthorityGroupId();

        if (hrData == null) {
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

        List<BudgetAllocation> budgetAllocationsListData = new ArrayList<>();

        if (authRequest.getAuthGroupId() == null || authRequest.getAuthGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }


        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDelete(authRequest.getAuthGroupId(), "0");

        if (allocationDetails.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AUTH GROUP ID");
        }

        for (Integer i = 0; i < allocationDetails.size(); i++) {
            BudgetAllocationDetails allocationData = allocationDetails.get(i);
            allocationData.setStatus("Approved");
            allocationData.setPrevInitial("1");
            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocationData);
        }


        List<CdaRevisionData> revisionData = budgetRevisionRepository.findByAuthGroupIdAndIsAutoAssignAllocation(authRequest.getAuthGroupId(), "0");

        for (Integer z = 0; z < revisionData.size(); z++) {

            if(revisionData.get(z).getIsComplete() != null){
                if(revisionData.get(z).getIsComplete().equalsIgnoreCase("1")){
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ACTION ALREADY PERFORMED.");
                }
            }

            if (revisionData.get(z).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingId(revisionData.get(z).getCdaTransId());
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(revisionData.get(z).getAmountType());
                double parkingAmount = Double.parseDouble(revisionData.get(z).getAmount()) * amountUnit.getAmount();

                double bakiPesa = 0;
                if (parkingAmount < 0) {
                    bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
                } else {
                    bakiPesa = (remainingCdaParkingAmount + parkingAmount) / cadAmountUnit.getAmount();
                }

                cdaParkingTrans.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(bakiPesa + ""));
                cdaParkingTransRepository.save(cdaParkingTrans);

            } else {


                if (Double.parseDouble(revisionData.get(z).getAmount()) > 0) {


                    double totalAmount = Double.parseDouble(revisionData.get(z).getRemainingAmount()) + Double.parseDouble(revisionData.get(z).getAmount());
                    double revisedAmount = Double.parseDouble(revisionData.get(z).getAmount());


                    double allocationAmount = 0;
                    List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(revisionData.get(z).getToUnitId(), revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getAllocTypeId(), "Approved", "0", "0");
                    for (Integer y = 0; y < data.size(); y++) {
                        BudgetAllocation budgetAllocationRevision = data.get(y);
                        budgetAllocationRevision.setIsBudgetRevision("1");
                        budgetAllocationRevision.setIsFlag("1");
                        budgetAllocationRevision.setRevisedAmount(revisionData.get(z).getReviserAmount());

//                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationRevision.getAmountType());
//                        if (budgetAllocationRevision.getPrevInitial().equalsIgnoreCase("1")) {
//                            allocationAmount = allocationAmount + Double.parseDouble(budgetAllocationRevision.getPrevAllocAmount()) * amountUnit.getAmount();
//                        } else {
//                            allocationAmount = allocationAmount + Double.parseDouble(budgetAllocationRevision.getAllocationAmount()) * amountUnit.getAmount();
//                        }
//
//
//                        budgetAllocationRevision.setPrevAllocAmount(budgetAllocationRevision.getAllocationAmount() + "");
//                        budgetAllocationRevision.setAllocationAmount(totalAmount + "");

                        budgetAllocationRepository.save(budgetAllocationRevision);
                    }


                    AmountUnit actualAmount = amountUnitRepository.findByAmountTypeId(revisionData.get(z).getAmountType());
                    double mainAmountRemening = allocationAmount / actualAmount.getAmount();

                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setFinYear(revisionData.get(z).getFinYearId());
                    budgetAllocation.setToUnit(revisionData.get(z).getToUnitId());
                    budgetAllocation.setFromUnit(hrData.getUnitId());
                    budgetAllocation.setSubHead(revisionData.get(z).getBudgetHeadId());
                    budgetAllocation.setIsTYpe("REVISION");
                    budgetAllocation.setAllocationTypeId(revisionData.get(z).getAllocTypeId());
                    budgetAllocation.setIsBudgetRevision("0");
                    budgetAllocation.setUnallocatedAmount("0");
                    budgetAllocation.setUnallocatedAmount("0.0000");
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(totalAmount + ""));
                    budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(revisedAmount + ""));
                    budgetAllocation.setUserId(hrData.getPid());
                    budgetAllocation.setStatus("Approved");
                    budgetAllocation.setPrevInitial("1");
                    budgetAllocation.setPrevAllocAmount(mainAmountRemening + "");
                    budgetAllocation.setAmountType(revisionData.get(z).getAmountType());
                    budgetAllocation.setAuthGroupId(revisionData.get(z).getAuthGroupId());
                    budgetAllocationRepository.save(budgetAllocation);


                    MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
                    BudgetHead budgetHead = subHeadRepository.findByBudgetCodeId(budgetAllocation.getSubHead());
                    mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
                    mangeInboxOutbox.setRemarks("Budget Revised");
                    mangeInboxOutbox.setIsRebase("0");
                    mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setToUnit(budgetAllocation.getToUnit());
                    mangeInboxOutbox.setType("In " + budgetHead.getSubHeadDescr() + " head budget revised by your head unit");
                    mangeInboxOutbox.setGroupId(mainOnlyViewAuthGroup);
                    mangeInboxOutbox.setIsRebase("0");
                    mangeInboxOutbox.setFromUnit(hrData.getUnitId());
                    mangeInboxOutbox.setRoleId(hrData.getRoleId());
                    mangeInboxOutbox.setCreaterpId(hrData.getPid());
                    mangeInboxOutbox.setApproverpId("");
                    mangeInboxOutbox.setStatus("Fully Approved");
                    mangeInboxOutbox.setAllocationType("");
                    mangeInboxOutbox.setIsFlag("1");
                    mangeInboxOutbox.setIsArchive("0");
                    mangeInboxOutbox.setIsApproved("0");
                    mangeInboxOutbox.setIsBgcg("UR");
                    mangeInboxOutbox.setState("CR");
                    mangeInboxOutbox.setIsRevision(1);

                    mangeInboxOutBoxRepository.save(mangeInboxOutbox);


                    budgetAllocationsListData.add(budgetAllocation);

                    List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getToUnitId(), revisionData.get(z).getAllocTypeId(), "0");

                    for (Integer k = 0; k < cdaParkingTransList.size(); k++) {
                        CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(k);
                        cdaParkingTrans.setIsFlag("1");
                        cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTransRepository.save(cdaParkingTrans);
                    }

                    List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), "0", revisionData.get(z).getAllocTypeId(), revisionData.get(z).getToUnitId(), 0);
                    for (Integer q = 0; q < cdaParkingCrAndDr.size(); q++) {
                        CdaParkingCrAndDr cddata = cdaParkingCrAndDr.get(q);
                        cddata.setIsFlag("1");
                        cddata.setIsRevision(1);
                        parkingCrAndDrRepository.save(cddata);
                    }


//                  Budget Revised All Unit Jisko diya gya hai

                    String currentUnitId = revisionData.get(z).getToUnitId();
                    List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
                    unitList.add(cgUnitRepository.findByUnit(currentUnitId));

                    for (Integer d = 0; d < unitList.size(); d++) {

                        if (unitList.get(d).getUnit().equalsIgnoreCase(revisionData.get(z).getToUnitId())) {
                            continue;
                        }

                        List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(unitList.get(d).getUnit(), revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getAllocTypeId(), "Approved", "0", "0");
                        for (Integer y = 0; y < budgetAllocationDetailsList.size(); y++) {
                            BudgetAllocationDetails budgetAllocationRevision = budgetAllocationDetailsList.get(y);
                            budgetAllocationRevision.setIsTYpe("REVISION");
                            budgetAllocationRevision.setPrevInitial("1");
//                            budgetAllocationRevision.setPrevAllocAmount(budgetAllocationRevision.getAllocationAmount() + "");

//                            budgetAllocationRevision.setAllocationAmount("0");
                            budgetAllocationDetailsRepository.save(budgetAllocationRevision);


                        }

                        List<BudgetAllocation> dataBudget = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unitList.get(d).getUnit(), revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getAllocTypeId(), "Approved", "0", "0");
                        for (Integer y = 0; y < dataBudget.size(); y++) {
                            BudgetAllocation budgetAllocationRevision = dataBudget.get(y);
                            budgetAllocationRevision.setIsTYpe("REVISION1");

                            if (budgetAllocationRevision.getPrevInitial().equalsIgnoreCase("1")) {
                            } else {
                                budgetAllocationRevision.setPrevAllocAmount(budgetAllocationRevision.getAllocationAmount() + "");
                            }

                            budgetAllocationRevision.setPrevInitial("1");
                            budgetAllocationRevision.setAllocationAmount("0");
                            budgetAllocationRepository.save(budgetAllocationRevision);


                            List<MangeInboxOutbox> checkMsgAlreadySendOrNot = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(mainOnlyViewAuthGroup, unitList.get(d).getUnit());

                            if (checkMsgAlreadySendOrNot.size() == 0) {
                                MangeInboxOutbox mangeInboxOutbox11 = new MangeInboxOutbox();

                                BudgetHead budgetHead11 = subHeadRepository.findByBudgetCodeId(budgetAllocationRevision.getSubHead());
                                mangeInboxOutbox11.setMangeInboxId(HelperUtils.getMangeInboxId());
                                mangeInboxOutbox11.setRemarks("Budget Revised");
                                mangeInboxOutbox11.setIsRebase("0");
                                mangeInboxOutbox11.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                mangeInboxOutbox11.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                mangeInboxOutbox11.setToUnit(unitList.get(d).getUnit());
                                mangeInboxOutbox11.setType("In " + budgetHead11.getSubHeadDescr() + " head budget revised by your head unit");
                                mangeInboxOutbox11.setGroupId(mainOnlyViewAuthGroup);
                                mangeInboxOutbox11.setIsRebase("0");
                                mangeInboxOutbox11.setFromUnit(hrData.getUnitId());
                                mangeInboxOutbox11.setRoleId(hrData.getRoleId());
                                mangeInboxOutbox11.setCreaterpId(hrData.getPid());
                                mangeInboxOutbox11.setApproverpId("");
                                mangeInboxOutbox11.setStatus("Fully Approved");
                                mangeInboxOutbox11.setAllocationType("");
                                mangeInboxOutbox11.setIsFlag("1");
                                mangeInboxOutbox11.setIsArchive("0");
                                mangeInboxOutbox11.setIsApproved("0");
                                mangeInboxOutbox11.setIsBgcg("UR");
                                mangeInboxOutbox11.setState("CR");
                                mangeInboxOutbox11.setIsRevision(1);

                                mangeInboxOutBoxRepository.save(mangeInboxOutbox11);
                            }


                        }

                        List<CdaParkingTrans> cdaParkingTransList11 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), unitList.get(d).getUnit(), revisionData.get(z).getAllocTypeId(), "0");

                        for (Integer k = 0; k < cdaParkingTransList11.size(); k++) {
                            CdaParkingTrans cdaParkingTrans = cdaParkingTransList11.get(k);
                            cdaParkingTrans.setIsFlag("1");
                            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParkingTransRepository.save(cdaParkingTrans);
                        }

                        List<CdaParkingCrAndDr> cdaParkingCrAndDr11 = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), "0", revisionData.get(z).getAllocTypeId(), unitList.get(d).getUnit(), 0);
                        for (Integer q = 0; q < cdaParkingCrAndDr11.size(); q++) {
                            CdaParkingCrAndDr cddata = cdaParkingCrAndDr11.get(q);
                            cddata.setIsFlag("1");
                            cddata.setIsRevision(1);
                            parkingCrAndDrRepository.save(cddata);
                        }
                    }


                } else {

                    //               Budget Revised All Unit Jisko Kata gya hai
                    String currentUnitId = revisionData.get(z).getToUnitId();
                    List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
                    unitList.add(cgUnitRepository.findByUnit(currentUnitId));

                    for (Integer d = 0; d < unitList.size(); d++) {


                        List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(unitList.get(d).getUnit(), revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getAllocTypeId(), "Approved", "0", "0");
                        for (Integer y = 0; y < budgetAllocationDetailsList.size(); y++) {
                            BudgetAllocationDetails budgetAllocationRevision = budgetAllocationDetailsList.get(y);
                            budgetAllocationRevision.setIsTYpe("REVISION");
                            budgetAllocationRevision.setPrevInitial("1");
//                            budgetAllocationRevision.setPrevAllocAmount(budgetAllocationRevision.getAllocationAmount() + "");

//                            budgetAllocationRevision.setAllocationAmount("0");
                            budgetAllocationDetailsRepository.save(budgetAllocationRevision);


                        }

                        List<BudgetAllocation> dataBudget = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(unitList.get(d).getUnit(), revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), revisionData.get(z).getAllocTypeId(), "Approved", "0", "0");
                        for (Integer y = 0; y < dataBudget.size(); y++) {
                            BudgetAllocation budgetAllocationRevision = dataBudget.get(y);
                            budgetAllocationRevision.setIsTYpe("REVISION1");

                            if (budgetAllocationRevision.getPrevInitial().equalsIgnoreCase("1")) {
                            } else {
                                budgetAllocationRevision.setPrevAllocAmount(budgetAllocationRevision.getAllocationAmount() + "");
                            }

                            budgetAllocationRevision.setPrevInitial("1");
                            budgetAllocationRevision.setAllocationAmount("0");
                            budgetAllocationRepository.save(budgetAllocationRevision);


                            List<MangeInboxOutbox> checkMsgAlreadySendOrNot = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(mainOnlyViewAuthGroup, unitList.get(d).getUnit());

                            if (checkMsgAlreadySendOrNot.size() == 0) {
                                MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

                                BudgetHead budgetHead = subHeadRepository.findByBudgetCodeId(budgetAllocationRevision.getSubHead());
                                mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
                                mangeInboxOutbox.setRemarks("Budget Revised");
                                mangeInboxOutbox.setIsRebase("0");
                                mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                mangeInboxOutbox.setToUnit(unitList.get(d).getUnit());
                                mangeInboxOutbox.setType("In " + budgetHead.getSubHeadDescr() + " head budget revised by your head unit");
                                mangeInboxOutbox.setGroupId(mainOnlyViewAuthGroup);
                                mangeInboxOutbox.setIsRebase("0");
                                mangeInboxOutbox.setFromUnit(hrData.getUnitId());
                                mangeInboxOutbox.setRoleId(hrData.getRoleId());
                                mangeInboxOutbox.setCreaterpId(hrData.getPid());
                                mangeInboxOutbox.setApproverpId("");
                                mangeInboxOutbox.setStatus("Fully Approved");
                                mangeInboxOutbox.setAllocationType("");
                                mangeInboxOutbox.setIsFlag("1");
                                mangeInboxOutbox.setIsArchive("0");
                                mangeInboxOutbox.setIsApproved("0");
                                mangeInboxOutbox.setIsBgcg("UR");
                                mangeInboxOutbox.setState("CR");
                                mangeInboxOutbox.setIsRevision(1);

                                mangeInboxOutBoxRepository.save(mangeInboxOutbox);
                            }


                        }

                        List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), unitList.get(d).getUnit(), revisionData.get(z).getAllocTypeId(), "0");

                        for (Integer k = 0; k < cdaParkingTransList.size(); k++) {
                            CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(k);
                            cdaParkingTrans.setIsFlag("1");
                            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParkingTransRepository.save(cdaParkingTrans);
                        }

                        List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionData.get(z).getFinYearId(), revisionData.get(z).getBudgetHeadId(), "0", revisionData.get(z).getAllocTypeId(), unitList.get(d).getUnit(), 0);
                        for (Integer q = 0; q < cdaParkingCrAndDr.size(); q++) {
                            CdaParkingCrAndDr cddata = cdaParkingCrAndDr.get(q);
                            cddata.setIsFlag("1");
                            cddata.setIsRevision(1);
                            parkingCrAndDrRepository.save(cddata);
                        }


                    }
                }
            }

            CdaRevisionData cdaRevisionData = revisionData.get(z);
            cdaRevisionData.setIsComplete("1");
            budgetRevisionRepository.save(cdaRevisionData);
        }


        ArrayList<String> unitList = new ArrayList<>();
        List<CdaRevisionData> revisionRemainingAmountSend = budgetRevisionRepository.findByAuthGroupIdAndIsAutoAssignAllocation(authRequest.getAuthGroupId(), "1");
        for (Integer v = 0; v < revisionRemainingAmountSend.size(); v++) {


            if(revisionRemainingAmountSend.get(v).getIsComplete() != null){
                if(revisionRemainingAmountSend.get(v).getIsComplete().equalsIgnoreCase("1")){
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ACTION ALREADY PERFORMED.");
                }
            }


            if (revisionRemainingAmountSend.get(v).getCdaTransId() == null) {

                double data = Double.parseDouble(revisionRemainingAmountSend.get(v).getAmount());
                if (data > 0) {


                    List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(revisionRemainingAmountSend.get(v).getToUnitId(), revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), revisionRemainingAmountSend.get(v).getAllocTypeId(), "Approved", "0", "0");
                    for (Integer y = 0; y < budgetAllocationDetailsList.size(); y++) {
                        BudgetAllocationDetails budgetAllocationRevision = budgetAllocationDetailsList.get(y);
                        budgetAllocationRevision.setIsBudgetRevision("1");
                        budgetAllocationRevision.setIsTYpe("REVISION1");
                        budgetAllocationRevision.setIsDelete("1");

                        budgetAllocationRevision.setPrevInitial("1");
//                        budgetAllocationRevision.setPrevAllocAmount(budgetAllocationRevision.getAllocationAmount() + "");
//                        budgetAllocationRevision.setAllocationAmount(revisionRemainingAmountSend.get(v).getAmount() + "");

                        budgetAllocationDetailsRepository.save(budgetAllocationRevision);
                    }


                    double allocationAmount = 0;
                    List<BudgetAllocation> dataBudget = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(revisionRemainingAmountSend.get(v).getToUnitId(), revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), revisionRemainingAmountSend.get(v).getAllocTypeId(), "Approved", "0", "0");
                    for (Integer y = 0; y < dataBudget.size(); y++) {
                        BudgetAllocation budgetAllocationRevision = dataBudget.get(y);
                        budgetAllocationRevision.setIsBudgetRevision("1");
                        budgetAllocationRevision.setIsFlag("1");
                        budgetAllocationRevision.setIsTYpe("REVISION1");

                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationRevision.getAmountType());
                        if (budgetAllocationRevision.getPrevInitial().equalsIgnoreCase("1")) {
                            allocationAmount = allocationAmount + Double.parseDouble(budgetAllocationRevision.getPrevAllocAmount()) * amountUnit.getAmount();
                        } else {
                            allocationAmount = allocationAmount + Double.parseDouble(budgetAllocationRevision.getAllocationAmount()) * amountUnit.getAmount();
                        }


                        budgetAllocationRevision.setPrevInitial("1");
                        budgetAllocationRevision.setPrevAllocAmount(revisionRemainingAmountSend.get(v).getAllocationAmount() + "");
                        budgetAllocationRevision.setAllocationAmount(ConverterUtils.addDecimalPoint(revisionRemainingAmountSend.get(v).getAmount() + ""));

                        budgetAllocationRepository.save(budgetAllocationRevision);

                    }

                    AmountUnit actualAmount = amountUnitRepository.findByAmountTypeId(revisionData.get(v).getAmountType());
                    double mainAmountRemening = allocationAmount / actualAmount.getAmount();

                    String authGroupId = HelperUtils.getAuthorityGroupId();
                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setFinYear(revisionRemainingAmountSend.get(v).getFinYearId());
                    budgetAllocation.setToUnit(revisionRemainingAmountSend.get(v).getToUnitId());
                    budgetAllocation.setFromUnit(hrData.getUnitId());
                    budgetAllocation.setSubHead(revisionRemainingAmountSend.get(v).getBudgetHeadId());
                    budgetAllocation.setIsTYpe("REVISION");
                    budgetAllocation.setAllocationTypeId(revisionRemainingAmountSend.get(v).getAllocTypeId());
                    budgetAllocation.setIsBudgetRevision("0");
                    budgetAllocation.setUnallocatedAmount("0");
                    budgetAllocation.setUnallocatedAmount("0.0000");
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(revisionRemainingAmountSend.get(v).getAmount() + ""));
                    budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(revisionRemainingAmountSend.get(v).getReviserAmount() + ""));
                    budgetAllocation.setUserId(hrData.getPid());
                    budgetAllocation.setStatus("Approved");
                    budgetAllocation.setPrevInitial("1");
                    budgetAllocation.setPrevAllocAmount(mainAmountRemening + "");

                    budgetAllocation.setAmountType(revisionRemainingAmountSend.get(v).getAmountType());
                    budgetAllocation.setAuthGroupId(authGroupId);


                    List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), revisionRemainingAmountSend.get(v).getToUnitId(), revisionRemainingAmountSend.get(v).getAllocTypeId(), "0");

                    for (Integer k = 0; k < cdaParkingTransList.size(); k++) {
                        CdaParkingTrans cdaParkingTrans = cdaParkingTransList.get(k);
                        cdaParkingTrans.setIsFlag("1");
                        cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTransRepository.save(cdaParkingTrans);
                    }

                    List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), "0", revisionRemainingAmountSend.get(v).getAllocTypeId(), revisionRemainingAmountSend.get(v).getToUnitId(), 0);
                    for (Integer q = 0; q < cdaParkingCrAndDr.size(); q++) {
                        CdaParkingCrAndDr cddata = cdaParkingCrAndDr.get(q);
                        cddata.setIsFlag("1");
                        cddata.setIsRevision(1);
                        parkingCrAndDrRepository.save(cddata);
                    }


                    budgetAllocationRepository.save(budgetAllocation);
                    unitList.add(revisionRemainingAmountSend.get(v).getToUnitId());

                    String authgroupid = authRequest.getAuthGroupId();
                    List<MangeInboxOutbox> inboxList = mangeInboxOutBoxRepository.findByGroupId(authgroupid);

                    MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

                    mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
                    mangeInboxOutbox.setRemarks("Budget Receipt");
                    mangeInboxOutbox.setIsRebase("0");
                    mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutbox.setToUnit(revisionRemainingAmountSend.get(v).getToUnitId());

                    if (inboxList.size() > 0) {
                        mangeInboxOutbox.setType(inboxList.get(0).getType());
                    }

                    mangeInboxOutbox.setGroupId(authGroupId);
                    mangeInboxOutbox.setIsRebase("0");
                    mangeInboxOutbox.setFromUnit(hrData.getUnitId());
                    mangeInboxOutbox.setRoleId(hrData.getRoleId());
                    mangeInboxOutbox.setCreaterpId(hrData.getPid());
                    mangeInboxOutbox.setApproverpId("");
                    mangeInboxOutbox.setStatus("Fully Approved");
                    mangeInboxOutbox.setAllocationType(revisionRemainingAmountSend.get(v).getAllocTypeId());
                    mangeInboxOutbox.setIsFlag("1");
                    mangeInboxOutbox.setIsArchive("0");
                    mangeInboxOutbox.setIsApproved("0");
                    mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint(revisionRemainingAmountSend.get(v).getAmount() + ""));
                    mangeInboxOutbox.setIsBgcg("BR");
                    mangeInboxOutbox.setState("CR");
                    mangeInboxOutbox.setIsRevision(1);

                    mangeInboxOutBoxRepository.save(mangeInboxOutbox);

                }

            }

            CdaRevisionData cdaRevisionData = revisionRemainingAmountSend.get(v);
            cdaRevisionData.setIsComplete("1");
            budgetRevisionRepository.save(cdaRevisionData);
        }

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


        String authgroupid = authRequest.getAuthGroupId();
        List<MangeInboxOutbox> inboxList = mangeInboxOutBoxRepository.findByGroupId(authgroupid);


        HashMap<String, BudgetAllocation> totalUnit = new HashMap<String, BudgetAllocation>();

        for (Integer i = 0; i < budgetAllocationsListData.size(); i++) {
            if (!unitList.contains(budgetAllocationsListData.get(i))) {
                totalUnit.put(budgetAllocationsListData.get(i).getToUnit(), budgetAllocationsListData.get(i));
            }
        }

        for (Map.Entry<String, BudgetAllocation> entry : totalUnit.entrySet()) {
            String key = entry.getKey();
            BudgetAllocation tabData = entry.getValue();

            MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

            mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
            mangeInboxOutbox.setRemarks("Budget Receipt");
            mangeInboxOutbox.setIsRebase("0");
            mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox.setToUnit(key);

            if (inboxList.size() > 0) {
                mangeInboxOutbox.setType(inboxList.get(0).getType());
            }

            mangeInboxOutbox.setGroupId(authgroupid);
            mangeInboxOutbox.setIsRebase("0");
            mangeInboxOutbox.setFromUnit(hrData.getUnitId());
            mangeInboxOutbox.setRoleId(hrData.getRoleId());
            mangeInboxOutbox.setCreaterpId(hrData.getPid());
            mangeInboxOutbox.setApproverpId("");
            mangeInboxOutbox.setStatus("Fully Approved");
            mangeInboxOutbox.setAllocationType(tabData.getAllocationTypeId());
            mangeInboxOutbox.setIsFlag("1");
            mangeInboxOutbox.setIsArchive("0");
            mangeInboxOutbox.setIsApproved("0");
            mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint(tabData.getAllocationAmount() + ""));
            mangeInboxOutbox.setIsBgcg("BR");
            mangeInboxOutbox.setState("CR");
            mangeInboxOutbox.setIsRevision(1);
            authgroupid = tabData.getAuthGroupId();

            mangeInboxOutBoxRepository.save(mangeInboxOutbox);


            Authority authority11 = new Authority();
            authority11.setAuthorityId(HelperUtils.getAuthorityId());
            authority11.setAuthority(authRequest.getAuthority());
            authority11.setAuthDate(ConverterUtils.convertDateTotimeStamp(authRequest.getAuthDate()));
            authority11.setDocId(authRequest.getAuthDocId());
            authority11.setAuthUnit(authRequest.getAuthUnitId());
            authority11.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            authority11.setAuthGroupId(authgroupid);
            authority11.setRemarks(authRequest.getAuthUnitId());
            authority11.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            authorityRepository.save(authority11);

        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(authgroupid, hrData.getUnitId());
        if (mangeInboxOutboxList.size() > 0) {
            for (Integer m = 0; m < mangeInboxOutboxList.size(); m++) {
                try {
                    MangeInboxOutbox mangeInboxOutbox11 = mangeInboxOutboxList.get(m);

                    mangeInboxOutbox11.setStatus("Fully Approved");
                    mangeInboxOutbox11.setIsApproved("1");
                    mangeInboxOutBoxRepository.save(mangeInboxOutbox11);

                } catch (Exception e) {

                }
            }
        }

        defaultResponse.setMsg("DATA SAVE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<List<CgUnitResponse>> getAllCgUnitData() {

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
        List<CgUnit> unitDataList = cgUnitRepository.findAllByOrderByDescrAsc();
        if (unitDataList.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT lIST NOT FOUND ");
        }
        for (Integer n = 0; n < unitDataList.size(); n++) {
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

        return ResponseUtils.createSuccessResponse(cgUnitResponseList, new TypeReference<List<CgUnitResponse>>() {
        });
    }


    public List<CgUnit> removeDuplicates(List<CgUnit> dataList) {
        List<CgUnit> resultList = new ArrayList<CgUnit>();

        // Convert array list to Linked list
        LinkedList<CgUnit> linkedList = new LinkedList<CgUnit>();
        for (CgUnit obj : dataList) {
            linkedList.add(obj);
        }

        // Iterate through linked list and remove if values are duplicates
        for (int i = 0; i < linkedList.size(); i++) {
            for (int j = i + 1; j < linkedList.size(); j++) {
                if (linkedList.get(j).equals(linkedList.get(i))) {
                    linkedList.remove();
                }
            }
        }
        resultList.addAll(linkedList);
        return resultList;
    }


}


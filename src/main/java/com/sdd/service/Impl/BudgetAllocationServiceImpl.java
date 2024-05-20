package com.sdd.service.Impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.*;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
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
    HrDataRepository hrDataRepository;

    @Autowired
    HeaderUtils headerUtils;

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
    FileUploadRepository fileUploadRepository;

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


        for (int i = 0; i < allocationRepositoryData.size(); i++) {

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

        for (BudgetHead subHeadsDatum : subHeadsData) {
            BudgetHeadResponse budgetHeadResponse = new BudgetHeadResponse();
            BeanUtils.copyProperties(subHeadsDatum, budgetHeadResponse);
            String amountType = "0";
            double amount = 0;
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(hrDataCheck.getUnitId(), budgetHeadRequest.getFinYearId(), subHeadsDatum.getBudgetCodeId(), budgetHeadRequest.getAllocationType(), "Approved", "0", "0");
            for (BudgetAllocationDetails budgetAllocationDetails : budgetAllocationDetailsList) {
                amountType = budgetAllocationDetails.getAmountType();
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(amountType);
                amount = ConverterUtils.doubleSum(amount , Double.parseDouble(budgetAllocationDetails.getAllocationAmount()) * amountUnit.getAmount());
            }
            budgetHeadResponse.setTotalAmount(ConverterUtils.addDoubleValue(amount)+"");
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


        for (BudgetHead subHeadsDatum : subHeadsData) {
            removeDuplicate.put(subHeadsDatum.getMajorHead(), subHeadsDatum);
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


        for (CgUnit unit : unitDataList) {
            CgUnitResponse cgUnitResponse = new CgUnitResponse();

            if (unit.getUnit().equalsIgnoreCase(HelperUtils.HEADUNITID)) {

            } else {
                BeanUtils.copyProperties(unit, cgUnitResponse);
                CgStation cgStation = null;
                if (unit.getStationId() == null) {
                } else {
                    cgStation = cgStationRepository.findByStationId(unit.getStationId());
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


        for (CgUnit unit : unitDataList) {
            CgUnitResponse cgUnitResponse = new CgUnitResponse();

            if (unit.getUnit().equalsIgnoreCase(HelperUtils.HEADUNITID)) {

            } else {
                if (!unit.getDescr().equalsIgnoreCase("MOD")) {
                    BeanUtils.copyProperties(unit, cgUnitResponse);
                    CgStation cgStation = null;
                    if (unit.getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unit.getStationId());
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
            for (CgUnit unit : unitDataList) {


                if (unit.getPurposeCode().equalsIgnoreCase("0") || unit.getPurposeCode().equalsIgnoreCase("1")) {
                    CgUnitResponse cgUnitResponse = new CgUnitResponse();
                    BeanUtils.copyProperties(unit, cgUnitResponse);
                    CgStation cgStation = null;
                    if (unit.getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unit.getStationId());
                    }
                    cgUnitResponse.setCgStation(cgStation);
                    cgUnitResponseList.add(cgUnitResponse);
                }

            }

        } else if (cuurentRole.equalsIgnoreCase(HelperUtils.UNITADMIN)) {
            List<CgUnit> unitDataList = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());

            for (CgUnit unit : unitDataList) {
                CgUnitResponse cgUnitResponse = new CgUnitResponse();
                if (unit.getPurposeCode().equalsIgnoreCase("0")) {
                    BeanUtils.copyProperties(unit, cgUnitResponse);
                    CgStation cgStation = null;
                    if (unit.getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unit.getStationId());
                    }
                    cgUnitResponse.setCgStation(cgStation);
                    cgUnitResponseList.add(cgUnitResponse);
                } else if (unit.getPurposeCode().equalsIgnoreCase("1")) {
                    BeanUtils.copyProperties(unit, cgUnitResponse);
                    CgStation cgStation = null;
                    if (unit.getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unit.getStationId());
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
        for (BudgetAllocationDetails budgetAllocationSubReport : budgetAllocations) {

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

//        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
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


//        for (int k = 0; k < budgetAllocationSaveRequestList.getAuthRequests().size(); k++) {
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
//        for (int k = 0; k < budgetAllocationSaveRequestList.getAuthRequests().size(); k++) {
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
        for (int i = 0; i < budgetAllocations.size(); i++) {

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


        for (int i = 0; i < budgetAllocations.size(); i++) {

            BudgetAllocationDetails budgetAllocationSubReport = budgetAllocations.get(i);


            if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                continue;
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
            if (!cdaCrDrTransData.isEmpty()) {
                List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
                for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaCrDrTransData) {
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
                for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaCrDrTransData11) {
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

            if (!cdaParkingList.isEmpty()) {
                if (!cdaParkingList.isEmpty()) {
                    budgetAllocationReport.setIsCDAparking("1");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                } else {
                    budgetAllocationReport.setIsCDAparking("0");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                }

            } else {
                List<CdaParkingTrans> cdaParkingList11 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", budgetAllocationSubReport.getAllocTypeId(), budgetAllocationSubReport.getToUnit());

                if (!cdaParkingList11.isEmpty()) {
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
        if (allocationType.isEmpty()) {
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

        for (BudgetHead head : budgetHead) {

            List<BudgetAllocation> budgetAllocations11 = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(head.getBudgetCodeId(), hrData.getUnitId(), budgetFinancialYear.getSerialNo(), allocationType.get(0).getAllocTypeId(), "0", "0", "Approved");

            budgetAllocations.addAll(budgetAllocations11);
        }

        for (BudgetAllocation budgetAllocationSubReport : budgetAllocations) {

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
            for (CgUnit cgUnit : subUnitList) {
                List<BudgetAllocation> budgetAllocationListASD = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(budgetAllocationSubReport.getSubHead(), cgUnit.getUnit(), budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getAllocationTypeId(), "0", "0");
                List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsUpdateAndIsFlag(cgUnit.getUnit(), budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", "0");

                for (ContigentBill contigentBill : contigentBills) {
                    expAmount = ConverterUtils.doubleSum(expAmount , Double.parseDouble(contigentBill.getCbAmount()));
                }
                for (BudgetAllocation budgetAllocation : budgetAllocationListASD) {
                    totalAllocationAmount = ConverterUtils.doubleSum(totalAllocationAmount ,Double.parseDouble(budgetAllocation.getAllocationAmount()) * amountUnitRepository.findByAmountTypeId(budgetAllocation.getAmountType()).getAmount());
                }
            }

            totalAllocationAmount = ConverterUtils.doubleSum(expAmount , totalAllocationAmount / amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()).getAmount());
            if (totalAllocationAmount == 0) {
                budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());
            } else {
                budgetAllocationReport.setUnallocatedAmount((ConverterUtils.doubleMinus(Double.parseDouble(budgetAllocationSubReport.getUnallocatedAmount()) , totalAllocationAmount)) + "");
            }


            List<CdaParkingTrans> cdaParkingList11 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", allocationType.get(0).getAllocTypeId(), budgetAllocationSubReport.getToUnit());
            if (cdaParkingList11.isEmpty()) {
                cdaParkingList11 = cdaParkingList11;
            }

            List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
            for (CdaParkingTrans cdaParkingCrAndDr : cdaParkingList11) {
                CdaParkingCrAndDrResponse cgUnitResponse = new CdaParkingCrAndDrResponse();
                BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                cgUnitResponse.setAmountType(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType()));
                cgUnitResponse.setRemainingAmount(cdaParkingCrAndDr.getRemainingCdaAmount());//ConverterUtils.addDecimalPoint(

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


        for (BudgetAllocationDetails budgetAllocationSubReport : budgetAllocations) {

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


            if (hrData.getUnitId().equalsIgnoreCase(budgetAllocationSubReport.getToUnit())) {
                List<CdaRevisionData> cdaRevisionData = budgetRevisionRepository.findByAuthGroupIdAndToUnitId(groupId, budgetAllocationSubReport.getToUnit());
                budgetAllocationReport.setAllocationAmount(cdaRevisionData.get(0).getRemainingAmount());

            } else {
                List<CdaRevisionData> cdaRevisionData = budgetRevisionRepository.findByAuthGroupIdAndToUnitId(groupId, budgetAllocationSubReport.getToUnit());
                budgetAllocationReport.setAllocationAmount(cdaRevisionData.get(0).getRemainingAmount());
            }

            budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());

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
        List<BudgetAllocationSubResponse> sortvalue = new ArrayList<BudgetAllocationSubResponse>();
        if (budgetAllocationList.size() > 0) {

            for (int i = 0; i < budgetAllocationList.size(); i++) {
                Boolean val = false;
                for (int j = 0; j < sortvalue.size(); j++) {
                    if (budgetAllocationList.get(i).getToUnit().equals(sortvalue.get(j).getToUnit())) {
                        val = true;
                        break;
                    }

                }
                if (!val) {
                    sortvalue.add(budgetAllocationList.get(i));
                }
            }

        }

        budgetAllocationResponse.setBudgetResponseist(sortvalue);
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

        for (BudgetAllocationDetails budgetAllocationSubReport : budgetAllocations) {

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
    public ApiResponse<List<CgUnit>> getUnderUnits() {

        String currentUnitId = "000001";
        List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
        unitList.add(cgUnitRepository.findByUnit(currentUnitId));

        System.out.println(unitList.size());

        return ResponseUtils.createSuccessResponse(unitList, new TypeReference<List<CgUnit>>() {
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

        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

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

                for (CdaTransAllocationRequest cdaDatum : cdaData) {

                    if (cdaDatum.getCdaParkingId() == null || cdaDatum.getCdaParkingId().isEmpty()) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA ID CAN NOT BE BLANK");
                    }

                    CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(cdaDatum.getCdaParkingId(), "0");
                    if (cdaParkingTrans == null) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID CDA PARKING ID");
                    }

                    if (cdaDatum.getCdaAmount() == null || cdaDatum.getCdaAmount().isEmpty()) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CDA AMOUNT CAN NOT BE BLANK");
                    }
                }
            }

//            List<BudgetAllocationDetails> checkBudgetRevisionExist = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Pending", "0", "1");
            List<BudgetAllocationDetails> checkBudgetAllocationPending = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "Pending", "0", "0");
//            if (checkBudgetRevisionExist.size() > 0) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET REVISION FOR THIS UNIT CURRENTLY NOT APPROVED. PLEASE APPROVED PREVIOUS REVISION");
//            }

            if (!checkBudgetAllocationPending.isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALLOCATION FOR THIS UNIT CURRENTLY NOT APPROVED. PLEASE APPROVED PREVIOUS ALLOCATION");
            }
        }


        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String type = "";
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

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


        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
            BudgetAllocationSubRequest revisonData = budgetAllocationSaveRequestList.getBudgetRequest().get(i);

            if (budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {

                for (int m = 0; m < revisonData.getCdaParkingId().size(); m++) {

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


        if (budgetAllocations.isEmpty()) {
            budgetAllocations = budgetAllocationRepository.findByAuthGroupIdAndToUnit(groupId, hrData.getUnitId());
        }


        for (BudgetAllocation budgetAllocationSubReport : budgetAllocations) {

            if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                continue;
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
            if (!cdaCrDrTransData.isEmpty()) {
                List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
                for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaCrDrTransData) {
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
                for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaCrDrTransData11) {
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

            if (!cdaParkingList.isEmpty()) {
                if (!cdaParkingList.isEmpty()) {
                    budgetAllocationReport.setIsCDAparking("1");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                } else {
                    budgetAllocationReport.setIsCDAparking("0");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                }

            } else {
                List<CdaParkingTrans> cdaParkingList11 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", budgetAllocationSubReport.getAllocationTypeId(), budgetAllocationSubReport.getToUnit());

                if (!cdaParkingList11.isEmpty()) {
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

        for (BudgetAllocation budgetAllocationSubReport : budgetAllocations) {
            BudgetAllocationSubResponse budgetAllocationReport = new BudgetAllocationSubResponse();
            if (budgetAllocationSubReport.getIsTYpe().equalsIgnoreCase("AFTER REVISION")) {

            } else if (Double.parseDouble(budgetAllocationSubReport.getAllocationAmount()) == 0) {
                continue;
            }


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
            double totalAllocationAmountCopy = 0;
            double totalAllocationAmount = 0;
            double expAmount = 0;
            for (int c = 0; c < subUnitList.size(); c++) {
                List<BudgetAllocation> budgetAllocationListASD = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(budgetAllocationSubReport.getSubHead(), subUnitList.get(c).getUnit(), budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getAllocationTypeId(), "0", "0");
                List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsUpdateAndIsFlag(subUnitList.get(c).getUnit(), budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", "0");

                for (ContigentBill contigentBill : contigentBills) {
                    expAmount = ConverterUtils.doubleSum(expAmount , Double.parseDouble(contigentBill.getCbAmount()));
                }


                for (BudgetAllocation budgetAllocation : budgetAllocationListASD) {
                    totalAllocationAmount = ConverterUtils.doubleSum(totalAllocationAmount , Double.parseDouble(budgetAllocation.getAllocationAmount()) * amountUnitRepository.findByAmountTypeId(budgetAllocation.getAmountType()).getAmount());
                }
            }
            totalAllocationAmountCopy=totalAllocationAmount;
            budgetAllocationReport.setTotalAllocationAmount(totalAllocationAmountCopy);//added by deewan add new key
            totalAllocationAmount = ConverterUtils.doubleSum(expAmount , totalAllocationAmount/ amountUnitRepository.findByAmountTypeId(budgetAllocationSubReport.getAmountType()).getAmount());

            if (totalAllocationAmount == 0) {
                budgetAllocationReport.setUnallocatedAmount(budgetAllocationSubReport.getUnallocatedAmount());
            } else {
                budgetAllocationReport.setUnallocatedAmount((ConverterUtils.doubleMinus(Double.parseDouble(budgetAllocationSubReport.getUnallocatedAmount()) , totalAllocationAmount) )+ "");
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
            if (!cdaCrDrTransData.isEmpty()) {
                List<CdaParkingCrAndDrResponse> data = new ArrayList<>();
                for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaCrDrTransData) {
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
                for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaCrDrTransData11) {
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

            if (!cdaParkingList.isEmpty()) {
                if (!cdaParkingList.isEmpty()) {
                    budgetAllocationReport.setIsCDAparking("1");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                } else {
                    budgetAllocationReport.setIsCDAparking("0");
                    budgetAllocationReport.setCdaList(cdaParkingList);
                }

            } else {
                List<CdaParkingTrans> cdaParkingList11 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationSubReport.getFinYear(), budgetAllocationSubReport.getSubHead(), "0", budgetAllocationSubReport.getAllocationTypeId(), budgetAllocationSubReport.getToUnit());

                if (!cdaParkingList11.isEmpty()) {
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

        for (CgUnit item : unit) {

            if (hrData.getUnitId().equalsIgnoreCase(item.getUnit())) {

                List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(item.getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), budgetRivRequest.getAllocTypeId(), "Approved", "0", "0");

                for (BudgetAllocation budgetAllocationsDetali : budgetAllocationsDetalis) {
                    BudgetReviResp res = new BudgetReviResp();
                    res.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetali.getToUnit()));

                    AmountUnit amountType = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetali.getAmountType());
                    res.setAmountType(amountType);


                    double expendure = 0;
                    double avilabaleAmount = 0;

                    String currentUnitId = item.getUnit();
                    List<CgUnit> unitList1 = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
                    List<CgUnit> unitList = unitList1.stream().filter(e -> !e.getUnit().equalsIgnoreCase(currentUnitId)).collect(Collectors.toList());
                    unitList.add(cgUnitRepository.findByUnit(item.getUnit()));

                    List<CgUnit> validUmit = removeDuplicates(unitList);
                    unitList.clear();
                    unitList.addAll(validUmit);


                    for (CgUnit value : unitList) {
                        double avilabaleTotalAmount = 0;

                        List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(value.getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                        for (ContigentBill contigentBill : contigentBills) {
                            expendure = ConverterUtils.doubleSum(expendure , Double.parseDouble(contigentBill.getCbAmount()));
                        }

                        List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationsDetali.getFinYear(), budgetAllocationsDetali.getSubHead(), "0", budgetAllocationsDetali.getAllocationTypeId(), value.getUnit());
                        if (!cdaParkingList.isEmpty()) {

                            for (CdaParkingTrans cdaParkingCrAndDr : cdaParkingList) {
                                AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());

                                avilabaleTotalAmount = ConverterUtils.doubleSum(avilabaleTotalAmount , (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount()));
                            }
                            avilabaleAmount = ConverterUtils.doubleSum(avilabaleAmount , avilabaleTotalAmount / amountType.getAmount());
                        } else {
                            double totalAllocation = 0;
//                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFromUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(currentUnitId, unit.get(i).getUnit(), budgetAllocationsDetalis.get(m).getFinYear(), budgetAllocationsDetalis.get(m).getSubHead(), budgetAllocationsDetalis.get(m).getAllocationTypeId(), "0", "0", "Approved");
                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(value.getUnit(), budgetAllocationsDetali.getFinYear(), budgetAllocationsDetali.getSubHead(), budgetAllocationsDetali.getAllocationTypeId(), "Approved", "0", "0");

                            for (BudgetAllocation budgetAllocation : reData1) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(budgetAllocation.getAmountType());
                                totalAllocation = ConverterUtils.doubleSum(totalAllocation , (Double.parseDouble(budgetAllocation.getAllocationAmount()) * subAmountType.getAmount()));
                            }
                            avilabaleAmount = ConverterUtils.doubleSum(avilabaleAmount , ((totalAllocation) / amountType.getAmount()));
                        }
                    }

                    res.setRemainingAmount((ConverterUtils.doubleMinus(avilabaleAmount , expendure / amountType.getAmount()) + ""));
                    res.setExpenditureAmount(expendure + "");
                    res.setStatus(budgetAllocationsDetali.getStatus());
                    res.setRevisedAmount(budgetAllocationsDetali.getRevisedAmount());
                    res.setFlag(budgetAllocationsDetali.getIsFlag());


                    res.setAllocationAmount(ConverterUtils.doubleSum(avilabaleAmount , expendure / amountType.getAmount()) + "");


                    List<CdaFilterData> data = new ArrayList<>();
                    double totalRemening = 0;

                    List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationsDetali.getAllocationId(), "0");
                    if (!cdaParkingTrans.isEmpty()) {
                        for (CdaParkingTrans cdaParkingCrAndDr : cdaParkingTrans) {
                            CdaFilterData cgUnitResponse = new CdaFilterData();
                            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                            AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());
                            cgUnitResponse.setAmountType(cdaAmountUnit);

                            totalRemening = ConverterUtils.doubleSum(totalRemening , (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount()));
                            data.add(cgUnitResponse);
                        }

                    } else {
                        double totalAllocation = 0;
                        BudgetAllocation budgetAllocation = budgetAllocationsDetali;
                        List<CgUnit> unitSubList = cgUnitRepository.findBySubUnitOrderByDescrAsc(item.getUnit());
                        for (int w = 0; w < unitSubList.size(); w++) {

                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(item.getUnit(), budgetAllocationsDetali.getFinYear(), budgetAllocationsDetali.getSubHead(), budgetAllocationsDetali.getAllocationTypeId(), "Approved", "0", "0");

                            for (BudgetAllocation allocation : reData1) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(allocation.getAmountType());
                                totalAllocation = ConverterUtils.doubleSum(totalAllocation , (Double.parseDouble(allocation.getAllocationAmount()) * subAmountType.getAmount()));
                            }
                        }
                    }
                    res.setCdaTransData(data);
                    budgetRevision.add(res);
                }


            } else {

                List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(item.getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), budgetRivRequest.getAllocTypeId(), "Approved", "0", "0");

                for (BudgetAllocation budgetAllocationsDetali : budgetAllocationsDetalis) {
                    BudgetReviResp res = new BudgetReviResp();
                    res.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetali.getToUnit()));

                    AmountUnit amountType = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetali.getAmountType());
                    res.setAmountType(amountType);

                    String currentUnitId = item.getUnit();
                    List<CgUnit> unitList1 = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
                    List<CgUnit> unitList = unitList1.stream().filter(e -> !e.getUnit().equalsIgnoreCase(currentUnitId)).collect(Collectors.toList());
                    unitList.add(cgUnitRepository.findByUnit(item.getUnit()));

                    List<CgUnit> validUmit = removeDuplicates(unitList);
                    unitList.clear();
                    unitList.addAll(validUmit);

                    double expendure = 0;
                    double avilabaleAmount = 0;
                    for (CgUnit value : unitList) {
                        double avilabaleTotalAmount = 0;
                        List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(value.getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                        for (ContigentBill contigentBill : contigentBills) {
                            expendure = ConverterUtils.doubleSum(expendure , Double.parseDouble(contigentBill.getCbAmount()));
                        }

                        List<CdaParkingTrans> cdaParkingList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAllocTypeIdAndUnitId(budgetAllocationsDetali.getFinYear(), budgetAllocationsDetali.getSubHead(), "0", budgetAllocationsDetali.getAllocationTypeId(), value.getUnit());
                        if (!cdaParkingList.isEmpty()) {

                            for (CdaParkingTrans cdaParkingCrAndDr : cdaParkingList) {
                                AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());

                                avilabaleTotalAmount = ConverterUtils.doubleSum(avilabaleTotalAmount , (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount()));

                            }
                            avilabaleAmount = ConverterUtils.doubleSum(avilabaleAmount , avilabaleTotalAmount / amountType.getAmount());
                        } else {
                            double totalAllocation = 0;
                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(value.getUnit(), budgetAllocationsDetali.getFinYear(), budgetAllocationsDetali.getSubHead(), budgetAllocationsDetali.getAllocationTypeId(), "Approved", "0", "0");

                            for (BudgetAllocation budgetAllocation : reData1) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(budgetAllocation.getAmountType());
                                totalAllocation = ConverterUtils.doubleSum(totalAllocation , (Double.parseDouble(budgetAllocation.getAllocationAmount()) * subAmountType.getAmount()));
                            }
//                        avilabaleAmount = avilabaleAmount + (Double.parseDouble(budgetAllocationsDetalis.get(m).getAllocationAmount()) - ((totalAllocation - expendure) / amountType.getAmount()));
                            avilabaleAmount = ConverterUtils.doubleSum(avilabaleAmount , ((totalAllocation) / amountType.getAmount()));
                        }
                    }

                    res.setRemainingAmount( ConverterUtils.doubleMinus(avilabaleAmount , expendure / amountType.getAmount()) + "");                    res.setExpenditureAmount(expendure + "");
                    res.setStatus(budgetAllocationsDetali.getStatus());
                    res.setRevisedAmount(budgetAllocationsDetali.getRevisedAmount());
                    res.setFlag(budgetAllocationsDetali.getIsFlag());
                    res.setAllocationAmount(((ConverterUtils.doubleSum(avilabaleAmount , expendure / amountType.getAmount()))) + "");

                    List<CdaFilterData> data = new ArrayList<>();

                    double totalRemening = 0;

                    List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationsDetali.getAllocationId(), "0");
                    if (!cdaParkingTrans.isEmpty()) {
                        for (CdaParkingTrans cdaParkingCrAndDr : cdaParkingTrans) {
                            CdaFilterData cgUnitResponse = new CdaFilterData();
                            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                            AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());
                            cgUnitResponse.setAmountType(cdaAmountUnit);

                            totalRemening = ConverterUtils.doubleSum(totalRemening , (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount()));
                            data.add(cgUnitResponse);
                        }

                    } else {
                        double totalAllocation = 0;
                        BudgetAllocation budgetAllocation = budgetAllocationsDetali;
                        List<CgUnit> unitSubList = cgUnitRepository.findBySubUnitOrderByDescrAsc(item.getUnit());
                        for (int w = 0; w < unitSubList.size(); w++) {
                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(item.getUnit(), budgetAllocationsDetali.getFinYear(), budgetAllocationsDetali.getSubHead(), budgetAllocationsDetali.getAllocationTypeId(), "Approved", "0", "0");

                            for (BudgetAllocation allocation : reData1) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(allocation.getAmountType());
                                totalAllocation = ConverterUtils.doubleSum(totalAllocation , (Double.parseDouble(allocation.getAllocationAmount()) * subAmountType.getAmount()));
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

        for (CgUnit element : unit) {

            if (element.getUnit().equalsIgnoreCase(HelperUtils.HEADUNITID)) {
                List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(element.getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), budgetRivRequest.getAllocTypeId(), "Approved", "0", "0");

                for (BudgetAllocation budgetAllocationsDetali : budgetAllocationsDetalis) {
                    BudgetReviResp res = new BudgetReviResp();
                    res.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetali.getToUnit()));

                    AmountUnit amountType = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetali.getAmountType());
                    res.setAmountType(amountType);

                    double expendure = 0;
                    double selfExpendure = 0;
                    double avilabaleAmount = 0;
                    String currentUnitId = element.getUnit();

                    List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(currentUnitId, budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                    for (ContigentBill contigentBill : contigentBills) {
                        expendure = ConverterUtils.doubleSum(expendure , Double.parseDouble(contigentBill.getCbAmount()));
                    }


                    List<ContigentBill> selfContigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(currentUnitId, budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                    for (int k = 0; k < selfContigentBills.size(); k++) {
                        selfExpendure = ConverterUtils.doubleSum(selfExpendure , Double.parseDouble(contigentBills.get(k).getCbAmount()));
                    }


                    double totalAllocationT = 0;
                    List<BudgetAllocation> reData2 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(currentUnitId, budgetAllocationsDetali.getFinYear(), budgetAllocationsDetali.getSubHead(), budgetAllocationsDetali.getAllocationTypeId(), "Approved", "0", "0");

                    for (BudgetAllocation allocation : reData2) {
                        AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(allocation.getAmountType());
                        totalAllocationT = ConverterUtils.doubleSum(totalAllocationT , (Double.parseDouble(allocation.getAllocationAmount()) * subAmountType.getAmount()));
                    }
                    avilabaleAmount = ConverterUtils.doubleSum(avilabaleAmount , ((totalAllocationT) / amountType.getAmount()));


                    res.setRemainingAmount(ConverterUtils.doubleMinus(avilabaleAmount , expendure / amountType.getAmount()) + "");
                    res.setExpenditureAmount(expendure + "");
                    res.setSelfExpenditureAmount(selfExpendure + "");
                    res.setStatus(budgetAllocationsDetali.getStatus());
                    res.setRevisedAmount(budgetAllocationsDetali.getRevisedAmount());
                    res.setFlag(budgetAllocationsDetali.getIsFlag());

                    res.setAllocationAmount(avilabaleAmount + "");

                    List<CdaFilterData> data = new ArrayList<>();
                    double totalRemening = 0;

//                    List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationsDetali.getAllocationId(), "0");
                    List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), element.getUnit(), budgetRivRequest.getAllocTypeId(), "0");

                    if (!cdaParkingTrans.isEmpty()) {
                        for (CdaParkingTrans cdaParkingCrAndDr : cdaParkingTrans) {
                            CdaFilterData cgUnitResponse = new CdaFilterData();
                            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                            AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());
                            cgUnitResponse.setAmountType(cdaAmountUnit);
                            cgUnitResponse.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(cdaParkingCrAndDr.getRemainingCdaAmount()));
                            totalRemening = ConverterUtils.doubleSum(totalRemening , (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount()));
                            data.add(cgUnitResponse);
                        }

                    } else {
                        double totalAllocation = 0;
                        List<CgUnit> unitSubList = cgUnitRepository.findBySubUnitOrderByDescrAsc(element.getUnit());
                        for (int w = 0; w < unitSubList.size(); w++) {

                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(element.getUnit(), budgetAllocationsDetali.getFinYear(), budgetAllocationsDetali.getSubHead(), budgetAllocationsDetali.getAllocationTypeId(), "Approved", "0", "0");

                            for (BudgetAllocation allocation : reData1) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(allocation.getAmountType());
                                totalAllocation = ConverterUtils.doubleSum(totalAllocation , (Double.parseDouble(allocation.getAllocationAmount()) * subAmountType.getAmount()));
                            }
                        }
                    }
                    res.setCdaTransData(data);
                    budgetRevision.add(res);
                }

            } else {

                List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike("%" + element.getUnit() + "%");
                unitList.add(cgUnitRepository.findByUnit(element.getUnit()));
                List<BudgetAllocation> budgetAllocationsDetalis = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(element.getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), budgetRivRequest.getAllocTypeId(), "Approved", "0", "0");

                for (BudgetAllocation budgetAllocationsDetali : budgetAllocationsDetalis) {
                    BudgetReviResp res = new BudgetReviResp();
                    res.setUnit(cgUnitRepository.findByUnit(budgetAllocationsDetali.getToUnit()));

                    AmountUnit amountType = amountUnitRepository.findByAmountTypeId(budgetAllocationsDetali.getAmountType());
                    res.setAmountType(amountType);

                    double expendure = 0;
                    double selfExpendure = 0;
                    double avilabaleAmount = 0;
                    String currentUnitId = element.getUnit();

                    for (CgUnit item : unitList) {
                        List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(item.getUnit(), budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                        for (ContigentBill contigentBill : contigentBills) {
                            expendure = ConverterUtils.doubleSum(expendure , Double.parseDouble(contigentBill.getCbAmount()));
                        }
                    }


                    List<ContigentBill> selfContigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(currentUnitId, budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), "0", "0");
                    for (ContigentBill selfContigentBill : selfContigentBills) {
                        selfExpendure = ConverterUtils.doubleSum(selfExpendure , Double.parseDouble(selfContigentBill.getCbAmount()));
                    }


                    double totalAllocationT = 0;
                    List<BudgetAllocation> reData2 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(currentUnitId, budgetAllocationsDetali.getFinYear(), budgetAllocationsDetali.getSubHead(), budgetAllocationsDetali.getAllocationTypeId(), "Approved", "0", "0");

                    for (BudgetAllocation value : reData2) {
                        AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(value.getAmountType());
                        totalAllocationT = ConverterUtils.doubleSum(totalAllocationT , (Double.parseDouble(value.getAllocationAmount()) * subAmountType.getAmount()));
                    }
                    avilabaleAmount = ConverterUtils.doubleSum(avilabaleAmount , ((totalAllocationT) / amountType.getAmount()));


                    res.setRemainingAmount(ConverterUtils.doubleMinus(avilabaleAmount , expendure / amountType.getAmount()) + "");
                    res.setExpenditureAmount(expendure + "");
                    res.setSelfExpenditureAmount(selfExpendure + "");
                    res.setStatus(budgetAllocationsDetali.getStatus());
                    res.setRevisedAmount(budgetAllocationsDetali.getRevisedAmount());
                    res.setFlag(budgetAllocationsDetali.getIsFlag());

                    res.setAllocationAmount(avilabaleAmount + "");

                    List<CdaFilterData> data = new ArrayList<>();
                    double totalRemening = 0;


//                    List<CdaParkingTrans> cdaParkingTrans1 = cdaParkingTransRepository.findByTransactionIdAndIsFlag(budgetAllocationsDetali.getAllocationId(), "0");
                    List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetRivRequest.getBudgetFinancialYearId(), budgetRivRequest.getSubHead(), element.getUnit(), budgetRivRequest.getAllocTypeId(), "0");
                    if (!cdaParkingTrans.isEmpty()) {
                        for (CdaParkingTrans cdaParkingCrAndDr : cdaParkingTrans) {
                            CdaFilterData cgUnitResponse = new CdaFilterData();
                            BeanUtils.copyProperties(cdaParkingCrAndDr, cgUnitResponse);
                            cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingCrAndDr.getGinNo()));
                            AmountUnit cdaAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDr.getAmountType());
                            cgUnitResponse.setAmountType(cdaAmountUnit);
                            cgUnitResponse.setRemainingCdaAmount(ConverterUtils.addDecimalPoint(cdaParkingCrAndDr.getRemainingCdaAmount()));

                            totalRemening = ConverterUtils.doubleSum(totalRemening , (Double.parseDouble(cdaParkingCrAndDr.getRemainingCdaAmount()) * cdaAmountUnit.getAmount()));
                            data.add(cgUnitResponse);
                        }

                    } else {
                        double totalAllocation = 0;
                        List<CgUnit> unitSubList = cgUnitRepository.findBySubUnitOrderByDescrAsc(element.getUnit());
                        for (int w = 0; w < unitSubList.size(); w++) {

                            List<BudgetAllocation> reData1 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(element.getUnit(), budgetAllocationsDetali.getFinYear(), budgetAllocationsDetali.getSubHead(), budgetAllocationsDetali.getAllocationTypeId(), "Approved", "0", "0");

                            for (BudgetAllocation allocation : reData1) {
                                AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(allocation.getAmountType());
                                totalAllocation = ConverterUtils.doubleSum(totalAllocation , (Double.parseDouble(allocation.getAllocationAmount()) * subAmountType.getAmount()));
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
        if (allocationDetails.isEmpty()) {
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

        for (BudgetAllocationDetails allocationData : allocationDetails) {

            status = budgetApproveRequest.getStatus();
//            allocationData.setReturnRemarks(budgetApproveRequest.getRemarks());
//            allocationData.setStatus(budgetApproveRequest.getStatus());
//            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//            budgetAllocationDetailsRepository.save(allocationData);


            BudgetAllocation budgetAllocation = new BudgetAllocation();
            budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
            budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
            budgetAllocation.setIsFlag("0");
            budgetAllocation.setIsTYpe(allocationData.getIsTYpe());
            budgetAllocation.setIsBudgetRevision("0");
            budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocation.setRefTransId(allocationData.getRefTransactionId());
            budgetAllocation.setFinYear(allocationData.getFinYear());
            budgetAllocation.setToUnit(allocationData.getToUnit());
            budgetAllocation.setFromUnit(hrData.getUnitId());
            budgetAllocation.setSubHead(allocationData.getSubHead());
            budgetAllocation.setAllocationTypeId(allocationData.getAllocTypeId());
            budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(allocationData.getAllocationAmount()));
            budgetAllocation.setUnallocatedAmount("0");
            budgetAllocation.setRevisedAmount("0");
            budgetAllocation.setUserId(allocationData.getUserId());
            budgetAllocation.setStatus("Pending");
            budgetAllocation.setAmountType(allocationData.getAmountType());
            budgetAllocation.setAuthGroupId(allocationData.getAuthGroupId());

            budgetAllocationRepository.save(budgetAllocation);

        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
        if (!mangeInboxOutboxList.isEmpty()) {
            for (MangeInboxOutbox inboxOutbox : mangeInboxOutboxList) {

                try {
                    MangeInboxOutbox mangeInboxOutbox = inboxOutbox;

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


        for (int i = 0; i < budgetApproveRequest.getCdaParkingId().size(); i++) {

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

        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDeleteAndIsBudgetRevision(budgetApproveRequest.getAuthGroupId(), "0", "0");
        if (allocationDetails.isEmpty()) {
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
        for (BudgetAllocationDetails allocationData : allocationDetails) {

            status = budgetApproveRequest.getStatus();
            allocationData.setReturnRemarks(budgetApproveRequest.getRemarks());
            allocationData.setStatus(budgetApproveRequest.getStatus());
            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocationData);

            BudgetFinancialYear finYear = budgetFinancialYearRepository.findBySerialNo(allocationData.getFinYear());

            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(allocationData.getTransactionId(), "0", 0);
            for (CdaParkingCrAndDr cdaCrDrTransDatum : cdaCrDrTransData) {

                List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndGinNoAndIsFlagAndAndAllocTypeIdAndUnitId(finYear.getSerialNo(), allocationData.getSubHead(), cdaCrDrTransDatum.getGinNo(), "0", allocationData.getAllocTypeId(), hrData.getUnitId());

                for (CdaParkingTrans cdaParkingTrans : cdaParkingTransList) {

                    AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                    double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                    AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(allocationData.getAmountType());
                    double parkingAmount = Double.parseDouble(cdaCrDrTransDatum.getAmount()) * amountUnit.getAmount();

                    double bakiPesa = ConverterUtils.doubleSum(remainingCdaParkingAmount , parkingAmount) / cadAmountUnit.getAmount();
                    cdaParkingTrans.setRemainingCdaAmount(bakiPesa + "");
                    cdaParkingTransRepository.save(cdaParkingTrans);

                }
            }
        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
        if (!mangeInboxOutboxList.isEmpty()) {
            for (MangeInboxOutbox inboxOutbox : mangeInboxOutboxList) {

                try {
                    MangeInboxOutbox mangeInboxOutbox = inboxOutbox;

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
        if (allocationDetails.isEmpty()) {
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

        for (BudgetAllocationDetails budgetAllocationDetails : allocationDetails) {

            List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(budgetAllocationDetails.getToUnit(), budgetAllocationDetails.getFinYear(), budgetAllocationDetails.getSubHead(), budgetAllocationDetails.getAllocTypeId(), "Approved", "0", "0");
            if (data.isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DATA.PLEASE CHECK YOUR ADMINISTRATOR.01");
            }
        }

        String status = "";
//        for (BudgetAllocationDetails allocationDetail : allocationDetails) {
//            if (!(budgetApproveRequest.getStatus().equalsIgnoreCase("Approved"))) {
//
//                status = budgetApproveRequest.getStatus();
//                BudgetAllocationDetails allocationData = allocationDetail;
//                allocationData.setStatus(budgetApproveRequest.getStatus());
//                allocationData.setIsDelete("1");
//                allocationData.setIsBudgetRevision("1");
//                allocationData.setStatus(budgetApproveRequest.getStatus());
//                allocationData.setReturnRemarks(budgetApproveRequest.getRemarks());
//                allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//                budgetAllocationDetailsRepository.save(allocationData);
//
//            }
//        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(budgetApproveRequest.getAuthGroupId(), hrData.getUnitId());
        if (!mangeInboxOutboxList.isEmpty()) {
            for (MangeInboxOutbox inboxOutbox : mangeInboxOutboxList) {

                try {
                    MangeInboxOutbox mangeInboxOutbox = inboxOutbox;

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
        if (!allocationType.isEmpty()) {
            allocationTypeData = allocationType.get(0);
        }

        List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetHeadId.getBudgetFinancialYearId(), budgetHeadId.getBudgetHeadId(), budgetHeadId.getUnitId(), allocationTypeData.getAllocTypeId(), "0");

        AmountUnit amountUnit = null;
        if (cdaParkingTrans.isEmpty()) {
            response.setFundAvailable("0");
        } else {
            double balanceAmount = 0;
            for (CdaParkingTrans cdaParkingTran : cdaParkingTrans) {
                amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTran.getAmountType());
                balanceAmount = ConverterUtils.doubleSum(balanceAmount , Double.parseDouble(cdaParkingTran.getRemainingCdaAmount()));
            }
            response.setFundAvailable(balanceAmount + "");//remove round off   by deewan ConverterUtils.addDecimalPoint(
            response.setAmountUnit(amountUnit);
        }

        response.setUnitName(cgUnitData.getCgUnitShort());


        double totalAllocationT = 0;
        List<BudgetAllocation> reData2 = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), budgetHeadId.getBudgetFinancialYearId(), budgetHeadId.getBudgetHeadId(), allocationTypeData.getAllocTypeId(), "Approved", "0", "0");

        for (BudgetAllocation budgetAllocation : reData2) {
            AmountUnit subAmountType = amountUnitRepository.findByAmountTypeId(budgetAllocation.getAmountType());
            totalAllocationT = ConverterUtils.doubleSum(totalAllocationT , (Double.parseDouble(budgetAllocation.getAllocationAmount()) * subAmountType.getAmount()));
        }

        BudgetFinancialYear budgetFinancialYear;
        CurrntStateType stateList1 = currentStateRepository.findByTypeAndIsFlag("FINYEAR", "1");
        if (stateList1 == null) {
            budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo("01");

        } else {
            budgetFinancialYear =
                    budgetFinancialYearRepository.findBySerialNo(stateList1.getStateId());

        }


        response.setCbAllocationAMount(totalAllocationT + "");
        List<ContigentBill> cbExpendure = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndFinYear(budgetHeadId.getUnitId(), budgetHeadId.getBudgetHeadId(), "0", "0",budgetFinancialYear.getSerialNo());
        if (cbExpendure.isEmpty()) {
            response.setExpenditure("0.0000");
        } else {
            double expenditure = 0;
            for (ContigentBill contigentBill : cbExpendure) {
                expenditure = ConverterUtils.doubleSum(expenditure , Double.parseDouble(contigentBill.getCbAmount()));
            }
            response.setExpenditure(expenditure + "");//remove round off 29/02/24 by deevan
        }

        List<CdaFilterData> data = new ArrayList<>();
        for (CdaParkingTrans cdaParkingCrAndDr : cdaParkingTrans) {
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
        if (!allocationType.isEmpty()) {
            allocationTypeData = allocationType.get(0);
        }

        List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetHeadId.getBudgetFinancialYearId(), budgetHeadId.getBudgetHeadId(), budgetHeadId.getUnitId(), allocationTypeData.getAllocTypeId(), "0");


        AmountUnit amountUnit = null;
        if (cdaParkingTrans.isEmpty()) {
            response.setFundAvailable("0");
        } else {
            double balanceAmount = 0;
//            double allocationAmount = 0;
            for (CdaParkingTrans cdaParkingTran : cdaParkingTrans) {

                amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTran.getAmountType());
                balanceAmount = ConverterUtils.doubleSum(balanceAmount , Double.parseDouble(cdaParkingTran.getRemainingCdaAmount()));
//                allocationAmount = allocationAmount + Double.parseDouble(cdaParkingTrans.get(i).getTotalParkingAmount());
            }
            response.setFundAvailable(balanceAmount + "");
//            response.setFundallocated(ConverterUtils.addDecimalPoint(allocationAmount + ""));
            response.setAmountUnit(amountUnit);
        }


        response.setUnitName(cgUnitData.getCgUnitShort());


        List<ContigentBill> cbExpendure = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(budgetHeadId.getUnitId(), budgetHeadId.getBudgetHeadId(), "0", "0");
        if (cbExpendure.isEmpty()) {
            response.setExpenditure("0.0000");
        } else {
            double expenditure = 0;
            for (ContigentBill contigentBill : cbExpendure) {
                expenditure = ConverterUtils.doubleSum(expenditure , Double.parseDouble(contigentBill.getCbAmount()));
            }
            response.setExpenditure(expenditure + "");
        }

        List<CdaFilterData> data = new ArrayList<>();
        for (CdaParkingTrans cdaParkingCrAndDr : cdaParkingTrans) {
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
        if (!allocationType.isEmpty()) {
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
        if (cdaParkingTrans.isEmpty()) {
            response.setFundAvailable("0");
        } else {
            double allocationAmount = 0;
            response.setFundAvailable("0");
            for (CdaParkingTrans cdaParkingTran : cdaParkingTrans) {
                allocationAmount = ConverterUtils.doubleSum(allocationAmount , Double.parseDouble(cdaParkingTran.getRemainingCdaAmount()));
            }
            response.setFundAvailable(allocationAmount + "");
        }

        response.setUnitName(cgUnitData.getCgUnitShort());


        List<CdaParkingTrans> cdaParkingTransD = cdaParkingTransRepository.findByFinYearIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetFinancialYear.getSerialNo(), hrData.getUnitId(), allocationTypeData.getAllocTypeId(), "0");

//        List<BudgetAllocationDetails> budgetAllocations = budgetAllocationDetailsRepository.findByToUnitAndIsDeleteAndIsBudgetRevision(hrData.getUnitId(), "0", "0");
        if (cdaParkingTransD.isEmpty()) {

            response.setPreviousAllocation("0.0000");
            response.setUnallocatedAmount("0.0000");

        } else {
            double previousAmount = 0;
            response.setFundAvailable("0.0000");
            for (CdaParkingTrans parkingTrans : cdaParkingTransD) {
                previousAmount = ConverterUtils.doubleSum(previousAmount , Double.parseDouble(parkingTrans.getRemainingCdaAmount()));
            }
            response.setFundAvailable(previousAmount + "");
            response.setUnallocatedAmount("0.0000");
        }


        List<CdaFilterData> data = new ArrayList<>();
        for (CdaParkingTrans cdaParkingCrAndDr : cdaParkingTrans) {
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
        if (!allocationType.isEmpty()) {
            allocationTypeData = allocationType.get(0);
        }
        List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(getAvilableFundRequest.getFinYearId(), getAvilableFundRequest.getSubHeadId(), hrData.getUnitId(), allocationTypeData.getAllocTypeId(), "0");
        response.setUnitName(checkUnitData.getCgUnitShort());

        if (cdaParkingTrans.isEmpty()) {
            response.setFundAvailable("0");
        } else {
            double allocationAmount = 0;

            for (CdaParkingTrans cdaParkingTran : cdaParkingTrans) {

                response.setFundAvailable("0.0000");
                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTran.getAmountType());
                allocationAmount = ConverterUtils.doubleSum(allocationAmount , (Double.parseDouble(cdaParkingTran.getRemainingCdaAmount()) * amountUnit.getAmount()));
                response.setFundAvailable(allocationAmount + "");

            }
        }


        List<CdaFilterData> data = new ArrayList<>();
        for (int m = 0; m < cdaParkingTrans.size(); m++) {
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

        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatusIn(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId(), "0", "0", dataIscgBg);
            if (!budgetAllocationDetailsList.isEmpty()) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALREADY ALLOCATED " + cgUnit.getDescr() + " . CAN NOT ASSIGN AGAIN");
            }
        }

        boolean isAmountExits = true;
        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            double amount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount());
            if (amount > 0) {
                isAmountExits = false;
            }
        }
        if (isAmountExits) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ATLEAST ADD ONE NON ZERO ALLOCATION ENTRY");
        }

        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


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

            if (cdaParkingList.isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PLEASE ADD CDA IN BUDGET RECEIPT.THAN TRY AGAIN");
            }


            for (int m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

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
            for (int m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");


                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
                remainingCdaParkingAmount = ConverterUtils.doubleSum(ConverterUtils.doubleSum(remainingCdaParkingAmount , remainingCdaParkingAmount) , (Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount()));
                allocationAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount()) * amountUnit.getAmount();
//                long allocationAmt= Math.round(allocationAmount);
//                allocationAmount=(double)allocationAmt;
                  allocationAmount= (ConverterUtils.addDoubleValue(allocationAmount));
               System.out.print("alloc"+allocationAmount);


            }

            if (allocationAmount > remainingCdaParkingAmount) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS GREATER THAN CDA REMAINING AMOUNT FOR " + cgUnit.getDescr());
            }

        }


        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            BudgetAllocationSubRequest budgetAllocationSubRequest = budgetAllocationSaveRequest.getBudgetRequest().get(i);
            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndFinYear(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0", budgetAllocationSubRequest.getBudgetFinanciaYearId());

            double totalCbAmount = 0;
            for (ContigentBill contigentBill : contigentBills) {
                totalCbAmount = ConverterUtils.doubleSum(totalCbAmount , Double.parseDouble(contigentBill.getCbAmount()));
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSubRequest.getAmountTypeId());
            double allocationAmount = Double.parseDouble(budgetAllocationSubRequest.getAmount()) * amountUnit.getAmount();

            if (totalCbAmount > allocationAmount) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSubRequest.getToUnitId());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS SMALLER THAN CDA UNIT EXPENDITURE AMOUNT FOR " + cgUnit.getDescr());
            }
        }


        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


            for (int m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

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


        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String type = "";
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

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


            for (int m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
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


        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


            for (int m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                   parkingAmount= (ConverterUtils.addDoubleValue(parkingAmount));

                double bakiPesa = ConverterUtils.doubleMinus(remainingCdaParkingAmount , parkingAmount) / cadAmountUnit.getAmount();

                bakiPesa= (ConverterUtils.addDoubleValue(bakiPesa));
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

        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatusIn(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "0", "0", dataIscgBg);
            if (!budgetAllocationDetailsList.isEmpty()) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALREADY ALLOCATED " + cgUnit.getDescr() + " . CAN NOT ASSIGN AGAIN");
            }
        }


        boolean isAmountExits = true;
        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            double amount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount());
            if (amount > 0) {
                isAmountExits = false;
            }
        }
        if (isAmountExits) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ATLEAST ADD ONE NON ZERO ALLOCATION ENTRY");
        }


        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


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


            for (int m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

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
            for (int m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
                remainingCdaParkingAmount = ConverterUtils.doubleSum(remainingCdaParkingAmount , Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount());
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


        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationSubRequest budgetAllocationSubRequest = budgetAllocationSaveRequestList.getBudgetRequest().get(i);
//            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0");
            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndFinYear(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0", budgetAllocationSubRequest.getBudgetFinanciaYearId());

            double totalCbAmount = 0;
            for (ContigentBill contigentBill : contigentBills) {
                totalCbAmount = ConverterUtils.doubleSum(totalCbAmount ,Double.parseDouble(contigentBill.getCbAmount()));
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSubRequest.getAmountTypeId());
            double allocationAmount = Double.parseDouble(budgetAllocationSubRequest.getAmount()) * amountUnit.getAmount();

            if (totalCbAmount > allocationAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS SMALLER THAN CDA UNIT EXPENDITURE AMOUNT");
            }
        }


        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            for (int m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

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
        String authGrouPid = HelperUtils.getAuthorityGroupId();
        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

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


            for (int m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
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
        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            for (int m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                double bakiPesa = ConverterUtils.doubleMinus(remainingCdaParkingAmount , parkingAmount) / cadAmountUnit.getAmount();
                bakiPesa= (ConverterUtils.addDoubleValue(bakiPesa));
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

        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatusIn(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequest.getBudgetRequest().get(i).getAllocationTypeId(), "0", "0", dataIscgBg);
            if (!budgetAllocationDetailsList.isEmpty()) {
                CgUnit cgUnit = cgUnitRepository.findByUnit(budgetAllocationSaveRequest.getBudgetRequest().get(i).getToUnitId());
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET ALREADY ALLOCATED " + cgUnit.getDescr() + " . CAN NOT ASSIGN AGAIN");
            }
        }

        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


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

            if (cdaParkingList.isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PLEASE ADD CDA IN BUDGET RECEIPT.THAN TRY AGAIN");
            }


            for (int m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

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
            for (int m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");


                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
                remainingCdaParkingAmount = ConverterUtils.doubleSum(ConverterUtils.doubleSum(remainingCdaParkingAmount , remainingCdaParkingAmount) , (Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount()));
                allocationAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmount()) * amountUnit.getAmount();

            }

            if (allocationAmount > remainingCdaParkingAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS GREATER THAN CDA REMAINING AMOUNT");
            }

        }


        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            BudgetAllocationSubRequestEdit budgetAllocationSubRequest = budgetAllocationSaveRequest.getBudgetRequest().get(i);
//            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0");
            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndFinYear(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0", budgetAllocationSubRequest.getBudgetFinanciaYearId());

            double totalCbAmount = 0;
            for (ContigentBill contigentBill : contigentBills) {
                totalCbAmount = ConverterUtils.doubleSum(totalCbAmount , Double.parseDouble(contigentBill.getCbAmount()));
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSubRequest.getAmountTypeId());
            double allocationAmount = Double.parseDouble(budgetAllocationSubRequest.getAmount()) * amountUnit.getAmount();

            if (totalCbAmount > allocationAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS SMALLER THAN CDA UNIT EXPENDITURE AMOUNT");
            }
        }


        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


            for (int m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

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


        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getTransactionId());
            if (budgetAllocationDetails == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TRANSACTION ID");
            }

        }


        String authGrouPid = "";
        String type = "";
//        String refTransID = HelperUtils.getBudgetAlloctionRefrensId();

        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {

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


            for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaCrDrTransData) {
                cdaParkingCrAndDr.setIsFlag("2");
                parkingCrAndDrRepository.save(cdaParkingCrAndDr);

            }


            for (int m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
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


        for (int i = 0; i < budgetAllocationSaveRequest.getBudgetRequest().size(); i++) {


            for (int m = 0; m < budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequest.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequest.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                double bakiPesa = ConverterUtils.doubleMinus(remainingCdaParkingAmount , parkingAmount) / cadAmountUnit.getAmount();
                bakiPesa= (ConverterUtils.addDoubleValue(bakiPesa));
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

        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {
            List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatusIn(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getBudgetFinanciaYearId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getSubHeadId(), budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAllocationTypeId(), "0", "0", dataIscgBg);
            if (!budgetAllocationDetailsList.isEmpty()) {
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


        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


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


            for (int m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

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
            for (int m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());
                remainingCdaParkingAmount = ConverterUtils.doubleSum(remainingCdaParkingAmount, Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount());
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

        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getTransactionId());
            if (budgetAllocationDetails == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TRANSACTION ID");
            }
        }


        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationSubRequestEdit budgetAllocationSubRequest = budgetAllocationSaveRequestList.getBudgetRequest().get(i);
//            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0");
            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndFinYear(budgetAllocationSubRequest.getToUnitId(), budgetAllocationSubRequest.getSubHeadId(), "0", "0", budgetAllocationSubRequest.getBudgetFinanciaYearId());

            double totalCbAmount = 0;
            for (ContigentBill contigentBill : contigentBills) {
                totalCbAmount = ConverterUtils.doubleSum(totalCbAmount , Double.parseDouble(contigentBill.getCbAmount()));
            }

            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSubRequest.getAmountTypeId());
            double allocationAmount = Double.parseDouble(budgetAllocationSubRequest.getAmount()) * amountUnit.getAmount();

            if (totalCbAmount > allocationAmount) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION AMOUNT IS SMALLER THAN CDA UNIT EXPENDITURE AMOUNT");
            }
        }


        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {


            for (int m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {

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


        String authGrouPid = "";
        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            BudgetAllocationDetails budgetAllocationDetails = budgetAllocationDetailsRepository.findByTransactionId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getTransactionId());
            authGrouPid = budgetAllocationDetails.getAuthGroupId();
            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmount()));
            budgetAllocationDetails.setStatus("Pending");

            budgetAllocationDetails.setAmountType(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());

            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetails.setIsDelete("0");
            budgetAllocationDetails.setUnallocatedAmount("0");
            budgetAllocationDetails.setIsTYpe("U");
//            type = budgetAllocationSaveRequestList.getBudgetRequest().get(i).getToUnitId();
            BudgetAllocationDetails saveData = budgetAllocationDetailsRepository.save(budgetAllocationDetails);


            List<CdaParkingCrAndDr> cdaCrDrTransData = parkingCrAndDrRepository.findByTransactionIdAndIsFlagAndIsRevision(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getTransactionId(), "0", 0);

            for (CdaParkingCrAndDr cdaParkingCrAndDr : cdaCrDrTransData) {

                cdaParkingCrAndDr.setIsFlag("2");
                parkingCrAndDrRepository.save(cdaParkingCrAndDr);

            }


            for (int m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
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
        for (int i = 0; i < budgetAllocationSaveRequestList.getBudgetRequest().size(); i++) {

            for (int m = 0; m < budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().size(); m++) {
                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingIdAndIsFlag(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaParkingId(), "0");
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getAmountTypeId());
                double parkingAmount = Double.parseDouble(budgetAllocationSaveRequestList.getBudgetRequest().get(i).getCdaParkingId().get(m).getCdaAmount()) * amountUnit.getAmount();

                double bakiPesa = ConverterUtils.doubleMinus(remainingCdaParkingAmount , parkingAmount) / cadAmountUnit.getAmount();
                bakiPesa= (ConverterUtils.addDoubleValue(bakiPesa));
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

        for (BudgetAllocationDetails budgetAllocationsDetali : budgetAllocationsDetalis) {

            FindBudgetResponse authirtyResponse = new FindBudgetResponse();

            AllocationType allocationType = allocationRepository.findByAllocTypeId(budgetAllocationsDetali.getAllocTypeId());
            BudgetHead subHeadData = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetAllocationsDetali.getSubHead());
            CgUnit cgUnitData = cgUnitRepository.findByUnit(budgetAllocationsDetali.getToUnit());

            authirtyResponse.setAllocationType(allocationType);
            authirtyResponse.setBudgetAllocationsDetalis(budgetAllocationsDetali);
            authirtyResponse.setSubHead(subHeadData);
            authirtyResponse.setUnit(cgUnitData);
            authirtyResponse.setRemark(budgetAllocationsDetali.getRemarks());
            authirtyResponse.setAmount(ConverterUtils.addDecimalPoint(budgetAllocationsDetali.getAllocationAmount()));

            findBudgetResponse.add(authirtyResponse);

        }

        return ResponseUtils.createSuccessResponse(findBudgetResponse, new TypeReference<List<FindBudgetResponse>>() {
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
            for (CgUnit unit : unitDataList) {


                if (unit.getPurposeCode().equalsIgnoreCase("0") || unit.getPurposeCode().equalsIgnoreCase("1")) {
                    CgUnitResponse cgUnitResponse = new CgUnitResponse();
                    BeanUtils.copyProperties(unit, cgUnitResponse);
                    CgStation cgStation = null;
                    if (unit.getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unit.getStationId());
                    }
                    cgUnitResponse.setCgStation(cgStation);
                    cgUnitResponseList.add(cgUnitResponse);
                }

            }

        } else if (cuurentRole.equalsIgnoreCase(HelperUtils.UNITADMIN)) {
            List<CgUnit> unitDataList = cgUnitRepository.findBySubUnitOrderByDescrAsc(cgUnit.getSubUnit());

            for (CgUnit unit : unitDataList) {
                CgUnitResponse cgUnitResponse = new CgUnitResponse();
                if (unit.getPurposeCode().equalsIgnoreCase("0")) {
                    BeanUtils.copyProperties(unit, cgUnitResponse);
                    CgStation cgStation = null;
                    if (unit.getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unit.getStationId());
                    }
                    cgUnitResponse.setCgStation(cgStation);
                    cgUnitResponseList.add(cgUnitResponse);
                } else if (unit.getPurposeCode().equalsIgnoreCase("1")) {
                    BeanUtils.copyProperties(unit, cgUnitResponse);
                    CgStation cgStation = null;
                    if (unit.getStationId() == null) {
                    } else {
                        cgStation = cgStationRepository.findByStationId(unit.getStationId());
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

        if (!authoritiesList.isEmpty()) {
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


        List<BudgetAllocationDetails> allocationDetails = budgetAllocationDetailsRepository.findByAuthGroupIdAndIsDeleteAndIsBudgetRevision(authRequest.getAuthGroupId(), "0", "0");
        for (BudgetAllocationDetails allocationData : allocationDetails) {
            allocationData.setStatus("Approved");
            allocationData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocationData);

        }


        List<BudgetAllocation> budgetAllocationsList = budgetAllocationRepository.findByAuthGroupIdAndIsFlagAndIsBudgetRevision(authRequest.getAuthGroupId(), "0", "0");
        for (BudgetAllocation budgetAllocationData : budgetAllocationsList) {

            budgetAllocationData.setStatus("Approved");
            budgetAllocationRepository.save(budgetAllocationData);

        }


        List<MangeInboxOutbox> mangeInboxOutboxList = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(authRequest.getAuthGroupId(), hrDataCheck.getUnitId());
        if (!mangeInboxOutboxList.isEmpty()) {
            for (MangeInboxOutbox mangeInboxOutbox11 : mangeInboxOutboxList) {
                mangeInboxOutbox11.setStatus("Fully Approved");
                mangeInboxOutbox11.setApproverpId(hrDataCheck.getPid());
                mangeInboxOutbox11.setIsApproved("1");
                mangeInboxOutBoxRepository.save(mangeInboxOutbox11);


                if (!HelperUtils.HEADUNITID.equalsIgnoreCase(hrDataCheck.getUnitId())) {

                    CgUnit cgUnit = cgUnitRepository.findByUnit(hrDataCheck.getUnitId());
                    String[] getAllUpperUnit = cgUnit.getBudGroupUnit().split(",");
                    for (String s : getAllUpperUnit) {

                        if (s.equalsIgnoreCase(hrDataCheck.getUnitId())) {
                            continue;
                        }

                        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
                        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutbox.setRemarks("Budget Allocation by " + hrDataCheck.getUnit());
                        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutbox.setToUnit(s);
                        mangeInboxOutbox.setFromUnit(HelperUtils.HEADUNITID);
                        mangeInboxOutbox.setGroupId(mangeInboxOutbox11.getGroupId());
                        mangeInboxOutbox.setType("Budget Allocation by " + hrDataCheck.getUnit() + " in (" + mangeInboxOutbox11.getType() + ")");
                        mangeInboxOutbox.setRoleId(mangeInboxOutbox11.getRoleId());
                        mangeInboxOutbox.setCreaterpId(mangeInboxOutbox11.getCreaterpId());
                        mangeInboxOutbox.setState("CR");
                        mangeInboxOutbox.setApproverpId(mangeInboxOutbox11.getApproverpId());
                        mangeInboxOutbox.setIsFlag("1");
                        mangeInboxOutbox.setIsArchive("0");
                        mangeInboxOutbox.setIsRebase("0");
                        mangeInboxOutbox.setIsApproved("0");
                        mangeInboxOutbox.setIsRevision(0);
                        mangeInboxOutbox.setStatus("Fully Approved");
                        mangeInboxOutbox.setIsBgcg("SBG");

                        mangeInboxOutBoxRepository.save(mangeInboxOutbox);
                    }
                }
            }
        }


        HashMap<String, BudgetAllocation> totalUnit = new HashMap<String, BudgetAllocation>();
        for (BudgetAllocation budgetAllocation : budgetAllocationsList) {
            if (Double.parseDouble(budgetAllocation.getAllocationAmount()) > 0 || Double.parseDouble(budgetAllocation.getAllocationAmount()) != 0) {
                totalUnit.put(budgetAllocation.getToUnit(), budgetAllocation);
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

            if (!inboxList.isEmpty()) {
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
    public ApiResponse<DefaultResponse> saveAuthDataRevisionSaveCbAsAllocation(AuthRequest authRequest) {
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

        if (!authoritiesList.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTH DATA ALREADY UPDATE.NOW YOU CAN NOT UPDATED.");
        }

        List<BudgetAllocation> budgetAllocationsListData = new ArrayList<>();

        if (authRequest.getAuthGroupId() == null || authRequest.getAuthGroupId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO AUTH GROUP ID");
        }


        String budgetHeadId = "";
        List<CdaRevisionData> revisionData = budgetRevisionRepository.findByAuthGroupIdAndIsAutoAssignAllocation(authRequest.getAuthGroupId(), "0");
        for (CdaRevisionData revisionDatum : revisionData) {

            budgetHeadId = revisionDatum.getBudgetHeadId();
            if (revisionDatum.getIsComplete() != null) {
                if (revisionDatum.getIsComplete().equalsIgnoreCase("1")) {
                    throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "REVISION ALREADY PERFORMED.");
                }
            }


            if (revisionDatum.getToUnitId().equalsIgnoreCase(hrData.getUnitId())) {

                CdaParkingTrans cdaParkingTrans = cdaParkingTransRepository.findByCdaParkingId(revisionDatum.getCdaTransId());
                AmountUnit cadAmountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.getAmountType());

                double remainingCdaParkingAmount = Double.parseDouble(cdaParkingTrans.getRemainingCdaAmount()) * cadAmountUnit.getAmount();

                AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(revisionDatum.getAmountType());
                double parkingAmount = Double.parseDouble(revisionDatum.getAmount()) * amountUnit.getAmount();

                double bakiPesa = 0;
                if (parkingAmount < 0) {
                    bakiPesa = (ConverterUtils.doubleSum(remainingCdaParkingAmount , parkingAmount)) / cadAmountUnit.getAmount();
                } else {
                    bakiPesa = (ConverterUtils.doubleSum(remainingCdaParkingAmount , parkingAmount)) / cadAmountUnit.getAmount();
                }

                cdaParkingTrans.setRemainingCdaAmount(String.valueOf(bakiPesa));
                cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cdaParkingTransRepository.save(cdaParkingTrans);

            } else {


                if (Double.parseDouble(revisionDatum.getAmount()) > 0) {

                    //   Budget Revised All Unit Jisko diya gya hai

                    double totalAmount = ConverterUtils.doubleSum(Double.parseDouble(revisionDatum.getRemainingAmount()) , Double.parseDouble(revisionDatum.getAmount()));
                    double revisedAmount = Double.parseDouble(revisionDatum.getAmount());

                    List<BudgetAllocation> data = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(revisionDatum.getToUnitId(), revisionDatum.getFinYearId(), revisionDatum.getBudgetHeadId(), revisionDatum.getAllocTypeId(), "Approved", "0", "0");
                    for (BudgetAllocation budgetAllocationRevision : data) {
                        budgetAllocationRevision.setIsBudgetRevision("1");
                        budgetAllocationRevision.setIsTYpe("REVISION");
                        budgetAllocationRevision.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRevision.setRevisedAmount(revisionDatum.getReviserAmount());
                        budgetAllocationRepository.save(budgetAllocationRevision);
                    }


                    List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(revisionDatum.getToUnitId(), revisionDatum.getFinYearId(), revisionDatum.getBudgetHeadId(), revisionDatum.getAllocTypeId(), "Approved", "0", "0");
                    for (BudgetAllocationDetails budgetAllocationDetails : budgetAllocationDetailsList) {
                        budgetAllocationDetails.setIsBudgetRevision("1");
                        budgetAllocationDetails.setRemarks("REVISION");
                        budgetAllocationDetails.setIsTYpe("REVISION");
                        budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRepository.save(budgetAllocationDetails);
                    }


                    BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                    budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                    budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setIsDelete("0");
                    budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setFinYear(revisionDatum.getFinYearId());
                    budgetAllocationDetails.setToUnit(revisionDatum.getToUnitId());
                    budgetAllocationDetails.setFromUnit(hrData.getUnitId());
                    budgetAllocationDetails.setSubHead(revisionDatum.getBudgetHeadId());
                    budgetAllocationDetails.setIsTYpe("AFTER REVISION");
                    budgetAllocationDetails.setAllocTypeId(revisionDatum.getAllocTypeId());
                    budgetAllocationDetails.setIsBudgetRevision("0");
                    budgetAllocationDetails.setUnallocatedAmount("0");
                    budgetAllocationDetails.setUnallocatedAmount("0.0000");
                    budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(totalAmount + ""));
                    budgetAllocationDetails.setRevisedAmount(ConverterUtils.addDecimalPoint(revisedAmount + ""));
                    budgetAllocationDetails.setUserId(hrData.getPid());
                    budgetAllocationDetails.setStatus("Approved");
                    budgetAllocationDetails.setAmountType(revisionDatum.getAmountType());
                    budgetAllocationDetails.setAuthGroupId(revisionDatum.getAuthGroupId());
                    budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());// added by deevan
                    budgetAllocationDetailsRepository.save(budgetAllocationDetails);


                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setFinYear(revisionDatum.getFinYearId());
                    budgetAllocation.setToUnit(revisionDatum.getToUnitId());
                    budgetAllocation.setFromUnit(hrData.getUnitId());
                    budgetAllocation.setSubHead(revisionDatum.getBudgetHeadId());
                    budgetAllocation.setIsTYpe("AFTER REVISION");
                    budgetAllocation.setAllocationTypeId(revisionDatum.getAllocTypeId());
                    budgetAllocation.setIsBudgetRevision("0");
                    budgetAllocation.setUnallocatedAmount("0");
                    budgetAllocation.setUnallocatedAmount("0.0000");
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(totalAmount + ""));
                    budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(revisedAmount + ""));
                    budgetAllocation.setUserId(hrData.getPid());
                    budgetAllocation.setStatus("Approved");
                    budgetAllocation.setAmountType(revisionDatum.getAmountType());
                    budgetAllocation.setAuthGroupId(revisionDatum.getAuthGroupId());
                    budgetAllocationRepository.save(budgetAllocation);


                    budgetAllocationsListData.add(budgetAllocation);
                    List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionDatum.getFinYearId(), revisionDatum.getBudgetHeadId(), revisionDatum.getToUnitId(), revisionDatum.getAllocTypeId(), "0");

                    for (CdaParkingTrans cdaParkingTrans : cdaParkingTransList) {
                        cdaParkingTrans.setIsFlag("1");
                        cdaParkingTrans.setRemarks("REVISION");
                        cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTransRepository.save(cdaParkingTrans);
                    }

                    List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionDatum.getFinYearId(), revisionDatum.getBudgetHeadId(), "0", revisionDatum.getAllocTypeId(), revisionDatum.getToUnitId(), 0);
                    for (CdaParkingCrAndDr cdData : cdaParkingCrAndDr) {
                        cdData.setIsFlag("0");
                        cdData.setIsRevision(1);
                        cdData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        parkingCrAndDrRepository.save(cdData);
                    }


                } else {

                    //   Budget Revised All Unit Jisko Kata gya hai
                    //   Sirf lower unit ko diya gaya hai...


                    String currentUnitId = revisionDatum.getToUnitId();
                    List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
                    unitList.add(cgUnitRepository.findByUnit(currentUnitId));

                    for (CgUnit cgUnit : unitList) {

                        if (cgUnit.getUnit().equalsIgnoreCase(revisionDatum.getToUnitId())) {
                            continue;
                        }


                        List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(cgUnit.getUnit(), revisionDatum.getFinYearId(), revisionDatum.getBudgetHeadId(), revisionDatum.getAllocTypeId(), "Approved", "0", "0");
                        for (BudgetAllocationDetails budgetAllocationRevision : budgetAllocationDetailsList) {
                            budgetAllocationRevision.setIsTYpe("REVISION");
                            budgetAllocationRevision.setIsBudgetRevision("1");
                            budgetAllocationRevision.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetailsRepository.save(budgetAllocationRevision);
                        }

                        List<BudgetAllocation> dataBudget = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(cgUnit.getUnit(), revisionDatum.getFinYearId(), revisionDatum.getBudgetHeadId(), revisionDatum.getAllocTypeId(), "Approved", "0", "0");
                        for (BudgetAllocation budgetAllocationRevision : dataBudget) {
                            budgetAllocationRevision.setIsTYpe("REVISION");
                            budgetAllocationRevision.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationRevision.setIsBudgetRevision("1");
                            budgetAllocationRepository.save(budgetAllocationRevision);
                        }


                        double expAmount = 0;
                        List<ContigentBill> cbDataList = new ArrayList<ContigentBill>();
                        if (!dataBudget.isEmpty()) {
                            List<ContigentBill> contigentBills = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsUpdateAndIsFlag(cgUnit.getUnit(), revisionDatum.getFinYearId(), revisionDatum.getBudgetHeadId(), "0", "0");
                            for (ContigentBill contigentBill : contigentBills) {
                                expAmount = ConverterUtils.doubleSum(expAmount , Double.parseDouble(contigentBill.getCbAmount()));
                                cbDataList.add(contigentBill);
                            }
                        }


                        // calculate self or lower Exp. for allContigentBill
                        String calculateAllExpenditure = cgUnit.getUnit();
                        List<CgUnit> calculateAllExpenditureUnitList = cgUnitRepository.findByBudGroupUnitLike("%" + calculateAllExpenditure + "%");
                        calculateAllExpenditureUnitList.add(cgUnitRepository.findByUnit(calculateAllExpenditure));

                        double totalSelfOrLowerAmount = 0;
                        for (CgUnit cgUnitExp : calculateAllExpenditureUnitList) {
                            List<ContigentBill> allContingentBill = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsUpdateAndIsFlag(cgUnitExp.getUnit(), revisionDatum.getFinYearId(), revisionDatum.getBudgetHeadId(), "0", "0");
                            if (!allContingentBill.isEmpty()) {
                                for (ContigentBill contigentBill : allContingentBill) {
                                    totalSelfOrLowerAmount = ConverterUtils.doubleSum(totalSelfOrLowerAmount , Double.parseDouble(contigentBill.getCbAmount()));
                                }
                            }
                        }

                        //Create New Allocation for REVISION
                        String authGroupId = HelperUtils.getAuthorityGroupId();
                        for (BudgetAllocationDetails allocationDetailsList : budgetAllocationDetailsList) {
                            AmountUnit actualAmount = amountUnitRepository.findByAmountTypeId(revisionDatum.getAmountType());

                            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                            BeanUtils.copyProperties(allocationDetailsList, budgetAllocationDetails);

                            AmountUnit allocationAmountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocationDetails.getAmountType());
                            double revisedAmount = ConverterUtils.doubleMinus((Double.parseDouble(budgetAllocationDetails.getAllocationAmount()) * allocationAmountUnit.getAmount()) , totalSelfOrLowerAmount) / actualAmount.getAmount();

                            budgetAllocationDetails.setAllocationAmount("" + ((totalSelfOrLowerAmount / actualAmount.getAmount())));
                            budgetAllocationDetails.setIsTYpe("AFTER REVISION");
                            budgetAllocationDetails.setAmountType(revisionDatum.getAmountType());
                            budgetAllocationDetails.setIsBudgetRevision("0");
                            budgetAllocationDetails.setIsDelete("0");
                            budgetAllocationDetails.setAuthGroupId(authGroupId);
                            budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                            budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                            budgetAllocationDetails.setRevisedAmount("-" + revisedAmount);
                            budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetailsRepository.save(allocationDetailsList);

                        }


                        for (BudgetAllocation createBudgetAllocationAfterRevision : dataBudget) {

                            AmountUnit actualAmount = amountUnitRepository.findByAmountTypeId(revisionDatum.getAmountType());

                            BudgetAllocation budgetAllocation = new BudgetAllocation();
                            BeanUtils.copyProperties(createBudgetAllocationAfterRevision, budgetAllocation);


                            AmountUnit allocationAmountUnit = amountUnitRepository.findByAmountTypeId(budgetAllocation.getAmountType());
                            double allocationAmount = ConverterUtils.doubleMinus((Double.parseDouble(budgetAllocation.getAllocationAmount()) * allocationAmountUnit.getAmount()) , totalSelfOrLowerAmount) / actualAmount.getAmount();

                            budgetAllocation.setIsTYpe("AFTER REVISION");
                            budgetAllocation.setIsBudgetRevision("0");
                            budgetAllocation.setIsFlag("0");
                            budgetAllocation.setAmountType(revisionDatum.getAmountType());
                            budgetAllocation.setAuthGroupId(authGroupId);
                            budgetAllocation.setAllocationAmount("" + (totalSelfOrLowerAmount / actualAmount.getAmount()));
                            budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                            budgetAllocation.setRevisedAmount("-" + allocationAmount);
                            budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationRepository.save(budgetAllocation);


                            List<MangeInboxOutbox> checkMsgAlreadySendOrNot = mangeInboxOutBoxRepository.findByGroupIdAndToUnit(mainOnlyViewAuthGroup, cgUnit.getUnit());
                            if (checkMsgAlreadySendOrNot.isEmpty()) {

                                MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
                                mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
                                mangeInboxOutbox.setRemarks("Budget Receipt");
                                mangeInboxOutbox.setIsRebase("0");
                                mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                mangeInboxOutbox.setToUnit(createBudgetAllocationAfterRevision.getToUnit());
                                mangeInboxOutbox.setType("Budget Receipt");
                                mangeInboxOutbox.setGroupId(authGroupId);
                                mangeInboxOutbox.setIsRebase("0");
                                mangeInboxOutbox.setFromUnit(hrData.getUnitId());
                                mangeInboxOutbox.setRoleId(hrData.getRoleId());
                                mangeInboxOutbox.setCreaterpId(hrData.getPid());
                                mangeInboxOutbox.setApproverpId("");
                                mangeInboxOutbox.setStatus("Fully Approved");
                                mangeInboxOutbox.setAllocationType(createBudgetAllocationAfterRevision.getAllocationTypeId());
                                mangeInboxOutbox.setIsFlag("1");
                                mangeInboxOutbox.setIsArchive("0");
                                mangeInboxOutbox.setIsApproved("0");
                                mangeInboxOutbox.setAmount(ConverterUtils.addDecimalPoint("" + (totalSelfOrLowerAmount / actualAmount.getAmount())));
                                mangeInboxOutbox.setIsBgcg("BR");
                                mangeInboxOutbox.setState("CR");
                                mangeInboxOutbox.setIsRevision(1);
                                mangeInboxOutBoxRepository.save(mangeInboxOutbox);


                                MangeInboxOutbox mangeInboxOutboxNotificationOnly = new MangeInboxOutbox();

                                BudgetHead budgetHead = subHeadRepository.findByBudgetCodeId(createBudgetAllocationAfterRevision.getSubHead());
                                mangeInboxOutboxNotificationOnly.setMangeInboxId(HelperUtils.getMangeInboxId());
                                mangeInboxOutboxNotificationOnly.setRemarks("Budget Revised");
                                mangeInboxOutboxNotificationOnly.setIsRebase("0");
                                mangeInboxOutboxNotificationOnly.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                mangeInboxOutboxNotificationOnly.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                mangeInboxOutboxNotificationOnly.setToUnit(cgUnit.getUnit());
                                mangeInboxOutboxNotificationOnly.setType("In " + budgetHead.getSubHeadDescr() + " head budget revised by your head unit");
                                mangeInboxOutboxNotificationOnly.setGroupId(mainOnlyViewAuthGroup);
                                mangeInboxOutboxNotificationOnly.setIsRebase("0");
                                mangeInboxOutboxNotificationOnly.setFromUnit(hrData.getUnitId());
                                mangeInboxOutboxNotificationOnly.setRoleId(hrData.getRoleId());
                                mangeInboxOutboxNotificationOnly.setCreaterpId(hrData.getPid());
                                mangeInboxOutboxNotificationOnly.setApproverpId("");
                                mangeInboxOutboxNotificationOnly.setStatus("Fully Approved");
                                mangeInboxOutboxNotificationOnly.setAllocationType("");
                                mangeInboxOutboxNotificationOnly.setIsFlag("1");
                                mangeInboxOutboxNotificationOnly.setIsArchive("0");
                                mangeInboxOutboxNotificationOnly.setIsApproved("0");
                                mangeInboxOutboxNotificationOnly.setIsBgcg("UR");
                                mangeInboxOutboxNotificationOnly.setState("CR");
                                mangeInboxOutboxNotificationOnly.setIsRevision(1);
                                mangeInboxOutBoxRepository.save(mangeInboxOutboxNotificationOnly);
                            }

                        }
                        List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionDatum.getFinYearId(), revisionDatum.getBudgetHeadId(), cgUnit.getUnit(), revisionDatum.getAllocTypeId(), "0");
                        for (CdaParkingTrans cdaParkingTrans : cdaParkingTransList) {
                            cdaParkingTrans.setIsFlag("1");
                            cdaParkingTrans.setRemarks("REVISION");
                            cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            CdaParkingTrans cdaSaveData = cdaParkingTransRepository.save(cdaParkingTrans);

                        }
                        if (expAmount > 0) {


                            for (ContigentBill contigentBill : cbDataList) {

                                List<CdaParkingCrAndDr> cdaInfoList = parkingCrAndDrRepository.findByTransactionId(contigentBill.getCbId());
                                for (CdaParkingCrAndDr cdaPrakingInfo : cdaInfoList) {

                                    AmountUnit actualAmount = amountUnitRepository.findByAmountTypeId(revisionDatum.getAmountType());

                                    CdaParkingTrans cdaParkingTransAfterRevision = new CdaParkingTrans();

                                    cdaParkingTransAfterRevision.setRemainingCdaAmount(cdaPrakingInfo.getAmount());
                                    cdaParkingTransAfterRevision.setCdaParkingId(HelperUtils.getCdaId());
                                    cdaParkingTransAfterRevision.setGinNo(cdaPrakingInfo.getGinNo());
                                    cdaParkingTransAfterRevision.setBudgetHeadId(revisionDatum.getBudgetHeadId());
                                    cdaParkingTransAfterRevision.setAuthGroupId(authGroupId);
                                    cdaParkingTransAfterRevision.setFinYearId(cdaPrakingInfo.getFinYearId());
                                    cdaParkingTransAfterRevision.setUnitId(cdaPrakingInfo.getUnitId());
                                    cdaParkingTransAfterRevision.setAllocTypeId(revisionDatum.getAllocTypeId());
                                    cdaParkingTransAfterRevision.setIsFlag("0");
                                    cdaParkingTransAfterRevision.setTransactionId(contigentBill.getCbId());
                                    cdaParkingTransAfterRevision.setAmountType(revisionDatum.getAmountType());
                                    cdaParkingTransAfterRevision.setRemainingCdaAmount("0.00");
                                    cdaParkingTransAfterRevision.setIsFlag("0");
                                    cdaParkingTransAfterRevision.setRemarks("AFTER REVISION AUTO");
                                    cdaParkingTransAfterRevision.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                    cdaParkingTransAfterRevision.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                    cdaParkingTransRepository.save(cdaParkingTransAfterRevision);
                                }

                            }


                            List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionDatum.getFinYearId(), revisionDatum.getBudgetHeadId(), "0", revisionDatum.getAllocTypeId(), cgUnit.getUnit(), 0);
                            for (CdaParkingCrAndDr cddata : cdaParkingCrAndDr) {
                                cddata.setIsFlag("1");
                                cddata.setIsRevision(1);
                                cddata.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                parkingCrAndDrRepository.save(cddata);
                            }

                        }
                    }
                }
            }

            revisionDatum.setIsComplete("1");
            budgetRevisionRepository.save(revisionDatum);
        }


        // Sirf just selceted unit jiska Amount kaata  gaya hai..
        ArrayList<String> unitList = new ArrayList<>();
        List<CdaRevisionData> revisionRemainingAmountSend = budgetRevisionRepository.findByAuthGroupIdAndIsAutoAssignAllocation(authRequest.getAuthGroupId(), "1");
        for (int v = 0; v < revisionRemainingAmountSend.size(); v++) {


            if (revisionRemainingAmountSend.get(v).getCdaTransId() == null) {

                AmountUnit revisionAmountType = amountUnitRepository.findByAmountTypeId(revisionRemainingAmountSend.get(v).getAmountType());
                double revisedAmount = Double.parseDouble(revisionRemainingAmountSend.get(v).getAmount());
                AmountUnit amountUnitData = amountUnitRepository.findByAmountTypeId(revisionRemainingAmountSend.get(v).getAmountType());
                double totalRemainingAmountAfterCbBill = ((revisedAmount * amountUnitData.getAmount())) / revisionAmountType.getAmount();


                List<BudgetAllocationDetails> budgetAllocationDetailsList = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(revisionRemainingAmountSend.get(v).getToUnitId(), revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), revisionRemainingAmountSend.get(v).getAllocTypeId(), "Approved", "0", "0");
                for (BudgetAllocationDetails budgetAllocationRevision : budgetAllocationDetailsList) {
                    budgetAllocationRevision.setIsBudgetRevision("1");
                    budgetAllocationRevision.setIsTYpe("REVISION");
                    budgetAllocationDetailsRepository.save(budgetAllocationRevision);
                }

                List<BudgetAllocation> dataBudget = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(revisionRemainingAmountSend.get(v).getToUnitId(), revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), revisionRemainingAmountSend.get(v).getAllocTypeId(), "Approved", "0", "0");
                for (BudgetAllocation budgetAllocationRevision : dataBudget) {
                    budgetAllocationRevision.setIsBudgetRevision("1");
                    budgetAllocationRevision.setIsTYpe("REVISION");
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
                budgetAllocation.setIsTYpe("AFTER REVISION");
                budgetAllocation.setAllocationTypeId(revisionRemainingAmountSend.get(v).getAllocTypeId());
                budgetAllocation.setIsBudgetRevision("0");
                budgetAllocation.setUnallocatedAmount("0.0000");
                budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(totalRemainingAmountAfterCbBill + ""));
                budgetAllocation.setRevisedAmount(ConverterUtils.addDecimalPoint(revisionRemainingAmountSend.get(v).getReviserAmount() + ""));
                budgetAllocation.setUserId(hrData.getPid());
                budgetAllocation.setStatus("Approved");
                budgetAllocation.setAmountType(revisionRemainingAmountSend.get(v).getAmountType());
                budgetAllocation.setAuthGroupId(authGroupId);
                budgetAllocationRepository.save(budgetAllocation);

                unitList.add(revisionRemainingAmountSend.get(v).getToUnitId());

                List<CdaParkingTrans> cdaParkingTransList = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), revisionRemainingAmountSend.get(v).getToUnitId(), revisionRemainingAmountSend.get(v).getAllocTypeId(), "0");

                for (CdaParkingTrans cdaParkingTrans : cdaParkingTransList) {
                    cdaParkingTrans.setIsFlag("1");
                    cdaParkingTrans.setRemarks("REVISION");
                    cdaParkingTrans.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    cdaParkingTransRepository.save(cdaParkingTrans);
                }

                List<CdaParkingCrAndDr> cdaParkingCrAndDr = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(revisionRemainingAmountSend.get(v).getFinYearId(), revisionRemainingAmountSend.get(v).getBudgetHeadId(), "0", revisionRemainingAmountSend.get(v).getAllocTypeId(), revisionRemainingAmountSend.get(v).getToUnitId(), 0);
                for (CdaParkingCrAndDr cddata : cdaParkingCrAndDr) {
                    cddata.setIsFlag("1");
                    cddata.setRemark("REVISION");
                    cddata.setIsRevision(1);
                    parkingCrAndDrRepository.save(cddata);
                }


                String authgroupid = authRequest.getAuthGroupId();
                List<MangeInboxOutbox> inboxList = mangeInboxOutBoxRepository.findByGroupId(authgroupid);

                MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();

                mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
                mangeInboxOutbox.setRemarks("Budget Receipt");
                mangeInboxOutbox.setIsRebase("0");
                mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                mangeInboxOutbox.setToUnit(revisionRemainingAmountSend.get(v).getToUnitId());
                if (!inboxList.isEmpty()) {
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

        for (BudgetAllocation budgetAllocationsListDatum : budgetAllocationsListData) {
            if (!unitList.contains(budgetAllocationsListDatum)) {
                totalUnit.put(budgetAllocationsListDatum.getToUnit(), budgetAllocationsListDatum);
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
            if (!inboxList.isEmpty()) {
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
        if (!mangeInboxOutboxList.isEmpty()) {
            for (MangeInboxOutbox mangeInboxOutbox : mangeInboxOutboxList) {
                try {
                    mangeInboxOutbox.setStatus("Fully Approved");
                    mangeInboxOutbox.setIsApproved("1");
                    mangeInboxOutBoxRepository.save(mangeInboxOutbox);
                } catch (Exception e) {

                }
            }
        }


        if (!HelperUtils.HEADUNITID.equalsIgnoreCase(hrData.getUnitId())) {

            BudgetHead budgetHead = subHeadRepository.findByBudgetCodeId(budgetHeadId);

            CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());
            String[] getAllUpperUnit = cgUnit.getBudGroupUnit().split(",");
            for (String s : getAllUpperUnit) {

                if (s.equalsIgnoreCase(hrData.getUnitId())) {
                    continue;
                }

                MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
                mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
                mangeInboxOutbox.setRemarks("Budget Revision by " + hrData.getUnit());
                mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                mangeInboxOutbox.setToUnit(s);
                mangeInboxOutbox.setFromUnit(HelperUtils.HEADUNITID);
                mangeInboxOutbox.setGroupId(authRequest.getAuthGroupId());
                mangeInboxOutbox.setType("Budget Revision by " + hrData.getUnit() + " in (" + budgetHead.getSubHeadDescr() + ")");
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
                mangeInboxOutbox.setIsBgcg("BGR");

                mangeInboxOutBoxRepository.save(mangeInboxOutbox);
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
        for (int n = 0; n < unitDataList.size(); n++) {
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


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
import com.sdd.service.MangeRebaseService;
import com.sdd.service.MangeUserService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MangeRebaseImpl implements MangeRebaseService {

    private static final long expirationTime = 1L * 60L * 60L;

    @Autowired
    private CgUnitRepository cgUnitRepository;

    @Autowired
    BudgetAllocationRepository budgetAllocationRepository;

    @Autowired
    private AmountUnitRepository amountUnitRepository;

    @Autowired
    MangeInboxOutBoxRepository mangeInboxOutBoxRepository;


    @Autowired
    private BudgetFinancialYearRepository budgetFinancialYearRepository;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private HrDataRepository hrDataRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HeaderUtils headerUtils;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private CgStationRepository cgStationRepository;

    @Autowired
    private BudgetRebaseRepository budgetRebaseRepository;

    @Autowired
    private BudgetAllocationDetailsRepository budgetAllocationDetailsRepository;

    @Autowired
    private ContigentBillRepository contigentBillRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    @Autowired
    private SubHeadRepository subHeadRepository;

    @Autowired
    CdaParkingCrAndDrRepository parkingCrAndDrRepository;
    @Autowired
    private CdaParkingTransRepository cdaParkingTransRepository;

    @Autowired
    private CdaParkingRepository cdaParkingRepository;


    @Override
    @Transactional
    public ApiResponse<DefaultResponse> saveRebaes(MangeRebaseRequest mangeRebaseRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
        }

        if (mangeRebaseRequest.getAuthDocId() == null || mangeRebaseRequest.getAuthDocId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "DOCUMENT ID CAN NOT BE BLANK");
        }

        if (mangeRebaseRequest.getAuthority() == null || mangeRebaseRequest.getAuthority().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY CAN NOT BE BLANK");
        }

        if (mangeRebaseRequest.getAuthDate() == null || mangeRebaseRequest.getAuthDate().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AUTHORITY DATE CAN NOT BE BLANK");
        }

        ConverterUtils.checkDateIsvalidOrNor(mangeRebaseRequest.getAuthDate());

        CgUnit chekUnit = cgUnitRepository.findByUnit(mangeRebaseRequest.getAuthUnitId());
        if (chekUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
        }

        FileUpload fileUpload = fileUploadRepository.findByUploadID(mangeRebaseRequest.getAuthDocId());
        if (fileUpload == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID DOCUMENT ID ");
        }


       /* for (Integer m = 0; m < mangeRebaseRequest.getUnitRebaseRequests().size(); m++) {


            if (mangeRebaseRequest.getUnitRebaseRequests().get(m).getToUnitId() == null || mangeRebaseRequest.getUnitRebaseRequests().get(m).getToUnitId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT ID CAN NOT BE BLANK");
            }

            if (mangeRebaseRequest.getUnitRebaseRequests().get(m).getBudgetFinanciaYearId() == null || mangeRebaseRequest.getUnitRebaseRequests().get(m).getBudgetFinanciaYearId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
            }


            if (mangeRebaseRequest.getUnitRebaseRequests().get(m).getStationId() == null || mangeRebaseRequest.getUnitRebaseRequests().get(m).getStationId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "STATION ID CAN NOT BE BLANK");
            }


            if (mangeRebaseRequest.getUnitRebaseRequests().get(m).getOccurrenceDate() == null || mangeRebaseRequest.getUnitRebaseRequests().get(m).getOccurrenceDate().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "OCCURRENCE DATE CAN NOT BE BLANK");
            }


            ConverterUtils.checkDateIsvalidOrNor(mangeRebaseRequest.getUnitRebaseRequests().get(m).getOccurrenceDate());

            CgUnit chekUnitData = cgUnitRepository.findByUnit(mangeRebaseRequest.getUnitRebaseRequests().get(m).getToUnitId());
            if (chekUnitData == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
            }

            BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(mangeRebaseRequest.getUnitRebaseRequests().get(m).getBudgetFinanciaYearId());
            if (budgetFinancialYear == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
            }

            CgStation cgStation = cgStationRepository.findByStationId(mangeRebaseRequest.getUnitRebaseRequests().get(m).getStationId());
            if (cgStation == null) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO STATION ID");
            }

        }*/


        Authority authority = new Authority();
        authority.setAuthority(mangeRebaseRequest.getAuthority());
        authority.setAuthorityId(HelperUtils.getAuthorityId());
        authority.setAuthDate(ConverterUtils.convertDateTotimeStamp(mangeRebaseRequest.getAuthDate()));
        authority.setDocId(mangeRebaseRequest.getAuthDocId());
        authority.setAuthUnit(mangeRebaseRequest.getAuthUnitId());
        authority.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        authority.setAuthGroupId(HelperUtils.getAuthorityGroupId());
        authority.setRemarks(mangeRebaseRequest.getRemark());
        authority.setUpdatedOn(HelperUtils.getCurrentTimeStamp());

        Authority saveAuthority = authorityRepository.save(authority);

        String refRensId = HelperUtils.getTransId();
        for (Integer l = 0; l < mangeRebaseRequest.getUnitRebaseRequests().size(); l++) {

            BudgetRebase budgetRebase = new BudgetRebase();
            budgetRebase.setBudgetRebaseId(HelperUtils.getUnitRebased());
            budgetRebase.setAuthorityId(saveAuthority.getAuthorityId());
            budgetRebase.setRefTransId(refRensId);
            //budgetRebase.setFromUnitId(hrDataCheck.getUnitId());
            //budgetRebase.setToUnitId(mangeRebaseRequest.getUnitRebaseRequests().get(l).getToUnitId());
            //budgetRebase.setStationId(mangeRebaseRequest.getUnitRebaseRequests().get(l).getStationId());
            //budgetRebase.setFinYear(mangeRebaseRequest.getUnitRebaseRequests().get(l).getBudgetFinanciaYearId());
            budgetRebase.setUserId(hrDataCheck.getPid());
            //budgetRebase.setLastCbDate(ConverterUtils.convertDateTotimeStamp(mangeRebaseRequest.getUnitRebaseRequests().get(l).getOccurrenceDate()));


            budgetRebase.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetRebase.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetRebaseRepository.save(budgetRebase);
        }


        DefaultResponse defaultResponse = new DefaultResponse();

        defaultResponse.setMsg("ROLE UPDATE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }

    @Override
    @Transactional
    public ApiResponse<List<CgStation>> getAllStation() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
        }

        List<CgStation> getAllData = cgStationRepository.findByIsBasePort("1");

        return ResponseUtils.createSuccessResponse(getAllData, new TypeReference<List<CgStation>>() {
        });
    }

    @Override
    @Transactional
    public ApiResponse<List<CgUnitResponse>> getAllUnit() {
        List<CgUnitResponse> responce = new ArrayList<CgUnitResponse>();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
        }

        List<CgUnit> getAllData = cgUnitRepository.findAllByOrderByDescrAsc();

        if (getAllData.size() > 0) {

            for (int i = 0; i < getAllData.size(); i++) {
                CgUnitResponse rep = new CgUnitResponse();
                rep.setUnit(getAllData.get(i).getUnit());
                rep.setDescr(getAllData.get(i).getDescr());
                rep.setCgUnitShort(getAllData.get(i).getCgUnitShort());
                rep.setPurposeCode(getAllData.get(i).getPurposeCode());
                rep.setCbUnit(getAllData.get(i).getSubUnit());
                rep.setIsActive(getAllData.get(i).getIsActive());
                rep.setCreatedOn(getAllData.get(i).getCreatedOn());
                rep.setUpdatedOn(getAllData.get(i).getUpdatedOn());
                String stationId = getAllData.get(i).getStationId();
                CgStation cgStation = cgStationRepository.findByStationId(stationId);
                rep.setCgStation(cgStation);
                responce.add(rep);
            }
        }


        return ResponseUtils.createSuccessResponse(responce, new TypeReference<List<CgUnitResponse>>() {
        });
    }


    @Override
    @Transactional
    public ApiResponse<List<BudgetFinancialYear>> getAllBudgetFinYr() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
        }

        List<BudgetFinancialYear> getAllFnYrData = budgetFinancialYearRepository.findAllByOrderByFinYearAsc();

        return ResponseUtils.createSuccessResponse(getAllFnYrData, new TypeReference<List<BudgetFinancialYear>>() {
        });
    }

    @Override
    @Transactional
    public ApiResponse<List<RebaseBudgetHistory>> getAllUnitRebaseData(String finYear, String unit) {
        List<RebaseBudgetHistory> responce = new ArrayList<RebaseBudgetHistory>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrDataCheck == null) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            }, "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS", HttpStatus.OK.value());
        } else {
            if (hrDataCheck.getRoleId().contains(HelperUtils.BUDGETMANGER)) {
            } else {
                return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
                }, "YOU ARE NOT AUTHORIZED TO REBASE THE STATION", HttpStatus.OK.value());
            }
        }
        if (unit.equalsIgnoreCase(hrDataCheck.getUnitId())) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            }, "YOU ARE NOT AUTHORIZED TO SELF REBASE", HttpStatus.OK.value());
        }
        if (finYear == null) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            }, "FIN YEAR ID CAN NOT BE NULL", HttpStatus.OK.value());
        }
        if (unit == null) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            }, "UNIT CAN NOT BE NULL", HttpStatus.OK.value());
        }
        BudgetFinancialYear Finyr = budgetFinancialYearRepository.findBySerialNo(finYear);
        CgUnit unitdata = cgUnitRepository.findByUnit(unit);
        List<AllocationType> allocType = allocationRepository.findByIsFlag("1");
        String allocTypes = allocType.get(0).getAllocTypeId();
        if (unitdata == null) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            }, "INVALID UNIT ID PLEASE CHECK", HttpStatus.OK.value());
        }
        List<BudgetAllocation> allocationData1 = budgetAllocationRepository.findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(unit, finYear, allocTypes, "0");
        List<BudgetAllocation> allocationData2 = allocationData1.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved") && e.getIsFlag().equalsIgnoreCase("0")).collect(Collectors.toList());
        List<BudgetAllocation> allocationData = allocationData2.stream().filter(e -> !e.getAllocationAmount().equalsIgnoreCase("0")).collect(Collectors.toList());
        if (allocationData.size() <= 0) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            }, "Record Not Found", HttpStatus.OK.value());
        }

        for (int i = 0; i < allocationData.size(); i++) {


            RebaseBudgetHistory rebase = new RebaseBudgetHistory();
            AmountUnit amountTypeObj = amountUnitRepository.findByAmountTypeId(allocationData.get(i).getAmountType());
            if (amountTypeObj == null) {
                return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
                }, "AMOUNT TYPE NOT FOUND FROM DB", HttpStatus.OK.value());
            }
            double amountUnit = amountTypeObj.getAmount();
            String allocId = allocationData.get(i).getAllocationTypeId();
            Double aAmount = Double.parseDouble(allocationData.get(i).getAllocationAmount());
            //CgUnit frmUnit = cgUnitRepository.findByUnit(allocationData.get(i).getFromUnit());
            rebase.setFromUnit(cgUnitRepository.findByUnit(allocationData.get(i).getFromUnit()));
            rebase.setUnit(unitdata.getDescr());
            rebase.setFinYear(Finyr.getFinYear());
            rebase.setStatus(allocationData.get(i).getStatus());
            rebase.setAmountType(amountTypeObj);
            rebase.setAllocationType(allocationRepository.findByAllocTypeId(allocId));
            rebase.setAuthGrupId(allocationData.get(i).getAuthGroupId());
            rebase.setSubHead(subHeadRepository.findByBudgetCodeId(allocationData.get(i).getSubHead()));
            String bHead = allocationData.get(i).getSubHead();
            List<CdaParkingTrans> cdaDetails = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYear, bHead, unit, allocId, "0");


            if (Double.parseDouble(allocationData.get(i).getAllocationAmount()) == 0) {
            } else {
                if (cdaDetails.size() <= 0) {
                    return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
                    }, "CDA NOT FOUND IN THIS SUBHEAD " + bHead, HttpStatus.OK.value());
                }
            }


            List<CdaDetailsForRebaseResponse> addRes = new ArrayList<CdaDetailsForRebaseResponse>();
            double remCdaBal = 0.0;
            double TotalCdaBal = 0.0;
            for (int j = 0; j < cdaDetails.size(); j++) {
                CdaDetailsForRebaseResponse cda = new CdaDetailsForRebaseResponse();
                cda.setGinNo(cdaParkingRepository.findByGinNo(cdaDetails.get(j).getGinNo()));
                cda.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaDetails.get(j).getAmountType()));
                cda.setTotalParkingAmount(cdaDetails.get(j).getTotalParkingAmount());
                cda.setRemainingCdaAmount(cdaDetails.get(j).getRemainingCdaAmount());
                cda.setRemarks(cdaDetails.get(j).getRemarks());
                cda.setSubHeadId(cdaDetails.get(j).getBudgetHeadId());
                if (cdaDetails.get(j).getRemainingCdaAmount() == null) {
                    remCdaBal+=0.0;
                }else{
                    remCdaBal += Double.parseDouble(cdaDetails.get(j).getRemainingCdaAmount());
                }
                if (cdaDetails.get(j).getTotalParkingAmount() == null) {
                    TotalCdaBal=0.0;
                }else{
                    TotalCdaBal += Double.parseDouble(cdaDetails.get(j).getTotalParkingAmount());
                }

                addRes.add(cda);
            }
            rebase.setAllocatedAmount(String.valueOf(TotalCdaBal));
            rebase.setRemCdaBal(String.valueOf(remCdaBal));
            rebase.setCdaData(addRes);
            List<ContigentBill> expenditure1 = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdate(unit, finYear, bHead, allocId, "0");
            List<ContigentBill> expenditure = expenditure1.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
            if (expenditure.size() > 0) {
                double totalAmount = 0.0;
                Date lastCbDate = null;
                for (ContigentBill data : expenditure) {
                    totalAmount += Double.parseDouble(data.getCbAmount());
                    lastCbDate = data.getCbDate();
                }
                double expAmnt = totalAmount / amountUnit;
                double bal = aAmount - expAmnt;
                rebase.setExpenditureAmount(String.valueOf(expAmnt));
                rebase.setRemBal(String.valueOf(remCdaBal));
                rebase.setLastCbDate(lastCbDate);
            } else {
                rebase.setExpenditureAmount("0.0000");
                rebase.setLastCbDate(null);
                rebase.setRemBal(String.valueOf(remCdaBal));
            }

            responce.add(rebase);
        }
        return ResponseUtils.createSuccessResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
        });
    }

    @Override
    @Transactional
    public ApiResponse<CgStation> getAllStationById(String stationId) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
        }

        if (stationId == null || stationId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "STATION ID CAN NOT BLANK");
        }
        CgStation cgStation = cgStationRepository.findByStationId(stationId);

        return ResponseUtils.createSuccessResponse(cgStation, new TypeReference<CgStation>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> saveUnitRebase(UnitRebaseSaveReq req) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        DefaultResponse defaultResponse = new DefaultResponse();

        String universalAuthGroup = HelperUtils.getAuthorityGroupId();
        String universalAuthGroupForRebase = HelperUtils.getAuthorityGroupId();


        if (hrDataCheck == null) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS", HttpStatus.OK.value());
        }
        if (req.getAuthority() == null || req.getAuthority().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "AUTHORITY CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getAuthDate() == null || req.getAuthDate().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "AUTHORITY DATE CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getAuthUnitId() == null || req.getAuthUnitId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "AUTHORITY UNIT CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getAuthDocId() == null || req.getAuthDocId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "DOCUMENT ID CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getOccurrenceDate() == null || req.getOccurrenceDate().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "OCCURRENCE DATE CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getFinYear() == null || req.getFinYear().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "FINANCIAL YEAR CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getRebaseUnitId() == null || req.getRebaseUnitId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "REBASE UNIT ID CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getToStationId() == null || req.getToStationId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "TO_STATION ID CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getFrmStationId() == null || req.getFrmStationId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "FROM_STATION ID CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getUnitRebaseRequests().size() <= 0) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "YOU CAN'T REBASE WITHOUT ALLOCATION", HttpStatus.OK.value());
        }


        for (Integer m = 0; m < req.getUnitRebaseRequests().size(); m++) {

            if (req.getUnitRebaseRequests().get(m).getAllocAmount() == null || req.getUnitRebaseRequests().get(m).getAllocAmount().isEmpty()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "ALLOCATION AMOUNT CAN NOT BE BLANK", HttpStatus.OK.value());
            }
            if (req.getUnitRebaseRequests().get(m).getExpAmount() == null || req.getUnitRebaseRequests().get(m).getExpAmount().isEmpty()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "EXPEND_AMOUNT CAN NOT BE BLANK", HttpStatus.OK.value());
            }
            if (req.getUnitRebaseRequests().get(m).getBalAmount() == null || req.getUnitRebaseRequests().get(m).getBalAmount().isEmpty()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "BAL_AMOUNT CAN NOT BE BLANK", HttpStatus.OK.value());
            }
            if (req.getUnitRebaseRequests().get(m).getAmountType() == null || req.getUnitRebaseRequests().get(m).getAmountType().isEmpty()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "AMOUNT_TYPE CAN NOT BE BLANK", HttpStatus.OK.value());
            }
            if (req.getUnitRebaseRequests().get(m).getAllocationTypeId() == null || req.getUnitRebaseRequests().get(m).getAllocationTypeId().isEmpty()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "ALLOCATION TYPE ID CAN NOT BE BLANK", HttpStatus.OK.value());
            }
        }


        CgUnit chekUnit = cgUnitRepository.findByUnit(req.getRebaseUnitId());
        String subUnits = chekUnit.getSubUnit();
        CgStation frmS = cgStationRepository.findByStationName(req.getFrmStationId());
        if (frmS == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FROM STATION REGION GETTING NULL");
        }
        CgStation toS = cgStationRepository.findByStationId(req.getToStationId());
        if (toS == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO STATION REGION GETTING NULL");
        }

        String toRegion = toS.getRhqId();
        String tohdUnit = "";
        if (toS.getDhqName() == null || toS.getDhqName().isEmpty()) {
            tohdUnit = toS.getRhqId();
        } else {
            tohdUnit = toS.getDhqName();
        }
        String frmRegion = frmS.getRhqId();
//        String frmhdUnit = "";
//        if (frmS.getDhqName() == null || frmS.getDhqName().isEmpty()) {
//            frmhdUnit = frmS.getRhqId();
//        } else {
//            frmhdUnit = frmS.getDhqName();
//        }
        CgUnit cgData = cgUnitRepository.findByUnit(hrDataCheck.getUnitId());
        String rebaseAuthority = cgData.getIsRebaseAuthority();
        if (!rebaseAuthority.equalsIgnoreCase("1")) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED FOR UNIT REBASE");
        }
        if (!frmRegion.equalsIgnoreCase(toRegion)) {
            if (!hrDataCheck.getUnitId().equalsIgnoreCase("001321"))
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED FOR UNIT REBASE IN ANOTHER REGION");
        }

        CgUnit obj = cgUnitRepository.findByCgUnitShort(tohdUnit);
        if (obj == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO HEAD UNIT NOT FOUND");
        }
        String toHdUnitId = obj.getUnit();
        CgUnit headBudgrpId = cgUnitRepository.findByUnit(toHdUnitId);
        String setBudGroupHdUnit = headBudgrpId.getBudGroupUnit();
        String setBudGroupUnit = setBudGroupHdUnit + "," + toHdUnitId;

        List<BudgetAllocation> allocationData = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(0).getBudgetHeadId(), req.getRebaseUnitId(), req.getFinYear(), req.getUnitRebaseRequests().get(0).getAllocationTypeId(), "0", "0");
        if (allocationData.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION NOT FOUND FOR THIS UNIT");
        }
        String frmUnit = allocationData.get(0).getFromUnit();


        if (chekUnit == null || chekUnit.getUnit().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID UNIT");
        }
        if (frmS.getStationId().equalsIgnoreCase(req.getToStationId())) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CAN NOT REBASE ON SAME STATION");
        }
        String maxRebaseId = budgetRebaseRepository.findMaxRebaseIDByRebaseUnitId(req.getRebaseUnitId());
        if (maxRebaseId != null) {
            BudgetRebase rebaseData = budgetRebaseRepository.findByBudgetRebaseId(maxRebaseId);
            Date crDate = rebaseData.getCreatedOn();
            Date expireDate = new Date(crDate.getTime() + expirationTime * 1000);
            Date todayDate = new Date();
//            if (expireDate.getTime() >= todayDate.getTime()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CAN NOT REBASE SAME UNIT ! TRY AFTER 1 HOURS");
//
//            } else {
            chekUnit.setStationId(req.getToStationId());
            chekUnit.setSubUnit(toHdUnitId);
            chekUnit.setBudGroupUnit(setBudGroupUnit);
            if(toS.getRhqId()==null){
                chekUnit.setUnitRhq("");
            }else{
                chekUnit.setUnitRhq(toS.getRhqId());
            }
            if(toS.getDhqName()==null){
                chekUnit.setUnitDhq("");
            }else{
                chekUnit.setUnitDhq(toS.getDhqName());
            }
            chekUnit.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cgUnitRepository.save(chekUnit);
//            }
        } else {
            chekUnit.setStationId(req.getToStationId());
            chekUnit.setSubUnit(toHdUnitId);
            chekUnit.setBudGroupUnit(setBudGroupUnit);
            if(toS.getRhqId()==null){
                chekUnit.setUnitRhq("");
            }else{
                chekUnit.setUnitRhq(toS.getRhqId());
            }
            if(toS.getDhqName()==null){
                chekUnit.setUnitDhq("");
            }else{
                chekUnit.setUnitDhq(toS.getDhqName());
            }
            chekUnit.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cgUnitRepository.save(chekUnit);
        }


        String authorityId = HelperUtils.getAuthorityId();


        Authority authority = new Authority();
        authority.setAuthority(req.getAuthority());
        authority.setAuthorityId(authorityId);
        authority.setAuthDate(ConverterUtils.convertDateTotimeStamp(req.getAuthDate()));
        authority.setDocId(req.getAuthDocId());
        authority.setAuthUnit(req.getAuthUnitId());
        authority.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        authority.setAuthGroupId(universalAuthGroupForRebase);
        authority.setRemarks(req.getRemark());
        authority.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        authorityRepository.save(authority);

/*
        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("UNIT REBASE");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrDataCheck.getUnitId());
        mangeInboxOutbox.setFromUnit(hrDataCheck.getUnitId());
        mangeInboxOutbox.setApproverpId("");
        mangeInboxOutbox.setType(chekUnit.getDescr());
        mangeInboxOutbox.setRoleId(hrDataCheck.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrDataCheck.getPid());
        mangeInboxOutbox.setStatus("Approved");
        mangeInboxOutbox.setState("CR");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setAllocationType(req.getUnitRebaseRequests().get(0).getAllocationTypeId());
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setIsRevision(0);
        mangeInboxOutbox.setIsBgcg("RR");
        mangeInboxOutbox.setIsRebase("1");
        mangeInboxOutbox.setGroupId(universalAuthGroupForRebase);
        mangeInboxOutBoxRepository.save(mangeInboxOutbox);
*/

        MangeInboxOutbox mangeInboxOutbox2 = new MangeInboxOutbox();
        mangeInboxOutbox2.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox2.setRemarks("UNIT REBASE");
        mangeInboxOutbox2.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox2.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox2.setToUnit(HelperUtils.HEADUNITID);
        mangeInboxOutbox2.setFromUnit(hrDataCheck.getUnitId());
        mangeInboxOutbox2.setApproverpId("");
        mangeInboxOutbox2.setType(chekUnit.getDescr());
        mangeInboxOutbox2.setRoleId(hrDataCheck.getRoleId());
        mangeInboxOutbox2.setCreaterpId(hrDataCheck.getPid());
        mangeInboxOutbox2.setStatus("Approved");
        mangeInboxOutbox2.setState("CR");
        mangeInboxOutbox2.setIsArchive("0");
        mangeInboxOutbox2.setIsApproved("0");
        mangeInboxOutbox2.setIsFlag("1");
        mangeInboxOutbox2.setAllocationType(req.getUnitRebaseRequests().get(0).getAllocationTypeId());
        mangeInboxOutbox2.setIsRevision(0);
        mangeInboxOutbox2.setIsBgcg("RR");
        mangeInboxOutbox2.setIsRebase("1");
        mangeInboxOutbox2.setGroupId(universalAuthGroupForRebase);
        mangeInboxOutBoxRepository.save(mangeInboxOutbox2);


        List<CdaParkingTrans> selfCdaSieze = cdaParkingTransRepository.findByFinYearIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getRebaseUnitId(), req.getUnitRebaseRequests().get(0).getAllocationTypeId(), "0");
        for (Integer i = 0; i < selfCdaSieze.size(); i++) {
            CdaParkingTrans cdaParking = selfCdaSieze.get(i);
            cdaParking.setIsFlag("1");
            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingTransRepository.save(cdaParking);
        }

        List<CdaParkingCrAndDr> selfDrCrCdaSieze = parkingCrAndDrRepository.findByFinYearIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(req.getFinYear(), "0", req.getUnitRebaseRequests().get(0).getAllocationTypeId(), req.getRebaseUnitId(), 0);
        for (Integer i = 0; i < selfDrCrCdaSieze.size(); i++) {
            CdaParkingCrAndDr cdaParking = selfDrCrCdaSieze.get(i);
            cdaParking.setIsFlag("1");
            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            parkingCrAndDrRepository.save(cdaParking);
        }


        String rebaseUnitAllocFromU = "";


        List<BudgetAllocation> selftAllocSieze = (budgetAllocationRepository.findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(req.getRebaseUnitId(), req.getFinYear(), req.getUnitRebaseRequests().get(0).getAllocationTypeId(), "0", "0", "Approved"));
        for (Integer i = 0; i < selftAllocSieze.size(); i++) {
            BudgetAllocation allocData = selftAllocSieze.get(i);
            rebaseUnitAllocFromU = allocData.getFromUnit();
            allocData.setIsFlag("1");
            allocData.setIsTYpe("REBASE");
            allocData.setIsBudgetRevision("1");
            allocData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationRepository.save(allocData);
        }


        List<BudgetAllocationDetails> selfAllocDetailSieze = (budgetAllocationDetailsRepository.findByToUnitAndFinYearAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(req.getRebaseUnitId(), req.getFinYear(), req.getUnitRebaseRequests().get(0).getAllocationTypeId(), "0", "0"));
        for (Integer i = 0; i < selfAllocDetailSieze.size(); i++) {
            BudgetAllocationDetails allocDatatails = selfAllocDetailSieze.get(i);
            allocDatatails.setIsDelete("1");
            allocDatatails.setIsTYpe("REBASE");
            allocDatatails.setIsBudgetRevision("1");
            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocDatatails);
        }


        int count = 0;
        for (Integer k = 0; k < req.getUnitRebaseRequests().size(); k++) {
            AmountUnit amtObj = amountUnitRepository.findByAmountTypeId(req.getUnitRebaseRequests().get(k).getAmountType());
            double allAmountUnit = amtObj.getAmount();
            double allocAmount = Double.parseDouble(req.getUnitRebaseRequests().get(k).getAllocAmount());
            double expAmount = Double.parseDouble(req.getUnitRebaseRequests().get(k).getExpAmount());
            double shipAllocAmount = allocAmount * allAmountUnit;
            double shipExpAmount = Double.parseDouble( 0+"");
            BudgetRebase budgetRebase = new BudgetRebase();
            budgetRebase.setBudgetRebaseId(HelperUtils.getUnitRebased());
            budgetRebase.setRefTransId(HelperUtils.getTransId());
            budgetRebase.setFinYear(req.getFinYear());
            budgetRebase.setAuthGrpId(universalAuthGroupForRebase);
            budgetRebase.setRebaseUnitId(req.getRebaseUnitId());
            budgetRebase.setHeadUnitId(frmUnit);
            budgetRebase.setFrmStationId(req.getFrmStationId());
            budgetRebase.setToStationId(req.getToStationId());
            budgetRebase.setToHeadUnitId(toHdUnitId);
            budgetRebase.setAllocFromUnit(frmUnit);
            budgetRebase.setRemCdaBal(req.getUnitRebaseRequests().get(k).getBalAmount());
            budgetRebase.setOccuranceDate(ConverterUtils.convertDateTotimeStamp(req.getOccurrenceDate()));
            budgetRebase.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
            budgetRebase.setBudgetHeadId(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
            budgetRebase.setAllocAmount(req.getUnitRebaseRequests().get(k).getAllocAmount());
            budgetRebase.setExpAmount(req.getUnitRebaseRequests().get(k).getExpAmount());
            budgetRebase.setBalAmount(req.getUnitRebaseRequests().get(k).getBalAmount());
            budgetRebase.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
            if (req.getUnitRebaseRequests().get(k).getLastCbDate() != null)
                budgetRebase.setLastCbDate(ConverterUtils.convertDateTotimeStamp(req.getUnitRebaseRequests().get(k).getLastCbDate()));
            else
                budgetRebase.setLastCbDate(null);
            budgetRebase.setAuthorityId(authorityId);
            budgetRebase.setUserId(hrDataCheck.getPid());
            budgetRebase.setLoginUnit(hrDataCheck.getUnitId());
            budgetRebase.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetRebase.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetRebaseRepository.save(budgetRebase);

            if (!frmRegion.equalsIgnoreCase(toRegion)) {

                CgUnit frmHead = cgUnitRepository.findByUnit(frmUnit);
                if (frmHead.getIsRebaseAuthority().equalsIgnoreCase("1")) {
                    String dBudgetUnit = "";


                    //............................RHQ REBASE BAL DEDUCT FROM ALLOCATION..............................

                    List<BudgetAllocationDetails> frmRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(frmUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> frmRhqDtls = frmRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    if (frmRhqDtls.size() > 0) {
                        for (Integer i = 0; i < frmRhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = frmRhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt - unloAmnt) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);
                        }
                    }
                    List<BudgetAllocation> frmRhqAllocs = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0", "Approved");
                    for (Integer i = 0; i < frmRhqAllocs.size(); i++) {
                        BudgetAllocation alloc = frmRhqAllocs.get(i);
                        AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                        double AmtUnit = amountType.getAmount();
                        double unloAmnt = shipAllocAmount / AmtUnit;
                        double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                        alloc.setIsFlag("0");
                        alloc.setIsBudgetRevision("0");
                        alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        alloc.setAllocationAmount((allocAmt - unloAmnt) + "");
                        BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                    }


                    List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
                    double totalAllocationAmount = shipAllocAmount;
                    for (Integer i = 0; i < frmRhqCda.size(); i++) {
                        CdaParkingTrans cdaParking = frmRhqCda.get(i);
                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        double remaningAmount = Double.parseDouble(cdaParking.getRemainingCdaAmount()) * amountUnit.getAmount();

                        double ghataSkateHai = allocatedAmount - remaningAmount;
                        if (ghataSkateHai == 0) {
                            continue;
                        }

                        double remeningCdaAMount = 0;
                        if (totalAllocationAmount >= ghataSkateHai) {
                            remeningCdaAMount = allocatedAmount - ghataSkateHai;
                            totalAllocationAmount = totalAllocationAmount - ghataSkateHai;
                        } else {
                            remeningCdaAMount = allocatedAmount - totalAllocationAmount;
                            totalAllocationAmount = 0;
                        }

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint(remeningCdaAMount / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }


                    if (count == 0) {

                        MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                        afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                        afterRebaseNotification.setRemarks("UNIT REBASE");
                        afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setToUnit(frmUnit);
                        afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                        afterRebaseNotification.setFromUnit(HelperUtils.HEADUNITID);
                        afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                        afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                        afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                        afterRebaseNotification.setStatus("Approved");
                        afterRebaseNotification.setState("CR");
                        afterRebaseNotification.setIsArchive("0");
                        afterRebaseNotification.setIsApproved("0");
                        afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        afterRebaseNotification.setIsFlag("1");
                        afterRebaseNotification.setType(chekUnit.getDescr());
                        afterRebaseNotification.setAmount("");
                        afterRebaseNotification.setIsBgcg("RR");
                        afterRebaseNotification.setIsRevision(0);
                        afterRebaseNotification.setIsRebase("1");

                        mangeInboxOutBoxRepository.save(afterRebaseNotification);
                    }


                } else {
                    String rhqUnit = "";

                    ///............................DHQ REBASE BAL DEDUCT FROM ALLOCATION..............................
                    List<BudgetAllocationDetails> frmHdUnitDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(frmUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> frmHdUnitDtls = frmHdUnitDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());

                    if (frmHdUnitDtls.size() > 0) {
                        for (Integer i = 0; i < frmHdUnitDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = frmHdUnitDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setAllocationAmount((allocAmt - unloAmnt) + "");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

                        }
                    }
                    List<BudgetAllocation> frmHdUnitAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");

                    for (Integer i = 0; i < frmHdUnitAlloc.size(); i++) {
                        BudgetAllocation alloc = frmHdUnitAlloc.get(i);
                        rhqUnit = alloc.getFromUnit();
                        AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                        double AmtUnit = amountType.getAmount();
                        double unloAmnt = shipAllocAmount / AmtUnit;
                        double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                        alloc.setIsFlag("0");
                        alloc.setAllocationAmount((allocAmt - unloAmnt) + "");
                        alloc.setIsBudgetRevision("0");
                        alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                    }
                    List<CdaParkingTrans> frmHdUnitCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    double totalAllocationAmount = shipAllocAmount;
                    for (Integer i = 0; i < frmHdUnitCda.size(); i++) {
                        CdaParkingTrans cdaParking = frmHdUnitCda.get(i);

                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        double remaningAmount = Double.parseDouble(cdaParking.getRemainingCdaAmount()) * amountUnit.getAmount();

                        double ghataSkateHai = allocatedAmount - remaningAmount;
                        if (ghataSkateHai == 0) {
                            continue;
                        }

                        double remeningCdaAMount = 0;
                        if (totalAllocationAmount >= ghataSkateHai) {
                            remeningCdaAMount = allocatedAmount - ghataSkateHai;
                            totalAllocationAmount = totalAllocationAmount - ghataSkateHai;
                        } else {
                            remeningCdaAMount = allocatedAmount - totalAllocationAmount;
                            totalAllocationAmount = 0;
                        }

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint(remeningCdaAMount / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }


                    if (count == 0) {
                        MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                        afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                        afterRebaseNotification.setRemarks("Unit Rebase");
                        afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setToUnit(frmUnit);
                        afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                        afterRebaseNotification.setFromUnit(rhqUnit);
                        afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                        afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                        afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                        afterRebaseNotification.setStatus("Fully Approved");
                        afterRebaseNotification.setState("CR");
                        afterRebaseNotification.setIsArchive("0");
                        afterRebaseNotification.setIsApproved("1");
                        afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        afterRebaseNotification.setIsFlag("0");
                        afterRebaseNotification.setType(chekUnit.getDescr());
                        afterRebaseNotification.setAmount("");
                        afterRebaseNotification.setIsBgcg("RR");
                        afterRebaseNotification.setIsRevision(0);
                        afterRebaseNotification.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(afterRebaseNotification);
                    }


                    ///............................RHQ REBASE BAL DEDUCT FROM ALLOCATION..................................

                    List<BudgetAllocationDetails> frmRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(rhqUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> frmRhqDtls = frmRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());

                    for (Integer i = 0; i < frmRhqDtls.size(); i++) {
                        BudgetAllocationDetails allocDatatails = frmRhqDtls.get(i);
                        AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                        double AmtUnit = amountType.getAmount();
                        double unloAmnt = shipAllocAmount / AmtUnit;
                        double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                        allocDatatails.setAllocationAmount((allocAmt - unloAmnt) + "");
                        allocDatatails.setIsDelete("0");
                        allocDatatails.setIsBudgetRevision("0");
                        allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

                    }

                    List<BudgetAllocation> frmRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rhqUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    for (Integer i = 0; i < frmRhqAlloc.size(); i++) {
                        BudgetAllocation alloc = frmRhqAlloc.get(i);
                        AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                        double AmtUnit = amountType.getAmount();
                        double unloAmnt = shipAllocAmount / AmtUnit;
                        double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                        alloc.setIsFlag("0");
                        alloc.setAllocationAmount((allocAmt - unloAmnt) + "");
                        alloc.setIsBudgetRevision("0");
                        alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                    }
                    List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rhqUnit, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    double totalAllocationAmount11 = shipAllocAmount;
                    for (Integer i = 0; i < frmRhqCda.size(); i++) {
                        CdaParkingTrans cdaParking = frmRhqCda.get(i);

                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        double remaningAmount = Double.parseDouble(cdaParking.getRemainingCdaAmount()) * amountUnit.getAmount();

                        double ghataSkateHai = allocatedAmount - remaningAmount;
                        if (ghataSkateHai == 0) {
                            continue;
                        }

                        double remeningCdaAMount = 0;
                        if (totalAllocationAmount11 >= ghataSkateHai) {
                            remeningCdaAMount = allocatedAmount - ghataSkateHai;
                            totalAllocationAmount11 = totalAllocationAmount11 - ghataSkateHai;
                        } else {
                            remeningCdaAMount = allocatedAmount - totalAllocationAmount11;
                            totalAllocationAmount11 = 0;
                        }

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint(remeningCdaAMount / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }


                    if (count == 0) {
                        MangeInboxOutbox afterRebaseNotification123 = new MangeInboxOutbox();
                        afterRebaseNotification123.setMangeInboxId(HelperUtils.getMangeInboxId());
                        afterRebaseNotification123.setRemarks("Unit Rebase");
                        afterRebaseNotification123.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification123.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification123.setToUnit(rhqUnit);
                        afterRebaseNotification123.setGroupId(universalAuthGroupForRebase);
                        afterRebaseNotification123.setFromUnit(HelperUtils.HEADUNITID);
                        afterRebaseNotification123.setRoleId(hrDataCheck.getRoleId());
                        afterRebaseNotification123.setCreaterpId(hrDataCheck.getPid());
                        afterRebaseNotification123.setApproverpId(hrDataCheck.getPid());
                        afterRebaseNotification123.setStatus("Fully Approved");
                        afterRebaseNotification123.setState("CR");
                        afterRebaseNotification123.setIsArchive("0");
                        afterRebaseNotification123.setIsApproved("1");
                        afterRebaseNotification123.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        afterRebaseNotification123.setIsFlag("0");
                        afterRebaseNotification123.setType(chekUnit.getDescr());
                        afterRebaseNotification123.setAmount("");
                        afterRebaseNotification123.setIsBgcg("RR");
                        afterRebaseNotification123.setIsRevision(0);
                        afterRebaseNotification123.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(afterRebaseNotification123);
                    }
                }


                // 2 Side..................... BAL ALLOCATION HERE AS RECEPIET..................................

                CgUnit toHead = cgUnitRepository.findByUnit(toHdUnitId);
                if (toHead.getIsRebaseAuthority().equalsIgnoreCase("1")) {

                    // 2 Side..................... RHQ BAL  ALLOCATION HERE AS RECEPIET..................................

                    List<BudgetAllocationDetails> toRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> toRhqDtls = toRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    String budgetReciptAuthGroupId = HelperUtils.getAuthorityGroupId();
                    if (toRhqDtls.size() > 0) {
                        for (Integer i = 0; i < toRhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = toRhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt + unloAmnt) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

                        }
                    } else {
                        // .....................CREATE BUDGET RECIPT..FOR RHQ................................
                        BudgetAllocationDetails budgetRecipt = new BudgetAllocationDetails();
                        budgetRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetRecipt.setFinYear(req.getFinYear());
                        budgetRecipt.setFromUnit(HelperUtils.HEADUNITID);
                        budgetRecipt.setToUnit(toHdUnitId);
                        budgetRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetRecipt.setStatus("Approved");
                        budgetRecipt.setIsTYpe("REBASE");
                        budgetRecipt.setUnallocatedAmount("0");
                        budgetRecipt.setIsBudgetRevision("0");
                        budgetRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAuthGroupId(budgetReciptAuthGroupId);
                        budgetRecipt.setRemarks("");
                        budgetRecipt.setPurposeCode("");
                        budgetRecipt.setRevisedAmount("0.0000");
                        budgetRecipt.setIsDelete("0");
                        budgetRecipt.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetRecipt.setUserId(hrDataCheck.getPid());
                        budgetRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetRecipt.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
                        budgetAllocationDetailsRepository.save(budgetRecipt);

                    }

                    List<BudgetAllocation> toRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    if (toRhqAlloc.size() > 0) {
                        for (Integer i = 0; i < toRhqAlloc.size(); i++) {
                            BudgetAllocation alloc = toRhqAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double amountu = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / amountu;
                            double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("0");

//                            List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
//                            if (frmRhqCda.size() == 0) {
//                                alloc.setUnallocatedAmount("-" + unloAmnt);
//                            } else {
                                alloc.setUnallocatedAmount("0");
//                            }

                            alloc.setAllocationAmount((allocAmt + unloAmnt) + "");
                            alloc.setIsBudgetRevision("0");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                        }

                    } else {
                        // .....................CREATE BUDGET RECIPT.....FOR RHQ.............................
                        BudgetAllocation budgetAllocationRecipt = new BudgetAllocation();
                        budgetAllocationRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationRecipt.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationRecipt.setFinYear(req.getFinYear());
                        budgetAllocationRecipt.setToUnit(toHdUnitId);
                        budgetAllocationRecipt.setFromUnit(HelperUtils.HEADUNITID);
                        budgetAllocationRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationRecipt.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationRecipt.setUnallocatedAmount("0");
                        budgetAllocationRecipt.setIsFlag("0");

                        budgetAllocationRecipt.setIsTYpe("REBASE");
                        budgetAllocationRecipt.setIsBudgetRevision("0");
                        budgetAllocationRecipt.setRevisedAmount("0");
                        budgetAllocationRecipt.setUserId(hrDataCheck.getPid());
                        budgetAllocationRecipt.setStatus("Approved");
                        budgetAllocationRecipt.setAuthGroupId(budgetReciptAuthGroupId);
                        budgetAllocationRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationRepository.save(budgetAllocationRecipt);

                    }

//                           Add  CDA Add karke Phir Minus karna hai
                    List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    if (frmRhqCda.size() > 0) {
                        CdaParkingTrans cdaParking = frmRhqCda.get(0);
                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint((allocatedAmount + shipAllocAmount) / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }

                    BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    if (count == 0) {
                        MangeInboxOutbox mangeInboxOutboxAllocation = new MangeInboxOutbox();
                        mangeInboxOutboxAllocation.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxAllocation.setRemarks("UNIT REBASE");
                        mangeInboxOutboxAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setToUnit(toHdUnitId);
                        mangeInboxOutboxAllocation.setFromUnit(HelperUtils.HEADUNITID);
                        mangeInboxOutboxAllocation.setGroupId(universalAuthGroupForRebase);
                        mangeInboxOutboxAllocation.setType(chekUnit.getDescr());
                        mangeInboxOutboxAllocation.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxAllocation.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxAllocation.setState("CR");
                        mangeInboxOutboxAllocation.setApproverpId("");
                        mangeInboxOutboxAllocation.setIsFlag("1");
                        mangeInboxOutboxAllocation.setIsArchive("0");
                        mangeInboxOutboxAllocation.setIsApproved("0");
                        mangeInboxOutboxAllocation.setIsRevision(0);
                        mangeInboxOutboxAllocation.setStatus("Approved");
                        mangeInboxOutboxAllocation.setIsBgcg("RR");
                        mangeInboxOutboxAllocation.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxAllocation);
                    }
                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................


                    BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                    budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocationDetails.setFinYear(req.getFinYear());
                    budgetAllocationDetails.setFromUnit(toHdUnitId);
                    budgetAllocationDetails.setToUnit(req.getRebaseUnitId());
                    budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocationDetails.setStatus("Approved");
                    budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAuthGroupId(universalAuthGroup);
                    budgetAllocationDetails.setIsDelete("0");
                    budgetAllocationDetails.setIsTYpe("REBASE");
                    budgetAllocationDetails.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocationDetails.setIsBudgetRevision("0");
                    budgetAllocationDetails.setRevisedAmount("0.0000");
                    budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                    budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                    budgetAllocationDetailsRepository.save(budgetAllocationDetails);

                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setIsTYpe("REBASE");
                    budgetAllocation.setIsBudgetRevision("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setFinYear(req.getFinYear());
                    budgetAllocation.setToUnit(req.getRebaseUnitId());
                    budgetAllocation.setFromUnit(toHdUnitId);
                    budgetAllocation.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocation.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocation.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocation.setRevisedAmount("0");
                    budgetAllocation.setUserId(hrDataCheck.getPid());
                    budgetAllocation.setStatus("Approved");
                    budgetAllocation.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocation.setAuthGroupId(universalAuthGroup);
                    budgetAllocationRepository.save(budgetAllocation);

                    if (count == 0) {
                        MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                        mangeInboxOutboxReciptMsg.setIsRebase("0");
                        mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                        mangeInboxOutboxReciptMsg.setFromUnit(hrDataCheck.getUnitId());
                        mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                        mangeInboxOutboxReciptMsg.setState("CR");
                        mangeInboxOutboxReciptMsg.setIsArchive("0");
                        mangeInboxOutboxReciptMsg.setIsApproved("0");
                        mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        mangeInboxOutboxReciptMsg.setIsFlag("0");
                        mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                        mangeInboxOutboxReciptMsg.setIsRevision(0);
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);
                    }


                    if (count == 0) {
                        MangeInboxOutbox mangeInboxOutboxAllocation = new MangeInboxOutbox();
                        mangeInboxOutboxAllocation.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxAllocation.setRemarks("UNIT REBASE");
                        mangeInboxOutboxAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxAllocation.setFromUnit(toHdUnitId);
                        mangeInboxOutboxAllocation.setGroupId(universalAuthGroupForRebase);
                        mangeInboxOutboxAllocation.setType(chekUnit.getDescr());
                        mangeInboxOutboxAllocation.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxAllocation.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxAllocation.setState("CR");
                        mangeInboxOutboxAllocation.setApproverpId("");
                        mangeInboxOutboxAllocation.setIsFlag("1");
                        mangeInboxOutboxAllocation.setIsArchive("0");
                        mangeInboxOutboxAllocation.setIsApproved("0");
                        mangeInboxOutboxAllocation.setIsRevision(0);
                        mangeInboxOutboxAllocation.setStatus("Approved");
                        mangeInboxOutboxAllocation.setIsBgcg("RR");
                        mangeInboxOutboxAllocation.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxAllocation);
                    }
                } else {
                    CgUnit dHqUnit = cgUnitRepository.findByUnit(toHdUnitId);
                    String rHqUnitIdMain = "";
                    if (dHqUnit != null) {
                        rHqUnitIdMain = dHqUnit.getSubUnit();
                    }
                    //----------------------------TO RHQ RECIEPT .......................................

                    List<BudgetAllocationDetails> toRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(rHqUnitIdMain, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> toRhqDtls = toRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    String budgetReciptAuthGroupId = HelperUtils.getAuthorityGroupId();
                    String budgetAllocationAuthGroupId = HelperUtils.getAuthorityGroupId();
                    if (toRhqDtls.size() > 0) {
                        for (Integer i = 0; i < toRhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = toRhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt + unloAmnt) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);
                        }
                    } else {
                        // .....................CREATE BUDGET RECIPT..FOR RHQ................................
                        BudgetAllocationDetails budgetRecipt = new BudgetAllocationDetails();
                        budgetRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetRecipt.setFinYear(req.getFinYear());
                        budgetRecipt.setFromUnit(HelperUtils.HEADUNITID);
                        budgetRecipt.setToUnit(rHqUnitIdMain);
                        budgetRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetRecipt.setStatus("Approved");
                        budgetRecipt.setIsTYpe("REBASE");
                        budgetRecipt.setUnallocatedAmount("0");
                        budgetRecipt.setIsBudgetRevision("0");
                        budgetRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAuthGroupId(budgetReciptAuthGroupId);
                        budgetRecipt.setRemarks("");
                        budgetRecipt.setPurposeCode("");
                        budgetRecipt.setRevisedAmount("0.0000");
                        budgetRecipt.setIsDelete("0");
                        budgetRecipt.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetRecipt.setUserId(hrDataCheck.getPid());
                        budgetRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetRecipt.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
                        budgetAllocationDetailsRepository.save(budgetRecipt);

                    }


                    List<BudgetAllocation> toRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rHqUnitIdMain, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    if (toRhqAlloc.size() > 0) {
                        for (Integer i = 0; i < toRhqAlloc.size(); i++) {
                            BudgetAllocation alloc = toRhqAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double amountu = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / amountu;
                            double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
                            alloc.setAllocationAmount((allocAmt + unloAmnt) + "");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rHqUnitIdMain, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
//                            if (frmRhqCda.size() == 0) {
//                                alloc.setUnallocatedAmount("-" + unloAmnt);
//                            } else {
                                alloc.setUnallocatedAmount("0");
//                            }

                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                        }

                    } else {

                        // .....................CREATE BUDGET RECIPT..FOR RHQ................................
                        BudgetAllocation budgetAllocationReciptMain = new BudgetAllocation();
                        budgetAllocationReciptMain.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationReciptMain.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationReciptMain.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationReciptMain.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationReciptMain.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationReciptMain.setFinYear(req.getFinYear());
                        budgetAllocationReciptMain.setToUnit(rHqUnitIdMain);
                        budgetAllocationReciptMain.setFromUnit(HelperUtils.HEADUNITID);
                        budgetAllocationReciptMain.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationReciptMain.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationReciptMain.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationReciptMain.setUnallocatedAmount("0");
                        budgetAllocationReciptMain.setIsFlag("0");
                        budgetAllocationReciptMain.setIsTYpe("REBASE");
                        budgetAllocationReciptMain.setIsBudgetRevision("0");
                        budgetAllocationReciptMain.setRevisedAmount("0");
                        budgetAllocationReciptMain.setUserId(hrDataCheck.getPid());
                        budgetAllocationReciptMain.setStatus("Approved");
                        budgetAllocationReciptMain.setAuthGroupId(budgetReciptAuthGroupId);
                        budgetAllocationReciptMain.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationRepository.save(budgetAllocationReciptMain);

                    }

//                      Add  CDA Add karke Phir Minus karna hai
                    List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rHqUnitIdMain, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    if (frmRhqCda.size() > 0) {
                        CdaParkingTrans cdaParking = frmRhqCda.get(0);
                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint((allocatedAmount + shipAllocAmount) / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }

                    if (count == 0) {
                        MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                        afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                        afterRebaseNotification.setRemarks("UNIT REBASE");
                        afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setToUnit(rHqUnitIdMain);
                        afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                        afterRebaseNotification.setFromUnit(HelperUtils.HEADUNITID);
                        afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                        afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                        afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                        afterRebaseNotification.setStatus("Approved");
                        afterRebaseNotification.setState("CR");
                        afterRebaseNotification.setIsArchive("0");
                        afterRebaseNotification.setIsApproved("0");
                        afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        afterRebaseNotification.setIsFlag("1");
                        afterRebaseNotification.setType(chekUnit.getDescr());
                        afterRebaseNotification.setAmount("");
                        afterRebaseNotification.setIsBgcg("RR");
                        afterRebaseNotification.setIsRevision(0);
                        afterRebaseNotification.setIsRebase("1");

                        mangeInboxOutBoxRepository.save(afterRebaseNotification);
                    }

                    //----------------------------CREATE BUDGET ALLOCATION...RHQ TO DHQ. .......................................

                    List<BudgetAllocationDetails> toDhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> toDhqDtls = toDhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    String budgetAllocationAuthGroupIds = HelperUtils.getAuthorityGroupId();
                    if (toDhqDtls.size() > 0) {
                        for (Integer i = 0; i < toDhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = toDhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt + unloAmnt) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);
                        }
                    }else{
                        // .....................CREATE BUDGET ALLOCATION...RHQ TO DHQ...............................
                        BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                        budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationDetails.setFinYear(req.getFinYear());
                        budgetAllocationDetails.setFromUnit(rHqUnitIdMain);
                        budgetAllocationDetails.setToUnit(toHdUnitId);
                        budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationDetails.setStatus("Approved");
                        budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAuthGroupId(budgetAllocationAuthGroupId);
                        budgetAllocationDetails.setIsDelete("0");
                        budgetAllocationDetails.setIsTYpe("REBASE");
                        budgetAllocationDetails.setUnallocatedAmount("0");
                        budgetAllocationDetails.setIsBudgetRevision("0");
                        budgetAllocationDetails.setRevisedAmount("0.0000");
                        budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                        budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                        budgetAllocationDetailsRepository.save(budgetAllocationDetails);
                    }

                    List<BudgetAllocation> toDhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    if (toDhqAlloc.size() > 0) {
                        for (Integer i = 0; i < toDhqAlloc.size(); i++) {
                            BudgetAllocation alloc = toDhqAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double amountu = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / amountu;
                            double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
                            alloc.setAllocationAmount((allocAmt + unloAmnt) + "");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//                            List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
//                            if (frmRhqCda.size() == 0) {
//                                alloc.setUnallocatedAmount("-" + unloAmnt);
//                            } else {
                                alloc.setUnallocatedAmount("0");
//                            }

                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);
                        }

                    } else {
                        // .....................CREATE RECEIPT ALLOCATION. RHQ TO DHQ.................................
                        BudgetAllocation budgetAllocationMain = new BudgetAllocation();
                        budgetAllocationMain.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationMain.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationMain.setIsFlag("0");
                        budgetAllocationMain.setIsTYpe("REBASE");
                        budgetAllocationMain.setIsBudgetRevision("0");
                        budgetAllocationMain.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationMain.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationMain.setFinYear(req.getFinYear());
                        budgetAllocationMain.setToUnit(toHdUnitId);
                        budgetAllocationMain.setFromUnit(rHqUnitIdMain);
                        budgetAllocationMain.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationMain.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationMain.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationMain.setUnallocatedAmount("");
                        budgetAllocationMain.setRevisedAmount("0");
                        budgetAllocationMain.setUserId(hrDataCheck.getPid());
                        budgetAllocationMain.setStatus("Approved");
                        budgetAllocationMain.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationMain.setAuthGroupId(budgetAllocationAuthGroupId);
                        budgetAllocationRepository.save(budgetAllocationMain);
                    }


//                      Add  CDA Add karke Phir Minus karna hai
                    List<CdaParkingTrans> toDhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    if (toDhqCda.size() > 0) {
                        CdaParkingTrans cdaParking = toDhqCda.get(0);
                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint((allocatedAmount + shipAllocAmount) / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }
                    if (count == 0) {
                        MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                        afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                        afterRebaseNotification.setRemarks("UNIT REBASE");
                        afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setToUnit(toHdUnitId);
                        afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                        afterRebaseNotification.setFromUnit(rHqUnitIdMain);
                        afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                        afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                        afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                        afterRebaseNotification.setStatus("Approved");
                        afterRebaseNotification.setState("CR");
                        afterRebaseNotification.setIsArchive("0");
                        afterRebaseNotification.setIsApproved("0");
                        afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        afterRebaseNotification.setIsFlag("1");
                        afterRebaseNotification.setType(chekUnit.getDescr());
                        afterRebaseNotification.setAmount("0");
                        afterRebaseNotification.setIsBgcg("RR");
                        afterRebaseNotification.setIsRevision(0);
                        afterRebaseNotification.setIsRebase("1");

                        mangeInboxOutBoxRepository.save(afterRebaseNotification);
                    }


                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                    BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                    budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocationDetails.setFinYear(req.getFinYear());
                    budgetAllocationDetails.setFromUnit(toHdUnitId);
                    budgetAllocationDetails.setToUnit(req.getRebaseUnitId());
                    budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocationDetails.setStatus("Approved");
                    budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAuthGroupId(universalAuthGroup);
                    budgetAllocationDetails.setIsDelete("0");
                    budgetAllocationDetails.setIsTYpe("REBASE");
                    budgetAllocationDetails.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocationDetails.setIsBudgetRevision("0");
                    budgetAllocationDetails.setRevisedAmount("0.0000");
                    budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                    budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                    budgetAllocationDetailsRepository.save(budgetAllocationDetails);

                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                    BudgetAllocation budgetAllocation123 = new BudgetAllocation();
                    budgetAllocation123.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation123.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation123.setIsFlag("0");
                    budgetAllocation123.setIsTYpe("REBASE");
                    budgetAllocation123.setIsBudgetRevision("0");
                    budgetAllocation123.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation123.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation123.setFinYear(req.getFinYear());
                    budgetAllocation123.setToUnit(req.getRebaseUnitId());
                    budgetAllocation123.setFromUnit(toHdUnitId);
                    budgetAllocation123.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocation123.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocation123.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocation123.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocation123.setRevisedAmount("0");
                    budgetAllocation123.setUserId(hrDataCheck.getPid());
                    budgetAllocation123.setStatus("Approved");
                    budgetAllocation123.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocation123.setAuthGroupId(universalAuthGroup);
                    budgetAllocationRepository.save(budgetAllocation123);


                    if (count == 0) {

                        BudgetHead budgetHeadIdS = subHeadRepository.findByBudgetCodeId(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        MangeInboxOutbox mangeInboxOutboxAllocation = new MangeInboxOutbox();
                        mangeInboxOutboxAllocation.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxAllocation.setRemarks("UNIT REBASE");
                        mangeInboxOutboxAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxAllocation.setFromUnit(toHdUnitId);
                        mangeInboxOutboxAllocation.setGroupId(universalAuthGroupForRebase);
                        mangeInboxOutboxAllocation.setType(chekUnit.getDescr());
                        mangeInboxOutboxAllocation.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxAllocation.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxAllocation.setState("CR");
                        mangeInboxOutboxAllocation.setApproverpId("");
                        mangeInboxOutboxAllocation.setIsFlag("1");
                        mangeInboxOutboxAllocation.setIsArchive("0");
                        mangeInboxOutboxAllocation.setIsApproved("0");
                        mangeInboxOutboxAllocation.setIsRevision(0);
                        mangeInboxOutboxAllocation.setStatus("Approved");
                        mangeInboxOutboxAllocation.setIsBgcg("RR");
                        mangeInboxOutboxAllocation.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxAllocation);


                        MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                        mangeInboxOutboxReciptMsg.setIsRebase("0");
                        mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                        mangeInboxOutboxReciptMsg.setFromUnit(hrDataCheck.getUnitId());
                        mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                        mangeInboxOutboxReciptMsg.setState("CR");
                        mangeInboxOutboxReciptMsg.setIsArchive("0");
                        mangeInboxOutboxReciptMsg.setIsApproved("0");
                        mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        mangeInboxOutboxReciptMsg.setIsFlag("0");
                        mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                        mangeInboxOutboxReciptMsg.setIsRevision(0);
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);
                    }
                }
            } else {
                //............................SAME REGION..............................

                CgUnit rebaseUnitAllocFromUnits = cgUnitRepository.findByUnit(rebaseUnitAllocFromU);
                if (rebaseUnitAllocFromUnits.getIsRebaseAuthority().equalsIgnoreCase("1")) {

                    //  if ship is in same region and also direct in RHQ
                    //  then not possible to rebase the ship to direct in RHQ
                    //  so ship rebase in same region under any DHQ

                    ///............................DHQ  BAL ALLOCATION..............................

                    List<BudgetAllocationDetails> toDhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> toDhqDtls = toDhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    String reciptAuthGroupId = HelperUtils.getAuthorityGroupId();
                    if (toDhqDtls.size() > 0) {
                        for (Integer i = 0; i < toDhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = toDhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt1 = shipAllocAmount / AmtUnit;
                            double allocAmt1 = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt1 + unloAmnt1) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);
                        }
                    } else {
                        // .....................CREATE BUDGET RECIPT..FOR TO DHQ................................
                        BudgetAllocationDetails budgetAllocationDetailsRecipt = new BudgetAllocationDetails();
                        budgetAllocationDetailsRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetailsRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationDetailsRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationDetailsRecipt.setFinYear(req.getFinYear());
                        budgetAllocationDetailsRecipt.setFromUnit(rebaseUnitAllocFromU);
                        budgetAllocationDetailsRecipt.setToUnit(toHdUnitId);
                        budgetAllocationDetailsRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationDetailsRecipt.setStatus("Approved");
                        budgetAllocationDetailsRecipt.setIsTYpe("REBASE");
                        budgetAllocationDetailsRecipt.setUnallocatedAmount("0");
                        budgetAllocationDetailsRecipt.setIsBudgetRevision("0");
                        budgetAllocationDetailsRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setAuthGroupId(reciptAuthGroupId);
                        budgetAllocationDetailsRecipt.setRemarks("");
                        budgetAllocationDetailsRecipt.setPurposeCode("");
                        budgetAllocationDetailsRecipt.setRevisedAmount("0.0000");
                        budgetAllocationDetailsRecipt.setIsDelete("0");
                        budgetAllocationDetailsRecipt.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetailsRecipt.setUserId(hrDataCheck.getPid());
                        budgetAllocationDetailsRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationDetailsRecipt.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
                        budgetAllocationDetailsRepository.save(budgetAllocationDetailsRecipt);
                    }
                    List<BudgetAllocation> toDhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    if (toDhqAlloc.size() > 0) {
                        for (Integer i = 0; i < toDhqAlloc.size(); i++) {
                            BudgetAllocation alloc = toDhqAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double amountu = amountType.getAmount();
                            double unloAmnt0 = shipAllocAmount / amountu;
                            double allocAmt0 = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
//                            alloc.setAllocationAmount((allocAmt0 + unloAmnt0) + "");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//                            List<CdaParkingTrans> frmHdUnitCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
//                            if (frmHdUnitCda.size() == 0) {
//                                alloc.setUnallocatedAmount("-" + unloAmnt0);
//                            } else {
                                alloc.setUnallocatedAmount("0");
//                            }
                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);
                        }
                    } else {
                        // .....................CREATE BUDGET RECIPT....FOR TO DHQ..............................
                        BudgetAllocation budgetAllocationRecipt = new BudgetAllocation();
                        budgetAllocationRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationRecipt.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationRecipt.setFinYear(req.getFinYear());
                        budgetAllocationRecipt.setToUnit(toHdUnitId);
                        budgetAllocationRecipt.setFromUnit(rebaseUnitAllocFromU);
                        budgetAllocationRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationRecipt.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationRecipt.setUnallocatedAmount("0");
                        budgetAllocationRecipt.setIsFlag("0");
                        budgetAllocationRecipt.setUnallocatedAmount("0");
                        budgetAllocationRecipt.setIsTYpe("REBASE");
                        budgetAllocationRecipt.setIsBudgetRevision("0");
                        budgetAllocationRecipt.setRevisedAmount("0");
                        budgetAllocationRecipt.setUserId(hrDataCheck.getPid());
                        budgetAllocationRecipt.setStatus("Approved");
                        budgetAllocationRecipt.setAuthGroupId(reciptAuthGroupId);
                        budgetAllocationRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationRepository.save(budgetAllocationRecipt);
                    }

                    List<CdaParkingTrans> frmHdUnitCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    if (frmHdUnitCda.size() > 0) {
                        CdaParkingTrans cdaParking = frmHdUnitCda.get(0);
                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint((allocatedAmount + shipAllocAmount) / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }


                    // .....................CREATE BUDGET ALLOCATION Details....FOR SHIP UNIT......................
                    BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                    budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocationDetails.setFinYear(req.getFinYear());
                    budgetAllocationDetails.setFromUnit(toHdUnitId);
                    budgetAllocationDetails.setToUnit(req.getRebaseUnitId());
                    budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocationDetails.setStatus("Approved");
                    budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAuthGroupId(universalAuthGroup);
                    budgetAllocationDetails.setIsDelete("0");
                    budgetAllocationDetails.setIsTYpe("REBASE");
                    budgetAllocationDetails.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocationDetails.setIsBudgetRevision("0");
                    budgetAllocationDetails.setRevisedAmount("0.0000");
                    budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                    budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                    budgetAllocationDetailsRepository.save(budgetAllocationDetails);

                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setIsTYpe("REBASE");
                    budgetAllocation.setIsBudgetRevision("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setFinYear(req.getFinYear());
                    budgetAllocation.setToUnit(req.getRebaseUnitId());
                    budgetAllocation.setFromUnit(toHdUnitId);
                    budgetAllocation.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocation.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocation.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocation.setRevisedAmount("0");
                    budgetAllocation.setUserId(hrDataCheck.getPid());
                    budgetAllocation.setStatus("Approved");
                    budgetAllocation.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocation.setAuthGroupId(universalAuthGroup);
                    budgetAllocationRepository.save(budgetAllocation);

                    if (count == 0) {
                        MangeInboxOutbox mangeInboxOutboxAllocation11111 = new MangeInboxOutbox();
                        mangeInboxOutboxAllocation11111.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxAllocation11111.setRemarks("UNIT REBASE");
                        mangeInboxOutboxAllocation11111.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation11111.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation11111.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxAllocation11111.setFromUnit(toHdUnitId);
                        mangeInboxOutboxAllocation11111.setGroupId(universalAuthGroupForRebase);
                        mangeInboxOutboxAllocation11111.setType(chekUnit.getDescr());
                        mangeInboxOutboxAllocation11111.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxAllocation11111.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxAllocation11111.setState("CR");
                        mangeInboxOutboxAllocation11111.setApproverpId("");
                        mangeInboxOutboxAllocation11111.setIsFlag("1");
                        mangeInboxOutboxAllocation11111.setIsArchive("0");
                        mangeInboxOutboxAllocation11111.setIsApproved("0");
                        mangeInboxOutboxAllocation11111.setIsRevision(0);
                        mangeInboxOutboxAllocation11111.setStatus("Approved");
                        mangeInboxOutboxAllocation11111.setIsBgcg("RR");
                        mangeInboxOutboxAllocation11111.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxAllocation11111);

                    }


                } else {

                    //  ...................from DHQ UNIT  BAL DEDUCION...........................

                    List<BudgetAllocationDetails> frmHdUnitDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(rebaseUnitAllocFromU, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> frmHdUnitDtls = frmHdUnitDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());

                    if (frmHdUnitDtls.size() > 0) {
                        for (Integer i = 0; i < frmHdUnitDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = frmHdUnitDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt - unloAmnt) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

                        }
                    }
                    List<BudgetAllocation> frmHdUnitAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rebaseUnitAllocFromU, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    double allocAmt = 0.0;
                    double unloAmnt = 0.0;
                    if (frmHdUnitAlloc.size() > 0) {
                        for (Integer i = 0; i < frmHdUnitAlloc.size(); i++) {
                            BudgetAllocation alloc = frmHdUnitAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            unloAmnt = shipAllocAmount / AmtUnit;

                            allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("1");
                            alloc.setIsBudgetRevision("1");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            alloc.setAllocationAmount((allocAmt - unloAmnt) + "");
                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                        }
                    }
                    List<CdaParkingTrans> frmHdUnitCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rebaseUnitAllocFromU, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    double totalAllocationAmount11 = shipAllocAmount;
                    if (frmHdUnitCda.size() > 0) {
                        for (Integer i = 0; i < frmHdUnitCda.size(); i++) {
                            CdaParkingTrans cdaParking = frmHdUnitCda.get(i);

                            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                            double remaningAmount = Double.parseDouble(cdaParking.getRemainingCdaAmount()) * amountUnit.getAmount();

                            double ghataSkateHai = allocatedAmount - remaningAmount;
                            if (ghataSkateHai == 0) {
                                continue;
                            }

                            double remeningCdaAMount = 0;
                            if (totalAllocationAmount11 >= ghataSkateHai) {
                                remeningCdaAMount = allocatedAmount - ghataSkateHai;
                                totalAllocationAmount11 = totalAllocationAmount11 - ghataSkateHai;
                            } else {
                                remeningCdaAMount = allocatedAmount - totalAllocationAmount11;
                                totalAllocationAmount11 = 0;
                            }

                            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint(remeningCdaAMount / amountUnit.getAmount() + ""));
                            cdaParkingTransRepository.save(cdaParking);
                        }
                    }


                    //  ...................TO DHQ UNIT BAL ALLOCATION HERE...........................


                    CgUnit toDhq = cgUnitRepository.findByUnit(toHdUnitId);
                    String toDhqHeadUnit = "";
                    if (toDhq != null) {
                        toDhqHeadUnit = toDhq.getSubUnit();
                    }

                    CgUnit isHeadRhq = cgUnitRepository.findByUnit(toHdUnitId);
                    if (isHeadRhq.getIsRebaseAuthority().equalsIgnoreCase("1")) {

                        // .....................CREATE BUDGET ALLOCATION Details....FOR SHIP UNIT......................
                        BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                        budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationDetails.setFinYear(req.getFinYear());
                        budgetAllocationDetails.setFromUnit(toHdUnitId);
                        budgetAllocationDetails.setToUnit(req.getRebaseUnitId());
                        budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationDetails.setStatus("Approved");
                        budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAuthGroupId(universalAuthGroup);
                        budgetAllocationDetails.setIsDelete("0");
                        budgetAllocationDetails.setIsTYpe("REBASE");
                        budgetAllocationDetails.setUnallocatedAmount(shipExpAmount + "");
                        budgetAllocationDetails.setIsBudgetRevision("0");
                        budgetAllocationDetails.setRevisedAmount("0.0000");
                        budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                        budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                        budgetAllocationDetailsRepository.save(budgetAllocationDetails);

                        // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                        BudgetAllocation budgetAllocation = new BudgetAllocation();
                        budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocation.setIsFlag("0");
                        budgetAllocation.setIsTYpe("REBASE");
                        budgetAllocation.setIsBudgetRevision("0");
                        budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocation.setFinYear(req.getFinYear());
                        budgetAllocation.setToUnit(req.getRebaseUnitId());
                        budgetAllocation.setFromUnit(toHdUnitId);
                        budgetAllocation.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocation.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocation.setUnallocatedAmount(shipExpAmount + "");
                        budgetAllocation.setRevisedAmount("0");
                        budgetAllocation.setUserId(hrDataCheck.getPid());
                        budgetAllocation.setStatus("Approved");
                        budgetAllocation.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocation.setAuthGroupId(universalAuthGroup);
                        budgetAllocationRepository.save(budgetAllocation);


                    }else{
                        List<BudgetAllocationDetails> toRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                        List<BudgetAllocationDetails> toRhqDtls = toRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                        String reciptAuthGroupId = HelperUtils.getAuthorityGroupId();
                        String allocationAuthGroupId = HelperUtils.getAuthorityGroupId();
                        if (toRhqDtls.size() > 0) {
                            for (Integer i = 0; i < toRhqDtls.size(); i++) {
                                BudgetAllocationDetails allocDatatails = toRhqDtls.get(i);
                                AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                                double AmtUnit = amountType.getAmount();
                                double unloAmnt1 = shipAllocAmount / AmtUnit;
                                double allocAmt1 = Double.parseDouble(allocDatatails.getAllocationAmount());
                                allocDatatails.setIsDelete("0");
                                allocDatatails.setIsBudgetRevision("0");
                                allocDatatails.setAllocationAmount((allocAmt1 + unloAmnt1) + "");
                                allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

                            }

                        } else {
                            // .....................CREATE BUDGET RECIPT..FOR TO DHQ................................
                            BudgetAllocationDetails budgetAllocationDetailsRecipt = new BudgetAllocationDetails();
                            budgetAllocationDetailsRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                            budgetAllocationDetailsRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                            budgetAllocationDetailsRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetailsRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetailsRecipt.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                            budgetAllocationDetailsRecipt.setFinYear(req.getFinYear());
                            budgetAllocationDetailsRecipt.setFromUnit(toDhqHeadUnit);
                            budgetAllocationDetailsRecipt.setToUnit(toHdUnitId);
                            budgetAllocationDetailsRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                            budgetAllocationDetailsRecipt.setStatus("Approved");
                            budgetAllocationDetailsRecipt.setIsTYpe("REBASE");
                            budgetAllocationDetailsRecipt.setUnallocatedAmount("0");
                            budgetAllocationDetailsRecipt.setIsBudgetRevision("0");
                            budgetAllocationDetailsRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetailsRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetailsRecipt.setAuthGroupId(reciptAuthGroupId);
                            budgetAllocationDetailsRecipt.setRemarks("");
                            budgetAllocationDetailsRecipt.setPurposeCode("");
                            budgetAllocationDetailsRecipt.setRevisedAmount("0.0000");
                            budgetAllocationDetailsRecipt.setIsDelete("0");
                            budgetAllocationDetailsRecipt.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                            budgetAllocationDetailsRecipt.setUserId(hrDataCheck.getPid());
                            budgetAllocationDetailsRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                            budgetAllocationDetailsRecipt.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
                            budgetAllocationDetailsRepository.save(budgetAllocationDetailsRecipt);
                        }


                        List<BudgetAllocation> toRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");

                        if (toRhqAlloc.size() > 0) {
                            for (Integer i = 0; i < toRhqAlloc.size(); i++) {
                                BudgetAllocation alloc = toRhqAlloc.get(i);
                                AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                                double amountu = amountType.getAmount();
                                double unloAmnt0 = shipAllocAmount / amountu;
                                double allocAmt0 = Double.parseDouble(alloc.getAllocationAmount());
                                alloc.setIsFlag("0");
                                alloc.setIsBudgetRevision("0");
                                alloc.setAllocationAmount((allocAmt0 + unloAmnt0) + "");
                                alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//                            List<CdaParkingTrans> frmHdUnitCdaMain = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
//                            if (frmHdUnitCdaMain.size() == 0) {
//                                alloc.setUnallocatedAmount("-" + unloAmnt0);
//                            } else {
                                alloc.setUnallocatedAmount("0");
//                            }
                                BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);
                            }
                        } else {
                            // .....................CREATE BUDGET RECIPT....FOR TO DHQ..............................
                            BudgetAllocation budgetAllocationRecipt = new BudgetAllocation();
                            budgetAllocationRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                            budgetAllocationRecipt.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationRecipt.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                            budgetAllocationRecipt.setFinYear(req.getFinYear());
                            budgetAllocationRecipt.setToUnit(toHdUnitId);
                            budgetAllocationRecipt.setFromUnit(toDhqHeadUnit);
                            budgetAllocationRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                            budgetAllocationRecipt.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                            budgetAllocationRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                            budgetAllocationRecipt.setUnallocatedAmount("0");
                            budgetAllocationRecipt.setIsFlag("0");

                            budgetAllocationRecipt.setIsTYpe("REBASE");
                            budgetAllocationRecipt.setIsBudgetRevision("0");
                            budgetAllocationRecipt.setRevisedAmount("0");
                            budgetAllocationRecipt.setUserId(hrDataCheck.getPid());
                            budgetAllocationRecipt.setStatus("Approved");
                            budgetAllocationRecipt.setAuthGroupId(reciptAuthGroupId);
                            budgetAllocationRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                            budgetAllocationRepository.save(budgetAllocationRecipt);
                        }


                        List<CdaParkingTrans> frmHdUnitCdaMain = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
                        if (frmHdUnitCdaMain.size() > 0) {
                            CdaParkingTrans cdaParking = frmHdUnitCdaMain.get(0);
                            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());
                            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                            cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint((allocatedAmount + shipAllocAmount) / amountUnit.getAmount() + ""));
                            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParkingTransRepository.save(cdaParking);
                        }


                        // .....................CREATE BUDGET ALLOCATION Details....FOR SHIP UNIT......................
                        BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                        budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationDetails.setFinYear(req.getFinYear());
                        budgetAllocationDetails.setFromUnit(toHdUnitId);
                        budgetAllocationDetails.setToUnit(req.getRebaseUnitId());
                        budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationDetails.setStatus("Approved");
                        budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAuthGroupId(universalAuthGroup);
                        budgetAllocationDetails.setIsDelete("0");
                        budgetAllocationDetails.setIsTYpe("REBASE");
                        budgetAllocationDetails.setUnallocatedAmount(shipExpAmount + "");
                        budgetAllocationDetails.setIsBudgetRevision("0");
                        budgetAllocationDetails.setRevisedAmount("0.0000");
                        budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                        budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                        budgetAllocationDetailsRepository.save(budgetAllocationDetails);

                        // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                        BudgetAllocation budgetAllocation = new BudgetAllocation();
                        budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocation.setIsFlag("0");
                        budgetAllocation.setIsTYpe("REBASE");
                        budgetAllocation.setIsBudgetRevision("0");
                        budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocation.setFinYear(req.getFinYear());
                        budgetAllocation.setToUnit(req.getRebaseUnitId());
                        budgetAllocation.setFromUnit(toHdUnitId);
                        budgetAllocation.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocation.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocation.setUnallocatedAmount(shipExpAmount + "");
                        budgetAllocation.setRevisedAmount("0");
                        budgetAllocation.setUserId(hrDataCheck.getPid());
                        budgetAllocation.setStatus("Approved");
                        budgetAllocation.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocation.setAuthGroupId(universalAuthGroup);
                        budgetAllocationRepository.save(budgetAllocation);
                    }



                }


                if (count == 0) {


                    MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                    mangeInboxOutboxReciptMsg.setIsRebase("0");
                    mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                    mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                    mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutboxReciptMsg.setToUnit(req.getRebaseUnitId());
                    mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                    mangeInboxOutboxReciptMsg.setFromUnit(hrDataCheck.getUnitId());
                    mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                    mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                    mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                    mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                    mangeInboxOutboxReciptMsg.setState("CR");
                    mangeInboxOutboxReciptMsg.setIsArchive("0");
                    mangeInboxOutboxReciptMsg.setIsApproved("0");
                    mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    mangeInboxOutboxReciptMsg.setIsFlag("0");
                    mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                    mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                    mangeInboxOutboxReciptMsg.setIsRevision(0);
                    mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);


                    MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                    afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                    afterRebaseNotification.setRemarks("UNIT REBASE");
                    afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    afterRebaseNotification.setToUnit(toHdUnitId);
                    afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                    afterRebaseNotification.setFromUnit(hrDataCheck.getUnitId());
                    afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                    afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                    afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                    afterRebaseNotification.setStatus("Approved");
                    afterRebaseNotification.setState("CR");
                    afterRebaseNotification.setIsArchive("0");
                    afterRebaseNotification.setIsApproved("0");
                    afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    afterRebaseNotification.setIsFlag("1");
                    afterRebaseNotification.setType(chekUnit.getDescr());
                    afterRebaseNotification.setAmount("");
                    afterRebaseNotification.setIsBgcg("RR");
                    afterRebaseNotification.setIsRevision(0);
                    afterRebaseNotification.setIsRebase("1");
                    mangeInboxOutBoxRepository.save(afterRebaseNotification);
                }
                if (count == 0) {
                    MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                    afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                    afterRebaseNotification.setRemarks("UNIT REBASE");
                    afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    afterRebaseNotification.setToUnit(frmUnit);
                    afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                    afterRebaseNotification.setFromUnit(hrDataCheck.getUnitId());
                    afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                    afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                    afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                    afterRebaseNotification.setStatus("Approved");
                    afterRebaseNotification.setState("CR");
                    afterRebaseNotification.setIsArchive("0");
                    afterRebaseNotification.setIsApproved("0");
                    afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    afterRebaseNotification.setIsFlag("1");
                    afterRebaseNotification.setType(chekUnit.getDescr());
                    afterRebaseNotification.setAmount("");
                    afterRebaseNotification.setIsBgcg("RR");
                    afterRebaseNotification.setIsRevision(0);
                    afterRebaseNotification.setIsRebase("1");
                    mangeInboxOutBoxRepository.save(afterRebaseNotification);
                }
            }

            count++;
        }

        defaultResponse.setMsg("UNIT REBASE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> saveUnitRebaseArvind(UnitRebaseSaveReq req) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        DefaultResponse defaultResponse = new DefaultResponse();

        String universalAuthGroup = HelperUtils.getAuthorityGroupId();
        String universalAuthGroupForRebase = HelperUtils.getAuthorityGroupId();


        if (hrDataCheck == null) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS", HttpStatus.OK.value());
        }
        if (req.getAuthority() == null || req.getAuthority().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "AUTHORITY CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getAuthDate() == null || req.getAuthDate().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "AUTHORITY DATE CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getAuthUnitId() == null || req.getAuthUnitId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "AUTHORITY UNIT CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getAuthDocId() == null || req.getAuthDocId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "DOCUMENT ID CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getOccurrenceDate() == null || req.getOccurrenceDate().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "OCCURRENCE DATE CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getFinYear() == null || req.getFinYear().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "FINANCIAL YEAR CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getRebaseUnitId() == null || req.getRebaseUnitId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "REBASE UNIT ID CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getToStationId() == null || req.getToStationId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "TO_STATION ID CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getFrmStationId() == null || req.getFrmStationId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "FROM_STATION ID CAN NOT BE BLANK", HttpStatus.OK.value());
        }
        if (req.getUnitRebaseRequests().size() <= 0) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "YOU CAN'T REBASE WITHOUT ALLOCATION", HttpStatus.OK.value());
        }


        for (Integer m = 0; m < req.getUnitRebaseRequests().size(); m++) {

            if (req.getUnitRebaseRequests().get(m).getAllocAmount() == null || req.getUnitRebaseRequests().get(m).getAllocAmount().isEmpty()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "ALLOCATION AMOUNT CAN NOT BE BLANK", HttpStatus.OK.value());
            }
            if (req.getUnitRebaseRequests().get(m).getExpAmount() == null || req.getUnitRebaseRequests().get(m).getExpAmount().isEmpty()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "EXPEND_AMOUNT CAN NOT BE BLANK", HttpStatus.OK.value());
            }
            if (req.getUnitRebaseRequests().get(m).getBalAmount() == null || req.getUnitRebaseRequests().get(m).getBalAmount().isEmpty()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "BAL_AMOUNT CAN NOT BE BLANK", HttpStatus.OK.value());
            }
            if (req.getUnitRebaseRequests().get(m).getAmountType() == null || req.getUnitRebaseRequests().get(m).getAmountType().isEmpty()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "AMOUNT_TYPE CAN NOT BE BLANK", HttpStatus.OK.value());
            }
            if (req.getUnitRebaseRequests().get(m).getAllocationTypeId() == null || req.getUnitRebaseRequests().get(m).getAllocationTypeId().isEmpty()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "ALLOCATION TYPE ID CAN NOT BE BLANK", HttpStatus.OK.value());
            }
        }


        CgUnit chekUnit = cgUnitRepository.findByUnit(req.getRebaseUnitId());
        String subUnits = chekUnit.getSubUnit();
        CgStation frmS = cgStationRepository.findByStationName(req.getFrmStationId());
        if (frmS == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FROM STATION REGION GETTING NULL");
        }
        CgStation toS = cgStationRepository.findByStationId(req.getToStationId());
        if (toS == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO STATION REGION GETTING NULL");
        }

        String toRegion = toS.getRhqId();
        String tohdUnit = "";
        if (toS.getDhqName() == null || toS.getDhqName().isEmpty()) {
            tohdUnit = toS.getRhqId();
        } else {
            tohdUnit = toS.getDhqName();
        }
        String frmRegion = frmS.getRhqId();
//        String frmhdUnit = "";
//        if (frmS.getDhqName() == null || frmS.getDhqName().isEmpty()) {
//            frmhdUnit = frmS.getRhqId();
//        } else {
//            frmhdUnit = frmS.getDhqName();
//        }
        CgUnit cgData = cgUnitRepository.findByUnit(hrDataCheck.getUnitId());
        String rebaseAuthority = cgData.getIsRebaseAuthority();
        if (!rebaseAuthority.equalsIgnoreCase("1")) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED FOR UNIT REBASE");
        }
        if (!frmRegion.equalsIgnoreCase(toRegion)) {
            if (!hrDataCheck.getUnitId().equalsIgnoreCase("001321"))
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED FOR UNIT REBASE IN ANOTHER REGION");
        }

        CgUnit obj = cgUnitRepository.findByCgUnitShort(tohdUnit);
        if (obj == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO HEAD UNIT NOT FOUND");
        }
        String toHdUnitId = obj.getUnit();
        CgUnit headBudgrpId = cgUnitRepository.findByUnit(toHdUnitId);
        String setBudGroupHdUnit = headBudgrpId.getBudGroupUnit();
        String setBudGroupUnit = setBudGroupHdUnit + "," + toHdUnitId;

        List<BudgetAllocation> allocationData = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(0).getBudgetHeadId(), req.getRebaseUnitId(), req.getFinYear(), req.getUnitRebaseRequests().get(0).getAllocationTypeId(), "0", "0");
        if (allocationData.size() == 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION NOT FOUND FOR THIS UNIT");
        }
        String frmUnit = allocationData.get(0).getFromUnit();


        if (chekUnit == null || chekUnit.getUnit().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID UNIT");
        }
        if (frmS.getStationId().equalsIgnoreCase(req.getToStationId())) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CAN NOT REBASE ON SAME STATION");
        }
        String maxRebaseId = budgetRebaseRepository.findMaxRebaseIDByRebaseUnitId(req.getRebaseUnitId());
        if (maxRebaseId != null) {
            BudgetRebase rebaseData = budgetRebaseRepository.findByBudgetRebaseId(maxRebaseId);
            Date crDate = rebaseData.getCreatedOn();
            Date expireDate = new Date(crDate.getTime() + expirationTime * 1000);
            Date todayDate = new Date();
//            if (expireDate.getTime() >= todayDate.getTime()) {
//                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CAN NOT REBASE SAME UNIT ! TRY AFTER 1 HOURS");
//
//            } else {
            chekUnit.setStationId(req.getToStationId());
            chekUnit.setSubUnit(toHdUnitId);
            chekUnit.setBudGroupUnit(setBudGroupUnit);
            if(toS.getRhqId()==null){
                chekUnit.setUnitRhq("");
            }else{
                chekUnit.setUnitRhq(toS.getRhqId());
            }
            if(toS.getDhqName()==null){
                chekUnit.setUnitDhq("");
            }else{
                chekUnit.setUnitDhq(toS.getDhqName());
            }
            chekUnit.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cgUnitRepository.save(chekUnit);
//            }
        } else {
            chekUnit.setStationId(req.getToStationId());
            chekUnit.setSubUnit(toHdUnitId);
            chekUnit.setBudGroupUnit(setBudGroupUnit);
            if(toS.getRhqId()==null){
                chekUnit.setUnitRhq("");
            }else{
                chekUnit.setUnitRhq(toS.getRhqId());
            }
            if(toS.getDhqName()==null){
                chekUnit.setUnitDhq("");
            }else{
                chekUnit.setUnitDhq(toS.getDhqName());
            }
            chekUnit.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cgUnitRepository.save(chekUnit);
        }


        String authorityId = HelperUtils.getAuthorityId();


        Authority authority = new Authority();
        authority.setAuthority(req.getAuthority());
        authority.setAuthorityId(authorityId);
        authority.setAuthDate(ConverterUtils.convertDateTotimeStamp(req.getAuthDate()));
        authority.setDocId(req.getAuthDocId());
        authority.setAuthUnit(req.getAuthUnitId());
        authority.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        authority.setAuthGroupId(universalAuthGroupForRebase);
        authority.setRemarks(req.getRemark());
        authority.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        authorityRepository.save(authority);

/*
        MangeInboxOutbox mangeInboxOutbox = new MangeInboxOutbox();
        mangeInboxOutbox.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox.setRemarks("UNIT REBASE");
        mangeInboxOutbox.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox.setToUnit(hrDataCheck.getUnitId());
        mangeInboxOutbox.setFromUnit(hrDataCheck.getUnitId());
        mangeInboxOutbox.setApproverpId("");
        mangeInboxOutbox.setType(chekUnit.getDescr());
        mangeInboxOutbox.setRoleId(hrDataCheck.getRoleId());
        mangeInboxOutbox.setCreaterpId(hrDataCheck.getPid());
        mangeInboxOutbox.setStatus("Approved");
        mangeInboxOutbox.setState("CR");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setAllocationType(req.getUnitRebaseRequests().get(0).getAllocationTypeId());
        mangeInboxOutbox.setIsApproved("0");
        mangeInboxOutbox.setIsFlag("1");
        mangeInboxOutbox.setIsRevision(0);
        mangeInboxOutbox.setIsBgcg("RR");
        mangeInboxOutbox.setIsRebase("1");
        mangeInboxOutbox.setGroupId(universalAuthGroupForRebase);
        mangeInboxOutBoxRepository.save(mangeInboxOutbox);
*/

        MangeInboxOutbox mangeInboxOutbox2 = new MangeInboxOutbox();
        mangeInboxOutbox2.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox2.setRemarks("UNIT REBASE");
        mangeInboxOutbox2.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox2.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox2.setToUnit(HelperUtils.HEADUNITID);
        mangeInboxOutbox2.setFromUnit(hrDataCheck.getUnitId());
        mangeInboxOutbox2.setApproverpId("");
        mangeInboxOutbox2.setType(chekUnit.getDescr());
        mangeInboxOutbox2.setRoleId(hrDataCheck.getRoleId());
        mangeInboxOutbox2.setCreaterpId(hrDataCheck.getPid());
        mangeInboxOutbox2.setStatus("Approved");
        mangeInboxOutbox2.setState("CR");
        mangeInboxOutbox2.setIsArchive("0");
        mangeInboxOutbox2.setIsApproved("0");
        mangeInboxOutbox2.setIsFlag("1");
        mangeInboxOutbox2.setAllocationType(req.getUnitRebaseRequests().get(0).getAllocationTypeId());
        mangeInboxOutbox2.setIsRevision(0);
        mangeInboxOutbox2.setIsBgcg("RR");
        mangeInboxOutbox2.setIsRebase("1");
        mangeInboxOutbox2.setGroupId(universalAuthGroupForRebase);
        mangeInboxOutBoxRepository.save(mangeInboxOutbox2);


        List<CdaParkingTrans> selfCdaSieze = cdaParkingTransRepository.findByFinYearIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getRebaseUnitId(), req.getUnitRebaseRequests().get(0).getAllocationTypeId(), "0");
        for (Integer i = 0; i < selfCdaSieze.size(); i++) {
            CdaParkingTrans cdaParking = selfCdaSieze.get(i);
            cdaParking.setIsFlag("1");
            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cdaParkingTransRepository.save(cdaParking);
        }

        List<CdaParkingCrAndDr> selfDrCrCdaSieze = parkingCrAndDrRepository.findByFinYearIdAndIsFlagAndAndAllocTypeIdAndUnitIdAndIsRevision(req.getFinYear(), "0", req.getUnitRebaseRequests().get(0).getAllocationTypeId(), req.getRebaseUnitId(), 0);
        for (Integer i = 0; i < selfDrCrCdaSieze.size(); i++) {
            CdaParkingCrAndDr cdaParking = selfDrCrCdaSieze.get(i);
            cdaParking.setIsFlag("1");
            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            parkingCrAndDrRepository.save(cdaParking);
        }


        String rebaseUnitAllocFromU = "";


        List<BudgetAllocation> selftAllocSieze = (budgetAllocationRepository.findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(req.getRebaseUnitId(), req.getFinYear(), req.getUnitRebaseRequests().get(0).getAllocationTypeId(), "0", "0", "Approved"));
        for (Integer i = 0; i < selftAllocSieze.size(); i++) {
            BudgetAllocation allocData = selftAllocSieze.get(i);
            rebaseUnitAllocFromU = allocData.getFromUnit();
            allocData.setIsFlag("1");
            allocData.setIsTYpe("REBASE");
            allocData.setIsBudgetRevision("1");
            allocData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationRepository.save(allocData);
        }


        List<BudgetAllocationDetails> selfAllocDetailSieze = (budgetAllocationDetailsRepository.findByToUnitAndFinYearAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(req.getRebaseUnitId(), req.getFinYear(), req.getUnitRebaseRequests().get(0).getAllocationTypeId(), "0", "0"));
        for (Integer i = 0; i < selfAllocDetailSieze.size(); i++) {
            BudgetAllocationDetails allocDatatails = selfAllocDetailSieze.get(i);
            allocDatatails.setIsDelete("1");
            allocDatatails.setIsTYpe("REBASE");
            allocDatatails.setIsBudgetRevision("1");
            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetAllocationDetailsRepository.save(allocDatatails);
        }


        int count = 0;
        for (Integer k = 0; k < req.getUnitRebaseRequests().size(); k++) {
            AmountUnit amtObj = amountUnitRepository.findByAmountTypeId(req.getUnitRebaseRequests().get(k).getAmountType());
            double allAmountUnit = amtObj.getAmount();
            double allocAmount = Double.parseDouble(req.getUnitRebaseRequests().get(k).getAllocAmount());
            double expAmount = Double.parseDouble(req.getUnitRebaseRequests().get(k).getExpAmount());
            double shipAllocAmount = allocAmount * allAmountUnit;
            double shipExpAmount = Double.parseDouble( 0+"");
            BudgetRebase budgetRebase = new BudgetRebase();
            budgetRebase.setBudgetRebaseId(HelperUtils.getUnitRebased());
            budgetRebase.setRefTransId(HelperUtils.getTransId());
            budgetRebase.setFinYear(req.getFinYear());
            budgetRebase.setAuthGrpId(universalAuthGroupForRebase);
            budgetRebase.setRebaseUnitId(req.getRebaseUnitId());
            budgetRebase.setHeadUnitId(frmUnit);
            budgetRebase.setFrmStationId(req.getFrmStationId());
            budgetRebase.setToStationId(req.getToStationId());
            budgetRebase.setToHeadUnitId(toHdUnitId);
            budgetRebase.setAllocFromUnit(frmUnit);
            budgetRebase.setRemCdaBal(req.getUnitRebaseRequests().get(k).getBalAmount());
            budgetRebase.setOccuranceDate(ConverterUtils.convertDateTotimeStamp(req.getOccurrenceDate()));
            budgetRebase.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
            budgetRebase.setBudgetHeadId(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
            budgetRebase.setAllocAmount(req.getUnitRebaseRequests().get(k).getAllocAmount());
            budgetRebase.setExpAmount(req.getUnitRebaseRequests().get(k).getExpAmount());
            budgetRebase.setBalAmount(req.getUnitRebaseRequests().get(k).getBalAmount());
            budgetRebase.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
            if (req.getUnitRebaseRequests().get(k).getLastCbDate() != null)
                budgetRebase.setLastCbDate(ConverterUtils.convertDateTotimeStamp(req.getUnitRebaseRequests().get(k).getLastCbDate()));
            else
                budgetRebase.setLastCbDate(null);
            budgetRebase.setAuthorityId(authorityId);
            budgetRebase.setUserId(hrDataCheck.getPid());
            budgetRebase.setLoginUnit(hrDataCheck.getUnitId());
            budgetRebase.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetRebase.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetRebaseRepository.save(budgetRebase);

            if (!frmRegion.equalsIgnoreCase(toRegion)) {

                CgUnit frmHead = cgUnitRepository.findByUnit(frmUnit);
                if (frmHead.getIsRebaseAuthority().equalsIgnoreCase("1")) {
                    String dBudgetUnit = "";
                    //............................RHQ REBASE BAL DEDUCT FROM ALLOCATION..............................

                    List<BudgetAllocationDetails> frmRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(frmUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> frmRhqDtls = frmRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    if (frmRhqDtls.size() > 0) {
                        for (Integer i = 0; i < frmRhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = frmRhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt - unloAmnt) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);
                        }
                    }
                    List<BudgetAllocation> frmRhqAllocs = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0", "Approved");
                    for (Integer i = 0; i < frmRhqAllocs.size(); i++) {
                        BudgetAllocation alloc = frmRhqAllocs.get(i);
                        AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                        double AmtUnit = amountType.getAmount();
                        double unloAmnt = shipAllocAmount / AmtUnit;
                        double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                        alloc.setIsFlag("0");
                        alloc.setIsBudgetRevision("0");
                        alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        alloc.setAllocationAmount((allocAmt - unloAmnt) + "");
                        BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                    }


                    List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
                    double totalAllocationAmount = shipAllocAmount;
                    for (Integer i = 0; i < frmRhqCda.size(); i++) {
                        CdaParkingTrans cdaParking = frmRhqCda.get(i);
                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        double remaningAmount = Double.parseDouble(cdaParking.getRemainingCdaAmount()) * amountUnit.getAmount();

                        double ghataSkateHai = allocatedAmount - remaningAmount;
                        if (ghataSkateHai == 0) {
                            continue;
                        }

                        double remeningCdaAMount = 0;
                        if (totalAllocationAmount >= ghataSkateHai) {
                            remeningCdaAMount = allocatedAmount - ghataSkateHai;
                            totalAllocationAmount = totalAllocationAmount - ghataSkateHai;
                        } else {
                            remeningCdaAMount = allocatedAmount - totalAllocationAmount;
                            totalAllocationAmount = 0;
                        }

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint(remeningCdaAMount / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }


                    if (count == 0) {

                        MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                        afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                        afterRebaseNotification.setRemarks("UNIT REBASE");
                        afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setToUnit(frmUnit);
                        afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                        afterRebaseNotification.setFromUnit(HelperUtils.HEADUNITID);
                        afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                        afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                        afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                        afterRebaseNotification.setStatus("Approved");
                        afterRebaseNotification.setState("CR");
                        afterRebaseNotification.setIsArchive("0");
                        afterRebaseNotification.setIsApproved("0");
                        afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        afterRebaseNotification.setIsFlag("1");
                        afterRebaseNotification.setType(chekUnit.getDescr());
                        afterRebaseNotification.setAmount("");
                        afterRebaseNotification.setIsBgcg("RR");
                        afterRebaseNotification.setIsRevision(0);
                        afterRebaseNotification.setIsRebase("1");

                        mangeInboxOutBoxRepository.save(afterRebaseNotification);
                    }


                }
                else {
                    String rhqUnit = "";

                    ///............................DHQ REBASE BAL DEDUCT FROM ALLOCATION..............................
                    List<BudgetAllocationDetails> frmHdUnitDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(frmUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> frmHdUnitDtls = frmHdUnitDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());

                    if (frmHdUnitDtls.size() > 0) {
                        for (Integer i = 0; i < frmHdUnitDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = frmHdUnitDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setAllocationAmount((allocAmt - unloAmnt) + "");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

                        }
                    }
                    List<BudgetAllocation> frmHdUnitAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");

                    for (Integer i = 0; i < frmHdUnitAlloc.size(); i++) {
                        BudgetAllocation alloc = frmHdUnitAlloc.get(i);
                        rhqUnit = alloc.getFromUnit();
                        AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                        double AmtUnit = amountType.getAmount();
                        double unloAmnt = shipAllocAmount / AmtUnit;
                        double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                        alloc.setIsFlag("0");
                        alloc.setAllocationAmount((allocAmt - unloAmnt) + "");
                        alloc.setIsBudgetRevision("0");
                        alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                    }
                    List<CdaParkingTrans> frmHdUnitCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    double totalAllocationAmount = shipAllocAmount;
                    for (Integer i = 0; i < frmHdUnitCda.size(); i++) {
                        CdaParkingTrans cdaParking = frmHdUnitCda.get(i);

                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        double remaningAmount = Double.parseDouble(cdaParking.getRemainingCdaAmount()) * amountUnit.getAmount();

                        double ghataSkateHai = allocatedAmount - remaningAmount;
                        if (ghataSkateHai == 0) {
                            continue;
                        }

                        double remeningCdaAMount = 0;
                        if (totalAllocationAmount >= ghataSkateHai) {
                            remeningCdaAMount = allocatedAmount - ghataSkateHai;
                            totalAllocationAmount = totalAllocationAmount - ghataSkateHai;
                        } else {
                            remeningCdaAMount = allocatedAmount - totalAllocationAmount;
                            totalAllocationAmount = 0;
                        }

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint(remeningCdaAMount / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }


                    if (count == 0) {
                        MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                        afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                        afterRebaseNotification.setRemarks("Unit Rebase");
                        afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setToUnit(frmUnit);
                        afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                        afterRebaseNotification.setFromUnit(rhqUnit);
                        afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                        afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                        afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                        afterRebaseNotification.setStatus("Fully Approved");
                        afterRebaseNotification.setState("CR");
                        afterRebaseNotification.setIsArchive("0");
                        afterRebaseNotification.setIsApproved("1");
                        afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        afterRebaseNotification.setIsFlag("0");
                        afterRebaseNotification.setType(chekUnit.getDescr());
                        afterRebaseNotification.setAmount("");
                        afterRebaseNotification.setIsBgcg("RR");
                        afterRebaseNotification.setIsRevision(0);
                        afterRebaseNotification.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(afterRebaseNotification);
                    }


                    ///............................RHQ REBASE BAL DEDUCT FROM ALLOCATION..................................

                    List<BudgetAllocationDetails> frmRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(rhqUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> frmRhqDtls = frmRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());

                    for (Integer i = 0; i < frmRhqDtls.size(); i++) {
                        BudgetAllocationDetails allocDatatails = frmRhqDtls.get(i);
                        AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                        double AmtUnit = amountType.getAmount();
                        double unloAmnt = shipAllocAmount / AmtUnit;
                        double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                        allocDatatails.setAllocationAmount((allocAmt - unloAmnt) + "");
                        allocDatatails.setIsDelete("0");
                        allocDatatails.setIsBudgetRevision("0");
                        allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

                    }

                    List<BudgetAllocation> frmRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rhqUnit, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    for (Integer i = 0; i < frmRhqAlloc.size(); i++) {
                        BudgetAllocation alloc = frmRhqAlloc.get(i);
                        AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                        double AmtUnit = amountType.getAmount();
                        double unloAmnt = shipAllocAmount / AmtUnit;
                        double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                        alloc.setIsFlag("0");
                        alloc.setAllocationAmount((allocAmt - unloAmnt) + "");
                        alloc.setIsBudgetRevision("0");
                        alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                    }
                    List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rhqUnit, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    double totalAllocationAmount11 = shipAllocAmount;
                    for (Integer i = 0; i < frmRhqCda.size(); i++) {
                        CdaParkingTrans cdaParking = frmRhqCda.get(i);

                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        double remaningAmount = Double.parseDouble(cdaParking.getRemainingCdaAmount()) * amountUnit.getAmount();

                        double ghataSkateHai = allocatedAmount - remaningAmount;
                        if (ghataSkateHai == 0) {
                            continue;
                        }

                        double remeningCdaAMount = 0;
                        if (totalAllocationAmount11 >= ghataSkateHai) {
                            remeningCdaAMount = allocatedAmount - ghataSkateHai;
                            totalAllocationAmount11 = totalAllocationAmount11 - ghataSkateHai;
                        } else {
                            remeningCdaAMount = allocatedAmount - totalAllocationAmount11;
                            totalAllocationAmount11 = 0;
                        }

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint(remeningCdaAMount / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }


                    if (count == 0) {
                        MangeInboxOutbox afterRebaseNotification123 = new MangeInboxOutbox();
                        afterRebaseNotification123.setMangeInboxId(HelperUtils.getMangeInboxId());
                        afterRebaseNotification123.setRemarks("Unit Rebase");
                        afterRebaseNotification123.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification123.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification123.setToUnit(rhqUnit);
                        afterRebaseNotification123.setGroupId(universalAuthGroupForRebase);
                        afterRebaseNotification123.setFromUnit(HelperUtils.HEADUNITID);
                        afterRebaseNotification123.setRoleId(hrDataCheck.getRoleId());
                        afterRebaseNotification123.setCreaterpId(hrDataCheck.getPid());
                        afterRebaseNotification123.setApproverpId(hrDataCheck.getPid());
                        afterRebaseNotification123.setStatus("Fully Approved");
                        afterRebaseNotification123.setState("CR");
                        afterRebaseNotification123.setIsArchive("0");
                        afterRebaseNotification123.setIsApproved("1");
                        afterRebaseNotification123.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        afterRebaseNotification123.setIsFlag("0");
                        afterRebaseNotification123.setType(chekUnit.getDescr());
                        afterRebaseNotification123.setAmount("");
                        afterRebaseNotification123.setIsBgcg("RR");
                        afterRebaseNotification123.setIsRevision(0);
                        afterRebaseNotification123.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(afterRebaseNotification123);
                    }
                }


                // 2 Side..................... BAL ALLOCATION HERE AS RECEPIET..................................

                CgUnit toHead = cgUnitRepository.findByUnit(toHdUnitId);
                if (toHead.getIsRebaseAuthority().equalsIgnoreCase("1")) {

                    // ..................... RHQ BAL  ALLOCATION HERE AS RECEPIET..................................

                    List<BudgetAllocationDetails> toRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> toRhqDtls = toRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    String budgetReciptAuthGroupId = HelperUtils.getAuthorityGroupId();
                    if (toRhqDtls.size() > 0) {
                        for (Integer i = 0; i < toRhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = toRhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt + unloAmnt) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

                        }
                    } else {
                        // .....................CREATE BUDGET RECIPT..FOR RHQ................................
                        BudgetAllocationDetails budgetRecipt = new BudgetAllocationDetails();
                        budgetRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetRecipt.setFinYear(req.getFinYear());
                        budgetRecipt.setFromUnit(HelperUtils.HEADUNITID);
                        budgetRecipt.setToUnit(toHdUnitId);
                        budgetRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetRecipt.setStatus("Approved");
                        budgetRecipt.setIsTYpe("REBASE");
                        budgetRecipt.setUnallocatedAmount("0");
                        budgetRecipt.setIsBudgetRevision("0");
                        budgetRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAuthGroupId(budgetReciptAuthGroupId);
                        budgetRecipt.setRemarks("");
                        budgetRecipt.setPurposeCode("");
                        budgetRecipt.setRevisedAmount("0.0000");
                        budgetRecipt.setIsDelete("0");
                        budgetRecipt.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetRecipt.setUserId(hrDataCheck.getPid());
                        budgetRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetRecipt.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
                        budgetAllocationDetailsRepository.save(budgetRecipt);

                    }

                    List<BudgetAllocation> toRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    if (toRhqAlloc.size() > 0) {
                        for (Integer i = 0; i < toRhqAlloc.size(); i++) {
                            BudgetAllocation alloc = toRhqAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double amountu = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / amountu;
                            double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("0");

//                            List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
//                            if (frmRhqCda.size() == 0) {
//                                alloc.setUnallocatedAmount("-" + unloAmnt);
//                            } else {
                            alloc.setUnallocatedAmount("0");
//                            }

                            alloc.setAllocationAmount((allocAmt + unloAmnt) + "");
                            alloc.setIsBudgetRevision("0");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                        }

                    } else {
                        // .....................CREATE BUDGET RECIPT.....FOR RHQ.............................
                        BudgetAllocation budgetAllocationRecipt = new BudgetAllocation();
                        budgetAllocationRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationRecipt.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationRecipt.setFinYear(req.getFinYear());
                        budgetAllocationRecipt.setToUnit(toHdUnitId);
                        budgetAllocationRecipt.setFromUnit(HelperUtils.HEADUNITID);
                        budgetAllocationRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationRecipt.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationRecipt.setUnallocatedAmount("0");
                        budgetAllocationRecipt.setIsFlag("0");
                        budgetAllocationRecipt.setIsTYpe("REBASE");
                        budgetAllocationRecipt.setIsBudgetRevision("0");
                        budgetAllocationRecipt.setRevisedAmount("0");
                        budgetAllocationRecipt.setUserId(hrDataCheck.getPid());
                        budgetAllocationRecipt.setStatus("Approved");
                        budgetAllocationRecipt.setAuthGroupId(budgetReciptAuthGroupId);
                        budgetAllocationRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationRepository.save(budgetAllocationRecipt);


                        if (count == 0) {
                            MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                            mangeInboxOutboxReciptMsg.setIsRebase("0");
                            mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                            mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                            mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            mangeInboxOutboxReciptMsg.setToUnit(toHdUnitId);
                            mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                            mangeInboxOutboxReciptMsg.setFromUnit(HelperUtils.HEADUNITID);
                            mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                            mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                            mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                            mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                            mangeInboxOutboxReciptMsg.setState("CR");
                            mangeInboxOutboxReciptMsg.setIsArchive("0");
                            mangeInboxOutboxReciptMsg.setIsApproved("0");
                            mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                            mangeInboxOutboxReciptMsg.setIsFlag("0");
                            mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                            mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                            mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                            mangeInboxOutboxReciptMsg.setIsRevision(0);
                            mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);
                        }

                    }

//                           Add  CDA Add karke Phir Minus karna hai
                    List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    if (frmRhqCda.size() > 0) {
                        CdaParkingTrans cdaParking = frmRhqCda.get(0);
                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint((allocatedAmount + shipAllocAmount) / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }

                    BudgetHead budgetHeadId = subHeadRepository.findByBudgetCodeId(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    if (count == 0) {
                        MangeInboxOutbox mangeInboxOutboxAllocation = new MangeInboxOutbox();
                        mangeInboxOutboxAllocation.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxAllocation.setRemarks("UNIT REBASE");
                        mangeInboxOutboxAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setToUnit(toHdUnitId);
                        mangeInboxOutboxAllocation.setFromUnit(HelperUtils.HEADUNITID);
                        mangeInboxOutboxAllocation.setGroupId(universalAuthGroupForRebase);
                        mangeInboxOutboxAllocation.setType(chekUnit.getDescr());
                        mangeInboxOutboxAllocation.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxAllocation.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxAllocation.setState("CR");
                        mangeInboxOutboxAllocation.setApproverpId("");
                        mangeInboxOutboxAllocation.setIsFlag("1");
                        mangeInboxOutboxAllocation.setIsArchive("0");
                        mangeInboxOutboxAllocation.setIsApproved("0");
                        mangeInboxOutboxAllocation.setIsRevision(0);
                        mangeInboxOutboxAllocation.setStatus("Approved");
                        mangeInboxOutboxAllocation.setIsBgcg("RR");
                        mangeInboxOutboxAllocation.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxAllocation);
                    }
                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................


                    BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                    budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocationDetails.setFinYear(req.getFinYear());
                    budgetAllocationDetails.setFromUnit(toHdUnitId);
                    budgetAllocationDetails.setToUnit(req.getRebaseUnitId());
                    budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocationDetails.setStatus("Approved");
                    budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAuthGroupId(universalAuthGroup);
                    budgetAllocationDetails.setIsDelete("0");
                    budgetAllocationDetails.setIsTYpe("REBASE");
                    budgetAllocationDetails.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocationDetails.setIsBudgetRevision("0");
                    budgetAllocationDetails.setRevisedAmount("0.0000");
                    budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                    budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                    budgetAllocationDetailsRepository.save(budgetAllocationDetails);

                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setIsTYpe("REBASE");
                    budgetAllocation.setIsBudgetRevision("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setFinYear(req.getFinYear());
                    budgetAllocation.setToUnit(req.getRebaseUnitId());
                    budgetAllocation.setFromUnit(toHdUnitId);
                    budgetAllocation.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocation.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocation.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocation.setRevisedAmount("0");
                    budgetAllocation.setUserId(hrDataCheck.getPid());
                    budgetAllocation.setStatus("Approved");
                    budgetAllocation.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocation.setAuthGroupId(universalAuthGroup);
                    budgetAllocationRepository.save(budgetAllocation);

                    if (count == 0) {
                        MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                        mangeInboxOutboxReciptMsg.setIsRebase("0");
                        mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                        mangeInboxOutboxReciptMsg.setFromUnit(hrDataCheck.getUnitId());
                        mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                        mangeInboxOutboxReciptMsg.setState("CR");
                        mangeInboxOutboxReciptMsg.setIsArchive("0");
                        mangeInboxOutboxReciptMsg.setIsApproved("0");
                        mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        mangeInboxOutboxReciptMsg.setIsFlag("0");
                        mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                        mangeInboxOutboxReciptMsg.setIsRevision(0);
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);
                    }


                    if (count == 0) {
                        MangeInboxOutbox mangeInboxOutboxAllocation = new MangeInboxOutbox();
                        mangeInboxOutboxAllocation.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxAllocation.setRemarks("UNIT REBASE");
                        mangeInboxOutboxAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxAllocation.setFromUnit(toHdUnitId);
                        mangeInboxOutboxAllocation.setGroupId(universalAuthGroupForRebase);
                        mangeInboxOutboxAllocation.setType(chekUnit.getDescr());
                        mangeInboxOutboxAllocation.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxAllocation.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxAllocation.setState("CR");
                        mangeInboxOutboxAllocation.setApproverpId("");
                        mangeInboxOutboxAllocation.setIsFlag("1");
                        mangeInboxOutboxAllocation.setIsArchive("0");
                        mangeInboxOutboxAllocation.setIsApproved("0");
                        mangeInboxOutboxAllocation.setIsRevision(0);
                        mangeInboxOutboxAllocation.setStatus("Approved");
                        mangeInboxOutboxAllocation.setIsBgcg("RR");
                        mangeInboxOutboxAllocation.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxAllocation);
                    }
                } else {
                    CgUnit dHqUnit = cgUnitRepository.findByUnit(toHdUnitId);
                    String rHqUnitIdMain = "";
                    if (dHqUnit != null) {
                        rHqUnitIdMain = dHqUnit.getSubUnit();
                    }
                    //----------------------------TO RHQ RECIEPT AND ALLOCATION FOR DHQ.......................................

                    List<BudgetAllocationDetails> toRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(rHqUnitIdMain, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> toRhqDtls = toRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    String budgetReciptAuthGroupId = HelperUtils.getAuthorityGroupId();
                    String budgetAllocationAuthGroupId = HelperUtils.getAuthorityGroupId();
                    if (toRhqDtls.size() > 0) {
                        for (Integer i = 0; i < toRhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = toRhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt + unloAmnt) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

//
                        }
                    } else {
                        // .....................CREATE BUDGET RECIPT..FOR RHQ................................
                        BudgetAllocationDetails budgetRecipt = new BudgetAllocationDetails();
                        budgetRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetRecipt.setFinYear(req.getFinYear());
                        budgetRecipt.setFromUnit(HelperUtils.HEADUNITID);
                        budgetRecipt.setToUnit(rHqUnitIdMain);
                        budgetRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetRecipt.setStatus("Approved");
                        budgetRecipt.setIsTYpe("REBASE");
                        budgetRecipt.setUnallocatedAmount("0");
                        budgetRecipt.setIsBudgetRevision("0");
                        budgetRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetRecipt.setAuthGroupId(budgetReciptAuthGroupId);
                        budgetRecipt.setRemarks("");
                        budgetRecipt.setPurposeCode("");
                        budgetRecipt.setRevisedAmount("0.0000");
                        budgetRecipt.setIsDelete("0");
                        budgetRecipt.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetRecipt.setUserId(hrDataCheck.getPid());
                        budgetRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetRecipt.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
                        budgetAllocationDetailsRepository.save(budgetRecipt);
                    }


                    List<BudgetAllocation> toRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rHqUnitIdMain, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    if (toRhqAlloc.size() > 0) {
                        for (Integer i = 0; i < toRhqAlloc.size(); i++) {
                            BudgetAllocation alloc = toRhqAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double amountu = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / amountu;
                            double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
                            alloc.setAllocationAmount((allocAmt + unloAmnt) + "");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rHqUnitIdMain, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
//                            if (frmRhqCda.size() == 0) {
//                                alloc.setUnallocatedAmount("-" + unloAmnt);
//                            } else {
                            alloc.setUnallocatedAmount("0");
//                            }

                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                        }


                    } else {

                        // .....................CREATE BUDGET RECIPT..FOR RHQ................................
                        BudgetAllocation budgetAllocationReciptMain = new BudgetAllocation();
                        budgetAllocationReciptMain.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationReciptMain.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationReciptMain.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationReciptMain.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationReciptMain.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationReciptMain.setFinYear(req.getFinYear());
                        budgetAllocationReciptMain.setToUnit(rHqUnitIdMain);
                        budgetAllocationReciptMain.setFromUnit(HelperUtils.HEADUNITID);
                        budgetAllocationReciptMain.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationReciptMain.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationReciptMain.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationReciptMain.setUnallocatedAmount("0");
                        budgetAllocationReciptMain.setIsFlag("0");
                        budgetAllocationReciptMain.setIsTYpe("REBASE");
                        budgetAllocationReciptMain.setIsBudgetRevision("0");
                        budgetAllocationReciptMain.setRevisedAmount("0");
                        budgetAllocationReciptMain.setUserId(hrDataCheck.getPid());
                        budgetAllocationReciptMain.setStatus("Approved");
                        budgetAllocationReciptMain.setAuthGroupId(budgetReciptAuthGroupId);
                        budgetAllocationReciptMain.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationRepository.save(budgetAllocationReciptMain);

                        MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                        mangeInboxOutboxReciptMsg.setIsRebase("0");
                        mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setToUnit(rHqUnitIdMain);
                        mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                        mangeInboxOutboxReciptMsg.setFromUnit(HelperUtils.HEADUNITID);
                        mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                        mangeInboxOutboxReciptMsg.setState("CR");
                        mangeInboxOutboxReciptMsg.setIsArchive("0");
                        mangeInboxOutboxReciptMsg.setIsApproved("0");
                        mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        mangeInboxOutboxReciptMsg.setIsFlag("0");
                        mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                        mangeInboxOutboxReciptMsg.setIsRevision(0);
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);

                    }


//                      Add  CDA Add karke Phir Minus karna hai
                    List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rHqUnitIdMain, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    if (frmRhqCda.size() > 0) {
                        CdaParkingTrans cdaParking = frmRhqCda.get(0);
                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint((allocatedAmount + shipAllocAmount) / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }

                    if (count == 0) {
                        MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                        afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                        afterRebaseNotification.setRemarks("UNIT REBASE");
                        afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        afterRebaseNotification.setToUnit(rHqUnitIdMain);
                        afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                        afterRebaseNotification.setFromUnit(HelperUtils.HEADUNITID);
                        afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                        afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                        afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                        afterRebaseNotification.setStatus("Approved");
                        afterRebaseNotification.setState("CR");
                        afterRebaseNotification.setIsArchive("0");
                        afterRebaseNotification.setIsApproved("0");
                        afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        afterRebaseNotification.setIsFlag("1");
                        afterRebaseNotification.setType(chekUnit.getDescr());
                        afterRebaseNotification.setAmount("");
                        afterRebaseNotification.setIsBgcg("RR");
                        afterRebaseNotification.setIsRevision(0);
                        afterRebaseNotification.setIsRebase("1");

                        mangeInboxOutBoxRepository.save(afterRebaseNotification);
                    }
                    List<BudgetAllocationDetails> toDhqAllocDt = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> toDhqAllocDts = toDhqAllocDt.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    String budgetAllocationAuthGroupIds = HelperUtils.getAuthorityGroupId();
                    if (toDhqAllocDts.size() > 0) {
                        for (Integer i = 0; i < toDhqAllocDts.size(); i++) {
                            BudgetAllocationDetails allocDatatails = toDhqAllocDts.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt + unloAmnt) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);
                        }
                    } else {
                        // .....................CREATE BUDGET ALLOCATION...RHQ TO DHQ...............................
                        BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                        budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationDetails.setFinYear(req.getFinYear());
                        budgetAllocationDetails.setFromUnit(rHqUnitIdMain);
                        budgetAllocationDetails.setToUnit(toHdUnitId);
                        budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationDetails.setStatus("Approved");
                        budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAuthGroupId(budgetAllocationAuthGroupId);
                        budgetAllocationDetails.setIsDelete("0");
                        budgetAllocationDetails.setIsTYpe("REBASE");
                        budgetAllocationDetails.setUnallocatedAmount("0");
                        budgetAllocationDetails.setIsBudgetRevision("0");
                        budgetAllocationDetails.setRevisedAmount("0.0000");
                        budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                        budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                        budgetAllocationDetailsRepository.save(budgetAllocationDetails);
                    }

                    List<BudgetAllocation> toDhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    if (toDhqAlloc.size() > 0) {
                        for (Integer i = 0; i < toDhqAlloc.size(); i++) {
                            BudgetAllocation alloc = toDhqAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double amountu = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / amountu;
                            double allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
                            alloc.setAllocationAmount((allocAmt + unloAmnt) + "");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//                            List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
//                            if (frmRhqCda.size() == 0) {
//                                alloc.setUnallocatedAmount("-" + unloAmnt);
//                            } else {
                            alloc.setUnallocatedAmount("0");
//                            }

                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                        }

                    } else {
                        // .....................CREATE RECEIPT ALLOCATION. RHQ TO DHQ.................................
                        BudgetAllocation budgetAllocationMain = new BudgetAllocation();
                        budgetAllocationMain.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationMain.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationMain.setIsFlag("0");
                        budgetAllocationMain.setIsTYpe("REBASE");
                        budgetAllocationMain.setIsBudgetRevision("0");
                        budgetAllocationMain.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationMain.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationMain.setFinYear(req.getFinYear());
                        budgetAllocationMain.setToUnit(toHdUnitId);
                        budgetAllocationMain.setFromUnit(rHqUnitIdMain);
                        budgetAllocationMain.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationMain.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationMain.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationMain.setUnallocatedAmount("");
                        budgetAllocationMain.setRevisedAmount("0");
                        budgetAllocationMain.setUserId(hrDataCheck.getPid());
                        budgetAllocationMain.setStatus("Approved");
                        budgetAllocationMain.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationMain.setAuthGroupId(budgetAllocationAuthGroupId);
                        budgetAllocationRepository.save(budgetAllocationMain);
                        if (count == 0) {
                            MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                            mangeInboxOutboxReciptMsg.setIsRebase("0");
                            mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                            mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                            mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            mangeInboxOutboxReciptMsg.setToUnit(toHdUnitId);
                            mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                            mangeInboxOutboxReciptMsg.setFromUnit(rHqUnitIdMain);
                            mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                            mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                            mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                            mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                            mangeInboxOutboxReciptMsg.setState("CR");
                            mangeInboxOutboxReciptMsg.setIsArchive("0");
                            mangeInboxOutboxReciptMsg.setIsApproved("0");
                            mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                            mangeInboxOutboxReciptMsg.setIsFlag("0");
                            mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                            mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                            mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                            mangeInboxOutboxReciptMsg.setIsRevision(0);
                            mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);

                            MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                            afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                            afterRebaseNotification.setRemarks("UNIT REBASE");
                            afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            afterRebaseNotification.setToUnit(toHdUnitId);
                            afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                            afterRebaseNotification.setFromUnit(rHqUnitIdMain);
                            afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                            afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                            afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                            afterRebaseNotification.setStatus("Approved");
                            afterRebaseNotification.setState("CR");
                            afterRebaseNotification.setIsArchive("0");
                            afterRebaseNotification.setIsApproved("0");
                            afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                            afterRebaseNotification.setIsFlag("1");
                            afterRebaseNotification.setType(chekUnit.getDescr());
                            afterRebaseNotification.setAmount("0");
                            afterRebaseNotification.setIsBgcg("RR");
                            afterRebaseNotification.setIsRevision(0);
                            afterRebaseNotification.setIsRebase("1");

                            mangeInboxOutBoxRepository.save(afterRebaseNotification);
                        }
                    }

//                      Add  CDA Add karke Phir Minus karna hai
                    List<CdaParkingTrans> frmDhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
                    if (frmDhqCda.size() > 0) {
                        CdaParkingTrans cdaParking = frmDhqCda.get(0);
                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint((allocatedAmount + shipAllocAmount) / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }

                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                    BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                    budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocationDetails.setFinYear(req.getFinYear());
                    budgetAllocationDetails.setFromUnit(toHdUnitId);
                    budgetAllocationDetails.setToUnit(req.getRebaseUnitId());
                    budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocationDetails.setStatus("Approved");
                    budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAuthGroupId(universalAuthGroup);
                    budgetAllocationDetails.setIsDelete("0");
                    budgetAllocationDetails.setIsTYpe("REBASE");
                    budgetAllocationDetails.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocationDetails.setIsBudgetRevision("0");
                    budgetAllocationDetails.setRevisedAmount("0.0000");
                    budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                    budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                    budgetAllocationDetailsRepository.save(budgetAllocationDetails);

                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                    BudgetAllocation budgetAllocation123 = new BudgetAllocation();
                    budgetAllocation123.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation123.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation123.setIsFlag("0");
                    budgetAllocation123.setIsTYpe("REBASE");
                    budgetAllocation123.setIsBudgetRevision("0");
                    budgetAllocation123.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation123.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation123.setFinYear(req.getFinYear());
                    budgetAllocation123.setToUnit(req.getRebaseUnitId());
                    budgetAllocation123.setFromUnit(toHdUnitId);
                    budgetAllocation123.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocation123.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocation123.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocation123.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocation123.setRevisedAmount("0");
                    budgetAllocation123.setUserId(hrDataCheck.getPid());
                    budgetAllocation123.setStatus("Approved");
                    budgetAllocation123.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocation123.setAuthGroupId(universalAuthGroup);
                    budgetAllocationRepository.save(budgetAllocation123);


                    if (count == 0) {

                        BudgetHead budgetHeadIdS = subHeadRepository.findByBudgetCodeId(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        MangeInboxOutbox mangeInboxOutboxAllocation = new MangeInboxOutbox();
                        mangeInboxOutboxAllocation.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxAllocation.setRemarks("UNIT REBASE");
                        mangeInboxOutboxAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxAllocation.setFromUnit(toHdUnitId);
                        mangeInboxOutboxAllocation.setGroupId(universalAuthGroupForRebase);
                        mangeInboxOutboxAllocation.setType(chekUnit.getDescr());
                        mangeInboxOutboxAllocation.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxAllocation.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxAllocation.setState("CR");
                        mangeInboxOutboxAllocation.setApproverpId("");
                        mangeInboxOutboxAllocation.setIsFlag("1");
                        mangeInboxOutboxAllocation.setIsArchive("0");
                        mangeInboxOutboxAllocation.setIsApproved("0");
                        mangeInboxOutboxAllocation.setIsRevision(0);
                        mangeInboxOutboxAllocation.setStatus("Approved");
                        mangeInboxOutboxAllocation.setIsBgcg("RR");
                        mangeInboxOutboxAllocation.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxAllocation);


                        MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                        mangeInboxOutboxReciptMsg.setIsRebase("0");
                        mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                        mangeInboxOutboxReciptMsg.setFromUnit(hrDataCheck.getUnitId());
                        mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                        mangeInboxOutboxReciptMsg.setState("CR");
                        mangeInboxOutboxReciptMsg.setIsArchive("0");
                        mangeInboxOutboxReciptMsg.setIsApproved("0");
                        mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        mangeInboxOutboxReciptMsg.setIsFlag("0");
                        mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                        mangeInboxOutboxReciptMsg.setIsRevision(0);
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);
                    }
                }
            } else {
                ///............................SAME REGION..............................
                ///............................DHQ REBASE BAL DEDUCT FROM ALLOCATION..............................

                CgUnit rebaseUnitAllocFromUnits = cgUnitRepository.findByUnit(rebaseUnitAllocFromU);
                if (rebaseUnitAllocFromUnits.getIsRebaseAuthority().equalsIgnoreCase("1")) {

                    List<BudgetAllocationDetails> toRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> toRhqDtls = toRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    String reciptAuthGroupId = HelperUtils.getAuthorityGroupId();
                    if (toRhqDtls.size() > 0) {
                        for (Integer i = 0; i < toRhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = toRhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt1 = shipAllocAmount / AmtUnit;
                            double allocAmt1 = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt1 + unloAmnt1) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);
                        }
                    } else {
                        // .....................CREATE BUDGET RECIPT..FOR TO DHQ................................
                        BudgetAllocationDetails budgetAllocationDetailsRecipt = new BudgetAllocationDetails();
                        budgetAllocationDetailsRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetailsRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationDetailsRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationDetailsRecipt.setFinYear(req.getFinYear());
                        budgetAllocationDetailsRecipt.setFromUnit(rebaseUnitAllocFromU);
                        budgetAllocationDetailsRecipt.setToUnit(toHdUnitId);
                        budgetAllocationDetailsRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationDetailsRecipt.setStatus("Approved");
                        budgetAllocationDetailsRecipt.setIsTYpe("REBASE");
                        budgetAllocationDetailsRecipt.setUnallocatedAmount("0");
                        budgetAllocationDetailsRecipt.setIsBudgetRevision("0");
                        budgetAllocationDetailsRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setAuthGroupId(reciptAuthGroupId);
                        budgetAllocationDetailsRecipt.setRemarks("");
                        budgetAllocationDetailsRecipt.setPurposeCode("");
                        budgetAllocationDetailsRecipt.setRevisedAmount("0.0000");
                        budgetAllocationDetailsRecipt.setIsDelete("0");
                        budgetAllocationDetailsRecipt.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetailsRecipt.setUserId(hrDataCheck.getPid());
                        budgetAllocationDetailsRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationDetailsRecipt.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
                        budgetAllocationDetailsRepository.save(budgetAllocationDetailsRecipt);
                    }
                    List<BudgetAllocation> toRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    if (toRhqAlloc.size() > 0) {
                        for (Integer i = 0; i < toRhqAlloc.size(); i++) {
                            BudgetAllocation alloc = toRhqAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double amountu = amountType.getAmount();
                            double unloAmnt0 = shipAllocAmount / amountu;
                            double allocAmt0 = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
//                            alloc.setAllocationAmount((allocAmt0 + unloAmnt0) + "");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//                            List<CdaParkingTrans> frmHdUnitCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
//                            if (frmHdUnitCda.size() == 0) {
//                                alloc.setUnallocatedAmount("-" + unloAmnt0);
//                            } else {
                            alloc.setUnallocatedAmount("0");
//                            }
                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                        }

                    } else {
                        // .....................CREATE BUDGET RECIPT....FOR TO DHQ..............................
                        BudgetAllocation budgetAllocationRecipt = new BudgetAllocation();
                        budgetAllocationRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationRecipt.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationRecipt.setFinYear(req.getFinYear());
                        budgetAllocationRecipt.setToUnit(toHdUnitId);
                        budgetAllocationRecipt.setFromUnit(rebaseUnitAllocFromU);
                        budgetAllocationRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationRecipt.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationRecipt.setUnallocatedAmount("0");
                        budgetAllocationRecipt.setIsFlag("0");
                        budgetAllocationRecipt.setUnallocatedAmount("0");
                        budgetAllocationRecipt.setIsTYpe("REBASE");
                        budgetAllocationRecipt.setIsBudgetRevision("0");
                        budgetAllocationRecipt.setRevisedAmount("0");
                        budgetAllocationRecipt.setUserId(hrDataCheck.getPid());
                        budgetAllocationRecipt.setStatus("Approved");
                        budgetAllocationRecipt.setAuthGroupId(reciptAuthGroupId);
                        budgetAllocationRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationRepository.save(budgetAllocationRecipt);
                        if (count == 0) {
                            MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                            mangeInboxOutboxReciptMsg.setIsRebase("0");
                            mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                            mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                            mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            mangeInboxOutboxReciptMsg.setToUnit(toHdUnitId);
                            mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                            mangeInboxOutboxReciptMsg.setFromUnit(rebaseUnitAllocFromU);
                            mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                            mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                            mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                            mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                            mangeInboxOutboxReciptMsg.setState("CR");
                            mangeInboxOutboxReciptMsg.setIsArchive("0");
                            mangeInboxOutboxReciptMsg.setIsApproved("0");
                            mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                            mangeInboxOutboxReciptMsg.setIsFlag("0");
                            mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                            mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                            mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                            mangeInboxOutboxReciptMsg.setIsRevision(0);
                            mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);
                        }
                    }


                    List<CdaParkingTrans> frmHdUnitCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    if (frmHdUnitCda.size() > 0) {
                        CdaParkingTrans cdaParking = frmHdUnitCda.get(0);
                        AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint((allocatedAmount + shipAllocAmount) / amountUnit.getAmount() + ""));
                        cdaParkingTransRepository.save(cdaParking);
                    }

                    // .....................CREATE BUDGET ALLOCATION Details....FOR SHIP UNIT......................
                    BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                    budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocationDetails.setFinYear(req.getFinYear());
                    budgetAllocationDetails.setFromUnit(toHdUnitId);
                    budgetAllocationDetails.setToUnit(req.getRebaseUnitId());
                    budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocationDetails.setStatus("Approved");
                    budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAuthGroupId(universalAuthGroup);
                    budgetAllocationDetails.setIsDelete("0");
                    budgetAllocationDetails.setIsTYpe("REBASE");
                    budgetAllocationDetails.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocationDetails.setIsBudgetRevision("0");
                    budgetAllocationDetails.setRevisedAmount("0.0000");
                    budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                    budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                    budgetAllocationDetailsRepository.save(budgetAllocationDetails);

                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setIsTYpe("REBASE");
                    budgetAllocation.setIsBudgetRevision("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setFinYear(req.getFinYear());
                    budgetAllocation.setToUnit(req.getRebaseUnitId());
                    budgetAllocation.setFromUnit(toHdUnitId);
                    budgetAllocation.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocation.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocation.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocation.setRevisedAmount("0");
                    budgetAllocation.setUserId(hrDataCheck.getPid());
                    budgetAllocation.setStatus("Approved");
                    budgetAllocation.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocation.setAuthGroupId(universalAuthGroup);
                    budgetAllocationRepository.save(budgetAllocation);

                    if (count == 0) {

                        MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                        mangeInboxOutboxReciptMsg.setIsRebase("0");
                        mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                        mangeInboxOutboxReciptMsg.setFromUnit(toHdUnitId);
                        mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                        mangeInboxOutboxReciptMsg.setState("CR");
                        mangeInboxOutboxReciptMsg.setIsArchive("0");
                        mangeInboxOutboxReciptMsg.setIsApproved("0");
                        mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        mangeInboxOutboxReciptMsg.setIsFlag("0");
                        mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                        mangeInboxOutboxReciptMsg.setIsRevision(0);
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);

                        MangeInboxOutbox mangeInboxOutboxAllocation11111 = new MangeInboxOutbox();
                        mangeInboxOutboxAllocation11111.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxAllocation11111.setRemarks("UNIT REBASE");
                        mangeInboxOutboxAllocation11111.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation11111.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxAllocation11111.setToUnit(req.getRebaseUnitId());
                        mangeInboxOutboxAllocation11111.setFromUnit(toHdUnitId);
                        mangeInboxOutboxAllocation11111.setGroupId(universalAuthGroupForRebase);
                        mangeInboxOutboxAllocation11111.setType(chekUnit.getDescr());
                        mangeInboxOutboxAllocation11111.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxAllocation11111.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxAllocation11111.setState("CR");
                        mangeInboxOutboxAllocation11111.setApproverpId("");
                        mangeInboxOutboxAllocation11111.setIsFlag("1");
                        mangeInboxOutboxAllocation11111.setIsArchive("0");
                        mangeInboxOutboxAllocation11111.setIsApproved("0");
                        mangeInboxOutboxAllocation11111.setIsRevision(0);
                        mangeInboxOutboxAllocation11111.setStatus("Approved");
                        mangeInboxOutboxAllocation11111.setIsBgcg("RR");
                        mangeInboxOutboxAllocation11111.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxAllocation11111);

                        MangeInboxOutbox notifi = new MangeInboxOutbox();
                        notifi.setMangeInboxId(HelperUtils.getMangeInboxId());
                        notifi.setRemarks("UNIT REBASE");
                        notifi.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        notifi.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        notifi.setToUnit(req.getRebaseUnitId());
                        notifi.setFromUnit(toHdUnitId);
                        notifi.setGroupId(universalAuthGroupForRebase);
                        notifi.setType(chekUnit.getDescr());
                        notifi.setRoleId(hrDataCheck.getRoleId());
                        notifi.setCreaterpId(hrDataCheck.getPid());
                        notifi.setState("CR");
                        notifi.setApproverpId("");
                        notifi.setIsFlag("1");
                        notifi.setIsArchive("0");
                        notifi.setIsApproved("0");
                        notifi.setIsRevision(0);
                        notifi.setStatus("Approved");
                        notifi.setIsBgcg("RR");
                        notifi.setIsRebase("1");
                        mangeInboxOutBoxRepository.save(notifi);

                    }


                } else {

                    //  ...................from DHQ UNIT  BAL DEDUCION...........................

                    List<BudgetAllocationDetails> frmHdUnitDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(rebaseUnitAllocFromU, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> frmHdUnitDtls = frmHdUnitDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());

                    if (frmHdUnitDtls.size() > 0) {
                        for (Integer i = 0; i < frmHdUnitDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = frmHdUnitDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt = shipAllocAmount / AmtUnit;
                            double allocAmt = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt - unloAmnt) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

                        }
                    }
                    List<BudgetAllocation> frmHdUnitAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rebaseUnitAllocFromU, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    double allocAmt = 0.0;
                    double unloAmnt = 0.0;
                    if (frmHdUnitAlloc.size() > 0) {
                        for (Integer i = 0; i < frmHdUnitAlloc.size(); i++) {
                            BudgetAllocation alloc = frmHdUnitAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            unloAmnt = shipAllocAmount / AmtUnit;

                            allocAmt = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("1");
                            alloc.setIsBudgetRevision("1");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            alloc.setAllocationAmount((allocAmt - unloAmnt) + "");
                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                        }
                    }
                    List<CdaParkingTrans> frmHdUnitCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rebaseUnitAllocFromU, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    double totalAllocationAmount11 = shipAllocAmount;
                    if (frmHdUnitCda.size() > 0) {
                        for (Integer i = 0; i < frmHdUnitCda.size(); i++) {
                            CdaParkingTrans cdaParking = frmHdUnitCda.get(i);

                            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());

                            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                            double remaningAmount = Double.parseDouble(cdaParking.getRemainingCdaAmount()) * amountUnit.getAmount();

                            double ghataSkateHai = allocatedAmount - remaningAmount;
                            if (ghataSkateHai == 0) {
                                continue;
                            }

                            double remeningCdaAMount = 0;
                            if (totalAllocationAmount11 >= ghataSkateHai) {
                                remeningCdaAMount = allocatedAmount - ghataSkateHai;
                                totalAllocationAmount11 = totalAllocationAmount11 - ghataSkateHai;
                            } else {
                                remeningCdaAMount = allocatedAmount - totalAllocationAmount11;
                                totalAllocationAmount11 = 0;
                            }

                            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint(remeningCdaAMount / amountUnit.getAmount() + ""));
                            cdaParkingTransRepository.save(cdaParking);
                        }
                    }


                    //  ...................TO DHQ UNIT BAL ALLOCATION HERE...........................
                    CgUnit toDhq = cgUnitRepository.findByUnit(toHdUnitId);
                    String toDhqHeadUnit = "";
                    if (toDhq != null) {
                        toDhqHeadUnit = toDhq.getSubUnit();
                    }

                    List<BudgetAllocationDetails> toRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");
                    List<BudgetAllocationDetails> toRhqDtls = toRhqDtl.stream().filter(e -> e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    String reciptAuthGroupId = HelperUtils.getAuthorityGroupId();
                    String allocationAuthGroupId = HelperUtils.getAuthorityGroupId();
                    if (toRhqDtls.size() > 0) {
                        for (Integer i = 0; i < toRhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = toRhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit = amountType.getAmount();
                            double unloAmnt1 = shipAllocAmount / AmtUnit;
                            double allocAmt1 = Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            allocDatatails.setAllocationAmount((allocAmt1 + unloAmnt1) + "");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            BudgetAllocationDetails saveData11 = budgetAllocationDetailsRepository.save(allocDatatails);

                        }

                    } else {
                        // .....................CREATE BUDGET RECIPT..FOR TO DHQ................................
                        BudgetAllocationDetails budgetAllocationDetailsRecipt = new BudgetAllocationDetails();
                        budgetAllocationDetailsRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetailsRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationDetailsRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationDetailsRecipt.setFinYear(req.getFinYear());
                        budgetAllocationDetailsRecipt.setFromUnit(toDhqHeadUnit);
                        budgetAllocationDetailsRecipt.setToUnit(toHdUnitId);
                        budgetAllocationDetailsRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationDetailsRecipt.setStatus("Approved");
                        budgetAllocationDetailsRecipt.setIsTYpe("REBASE");
                        budgetAllocationDetailsRecipt.setUnallocatedAmount("0");
                        budgetAllocationDetailsRecipt.setIsBudgetRevision("0");
                        budgetAllocationDetailsRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRecipt.setAuthGroupId(reciptAuthGroupId);
                        budgetAllocationDetailsRecipt.setRemarks("");
                        budgetAllocationDetailsRecipt.setPurposeCode("");
                        budgetAllocationDetailsRecipt.setRevisedAmount("0.0000");
                        budgetAllocationDetailsRecipt.setIsDelete("0");
                        budgetAllocationDetailsRecipt.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetailsRecipt.setUserId(hrDataCheck.getPid());
                        budgetAllocationDetailsRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationDetailsRecipt.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
                        budgetAllocationDetailsRepository.save(budgetAllocationDetailsRecipt);
                    }


                    List<BudgetAllocation> toRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0", "0");

                    if (toRhqAlloc.size() > 0) {
                        for (Integer i = 0; i < toRhqAlloc.size(); i++) {
                            BudgetAllocation alloc = toRhqAlloc.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double amountu = amountType.getAmount();
                            double unloAmnt0 = shipAllocAmount / amountu;
                            double allocAmt0 = Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
                            alloc.setAllocationAmount((allocAmt0 + unloAmnt0) + "");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
//                            List<CdaParkingTrans> frmHdUnitCdaMain = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
//                            if (frmHdUnitCdaMain.size() == 0) {
//                                alloc.setUnallocatedAmount("-" + unloAmnt0);
//                            } else {
                            alloc.setUnallocatedAmount("0");
//                            }

                            BudgetAllocation saveData11 = budgetAllocationRepository.save(alloc);

                        }

                        List<CdaParkingTrans> frmHdUnitCdaMain = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), toHdUnitId, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                        if (frmHdUnitCdaMain.size() > 0) {
                            CdaParkingTrans cdaParking = frmHdUnitCdaMain.get(0);
                            AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaParking.getAmountType());
                            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            double allocatedAmount = Double.parseDouble(cdaParking.getTotalParkingAmount()) * amountUnit.getAmount();
                            cdaParking.setTotalParkingAmount(ConverterUtils.addDecimalPoint((allocatedAmount + shipAllocAmount) / amountUnit.getAmount() + ""));
                            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            cdaParkingTransRepository.save(cdaParking);
                        }


                    } else {
                        // .....................CREATE BUDGET RECIPT....FOR TO DHQ..............................
                        BudgetAllocation budgetAllocationRecipt = new BudgetAllocation();
                        budgetAllocationRecipt.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationRecipt.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationRecipt.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationRecipt.setFinYear(req.getFinYear());
                        budgetAllocationRecipt.setToUnit(toHdUnitId);
                        budgetAllocationRecipt.setFromUnit(toDhqHeadUnit);
                        budgetAllocationRecipt.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationRecipt.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationRecipt.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        budgetAllocationRecipt.setUnallocatedAmount("0");
                        budgetAllocationRecipt.setIsFlag("0");

                        budgetAllocationRecipt.setIsTYpe("REBASE");
                        budgetAllocationRecipt.setIsBudgetRevision("0");
                        budgetAllocationRecipt.setRevisedAmount("0");
                        budgetAllocationRecipt.setUserId(hrDataCheck.getPid());
                        budgetAllocationRecipt.setStatus("Approved");
                        budgetAllocationRecipt.setAuthGroupId(reciptAuthGroupId);
                        budgetAllocationRecipt.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationRepository.save(budgetAllocationRecipt);

                        MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                        mangeInboxOutboxReciptMsg.setIsRebase("0");
                        mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxReciptMsg.setToUnit(toHdUnitId);
                        mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                        mangeInboxOutboxReciptMsg.setFromUnit(toDhqHeadUnit);
                        mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                        mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                        mangeInboxOutboxReciptMsg.setState("CR");
                        mangeInboxOutboxReciptMsg.setIsArchive("0");
                        mangeInboxOutboxReciptMsg.setIsApproved("0");
                        mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        mangeInboxOutboxReciptMsg.setIsFlag("0");
                        mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                        mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                        mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                        mangeInboxOutboxReciptMsg.setIsRevision(0);
                        mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);
                    }


                    // .....................CREATE BUDGET ALLOCATION Details....FOR SHIP UNIT......................
                    BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                    budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocationDetails.setFinYear(req.getFinYear());
                    budgetAllocationDetails.setFromUnit(toHdUnitId);
                    budgetAllocationDetails.setToUnit(req.getRebaseUnitId());
                    budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocationDetails.setStatus("Approved");
                    budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocationDetails.setAuthGroupId(universalAuthGroup);
                    budgetAllocationDetails.setIsDelete("0");
                    budgetAllocationDetails.setIsTYpe("REBASE");
                    budgetAllocationDetails.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocationDetails.setIsBudgetRevision("0");
                    budgetAllocationDetails.setRevisedAmount("0.0000");
                    budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                    budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocationDetails.setTransactionId(HelperUtils.getTransId());
                    budgetAllocationDetailsRepository.save(budgetAllocationDetails);

                    // .....................CREATE BUDGET ALLOCATION....FOR SHIP UNIT..............................
                    BudgetAllocation budgetAllocation = new BudgetAllocation();
                    budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setIsFlag("0");
                    budgetAllocation.setIsTYpe("REBASE");
                    budgetAllocation.setIsBudgetRevision("0");
                    budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                    budgetAllocation.setFinYear(req.getFinYear());
                    budgetAllocation.setToUnit(req.getRebaseUnitId());
                    budgetAllocation.setFromUnit(toHdUnitId);
                    budgetAllocation.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                    budgetAllocation.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    budgetAllocation.setUnallocatedAmount(shipExpAmount + "");
                    budgetAllocation.setRevisedAmount("0");
                    budgetAllocation.setUserId(hrDataCheck.getPid());
                    budgetAllocation.setStatus("Approved");
                    budgetAllocation.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                    budgetAllocation.setAuthGroupId(universalAuthGroup);
                    budgetAllocationRepository.save(budgetAllocation);

                }


                if (count == 0) {


                    MangeInboxOutbox mangeInboxOutboxReciptMsg = new MangeInboxOutbox();
                    mangeInboxOutboxReciptMsg.setIsRebase("0");
                    mangeInboxOutboxReciptMsg.setMangeInboxId(HelperUtils.getMangeInboxId());
                    mangeInboxOutboxReciptMsg.setRemarks("Budget Receipt");
                    mangeInboxOutboxReciptMsg.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutboxReciptMsg.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    mangeInboxOutboxReciptMsg.setToUnit(req.getRebaseUnitId());
                    mangeInboxOutboxReciptMsg.setGroupId(universalAuthGroup);
                    mangeInboxOutboxReciptMsg.setFromUnit(hrDataCheck.getUnitId());
                    mangeInboxOutboxReciptMsg.setRoleId(hrDataCheck.getRoleId());
                    mangeInboxOutboxReciptMsg.setCreaterpId(hrDataCheck.getPid());
                    mangeInboxOutboxReciptMsg.setApproverpId(hrDataCheck.getPid());
                    mangeInboxOutboxReciptMsg.setStatus("Fully Approved");
                    mangeInboxOutboxReciptMsg.setState("CR");
                    mangeInboxOutboxReciptMsg.setIsArchive("0");
                    mangeInboxOutboxReciptMsg.setIsApproved("0");
                    mangeInboxOutboxReciptMsg.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    mangeInboxOutboxReciptMsg.setIsFlag("0");
                    mangeInboxOutboxReciptMsg.setType("Budget Receipt");
                    mangeInboxOutboxReciptMsg.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getAllocAmount()));
                    mangeInboxOutboxReciptMsg.setIsBgcg("BR");
                    mangeInboxOutboxReciptMsg.setIsRevision(0);
                    mangeInboxOutBoxRepository.save(mangeInboxOutboxReciptMsg);


                    MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                    afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                    afterRebaseNotification.setRemarks("UNIT REBASE");
                    afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    afterRebaseNotification.setToUnit(toHdUnitId);
                    afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                    afterRebaseNotification.setFromUnit(hrDataCheck.getUnitId());
                    afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                    afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                    afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                    afterRebaseNotification.setStatus("Approved");
                    afterRebaseNotification.setState("CR");
                    afterRebaseNotification.setIsArchive("0");
                    afterRebaseNotification.setIsApproved("0");
                    afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    afterRebaseNotification.setIsFlag("1");
                    afterRebaseNotification.setType(chekUnit.getDescr());
                    afterRebaseNotification.setAmount("");
                    afterRebaseNotification.setIsBgcg("RR");
                    afterRebaseNotification.setIsRevision(0);
                    afterRebaseNotification.setIsRebase("1");
                    mangeInboxOutBoxRepository.save(afterRebaseNotification);
                }
                if (count == 0) {
                    MangeInboxOutbox afterRebaseNotification = new MangeInboxOutbox();
                    afterRebaseNotification.setMangeInboxId(HelperUtils.getMangeInboxId());
                    afterRebaseNotification.setRemarks("UNIT REBASE");
                    afterRebaseNotification.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                    afterRebaseNotification.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                    afterRebaseNotification.setToUnit(req.getRebaseUnitId());
                    afterRebaseNotification.setGroupId(universalAuthGroupForRebase);
                    afterRebaseNotification.setFromUnit(hrDataCheck.getUnitId());
                    afterRebaseNotification.setRoleId(hrDataCheck.getRoleId());
                    afterRebaseNotification.setCreaterpId(hrDataCheck.getPid());
                    afterRebaseNotification.setApproverpId(hrDataCheck.getPid());
                    afterRebaseNotification.setStatus("Approved");
                    afterRebaseNotification.setState("CR");
                    afterRebaseNotification.setIsArchive("0");
                    afterRebaseNotification.setIsApproved("0");
                    afterRebaseNotification.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                    afterRebaseNotification.setIsFlag("1");
                    afterRebaseNotification.setType(chekUnit.getDescr());
                    afterRebaseNotification.setAmount("");
                    afterRebaseNotification.setIsBgcg("RR");
                    afterRebaseNotification.setIsRevision(0);
                    afterRebaseNotification.setIsRebase("1");
                    mangeInboxOutBoxRepository.save(afterRebaseNotification);
                }
            }

            count++;
        }

        defaultResponse.setMsg("UNIT REBASE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    @Transactional
    public ApiResponse<List<CgUnitResponse>> getAllIsShipCgUnitData() {

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
        String unitIdHr = hrDataCheck.getUnitId();
        List<CgUnit> unitDataList1 = cgUnitRepository.findByBudGroupUnitLike("%" + unitIdHr + "%");
/*        List<CgUnit> unitDataList1;
        if(hrDataCheck.getUnitId().equalsIgnoreCase(HelperUtils.HEADUNITID))
        {
            unitDataList1 = cgUnitRepository.findAll();
        }else{
            unitDataList1 = cgUnitRepository.findBySubUnitOrderByDescrAsc(unitIdHr);
        }*/
        List<CgUnit> unitDataList = unitDataList1.stream().filter(e -> e.getIsShip().equalsIgnoreCase("1") && e.getIsActive().equalsIgnoreCase("1")).collect(Collectors.toList());
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

    @Override
    public ApiResponse<List<RebaseNotificationResp>> getUnitRebaseNotificationData(String authGrpId) {
        List<RebaseNotificationResp> responce = new ArrayList<RebaseNotificationResp>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrDataCheck == null) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseNotificationResp>>() {
            }, "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS", HttpStatus.OK.value());
        } else {
            if (hrDataCheck.getRoleId().contains(HelperUtils.BUDGETMANGER)) {
            } else {
                return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseNotificationResp>>() {
                }, "YOU ARE NOT AUTHORIZED TO REBASE THE STATION", HttpStatus.OK.value());
            }
        }
        if (authGrpId == null || authGrpId.isEmpty()) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseNotificationResp>>() {
            }, "AUTHGROUP ID CAN NOT BE NULL", HttpStatus.OK.value());
        }

        List<BudgetRebase> rebaseData = budgetRebaseRepository.findByAuthGrpId(authGrpId);

        if (rebaseData.size() > 0) {
            for (Integer k = 0; k < rebaseData.size(); k++) {

                RebaseNotificationResp rebase = new RebaseNotificationResp();
                CgUnit unitN = cgUnitRepository.findByUnit(rebaseData.get(0).getRebaseUnitId());
                CgUnit loginN = cgUnitRepository.findByUnit(rebaseData.get(0).getLoginUnit());
                CgUnit frmU = cgUnitRepository.findByUnit(rebaseData.get(0).getAllocFromUnit());
                CgUnit toU = cgUnitRepository.findByUnit(rebaseData.get(0).getToHeadUnitId());
                //CgStation frmS = cgStationRepository.findByStationId(rebaseData.get(0).getFrmStationId());
                CgStation toS = cgStationRepository.findByStationId(rebaseData.get(0).getToStationId());
                BudgetFinancialYear findyr = budgetFinancialYearRepository.findBySerialNo(rebaseData.get(0).getFinYear());
                AllocationType type = allocationRepository.findByAllocTypeId(rebaseData.get(0).getAllocTypeId());
                BudgetHead bHead = subHeadRepository.findByBudgetCodeId(rebaseData.get(k).getBudgetHeadId());
                AmountUnit amountTypeObjs = amountUnitRepository.findByAmountTypeId(rebaseData.get(k).getAmountType());
                rebase.setUnitRebaseName(unitN.getDescr());
                rebase.setLoginUnitName(loginN.getDescr());
                rebase.setFromUnitName(frmU.getDescr());
                rebase.setToUnitName(toU.getDescr());
                rebase.setDateOfRebase(rebaseData.get(k).getOccuranceDate());
                rebase.setFromStation(rebaseData.get(k).getFrmStationId());
                rebase.setToStation(toS.getStationName());
                rebase.setFinYear(findyr.getFinYear());
                rebase.setAllocationType(type.getAllocDesc());
                rebase.setSubHead(bHead.getSubHeadDescr());
                rebase.setAllocationAmount(rebaseData.get(k).getAllocAmount());
                rebase.setExpenditureAmount(rebaseData.get(k).getExpAmount());
                rebase.setBalAmount(rebaseData.get(k).getBalAmount());
                rebase.setAmountType(amountTypeObjs.getAmountType());
                rebase.setAuthGrpId(rebaseData.get(k).getAuthGrpId());
                rebase.setLastCbDate(rebaseData.get(k).getLastCbDate());
                responce.add(rebase);
            }
        }

        return ResponseUtils.createSuccessResponse(responce, new TypeReference<List<RebaseNotificationResp>>() {
        });
    }

}

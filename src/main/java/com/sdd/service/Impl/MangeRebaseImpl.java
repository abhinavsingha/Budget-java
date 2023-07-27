package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.*;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.request.MangeRebaseRequest;
import com.sdd.request.RebaseBudgetHistory;
import com.sdd.request.UnitRebaseRequest;
import com.sdd.request.UnitRebaseSaveReq;
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
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
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

    private static final long expirationTime = 24L * 60L * 60L;

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

        List<CgStation> getAllData = cgStationRepository.findAll();

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
        List<BudgetAllocation> allocationData=allocationData1.stream().filter(e->e.getStatus().equalsIgnoreCase("Approved") && e.getIsFlag().equalsIgnoreCase("0")).collect(Collectors.toList());
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
            if (cdaDetails.size()<=0) {
                return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
                }, "CDA NOT FOUND IN THIS SUBHEAD "+bHead, HttpStatus.OK.value());
            }
            AmountUnit cdaAmtObj = amountUnitRepository.findByAmountTypeId(cdaDetails.get(0).getAmountType());
            double cdaAmtUnit=cdaAmtObj.getAmount();
            List<CdaDetailsForRebaseResponse> addRes = new ArrayList<CdaDetailsForRebaseResponse>();
            double remCdaBal=0.0;
            double TotalCdaBal=0.0;
            if (cdaDetails.size() > 0) {
                for (int j = 0; j < cdaDetails.size(); j++) {
                    CdaDetailsForRebaseResponse cda = new CdaDetailsForRebaseResponse();
                    cda.setGinNo(cdaParkingRepository.findByGinNo(cdaDetails.get(j).getGinNo()));
                    cda.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaDetails.get(j).getAmountType()));
                    cda.setTotalParkingAmount(cdaDetails.get(j).getTotalParkingAmount());
                    cda.setRemainingCdaAmount(cdaDetails.get(j).getRemainingCdaAmount());
                    cda.setRemarks(cdaDetails.get(j).getRemarks());
                    cda.setSubHeadId(cdaDetails.get(j).getBudgetHeadId());
                    remCdaBal += Double.parseDouble(cdaDetails.get(j).getRemainingCdaAmount());
                    TotalCdaBal += Double.parseDouble(cdaDetails.get(j).getTotalParkingAmount());
                    addRes.add(cda);
                }
            }
            rebase.setAllocatedAmount(String.valueOf(TotalCdaBal));
            rebase.setRemCdaBal(String.valueOf(remCdaBal));
            rebase.setCdaData(addRes);
            List<ContigentBill> expenditure1 = contigentBillRepository.findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdate(unit, finYear, bHead, allocId,  "0");
            List<ContigentBill> expenditure=expenditure1.stream().filter(e->e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
            if (expenditure.size() > 0) {
                double totalAmount = 0.0;
                Date lastCbDate = null;
                for (ContigentBill data : expenditure) {
                    totalAmount += Double.parseDouble(data.getCbAmount());
                    lastCbDate=data.getCbDate();
                }
                double expAmnt=totalAmount/amountUnit;
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
    @Transactional
    public ApiResponse<DefaultResponse> saveUnitRebase(UnitRebaseSaveReq req) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        DefaultResponse defaultResponse = new DefaultResponse();

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
        if (req.getUnitRebaseRequests().size() <= 0 || req.getToHeadUnitId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "YOU CAN'T REBASE WITHOUT ALLOCATION", HttpStatus.OK.value());
        }

        if (req.getUnitRebaseRequests().size() > 0) {

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
        }
        CgUnit chekUnit = cgUnitRepository.findByUnit(req.getRebaseUnitId());
        String subUnits=chekUnit.getSubUnit();
        CgStation frmS=cgStationRepository.findByStationName(req.getFrmStationId());
        if (frmS==null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FROM STATION REGION GETTING NULL");
        }
        CgStation toS=cgStationRepository.findByStationId(req.getToStationId());
        if (toS ==null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO STATION REGION GETTING NULL");
        }
        String toRegion=toS.getRhqId();
        String tohdUnit="";
        if (toS.getDhqName()==null || toS.getDhqName().isEmpty()){
            tohdUnit=toS.getRhqId();
        }else{
            tohdUnit=toS.getDhqName();
        }
        String frmRegion=frmS.getRhqId();
        String frmhdUnit="";
        if (frmS.getDhqName()==null || frmS.getDhqName().isEmpty()){
            frmhdUnit=frmS.getRhqId();
        }else{
            frmhdUnit=frmS.getDhqName();
        }
        CgUnit cgData = cgUnitRepository.findByUnit(hrDataCheck.getUnitId());
        String rebaseAuthority=cgData.getIsRebaseAuthority();
        if(!rebaseAuthority.equalsIgnoreCase("1")){
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED FOR UNIT REBASE");
        }
        if(!frmRegion.equalsIgnoreCase(toRegion)){
            if(!hrDataCheck.getUnitId().equalsIgnoreCase("001321"))
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED FOR UNIT REBASE IN ANOTHER REGION");
        }

        CgUnit obj = cgUnitRepository.findByCgUnitShort(tohdUnit);
        if (obj==null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "TO HEAD UNIT NOT FOUND");
        }
        String toHdUnitId=obj.getUnit();

        List<BudgetAllocation> allocationData = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(req.getUnitRebaseRequests().get(0).getBudgetHeadId(), req.getRebaseUnitId(),req.getFinYear(), req.getUnitRebaseRequests().get(0).getAllocationTypeId(), "0");
        if (allocationData.size()==0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION NOT FOUND FOR THIS UNIT");
        }
        String frmUnit=allocationData.get(0).getFromUnit();



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
            if (expireDate.getTime() >= todayDate.getTime()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "CAN NOT REBASE SAME UNIT ! TRY AFTER 24 HOURS");

            } else {
                chekUnit.setStationId(req.getToStationId());
                chekUnit.setSubUnit(toHdUnitId);
                chekUnit.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cgUnitRepository.save(chekUnit);
            }
        } else {
            chekUnit.setStationId(req.getToStationId());
            chekUnit.setSubUnit(toHdUnitId);
            chekUnit.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cgUnitRepository.save(chekUnit);
        }
        String authorityId = HelperUtils.getAuthorityId();
        String refRensId = HelperUtils.getTransId();
        String authGrId = HelperUtils.getAuthorityGroupId();

        Authority authority = new Authority();
        authority.setAuthority(req.getAuthority());
        authority.setAuthorityId(authorityId);
        authority.setAuthDate(ConverterUtils.convertDateTotimeStamp(req.getAuthDate()));
        authority.setDocId(req.getAuthDocId());
        authority.setAuthUnit(req.getAuthUnitId());
        authority.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        authority.setAuthGroupId(authGrId);
        authority.setRemarks(req.getRemark());
        authority.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        Authority saveAuthority = authorityRepository.save(authority);

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
        mangeInboxOutbox.setStatus("Fully Approved");
        mangeInboxOutbox.setState("AP");
        mangeInboxOutbox.setIsArchive("0");
        mangeInboxOutbox.setIsApproved("1");
        mangeInboxOutbox.setIsFlag("0");
        mangeInboxOutbox.setIsRevision(0);
        mangeInboxOutbox.setIsBgcg("RR");
        mangeInboxOutbox.setGroupId(authGrId);
        mangeInboxOutBoxRepository.save(mangeInboxOutbox);



        if(!hrDataCheck.getUnitId().equalsIgnoreCase("001321")){
            MangeInboxOutbox mangeInboxOutbox2 = new MangeInboxOutbox();
            mangeInboxOutbox2.setMangeInboxId(HelperUtils.getMangeInboxId());
            mangeInboxOutbox2.setRemarks("UNIT REBASE");
            mangeInboxOutbox2.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox2.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            mangeInboxOutbox2.setToUnit(req.getRebaseUnitId());
            mangeInboxOutbox2.setFromUnit(req.getRebaseUnitId());
            mangeInboxOutbox2.setApproverpId("");
            mangeInboxOutbox2.setType(chekUnit.getDescr());
            mangeInboxOutbox2.setRoleId(hrDataCheck.getRoleId());
            mangeInboxOutbox2.setCreaterpId(hrDataCheck.getPid());
            mangeInboxOutbox2.setStatus("Fully Approved");
            mangeInboxOutbox2.setState("AP");
            mangeInboxOutbox2.setIsArchive("0");
            mangeInboxOutbox2.setIsApproved("1");
            mangeInboxOutbox2.setIsFlag("0");
            mangeInboxOutbox2.setIsRevision(0);
            mangeInboxOutbox2.setIsBgcg("RR");
            mangeInboxOutbox2.setGroupId(authGrId);
            mangeInboxOutBoxRepository.save(mangeInboxOutbox2);
        }
        MangeInboxOutbox mangeInboxOutbox1 = new MangeInboxOutbox();
        mangeInboxOutbox1.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox1.setRemarks("UNIT REBASE");
        mangeInboxOutbox1.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox1.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox1.setToUnit(req.getRebaseUnitId());
        mangeInboxOutbox1.setFromUnit(req.getRebaseUnitId());
        mangeInboxOutbox1.setApproverpId("");
        mangeInboxOutbox1.setType(chekUnit.getDescr());
        mangeInboxOutbox1.setRoleId(hrDataCheck.getRoleId());
        mangeInboxOutbox1.setCreaterpId(hrDataCheck.getPid());
        mangeInboxOutbox1.setStatus("Fully Approved");
        mangeInboxOutbox1.setState("AP");
        mangeInboxOutbox1.setIsArchive("0");
        mangeInboxOutbox1.setIsApproved("1");
        mangeInboxOutbox1.setIsFlag("0");
        mangeInboxOutbox1.setIsRevision(0);
        mangeInboxOutbox1.setIsBgcg("RR");
        mangeInboxOutbox1.setGroupId(authGrId);
        mangeInboxOutBoxRepository.save(mangeInboxOutbox1);

        MangeInboxOutbox mangeInboxOutbox2 = new MangeInboxOutbox();
        mangeInboxOutbox2.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox2.setRemarks("UNIT REBASE");
        mangeInboxOutbox2.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox2.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox2.setToUnit(toHdUnitId);
        mangeInboxOutbox2.setFromUnit(toHdUnitId);
        mangeInboxOutbox2.setApproverpId("");
        mangeInboxOutbox2.setType(chekUnit.getDescr());
        mangeInboxOutbox2.setRoleId(hrDataCheck.getRoleId());
        mangeInboxOutbox2.setCreaterpId(hrDataCheck.getPid());
        mangeInboxOutbox2.setStatus("Fully Approved");
        mangeInboxOutbox2.setState("AP");
        mangeInboxOutbox2.setIsArchive("0");
        mangeInboxOutbox2.setIsApproved("1");
        mangeInboxOutbox2.setIsFlag("0");
        mangeInboxOutbox2.setIsRevision(0);
        mangeInboxOutbox2.setIsBgcg("RR");
        mangeInboxOutbox2.setGroupId(authGrId);
        mangeInboxOutBoxRepository.save(mangeInboxOutbox2);

        MangeInboxOutbox mangeInboxOutbox3 = new MangeInboxOutbox();
        mangeInboxOutbox3.setMangeInboxId(HelperUtils.getMangeInboxId());
        mangeInboxOutbox3.setRemarks("UNIT REBASE");
        mangeInboxOutbox3.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox3.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        mangeInboxOutbox3.setToUnit(frmUnit);
        mangeInboxOutbox3.setFromUnit(frmUnit);
        mangeInboxOutbox3.setApproverpId("");
        mangeInboxOutbox3.setType(chekUnit.getDescr());
        mangeInboxOutbox3.setRoleId(hrDataCheck.getRoleId());
        mangeInboxOutbox3.setCreaterpId(hrDataCheck.getPid());
        mangeInboxOutbox3.setStatus("Fully Approved");
        mangeInboxOutbox3.setState("AP");
        mangeInboxOutbox3.setIsArchive("0");
        mangeInboxOutbox3.setIsApproved("1");
        mangeInboxOutbox3.setIsFlag("0");
        mangeInboxOutbox3.setIsRevision(0);
        mangeInboxOutbox3.setIsBgcg("RR");
        mangeInboxOutbox3.setGroupId(authGrId);
        mangeInboxOutBoxRepository.save(mangeInboxOutbox3);
        String isType="";
        if (req.getUnitRebaseRequests().size() > 0) {
            for (Integer m = 0; m < req.getUnitRebaseRequests().size(); m++) {
                AmountUnit amtObj = amountUnitRepository.findByAmountTypeId(req.getUnitRebaseRequests().get(m).getAmountType());
                double allAmountUnit=amtObj.getAmount();
                double balAmount= Double.parseDouble(req.getUnitRebaseRequests().get(m).getBalAmount());
                double trnsfrAmount=balAmount*allAmountUnit;

                List<CdaParkingTrans> selfCdaSieze = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(m).getBudgetHeadId(), req.getRebaseUnitId(), req.getUnitRebaseRequests().get(m).getAllocationTypeId(), "0");
                if(selfCdaSieze.size()>0) {
                    for (Integer i = 0; i < selfCdaSieze.size(); i++) {
                        CdaParkingTrans cdaParking = selfCdaSieze.get(i);
                        cdaParking.setIsFlag("1");
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        cdaParkingTransRepository.save(cdaParking);
                    }
                }
                List<CdaParkingCrAndDr> selfDrCrCdaSieze = parkingCrAndDrRepository.findByFinYearIdAndBudgetHeadIdAndAllocTypeIdAndUnitId(req.getFinYear(), req.getUnitRebaseRequests().get(m).getBudgetHeadId(), req.getUnitRebaseRequests().get(m).getAllocationTypeId(), req.getRebaseUnitId());
                if(selfDrCrCdaSieze.size()>0) {
                    for (Integer i = 0; i < selfDrCrCdaSieze.size(); i++) {
                        CdaParkingCrAndDr cdaParking = selfDrCrCdaSieze.get(i);
                        cdaParking.setIsFlag("1");
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        parkingCrAndDrRepository.save(cdaParking);
                    }
                }

                List<BudgetAllocation> selftAllocSieze = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(req.getUnitRebaseRequests().get(0).getBudgetHeadId(), req.getRebaseUnitId(),req.getFinYear(), req.getUnitRebaseRequests().get(0).getAllocationTypeId(), "0");
                if (selftAllocSieze.size()>0) {
                    for (Integer i = 0; i < selftAllocSieze.size(); i++) {
                        BudgetAllocation allocData = selftAllocSieze.get(i);
                        allocData.setIsFlag("1");
                        allocData.setIsBudgetRevision("1");
                        allocData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        isType=allocData.getIsTYpe();
                        budgetAllocationRepository.save(allocData);
                    }
                }

                List<BudgetAllocationDetails> selfAllocDetailSieze = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(req.getRebaseUnitId(),req.getFinYear(),req.getUnitRebaseRequests().get(m).getBudgetHeadId(),req.getUnitRebaseRequests().get(m).getAllocationTypeId(), "0","0");
                if (selfAllocDetailSieze.size()>0) {
                    for (Integer i = 0; i < selfAllocDetailSieze.size(); i++) {
                        BudgetAllocationDetails allocDatatails = selfAllocDetailSieze.get(i);
                        allocDatatails.setIsDelete("1");
                        allocDatatails.setIsBudgetRevision("1");
                        allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetailsRepository.save(allocDatatails);
                    }
                }
                }
            }


        if (req.getUnitRebaseRequests().size() > 0) {
            for (Integer k = 0; k < req.getUnitRebaseRequests().size(); k++) {
                AmountUnit amtObj = amountUnitRepository.findByAmountTypeId(req.getUnitRebaseRequests().get(k).getAmountType());
                double allAmountUnit=amtObj.getAmount();
                double balAmount= Double.parseDouble(req.getUnitRebaseRequests().get(k).getBalAmount());
                double trnsfrAmount=balAmount*allAmountUnit;
                BudgetRebase budgetRebase = new BudgetRebase();
                budgetRebase.setBudgetRebaseId(HelperUtils.getUnitRebased());
                budgetRebase.setRefTransId(refRensId);
                budgetRebase.setFinYear(req.getFinYear());
                budgetRebase.setAuthGrpId(authGrId);
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
                budgetRebase.setAuthorityId(authorityId);
                budgetRebase.setAuthorityId(authorityId);
                budgetRebase.setUserId(hrDataCheck.getPid());
                budgetRebase.setLoginUnit(hrDataCheck.getUnitId());
                budgetRebase.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                budgetRebase.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                budgetRebaseRepository.save(budgetRebase);

                if(!frmRegion.equalsIgnoreCase(toRegion)){
                    String rhqUnit="";

                    ///............................DHQ REBASE BAL DEDUCT FROM ALLOCATION..............................

                    List<BudgetAllocationDetails> frmHdUnitDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(frmUnit,req.getFinYear(),req.getUnitRebaseRequests().get(k).getBudgetHeadId(),req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0","0");
                    List<BudgetAllocationDetails> frmHdUnitDtls=frmHdUnitDtl.stream().filter(e->e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    if (frmHdUnitDtls.size()>0) {
                        for (Integer i = 0; i < frmHdUnitDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = frmHdUnitDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit=amountType.getAmount();
                            double unloAmnt=trnsfrAmount/AmtUnit;
                            double allocAmt= Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("1");
                            allocDatatails.setIsBudgetRevision("1");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetailsRepository.save(allocDatatails);

                            allocDatatails.setTransactionId(HelperUtils.getTransId());
                            allocDatatails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setAllocationAmount((allocAmt-unloAmnt)+"");
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            budgetAllocationDetailsRepository.save(allocDatatails);
                        }
                    }
                    List<BudgetAllocation> frmHdUnitAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit,req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
                    List<BudgetAllocation> frmHdUnitAllocs=frmHdUnitAlloc.stream().filter(e->e.getIsFlag().equalsIgnoreCase("0")).collect(Collectors.toList());
                    double unloAmntData= 0;
                    if (frmHdUnitAllocs.size()>0) {
                        for (Integer i = 0; i < frmHdUnitAllocs.size(); i++) {
                            BudgetAllocation alloc = frmHdUnitAllocs.get(i);
                            rhqUnit=alloc.getFromUnit();
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double AmtUnit=amountType.getAmount();
                            double unloAmnt=trnsfrAmount/AmtUnit;

                            unloAmntData = unloAmnt;
                            double allocAmt= Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("1");
                            alloc.setIsBudgetRevision("1");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationRepository.save(alloc);

                            alloc.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
                            alloc.setAllocationAmount((allocAmt-unloAmnt)+"");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            alloc.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        }
                    }
                    List<CdaParkingTrans> frmHdUnitCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    for (Integer i = 0; i < frmHdUnitCda.size(); i++) {
                        CdaParkingTrans cdaParking = frmHdUnitCda.get(i);
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double remaning = Double.parseDouble(cdaParking.getRemainingCdaAmount());
                        cdaParking.setTotalParkingAmount((remaning)+"");
                        cdaParkingTransRepository.save(cdaParking);
                    }
                    if(frmHdUnitCda.size()>0) {
                            CdaParkingTrans cdaParking = frmHdUnitCda.get(0);
                            cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            double remaning = Double.parseDouble(cdaParking.getRemainingCdaAmount());
                            double exp= Double.parseDouble(req.getUnitRebaseRequests().get(k).getExpAmount());
                            cdaParking.setTotalParkingAmount((remaning+exp)+"");
                            cdaParkingTransRepository.save(cdaParking);
                    }
                    ///............................RHQ REBASE BAL DEDUCT FROM ALLOCATION..................................

                    String dBudgetUnit="";
                    List<BudgetAllocationDetails> frmRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(rhqUnit,req.getFinYear(),req.getUnitRebaseRequests().get(k).getBudgetHeadId(),req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0","0");
                    List<BudgetAllocationDetails> frmRhqDtls=frmRhqDtl.stream().filter(e->e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    if (frmRhqDtls.size()>0) {
                        for (Integer i = 0; i < frmRhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = frmRhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit=amountType.getAmount();
                            double unloAmnt=trnsfrAmount/AmtUnit;
                            double allocAmt= Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setAllocationAmount("-"+unloAmnt);
                            allocDatatails.setIsDelete("1");
                            allocDatatails.setIsBudgetRevision("1");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetailsRepository.save(allocDatatails);

                            allocDatatails.setTransactionId(HelperUtils.getTransId());
                            allocDatatails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setAllocationAmount((allocAmt-unloAmnt)+"");
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            budgetAllocationDetailsRepository.save(allocDatatails);

                        }
                    }
                    List<BudgetAllocation> frmRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), rhqUnit,req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
                    List<BudgetAllocation> frmRhqAllocs=frmRhqAlloc.stream().filter(e->e.getIsFlag().equalsIgnoreCase("0")).collect(Collectors.toList());
                    if (frmRhqAllocs.size()>0) {
                        for (Integer i = 0; i < frmRhqAllocs.size(); i++) {
                            BudgetAllocation alloc = frmRhqAllocs.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double AmtUnit=amountType.getAmount();
                            double unloAmnt=trnsfrAmount/AmtUnit;
                            double allocAmt= Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("1");
                            alloc.setIsBudgetRevision("1");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationRepository.save(alloc);
                            alloc.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
                            alloc.setAllocationAmount((allocAmt-unloAmnt)+"");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            alloc.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        }
                    }
                    List<CdaParkingTrans> frmRhqCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    for (Integer i = 0; i < frmRhqCda.size(); i++) {
                        CdaParkingTrans cdaParking = frmRhqCda.get(i);
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double remaning = Double.parseDouble(cdaParking.getRemainingCdaAmount());
                        cdaParking.setTotalParkingAmount((remaning)+"");
                        cdaParkingTransRepository.save(cdaParking);
                    }
                    if(frmRhqCda.size()>0) {
                        CdaParkingTrans cdaParking = frmRhqCda.get(0);
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double remaning = Double.parseDouble(cdaParking.getRemainingCdaAmount());
                        double exp= Double.parseDouble(req.getUnitRebaseRequests().get(k).getExpAmount());
                        cdaParking.setTotalParkingAmount((remaning+exp)+"");
                        cdaParkingTransRepository.save(cdaParking);
                    }

                    // .....................DHQ BAL ALLOCATION HERE AS RECEPIET..................................

                    CgUnit toHead= cgUnitRepository.findByUnit(tohdUnit);
                    if (toHead.getIsRebaseAuthority().equalsIgnoreCase("1")){
                    }else{
                        List<BudgetAllocationDetails> toRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(tohdUnit,req.getFinYear(),req.getUnitRebaseRequests().get(k).getBudgetHeadId(),req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0","0");
                        List<BudgetAllocationDetails> toRhqDtls=toRhqDtl.stream().filter(e->e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                        String authGroupId=HelperUtils.getAuthorityGroupId();
                        if (toRhqDtls.size()>0) {
                            for (Integer i = 0; i < toRhqDtls.size(); i++) {
                                BudgetAllocationDetails allocDatatails = toRhqDtls.get(i);
                                AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                                double AmtUnit=amountType.getAmount();
                                double unloAmnt=trnsfrAmount/AmtUnit;
                                double allocAmt= Double.parseDouble(allocDatatails.getAllocationAmount());
                                allocDatatails.setIsDelete("1");
                                allocDatatails.setIsBudgetRevision("1");
                                allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                budgetAllocationDetailsRepository.save(allocDatatails);

                                allocDatatails.setTransactionId(HelperUtils.getTransId());
                                allocDatatails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                                allocDatatails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                allocDatatails.setAllocationAmount((allocAmt+unloAmnt)+"");
                                allocDatatails.setIsDelete("0");
                                allocDatatails.setIsBudgetRevision("0");
                                budgetAllocationDetailsRepository.save(allocDatatails);
                            }
                        }else{
                            BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                            budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                            budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getBalAmount()));
                            budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                            budgetAllocationDetails.setFinYear(req.getFinYear());
                            budgetAllocationDetails.setFromUnit(tohdUnit);
                            budgetAllocationDetails.setToUnit(tohdUnit);
                            budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                            budgetAllocationDetails.setStatus("Approved");
                            budgetAllocationDetails.setIsTYpe("M");
                            budgetAllocationDetails.setUnallocatedAmount("0");
                            budgetAllocationDetails.setIsBudgetRevision("0");
                            budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetails.setAuthGroupId(authGroupId);
                            budgetAllocationDetails.setRemarks("");
                            budgetAllocationDetails.setPurposeCode("");
                            budgetAllocationDetails.setRevisedAmount("0.0000");
                            budgetAllocationDetails.setIsDelete("0");
                            budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                            budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                            budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                            budgetAllocationDetails.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
                            budgetAllocationDetailsRepository.save(budgetAllocationDetails);
                        }
                        List<BudgetAllocation> toRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), tohdUnit,req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
                        List<BudgetAllocation> toRhqAllocs=toRhqAlloc.stream().filter(e->e.getIsFlag().equalsIgnoreCase("0")).collect(Collectors.toList());
                        if (toRhqAllocs.size()>0) {
                            for (Integer i = 0; i < toRhqAllocs.size(); i++) {
                                BudgetAllocation alloc = toRhqAllocs.get(i);
                                AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                                double amountu=amountType.getAmount();
                                double unloAmnt=trnsfrAmount/amountu;
                                double allocAmt= Double.parseDouble(alloc.getAllocationAmount());
                                alloc.setIsFlag("1");
                                alloc.setIsBudgetRevision("1");
                                alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                budgetAllocationRepository.save(alloc);

                                alloc.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                                alloc.setIsFlag("0");
                                alloc.setIsBudgetRevision("0");
                                alloc.setAllocationAmount((allocAmt+unloAmnt)+"");
                                alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                alloc.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            }
                        }else{
                            BudgetAllocation budgetAllocation = new BudgetAllocation();
                            budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                            budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                            budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                            budgetAllocation.setFinYear(req.getFinYear());
                            budgetAllocation.setToUnit(tohdUnit);
                            budgetAllocation.setFromUnit(HelperUtils.HEADUNITID);
                            budgetAllocation.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                            budgetAllocation.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                            budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getBalAmount()));
                            budgetAllocation.setUnallocatedAmount("0");
                            budgetAllocation.setIsFlag("0");
                            budgetAllocation.setUnallocatedAmount("0");
                            budgetAllocation.setIsTYpe("R");
                            budgetAllocation.setIsBudgetRevision("0");
                            budgetAllocation.setRevisedAmount("0");
                            budgetAllocation.setUserId(hrDataCheck.getPid());
                            budgetAllocation.setStatus("Approved");
                            budgetAllocation.setAuthGroupId(authGroupId);
                            budgetAllocation.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                            budgetAllocationRepository.save(budgetAllocation);

                            MangeInboxOutbox mangeInboxOutboxRHqSend = new MangeInboxOutbox();
                            mangeInboxOutboxRHqSend.setMangeInboxId(HelperUtils.getMangeInboxId());
                            mangeInboxOutboxRHqSend.setRemarks("Budget Receipt");
                            mangeInboxOutboxRHqSend.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            mangeInboxOutboxRHqSend.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            mangeInboxOutboxRHqSend.setToUnit(tohdUnit);
                            mangeInboxOutboxRHqSend.setGroupId(authGroupId);
                            mangeInboxOutboxRHqSend.setFromUnit(HelperUtils.HEADUNITID);
                            mangeInboxOutboxRHqSend.setRoleId(hrDataCheck.getRoleId());
                            mangeInboxOutboxRHqSend.setCreaterpId(hrDataCheck.getPid());
                            mangeInboxOutboxRHqSend.setApproverpId(hrDataCheck.getPid());
                            mangeInboxOutboxRHqSend.setStatus("Fully Approved");
                            mangeInboxOutboxRHqSend.setState("CR");
                            mangeInboxOutboxRHqSend.setIsArchive("0");
                            mangeInboxOutboxRHqSend.setIsApproved("0");
                            mangeInboxOutboxRHqSend.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                            mangeInboxOutboxRHqSend.setIsFlag("0");
                            mangeInboxOutboxRHqSend.setType("Rebase Receipt");
                            mangeInboxOutboxRHqSend.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getBalAmount()));
                            mangeInboxOutboxRHqSend.setIsBgcg("BR");
                            mangeInboxOutboxRHqSend.setIsRevision(0);
                            mangeInboxOutBoxRepository.save(mangeInboxOutbox);
                        }
                    }
                }else{
                    ///............................DHQ REBASE BAL DEDUCT FROM ALLOCATION..............................

                    List<BudgetAllocationDetails> frmHdUnitDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(frmUnit,req.getFinYear(),req.getUnitRebaseRequests().get(k).getBudgetHeadId(),req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0","0");
                    List<BudgetAllocationDetails> frmHdUnitDtls=frmHdUnitDtl.stream().filter(e->e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    if (frmHdUnitDtls.size()>0) {
                        for (Integer i = 0; i < frmHdUnitDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = frmHdUnitDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit=amountType.getAmount();
                            double unloAmnt=trnsfrAmount/AmtUnit;
                            double allocAmt= Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("1");
                            allocDatatails.setIsBudgetRevision("1");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetailsRepository.save(allocDatatails);

                            allocDatatails.setTransactionId(HelperUtils.getTransId());
                            allocDatatails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setAllocationAmount((allocAmt-unloAmnt)+"");
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            budgetAllocationDetailsRepository.save(allocDatatails);
                        }
                    }
                    List<BudgetAllocation> frmHdUnitAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit,req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
                    List<BudgetAllocation> frmHdUnitAllocs=frmHdUnitAlloc.stream().filter(e->e.getIsFlag().equalsIgnoreCase("0")).collect(Collectors.toList());
                    double unloAmntData= 0;
                    if (frmHdUnitAllocs.size()>0) {
                        for (Integer i = 0; i < frmHdUnitAllocs.size(); i++) {
                            BudgetAllocation alloc = frmHdUnitAllocs.get(i);
                            String rhqUnit=alloc.getFromUnit();
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double AmtUnit=amountType.getAmount();
                            double unloAmnt=trnsfrAmount/AmtUnit;

                            unloAmntData = unloAmnt;
                            double allocAmt= Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("1");
                            alloc.setIsBudgetRevision("1");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationRepository.save(alloc);

                            alloc.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
                            alloc.setAllocationAmount((allocAmt-unloAmnt)+"");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            alloc.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        }
                    }
                    List<CdaParkingTrans> frmHdUnitCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(req.getFinYear(), req.getUnitRebaseRequests().get(k).getBudgetHeadId(), frmUnit, req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");

                    for (Integer i = 0; i < frmHdUnitCda.size(); i++) {
                        CdaParkingTrans cdaParking = frmHdUnitCda.get(i);
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double remaning = Double.parseDouble(cdaParking.getRemainingCdaAmount());
                        cdaParking.setTotalParkingAmount((remaning)+"");
                        cdaParkingTransRepository.save(cdaParking);
                    }
                    if(frmHdUnitCda.size()>0) {
                        CdaParkingTrans cdaParking = frmHdUnitCda.get(0);
                        cdaParking.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        double remaning = Double.parseDouble(cdaParking.getRemainingCdaAmount());
                        double exp= Double.parseDouble(req.getUnitRebaseRequests().get(k).getExpAmount());
                        cdaParking.setTotalParkingAmount((remaning+exp)+"");
                        cdaParkingTransRepository.save(cdaParking);
                    }
                   //  ...................DHQ TO HAED UNIT ALLOCATION HERE...........................

                    List<BudgetAllocationDetails> toRhqDtl = budgetAllocationDetailsRepository.findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(tohdUnit,req.getFinYear(),req.getUnitRebaseRequests().get(k).getBudgetHeadId(),req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0","0");
                    List<BudgetAllocationDetails> toRhqDtls=toRhqDtl.stream().filter(e->e.getStatus().equalsIgnoreCase("Approved")).collect(Collectors.toList());
                    String authGroupId=HelperUtils.getAuthorityGroupId();
                    if (toRhqDtls.size()>0) {
                        for (Integer i = 0; i < toRhqDtls.size(); i++) {
                            BudgetAllocationDetails allocDatatails = toRhqDtls.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(allocDatatails.getAmountType());
                            double AmtUnit=amountType.getAmount();
                            double unloAmnt=trnsfrAmount/AmtUnit;
                            double allocAmt= Double.parseDouble(allocDatatails.getAllocationAmount());
                            allocDatatails.setIsDelete("1");
                            allocDatatails.setIsBudgetRevision("1");
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationDetailsRepository.save(allocDatatails);

                            allocDatatails.setTransactionId(HelperUtils.getTransId());
                            allocDatatails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            allocDatatails.setAllocationAmount((allocAmt+unloAmnt)+"");
                            allocDatatails.setIsDelete("0");
                            allocDatatails.setIsBudgetRevision("0");
                            budgetAllocationDetailsRepository.save(allocDatatails);
                        }
                    }else{
                        BudgetAllocationDetails budgetAllocationDetails = new BudgetAllocationDetails();
                        budgetAllocationDetails.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetails.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getBalAmount()));
                        budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocationDetails.setFinYear(req.getFinYear());
                        budgetAllocationDetails.setFromUnit(tohdUnit);
                        budgetAllocationDetails.setToUnit(tohdUnit);
                        budgetAllocationDetails.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocationDetails.setStatus("Approved");
                        budgetAllocationDetails.setIsTYpe("M");
                        budgetAllocationDetails.setUnallocatedAmount("0");
                        budgetAllocationDetails.setIsBudgetRevision("0");
                        budgetAllocationDetails.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocationDetails.setAuthGroupId(authGroupId);
                        budgetAllocationDetails.setRemarks("");
                        budgetAllocationDetails.setPurposeCode("");
                        budgetAllocationDetails.setRevisedAmount("0.0000");
                        budgetAllocationDetails.setIsDelete("0");
                        budgetAllocationDetails.setRefTransactionId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocationDetails.setUserId(hrDataCheck.getPid());
                        budgetAllocationDetails.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationDetails.setTransactionId(HelperUtils.getBudgetAlloctionRefrensId());
                        budgetAllocationDetailsRepository.save(budgetAllocationDetails);
                    }
                    List<BudgetAllocation> toRhqAlloc = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(req.getUnitRebaseRequests().get(k).getBudgetHeadId(), tohdUnit,req.getFinYear(), req.getUnitRebaseRequests().get(k).getAllocationTypeId(), "0");
                    List<BudgetAllocation> toRhqAllocs=toRhqAlloc.stream().filter(e->e.getIsFlag().equalsIgnoreCase("0")).collect(Collectors.toList());
                    if (toRhqAllocs.size()>0) {
                        for (Integer i = 0; i < toRhqAllocs.size(); i++) {
                            BudgetAllocation alloc = toRhqAllocs.get(i);
                            AmountUnit amountType = amountUnitRepository.findByAmountTypeId(alloc.getAmountType());
                            double amountu=amountType.getAmount();
                            double unloAmnt=trnsfrAmount/amountu;
                            double allocAmt= Double.parseDouble(alloc.getAllocationAmount());
                            alloc.setIsFlag("1");
                            alloc.setIsBudgetRevision("1");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            budgetAllocationRepository.save(alloc);

                            alloc.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                            alloc.setIsFlag("0");
                            alloc.setIsBudgetRevision("0");
                            alloc.setAllocationAmount((allocAmt+unloAmnt)+"");
                            alloc.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                            alloc.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        }
                    }else{
                        BudgetAllocation budgetAllocation = new BudgetAllocation();
                        budgetAllocation.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocation.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                        budgetAllocation.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocation.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        budgetAllocation.setRefTransId(HelperUtils.getBudgetAllocationTypeId());
                        budgetAllocation.setFinYear(req.getFinYear());
                        budgetAllocation.setToUnit(tohdUnit);
                        budgetAllocation.setFromUnit(HelperUtils.HEADUNITID);
                        budgetAllocation.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                        budgetAllocation.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        budgetAllocation.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getBalAmount()));
                        budgetAllocation.setUnallocatedAmount("0");
                        budgetAllocation.setIsFlag("0");
                        budgetAllocation.setUnallocatedAmount("0");
                        budgetAllocation.setIsTYpe("R");
                        budgetAllocation.setIsBudgetRevision("0");
                        budgetAllocation.setRevisedAmount("0");
                        budgetAllocation.setUserId(hrDataCheck.getPid());
                        budgetAllocation.setStatus("Approved");
                        budgetAllocation.setAuthGroupId(authGroupId);
                        budgetAllocation.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                        budgetAllocationRepository.save(budgetAllocation);

                        MangeInboxOutbox mangeInboxOutboxRHqSend = new MangeInboxOutbox();
                        mangeInboxOutboxRHqSend.setMangeInboxId(HelperUtils.getMangeInboxId());
                        mangeInboxOutboxRHqSend.setRemarks("Budget Receipt");
                        mangeInboxOutboxRHqSend.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxRHqSend.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                        mangeInboxOutboxRHqSend.setToUnit(tohdUnit);
                        mangeInboxOutboxRHqSend.setGroupId(authGroupId);
                        mangeInboxOutboxRHqSend.setFromUnit(HelperUtils.HEADUNITID);
                        mangeInboxOutboxRHqSend.setRoleId(hrDataCheck.getRoleId());
                        mangeInboxOutboxRHqSend.setCreaterpId(hrDataCheck.getPid());
                        mangeInboxOutboxRHqSend.setApproverpId(hrDataCheck.getPid());
                        mangeInboxOutboxRHqSend.setStatus("Fully Approved");
                        mangeInboxOutboxRHqSend.setState("CR");
                        mangeInboxOutboxRHqSend.setIsArchive("0");
                        mangeInboxOutboxRHqSend.setIsApproved("0");
                        mangeInboxOutboxRHqSend.setAllocationType(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                        mangeInboxOutboxRHqSend.setIsFlag("0");
                        mangeInboxOutboxRHqSend.setType("Rebase Receipt");
                        mangeInboxOutboxRHqSend.setAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getBalAmount()));
                        mangeInboxOutboxRHqSend.setIsBgcg("BR");
                        mangeInboxOutboxRHqSend.setIsRevision(0);
                        mangeInboxOutBoxRepository.save(mangeInboxOutbox);
                    }
                }

                // ...............................ALLOCATION FOR REBASE UNIT HERE........................
                String authGrId11=HelperUtils.getAuthorityGroupId();
                BudgetAllocationDetails budgetAllocationDetails1 = new BudgetAllocationDetails();
                budgetAllocationDetails1.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                budgetAllocationDetails1.setAllocationAmount(ConverterUtils.addDecimalPoint(req.getUnitRebaseRequests().get(k).getBalAmount()));
                budgetAllocationDetails1.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                budgetAllocationDetails1.setAllocationDate(HelperUtils.getCurrentTimeStamp());
                budgetAllocationDetails1.setAllocTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                budgetAllocationDetails1.setFinYear(req.getFinYear());
                budgetAllocationDetails1.setFromUnit(toHdUnitId);
                budgetAllocationDetails1.setToUnit(req.getRebaseUnitId());
                budgetAllocationDetails1.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                budgetAllocationDetails1.setStatus("Approved");
                budgetAllocationDetails1.setRemarks("Rebase Amount");
                budgetAllocationDetails1.setIsTYpe(isType);
                budgetAllocationDetails1.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocationDetails1.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocationDetails1.setAuthGroupId(authGrId11);
                budgetAllocationDetails1.setRevisedAmount("0");
                budgetAllocationDetails1.setRefTransactionId(HelperUtils.getTransId());
                budgetAllocationDetails1.setUserId(hrDataCheck.getPid());
                budgetAllocationDetails1.setIsDelete("0");
                budgetAllocationDetails1.setTransactionId(HelperUtils.getTransId());
                budgetAllocationDetailsRepository.save(budgetAllocationDetails1);

                BudgetAllocation budgetAllocation1 = new BudgetAllocation();
                budgetAllocation1.setAllocationId(HelperUtils.getBudgetAllocationTypeId());
                budgetAllocation1.setUpdatedDate(HelperUtils.getCurrentTimeStamp());
                budgetAllocation1.setIsFlag("0");
                budgetAllocation1.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                budgetAllocation1.setRefTransId(HelperUtils.getTransId());
                budgetAllocation1.setFinYear(req.getFinYear());
                budgetAllocation1.setToUnit(req.getRebaseUnitId());
                budgetAllocation1.setFromUnit(toHdUnitId);
                budgetAllocation1.setSubHead(req.getUnitRebaseRequests().get(k).getBudgetHeadId());
                budgetAllocation1.setIsTYpe(isType);
                budgetAllocation1.setAllocationTypeId(req.getUnitRebaseRequests().get(k).getAllocationTypeId());
                budgetAllocation1.setIsBudgetRevision("0");
                budgetAllocation1.setUnallocatedAmount("0");
                budgetAllocation1.setAllocationAmount(ConverterUtils.addDecimalPoint((req.getUnitRebaseRequests().get(k).getBalAmount()) + ""));
                budgetAllocation1.setRevisedAmount(ConverterUtils.addDecimalPoint(0 + ""));
                budgetAllocation1.setUserId(hrDataCheck.getPid());
                budgetAllocation1.setStatus("Approved");
                budgetAllocation1.setAmountType(req.getUnitRebaseRequests().get(k).getAmountType());
                budgetAllocation1.setAuthGroupId(authGrId11);
                budgetAllocationRepository.save(budgetAllocation1);

            }
        }else{
            BudgetRebase budgetRebase = new BudgetRebase();
            budgetRebase.setBudgetRebaseId(HelperUtils.getUnitRebased());
            budgetRebase.setRefTransId(refRensId);
            budgetRebase.setFinYear(req.getFinYear());
            budgetRebase.setRebaseUnitId(req.getRebaseUnitId());
            budgetRebase.setAuthGrpId(authGrId);
            budgetRebase.setHeadUnitId(frmUnit);
            budgetRebase.setFrmStationId(req.getFrmStationId());
            budgetRebase.setToStationId(req.getToStationId());
            budgetRebase.setToHeadUnitId(toHdUnitId);
            budgetRebase.setOccuranceDate(ConverterUtils.convertDateTotimeStamp(req.getOccurrenceDate()));
            budgetRebase.setAllocTypeId("");
            budgetRebase.setBudgetHeadId("");
            budgetRebase.setAllocAmount("");
            budgetRebase.setExpAmount("");
            budgetRebase.setBalAmount("");
            budgetRebase.setAmountType("");
            budgetRebase.setLastCbDate(null);
            budgetRebase.setAuthorityId(authorityId);
            budgetRebase.setAuthorityId(authorityId);
            budgetRebase.setUserId(hrDataCheck.getPid());
            budgetRebase.setLoginUnit(hrDataCheck.getUnitId());
            budgetRebase.setAllocFromUnit(frmUnit);
            budgetRebase.setRemCdaBal("");
            budgetRebase.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            budgetRebase.setCreatedOn(HelperUtils.getCurrentTimeStamp());
            budgetRebaseRepository.save(budgetRebase);
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
        String unitIdHr=hrDataCheck.getUnitId();
        List<CgUnit> unitDataList1 = cgUnitRepository.findByBudGroupUnitLike("%" + unitIdHr + "%");
        List<CgUnit> unitDataList=unitDataList1.stream().filter(e->e.getIsShip().equalsIgnoreCase("1") && e.getIsActive().equalsIgnoreCase("1")).collect(Collectors.toList());
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

        List<BudgetRebase> rebaseData=budgetRebaseRepository.findByAuthGrpId(authGrpId);

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

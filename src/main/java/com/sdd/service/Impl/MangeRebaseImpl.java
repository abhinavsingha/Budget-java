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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MangeRebaseImpl implements MangeRebaseService {

    private static final long expirationTime = 180L * 24L * 60L * 60L;

    @Autowired
    private CgUnitRepository cgUnitRepository;

    @Autowired
    BudgetAllocationRepository budgetAllocationRepository;

    @Autowired
    private AmountUnitRepository amountUnitRepository;


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
    private CdaParkingTransRepository cdaParkingTransRepository;

    @Autowired
    private CdaParkingRepository cdaParkingRepository;


    @Override
    public ApiResponse<DefaultResponse> saveRebaes(MangeRebaseRequest mangeRebaseRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(),"1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
        } else {
            if (hrDataCheck.getRoleId().contains(HelperUtils.SYSTEMADMIN)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
            }
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


        for (Integer m = 0; m < mangeRebaseRequest.getUnitRebaseRequests().size(); m++) {


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

        }


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
            budgetRebase.setFromUnitId(hrDataCheck.getUnitId());
            budgetRebase.setToUnitId(mangeRebaseRequest.getUnitRebaseRequests().get(l).getToUnitId());
            budgetRebase.setStationId(mangeRebaseRequest.getUnitRebaseRequests().get(l).getStationId());
            budgetRebase.setFinYear(mangeRebaseRequest.getUnitRebaseRequests().get(l).getBudgetFinanciaYearId());
            budgetRebase.setUserId(hrDataCheck.getPid());
            budgetRebase.setLastCbDate(ConverterUtils.convertDateTotimeStamp(mangeRebaseRequest.getUnitRebaseRequests().get(l).getOccurrenceDate()));


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
    public ApiResponse<List<CgStation>> getAllStation() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(),"1");

/*        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
        } else {
            if (hrDataCheck.getRoleId().contains(HelperUtils.SYSTEMADMIN)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
            }
        }*/

        List<CgStation> getAllData = cgStationRepository.findAll();

        return ResponseUtils.createSuccessResponse(getAllData, new TypeReference<List<CgStation>>() {
        });
    }

    @Override
    public ApiResponse<List<CgUnitResponse>> getAllUnit() {
        List<CgUnitResponse> responce = new ArrayList<CgUnitResponse>();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(),"1");

/*        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
        } else {
            if (hrDataCheck.getRoleId().contains(HelperUtils.SYSTEMADMIN)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
            }
        }*/

        List<CgUnit> getAllData = cgUnitRepository.findAllByOrderByDescrAsc();

        if (getAllData.size()>0){

            for (int i = 0; i < getAllData.size(); i++) {
                CgUnitResponse rep = new CgUnitResponse();
                rep.setUnit(getAllData.get(i).getUnit());
                rep.setDescr(getAllData.get(i).getDescr());
                rep.setCgUnitShort(getAllData.get(i).getCgUnitShort());
                rep.setPurposeCode(getAllData.get(i).getPurposeCode());
                rep.setCbUnit(getAllData.get(i).getSubUnit());
                rep.setIsActive(getAllData.get(i).getIsActive());
                rep.setIsFlag(getAllData.get(i).getIsFlag());
                rep.setCreatedOn(getAllData.get(i).getCreatedOn());
                rep.setUpdatedOn(getAllData.get(i).getUpdatedOn());
                String stationId= getAllData.get(i).getStationId();
                CgStation cgStation = cgStationRepository.findByStationId(stationId);
                rep.setCgStation(cgStation);
                responce.add(rep);
            }
        }


        return ResponseUtils.createSuccessResponse(responce, new TypeReference<List<CgUnitResponse>>() {
        });
    }


    @Override
    public ApiResponse<List<BudgetFinancialYear>> getAllBudgetFinYr() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(),"1");

/*        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
        } else {
            if (hrDataCheck.getRoleId().contains(HelperUtils.SYSTEMADMIN)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
            }
        }*/

        List<BudgetFinancialYear> getAllFnYrData = budgetFinancialYearRepository.findAllByOrderByFinYearAsc();

        return ResponseUtils.createSuccessResponse(getAllFnYrData, new TypeReference<List<BudgetFinancialYear>>() {
        });
    }

    @Override
    public ApiResponse<List<RebaseBudgetHistory>> getAllUnitRebaseData(String finYear, String unit) {
        List<RebaseBudgetHistory> responce = new ArrayList<RebaseBudgetHistory>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(),"1");
        if (hrDataCheck == null) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            },"YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS",HttpStatus.OK.value());
        } else {
            if (hrDataCheck.getRoleId().contains(HelperUtils.SYSTEMADMIN)) {
            } else {
                return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
                },"YOU ARE NOT AUTHORIZED TO REBASE THE STATION",HttpStatus.OK.value());
            }
        }
        if (finYear == null) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            },"FIN YEAR ID CAN NOT BE NULL",HttpStatus.OK.value());
        }
        if (unit == null) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            },"UNIT CAN NOT BE NULL",HttpStatus.OK.value());
        }
        BudgetFinancialYear Finyr=budgetFinancialYearRepository.findBySerialNo(finYear);
        CgUnit unitdata = cgUnitRepository.findByUnit(unit);
        List<AllocationType> allocType=allocationRepository.findByIsFlag("1");
        if (unitdata == null) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            },"INVALID UNIT ID PLEASE CHECK",HttpStatus.OK.value());
        }
        List<BudgetAllocation> allocationData = budgetAllocationRepository.findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(unit, finYear,allocType.get(0).getAllocTypeId(),"0");
        if (allocationData.size()<=0) {
            return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
            },"Record Not Found",HttpStatus.OK.value());
        }

        Double amountUnit;
        for (int i = 0; i < allocationData.size(); i++) {
            RebaseBudgetHistory rebase = new RebaseBudgetHistory();
            AmountUnit amountTypeObj=amountUnitRepository.findByAmountTypeId(allocationData.get(i).getAmountType());
            if(amountTypeObj==null){
                return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
                },"AMOUNT TYPE NOT FOUND FROM DB",HttpStatus.OK.value());
            }
            amountUnit=amountTypeObj.getAmount();
            double aAmount = Double.parseDouble(allocationData.get(i).getAllocationAmount());
            rebase.setUnit(unitdata.getDescr());
            rebase.setFinYear(Finyr.getFinYear());
            rebase.setAllocatedAmount(aAmount*amountUnit);
            rebase.setStatus(allocationData.get(i).getStatus());
            rebase.setAmountType(amountTypeObj);
            rebase.setAuthGrupId(allocationData.get(i).getAuthGroupId());
            rebase.setSubHead(subHeadRepository.findByBudgetCodeId(allocationData.get(i).getSubHead()));
            String bHead=allocationData.get(i).getSubHead();
            String allocId=allocationData.get(i).getAllocationTypeId();
            List<CdaParkingTrans> cdaDetails=cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYear,bHead,unit,allocId,"0");
            List<CdaDetailsForRebaseResponse> addRes = new ArrayList<CdaDetailsForRebaseResponse>();
            if(cdaDetails.size()>0) {
                for (int j = 0; j < cdaDetails.size(); j++) {
                    CdaDetailsForRebaseResponse cda = new CdaDetailsForRebaseResponse();
                    cda.setGinNo(cdaParkingRepository.findByGinNo(cdaDetails.get(j).getGinNo()));
                    cda.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaDetails.get(j).getAmountType()));
                    cda.setTotalParkingAmount(cdaDetails.get(j).getTotalParkingAmount());
                    cda.setRemainingCdaAmount(cdaDetails.get(j).getRemainingCdaAmount());
                    cda.setRemarks(cdaDetails.get(j).getRemarks());
                    cda.setSubHeadId(cdaDetails.get(j).getBudgetHeadId());
                    addRes.add(cda);

                }
            }
            rebase.setCdaData(addRes);
            List<ContigentBill> expenditure = contigentBillRepository.findExpAndCbDate(unit, finYear, bHead,"Approved");
            if (expenditure.size()>0) {
                double eAmount=0.0;
                for (int k = 0; k < expenditure.size(); k++) {
                    eAmount += Double.parseDouble(expenditure.get(k).getCbAmount());
                    rebase.setLastCbDate(expenditure.get(k).getCbDate());
                }
                rebase.setExpenditureAmount(eAmount);
                rebase.setAllcAmntSubtrctExpnAmunt(aAmount*amountUnit-eAmount);
            }else{
                rebase.setExpenditureAmount(0);
                rebase.setLastCbDate(null);
                rebase.setAllcAmntSubtrctExpnAmunt(aAmount*amountUnit-0);
            }

            responce.add(rebase);
        }
        return ResponseUtils.createSuccessResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
        });
    }

    @Override
    public ApiResponse<CgStation> getAllStationById(String stationId) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(),"1");

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
        } else {
            if (hrDataCheck.getRoleId().contains(HelperUtils.SYSTEMADMIN)) {

            } else {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS");
            }
        }

        if (stationId == null || stationId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "STATION ID CAN NOT BLANK");
        }
        CgStation cgStation = cgStationRepository.findByStationId(stationId);

        return ResponseUtils.createSuccessResponse(cgStation, new TypeReference<CgStation>() {
        });
    }
    @Override
    public ApiResponse<DefaultResponse> saveUnitRebase(UnitRebaseSaveReq req) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(),"1");
        DefaultResponse defaultResponse = new DefaultResponse();

        if (hrDataCheck == null) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS",HttpStatus.OK.value());
        } else {
            if (hrDataCheck.getRoleId().contains(HelperUtils.BUDGETMANGER)) {

            } else {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                },"YOU ARE NOT AUTHORIZED TO UPDATE USER STATUS",HttpStatus.OK.value());
            }
        }
        if (req.getBudgetFinanciaYearId() == null || req.getBudgetFinanciaYearId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"FINANCIAL YEAR CAN NOT BE BLANK",HttpStatus.OK.value());
        }
        if (req.getToUnitId() == null || req.getToUnitId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"TO UNIT CAN NOT BE BLANK",HttpStatus.OK.value());
        }
        if (req.getHeadUnitId() == null || req.getHeadUnitId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"TO HEAD UNIT CAN NOT BE BLANK",HttpStatus.OK.value());
        }
        if (req.getToStationId() == null || req.getToStationId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"TO_STATION ID CAN NOT BE BLANK",HttpStatus.OK.value());
        }
        if (req.getFromStationId() == null || req.getFromStationId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"FROM_STATION ID CAN NOT BE BLANK",HttpStatus.OK.value());
        }
        if (req.getAuthority() == null || req.getAuthority().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"AUTHORITY CAN NOT BE BLANK",HttpStatus.OK.value());
        }
        if (req.getAuthDate() == null || req.getAuthDate().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"AUTHORITY DATE CAN NOT BE BLANK",HttpStatus.OK.value());
        }
        if (req.getAuthUnitId() == null || req.getAuthUnitId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"AUTHORITY UNIT CAN NOT BE BLANK",HttpStatus.OK.value());
        }
        if (req.getAuthDocId() == null || req.getAuthDocId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"DOCUMENT ID CAN NOT BE BLANK",HttpStatus.OK.value());
        }
        if (req.getAuthorityId() == null || req.getAuthorityId().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"AUTHORITY ID CAN NOT BE BLANK",HttpStatus.OK.value());
        }
        if (req.getOccurrenceDate() == null) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"OCCURRENCE DATE CAN NOT BE BLANK",HttpStatus.OK.value());
        }


        CgUnit chekUnit = cgUnitRepository.findByUnit(req.getToUnitId());
        if (chekUnit == null || chekUnit.getUnit().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"RECORD NOT FOUND",HttpStatus.OK.value());
        }
        if (chekUnit.getStationId().equalsIgnoreCase(req.getToStationId())) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            },"CAN NOT REBASE ON SAME STATION",HttpStatus.OK.value());
        }
        String maxRebaseId =budgetRebaseRepository.findMaxRebaseIDByTounit(req.getToUnitId());
        if (maxRebaseId!=null) {
            BudgetRebase rebaseData=budgetRebaseRepository.findByBudgetRebaseId(maxRebaseId);
            Date crDate=rebaseData.getCreatedOn();
            Date expireDate= new Date(crDate.getTime()+expirationTime*1000);
            Date todayDate= new Date();
            if(expireDate.getTime()>=todayDate.getTime()){
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                },"CAN NOT REBASE ON SAME UNIT BEFORE SIX MONTH",HttpStatus.OK.value());
            }else{
                chekUnit.setStationId(req.getToStationId());
                chekUnit.setUpdatedOn(ConverterUtils.convertDateTotimeStamp(req.getOccurrenceDate()));
                cgUnitRepository.save(chekUnit);
            }
        }else {
            chekUnit.setStationId(req.getToStationId());
            chekUnit.setUpdatedOn(ConverterUtils.convertDateTotimeStamp(req.getOccurrenceDate()));
            cgUnitRepository.save(chekUnit);
        }


        Authority authority = new Authority();
        authority.setAuthority(req.getAuthority());
        authority.setAuthorityId(HelperUtils.getAuthorityId());
        authority.setAuthDate(ConverterUtils.convertDateTotimeStamp(req.getAuthDate()));
        authority.setDocId(req.getAuthDocId());
        authority.setAuthUnit(req.getAuthUnitId());
        authority.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        authority.setAuthGroupId(HelperUtils.getAuthorityGroupId());
        authority.setRemarks(req.getRemark());
        authority.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        Authority saveAuthority = authorityRepository.save(authority);

        String refRensId = HelperUtils.getTransId();
        BudgetRebase budgetRebase = new BudgetRebase();
        budgetRebase.setBudgetRebaseId(HelperUtils.getUnitRebased());
        budgetRebase.setAuthorityId(saveAuthority.getAuthorityId());
        budgetRebase.setRefTransId(refRensId);
        budgetRebase.setFromUnitId(hrDataCheck.getUnitId());
        budgetRebase.setToUnitId(req.getToUnitId());
        budgetRebase.setStationId(req.getToStationId());
        budgetRebase.setFinYear(req.getBudgetFinanciaYearId());
        budgetRebase.setUserId(hrDataCheck.getPid());
        budgetRebase.setLastCbDate(ConverterUtils.convertDateTotimeStamp(req.getOccurrenceDate()));
        budgetRebase.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        budgetRebase.setCreatedOn(HelperUtils.getCurrentTimeStamp());
        budgetRebaseRepository.save(budgetRebase);


        defaultResponse.setMsg("UNIT REBASE SUCCESSFULLY");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });


    }

}

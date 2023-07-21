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
        List<BudgetAllocation> allocationData = budgetAllocationRepository.findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(unit, finYear, allocTypes, "0");
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
            rebase.setAllocatedAmount(allocationData.get(i).getAllocationAmount());
            rebase.setStatus(allocationData.get(i).getStatus());
            rebase.setAmountType(amountTypeObj);
            rebase.setAllocationType(allocationRepository.findByAllocTypeId(allocId));
            rebase.setAuthGrupId(allocationData.get(i).getAuthGroupId());
            rebase.setSubHead(subHeadRepository.findByBudgetCodeId(allocationData.get(i).getSubHead()));
            String bHead = allocationData.get(i).getSubHead();
            List<CdaParkingTrans> cdaDetails = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYear, bHead, unit, allocId, "0");
            List<CdaDetailsForRebaseResponse> addRes = new ArrayList<CdaDetailsForRebaseResponse>();
            double remCdaBal=0.0;
            if (cdaDetails.size() > 0) {
                for (int j = 0; j < cdaDetails.size(); j++) {
                    CdaDetailsForRebaseResponse cda = new CdaDetailsForRebaseResponse();
                    cda.setGinNo(cdaParkingRepository.findByGinNo(cdaDetails.get(j).getGinNo()));
                    cda.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaDetails.get(j).getAmountType()));
                    cda.setTotalParkingAmount(cdaDetails.get(j).getTotalParkingAmount());
                    cda.setRemainingCdaAmount(cdaDetails.get(j).getRemainingCdaAmount());
                    cda.setRemarks(cdaDetails.get(j).getRemarks());
                    cda.setSubHeadId(cdaDetails.get(j).getBudgetHeadId());
                    AmountUnit cdaAmtObj = amountUnitRepository.findByAmountTypeId(cdaDetails.get(j).getAmountType());
                    if (cdaAmtObj == null) {
                        return ResponseUtils.createFailureResponse(responce, new TypeReference<List<RebaseBudgetHistory>>() {
                        }, "CDA AMOUNT TYPE NOT FOUND FROM DB", HttpStatus.OK.value());
                    }
                    double cdaAmtUnit=cdaAmtObj.getAmount();
                    double cdabal=Double.parseDouble(cdaDetails.get(j).getRemainingCdaAmount());
                    double finCdaBal=cdabal*cdaAmtUnit/amountUnit;
                    remCdaBal += finCdaBal;
                    addRes.add(cda);
                }
            }
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
                //DecimalFormat decimalFormat = new DecimalFormat("#");
                //String eAmount = decimalFormat.format(totalAmount/amountUnit);
                double expAmnt=totalAmount/amountUnit;
                double bal = aAmount - expAmnt;
                rebase.setExpenditureAmount(String.valueOf(expAmnt));
                rebase.setRemBal(Double.toString(bal));
                rebase.setLastCbDate(lastCbDate);
            } else {
                rebase.setExpenditureAmount("0.0000");
                rebase.setLastCbDate(null);
                rebase.setRemBal(allocationData.get(i).getAllocationAmount());
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
        if (req.getOccurrenceDate() == null) {
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


        if (chekUnit == null || chekUnit.getUnit().isEmpty()) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "RECORD NOT FOUND", HttpStatus.OK.value());
        }
        if (chekUnit.getStationId().equalsIgnoreCase(req.getToStationId())) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "CAN NOT REBASE ON SAME STATION", HttpStatus.OK.value());
        }
        String maxRebaseId = budgetRebaseRepository.findMaxRebaseIDByRebaseUnitId(req.getRebaseUnitId());
        if (maxRebaseId != null) {
            BudgetRebase rebaseData = budgetRebaseRepository.findByBudgetRebaseId(maxRebaseId);
            Date crDate = rebaseData.getCreatedOn();
            Date expireDate = new Date(crDate.getTime() + expirationTime * 1000);
            Date todayDate = new Date();
            if (expireDate.getTime() >= todayDate.getTime()) {
                return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
                }, "CAN NOT REBASE SAME UNIT ! TRY AFTER 24 HOURS", HttpStatus.OK.value());
            } else {
                chekUnit.setStationId(req.getToStationId());
                chekUnit.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                cgUnitRepository.save(chekUnit);
            }
        } else {
            chekUnit.setStationId(req.getToStationId());
            chekUnit.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
            cgUnitRepository.save(chekUnit);
        }
        String headUnits = chekUnit.getSubUnit();
        String headUnit = "";
        if (headUnits.equalsIgnoreCase("000225")) {
            headUnit = "001321";
        } else {
            headUnit = headUnits;
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

        String authorityId = HelperUtils.getAuthorityId();
        String occurrenceDate = req.getOccurrenceDate();
        String finYear = req.getFinYear();
        String rebaseUnitId = req.getRebaseUnitId();
        String headUnitId = req.getHeadUnitId();
        String frmStationId = req.getFrmStationId();
        String toStationId = req.getToStationId();
        String toHeadUnitId = req.getToHeadUnitId();

        CgStation frmS=cgStationRepository.findByStationId(frmStationId);
        CgStation toS=cgStationRepository.findByStationId(toStationId);
        if (frmS==null || toS ==null) {
            return ResponseUtils.createFailureResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            }, "REGION GETTING NULL FROM DB", HttpStatus.OK.value());
        }
        String toRegion=toS.getRhqId();
        String tohdUnit=toS.getDhqName();
        CgUnit toUnitIds = cgUnitRepository.findByCgUnitShort(tohdUnit);
        String toHdUnitId=toUnitIds.getUnit();
        String frmRegion=frmS.getRhqId();
        String frmhdUnit=frmS.getDhqName();


        String refRensId = HelperUtils.getTransId();
        String authGrId = HelperUtils.getAuthorityGroupId();
        if (req.getUnitRebaseRequests().size() > 0) {
            for (Integer l = 0; l < req.getUnitRebaseRequests().size(); l++) {
                BudgetRebase budgetRebase = new BudgetRebase();
                String budHd = req.getUnitRebaseRequests().get(l).getBudgetHeadId();
                String allocTypeId = req.getUnitRebaseRequests().get(l).getAllocationTypeId();
                budgetRebase.setBudgetRebaseId(HelperUtils.getUnitRebased());
                budgetRebase.setRefTransId(refRensId);
                budgetRebase.setFinYear(finYear);
                budgetRebase.setRebaseUnitId(rebaseUnitId);
                budgetRebase.setHeadUnitId(headUnit);
                budgetRebase.setFrmStationId(frmStationId);
                budgetRebase.setToStationId(toStationId);
                budgetRebase.setToHeadUnitId(toHeadUnitId);
                budgetRebase.setAllocTypeId(allocTypeId);
                budgetRebase.setOccuranceDate(ConverterUtils.convertDateTotimeStamp(occurrenceDate));
                budgetRebase.setBudgetHeadId(req.getUnitRebaseRequests().get(l).getBudgetHeadId());
                budgetRebase.setAllocAmount(req.getUnitRebaseRequests().get(l).getAllocAmount());
                budgetRebase.setExpAmount(req.getUnitRebaseRequests().get(l).getExpAmount());
                budgetRebase.setBalAmount(req.getUnitRebaseRequests().get(l).getBalAmount());
                budgetRebase.setAmountType(req.getUnitRebaseRequests().get(l).getAmountType());
                if (req.getUnitRebaseRequests().get(l).getLastCbDate() != null)
                    budgetRebase.setLastCbDate(ConverterUtils.convertDateTotimeStamp(req.getUnitRebaseRequests().get(l).getLastCbDate()));
                budgetRebase.setAuthorityId(authorityId);
                budgetRebase.setUserId(hrDataCheck.getPid());
                budgetRebase.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                budgetRebase.setCreatedOn(HelperUtils.getCurrentTimeStamp());


                double remCdaBal=0.0;
                String hdUnits="";
                if(frmRegion.equalsIgnoreCase(toRegion)) {
                    List<CdaParkingTrans> cdaDetail = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYear, budHd, rebaseUnitId, allocTypeId, "0");
                    String allocTyId=cdaDetail.get(0).getAllocTypeId();
                    List<BudgetAllocation> allocationData = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(budHd, rebaseUnitId,finYear, allocTyId, "0");
                    hdUnits=allocationData.get(0).getFromUnit();
                    if (cdaDetail.size() > 0) {
                        for (int j = 0; j < cdaDetail.size(); j++) {

                            String cdaId= cdaDetail.get(j).getGinNo();
                            String amountTotal=cdaDetail.get(j).getTotalParkingAmount();
                            double amountRemaining= Double.parseDouble(cdaDetail.get(j).getRemainingCdaAmount());
                            remCdaBal += Double.parseDouble(cdaDetail.get(j).getRemainingCdaAmount());
                            AmountUnit cdaAmtObj = amountUnitRepository.findByAmountTypeId(cdaDetail.get(j).getAmountType());
                            double cdaAmtUnit=cdaAmtObj.getAmount();

                            if(tohdUnit.equalsIgnoreCase(frmhdUnit)){
                                List<CdaParkingTrans> headCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYear, budHd, hdUnits, allocTypeId, "0");
                                String ginNo=headCda.get(0).getGinNo();
                                String rmCdabal=headCda.get(0).getRemainingCdaAmount();
                                AmountUnit hdamtUnit = amountUnitRepository.findByAmountTypeId(headCda.get(0).getAmountType());
                                double rqUnit=hdamtUnit.getAmount();
                                double fnAmount=amountRemaining*cdaAmtUnit/rqUnit;

                                CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
                                cdaParkingCrAndDr.setCdaParkingTrans(HelperUtils.getCdaId());
                                cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                                cdaParkingCrAndDr.setFinYearId(finYear);
                                cdaParkingCrAndDr.setBudgetHeadId(budHd);
                                cdaParkingCrAndDr.setGinNo(ginNo);
                                cdaParkingCrAndDr.setUnitId(hdUnits);
                                cdaParkingCrAndDr.setAuthGroupId(authGrId);
                                cdaParkingCrAndDr.setAmount(String.valueOf(fnAmount));
                                cdaParkingCrAndDr.setIscrdr("CR");
                                cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr.setAllocTypeId(allocTyId);
                                cdaParkingCrAndDr.setIsFlag("0");
                                cdaParkingCrAndDr.setTransactionId(HelperUtils.getTransId());
                                cdaParkingCrAndDr.setAmountType(headCda.get(0).getAmountType());
                                cdaParkingCrAndDr.setIsRevision(0);
                                parkingCrAndDrRepository.save(cdaParkingCrAndDr);
                            }else{
                                List<CdaParkingTrans> headCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYear, budHd, hdUnits, allocTypeId, "0");
                                String ginNo=headCda.get(0).getGinNo();
                                String rmCdabal=headCda.get(0).getRemainingCdaAmount();
                                AmountUnit hdamtUnit = amountUnitRepository.findByAmountTypeId(headCda.get(0).getAmountType());
                                double rqUnit=hdamtUnit.getAmount();
                                double fnAmount=amountRemaining*cdaAmtUnit/rqUnit;

                                CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
                                cdaParkingCrAndDr.setCdaParkingTrans(HelperUtils.getCdaId());
                                cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                                cdaParkingCrAndDr.setFinYearId(finYear);
                                cdaParkingCrAndDr.setBudgetHeadId(budHd);
                                cdaParkingCrAndDr.setGinNo(ginNo);
                                cdaParkingCrAndDr.setUnitId(hdUnits);
                                cdaParkingCrAndDr.setAuthGroupId(authGrId);
                                cdaParkingCrAndDr.setAmount(String.valueOf(fnAmount));
                                cdaParkingCrAndDr.setIscrdr("CR");
                                cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr.setAllocTypeId(allocTyId);
                                cdaParkingCrAndDr.setIsFlag("0");
                                cdaParkingCrAndDr.setTransactionId(HelperUtils.getTransId());
                                cdaParkingCrAndDr.setAmountType(headCda.get(0).getAmountType());
                                cdaParkingCrAndDr.setIsRevision(0);
                                parkingCrAndDrRepository.save(cdaParkingCrAndDr);

                                CdaParkingCrAndDr cdaParkingCrAndDr1 = new CdaParkingCrAndDr();
                                cdaParkingCrAndDr1.setCdaParkingTrans(HelperUtils.getCdaId());
                                cdaParkingCrAndDr1.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                                cdaParkingCrAndDr1.setFinYearId(finYear);
                                cdaParkingCrAndDr1.setBudgetHeadId(budHd);
                                cdaParkingCrAndDr1.setGinNo(ginNo);
                                cdaParkingCrAndDr1.setUnitId(hdUnits);
                                cdaParkingCrAndDr1.setAuthGroupId(authGrId);
                                cdaParkingCrAndDr1.setAmount(String.valueOf(fnAmount));
                                cdaParkingCrAndDr1.setIscrdr("DR");
                                cdaParkingCrAndDr1.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr1.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr1.setAllocTypeId(allocTyId);
                                cdaParkingCrAndDr1.setIsFlag("0");
                                cdaParkingCrAndDr1.setTransactionId(HelperUtils.getTransId());
                                cdaParkingCrAndDr1.setAmountType(headCda.get(0).getAmountType());
                                cdaParkingCrAndDr1.setIsRevision(0);
                                parkingCrAndDrRepository.save(cdaParkingCrAndDr1);

                                List<BudgetAllocation> allocationData1 = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(budHd, hdUnits,finYear, allocTyId, "0");
                                String rhqUnitId=allocationData1.get(0).getFromUnit();
                                List<CdaParkingTrans> headCda1 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYear, budHd, rhqUnitId, allocTypeId, "0");
                                String ginNo1=headCda1.get(0).getGinNo();
                                String rmCdabal1=headCda1.get(0).getRemainingCdaAmount();
                                AmountUnit hdamtUnit1 = amountUnitRepository.findByAmountTypeId(headCda1.get(0).getAmountType());
                                double rqUnit1=hdamtUnit1.getAmount();
                                double fnAmount1=amountRemaining*cdaAmtUnit/rqUnit1;

                                CdaParkingCrAndDr cdaParkingCrAndDr2 = new CdaParkingCrAndDr();
                                cdaParkingCrAndDr2.setCdaParkingTrans(HelperUtils.getCdaId());
                                cdaParkingCrAndDr2.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                                cdaParkingCrAndDr2.setFinYearId(finYear);
                                cdaParkingCrAndDr2.setBudgetHeadId(budHd);
                                cdaParkingCrAndDr2.setGinNo(ginNo1);
                                cdaParkingCrAndDr2.setUnitId(rhqUnitId);
                                cdaParkingCrAndDr2.setAuthGroupId(authGrId);
                                cdaParkingCrAndDr2.setAmount(String.valueOf(fnAmount1));
                                cdaParkingCrAndDr2.setIscrdr("CR");
                                cdaParkingCrAndDr2.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr2.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr2.setAllocTypeId(allocTyId);
                                cdaParkingCrAndDr2.setIsFlag("0");
                                cdaParkingCrAndDr2.setTransactionId(HelperUtils.getTransId());
                                cdaParkingCrAndDr2.setAmountType(headCda.get(0).getAmountType());
                                cdaParkingCrAndDr2.setIsRevision(0);
                                parkingCrAndDrRepository.save(cdaParkingCrAndDr2);

                                CdaParkingCrAndDr cdaParkingCrAndDr3 = new CdaParkingCrAndDr();
                                cdaParkingCrAndDr3.setCdaParkingTrans(HelperUtils.getCdaId());
                                cdaParkingCrAndDr3.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                                cdaParkingCrAndDr3.setFinYearId(finYear);
                                cdaParkingCrAndDr3.setBudgetHeadId(budHd);
                                cdaParkingCrAndDr3.setGinNo(ginNo1);
                                cdaParkingCrAndDr3.setUnitId(rhqUnitId);
                                cdaParkingCrAndDr3.setAuthGroupId(authGrId);
                                cdaParkingCrAndDr3.setAmount(String.valueOf(fnAmount1));
                                cdaParkingCrAndDr3.setIscrdr("DR");
                                cdaParkingCrAndDr3.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr3.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr3.setAllocTypeId(allocTyId);
                                cdaParkingCrAndDr3.setIsFlag("0");
                                cdaParkingCrAndDr3.setTransactionId(HelperUtils.getTransId());
                                cdaParkingCrAndDr3.setAmountType(headCda.get(0).getAmountType());
                                cdaParkingCrAndDr3.setIsRevision(0);
                                parkingCrAndDrRepository.save(cdaParkingCrAndDr3);

                                List<CdaParkingTrans> headCda2 = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYear, budHd, toHdUnitId, allocTypeId, "0");
                                String ginNo2=headCda2.get(0).getGinNo();
                                String rmCdabal2=headCda2.get(0).getRemainingCdaAmount();
                                AmountUnit hdamtUnit2 = amountUnitRepository.findByAmountTypeId(headCda2.get(0).getAmountType());
                                double rqUnit2=hdamtUnit2.getAmount();
                                double fnAmount2=amountRemaining*cdaAmtUnit/rqUnit2;

                                CdaParkingCrAndDr cdaParkingCrAndDr4 = new CdaParkingCrAndDr();
                                cdaParkingCrAndDr4.setCdaParkingTrans(HelperUtils.getCdaId());
                                cdaParkingCrAndDr4.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                                cdaParkingCrAndDr4.setFinYearId(finYear);
                                cdaParkingCrAndDr4.setBudgetHeadId(budHd);
                                cdaParkingCrAndDr4.setGinNo(ginNo2);
                                cdaParkingCrAndDr4.setUnitId(toHdUnitId);
                                cdaParkingCrAndDr4.setAuthGroupId(authGrId);
                                cdaParkingCrAndDr4.setAmount(String.valueOf(fnAmount2));
                                cdaParkingCrAndDr4.setIscrdr("CR");
                                cdaParkingCrAndDr4.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr4.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr4.setAllocTypeId(allocTyId);
                                cdaParkingCrAndDr4.setIsFlag("0");
                                cdaParkingCrAndDr4.setTransactionId(HelperUtils.getTransId());
                                cdaParkingCrAndDr4.setAmountType(headCda.get(0).getAmountType());
                                cdaParkingCrAndDr4.setIsRevision(0);
                                parkingCrAndDrRepository.save(cdaParkingCrAndDr4);

                            }
                            cdaDetail.get(j).setIsFlag("1");
                            cdaParkingTransRepository.save(cdaDetail.get(j));

                        }
                    }
                }else{
                    List<CdaParkingTrans> cdaDetail = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYear, budHd, rebaseUnitId, allocTypeId, "0");
                    if (cdaDetail.size() > 0) {
                        for (int j = 0; j < cdaDetail.size(); j++) {

                            String cdaId = cdaDetail.get(j).getGinNo();
                            String amountTotal = cdaDetail.get(j).getTotalParkingAmount();
                            double amountRemaining = Double.parseDouble(cdaDetail.get(j).getRemainingCdaAmount());
                            AmountUnit cdaAmtObj = amountUnitRepository.findByAmountTypeId(cdaDetail.get(j).getAmountType());
                            double cdaAmtUnit = cdaAmtObj.getAmount();

                                List<CdaParkingTrans> headCda = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(finYear, budHd, "001321", allocTypeId, "0");
                                String ginNo = headCda.get(0).getGinNo();
                                String rmCdabal = headCda.get(0).getRemainingCdaAmount();
                                AmountUnit hdamtUnit = amountUnitRepository.findByAmountTypeId(headCda.get(0).getAmountType());
                                double rqUnit = hdamtUnit.getAmount();
                                double fnAmount = amountRemaining * cdaAmtUnit / rqUnit;

                                CdaParkingCrAndDr cdaParkingCrAndDr = new CdaParkingCrAndDr();
                                cdaParkingCrAndDr.setCdaParkingTrans(HelperUtils.getCdaId());
                                cdaParkingCrAndDr.setCdaCrdrId(HelperUtils.getCdaCrDrId());
                                cdaParkingCrAndDr.setFinYearId(finYear);
                                cdaParkingCrAndDr.setBudgetHeadId(budHd);
                                cdaParkingCrAndDr.setGinNo(ginNo);
                                cdaParkingCrAndDr.setUnitId("001321");
                                cdaParkingCrAndDr.setAuthGroupId(authGrId);
                                cdaParkingCrAndDr.setAmount(String.valueOf(fnAmount));
                                cdaParkingCrAndDr.setIscrdr("CR");
                                cdaParkingCrAndDr.setCreatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
                                cdaParkingCrAndDr.setAllocTypeId(allocTypeId);
                                cdaParkingCrAndDr.setIsFlag("0");
                                cdaParkingCrAndDr.setTransactionId(HelperUtils.getTransId());
                                cdaParkingCrAndDr.setAmountType(headCda.get(0).getAmountType());
                                cdaParkingCrAndDr.setIsRevision(0);
                                parkingCrAndDrRepository.save(cdaParkingCrAndDr);
                        }
                    }

                }
                budgetRebase.setAllocFromUnit(hdUnits);
                budgetRebase.setRemCdaBal(String.valueOf(remCdaBal));
                budgetRebaseRepository.save(budgetRebase);

            }
        } else {
            BudgetRebase budgetRebase = new BudgetRebase();
            budgetRebase.setBudgetRebaseId(HelperUtils.getUnitRebased());
            budgetRebase.setRefTransId(refRensId);
            budgetRebase.setFinYear(finYear);
            budgetRebase.setRebaseUnitId(rebaseUnitId);
            budgetRebase.setHeadUnitId(headUnit);
            budgetRebase.setFrmStationId(frmStationId);
            budgetRebase.setToStationId(toStationId);
            budgetRebase.setToHeadUnitId(toHeadUnitId);
            budgetRebase.setOccuranceDate(ConverterUtils.convertDateTotimeStamp(occurrenceDate));
            budgetRebase.setAllocTypeId(null);
            budgetRebase.setBudgetHeadId(null);
            budgetRebase.setAllocAmount(null);
            budgetRebase.setExpAmount(null);
            budgetRebase.setBalAmount(null);
            budgetRebase.setAmountType(null);
            budgetRebase.setLastCbDate(null);
            budgetRebase.setAuthorityId(authorityId);
            budgetRebase.setUserId(hrDataCheck.getPid());
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
        List<CgUnit> unitDataList = cgUnitRepository.findByIsActiveAndIsShipOrderByDescrAsc("1", "1");
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


}

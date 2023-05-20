package com.sdd.service.Impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.*;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.request.BudgetFilterRequest;
import com.sdd.request.CdaFilterData;
import com.sdd.response.*;
import com.sdd.service.BudgetFilterService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class BudgetFilterServiceImpl implements BudgetFilterService {

    @Autowired
    BudgetWiseFilrerRepository budgetWiseFilrerRepository;

    @Autowired
    CdaParkingTransRepository cdaParkingTransRepository;

    @Autowired
    CdaParkingRepository cdaParkingRepository;


    @Autowired
    BudgetFinancialYearRepository budgetFinancialYearRepository;

    @Autowired
    CgUnitRepository cgUnitRepository;


//    @Autowired
//    BudgetAllocationRepository budgetAllocationRepository;

    @Autowired
    AmountUnitRepository amountUnitRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HeaderUtils headerUtils;

    @Autowired
    SubHeadRepository subHeadRepository;

    @Autowired
    private HrDataRepository hrDataRepository;

    @Override
    public ApiResponse<BudgetFilterResponse> saveData(BudgetUnitWiseSubHeadFilter budgetUnitWiseSubHeadFilter) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        if (budgetUnitWiseSubHeadFilter.getCodeSubHeadId() == null || budgetUnitWiseSubHeadFilter.getCodeSubHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }

        if (budgetUnitWiseSubHeadFilter.getCodeMajorHeadId() == null || budgetUnitWiseSubHeadFilter.getCodeMajorHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "MAJOR HEAD ID CAN NOT BE BLANK");
        }

        if (budgetUnitWiseSubHeadFilter.getUnitId() == null || budgetUnitWiseSubHeadFilter.getUnitId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT ID CAN NOT BE BLANK");
        }

        if (budgetUnitWiseSubHeadFilter.getFinYearId() == null || budgetUnitWiseSubHeadFilter.getFinYearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR ID CAN NOT BE BLANK");
        }

        if (budgetUnitWiseSubHeadFilter.getAllocationType() == null || budgetUnitWiseSubHeadFilter.getAllocationType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE ID CAN NOT BE BLANK");
        }


        if (budgetUnitWiseSubHeadFilter.getSubHeadTypeId() == null || budgetUnitWiseSubHeadFilter.getSubHeadTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD TYPE ID ID CAN NOT BE BLANK");
        }


        BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(budgetUnitWiseSubHeadFilter.getFinYearId());
        if (budgetFinancialYear == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
        }

        CgUnit cgToUnit = cgUnitRepository.findByUnit(budgetUnitWiseSubHeadFilter.getUnitId());
        if (cgToUnit == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO UNIT ID");
        }


        BudgetHead budgetHeadData = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(budgetUnitWiseSubHeadFilter.getCodeSubHeadId());
        if (budgetHeadData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TO BUDGET HEAD ID");
        }

        budgetUnitWiseSubHeadFilter.setBuwsFilterId(HelperUtils.getFilterIdId());
        budgetUnitWiseSubHeadFilter.setPidData(hrData.getPid());
        budgetUnitWiseSubHeadFilter.setSubHeadTypeId(budgetUnitWiseSubHeadFilter.getSubHeadTypeId());
        budgetUnitWiseSubHeadFilter.setUpdatedOn(HelperUtils.getCurrentTimeStamp());
        budgetUnitWiseSubHeadFilter.setCreatedOn(HelperUtils.getCurrentTimeStamp());

        budgetWiseFilrerRepository.save(budgetUnitWiseSubHeadFilter);


        BudgetFilterResponse defaultResponse = new BudgetFilterResponse();
        List<BudgetUnitWiseSubHeadFilter> listData = budgetWiseFilrerRepository.findByUnitIdAndFinYearIdAndCodeMajorHeadId(budgetUnitWiseSubHeadFilter.getUnitId(), budgetUnitWiseSubHeadFilter.getFinYearId(), budgetUnitWiseSubHeadFilter.getCodeMajorHeadId());

        if (listData.size() <= 0) {
            List<BudgetHead> majorHedaData = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(budgetUnitWiseSubHeadFilter.getCodeMajorHeadId(), budgetUnitWiseSubHeadFilter.getSubHeadTypeId());

            Collections.sort(majorHedaData, new Comparator<BudgetHead>() {
                public int compare(BudgetHead v1, BudgetHead v2) {
                    return v1.getSerialNumber().compareTo(v2.getSerialNumber());
                }
            });

            List<BudgetHeadResponse> budgetListWithAmount = new ArrayList<>();

            for (Integer i = 0; i < majorHedaData.size(); i++) {
                BudgetHeadResponse budgetHeadResponse = new BudgetHeadResponse();
                BeanUtils.copyProperties(majorHedaData.get(i), budgetHeadResponse);

                double amount = 0;
                AmountUnit amountUnit = null;

                List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetUnitWiseSubHeadFilter.getFinYearId(), majorHedaData.get(i).getBudgetCodeId(), hrData.getUnitId(), budgetUnitWiseSubHeadFilter.getAllocationType(), "0");

//                List<BudgetAllocation> budgetAllocationDetailsList = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(budgetUnitWiseSubHeadFilter.getUnitId(), budgetUnitWiseSubHeadFilter.getFinYearId(), majorHedaData.get(i).getBudgetCodeId(), budgetUnitWiseSubHeadFilter.getAllocationType(), "Approved", "0", "0");
                for (Integer m = 0; m < cdaParkingTrans.size(); m++) {
                    amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(m).getAmountType());
                    amount = amount + Double.parseDouble(cdaParkingTrans.get(m).getRemainingCdaAmount());
                }

                budgetHeadResponse.setTotalAmount(ConverterUtils.addDecimalPoint(amount + ""));
                budgetHeadResponse.setAmountUnit(amountUnit);
                List<CdaFilterData> cdaTransData = new ArrayList<CdaFilterData>();
                for (Integer h = 0; h < cdaParkingTrans.size(); h++) {

                    CdaFilterData cgUnitResponse = new CdaFilterData();
                    BeanUtils.copyProperties(cdaParkingTrans.get(h), cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingTrans.get(h).getGinNo()));
                    cdaTransData.add(cgUnitResponse);
                }

                budgetHeadResponse.setCdaParkingTrans(cdaTransData);


                budgetListWithAmount.add(budgetHeadResponse);
            }


            defaultResponse.setSubHeads(budgetListWithAmount);
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<BudgetFilterResponse>() {
            });

        } else {

            List<BudgetHead> majorHedaDataList = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(budgetUnitWiseSubHeadFilter.getCodeMajorHeadId(), budgetUnitWiseSubHeadFilter.getSubHeadTypeId());
            Collections.sort(majorHedaDataList, new Comparator<BudgetHead>() {
                public int compare(BudgetHead v1, BudgetHead v2) {
                    return v1.getSerialNumber().compareTo(v2.getSerialNumber());
                }
            });


            List<String> mjorDaraFind = new ArrayList<>();

            for (Integer i = 0; i < majorHedaDataList.size(); i++) {
                mjorDaraFind.add(majorHedaDataList.get(i).getBudgetCodeId());
            }

            for (Integer i = 0; i < listData.size(); i++) {
                BudgetUnitWiseSubHeadFilter filData = listData.get(i);
                mjorDaraFind.remove(filData.getCodeSubHeadId());
            }

            List<BudgetHeadResponse> budgetListWithAmount = new ArrayList<>();
            for (Integer i = 0; i < mjorDaraFind.size(); i++) {
                BudgetHead budgetHeadData11 = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(mjorDaraFind.get(i));

                BudgetHeadResponse budgetHeadResponse = new BudgetHeadResponse();
                BeanUtils.copyProperties(budgetHeadData11, budgetHeadResponse);

                double amount = 0;
                AmountUnit amountUnit = null;

                List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetUnitWiseSubHeadFilter.getFinYearId(), budgetHeadData11.getBudgetCodeId(), hrData.getUnitId(), budgetUnitWiseSubHeadFilter.getAllocationType(), "0");

//                List<BudgetAllocation> budgetAllocationDetailsList = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), budgetUnitWiseSubHeadFilter.getFinYearId(), budgetHeadData11.getBudgetCodeId(), budgetUnitWiseSubHeadFilter.getAllocationType(), "Approved", "0", "0");
                for (Integer m = 0; m < cdaParkingTrans.size(); m++) {

                    amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(m).getAmountType());
                    amount = amount + Double.parseDouble(cdaParkingTrans.get(m).getRemainingCdaAmount());
                }
                budgetHeadResponse.setTotalAmount(ConverterUtils.addDecimalPoint(amount + ""));
                budgetHeadResponse.setAmountUnit(amountUnit);
                List<CdaFilterData> cdaTransData = new ArrayList<CdaFilterData>();
                for (Integer h = 0; h < cdaParkingTrans.size(); h++) {

                    CdaFilterData cgUnitResponse = new CdaFilterData();
                    BeanUtils.copyProperties(cdaParkingTrans.get(h), cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingTrans.get(h).getGinNo()));
                    cdaTransData.add(cgUnitResponse);
                }

                budgetHeadResponse.setCdaParkingTrans(cdaTransData);

                budgetListWithAmount.add(budgetHeadResponse);
            }


            defaultResponse.setSubHeads(budgetListWithAmount);
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<BudgetFilterResponse>() {
            });
        }
    }

    @Override
    public ApiResponse<BudgetFilterResponse> getFilterData(BudgetFilterRequest budgetFilterRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        if (budgetFilterRequest.getMajorHead() == null || budgetFilterRequest.getMajorHead().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET HEAD ID ID CAN NOT BE BLANK");
        }

        if (budgetFilterRequest.getUnitId() == null || budgetFilterRequest.getUnitId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT ID CAN NOT BE BLANK");
        }

        if (budgetFilterRequest.getFinyearId() == null || budgetFilterRequest.getFinyearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL YEAR ID CAN NOT BE BLANK");
        }


        if (budgetFilterRequest.getAllocationType() == null || budgetFilterRequest.getAllocationType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE CAN NOT BE BLANK");
        }


        if (budgetFilterRequest.getSubHeadTypeId() == null || budgetFilterRequest.getSubHeadTypeId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD TYPE ID ID CAN NOT BE BLANK");
        }


        BudgetFilterResponse defaultResponse = new BudgetFilterResponse();
        List<BudgetUnitWiseSubHeadFilter> listData = budgetWiseFilrerRepository.findByUnitIdAndFinYearIdAndCodeMajorHeadId(budgetFilterRequest.getUnitId(), budgetFilterRequest.getFinyearId(), budgetFilterRequest.getMajorHead());

        if (listData.size() <= 0) {
            List<BudgetHead> majorHedaData = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(budgetFilterRequest.getMajorHead(), budgetFilterRequest.getSubHeadTypeId());

            Collections.sort(majorHedaData, new Comparator<BudgetHead>() {
                public int compare(BudgetHead v1, BudgetHead v2) {
                    return v1.getSerialNumber().compareTo(v2.getSerialNumber());
                }
            });


            List<BudgetHeadResponse> budgetListWithAmount = new ArrayList<>();

            for (Integer i = 0; i < majorHedaData.size(); i++) {
                BudgetHeadResponse budgetHeadResponse = new BudgetHeadResponse();
                BeanUtils.copyProperties(majorHedaData.get(i), budgetHeadResponse);

                double amount = 0;
                AmountUnit amountUnit = null;

                List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetFilterRequest.getFinyearId(), majorHedaData.get(i).getBudgetCodeId(), hrData.getUnitId(), budgetFilterRequest.getAllocationType(), "0");


//                List<BudgetAllocation> budgetAllocationDetailsList = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), budgetFilterRequest.getFinyearId(), majorHedaData.get(i).getBudgetCodeId(), budgetFilterRequest.getAllocationType(), "Approved", "0", "0");


                for (Integer m = 0; m < cdaParkingTrans.size(); m++) {
                    amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(m).getAmountType());
                    amount = amount + Double.parseDouble(cdaParkingTrans.get(m).getRemainingCdaAmount());
                }

                budgetHeadResponse.setTotalAmount(ConverterUtils.addDecimalPoint(amount + ""));
                budgetHeadResponse.setAmountUnit(amountUnit);
                List<CdaFilterData> cdaTransData = new ArrayList<CdaFilterData>();
                for (Integer h = 0; h < cdaParkingTrans.size(); h++) {

                    CdaFilterData cgUnitResponse = new CdaFilterData();
                    BeanUtils.copyProperties(cdaParkingTrans.get(h), cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingTrans.get(h).getGinNo()));
                    cdaTransData.add(cgUnitResponse);
                }

                budgetHeadResponse.setCdaParkingTrans(cdaTransData);

                budgetListWithAmount.add(budgetHeadResponse);
            }


            defaultResponse.setSubHeads(budgetListWithAmount);
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<BudgetFilterResponse>() {
            });
        } else {

            List<BudgetHead> majorHedaDataList = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(budgetFilterRequest.getMajorHead(), budgetFilterRequest.getSubHeadTypeId());
            Collections.sort(majorHedaDataList, new Comparator<BudgetHead>() {
                public int compare(BudgetHead v1, BudgetHead v2) {
                    return v1.getSerialNumber().compareTo(v2.getSerialNumber());
                }
            });


            List<String> mjorDaraFind = new ArrayList<>();

            for (Integer i = 0; i < majorHedaDataList.size(); i++) {
                mjorDaraFind.add(majorHedaDataList.get(i).getBudgetCodeId());
            }

            for (Integer i = 0; i < listData.size(); i++) {
                BudgetUnitWiseSubHeadFilter filData = listData.get(i);
                mjorDaraFind.remove(filData.getCodeSubHeadId());
            }

            List<BudgetHeadResponse> budgetListWithAmount = new ArrayList<>();
            for (Integer i = 0; i < mjorDaraFind.size(); i++) {
                BudgetHead budgetHeadData11 = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(mjorDaraFind.get(i));

                BudgetHeadResponse budgetHeadResponse = new BudgetHeadResponse();
                BeanUtils.copyProperties(budgetHeadData11, budgetHeadResponse);

                double amount = 0;
                AmountUnit amountUnit = null;
//                List<BudgetAllocation> budgetAllocationDetailsList = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), budgetFilterRequest.getFinyearId(), budgetHeadData11.getBudgetCodeId(), budgetFilterRequest.getAllocationType(), "Approved", "0", "0");
                List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetFilterRequest.getFinyearId(), budgetHeadData11.getBudgetCodeId(), hrData.getUnitId(), budgetFilterRequest.getAllocationType(), "0");


                for (Integer m = 0; m < cdaParkingTrans.size(); m++) {
                    amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(m).getAmountType());
                    amount = amount + Double.parseDouble(cdaParkingTrans.get(m).getRemainingCdaAmount());
                }
                budgetHeadResponse.setTotalAmount(ConverterUtils.addDecimalPoint(amount + ""));
                budgetHeadResponse.setAmountUnit(amountUnit);

                List<CdaFilterData> cdaTransData = new ArrayList<CdaFilterData>();
                for (Integer h = 0; h < cdaParkingTrans.size(); h++) {

                    CdaFilterData cgUnitResponse = new CdaFilterData();
                    BeanUtils.copyProperties(cdaParkingTrans.get(h), cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingTrans.get(h).getGinNo()));
                    cdaTransData.add(cgUnitResponse);
                }

                budgetHeadResponse.setCdaParkingTrans(cdaTransData);


                budgetListWithAmount.add(budgetHeadResponse);
            }

            defaultResponse.setSubHeads(budgetListWithAmount);
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<BudgetFilterResponse>() {
            });
        }
    }

    @Override
    public ApiResponse<BudgetFilterResponse> deleteData(BudgetFilterRequest budgetFilterRequest) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }

        if (budgetFilterRequest.getMajorHead() == null || budgetFilterRequest.getMajorHead().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "BUDGET HEAD ID ID CAN NOT BE BLANK");
        }

        if (budgetFilterRequest.getUnitId() == null || budgetFilterRequest.getUnitId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT ID CAN NOT BE BLANK");
        }

        if (budgetFilterRequest.getSubHeadId() == null || budgetFilterRequest.getSubHeadId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
        }

        if (budgetFilterRequest.getFinyearId() == null || budgetFilterRequest.getFinyearId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FIN YEAR ID CAN NOT BE BLANK");
        }


        if (budgetFilterRequest.getAllocationType() == null || budgetFilterRequest.getAllocationType().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ALLOCATION TYPE CAN NOT BE BLANK");
        }


        List<BudgetUnitWiseSubHeadFilter> filterListData = budgetWiseFilrerRepository.findByUnitIdAndFinYearIdAndCodeSubHeadIdAndCodeMajorHeadId(budgetFilterRequest.getUnitId(), budgetFilterRequest.getFinyearId(), budgetFilterRequest.getSubHeadId(), budgetFilterRequest.getMajorHead());
        if (filterListData.size() <= 0) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "NO DATA FOUND.");
        }
        for (Integer i = 0; i < filterListData.size(); i++) {
            BudgetUnitWiseSubHeadFilter filterData = filterListData.get(i);
            budgetWiseFilrerRepository.delete(filterData);
        }


        BudgetFilterResponse defaultResponse = new BudgetFilterResponse();
        List<BudgetUnitWiseSubHeadFilter> listData = budgetWiseFilrerRepository.findByUnitIdAndFinYearIdAndCodeMajorHeadId(hrData.getUnitId(), budgetFilterRequest.getFinyearId(), budgetFilterRequest.getMajorHead());

        if (listData.size() <= 0) {
            List<BudgetHead> majorHedaData = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(budgetFilterRequest.getMajorHead(), budgetFilterRequest.getSubHeadTypeId());
            Collections.sort(majorHedaData, new Comparator<BudgetHead>() {
                public int compare(BudgetHead v1, BudgetHead v2) {
                    return v1.getSerialNumber().compareTo(v2.getSerialNumber());
                }
            });


            List<BudgetHeadResponse> budgetListWithAmount = new ArrayList<>();

            for (Integer i = 0; i < majorHedaData.size(); i++) {
                BudgetHeadResponse budgetHeadResponse = new BudgetHeadResponse();
                BeanUtils.copyProperties(majorHedaData.get(i), budgetHeadResponse);

                double amount = 0;
                AmountUnit amountUnit = null;
                List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetFilterRequest.getFinyearId(), majorHedaData.get(i).getBudgetCodeId(), hrData.getUnitId(), budgetFilterRequest.getAllocationType(), "0");


//                List<BudgetAllocation> budgetAllocationDetailsList = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), budgetFilterRequest.getFinyearId(), majorHedaData.get(i).getBudgetCodeId(), budgetFilterRequest.getAllocationType(), "Approved", "0", "0");
                for (Integer m = 0; m < cdaParkingTrans.size(); m++) {

                    amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(m).getAmountType());
                    amount = amount + Double.parseDouble(cdaParkingTrans.get(m).getRemainingCdaAmount());
                }

                budgetHeadResponse.setTotalAmount(ConverterUtils.addDecimalPoint(amount + ""));
                budgetHeadResponse.setAmountUnit(amountUnit);
                List<CdaFilterData> cdaTransData = new ArrayList<CdaFilterData>();
                for (Integer h = 0; h < cdaParkingTrans.size(); h++) {

                    CdaFilterData cgUnitResponse = new CdaFilterData();
                    BeanUtils.copyProperties(cdaParkingTrans.get(h), cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingTrans.get(h).getGinNo()));
                    cdaTransData.add(cgUnitResponse);
                }

                budgetHeadResponse.setCdaParkingTrans(cdaTransData);

                budgetListWithAmount.add(budgetHeadResponse);
            }


            defaultResponse.setSubHeads(budgetListWithAmount);
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<BudgetFilterResponse>() {
            });

        } else {


            List<BudgetHead> majorHedaDataList = subHeadRepository.findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(budgetFilterRequest.getMajorHead(), budgetFilterRequest.getSubHeadTypeId());
            Collections.sort(majorHedaDataList, new Comparator<BudgetHead>() {
                public int compare(BudgetHead v1, BudgetHead v2) {
                    return v1.getSerialNumber().compareTo(v2.getSerialNumber());
                }
            });


            List<String> mjorDaraFind = new ArrayList<>();

            for (Integer i = 0; i < majorHedaDataList.size(); i++) {
                mjorDaraFind.add(majorHedaDataList.get(i).getBudgetCodeId());
            }

            for (Integer i = 0; i < listData.size(); i++) {
                BudgetUnitWiseSubHeadFilter filData = listData.get(i);
                mjorDaraFind.remove(filData.getCodeSubHeadId());
            }

            List<BudgetHeadResponse> budgetListWithAmount = new ArrayList<>();
            for (Integer i = 0; i < mjorDaraFind.size(); i++) {
                BudgetHead budgetHeadData11 = subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(mjorDaraFind.get(i));

                BudgetHeadResponse budgetHeadResponse = new BudgetHeadResponse();
                BeanUtils.copyProperties(budgetHeadData11, budgetHeadResponse);

                double amount = 0;
                AmountUnit amountUnit = null;
                List<CdaParkingTrans> cdaParkingTrans = cdaParkingTransRepository.findByFinYearIdAndBudgetHeadIdAndUnitIdAndAllocTypeIdAndIsFlag(budgetFilterRequest.getFinyearId(), budgetHeadData11.getBudgetCodeId(), hrData.getUnitId(), budgetFilterRequest.getAllocationType(), "0");

//                List<BudgetAllocation> budgetAllocationDetailsList = budgetAllocationRepository.findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(hrData.getUnitId(), budgetFilterRequest.getFinyearId(), budgetHeadData11.getBudgetCodeId(), budgetFilterRequest.getAllocationType(), "Approved", "0", "0");
                for (Integer m = 0; m < cdaParkingTrans.size(); m++) {

                    amountUnit = amountUnitRepository.findByAmountTypeId(cdaParkingTrans.get(m).getAmountType());
                    amount = amount + Double.parseDouble(cdaParkingTrans.get(m).getRemainingCdaAmount());
                }

                budgetHeadResponse.setTotalAmount(ConverterUtils.addDecimalPoint(amount + ""));
                budgetHeadResponse.setAmountUnit(amountUnit);
                List<CdaFilterData> cdaTransData = new ArrayList<CdaFilterData>();
                for (Integer h = 0; h < cdaParkingTrans.size(); h++) {

                    CdaFilterData cgUnitResponse = new CdaFilterData();
                    BeanUtils.copyProperties(cdaParkingTrans.get(h), cgUnitResponse);
                    cgUnitResponse.setGinNo(cdaParkingRepository.findByGinNo(cdaParkingTrans.get(h).getGinNo()));
                    cdaTransData.add(cgUnitResponse);
                }

                budgetHeadResponse.setCdaParkingTrans(cdaTransData);

                budgetListWithAmount.add(budgetHeadResponse);
            }

            defaultResponse.setSubHeads(budgetListWithAmount);
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<BudgetFilterResponse>() {
            });
        }


    }

    @Override
    public ApiResponse<DefaultResponse> deleteDataByPid() {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrData = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE BUDGET ALLOCATION");
        }
        List<BudgetUnitWiseSubHeadFilter> filterListData = budgetWiseFilrerRepository.findByPidData(hrData.getPid());
        if (filterListData.size() <= 0) {
            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Data delete successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
            });
        }
        for (Integer i = 0; i < filterListData.size(); i++) {

            BudgetUnitWiseSubHeadFilter filterData = filterListData.get(i);
            budgetWiseFilrerRepository.delete(filterData);
        }

        DefaultResponse defaultResponse = new DefaultResponse();
        defaultResponse.setMsg("Data delete successfully");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }
}

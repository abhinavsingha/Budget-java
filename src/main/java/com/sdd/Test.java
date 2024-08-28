 package com.sdd;
 
 import com.sdd.entities.*;
 import com.sdd.entities.repository.*;
 import com.sdd.exception.SDDException;
 import com.sdd.jwt.HeaderUtils;
 import com.sdd.jwtParse.TokenParseData;
 import com.sdd.request.CdaAndAllocationDataRequest;
 import com.sdd.request.CdaParkingCrAndDrResponse;
 import com.sdd.response.*;
 import com.sdd.utils.ConverterUtils;
 import org.springframework.beans.BeanUtils;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.http.HttpStatus;

 import java.math.BigDecimal;
 import java.util.*;

 public class Test {
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
 
 
     public void main() {
         List<CgUnit> units=new ArrayList<>();
         units=cgUnitRepository.findAll();
         for (CgUnit unit : units) {
             BudgetAllocationResponse allocationresponse = getAllocation(unit);
             for(int i=0;i<allocationresponse.getBudgetResponseist().size();i++){
                 CdaAndAllocationDataRequest request=new CdaAndAllocationDataRequest();
                 request.setAmountType(allocationresponse.getBudgetResponseist().get(i).getAmountUnit().getAmountTypeId());
                 request.setAllocationTypeId(allocationresponse.getBudgetResponseist().get(i).getAllocTypeId().getAllocTypeId());
                 request.setBudgetHeadId(allocationresponse.getBudgetResponseist().get(i).getSubHead().getBudgetCodeId());
                 request.setFinancialYearId(allocationresponse.getBudgetResponseist().get(i).getFinYear().getSerialNo());
                 CdaAndAllocationDataResponse expResponse = getExp(request);

             }

         }
     }

     private CdaAndAllocationDataResponse getExp(CdaAndAllocationDataRequest cdaRequest) {

             CdaAndAllocationDataResponse mainResponse = new CdaAndAllocationDataResponse();

             String token = headerUtils.getTokeFromHeader();
             TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

             if (cdaRequest.getBudgetHeadId() == null || cdaRequest.getBudgetHeadId().isEmpty()) {
                 throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "SUB HEAD ID CAN NOT BE BLANK");
             }

             if (cdaRequest.getFinancialYearId() == null || cdaRequest.getFinancialYearId().isEmpty()) {
                 throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FINANCIAL ID CAN NOT BE BLANK");
             }


             if (cdaRequest.getAmountType() == null || cdaRequest.getAmountType().isEmpty()) {
                 throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "AMOUNT TYPE ID CAN NOT BE BLANK");
             }

             AmountUnit amountUnit = amountUnitRepository.findByAmountTypeId(cdaRequest.getAmountType());
             if (amountUnit == null) {
                 throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID AMOUNT UNIT ID");
             }

             BudgetFinancialYear budgetFinancialYear = budgetFinancialYearRepository.findBySerialNo(cdaRequest.getFinancialYearId());
             if (budgetFinancialYear == null) {
                 throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID FINANCIAL YEAR ID");
             }

             AllocationType allocationType = allocationRepository.findByAllocTypeId(cdaRequest.getAllocationTypeId());
             if (allocationType == null) {
                 throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ALLOCATION TYPE ID");
             }

             double totalExpWithAllocation = 0;
             String currentUnitId = cdaRequest.getUnitId();
             List<CgUnit> unitList = cgUnitRepository.findByBudGroupUnitLike("%" + currentUnitId + "%");
             unitList.remove(cgUnitRepository.findByUnit(currentUnitId));
             List<BudgetAllocation> budgetAllocationData = new ArrayList<BudgetAllocation>();
             for (CgUnit cgUnit : unitList) {
                 List<BudgetAllocation> dataBudget = budgetAllocationRepository.findByToUnitAndFromUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(cgUnit.getUnit(), cdaRequest.getUnitId(), cdaRequest.getFinancialYearId(), cdaRequest.getBudgetHeadId(), cdaRequest.getAllocationTypeId(), "0", "0", "Approved");
                 for (BudgetAllocation budgetAllocation : dataBudget) {
                     double allocationAMount = Double.parseDouble(budgetAllocation.getAllocationAmount());
                     if (allocationAMount > 0) {
                         AmountUnit amountUnit1 = amountUnitRepository.findByAmountTypeId(budgetAllocation.getAmountType());
                         budgetAllocationData.add(budgetAllocation);
                         totalExpWithAllocation = ConverterUtils.doubleSum(totalExpWithAllocation ,(allocationAMount * amountUnit1.getAmount()));
                     }
                 }
             }
             mainResponse.setBudgetAllocationData(budgetAllocationData);

             HashMap<String, CdaParkingTransSubResponse> subHeadData = new LinkedHashMap<>();
             List<ContigentBill> contingentBills = contigentBillRepository.findByFinYearAndBudgetHeadIDAndIsUpdateAndIsFlagAndCbUnitId(cdaRequest.getFinancialYearId(), cdaRequest.getBudgetHeadId(), "0", "0", cdaRequest.getUnitId());

             for (ContigentBill contigentBill : contingentBills) {
                 List<CdaParkingCrAndDr> cdaParkingCrAndDrsList = parkingCrAndDrRepository.findByTransactionIdAndIsFlag(contigentBill.getCbId(), "0");
                 for (CdaParkingCrAndDr cdaParkingCrAndDrs : cdaParkingCrAndDrsList) {
                     CdaParking cdaName = cdaParkingRepository.findByGinNo(cdaParkingCrAndDrs.getGinNo());
                     if (subHeadData.containsKey(cdaName.getCdaName())) {
                         CdaParkingTransSubResponse cdaParkingTransSubResponses = subHeadData.get(cdaName.getCdaName());
                         double totalBillAmount = Double.parseDouble(cdaParkingCrAndDrs.getAmount()) / amountUnit.getAmount();
                         double totalParking = Double.parseDouble(cdaParkingTransSubResponses.getTotalParkingAmount());
                         double totalRemenig = Double.parseDouble(cdaParkingTransSubResponses.getRemainingCdaAmount());
                         cdaParkingTransSubResponses.setTotalParkingAmount(new BigDecimal(ConverterUtils.doubleSum(totalBillAmount , totalParking) + "").toPlainString());
                         cdaParkingTransSubResponses.setRemainingCdaAmount(new BigDecimal(ConverterUtils.doubleSum(totalBillAmount , totalRemenig) + "").toPlainString());
                         subHeadData.put(cdaName.getCdaName(), cdaParkingTransSubResponses);
                     } else {
                         double totalBillAmount = Double.parseDouble(cdaParkingCrAndDrs.getAmount());
                         double totalBill = totalBillAmount / amountUnit.getAmount();
                         CdaParkingTransSubResponse cdaParkingTransResponse = new CdaParkingTransSubResponse();
                         cdaParkingTransResponse.setFinYearId(budgetFinancialYearRepository.findBySerialNo(cdaParkingCrAndDrs.getFinYearId()));
                         cdaParkingTransResponse.setBudgetHead(subHeadRepository.findByBudgetCodeIdOrderBySerialNumberAsc(cdaParkingCrAndDrs.getBudgetHeadId()));
                         cdaParkingTransResponse.setGinNo(cdaName);
                         cdaParkingTransResponse.setAllocationType(allocationRepository.findByAllocTypeId(cdaParkingCrAndDrs.getAllocTypeId()));
                         cdaParkingTransResponse.setRemainingCdaAmount(new BigDecimal(ConverterUtils.addDoubleValue(totalBill) + "").toPlainString());
                         cdaParkingTransResponse.setTotalParkingAmount(new BigDecimal(ConverterUtils.addDoubleValue(totalBill) + "").toPlainString());
                         cdaParkingTransResponse.setUpdatedOn(cdaParkingCrAndDrs.getUpdatedOn());
                         cdaParkingTransResponse.setTransactionId(cdaParkingCrAndDrs.getTransactionId());
                         cdaParkingTransResponse.setCreatedOn(cdaParkingCrAndDrs.getCreatedOn());
                         cdaParkingTransResponse.setAmountUnit(amountUnitRepository.findByAmountTypeId(cdaParkingCrAndDrs.getAmountType()));
                         cdaParkingTransResponse.setAuthGroupId(cdaParkingCrAndDrs.getAuthGroupId());
                         cdaParkingTransResponse.setUnitId(cdaParkingCrAndDrs.getUnitId());
                         subHeadData.put(cdaName.getCdaName(), cdaParkingTransResponse);
                     }
                 }
             }
             mainResponse.setSubHeadData(subHeadData);
             mainResponse.setTotalExpWithAllocation(totalExpWithAllocation);
             return (mainResponse);
         }


     BudgetAllocationResponse getAllocation(CgUnit unit){
 
 
             List<BudgetAllocationSubResponse> budgetAllocationList = new ArrayList<BudgetAllocationSubResponse>();
 
 
 
             String token = headerUtils.getTokeFromHeader();
             TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
 
 
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
 
                 List<BudgetAllocation> budgetAllocations11 = budgetAllocationRepository.findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(head.getBudgetCodeId(), unit.getUnit(), budgetFinancialYear.getSerialNo(), allocationType.get(0).getAllocTypeId(), "0", "0", "Approved");
 
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
 
 
                 List<CgUnit> subUnitList = cgUnitRepository.findBySubUnitOrderByDescrAsc(unit.getUnit());
 
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
                     cgUnitResponse.setRemainingAmount(cdaParkingCrAndDr.getRemainingCdaAmount());
 
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
 
 
             return (budgetAllocationResponse);
         }
 
 
 
 }

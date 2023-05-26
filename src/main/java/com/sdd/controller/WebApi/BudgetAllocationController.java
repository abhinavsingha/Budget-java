package com.sdd.controller.WebApi;


import com.sdd.entities.*;
import com.sdd.request.*;
import com.sdd.response.*;

import com.sdd.service.BudgetAllocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/budgetAllocation")
@Slf4j
public class BudgetAllocationController {

    @Autowired
    private BudgetAllocationService budgetAllocationService;

    @GetMapping("/getBudgetFinYear")
    public ResponseEntity<ApiResponse<List<BudgetFinancialYear>>> getBudgetFinYear() {
        return new ResponseEntity<>(budgetAllocationService.getBudgetFinYear(), HttpStatus.OK);
    }

    @GetMapping("/getAllocationAllData")
    public ResponseEntity<ApiResponse<List<AllocationType>>> getAllocationAllData() {
        return new ResponseEntity<>(budgetAllocationService.getAllocationAllData(), HttpStatus.OK);
    }

    @GetMapping("/getAllocationByFinYear/{finYearId}")
    public ResponseEntity<ApiResponse<List<AllocationType>>> getAllocationByFinYear(@PathVariable("finYearId") String finYearId) {
        return new ResponseEntity<>(budgetAllocationService.getAllocationByFinYear(finYearId), HttpStatus.OK);
    }


    @PostMapping("/updateAllocation")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateAllocation(@RequestBody AllocationType allocationType) {
        return new ResponseEntity<>(budgetAllocationService.updateAllocation(allocationType), HttpStatus.OK);
    }



    @GetMapping("/getAllocationType")
    public ResponseEntity<ApiResponse<List<AllocationType>>> getAllocationType() {
        return new ResponseEntity<>(budgetAllocationService.getAllocationType(), HttpStatus.OK);
    }


    @GetMapping("/getSubHeadsData")
    public ResponseEntity<ApiResponse<List<BudgetHead>>> getSubHeadsData() {
        return new ResponseEntity<>(budgetAllocationService.getSubHeadsData(), HttpStatus.OK);
    }


    @PostMapping("/getSubHeadListWithAmount")
    public ResponseEntity<ApiResponse<List<BudgetHeadResponse>>> getSubHeadListWithAmount(@RequestBody BudgetHeadRequest budgetHeadRequest) {
        return new ResponseEntity<>(budgetAllocationService.getSubHeadListWithAmount(budgetHeadRequest), HttpStatus.OK);
    }

    @GetMapping("/getMajorData")
    public ResponseEntity<ApiResponse<BudgetResponseWithToken>> getSubHeads() {
        return new ResponseEntity<>(budgetAllocationService.getSubHeads(), HttpStatus.OK);
    }

    @GetMapping("/getCgUnitData")
    public ResponseEntity<ApiResponse<List<CgUnitResponse>>> getCgUnitData() {
        return new ResponseEntity<>(budgetAllocationService.getCgUnitData(), HttpStatus.OK);
    }

    @GetMapping("/getCgUnitDataWithPurposeCode")
    public ResponseEntity<ApiResponse<List<CgUnitResponse>>> getCgUnitDataWithPurposeCode() {
        return new ResponseEntity<>(budgetAllocationService.getCgUnitDataWithPurposeCode(), HttpStatus.OK);
    }


    @GetMapping("/getCgUnitDataForBudgetReceipt")
    public ResponseEntity<ApiResponse<List<CgUnitResponse>>> getCgUnitDataForBudgetRecipt() {
        return new ResponseEntity<>(budgetAllocationService.getCgUnitDataForBudgetRecipt(), HttpStatus.OK);
    }

    @PostMapping("/getAllSubHeadByMajorHead")
    public ResponseEntity<ApiResponse<List<BudgetHead>>> getAuthorityTypes(@RequestBody BudgetHeadRequest majorHead) {
        return new ResponseEntity<>(budgetAllocationService.getSubHeadsDataByMajorHead(majorHead), HttpStatus.OK);
    }


    @PostMapping("/getAvailableFund")
    public ResponseEntity<ApiResponse<AvilableFundResponse>> getAvailableFund(@RequestBody GetAmountRequest  budgetHeadId) {
        return new ResponseEntity<>(budgetAllocationService.findAvailableAmount(budgetHeadId), HttpStatus.OK);
    }


    @PostMapping("/getAvailableFundFindByUnitIdAndFinYearId")
    public ResponseEntity<ApiResponse<AvilableFundResponse>> getAvailableFundFindByUnitIdAndFinYearId(@RequestBody GetAvilableFundRequest getAvilableFundRequest) {
        return new ResponseEntity<>(budgetAllocationService.getAvailableFundFindByUnitIdAndFinYearId(getAvilableFundRequest), HttpStatus.OK);
    }


    @GetMapping("/getSubHeadType")
    public ResponseEntity<ApiResponse<List<SubHeadVotedOrChargedType>>> getSubHeadType() {
        return new ResponseEntity<>(budgetAllocationService.getSubHeadType(), HttpStatus.OK);
    }


    @GetMapping("/getAvailableFundData")
    public ResponseEntity<ApiResponse<AvilableFundResponse>> getAvailableFundData() {
        return new ResponseEntity<>(budgetAllocationService.getAvailableFundData(), HttpStatus.OK);
    }


    @PostMapping("/findBudgetAllocationFinYearAndUnit")
    public ResponseEntity<ApiResponse<List<FindBudgetResponse>>> findBudgetAllocationFinYearAndUnit(@RequestBody FindBudgetRequest findBudgetRequest) {
        return new ResponseEntity<>(budgetAllocationService.findBudgetAllocationFinYearAndUnit(findBudgetRequest), HttpStatus.OK);
    }


    @PostMapping("/budgetAllocationReport")
    public ResponseEntity<ApiResponse<BudgetAllocationResponse>> budgetAllocationReport(@RequestBody BudgetAllocationReportRequest budgetAllocationReportRequest) {
        return new ResponseEntity<>(budgetAllocationService.budgetAllocationReport(budgetAllocationReportRequest), HttpStatus.OK);
    }


    @PostMapping("/getBudgetAllocationData")
    public ResponseEntity<ApiResponse<BudgetAllocationResponse>> getBudgetAllocationData() {
        return new ResponseEntity<>(budgetAllocationService.getBudgetAllocationData(), HttpStatus.OK);
    }


    @GetMapping("/getAlGroupId/{groupId}")
    public ResponseEntity<ApiResponse<BudgetAllocationResponse>> getAlGroupId(@PathVariable("groupId") String groupId) {
        return new ResponseEntity<>(budgetAllocationService.getBudgetAllocationDataGroupId(groupId), HttpStatus.OK);
    }



    @GetMapping("/getAllGroupIdAndUnitId/{groupId}")
    public ResponseEntity<ApiResponse<BudgetAllocationResponse>> getAllGroupIdAndUnitId(@PathVariable("groupId") String groupId) {
        return new ResponseEntity<>(budgetAllocationService.getAllGroupIdAndUnitId(groupId), HttpStatus.OK);
    }



    @PostMapping("/saveBudgetAllocationSubHeadWise")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> saveBudgetAllocationSubHeadWise(@RequestBody BudgetAllocationSaveRequest budgetAllocationSaveRequest) {
        return new ResponseEntity<>(budgetAllocationService.saveBudgetAllocationSubHeadWise(budgetAllocationSaveRequest), HttpStatus.OK);
    }


    @PostMapping("/saveBudgetAllocationUnitWise")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> saveBudgetAllocationUnitWise(@RequestBody BudgetAllocationSaveUnitRequest budgetAllocationSaveUnitRequest) {
        return new ResponseEntity<>(budgetAllocationService.saveBudgetAllocationUnitWise(budgetAllocationSaveUnitRequest), HttpStatus.OK);
    }


    @PostMapping("/updateBudgetAllocation")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> updateBudgetAllocationSubHeadWise(@RequestBody BudgetAllocationUpdateRequest budgetAllocationSaveRequest) {
        return new ResponseEntity<>(budgetAllocationService.updateBudgetAllocationSubHeadWise(budgetAllocationSaveRequest), HttpStatus.OK);
    }


    @PostMapping("/budgetDelete")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> budgetDelete(@RequestBody BudgetDeleteRequest budgetDeleteRequest) {
        return new ResponseEntity<>(budgetAllocationService.budgetDelete(budgetDeleteRequest), HttpStatus.OK);
    }


    @PostMapping("/approveBudgetOrReject")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> approveBudget(@RequestBody BudgetApproveRequest budgetApproveRequest) {
        return new ResponseEntity<>(budgetAllocationService.approveBudgetOrReject(budgetApproveRequest), HttpStatus.OK);
    }


//    @PostMapping("/getBudgetRevisionData")
//    public ResponseEntity<ApiResponse<List<BudgetRevisionResponse>>> getBudgetRevisionData(@RequestBody BudgetAllocationReportRequest budgetAllocationReportRequest) {
//        return new ResponseEntity<>(budgetAllocationService.getBudgetRevisionData(budgetAllocationReportRequest), HttpStatus.OK);
//    }
//
//
//    @PostMapping("/saveBudgetRevisionData")
//    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> saveBudgetRevisionData(@RequestBody BudgetAllocationSaveUnitRequest budgetAllocationSaveUnitRequest) {
//        return new ResponseEntity<>(budgetAllocationService.saveBudgetRevisonData(budgetAllocationSaveUnitRequest), HttpStatus.OK);
//    }


    @PostMapping("/saveAuthData")
    public ResponseEntity<ApiResponse<DefaultResponse>> saveAuthData(@RequestBody AuthRequest authRequest) {
        return new ResponseEntity<>(budgetAllocationService.saveAuthData(authRequest), HttpStatus.OK);
    }


    @GetMapping("/getApprovedBudgetData")
    public ResponseEntity<ApiResponse<BudgetAllocationResponse>> getApprovedBudgetData() {
        return new ResponseEntity<>(budgetAllocationService.getApprovedBudgetData(), HttpStatus.OK);
    }


    @PostMapping("/getBudgetRevisionData")
    public ResponseEntity<ApiResponse<List<BudgetReviResp>>> getBudgetRevisionData(@RequestBody BudgetReviReq budgetRivRequest) {
        return new ResponseEntity<>(budgetAllocationService.getBudgetRevisionData(budgetRivRequest), HttpStatus.OK);
    }


    @PostMapping("/saveBudgetRevision")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> saveBudgetRevision(@RequestBody BudgetAllocationSaveUnitRequest budgetAllocationSaveRequestList) {
        return new ResponseEntity<>(budgetAllocationService.saveBudgetRevision(budgetAllocationSaveRequestList), HttpStatus.OK);
    }


    @PostMapping("/approveRevisionBudgetOrReject")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> approveRevisionBudgetOrReject(@RequestBody BudgetApproveRequest budgetApproveRequest) {
        return new ResponseEntity<>(budgetAllocationService.approveRivisonBudgetOrReject(budgetApproveRequest), HttpStatus.OK);
    }


    @GetMapping("/getAllRevisionGroupId/{groupId}")
    public ResponseEntity<ApiResponse<BudgetAllocationResponse>> getAllRevisionGroupId(@PathVariable("groupId") String groupId) {
        return new ResponseEntity<>(budgetAllocationService.getAllRevisionGroupId(groupId), HttpStatus.OK);
    }

    @GetMapping("/getAllCgUnitData")
    public ResponseEntity<ApiResponse<List<CgUnitResponse>>> getAllCgUnitData() {
        return new ResponseEntity<>(budgetAllocationService.getAllCgUnitData(), HttpStatus.OK);
    }

}

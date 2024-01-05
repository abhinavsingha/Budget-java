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

    @GetMapping("/getCgUnitWithoutMOD")
    public ResponseEntity<ApiResponse<List<CgUnitResponse>>> getCgUnitWithoutMOD() {
        return new ResponseEntity<>(budgetAllocationService.getCgUnitWithoutMOD(), HttpStatus.OK);
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
    public ResponseEntity<ApiResponse<AvilableFundResponse>> getAvailableFund(@RequestBody GetAmountRequest budgetHeadId) {
        return new ResponseEntity<>(budgetAllocationService.findAvailableAmount(budgetHeadId), HttpStatus.OK);
    }

    @PostMapping("/getAvailableFundCB")
    public ResponseEntity<ApiResponse<AvilableFundResponse>> getAvailableFundCb(@RequestBody GetAmountRequest budgetHeadId) {
        return new ResponseEntity<>(budgetAllocationService.getAvailableFundCB(budgetHeadId), HttpStatus.OK);
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

    @GetMapping("/getAllGroupIdAndUnitIdRevisionCase/{groupId}")
    public ResponseEntity<ApiResponse<BudgetAllocationResponse>> getAllGroupIdAndUnitIdRevisionCase(@PathVariable("groupId") String groupId) {
        return new ResponseEntity<>(budgetAllocationService.getAllGroupIdAndUnitIdRevisionCase(groupId), HttpStatus.OK);
    }


    @PostMapping("/saveBudgetAllocationSubHeadWise")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> saveBudgetAllocationSubHeadWise(@RequestBody BudgetAllocationSaveRequest budgetAllocationSaveRequest) {
        return new ResponseEntity<>(budgetAllocationService.saveBudgetAllocationSubHeadWise(budgetAllocationSaveRequest), HttpStatus.OK);
    }


    @PostMapping("/saveBudgetAllocationUnitWise")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> saveBudgetAllocationUnitWise(@RequestBody BudgetAllocationSaveUnitRequest budgetAllocationSaveUnitRequest) {
        return new ResponseEntity<>(budgetAllocationService.saveBudgetAllocationUnitWise(budgetAllocationSaveUnitRequest), HttpStatus.OK);
    }


    @PostMapping("/saveBudgetAllocationSubHeadWiseEdit")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> saveBudgetAllocationSubHeadWiseEdit(@RequestBody BudgetAllocationSaveRequestEdit budgetAllocationSaveRequestEdit) {
        return new ResponseEntity<>(budgetAllocationService.saveBudgetAllocationSubHeadWiseEdit(budgetAllocationSaveRequestEdit), HttpStatus.OK);
    }


    @PostMapping("/saveBudgetAllocationUnitWiseEdit")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> saveBudgetAllocationUnitWiseEdit(@RequestBody BudgetAllocationSaveUnitRequestEdit budgetAllocationSaveUnitRequestEdit) {
        return new ResponseEntity<>(budgetAllocationService.saveBudgetAllocationUnitWiseEdit(budgetAllocationSaveUnitRequestEdit), HttpStatus.OK);
    }


    @PostMapping("/updateBudgetAllocation")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> updateBudgetAllocationSubHeadWise(@RequestBody BudgetAllocationUpdateRequest budgetAllocationSaveRequest) {
        return new ResponseEntity<>(budgetAllocationService.updateBudgetAllocationSubHeadWise(budgetAllocationSaveRequest), HttpStatus.OK);
    }


    @PostMapping("/budgetDelete")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> budgetDelete(@RequestBody BudgetDeleteRequest budgetDeleteRequest) {
        return new ResponseEntity<>(budgetAllocationService.budgetDelete(budgetDeleteRequest), HttpStatus.OK);
    }


    @PostMapping("/budgetApprove")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> approveBudget(@RequestBody BudgetApproveRequest budgetApproveRequest) {
        return new ResponseEntity<>(budgetAllocationService.budgetApprove(budgetApproveRequest), HttpStatus.OK);
    }

    @PostMapping("/budgetReject")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> budgetReject(@RequestBody BudgetApproveRequest budgetApproveRequest) {
        return new ResponseEntity<>(budgetAllocationService.budgetReject(budgetApproveRequest), HttpStatus.OK);
    }


    @PostMapping("/saveAuthData")
    public ResponseEntity<ApiResponse<DefaultResponse>> saveAuthData(@RequestBody AuthRequest authRequest) {
        return new ResponseEntity<>(budgetAllocationService.saveAuthData(authRequest), HttpStatus.OK);
    }


    @GetMapping("/getApprovedBudgetData")
    public ResponseEntity<ApiResponse<BudgetAllocationResponse>> getApprovedBudgetData() {
        return new ResponseEntity<>(budgetAllocationService.getApprovedBudgetData(), HttpStatus.OK);
    }



    @PostMapping("/getBudgetRevisionData33")
    public ResponseEntity<ApiResponse<List<BudgetReviResp>>> getBudgetRevisionData33(@RequestBody BudgetReviReq budgetRivRequest) {
        return new ResponseEntity<>(budgetAllocationService.getBudgetRevisionData33(budgetRivRequest), HttpStatus.OK);
    }

    @PostMapping("/getBudgetRevisionData3")
    public ResponseEntity<ApiResponse<List<BudgetReviResp>>> getBudgetRevisionData3(@RequestBody BudgetReviReq budgetRivRequest) {
        return new ResponseEntity<>(budgetAllocationService.getBudgetRevisionData3(budgetRivRequest), HttpStatus.OK);
    }


    @PostMapping("/approveRevisionBudgetOrReject3")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> approveRevisionBudgetOrReject3(@RequestBody BudgetApproveRequest budgetApproveRequest) {
        return new ResponseEntity<>(budgetAllocationService.approveRivisonBudgetOrReject3(budgetApproveRequest), HttpStatus.OK);
    }


    @PostMapping("/saveBudgetRevision3")
    public ResponseEntity<ApiResponse<BudgetAllocationSaveResponse>> saveBudgetRevision3(@RequestBody BudgetAllocationSaveUnitRequest budgetAllocationSaveRequestList) {
        return new ResponseEntity<>(budgetAllocationService.saveBudgetRevision3(budgetAllocationSaveRequestList), HttpStatus.OK);
    }


    @PostMapping("/saveAuthDataRevisionSaveCbAsAllocation")
    public ResponseEntity<ApiResponse<DefaultResponse>> saveAuthDataRevisionSaveCbAsAllocation(@RequestBody AuthRequest authRequest) {
        return new ResponseEntity<>(budgetAllocationService.saveAuthDataRevisionSaveCbAsAllocation(authRequest), HttpStatus.OK);
    }


    @GetMapping("/getAllRevisionGroupId/{groupId}")
    public ResponseEntity<ApiResponse<BudgetAllocationResponse>> getAllRevisionGroupId(@PathVariable("groupId") String groupId) {
        return new ResponseEntity<>(budgetAllocationService.getAllRevisionGroupId(groupId), HttpStatus.OK);
    }

    @GetMapping("/getAllCgUnitData")
    public ResponseEntity<ApiResponse<List<CgUnitResponse>>> getAllCgUnitData() {
        return new ResponseEntity<>(budgetAllocationService.getAllCgUnitData(), HttpStatus.OK);
    }

    @GetMapping("/getAllSubHeadList")
    public ResponseEntity<ApiResponse<BudgetAllocationResponse>> getAllSubHeadList() {
        return new ResponseEntity<>(budgetAllocationService.getAllSubHeadList(), HttpStatus.OK);
    }


}

package com.sdd.controller.WebApi;

import com.sdd.entities.AmountUnit;
import com.sdd.request.DashBoardRequest;
import com.sdd.request.DashExpResquest;
import com.sdd.request.UnitWiseAllocationReport;
import com.sdd.response.*;
import com.sdd.service.DashBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/dashBoard")
@Slf4j
public class DashBoardController {

  @Autowired     private DashBoardService dashBoardService;

  @PostMapping("/getDashBoardDta")
  public ResponseEntity<ApiResponse<DashBoardResponse>> getBudgetFinYear(
          DashBoardRequest dashBoardRequest) {
    return new ResponseEntity<>(dashBoardService.getDashBoardData(dashBoardRequest), HttpStatus.OK);
  }

  @GetMapping("/updateInboxOutBox")
  public ResponseEntity<ApiResponse<DashBoardResponse>> updateInboxOutBox() {
    return new ResponseEntity<>(dashBoardService.updateInboxOutBox(), HttpStatus.OK);
  }

  @GetMapping("/getUiData/{roleId}")
  public ResponseEntity<ApiResponse<UiResponse>> getUiData(
          @PathVariable(value = "roleId") String roleId) {
    return new ResponseEntity<>(dashBoardService.getUiData(roleId), HttpStatus.OK);
  }

  @GetMapping("/showAllData")
  public ResponseEntity<ApiResponse<DashBoardResponse>> showAllData() {
    return new ResponseEntity<>(dashBoardService.showAllData(), HttpStatus.OK);
  }

  @GetMapping("/showAllAmountUnit")
  public ResponseEntity<ApiResponse<List<AmountUnit>>> getAllAmountUnit() {
    return new ResponseEntity<>(dashBoardService.getAllAmountUnit(), HttpStatus.OK);
  }

  @GetMapping("/getSubHeadWiseExpenditureByUnitIdFinYearIdAllocationTypeIdSubHeadTypeId/{unitId}/{finYearId}/{subHeadTypeId}/{allocationTypeId}/{amountTypeId}")
  public ResponseEntity<ApiResponse<List<DashBoardExprnditureResponse>>> getSubHeadWiseExpenditureByUnitIdFinYearIdAllocationTypeIdSubHeadTypeId(@PathVariable(value = "unitId") String unitId, @PathVariable(value = "finYearId") String finYearId, @PathVariable(value = "subHeadTypeId") String subHeadTypeId, @PathVariable(value = "allocationTypeId") String allocationTypeId, @PathVariable(value = "amountTypeId") String amountTypeId) {
    return new ResponseEntity<>(dashBoardService.getSubHeadWiseExpenditureByUnitIdFinYearIdAllocationTypeIdSubHeadTypeId(unitId, finYearId, subHeadTypeId, allocationTypeId,amountTypeId), HttpStatus.OK);
  }

  @PostMapping("/getDashBordSubHeadwiseExpenditure")
  public ResponseEntity<ApiResponse<List<SubHeadWiseExpResp>>> getDashBordSubHeadwiseExpenditure(@RequestBody DashExpResquest dashExpResquest ) {
    return new ResponseEntity<>(dashBoardService.getDashBordSubHeadwiseExpenditure(dashExpResquest), HttpStatus.OK);
  }
}

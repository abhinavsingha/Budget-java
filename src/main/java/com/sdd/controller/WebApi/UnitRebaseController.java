package com.sdd.controller.WebApi;

import com.sdd.entities.BudgetFinancialYear;
import com.sdd.entities.CgStation;
import com.sdd.request.MangeRebaseRequest;
import com.sdd.request.RebaseBudgetHistory;
import com.sdd.request.UnitRebaseSaveReq;
import com.sdd.response.*;
import com.sdd.service.MangeRebaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/unitRebaseController")
@Slf4j
public class UnitRebaseController {

    @Autowired
    private MangeRebaseService mangeRebaseService;


    @PostMapping("/saveRebase")
    public ResponseEntity<ApiResponse<DefaultResponse>> budgetAllocationReport(@RequestBody MangeRebaseRequest mangeRebaseRequest) {
        return new ResponseEntity<>(mangeRebaseService.saveRebaes(mangeRebaseRequest), HttpStatus.OK);
    }


    @GetMapping("/getAllStation")
    public ResponseEntity<ApiResponse<List<CgStation>>> getAllStation() {
        return new ResponseEntity<>(mangeRebaseService.getAllStation(), HttpStatus.OK);
    }

    @GetMapping("/getAllUnit")
    public ResponseEntity<ApiResponse<List<CgUnitResponse>>> getAllUnit() {
        return new ResponseEntity<>(mangeRebaseService.getAllUnit(), HttpStatus.OK);
    }

    @GetMapping("/getAllBudgetFinYr")
    public ResponseEntity<ApiResponse<List<BudgetFinancialYear>>> getAllBudgetFinYr() {
        return new ResponseEntity<>(mangeRebaseService.getAllBudgetFinYr(), HttpStatus.OK);
    }


    @GetMapping("/getAllStationById/{stationId}")
    public ResponseEntity<ApiResponse<CgStation>> getAllStation(@PathVariable(value = "stationId") String stationId) {
        return new ResponseEntity<>(mangeRebaseService.getAllStationById(stationId), HttpStatus.OK);
    }


    @GetMapping("/getAllUnitRebaseData/{finYear}/{unit}")
    public ResponseEntity<ApiResponse<List<RebaseBudgetHistory>>> getAllUnitRebaseData(@PathVariable(value = "finYear") String finYear, @PathVariable(value = "unit") String unit) {
        return new ResponseEntity<>(mangeRebaseService.getAllUnitRebaseData(finYear, unit), HttpStatus.OK);
    }

    @PostMapping("/saveUnitRebase")
    public ResponseEntity<ApiResponse<DefaultResponse>> saveUnitRebase(@RequestBody UnitRebaseSaveReq req) {
        return new ResponseEntity<>(mangeRebaseService.saveUnitRebase(req), HttpStatus.OK);
    }



//    @PostMapping("/saveUnitRebaseArvind")
//    public ResponseEntity<ApiResponse<DefaultResponse>> saveUnitRebaseArvind(@RequestBody UnitRebaseSaveReq req) {
//        return new ResponseEntity<>(mangeRebaseService.saveUnitRebaseArvind(req), HttpStatus.OK);
//    }

    @GetMapping("/getAllIsShipCgUnitData")
    public ResponseEntity<ApiResponse<List<CgUnitResponse>>> getAllIsShipCgUnitData() {
        return new ResponseEntity<>(mangeRebaseService.getAllIsShipCgUnitData(), HttpStatus.OK);
    }

    @GetMapping("/getUnitRebaseNotificationData/{authGrpId}")
    public ResponseEntity<ApiResponse<List<RebaseNotificationResp>>> getUnitRebaseNotificationData(@PathVariable(value = "authGrpId") String authGrpId) {
        return new ResponseEntity<>(mangeRebaseService.getUnitRebaseNotificationData(authGrpId), HttpStatus.OK);
    }

    @GetMapping("/getIsShipCgUnit")
    public ResponseEntity<ApiResponse<List<CgUnitResponse>>> getIsShipCgUnit() {
        return new ResponseEntity<>(mangeRebaseService.getIsShipCgUnit(), HttpStatus.OK);
    }

}

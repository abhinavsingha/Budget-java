package com.sdd.controller.WebApi;


import com.sdd.request.*;
import com.sdd.response.*;
import com.sdd.service.ContingentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/contingentBillController")
@Slf4j
public class ContingentBillController {

    @Autowired
    private ContingentService contingentService;

    @PostMapping("/saveContingentBill")
    public ResponseEntity<ApiResponse<ContingentSaveResponse>> saveContingentBill(@RequestBody ArrayList<ContingentBillSaveRequest> contingentBillSaveRequest) {
        return new ResponseEntity<>(contingentService.saveContingentBill(contingentBillSaveRequest), HttpStatus.OK);
    }


    @PostMapping("/updateContingentBill")
    public ResponseEntity<ApiResponse<ContingentSaveResponse>> updateContingentBill(@RequestBody ArrayList<ContingentBillSaveRequest> contingentBillSaveRequest) {
        return new ResponseEntity<>(contingentService.updateContingentBill(contingentBillSaveRequest), HttpStatus.OK);
    }

    @GetMapping("/getContingentBill")
    public ResponseEntity<ApiResponse<List<ContingentBillResponse>>> getContingentBill() {
        return new ResponseEntity<>(contingentService.getContingentBill(), HttpStatus.OK);
    }


    @GetMapping("/getCbGroupId/{groupId}")
    public ResponseEntity<ApiResponse<List<ContingentBillResponse>>> getCbGroupId(@PathVariable("groupId") String groupId) {
        return new ResponseEntity<>(contingentService.getContingentBillGroupId(groupId), HttpStatus.OK);
    }

    @PostMapping("/getMaxSectionNumber")
    public ResponseEntity<ApiResponse<ContigentSectionResp>> getMaxSectionNumber(@RequestBody MaxNumberRequest budgetHeadId) {
        return new ResponseEntity<>(contingentService.getMaxSectionNumber(budgetHeadId), HttpStatus.OK);
    }

    @PostMapping("/approveContingentBill")
    public ResponseEntity<ApiResponse<ContingentSaveResponse>> approveContingentBill(@RequestBody ApproveContigentBillRequest approveContigentBillRequest) {
        return new ResponseEntity<>(contingentService.approveContingentBill(approveContigentBillRequest), HttpStatus.OK);
    }


    @PostMapping("/verifyContingentBill")
    public ResponseEntity<ApiResponse<ContingentSaveResponse>> verifyContingentBill(@RequestBody ApproveContigentBillRequest approveContigentBillRequest) {
        return new ResponseEntity<>(contingentService.verifyContingentBill(approveContigentBillRequest), HttpStatus.OK);
    }


    @GetMapping("/getCbReport/{groupId}")
    public ResponseEntity<ApiResponse<List<ContingentBillResponse>>> getCbReport(@PathVariable("groupId") String groupId) {
        return new ResponseEntity<>(contingentService.getContingentBillGroupId(groupId), HttpStatus.OK);
    }


    @GetMapping("/getCountRejectedBil")
    public ResponseEntity<ApiResponse<List<ContingentBillResponse>>> getCountRejectedBil() {
        return new ResponseEntity<>(contingentService.getCountRejectedBil(), HttpStatus.OK);
    }


    @PostMapping(value = "/updateFinalStatus")
    public ResponseEntity<ApiResponse<DefaultResponse>> uploadPhotoRegistrationApi(@RequestBody UploadCBRequest approveContigentBillRequest) throws IOException {
        return new ResponseEntity<>(contingentService.updateFinalStatus(approveContigentBillRequest), HttpStatus.OK);
    }


    @PostMapping("/transferCbBill")
    public ResponseEntity<ApiResponse<ContingentSaveResponse>> transferCbBill(@RequestBody TransferCbBill transferCbBill) {
        return new ResponseEntity<>(contingentService.transferCbBill(transferCbBill), HttpStatus.OK);
    }
}

package com.sdd.controller.WebApi;


import com.sdd.entities.HrData;
import com.sdd.request.BudgetApproveRequest;
import com.sdd.request.InboxOutboxStatusRequest;
import com.sdd.request.RebaseBudgetHistory;
import com.sdd.response.*;
import com.sdd.service.InboxOutBoxService;
import com.sdd.service.MangeUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/inboxOutbox")
@Slf4j
public class InboxOutboxController {

    @Autowired    
    private InboxOutBoxService inboxOutBoxService;


    @GetMapping("/getInboxList")
    public ResponseEntity<ApiResponse<InboxOutBoxResponse>> getInboxList() {
        return new ResponseEntity<>(inboxOutBoxService.getInboxList(), HttpStatus.OK);
    }

    @GetMapping("/getOutBoxList")
    public ResponseEntity<ApiResponse<List<InboxOutBoxResponse>>> getOutBoxList() {
        return new ResponseEntity<>(inboxOutBoxService.getOutBoxList(), HttpStatus.OK);
    }

    @GetMapping("/readMessage/{msgId}")
    public ResponseEntity<ApiResponse<InboxOutBoxResponse>> readMessage(@PathVariable(value = "msgId") String msgId) {
        return new ResponseEntity<>(inboxOutBoxService.readMessage(msgId), HttpStatus.OK);
    }
//	@GetMapping("/getApprovedList")
//	public ResponseEntity<ApiResponse<List<ApprovedResponse>>> getApprovedList() {
//		return new ResponseEntity<>(inboxOutBoxService.getApprovedList(), HttpStatus.OK);
//	}
//	@GetMapping("/getArchivedList")
//	public ResponseEntity<ApiResponse<List<ApprovedResponse>>>  getArchivedList() {
//		return new ResponseEntity<>(inboxOutBoxService.getArchivedList(), HttpStatus.OK);
//	}


    @GetMapping("/getApprovedListData/{groupId}")
    public ResponseEntity<ApiResponse<List<ArchivedResponse>>> getApprovedListData(@PathVariable(value = "groupId") String groupId) {
        return new ResponseEntity<>(inboxOutBoxService.getApprovedListData(groupId), HttpStatus.OK);
    }


    @GetMapping("/updateMsgStatusMain/{msgId}")
    public ResponseEntity<ApiResponse<ArchivedResponse>> updateMsgStatusMain(@PathVariable(value = "msgId") String msgId) {
        return new ResponseEntity<>(inboxOutBoxService.updateMsgStatusMain(msgId), HttpStatus.OK);
    }


    @GetMapping("/moveToArchive/{msgId}")
    public ResponseEntity<ApiResponse<ArchivedResponse>> moveToArchive(@PathVariable(value = "msgId") String msgId) {
        return new ResponseEntity<>(inboxOutBoxService.moveToArchive(msgId), HttpStatus.OK);
    }


//    @PostMapping("/updateMsgStatus")
//    public ResponseEntity<ApiResponse<ArchivedResponse>> updateMsgStatus(@RequestBody InboxOutboxStatusRequest inboxOutboxStatusRequest) {
//        return new ResponseEntity<>(inboxOutBoxService.updateMsgStatus(inboxOutboxStatusRequest), HttpStatus.OK);
//    }



}

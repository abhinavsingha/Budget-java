package com.sdd.controller.WebApi;


import com.sdd.entities.HrData;
import com.sdd.response.ApiResponse;
import com.sdd.response.DefaultResponse;
import com.sdd.response.HradataResponse;
import com.sdd.response.InboxOutBoxResponse;
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



}

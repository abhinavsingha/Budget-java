package com.sdd.controller.WebApi;


import com.sdd.entities.AmountUnit;
import com.sdd.request.DashBoardRequest;
import com.sdd.response.ApiResponse;
import com.sdd.response.DashBoardResponse;
import com.sdd.response.UiResponse;
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

	@Autowired
	private DashBoardService dashBoardService;


	@PostMapping("/getDashBoardDta")
	public ResponseEntity<ApiResponse<DashBoardResponse>> getBudgetFinYear(DashBoardRequest dashBoardRequest) {
		return new ResponseEntity<>(dashBoardService.getDashBoardData(dashBoardRequest), HttpStatus.OK);
	}


	@GetMapping("/updateInboxOutBox")
	public ResponseEntity<ApiResponse<DashBoardResponse>> updateInboxOutBox() {
		return new ResponseEntity<>(dashBoardService.updateInboxOutBox(), HttpStatus.OK);
	}




	@GetMapping("/getUiData/{roleId}")
	public ResponseEntity<ApiResponse<UiResponse>> getUiData(@PathVariable(value = "roleId") String roleId) {
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

}

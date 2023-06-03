package com.sdd.controller.WebApi;


import com.sdd.entities.CdaParking;
import com.sdd.request.CDAReportRequest;
import com.sdd.request.CDARequest;
import com.sdd.response.ApiResponse;
import com.sdd.response.CdaParkingTransResponse;
import com.sdd.response.DefaultResponse;
import com.sdd.service.CdaParkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/cdaParkingController")
@Slf4j
public class CdaParkingController {

	@Autowired    
	private CdaParkingService cdaParkingService;


	@GetMapping("/getCdaUnitList")
	public ResponseEntity<ApiResponse<List<CdaParking>>> getAllCdaData() {
		return new ResponseEntity<>(cdaParkingService.getCdaUnitList(), HttpStatus.OK);
	}


	@GetMapping("/getCdaData/{groupId}")
	public ResponseEntity<ApiResponse<CdaParkingTransResponse>> getCdaData(@PathVariable("groupId") String groupId) {
		return new ResponseEntity<>(cdaParkingService.getCdaData(groupId), HttpStatus.OK);
	}

	@PostMapping("/getCdaDataList")
	public ResponseEntity<ApiResponse<CdaParkingTransResponse>> getAllCdaData(@RequestBody CDARequest cdaRequest) {
		return new ResponseEntity<>(cdaParkingService.getAllCdaData(cdaRequest), HttpStatus.OK);
	}


	@PostMapping("/saveCdaParkingData")
	public ResponseEntity<ApiResponse<DefaultResponse>> budgetAllocationReport(@RequestBody CDARequest cdaRequest) {
		return new ResponseEntity<>(cdaParkingService.saveCdaParkingData(cdaRequest), HttpStatus.OK);
	}


	@PostMapping("/updateCdaParkingData")
	public ResponseEntity<ApiResponse<DefaultResponse>> updateCdaParkingData(@RequestBody CDARequest cdaRequest) {
		return new ResponseEntity<>(cdaParkingService.updateCdaParkingData(cdaRequest), HttpStatus.OK);
	}

}

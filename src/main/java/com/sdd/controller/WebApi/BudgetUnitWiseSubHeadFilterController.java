package com.sdd.controller.WebApi;


import com.sdd.entities.BudgetUnitWiseSubHeadFilter;
import com.sdd.entities.FileUpload;
import com.sdd.request.BudgetAllocationSaveRequest;
import com.sdd.request.BudgetFilterRequest;
import com.sdd.response.ApiResponse;
import com.sdd.response.BudgetFilterResponse;
import com.sdd.response.DefaultResponse;
import com.sdd.response.UplaodMainFormDocumentsResponse;
import com.sdd.service.BudgetFilterService;
import com.sdd.service.UploadDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/budgetFilterApi")
@Slf4j
public class BudgetUnitWiseSubHeadFilterController {

	@Autowired    
	private BudgetFilterService budgetFilterService;



	@PostMapping(value = "/saveFilterData")
	public ResponseEntity<ApiResponse<BudgetFilterResponse>> uploadPhotoRegistrationApi(@RequestBody BudgetUnitWiseSubHeadFilter budgetUnitWiseSubHeadFilter) throws IOException {
		return new ResponseEntity<>(budgetFilterService.saveData(budgetUnitWiseSubHeadFilter), HttpStatus.OK);
	}



	@PostMapping(value = "/getFilterData")
	public ResponseEntity<ApiResponse<BudgetFilterResponse>> getFilterData(@RequestBody BudgetFilterRequest budgetFilterRequest) throws IOException {
		return new ResponseEntity<>(budgetFilterService.getFilterData(budgetFilterRequest), HttpStatus.OK);
	}



	@PostMapping(value = "/deleteData")
	public ResponseEntity<ApiResponse<BudgetFilterResponse>> deleteData(@RequestBody BudgetFilterRequest budgetFilterRequest) throws IOException {
		return new ResponseEntity<>(budgetFilterService.deleteData(budgetFilterRequest), HttpStatus.OK);
	}


	@GetMapping(value = "/deleteDataByPid")
	public ResponseEntity<ApiResponse<DefaultResponse>> deleteData() throws IOException {
		return new ResponseEntity<>(budgetFilterService.deleteDataByPid(), HttpStatus.OK);
	}

}

package com.sdd.controller.WebApi;


import com.sdd.entities.CdaParking;
import com.sdd.entities.CgUnit;
import com.sdd.entities.repository.CgUnitRepository;
import com.sdd.request.BudgetReciptSaveRequest;
import com.sdd.request.BudgetReciptUpdateRequest;
import com.sdd.response.*;
import com.sdd.service.BudgetReciptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/budgetRecipet")
@Slf4j
public class BudgetRecipetController {

	@Autowired
	private BudgetReciptService budgetReciptService;



	@PostMapping("/budgetRecipetSave")
	public ResponseEntity<ApiResponse<BudgetReciptListResponse>> budgetRecipetSave(@RequestBody BudgetReciptSaveRequest budgetReciptSaveRequest) {
		return new ResponseEntity<>(budgetReciptService.budgetRecipetSave(budgetReciptSaveRequest), HttpStatus.OK);
	}


	@PostMapping("/updateRecipetSave")
	public ResponseEntity<ApiResponse<ContingentSaveResponse>> updateRecipetSave(@RequestBody BudgetReciptUpdateRequest budgetReciptSaveRequest) {
		return new ResponseEntity<>(budgetReciptService.updateRecipetSave(budgetReciptSaveRequest), HttpStatus.OK);
	}


	@GetMapping("/getBudgetRecipt")
	public ResponseEntity<ApiResponse<BudgetReciptListResponse>> getBudgetAllocationData() {
		return new ResponseEntity<>(budgetReciptService.getBudgetRecipt(), HttpStatus.OK);
	}

	@GetMapping("/getModData")
	public ResponseEntity<ApiResponse<CgUnit>> getModData() {
		return new ResponseEntity<>(budgetReciptService.getModData(), HttpStatus.OK);
	}



	@PostMapping("/getBudgetReciptFilter")
	public ResponseEntity<ApiResponse<AllBudgetRevisionResponse>> getBudgetAllocationDataFilter(@RequestBody BudgetReciptSaveRequest budgetReciptSaveRequest) {
		return new ResponseEntity<>(budgetReciptService.getBudgetReciptFilter(budgetReciptSaveRequest), HttpStatus.OK);
	}




	@GetMapping("/getAllCda")
	public ResponseEntity<ApiResponse<List<CdaParking>>> getAllCda() {
		return new ResponseEntity<>(budgetReciptService.getAllCda(), HttpStatus.OK);
	}


}

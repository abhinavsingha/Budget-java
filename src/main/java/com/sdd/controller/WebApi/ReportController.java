package com.sdd.controller.WebApi;



import com.sdd.request.CDAReportRequest;
import com.sdd.request.ReportRequest;
import com.sdd.request.SubHeadWiseAllocationReportReq;
import com.sdd.request.UnitWiseAllocationReport;
import com.sdd.response.ApiResponse;
import com.sdd.response.FilePathResponse;
import com.sdd.response.UnitRebaseReportResponce;
import com.sdd.service.MangeReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/reportController")
@Slf4j
public class ReportController {

	@Autowired
	private MangeReportService mangeReportService;



	@GetMapping("/getAllocationReport/{authgroupId}")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getAllocationReport(@PathVariable("authgroupId") String authgroupId) {
		return new ResponseEntity<>(mangeReportService.getAllocationReport(authgroupId), HttpStatus.OK);
	}

	@GetMapping("/getAllocationReportDoc/{authgroupId}")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getAllocationReportDoc(@PathVariable("authgroupId") String authgroupId) {
		return new ResponseEntity<>(mangeReportService.getAllocationReportDoc(authgroupId), HttpStatus.OK);
	}







	@PostMapping("/getAllocationReportRevised")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getAllocationReportRevised(@RequestBody ReportRequest reportRequest) {
		return new ResponseEntity<>(mangeReportService.getAllocationReportRevised(reportRequest), HttpStatus.OK);
	}



	@PostMapping("/getContingentBillReport")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getContingentBillReport(@RequestBody ReportRequest reportRequest) {
		return new ResponseEntity<>(mangeReportService.getContingentBillReport(reportRequest), HttpStatus.OK);
	}

	@PostMapping("/getContingentBillReportDoc")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getContingentBillReportDoc(@RequestBody ReportRequest reportRequest) {
		return new ResponseEntity<>(mangeReportService.getContingentBillReportDoc(reportRequest), HttpStatus.OK);
	}


	@PostMapping("/getAllocationReportUnitWise")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getAllocationReportUnitWise(@RequestBody ReportRequest reportRequest) {
		return new ResponseEntity<>(mangeReportService.getAllocationReportUnitWise(reportRequest), HttpStatus.OK);
	}



	@PostMapping("/getAllocationReportSubHead")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getAllocationReportSubHead(@RequestBody ReportRequest reportRequest) {
		return new ResponseEntity<>(mangeReportService.getAllocationReportSubHead(reportRequest), HttpStatus.OK);
	}



	@PostMapping("/getReAllocationReport")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getReAllocationReport(@RequestBody ReportRequest reportRequest) {
		return new ResponseEntity<>(mangeReportService.getReAllocationReport(reportRequest), HttpStatus.OK);
	}


	@PostMapping("/getCdaParkingReport")
	public ResponseEntity<ApiResponse<FilePathResponse>> getCdaParkingReport(@RequestBody CDAReportRequest reportRequest) {
		return new ResponseEntity<>(mangeReportService.getCdaParkingReport(reportRequest), HttpStatus.OK);
	}




	@PostMapping("/getCdaParkingReportDoc")
	public ResponseEntity<ApiResponse<FilePathResponse>> getCdaParkingReportDoc(@RequestBody CDAReportRequest reportRequest) {
		return new ResponseEntity<>(mangeReportService.getCdaParkingReportDoc(reportRequest), HttpStatus.OK);
	}




	@PostMapping("/getUnitWiseAllocationReport")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getUnitWiseAllocationReport(@RequestBody UnitWiseAllocationReport reportRequest) {
		return new ResponseEntity<>(mangeReportService.getUnitWiseAllocationReport(reportRequest), HttpStatus.OK);
	}

	@PostMapping("/getSubHeadWiseAllocationReport")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getSubHeadWiseAllocationReport(@RequestBody SubHeadWiseAllocationReportReq req) {
		return new ResponseEntity<>(mangeReportService.getSubHeadWiseAllocationReport(req), HttpStatus.OK);
	}

	@GetMapping("/getBEAllocationReport/{finYearId}/{allocationType}/{amountTypeId}")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getBEAllocationReport(@PathVariable(value = "finYearId") String finYearId , @PathVariable(value = "allocationType") String allocationType, @PathVariable(value = "amountTypeId") String amountTypeId)  {
		return new ResponseEntity<>(mangeReportService.getBEAllocationReport(finYearId,allocationType,amountTypeId), HttpStatus.OK);
	}

	@GetMapping("/getREAllocationReport/{finYearId}/{allocationType}/{amountTypeId}")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getREAllocationReport(@PathVariable(value = "finYearId") String finYearId , @PathVariable(value = "allocationType") String allocationType, @PathVariable(value = "amountTypeId") String amountTypeId)  {
		return new ResponseEntity<>(mangeReportService.getREAllocationReport(finYearId,allocationType,amountTypeId), HttpStatus.OK);
	}

	@GetMapping("/getBEREAllocationReport/{finYearId}/{allocationTypeBE}/{allocationTypeRE}/{amountTypeId}")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getBEREAllocationReport(@PathVariable(value = "finYearId") String finYearId , @PathVariable(value = "allocationTypeBE") String allocationTypeBE,@PathVariable(value = "allocationTypeRE") String allocationTypeRE, @PathVariable(value = "amountTypeId") String amountTypeId)  {
		return new ResponseEntity<>(mangeReportService.getBEREAllocationReport(finYearId,allocationTypeBE,allocationTypeRE,amountTypeId), HttpStatus.OK);
	}

	@GetMapping("/getMainBEAllocationReport/{finYearId}/{allocationType}/{amountTypeId}/{fromDate}/{toDate}")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getMainBEAllocationReport(@PathVariable(value = "finYearId") String finYearId , @PathVariable(value = "allocationType") String allocationType, @PathVariable(value = "amountTypeId") String amountTypeId , @PathVariable(value = "fromDate") String fromDate , @PathVariable(value = "toDate") String toDate)  {
		return new ResponseEntity<>(mangeReportService.getMainBEAllocationReport(finYearId,allocationType,amountTypeId,fromDate,toDate), HttpStatus.OK);
	}

	//@GetMapping("/getUnitRebaseReport/{amountTypeId}/{fromDate}/{toDate}")
	@GetMapping("/getUnitRebaseReport/{fromDate}/{toDate}")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getUnitRebaseReport(@PathVariable(value = "fromDate") String fromDate , @PathVariable(value = "toDate") String toDate)  {
		return new ResponseEntity<>(mangeReportService.getUnitRebaseReport(fromDate,toDate), HttpStatus.OK);
	}

	@GetMapping("/getRevisedAllocationReport/{authGroupId}")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getRevisedAllocationReport(@PathVariable(value = "authGroupId") String authGroupId )  {
		return new ResponseEntity<>(mangeReportService.getRevisedAllocationReport(authGroupId), HttpStatus.OK);
	}

	@GetMapping("/getUnitRebaseReportData/{fromDate}/{toDate}")
	public ResponseEntity<ApiResponse<List<FilePathResponse>>> getUnitRebaseReportData(@PathVariable(value = "fromDate") String fromDate , @PathVariable(value = "toDate") String toDate)  {
		return new ResponseEntity<>(mangeReportService.getUnitRebaseReportData(fromDate,toDate), HttpStatus.OK);
	}

}

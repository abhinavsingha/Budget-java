package com.sdd.controller.WebApi;



import com.sdd.entities.FileUpload;
import com.sdd.response.ApiResponse;
import com.sdd.request.BudgetAllocationSaveRequest;
import com.sdd.response.UplaodMainFormDocumentsResponse;
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
@RequestMapping("/fileUpload")
@Slf4j
public class FileUploadController {

	@Autowired
	private UploadDocumentService uploadDocumentService;



	@PostMapping(value = "/uploadFile")
	public ResponseEntity<ApiResponse<UplaodMainFormDocumentsResponse>> uploadPhotoRegistrationApi(@FormDataParam("file") MultipartFile file) throws IOException {
		return new ResponseEntity<>(uploadDocumentService.fileUplaod(file), HttpStatus.OK);
	}

//	@CrossOrigin(origins = "http://localhost:1111")

	@PostMapping("/Demo" )
	public String demo(@RequestBody BudgetAllocationSaveRequest budgetAllocationSaveRequest) {
		log.info("[Owner] create Owner {}",budgetAllocationSaveRequest);
		return budgetAllocationSaveRequest.toString();
	}


	@GetMapping("/getFilePath/{fileId}")
	public ResponseEntity<ApiResponse<FileUpload>> getFilePath(@PathVariable(value = "fileId") String fileId) throws IOException {
		return new ResponseEntity<>(uploadDocumentService.getFilePath(fileId), HttpStatus.OK);
	}

	@GetMapping("/getApprovedFilePath/{authGoupId}/{type}")
	public ResponseEntity<ApiResponse<FileUpload>> getApprovedFilePath(@PathVariable(value = "authGoupId") String authGoupId ,@PathVariable(value = "type") String type) throws IOException {
		return new ResponseEntity<>(uploadDocumentService.getApprovedFilePath(authGoupId,type), HttpStatus.OK);
	}


}

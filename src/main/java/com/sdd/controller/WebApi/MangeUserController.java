package com.sdd.controller.WebApi;


import com.sdd.entities.HrData;
import com.sdd.request.UpdateRoleRequest;
import com.sdd.response.ApiResponse;
import com.sdd.response.DefaultResponse;
import com.sdd.response.HradataResponse;
import com.sdd.service.MangeUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/mangeUser")
@Slf4j
public class MangeUserController {

	@Autowired    
	private MangeUserService mangeUserService;



	@GetMapping("/getAllUser")
	public ResponseEntity<ApiResponse<List<HradataResponse>>> getAllUser() {
		return new ResponseEntity<>(mangeUserService.getAllUser(), HttpStatus.OK);
	}

	@GetMapping("/removeUser/{pid}")
	public ResponseEntity<ApiResponse<DefaultResponse>> removeUser(@PathVariable(value = "pid") String pid) {
		return new ResponseEntity<>(mangeUserService.removeUser(pid), HttpStatus.OK);
	}

	@GetMapping("/activateUser/{pid}")
	public ResponseEntity<ApiResponse<DefaultResponse>> activateUser(@PathVariable(value = "pid") String pid) {
		return new ResponseEntity<>(mangeUserService.activateUser(pid), HttpStatus.OK);
	}

	@GetMapping("/deActivateUser/{pid}/{rollId}")
	public ResponseEntity<ApiResponse<DefaultResponse>> deActivateUser(@PathVariable(value = "pid") String pid,@PathVariable(value = "rollId") String rollId) {
		return new ResponseEntity<>(mangeUserService.deActivateUser(pid,rollId), HttpStatus.OK);
	}


	@PostMapping("/createUser")
	public ResponseEntity<ApiResponse<DefaultResponse>> addUser(@RequestBody HrData hrData) {
		return new ResponseEntity<>(mangeUserService.addUser(hrData), HttpStatus.OK);
	}


	@PostMapping("/removeRole")
	public ResponseEntity<ApiResponse<DefaultResponse>> removeRole(@RequestBody HrData hrData) {
		return new ResponseEntity<>(mangeUserService.removeRole(hrData), HttpStatus.OK);
	}

	@GetMapping("/userExit")
	public ResponseEntity<ApiResponse<Boolean>> userExit() {
		return new ResponseEntity<>(mangeUserService.userExit(), HttpStatus.OK);
	}

}

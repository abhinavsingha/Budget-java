package com.sdd.controller.WebApi;


import com.sdd.entities.Role;
import com.sdd.response.ApiResponse;
import com.sdd.service.MangeRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/mangeRoleController")
@Slf4j
public class MangeRoleController {

	@Autowired    
	private MangeRoleService mangeRoleService;



	@GetMapping("/getAllRole")
	public ResponseEntity<ApiResponse<List<Role>>> getAllRole() {
		return new ResponseEntity<>(mangeRoleService.getAllRole(), HttpStatus.OK);
	}



	@GetMapping("/getRoleById/{roleId}")
	public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable(value = "roleId") String roleId) {
		return new ResponseEntity<>(mangeRoleService.getRoleById(roleId), HttpStatus.OK);
	}





}

package com.sdd.service;

import com.sdd.entities.Role;
import com.sdd.response.ApiResponse;

import java.util.List;

;

public interface MangeRoleService {


	ApiResponse<List<Role>> getAllRole();

	ApiResponse<List<Role>> getAllRoleMain();

	ApiResponse<Role> getRoleById( String roleId);
}

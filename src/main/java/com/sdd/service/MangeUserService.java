package com.sdd.service;

import com.sdd.entities.HrData;
import com.sdd.request.UpdateRoleRequest;
import com.sdd.response.ApiResponse;
import com.sdd.response.DefaultResponse;
import com.sdd.response.HradataResponse;

import java.util.List;

;

public interface MangeUserService {


	ApiResponse<DefaultResponse> addUser(HrData hrData);

	ApiResponse<List<HradataResponse>> getAllUser();

	ApiResponse<DefaultResponse>removeUser(String pid);

	ApiResponse<DefaultResponse> activateUser(String pid);

	ApiResponse<DefaultResponse> deActivateUser(String pid);

	ApiResponse<DefaultResponse> removeRole(HrData hrData);
}

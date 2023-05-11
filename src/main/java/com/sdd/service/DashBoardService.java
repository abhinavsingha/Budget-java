package com.sdd.service;

import com.sdd.entities.AmountUnit;
import com.sdd.request.DashBoardRequest;
import com.sdd.response.ApiResponse;
import com.sdd.response.DashBoardResponse;
import com.sdd.response.UiResponse;

import java.util.List;


public interface DashBoardService {


	ApiResponse<DashBoardResponse> getDashBoardData(DashBoardRequest dashBoardRequest);

    ApiResponse<UiResponse> getUiData(String roleId);

    ApiResponse<DashBoardResponse> showAllData();

    ApiResponse<List<AmountUnit>> getAllAmountUnit();

    ApiResponse<DashBoardResponse> updateInboxOutBox();
}

package com.sdd.service;

import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetUnitWiseSubHeadFilter;
import com.sdd.request.BudgetFilterRequest;
import com.sdd.response.ApiResponse;
import com.sdd.response.BudgetFilterResponse;
import com.sdd.response.DefaultResponse;

import java.util.List;

;

public interface BudgetFilterService {

	ApiResponse<BudgetFilterResponse> saveData(BudgetUnitWiseSubHeadFilter budgetUnitWiseSubHeadFilter);

	ApiResponse<BudgetFilterResponse> getFilterData(BudgetFilterRequest budgetFilterRequest);

	ApiResponse<BudgetFilterResponse> deleteData(BudgetFilterRequest budgetFilterRequest);

	ApiResponse<DefaultResponse> deleteDataByPid();

}

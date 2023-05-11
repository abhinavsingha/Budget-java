package com.sdd.service;


import com.sdd.entities.CgUnit;
import com.sdd.request.BudgetReciptSaveRequest;
import com.sdd.request.BudgetReciptUpdateRequest;
import com.sdd.response.*;


public interface BudgetReciptService {


	ApiResponse<BudgetReciptListResponse> budgetRecipetSave(BudgetReciptSaveRequest budgetReciptSaveRequest);

	ApiResponse<ContingentSaveResponse> updateRecipetSave(BudgetReciptUpdateRequest budgetReciptSaveRequest);

	ApiResponse<BudgetReciptListResponse> getBudgetRecipt();

	ApiResponse<AllBudgetRevisionResponse> getBudgetReciptFilter(BudgetReciptSaveRequest budgetReciptSaveRequest);

	ApiResponse<CgUnit> getModData();
}

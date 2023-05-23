package com.sdd.service;


import com.sdd.entities.CdaParking;
import com.sdd.entities.CgUnit;
import com.sdd.request.BudgetReciptSaveRequest;
import com.sdd.request.BudgetReciptUpdateRequest;
import com.sdd.response.*;

import java.util.List;


public interface BudgetReciptService {


	ApiResponse<BudgetReciptListResponse> budgetRecipetSave(BudgetReciptSaveRequest budgetReciptSaveRequest);

	ApiResponse<ContingentSaveResponse> updateRecipetSave(BudgetReciptUpdateRequest budgetReciptSaveRequest);

	ApiResponse<BudgetReciptListResponse> getBudgetRecipt();

	ApiResponse<AllBudgetRevisionResponse> getBudgetReciptFilter(BudgetReciptSaveRequest budgetReciptSaveRequest);

	ApiResponse<CgUnit> getModData();

	ApiResponse<List<CdaParking>> getAllCda();
}

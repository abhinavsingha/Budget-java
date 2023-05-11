package com.sdd.service;

import com.sdd.entities.BudgetFinancialYear;
import com.sdd.request.BudgetAllocationReportRequest;
import com.sdd.response.ApiResponse;
import com.sdd.response.BudgetAllocationReportResponse;

import java.util.List;

;

public interface UnitRebaseService {


	ApiResponse<List<BudgetFinancialYear>> getBudgetFinYear();
	ApiResponse<BudgetAllocationReportResponse> budgetAllocationReport(BudgetAllocationReportRequest budgetAllocationReportRequest);
}

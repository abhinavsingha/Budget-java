package com.sdd.service;

import com.sdd.entities.*;
import com.sdd.request.*;
import com.sdd.response.*;

;import java.util.List;

public interface BudgetAllocationService {


	ApiResponse<AvilableFundResponse> findAvailableAmount(GetAmountRequest budgetHeadId);

	ApiResponse<AvilableFundResponse> getAvailableFundData();

	ApiResponse<AvilableFundResponse> getAvailableFundFindByUnitIdAndFinYearId(GetAvilableFundRequest getAvilableFundRequest);

	ApiResponse<BudgetAllocationSaveResponse> saveBudgetAllocationSubHeadWise(BudgetAllocationSaveRequest budgetAllocationSaveRequest);

	ApiResponse<BudgetAllocationSaveResponse> saveBudgetAllocationUnitWise(BudgetAllocationSaveUnitRequest budgetAllocationSaveUnitRequest);

	ApiResponse<List<FindBudgetResponse>> findBudgetAllocationFinYearAndUnit(FindBudgetRequest findBudgetRequest);

	ApiResponse<List<BudgetFinancialYear>> getBudgetFinYear();

	ApiResponse<List<AllocationType>> getAllocationAllData();

	ApiResponse<List<AllocationType>> getAllocationType();

	ApiResponse<List<BudgetHead>> getSubHeadsData();

	ApiResponse<List<BudgetHeadResponse>> getSubHeadListWithAmount(BudgetHeadRequest budgetHeadRequest);

	ApiResponse<BudgetResponseWithToken> getSubHeads();

	ApiResponse<List<CgUnitResponse>> getCgUnitData();

	ApiResponse<List<CgUnitResponse>> getCgUnitWithoutMOD();

	ApiResponse<List<CgUnitResponse>> getCgUnitDataForBudgetRecipt();

	ApiResponse<BudgetAllocationResponse> budgetAllocationReport(BudgetAllocationReportRequest budgetAllocationReportRequest);

	ApiResponse<List<BudgetHead>> getSubHeadsDataByMajorHead(BudgetHeadRequest majorHead);

	ApiResponse<BudgetAllocationSaveResponse>  updateBudgetAllocationSubHeadWise(BudgetAllocationUpdateRequest budgetAllocationSaveRequest);

	ApiResponse<BudgetAllocationSaveResponse>  updateBudgetAllocationUnitWise(BudgetAllocationUpdateRequest budgetAllocationSaveUnitRequest);

	ApiResponse<BudgetAllocationResponse> getBudgetAllocationData();

	ApiResponse<BudgetAllocationSaveResponse>  budgetDelete(BudgetDeleteRequest budgetDeleteRequest);

	ApiResponse<BudgetAllocationSaveResponse> approveBudgetOrReject(BudgetApproveRequest budgetApproveRequest);

	ApiResponse<List<BudgetRevisionResponse>>  getBudgetRevisionData(BudgetAllocationReportRequest budgetAllocationReportRequest);

	ApiResponse<BudgetAllocationSaveResponse> saveBudgetRevisonData(BudgetAllocationSaveUnitRequest budgetAllocationSaveUnitRequest);

	ApiResponse<List<CgUnitResponse>> getCgUnitDataWithPurposeCode();

	ApiResponse<DefaultResponse> saveAuthData(AuthRequest authRequest);

	ApiResponse<List<BudgetReviResp>>  getBudgetRevisionData(BudgetReviReq budgetRivRequest);

	ApiResponse<BudgetAllocationResponse> getApprovedBudgetData();

	ApiResponse<BudgetAllocationSaveResponse> saveBudgetRevision(BudgetAllocationSaveUnitRequest budgetAllocationSaveRequestList);

	ApiResponse<List<SubHeadVotedOrChargedType>> getSubHeadType();

	ApiResponse<BudgetAllocationResponse> getAllGroupIdAndUnitId(String groupId);

	ApiResponse<BudgetAllocationResponse> getAllGroupIdAndUnitIdRevisionCase(String groupId);

	ApiResponse<BudgetAllocationResponse> getBudgetAllocationDataGroupId(String groupId);

	ApiResponse<BudgetAllocationResponse> getAllRevisionGroupId(String groupId);

	ApiResponse<BudgetAllocationSaveResponse> approveRivisonBudgetOrReject(BudgetApproveRequest budgetApproveRequest);

	ApiResponse<DefaultResponse> saveAuthDataRevision(AuthRequest authRequest);


	ApiResponse<List<CgUnitResponse>> getAllCgUnitData();

	ApiResponse<List<AllocationType>> getAllocationByFinYear(String finyear);

	ApiResponse<DefaultResponse>  updateAllocation(AllocationType allocationType);
}

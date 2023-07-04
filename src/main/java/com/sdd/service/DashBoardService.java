package com.sdd.service;

import com.sdd.entities.AmountUnit;
import com.sdd.request.DashBoardRequest;
import com.sdd.response.*;

import java.util.List;


public interface DashBoardService {


    ApiResponse<DashBoardResponse> getDashBoardData(DashBoardRequest dashBoardRequest);

    ApiResponse<UiResponse> getUiData(String roleId);

    ApiResponse<DashBoardResponse> showAllData();

    ApiResponse<List<AmountUnit>> getAllAmountUnit();

    ApiResponse<DashBoardResponse> updateInboxOutBox();


    ApiResponse<List<DashBoardExprnditureResponse>> getSubHeadWiseExpenditureByUnitIdFinYearIdAllocationTypeIdSubHeadTypeId(String unitId,String finYearId,String subHeadTypeId,String allocationTypeId,String amountTypeId);

    ApiResponse<List<SubHeadWiseExpResp>> getDashBordSubHeadwiseExpenditure(String subHeadId, String finYearId, String allocationTypeId, String amounttypeId);

}

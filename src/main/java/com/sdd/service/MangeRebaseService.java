package com.sdd.service;

import com.sdd.entities.BudgetFinancialYear;
import com.sdd.entities.CgStation;
import com.sdd.request.MangeRebaseRequest;
import com.sdd.request.RebaseBudgetHistory;
import com.sdd.request.UnitRebaseSaveReq;
import com.sdd.response.*;

import java.util.Date;
import java.util.List;
import java.sql.Timestamp;

public interface MangeRebaseService {

	ApiResponse<DefaultResponse> saveRebaes(MangeRebaseRequest mangeRebaseRequest);

	ApiResponse<List<CgStation>>  getAllStation();

	ApiResponse<List<CgUnitResponse>>  getAllUnit();

	ApiResponse<List<BudgetFinancialYear>>  getAllBudgetFinYr();

	ApiResponse<List<RebaseBudgetHistory>> getAllUnitRebaseData(String finYear, String unit);

	ApiResponse<DefaultResponse> saveUnitRebase(UnitRebaseSaveReq req);

//	ApiResponse<DefaultResponse> saveUnitRebaseArvind(UnitRebaseSaveReq req);

	ApiResponse<CgStation> getAllStationById(String stationId);

	ApiResponse<List<CgUnitResponse>> getAllIsShipCgUnitData();

	ApiResponse<List<CgUnitResponse>> getIsShipCgUnit();
	ApiResponse<List<CgUnitResponse>> getIsShipRebaseUnits(String fromdate,String todate);

	ApiResponse<List<RebaseNotificationResp>> getUnitRebaseNotificationData(String authGrpId);


}

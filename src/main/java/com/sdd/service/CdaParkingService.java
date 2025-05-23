package com.sdd.service;

import com.sdd.entities.CdaParking;
import com.sdd.request.CDARequest;
import com.sdd.request.CDARequestReBase;
import com.sdd.request.CdaAndAllocationDataRequest;
import com.sdd.response.*;

import java.util.List;

public interface CdaParkingService {



	ApiResponse<DefaultResponse> saveCdaParkingData(CDARequest cdaRequest);

	ApiResponse<DefaultResponse> saveCdaParkingDataForRebase(CDARequest cdaRequest);

	ApiResponse<CdaParkingTransResponse> getCdaData(String groupId);

	ApiResponse<CdaParkingTransResponse> getCdaDataUnit(String groupId,String unitId);

	ApiResponse<CdaHistoryResponse> getCdaHistoryData(String groupId,String unitId);

	ApiResponse<CdaParkingTransResponse> getAllCdaData(CDARequest cdaRequest);

	ApiResponse<ReabseCdaParkingResponse> getOldCdaDataForRebase(CDARequestReBase cdaRequest);

	ApiResponse<CdaAndAllocationDataResponse> getAllBillCdaAndAllocationSummery(CdaAndAllocationDataRequest cdaAndAllocationDataRequest);

	ApiResponse<CdaAndAllocationDataResponse> getAllBillCdaAndAllocationSummeryunit(CdaAndAllocationDataRequest cdaAndAllocationDataRequest);

	ApiResponse<ReabseCdaParkingResponse> getCheckExpForUnit(CDARequestReBase cdaRequest);

	ApiResponse<List<CdaParking>> getCdaUnitList();

	ApiResponse<DefaultResponse> updateCdaParkingData(CDARequest cdaRequest) throws Exception;

}

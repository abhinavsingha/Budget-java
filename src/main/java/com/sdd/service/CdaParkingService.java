package com.sdd.service;

import com.sdd.entities.CdaParking;
import com.sdd.request.CDARequest;
import com.sdd.request.CDARequestReBase;
import com.sdd.response.ApiResponse;
import com.sdd.response.CdaParkingTransResponse;
import com.sdd.response.DefaultResponse;

import java.util.List;

public interface CdaParkingService {



	ApiResponse<DefaultResponse> saveCdaParkingData(CDARequest cdaRequest);

	ApiResponse<DefaultResponse> saveCdaParkingDataForRebase(CDARequest cdaRequest);

	ApiResponse<CdaParkingTransResponse> getCdaData(String groupId);

	ApiResponse<CdaParkingTransResponse> getAllCdaData(CDARequest cdaRequest);

	ApiResponse<CdaParkingTransResponse> getOldCdaDataForRebase(CDARequestReBase cdaRequest);

	ApiResponse<List<CdaParking>> getCdaUnitList();

	ApiResponse<DefaultResponse> updateCdaParkingData(CDARequest cdaRequest);

	ApiResponse<DefaultResponse> updateCdaParkingDataRebase(CDARequest cdaRequest);
}

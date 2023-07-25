package com.sdd.service;

import com.sdd.request.*;
import com.sdd.response.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

;

public interface ContingentService {



    ApiResponse<ContingentSaveResponse> saveContingentBill(ArrayList<ContingentBillSaveRequest> budgetAllocationReportRequest);

    ApiResponse<ContingentSaveResponse> updateContingentBill(ArrayList<ContingentBillSaveRequest> contingentBillSaveRequest);

    ApiResponse<List<ContingentBillResponse>> getContingentBill();

    ApiResponse<List<ContingentBillResponse>> getContingentBillGroupId(String groupId);

    ApiResponse<ContigentSectionResp> getMaxSectionNumber(MaxNumberRequest budgetHeadId);

    ApiResponse<ContingentSaveResponse>  approveContingentBill(ApproveContigentBillRequest approveContigentBillRequest);

    ApiResponse<DefaultResponse>  updateFinalStatus(UploadCBRequest approveContigentBillRequest)  throws IOException;

    ApiResponse<ContingentSaveResponse> verifyContingentBill(ApproveContigentBillRequest approveContigentBillRequest);
}

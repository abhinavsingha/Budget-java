package com.sdd.service;

import com.sdd.entities.HrData;
import com.sdd.request.RebaseBudgetHistory;
import com.sdd.response.*;

import java.util.List;

;

public interface InboxOutBoxService {

	ApiResponse<InboxOutBoxResponse> getInboxList();

	ApiResponse<List<InboxOutBoxResponse>> getOutBoxList();

	ApiResponse<InboxOutBoxResponse> readMessage(String msgId);

	ApiResponse<List<ApprovedResponse>>getApprovedList();

	ApiResponse<List<ApprovedResponse>> getArchivedList();

	ApiResponse<List<ArchivedResponse>> getApprovedListData(String groupId);




}

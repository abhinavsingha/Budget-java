package com.sdd.service;

import com.sdd.entities.HrData;
import com.sdd.request.InboxOutboxStatusRequest;
import com.sdd.request.RebaseBudgetHistory;
import com.sdd.response.*;

import java.util.List;

;

public interface InboxOutBoxService {

	ApiResponse<InboxOutBoxResponse> getInboxList();

	ApiResponse<List<InboxOutBoxResponse>> getOutBoxList();

	ApiResponse<InboxOutBoxResponse> readMessage(String msgId);

	ApiResponse<List<ArchivedResponse>> getApprovedListData(String groupId);

//	ApiResponse<ArchivedResponse> updateMsgStatus(InboxOutboxStatusRequest inboxOutboxStatusRequest);

	ApiResponse<ArchivedResponse> updateMsgStatusMain(String msgId);
	ApiResponse<ArchivedResponse> moveToArchive(String msgId);
}

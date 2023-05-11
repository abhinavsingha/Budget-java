package com.sdd.service;

import com.sdd.entities.HrData;
import com.sdd.response.ApiResponse;
import com.sdd.response.DefaultResponse;
import com.sdd.response.HradataResponse;
import com.sdd.response.InboxOutBoxResponse;

import java.util.List;

;

public interface InboxOutBoxService {

	ApiResponse<InboxOutBoxResponse> getInboxList();

	ApiResponse<List<InboxOutBoxResponse>> getOutBoxList();

	ApiResponse<InboxOutBoxResponse> readMessage(String msgId);
}

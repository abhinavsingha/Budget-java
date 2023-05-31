package com.sdd.service;

import com.sdd.request.CDAReportRequest;
import com.sdd.request.ReportRequest;
import com.sdd.request.SubHeadWiseAllocationReportReq;
import com.sdd.request.UnitWiseAllocationReport;
import com.sdd.response.ApiResponse;
import com.sdd.response.FilePathResponse;
import com.sdd.response.UnitRebaseReportResponce;

import java.util.Date;
import java.util.List;


public interface MangeReportService {



    ApiResponse<List<FilePathResponse>> getAllocationReport(String reportRequest);

    ApiResponse<List<FilePathResponse>> getAllocationReportDoc(String reportRequest);

    ApiResponse<List<FilePathResponse>> getAllocationReportRevised(ReportRequest reportRequest);

    ApiResponse<List<FilePathResponse>> getContingentBillReport(ReportRequest reportRequest);

    ApiResponse<List<FilePathResponse>> getContingentBillReportDoc(ReportRequest reportRequest);

    ApiResponse<List<FilePathResponse>> getAllocationReportUnitWise(ReportRequest reportRequest);

    ApiResponse<List<FilePathResponse>> getAllocationReportSubHead(ReportRequest reportRequest);

    ApiResponse<List<FilePathResponse>> getReAllocationReport(ReportRequest reportRequest);

//    ApiResponse<DefaultResponse> getConicalPath();

    ApiResponse<FilePathResponse> getCdaParkingReport(CDAReportRequest reportRequest);

    ApiResponse<FilePathResponse> getCdaParkingReportDoc(CDAReportRequest reportRequest);

    ApiResponse<List<FilePathResponse>> getUnitWiseAllocationReport(UnitWiseAllocationReport reportRequest);

    ApiResponse<List<FilePathResponse>> getSubHeadWiseAllocationReport(SubHeadWiseAllocationReportReq req);

    ApiResponse<List<FilePathResponse>> getBEAllocationReport(String finYearId,String allocationType, String amountTypeId);

    ApiResponse<List<FilePathResponse>> getREAllocationReport(String finYearId,String allocationType, String amountTypeId);

    ApiResponse<List<FilePathResponse>> getBEREAllocationReport(String finYearId,String allocationTypeBE,String allocationTypeRE, String amountTypeId);

    ApiResponse<List<FilePathResponse>> getMainBEAllocationReport(String finYearId, String allocationType, String amountTypeId, String fromDate, String toDate);

    ApiResponse<List<FilePathResponse>> getUnitRebaseReport(String fromDate, String toDate);

    ApiResponse<List<FilePathResponse>> getRevisedAllocationReport(String authGroupId);

    ApiResponse<List<UnitRebaseReportResponce>> getUnitRebaseReportData(String fromDate, String toDate);

}

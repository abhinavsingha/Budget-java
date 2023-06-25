package com.sdd.response;

import com.sdd.utils.ReportSubModel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;


@Getter
@Setter

public class FilePathResponse {


   String path;
   String fileName;
   String finYear;
   String amountType;
   String unit;
   String subHeadKey;
   String revenueOrCapital;
   String type;
   String remark;
   HashMap<String, List<ReportSubModel>> reciptRespone;
   HashMap<String, List<CDAReportResponse>> allCdaData;



   String approveName;
   String approveRank;


}

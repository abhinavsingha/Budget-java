package com.sdd.response;

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
   String unit;
   String SubHead;
   String type;
   String remark;
   HashMap<String, List<CDAReportResponse>> allCdaData;

}

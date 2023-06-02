package com.sdd.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DemoReprotMain {
  private List<DemoReportResponse> demoReportResponseList = new ArrayList<>();
}

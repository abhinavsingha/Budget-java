package com.sdd.response;

import lombok.Data;


@Data
public class ApiResponseWithVersion<T> {
  T response;
  private Integer status;
  private String message;
  private String androidVersion = "prod-v1.0.0";
  private String iosVersion = "prod-v1.0.0";
  private String apiVersion = "prod-v1.0.0";
}

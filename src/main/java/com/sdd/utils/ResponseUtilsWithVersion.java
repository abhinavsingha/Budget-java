package com.sdd.utils;

import com.fasterxml.jackson.core.type.TypeReference;

import com.sdd.response.ApiResponseWithVersion;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

@UtilityClass

public class ResponseUtilsWithVersion {

    public <T> ApiResponseWithVersion<T> createSuccessResponse(T  data, TypeReference<T> tClass){
        ApiResponseWithVersion<T> response  =   new ApiResponseWithVersion<>();
        response.setResponse(data);
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("success");
        response.setAndroidVersion("prod_v1.1.0");
        response.setApiVersion("prod_v1.1.0");
        response.setIosVersion("prod_v1.1.0");
        return response;
    }
}

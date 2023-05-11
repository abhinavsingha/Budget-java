package com.sdd.exception;

import com.sdd.response.ApiError;
import com.sdd.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class SDDExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SDDException.class)
    public ResponseEntity<Object> handleSDDException(SDDException sddException){
        ApiResponse<ApiError> errorApiResponse = new ApiResponse<>();
        errorApiResponse.setMessage(sddException.getMessage());
        errorApiResponse.setStatus(sddException.getStatus());
        ApiError apiError = new ApiError();
        apiError.setMessage(sddException.getMessage());
        errorApiResponse.setResponse(apiError);
        return new ResponseEntity<>(errorApiResponse, HttpStatus.valueOf(sddException.getStatus()));
    }

}

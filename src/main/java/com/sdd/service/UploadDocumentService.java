package com.sdd.service;


import com.sdd.entities.FileUpload;
import com.sdd.response.ApiResponse;
import com.sdd.response.UplaodMainFormDocumentsResponse;
import com.sdd.response.UserManualResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface UploadDocumentService {

	ApiResponse<UplaodMainFormDocumentsResponse> fileUplaod(MultipartFile file) throws IOException;;

    ApiResponse<FileUpload> getFilePath(String fileId);


    ApiResponse<UserManualResponse> getUserManual();

    ApiResponse<FileUpload> getApprovedFilePath(String authGoupId,String type);



}

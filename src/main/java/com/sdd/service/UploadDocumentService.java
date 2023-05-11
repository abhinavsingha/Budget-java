package com.sdd.service;


import com.sdd.entities.FileUpload;
import com.sdd.response.ApiResponse;
import com.sdd.response.UplaodMainFormDocumentsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface UploadDocumentService {

	ApiResponse<UplaodMainFormDocumentsResponse> fileUplaod(MultipartFile file) throws IOException;;

    ApiResponse<FileUpload> getFilePath(String fileId);


}

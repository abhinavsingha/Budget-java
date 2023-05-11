package com.sdd.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DocumentTypeResponse {

    private String docType;
    private String docTypeId;

    public DocumentTypeResponse(String docType, String docTypeId) {
        this.docType = docType;
        this.docTypeId = docTypeId;
    }
}

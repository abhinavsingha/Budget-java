package com.sdd.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter

public class CdaParkingResponse {

    private String ginNo;

    private String cdaName;

    private String station;

    private String cdaGroupCode;

    private Timestamp createdOn;

    private Timestamp updatedOn;



}

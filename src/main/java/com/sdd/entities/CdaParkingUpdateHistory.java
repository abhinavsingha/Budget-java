package com.sdd.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "CdaParkingUpdateHistory")
public class CdaParkingUpdateHistory {
    @Id
    @Column(name = "CDA_PARKING_UPDATE_ID", nullable = false)
    private String cdaParkingUpdateId;

    @Column(name = "OLD_AMOUNT")
    private String oldAmount;

    @Column(name = "OLD_GIN_NO")
    private String oldGinNo;

    @Column(name = "NEW_AMOUNT")
    private String newAmount;

    @Column(name = "NEW_GIN_NO")
    private String newGinNo;

    @Column(name = "UNIT_ID")
    private String unitId;

    @Column(name = "AUTH_GROUP_ID")
    private String authGroupId;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON")
    private Timestamp updatedOn;

    @Column(name = "AMOUNT_TYPE")
    private String amountType;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "SUB_HEAD")
    private String subHead;

}

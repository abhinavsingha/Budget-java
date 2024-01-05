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
@Table(name = "TransferContingentBillHistory")
public class TransferContingentBillHistory {
    @Id
    @Column(name = "TRANSFER_CB_ID")
    private String cbId;

    @Column(name = "CB_NO")
    private String cbNo;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "OLD_USER_PID")
    private String oldUserId;

    @Column(name = "NEW_USER_PIOD")
    private String newUserPid;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON")
    private Timestamp updatedOn;




}

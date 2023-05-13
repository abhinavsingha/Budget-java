package com.sdd.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "MangeInboxOutbox")
public class MangeInboxOutbox {
    @Id
    @Column(name = "MANGE_INBOX_ID", nullable = false)
    private String mangeInboxId;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "IS_FLAG")
    private String isFlag;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "status")
    private String status;

    @Column(name = "UPDATED_ON")
    private Date updatedOn;

    @Column(name = "ROLE_ID")
    private String roleId;

    @Column(name = "CREATERP_ID")
    private String createrpId;

    @Column(name = "APPROVERP_ID")
    private String approverpId;

    @Column(name = "TO_UNIT")
    private String toUnit;

    @Column(name = "FROM_UNIT")
    private String fromUnit;

    @Column(name = "IS_BGCG")
    private String isBgcg;

    @Column(name = "GROUP_ID")
    private String groupId;

    @Column(name = "type")
    private String type;

    @Column(name = "AMOUNT")
    private String amount;

    @Column(name = "STATE")
    private String state;

    @Column(name = "ALLOCATION_TYPE")
    private String allocationType;


}

package com.sdd.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "SUBHEAD_CHARGED_VOTED")
public class SubHeadVotedOrChargedType {
    @Id
    @Column(name = "SUB_HEAD_TYPE_ID", nullable = false)
    private String subHeadTypeId;

    @Column(name = "SUB_TYPE")
    private String subType;

    @Column(name = "IS_FLAG")
    private String isFlag;

    @Column(name = "UPDATED_ON")
    private Timestamp updatedOn;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;


}

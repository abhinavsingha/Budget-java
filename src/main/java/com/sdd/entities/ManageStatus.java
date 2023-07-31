package com.sdd.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "MsgStatus")
public class ManageStatus {

    @Id
    @Column(name = "STATUS_ID")
    private String statusId;

    @Column(name = "STATUS_DESCRIPTION")
    private String statusDescription;

    @Column(name = "is_flag")
    private String isFlag;


    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON")
    private Timestamp updatedOn;


}
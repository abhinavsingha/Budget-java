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
@Table(name = "CurrntStateType")
public class CurrntStateType {
    @Id
    @Column(name = "CURRENT_STATE_ID", nullable = false)
    private String currentStateId;

    @Column(name = "STATE_ID")
    private String StateId;

    @Column(name = "STATE_NAME")
    private String StateName;


    @Column(name = "IS_FLAG")
    private String isFlag;


    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;



}

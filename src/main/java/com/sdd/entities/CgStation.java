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
@Transactional
@Table(name = "CgStation")
public class CgStation {

    @Id
    @Column(name = "STATION_ID")
    private String stationId;

    @Column(name = "STATION_NAME")
    private String stationName;

    @Column(name = "RHQ_ID")
    private String rhqId;

    @Column(name = "DHQ_NAME")
    private String dhqName;

    @Column(name = "IS_FLAG")
    private String isFlag;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;


}

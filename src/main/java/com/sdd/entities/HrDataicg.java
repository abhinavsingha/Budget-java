package com.sdd.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "hr_data")
public class HrDataicg {
    @Id
    @Size(max = 4)
    @NotNull
    @Column(name = "pid", nullable = false, length = 4)
    private String pid;

    @Size(max = 12)
    @Column(name = "aadhaar_no", length = 12)
    private String aadhaarNo;

    @Size(max = 246)
    @Column(name = "add_comm", length = 246)
    private String addComm;

    @Size(max = 246)
    @Column(name = "add_pmt", length = 246)
    private String addPmt;

    @Size(max = 1)
    @Column(name = "blood_grp", length = 1)
    private String bloodGrp;

    @Size(max = 2)
    @Column(name = "cadre", length = 2)
    private String cadre;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "doe")
    private LocalDate doe;

    @Size(max = 100)
    @Column(name = "esign_id", length = 100)
    private String esignId;

    @Size(max = 1)
    @Column(name = "gender", length = 1)
    private String gender;

    @Size(max = 6)
    @Column(name = "l_unit", length = 6)
    private String lUnit;

    @Size(max = 1)
    @Column(name = "marital_status", length = 1)
    private String maritalStatus;

    @Column(name = "medcat_wef")
    private LocalDate medcatWef;

    @Size(max = 10)
    @Column(name = "mobile_no", length = 10)
    private String mobileNo;

    @Size(max = 6)
    @Column(name = "n_unit", length = 6)
    private String nUnit;

    @Column(name = "n_unit_dt")
    private LocalDate nUnitDt;

    @Size(max = 60)
    @Column(name = "name_descr", length = 60)
    private String nameDescr;

    @Size(max = 60)
    @Column(name = "name_short", length = 60)
    private String nameShort;

    @Size(max = 1)
    @Column(name = "nok_name", length = 1)
    private String nokName;

    @Column(name = "nok_nomi_dt")
    private LocalDate nokNomiDt;

    @Size(max = 1)
    @Column(name = "nok_relation", length = 1)
    private String nokRelation;

    @Size(max = 100)
    @Column(name = "off_email", length = 100)
    private String offEmail;

    @Size(max = 10)
    @Column(name = "pan_no", length = 10)
    private String panNo;

    @Size(max = 100)
    @Column(name = "pers_email", length = 100)
    private String persEmail;

    @Size(max = 15)
    @Column(name = "pno", length = 15)
    private String pno;

    @Size(max = 3)
    @Column(name = "rank", length = 3)
    private String rank;

    @Size(max = 1)
    @Column(name = "resi_phone", length = 1)
    private String resiPhone;

    @Column(name = "seniority")
    private LocalDate seniority;

    @Size(max = 4)
    @Column(name = "station", length = 4)
    private String station;

    @Column(name = "station_dt")
    private LocalDate stationDt;

    @Size(max = 1)
    @Column(name = "status_code", length = 1)
    private String statusCode;

    @Size(max = 1)
    @Column(name = "suf", length = 1)
    private String suf;

    @Size(max = 6)
    @Column(name = "unit", length = 6)
    private String unit;

    @Column(name = "unit_dt")
    private LocalDate unitDt;

    @Size(max = 100)
    @Column(name = "user_name", length = 100)
    private String userName;

    @Size(max = 20)
    @Column(name = "voter_id", length = 20)
    private String voterId;

    @Size(max = 1)
    @Column(name = "work_phone", length = 1)
    private String workPhone;

    @Size(max = 20)
    @Column(name = "org", length = 20)
    private String org;

    @Size(max = 10)
    @Column(name = "is_release", length = 10)
    private String isRelease;

}

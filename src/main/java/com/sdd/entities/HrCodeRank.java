package com.sdd.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "hr_code_rank")
public class HrCodeRank {
    @Id
    @Size(max = 3)
    @Column(name = "frank", length = 3)
    private String frank;

    @Size(max = 3)
    @Column(name = "rank_bvk", length = 3)
    private String rankBvk;

    @Size(max = 4)
    @Column(name = "rank_hiimis", length = 4)
    private String rankHiimis;

    @Size(max = 35)
    @Column(name = "descr", length = 35)
    private String descr;

    @Size(max = 30)
    @Column(name = "short", length = 30)
    private String shortField;

    @Size(max = 2)
    @Column(name = "rank_rk", length = 2)
    private String rankRk;

    @Size(max = 2)
    @Column(name = "rank_spl", length = 2)
    private String rankSpl;

    @Size(max = 2)
    @Column(name = "cadre", length = 2)
    private String cadre;

    @Size(max = 2)
    @Column(name = "branch", length = 2)
    private String branch;

    @Size(max = 2)
    @Column(name = "org", length = 2)
    private String org;

    @Column(name = "is_so")
    private Short isSo;

    @Column(name = "is_gazetted")
    private Short isGazetted;

    @Size(max = 2)
    @Column(name = "bvk_cadre", length = 2)
    private String bvkCadre;

}

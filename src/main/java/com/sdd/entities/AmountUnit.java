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
@Table(name = "AmountUnit")
public class AmountUnit {

    @Id
    @Column(name = "AMOUNT_TYPE_ID", nullable = false)
    private String amountTypeId;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @Column(name = "AMOUNT_TYPE")
    private String amountType;


    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @Column(name = "UPDATED_ON", nullable = false)
    private Timestamp updatedOn;


}

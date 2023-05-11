package com.sdd.entities.repository;



import com.sdd.entities.AmountUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmountUnitRepository extends JpaRepository<AmountUnit, String> {


    AmountUnit findByAmountTypeId(String amountTypeId);



}

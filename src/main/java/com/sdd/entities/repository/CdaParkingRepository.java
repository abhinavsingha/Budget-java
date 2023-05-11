package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetFinancialYear;
import com.sdd.entities.CdaParking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CdaParkingRepository extends JpaRepository<CdaParking, String> {


    CdaParking findByGinNo(String s);

    List<CdaParking> findAllByOrderByCdaNameAsc();
    List<CdaParking> findByCdaGroupCodeOrderByCdaNameAsc(String purposeCOde);
}

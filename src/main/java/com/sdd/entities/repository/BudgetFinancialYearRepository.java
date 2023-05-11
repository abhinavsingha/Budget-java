package com.sdd.entities.repository;


import com.sdd.entities.BudgetFinancialYear;
import com.sdd.entities.CgUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetFinancialYearRepository extends JpaRepository<BudgetFinancialYear, String> {


    BudgetFinancialYear findBySerialNo(String serialNo);

    List<BudgetFinancialYear> findAllByOrderByFinYearAsc();

}

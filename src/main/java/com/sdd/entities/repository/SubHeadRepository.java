package com.sdd.entities.repository;


import com.sdd.entities.BudgetHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubHeadRepository extends JpaRepository<BudgetHead, Long> {


    BudgetHead findByBudgetCodeIdAndSubHeadTypeIdOrderBySerialNumberAsc(String subHead, String subHeadType);

    BudgetHead findByBudgetCodeIdOrderBySerialNumberAsc(String subHead);

    BudgetHead findByBudgetCodeId(String subHead);

    List<BudgetHead> findByMajorHeadAndSubHeadTypeIdOrderBySerialNumberAsc(String majorHead, String subHeadType);

    List<BudgetHead> findBySubHeadTypeIdOrderBySerialNumberAsc(String subHeadType);

    List<BudgetHead> findByMajorHeadOrderBySerialNumberAsc(String majorHead);

    List<BudgetHead> findAllByOrderBySerialNumberAsc();
}

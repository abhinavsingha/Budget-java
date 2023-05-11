package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetRebase;
import com.sdd.entities.CgUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BudgetRebaseRepository extends JpaRepository<BudgetRebase, String> {


    BudgetRebase findByBudgetRebaseId(String rebaseId);

    @Query(value="SELECT max(BUDGET_REBASE_ID) FROM budgetrebase WHERE TO_UNIT_ID=:toUnitId",nativeQuery = true)
    String findMaxRebaseIDByTounit(String toUnitId);





}

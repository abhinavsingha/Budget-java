package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetRebase;
import com.sdd.entities.CgUnit;
import com.sdd.entities.ContigentBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BudgetRebaseRepository extends JpaRepository<BudgetRebase, String> {


    BudgetRebase findByBudgetRebaseId(String rebaseId);

    @Query(value="SELECT max(BUDGET_REBASE_ID) FROM budgetrebase WHERE REBASE_UNIT_ID=:rebaseUnitId",nativeQuery = true)
    String findMaxRebaseIDByRebaseUnitId(String rebaseUnitId);

    List<BudgetRebase> findAllByOrderByBudgetHeadIdAsc();

    @Query(value ="select REBASE_UNIT_ID from budgetrebase group by REBASE_UNIT_ID",nativeQuery = true)
    List<String> findGroupRebaseUnit();

    @Query(value ="select AUTH_GRP_ID from budgetrebase group by AUTH_GRP_ID",nativeQuery = true)
    List<String> findAuthGroupRebaseUnit();

    @Query(value ="select AUTH_GRP_ID from budgetrebase where REBASE_UNIT_ID=:unitId group by AUTH_GRP_ID",nativeQuery = true)
    List<String> findAuthGroupRebaseUnit(String unitId);

    List<BudgetRebase> findByRebaseUnitId(String rebaseUnitId);

    List<BudgetRebase> findByAuthGrpId(String authGrId);

    @Query(value ="select ALLOC_FROM_UNIT,REBASE_UNIT_ID,TO_HEAD_UNIT_ID from budgetrebase where REBASE_UNIT_ID=:unitId",nativeQuery = true)
    List<String> findByAuthorityunit(String unitId);







}

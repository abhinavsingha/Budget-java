package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.ContigentBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContigentBillRepository extends JpaRepository<ContigentBill, Long> {


    ContigentBill findByCbIdAndIsFlag(String contingentBilId,String isFlag);

    List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadID(String unitId, String finYear, String subHead);
    List<ContigentBill> findByCbUnitIdAndIsFlag(String cbUnitId,String isFlag);
    List<ContigentBill> findByBudgetHeadIDAndIsFlag(String cbUnitId,String isFlag);
    List<ContigentBill> findByCbUnitIdAndBudgetHeadIDAndIsFlag(String cbUnitId,String budgetHeadId,String isFlag);
    List<ContigentBill> findByAuthGroupIdAndIsFlag(String groupId,String isFlag);
    List<ContigentBill> findByAuthGroupIdAndStatusAndIsFlag(String groupId,String ststus,String isFlag);

    @Query(value="SELECT PROGRESSIVE_AMOUNT,CB_DATE FROM contigentbill where CB_UNIT_ID=:unitId and FIN_YEAR=:finYear and BUDGET_HEAD_ID=:subHead",nativeQuery = true)
    List<ContigentBill> findExpAndCbDate(String unitId, String finYear, String subHead);
}

package com.sdd.entities.repository;

import com.sdd.entities.AllocationType;
import com.sdd.entities.CgUnit;
import com.sdd.entities.ContigentBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContigentBillRepository extends JpaRepository<ContigentBill, Long> {

  @Query(
      value =
          "SELECT CB_AMOUNT,CB_DATE FROM contigentbill where CB_UNIT_ID=:unitId and FIN_YEAR=:finYear and BUDGET_HEAD_ID=:subHead and STATUS=:status and IS_UPDATED=:update",
      nativeQuery = true)
  List<ContigentBill> findExpAndCbDate(String unitId, String finYear, String subHead,String status,String update);

    List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadIDAndStatusAndIsUpdateAndIsFlag(String unitId, String finYear, String subHead,String status,String update, String isFlag);



    ContigentBill findByCbIdAndIsFlagAndIsUpdate(String contingentBilId, String isFlag, String isUpdate);
    ContigentBill findByCbIdAndIsFlag(String contingentBilId, String isFlag);
    ContigentBill findByCbId(String contingentBilId);

    List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsUpdate(String unitId, String finYear, String subHead, String isUpdate);

  List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdate(String unitId, String finYear, String subHead,String allocTypeId, String isUpdate);

    List<ContigentBill> findByCbUnitIdAndIsFlagAndIsUpdate(String cbUnitId, String isFlag, String isUpdate);
    List<ContigentBill> findByCbUnitId(String cbUnitId);
    List<ContigentBill> findByCbUnitIdAndFinYearAndAndIsFlagAndIsUpdate(String cbUnitId,String finYear, String isFlag, String isUpdate);

    List<ContigentBill> findByBudgetHeadIDAndIsFlagAndIsUpdate(String cbUnitId, String isFlag, String isUpdate);

    List<ContigentBill> findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(
            String cbUnitId, String budgetHeadId, String isFlag, String isUpdate);

    List<ContigentBill> findByAuthGroupIdAndIsFlag(String groupId, String isFlag);
    List<ContigentBill> findByAuthGroupId(String groupId);

    List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(
            String cgUnits, String finYear, String budgetHeadID,String isFlag,String isupdate);



}

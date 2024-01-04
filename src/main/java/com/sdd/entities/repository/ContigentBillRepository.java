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




  ContigentBill findByCbIdAndIsFlagAndIsUpdate(String contingentBilId, String isFlag, String isUpdate);
  ContigentBill findByCbId(String contingentBilId);
  ContigentBill findByCbUnitIdAndSectionNumberAndBudgetHeadID(String contingentBilId,String billNo,String budgetHeadId);
  List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsUpdate(String unitId, String finYear, String subHead, String isUpdate);
//  List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdate(String unitId, String finYear, String subHead,String allocTypeId, String isUpdate);


  List<ContigentBill> findByCbUnitId(String cbUnitId);
  List<ContigentBill> findByCbUnitIdAndCreatedBy(String cbUnitId,String createrId);
  List<ContigentBill> findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdate(String cbUnitId, String budgetHeadId, String isFlag, String isUpdate);
//  List<ContigentBill> findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndAllocationTypeIdAndFinYear(String cbUnitId, String budgetHeadId, String isFlag, String isUpdate, String allocationTypeId, String finYear);

  List<ContigentBill> findByCbUnitIdAndBudgetHeadIDAndIsFlagAndIsUpdateAndFinYear(String cbUnitId, String budgetHeadId, String isFlag, String isUpdate,  String finYear);

  List<ContigentBill> findByAuthGroupIdAndIsFlag(String groupId, String isFlag);
  List<ContigentBill> findByAuthGroupId(String groupId);
  List<ContigentBill> findByCbUnitIdAndStatus(String groupId,String status);
  List<ContigentBill> findByCbUnitIdAndStatusAndCreatedBy(String groupId,String status,String createdBy);

  List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsFlagAndIsUpdateOrderByCbDateDesc(
          String cgUnits, String finYear, String budgetHeadID,String isFlag,String isupdate);

//  List<ContigentBill> findByAllocationTypeIdAndCbUnitIdAndFinYear(String allocationId, String unitId, String finYear);
  List<ContigentBill> findByCbUnitIdAndFinYear(String unitId, String finYear);


//  List<ContigentBill> findByAllocationTypeIdAndCbUnitIdAndFinYearAndBudgetHeadID(String allocationId, String unitId, String finYear, String budgetHeadId);
  List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadID(String unitId, String finYear, String budgetHeadId);


  List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadIDAndIsUpdateAndIsFlag(String unitId, String finYear, String subHead, String isUpdate,String isFlag);
//  List<ContigentBill> findByCbUnitIdAndFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdateAndIsFlag(String unitId, String finYear, String subHead,String allocTypeId, String isUpdate,String isFlag);


  List<ContigentBill> findByFinYearAndBudgetHeadIDAndIsUpdateAndIsFlagAndCbUnitId(String finYear,String subHead, String isUpdate,String isFlag,String unitId);
//  List<ContigentBill> findByFinYearAndBudgetHeadIDAndAllocationTypeIdAndIsUpdateAndIsFlagAndCbUnitId(String finYear,String subHead,String allocTypeId, String isUpdate,String isFlag,String unitId);


}

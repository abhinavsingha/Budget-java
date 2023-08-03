package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetAllocation;
import com.sdd.entities.BudgetAllocationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BudgetAllocationDetailsRepository extends JpaRepository<BudgetAllocationDetails, Long> {



    List<BudgetAllocationDetails> findByAuthGroupIdAndIsDeleteAndIsBudgetRevision(String unitId,String isDelete,String isRevision);
    List<BudgetAllocationDetails> findByAuthGroupIdAndIsDelete(String unitId,String isDelete);
    List<BudgetAllocationDetails> findByToUnitAndIsDeleteAndIsBudgetRevision(String unitId,String isDelete,String isRevision);
    List<BudgetAllocationDetails> findByFromUnitAndIsDeleteAndStatusAndIsBudgetRevision(String unitId,String isDelete,String status,String isRivision);
    List<BudgetAllocationDetails> findByFromUnitAndIsDeleteAndIsBudgetRevision(String unitId,String isDelete,String isRivision);
    BudgetAllocationDetails findByTransactionIdAndIsDeleteAndIsBudgetRevision(String unitId,String isDelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String isDelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndIsDeleteAndStatusOrStatusAndIsBudgetRevision(String unitId,String finYear,String isDelete,String status,String statusR,String isRivision);
    List<BudgetAllocationDetails> findBySubHeadAndFinYearAndIsDeleteAndStatusAndIsBudgetRevision(String subHeadId,String finYear,String isDelete,String status,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String subHeadId,String allocationType,String status,String isdelete,String isRivision);
    List<BudgetAllocationDetails> findByFromUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String subHeadId,String allocationType,String status,String isdelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatusIn(String unitId,String finYear,String subHeadId,String allocationType,String isDelete,String isRivision,List<String> cgUnits);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatus(String unitId,String finYear,String allocationType,String isDelete,String isRivision,String status);

    @Query(
            value =
                    "select BUDGET_HEAD from BudgetAllocationDetails where AUTH_GROUP_ID=:authGroupId  group by BUDGET_HEAD",
            nativeQuery = true)
    List<String> findSubHeadByAuthGroupIds(String authGroupId);
    @Query(
            value =
                    "select BUDGET_HEAD from BudgetAllocationDetails where FIN_YEAR=:finYearId and ALLOC_TYPE_ID=:allocationTypeId and FROM_UNIT=:frmUnit  group by BUDGET_HEAD",
            nativeQuery = true)
    List<String> findSubHead(String finYearId, String allocationTypeId,String frmUnit);

    List<BudgetAllocationDetails> findByAuthGroupIdAndIsDeleteOrderByTransactionIdAsc(String authGroupId, String s);
    BudgetAllocationDetails findByTransactionId(String transId);
    List<BudgetAllocationDetails> findByAuthGroupId(String authGroupId);

    List<BudgetAllocationDetails> findByAuthGroupIdAndSubHead(String authGId, String subHead);
    List<BudgetAllocationDetails> findByAuthGroupIdAndToUnitOrderByTransactionIdAsc(String authGroupId,String toUnitid);
    List<BudgetAllocationDetails> findByFromUnitAndFinYearAndAllocTypeIdAndIsBudgetRevision(
            String frmUnit, String finYear, String allocationTypeId, String isRevision);
    List<BudgetAllocationDetails> findBySubHeadAndFromUnitAndFinYearAndAllocTypeIdAndIsBudgetRevision(
            String subHeadId,String frmUnit, String finYear, String allocationTypeId, String isRevision);

    List<BudgetAllocationDetails> findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String subHeadId,String allocationType,String isDelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String allocationType,String isDelete,String isRivision);

}

package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
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
    List<BudgetAllocationDetails> findByFromUnitAndFinYearAndSubHeadAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String subHeadId,String isDelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String subHeadId,String allocationType,String status,String isdelete,String isRivision);
    List<BudgetAllocationDetails> findByFromUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String subHeadId,String allocationType,String status,String isdelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String subHeadId,String allocationType,String isDelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevisionAndStatusIn(String unitId,String finYear,String subHeadId,String allocationType,String isDelete,String isRivision,List<String> cgUnits);
    List<BudgetAllocationDetails> findByAuthGroupIdAndSubHeadAndIsDelete(String authGroupId,String subHead,String isDelete);
    @Query(
            value =
                    "select BUDGET_HEAD from BudgetAllocationDetails where AUTH_GROUP_ID=:authGroupId  group by BUDGET_HEAD",
            nativeQuery = true)
    List<String> findSubHead(String authGroupId);

    List<BudgetAllocationDetails> findByAuthGroupIdAndIsDeleteOrderByTransactionIdAsc(String authGroupId, String s);
    List<BudgetAllocationDetails> findByAuthGroupId(String authGroupId);
    List<BudgetAllocationDetails> findByAuthGroupIdAndToUnitOrderByTransactionIdAsc(String authGroupId,String toUnitid);
}

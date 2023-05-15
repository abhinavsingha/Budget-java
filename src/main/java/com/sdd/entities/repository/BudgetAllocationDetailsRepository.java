package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetAllocationDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetAllocationDetailsRepository extends JpaRepository<BudgetAllocationDetails, Long> {



    List<BudgetAllocationDetails> findByAuthGroupIdAndIsDeleteAndIsBudgetRevision(String unitId,String isDelete,String isRevision);
    List<BudgetAllocationDetails> findByToUnitAndIsDeleteAndIsBudgetRevision(String unitId,String isDelete,String isRevision);
    List<BudgetAllocationDetails> findByFromUnitAndIsDeleteAndStatusAndIsBudgetRevision(String unitId,String isDelete,String status,String isRivision);
    List<BudgetAllocationDetails> findByFromUnitAndIsDeleteAndIsBudgetRevision(String unitId,String isDelete,String isRivision);
    BudgetAllocationDetails findByTransactionIdAndIsDeleteAndIsBudgetRevision(String unitId,String isDelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String isDelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndIsDeleteAndStatusOrStatusAndIsBudgetRevision(String unitId,String finYear,String isDelete,String status,String statusR,String isRivision);
    List<BudgetAllocationDetails> findBySubHeadAndFinYearAndIsDeleteAndStatusAndIsBudgetRevision(String subHeadId,String finYear,String isDelete,String status,String isRivision);
    List<BudgetAllocationDetails> findByFromUnitAndFinYearAndSubHeadAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String subHeadId,String isDelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String subHeadId,String allocationType,String status,String isdelete,String isRivision);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDeleteAndIsBudgetRevision(String unitId,String finYear,String subHeadId,String allocationType,String isDelete,String isRivision);

}

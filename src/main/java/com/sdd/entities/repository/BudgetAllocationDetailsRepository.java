package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetAllocationDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetAllocationDetailsRepository extends JpaRepository<BudgetAllocationDetails, Long> {



    List<BudgetAllocationDetails> findByAuthGroupIdAndIsDelete(String unitId,String isDelete);
    BudgetAllocationDetails findByAuthGroupIdAndTransactionIdAndIsDelete(String authGroupId,String tansId,String isDelete);
    List<BudgetAllocationDetails> findByAuthGroupIdAndIsDeleteAndToUnit(String unitId,String isDelete,String toUnit);
    List<BudgetAllocationDetails> findByToUnitAndIsDelete(String unitId,String isDelete);
    List<BudgetAllocationDetails> findByFromUnitAndIsDeleteAndStatus(String unitId,String isDelete,String status);
    List<BudgetAllocationDetails> findByFromUnitAndIsDelete(String unitId,String isDelete);
    BudgetAllocationDetails findByTransactionIdAndIsDelete(String unitId,String isDelete);
    BudgetAllocationDetails findByAllocationAmountAndIsDelete(String allocationId,String isDelete);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndIsDelete(String unitId,String finYear,String isDelete);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndIsDeleteAndStatusOrStatus(String unitId,String finYear,String isDelete,String status,String statusR);

    List<BudgetAllocationDetails> findBySubHeadAndFinYearAndIsDeleteAndStatus(String subHeadId,String finYear,String isDelete,String status);
    List<BudgetAllocationDetails> findByFromUnitAndFinYearAndSubHeadAndIsDelete(String unitId,String finYear,String subHeadId,String isDelete);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndStatusAndIsDelete(String unitId,String finYear,String subHeadId,String allocationType,String status,String isdelete);
    List<BudgetAllocationDetails> findByToUnitAndFinYearAndSubHeadAndAllocTypeIdAndIsDelete(String unitId,String finYear,String subHeadId,String allocationType,String isDelete);

}

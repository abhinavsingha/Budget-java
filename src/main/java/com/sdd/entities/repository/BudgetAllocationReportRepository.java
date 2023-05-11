package com.sdd.entities.repository;


import com.sdd.entities.BudgetAllocationDetails;
import com.sdd.entities.BudgetAllocationReport;
import com.sdd.entities.BudgetRebase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BudgetAllocationReportRepository extends JpaRepository<BudgetAllocationReport, String> {


    List<BudgetAllocationReport> findByUnitIdAndFinYearIdAndAllocationTypeId(String unitId, String budgetFinancialYearId, String allocationTypeId);
    List<BudgetAllocationReport> findByUnitIdAndFinYearIdAndAllocationTypeIdAndSubHeadIdAndIsFlag(String unitId, String budgetFinancialYearId, String allocationTypeId, String subHeadId,String isFlag);
    List<BudgetAllocationReport>findByUnitIdAndFinYearId(String unitId,String finYear);
    List<BudgetAllocationReport>findBySubHeadIdAndFinYearId(String subHeadId,String finYear);
    List<BudgetAllocationReport>findByAuthGroupId(String authGroupId);
    List<BudgetAllocationReport>findBySubHeadIdAndAllocationType(String subHeadId,String allocationType);
    List<BudgetAllocationReport> findByUnitIdAndFinYearIdAndAllocationTypeAndSubHeadId(String unitId, String budgetFinancialYearId, String allocationTypeId, String subHeadId);
    @Query(value="select SUB_HEAD_ID from budgetallocationreport where FIN_YEAR_ID=:finYearId and ALLOCATION_TYPE=:allocationType  group by SUB_HEAD_ID",nativeQuery = true)
    List<String> findSubHead(String finYearId,String allocationType);

}

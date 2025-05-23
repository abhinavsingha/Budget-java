package com.sdd.entities.repository;

import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetAllocation;
import com.sdd.entities.BudgetAllocationDetails;
import com.sdd.entities.ContigentBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface BudgetAllocationRepository extends JpaRepository<BudgetAllocation, Long> {

    List<BudgetAllocation> findByToUnitAndIsFlagAndIsBudgetRevision(String toUnit, String isFlag, String isRebison);

    List<BudgetAllocation> findByToUnitAndSubHeadAndIsFlagAndIsBudgetRevision(
            String toUnit, String budgetHeadId, String isFlag, String isRevison);

    List<BudgetAllocation> findByAuthGroupIdAndIsFlagAndIsBudgetRevision(String toUnit, String isFlag ,String isRevison);

    List<BudgetAllocation> findByToUnitAndFinYearAndIsFlagAndIsBudgetRevision(
            String toUnit, String finYear, String isFalg, String isRevision);
    List<BudgetAllocation> findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(
            String toUnit, String finYear, String allocationTypeId, String isRevision);


    List<BudgetAllocation> findByToUnitAndFinYearAndIsBudgetRevision(
            String toUnit, String finYear, String isRevision);

    List<BudgetAllocation> findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(String unitId, String finYearId, String allocationTypeIdR, String s, String s1, String approved);
    List<BudgetAllocation> findByToUnitAndFinYearAndIsBudgetRevisionAndIsFlagAndStatus(String unitId, String finYearId,   String s, String s1, String approved);


    List<BudgetAllocation> findByToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatusOrderByCreatedOnAsc(String unitId, String finYearId, String allocationTypeIdR, String s, String s1, String approved);



    List<BudgetAllocation> findByAuthGroupIdAndIsFlagAndToUnit(
            String unitId, String isFlag, String toUnit);

    List<BudgetAllocation> findByAuthGroupIdAndToUnit(
            String unitId, String toUnit);

    List<BudgetAllocation> findByAuthGroupIdAndToUnitOrderByCreatedOnAsc(
            String unitId, String toUnit);

    List<BudgetAllocation> findByAuthGroupIdAndToUnitAndIsFlag(
            String unitId, String toUnit, String isDelete);


    List<BudgetAllocation> findByAuthGroupIdAndToUnitAndIsBudgetRevisionAndIsFlag(
            String unitId, String toUnit, String isRivison, String isDelete);

    List<BudgetAllocation> findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(
            String unitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String approved,
            String isFlag,
            String isRevision);



    List<BudgetAllocation> findByFromUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(
            String unitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String approved,
            String isFlag,
            String isRevision);



    List<BudgetAllocation> findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevision(
            String unitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String isRevision);

    List<BudgetAllocation> findByToUnitAndFinYearAndSubHeadAndAllocationTypeIdAndStatusOrderByCreatedOnAsc(
            String unitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String status);
    List<BudgetAllocation> findByFromUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(
            String frmUnit, String finYear, String allocationTypeId, String isRevision);





    List<BudgetAllocation> findByToUnitAndFromUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevision(
            String unitId,
            String frmUnitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String isRevision);




    List<BudgetAllocation> findByToUnitAndFromUnitAndFinYearAndSubHeadAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(
            String unitId,
            String frmUnitId,
            String finYearId,
            String budgetCodeId,
            String allocationType,
            String isRevision,
            String isFlag,
            String status);


    BudgetAllocation findByAllocationId(String transactionId);
    List<BudgetAllocation> findByAllocationTypeIdAndToUnit(
            String allocationTypeId,String unitId);




    List<BudgetAllocation> findBySubHeadAndFromUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevision(
            String subHeadId,String frmUnit, String finYear, String allocationTypeId, String isRevision);
    List<BudgetAllocation> findBySubHeadAndFromUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(
            String subHeadId,String frmUnit, String finYear, String allocationTypeId, String isRevision,String isFlag,String status);

    List<BudgetAllocation> findBySubHeadAndFromUnitAndFinYearAndIsBudgetRevisionAndIsFlagAndStatus(
            String subHeadId,String frmUnit, String finYear,  String isRevision,String isFlag,String status);

    @Query(value = "SELECT a FROM BudgetAllocation a  WHERE a.fromUnit = :fromUnit AND " +
            "a.allocationTypeId = :allocationTypeId  AND a.allocationAmount <> '0.0000'  AND a.status = :status " +
            "AND a.createdOn = (  SELECT MIN(b.createdOn) FROM BudgetAllocation b  " +
            "WHERE b.subHead = a.subHead  AND b.toUnit = a.toUnit " +
            "AND b.fromUnit = :fromUnit  AND b.allocationTypeId = :allocationTypeId  AND b.allocationAmount <> '0.0000'   AND b.status = :status)")
    List<BudgetAllocation> getAllocationReport(
            @Param("fromUnit") String fromUnit,
            @Param("allocationTypeId") String allocationTypeId,
             @Param("status") String status
    );




    List<BudgetAllocation> findByFromUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(
            String frmUnit, String finYear, String allocationTypeId, String isRevision,String isFlag,String status);

    List<BudgetAllocation> findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlag(
            String subHeadId,String toUnit, String finYear, String allocationTypeId, String isRevision, String isFlag);

    List<BudgetAllocation> findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndIsBudgetRevisionAndIsFlagAndStatus(
            String subHeadId,String toUnit, String finYear, String allocationTypeId, String isRevision,String isFlag,String status);


    List<BudgetAllocation> findBySubHeadAndToUnitAndFinYearAndAllocationTypeIdAndStatusOrderByCreatedOnAsc(
            String subHeadId,String toUnit, String finYear, String allocationTypeId,String status);

    List<BudgetAllocation> findByAuthGroupId(String authGroupId);


    List<BudgetAllocation> findByAuthGroupIdAndIsFlag(String authGroupId, String s);

    List<BudgetAllocation> findByAuthGroupIdAndSubHead(String authGroupId, String subHeadId);

    @Query(
            value =
                    "select SUB_HEAD from BudgetAllocation where AUTH_GROUP_ID=:authGroupId  group by SUB_HEAD",
            nativeQuery = true)
    List<String> findSubHeadByAuthGroupIds(String authGroupId);

    List<BudgetAllocation> findByToUnitAndFinYearAndAllocationTypeIdAndStatusAndIsFlagAndIsBudgetRevision(String unitId, String finYearId, String allocationTypeId, String status, String isFlag, String isRevision);
}

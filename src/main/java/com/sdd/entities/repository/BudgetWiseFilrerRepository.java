package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetUnitWiseSubHeadFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetWiseFilrerRepository extends JpaRepository<BudgetUnitWiseSubHeadFilter, String> {


    List<BudgetUnitWiseSubHeadFilter> findByUnitIdAndFinYearIdAndCodeSubHeadIdAndCodeMajorHeadId(String unitId,String finYear,String subHeadId,String codeMajorHeadId);
    List<BudgetUnitWiseSubHeadFilter> findByPidData(String pId);
    List<BudgetUnitWiseSubHeadFilter> findByUnitIdAndFinYearIdAndCodeMajorHeadId(String unitId,String finYear,String codeMajorHeadId);

}

package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.CdaRevisionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRevisionRepository extends JpaRepository<CdaRevisionData, String> {


    List<CdaRevisionData> findByAuthGroupId(String authGroupId);
    List<CdaRevisionData> findByAuthGroupIdAndToUnitId(String authGroupId,String unitId);
}

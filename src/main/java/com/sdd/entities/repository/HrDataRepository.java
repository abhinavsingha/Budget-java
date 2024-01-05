package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.CgUnit;
import com.sdd.entities.HrData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrDataRepository extends JpaRepository<HrData, String> {


    HrData findByUserNameAndIsActive(String userName,String isActive);

    List<HrData> findByIsActive(String isActive);


    HrData findByPidAndIsActive(String userName,String isActive);


//    List<HrData> findByUnitId(String unitId);
    List<HrData> findByUnitIdAndIsActive(String unitId,String isActive);

    HrData findByUserName(String updatedBy);
    HrData findByPid(String pid);
}

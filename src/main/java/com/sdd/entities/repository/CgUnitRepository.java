package com.sdd.entities.repository;


import com.sdd.entities.CgUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CgUnitRepository extends JpaRepository<CgUnit, Long> {


    CgUnit findByUnit(String cbUnit);

    @Query(value = "SELECT DESCR FROM cgunit where UNIT=:unitId", nativeQuery = true)
    String findUnitName(String unitId);

    List<CgUnit> findAllByOrderByDescrAsc();

    List<CgUnit> findByPurposeCodeOrPurposeCodeOrderByDescrAsc(String purpose, String purpose1);

    List<CgUnit> findBySubUnitOrderByDescrAsc(String subUnit);

    List<CgUnit> findByPurposeCodeOrderByDescrAsc(String purpose);

    List<CgUnit> findByBudGroupUnitLike(String unitId);

//    CgUnit findByUnit(String authorityUnit);
}

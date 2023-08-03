package com.sdd.entities.repository;

import com.sdd.entities.CgUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CgUnitRepository extends JpaRepository<CgUnit, Long> {

  CgUnit findByUnit(String cbUnit);
  CgUnit findByUnitAndIsActive(String cbUnit,String isActive);

  CgUnit findByCgUnitShort(String unitShort);


  List<CgUnit> findAllByOrderByDescrAsc();
  List<CgUnit> findByUnitOrderByDescrAsc(String cbUnit);

  List<CgUnit> findByPurposeCodeOrPurposeCodeOrderByDescrAsc(String purpose, String purpose1);

  List<CgUnit> findBySubUnitOrderByDescrAsc(String subUnit);

  List<CgUnit> findByIsActiveAndIsShipOrderByDescrAsc(String active,String isShip);


  List<CgUnit> findByPurposeCodeOrderByDescrAsc(String purpose);

  List<CgUnit> findByBudGroupUnitLike(String unitId);

  CgUnit findByStationId(String stationId);

  //    CgUnit findByUnit(String authorityUnit);
}

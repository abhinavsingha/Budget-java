package com.sdd.entities.repository;

import com.sdd.entities.CgUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CgUnitRepository extends JpaRepository<CgUnit, Long> {

  CgUnit findByUnit(String cbUnit);
  CgUnit findByUnitAndIsActive(String cbUnit,String isActive);

  CgUnit findByCgUnitShort(String unitShort);


  List<CgUnit> findAllByOrderByDescrAsc();
  List<CgUnit> findByUnitOrderByDescrAsc(String cbUnit);

  List<CgUnit> findByPurposeCodeOrPurposeCodeOrderByDescrAsc(String purpose, String purpose1);

  List<CgUnit> findBySubUnitOrderByDescrAsc(String subUnit);


  List<CgUnit> findByBudGroupUnitLike(String unitId);
  List<CgUnit> findByBudGroupUnitLikeOrderByDescrAsc(String unitId);

  CgUnit findByStationId(String stationId);

  List<CgUnit> findByIsShipOrderByDescrAsc(String isShip);
  //    CgUnit findByUnit(String authorityUnit);
}

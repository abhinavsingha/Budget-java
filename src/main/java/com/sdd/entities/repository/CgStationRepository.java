package com.sdd.entities.repository;


import com.sdd.entities.CgStation;
import com.sdd.entities.CgUnit;
import com.sdd.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CgStationRepository extends JpaRepository<CgStation, String> {


    CgStation findByStationId(String stationId);
}

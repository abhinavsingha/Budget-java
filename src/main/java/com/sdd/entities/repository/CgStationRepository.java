package com.sdd.entities.repository;


import com.sdd.entities.CgStation;
import com.sdd.entities.CgUnit;
import com.sdd.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CgStationRepository extends JpaRepository<CgStation, String> {


    CgStation findByStationId(String stationId);
    CgStation findByStationName(String stationId);

    List<CgStation> findByIsBasePort(String basePort);
}

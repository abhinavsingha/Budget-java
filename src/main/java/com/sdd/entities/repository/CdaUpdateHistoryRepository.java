package com.sdd.entities.repository;


import com.sdd.entities.CdaParkingUpdateHistory;
import com.sdd.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CdaUpdateHistoryRepository extends JpaRepository<CdaParkingUpdateHistory, Long> {

    List<CdaParkingUpdateHistory> findByAuthGroupId(String groupId);
}

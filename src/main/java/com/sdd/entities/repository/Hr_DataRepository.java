package com.sdd.entities.repository;
import com.sdd.entities.HrCodeRank;
import com.sdd.entities.HrDataicg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Hr_DataRepository extends JpaRepository<HrDataicg, String> {
    HrCodeRank findByRank(String rank);
}

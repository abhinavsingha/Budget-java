package com.sdd.entities.repository;
import com.sdd.entities.HrCodeRank;
import com.sdd.entities.HrDataicg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HrcodeRankRepository extends JpaRepository<HrCodeRank, String> {
    HrCodeRank findByRank(String rank);
}

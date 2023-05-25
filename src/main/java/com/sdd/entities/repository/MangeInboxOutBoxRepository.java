package com.sdd.entities.repository;


import com.sdd.entities.CdaParking;
import com.sdd.entities.MangeInboxOutbox;
import com.sdd.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MangeInboxOutBoxRepository extends JpaRepository<MangeInboxOutbox, Long> {



    List<MangeInboxOutbox> findByToUnitAndIsBgcgOrIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(String toUnit, String isBgOrCg, String isBgOrCg1, String isAproovedd, String isArchived);

//    List<MangeInboxOutbox> findByToUnitAndIsBgcgOrderByCreatedOnDesc(String toUnit, String isBgOrCg);

    List<MangeInboxOutbox> findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(String toUnit, String isBgOrCg, String isArchived, String isApproved);
    List<MangeInboxOutbox> findByToUnitAndIsApprovedOrderByCreatedOnDesc(String toUnit, String isApproved);

    List<MangeInboxOutbox> findByFromUnitAndIsBgcgOrderByCreatedOnAsc(String toUnit, String isBgOrCg);

    MangeInboxOutbox findByGroupIdAndToUnit(String groupId, String toUnit);

    MangeInboxOutbox findByMangeInboxId(String msgId);

    List<MangeInboxOutbox> findByToUnitAndIsBgcgAndStatus(String toUnit, String bgcg, String status);
}

package com.sdd.entities.repository;


import com.sdd.entities.CdaParking;
import com.sdd.entities.MangeInboxOutbox;
import com.sdd.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MangeInboxOutBoxRepository extends JpaRepository<MangeInboxOutbox, Long> {



//    List<MangeInboxOutbox> findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgOrIsBgcgOrderByCreatedOnDesc(String toUnit, String isAproovedd, String isArchived ,String isBgOrCg, String isBgOrCg1);


    List<MangeInboxOutbox> findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(String toUnit, String isBgOrCg, String isArchived, String isApproved);
    List<MangeInboxOutbox> findByToUnitAndIsApprovedOrderByCreatedOnDesc(String toUnit, String isApproved);
    List<MangeInboxOutbox> findByToUnitAndIsArchiveOrderByCreatedOnDesc(String toUnit, String isArchvied);

    List<MangeInboxOutbox> findByFromUnitAndIsBgcgOrderByCreatedOnAsc(String toUnit, String isBgOrCg);

    MangeInboxOutbox findByGroupIdAndToUnit(String groupId, String toUnit);

    MangeInboxOutbox findByMangeInboxId(String msgId);

    List<MangeInboxOutbox> findByToUnitAndIsBgcgAndStatus(String toUnit, String bgcg, String status);

    @Query(value="select * FROM Budget.MangeInboxOutbox where TO_UNIT =:rebaseUnitId and iS_APPROVED = '0' AND IS_ARCHIVE = '0' AND (IS_BGCG = 'BG' OR IS_BGCG = 'BR');",nativeQuery = true)
    List<MangeInboxOutbox> findByInboxData(String rebaseUnitId);

}

package com.sdd.entities.repository;



import com.sdd.entities.MangeInboxOutbox;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface MangeInboxOutBoxRepository extends JpaRepository<MangeInboxOutbox, Long> {


    List<MangeInboxOutbox> findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedOrderByCreatedOnDesc(String toUnit, String isBgOrCg, String isArchived, String isApproved);
    List<MangeInboxOutbox> findByToUnitAndIsBgcgAndIsArchiveAndIsApprovedAndCreaterpIdOrderByCreatedOnDesc(String toUnit, String isBgOrCg, String isArchived, String isApproved, String createdId);
    List<MangeInboxOutbox> findByToUnitAndIsBgcgAndIsApprovedOrderByCreatedOnDesc(String toUnit,String isCgBg, String isApproved);
    List<MangeInboxOutbox> findByToUnitAndIsBgcgAndIsApprovedAndCreaterpIdOrderByCreatedOnDesc(String toUnit,String isCgBg, String isApproved, String createdId);
    List<MangeInboxOutbox> findByToUnitAndIsBgcgAndIsArchiveOrderByCreatedOnDesc(String toUnit,String isCgBg,  String isArchvied);


    List<MangeInboxOutbox> findByToUnitAndIsApprovedOrderByCreatedOnDesc(String toUnit, String isApproved);
    List<MangeInboxOutbox> findByToUnitAndIsArchiveOrderByCreatedOnDesc(String toUnit, String isArchvied);


    List<MangeInboxOutbox> findByFromUnitAndIsBgcgOrderByCreatedOnAsc(String toUnit, String isBgOrCg);

    List<MangeInboxOutbox> findByGroupIdAndToUnit(String groupId, String toUnit);
    List<MangeInboxOutbox> findByCreaterpIdAndToUnit(String createrID, String toUnit);
    List<MangeInboxOutbox> findByGroupId(String groupId);

    MangeInboxOutbox findByMangeInboxId(String msgId);

    List<MangeInboxOutbox> findByToUnitAndIsArchiveAndIsApprovedAndIsBgcgInOrderByCreatedOnDesc(String rebaseUnitId,String isArchv,String isApprov,List<String> cgUnits);


    List<MangeInboxOutbox> findByRemarks(String remarks);
}

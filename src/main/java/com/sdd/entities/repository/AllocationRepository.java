package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface AllocationRepository extends JpaRepository<AllocationType, String> {


    AllocationType findByAllocTypeId(String allocationId);

//    AllocationType findByAutherId(String authorsId);
//    List<AllocationType> findByFullNameContainingIgnoreCaseOrPublicationContainingIgnoreCaseOrPublistionDateContainingIgnoreCase(String fullName,String publication,String publicationdate);
//    List<AllocationType> findByIsActiveOrderByFullName(BigInteger isActive);
//    List<AllocationType> findAllByOrderByFullName();
}

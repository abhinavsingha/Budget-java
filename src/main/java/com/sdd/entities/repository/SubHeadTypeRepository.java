package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.SubHeadVotedOrChargedType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubHeadTypeRepository extends JpaRepository<SubHeadVotedOrChargedType, String> {


}

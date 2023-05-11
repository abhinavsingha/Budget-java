package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByAuthorityId(String authId);
    List<Authority> findByAuthGroupId(String authGroupId);
}

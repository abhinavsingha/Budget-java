package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {


    Role findByRoleId(String roleId);
    List<Role> findByPurposeCode(String roleId);
    List<Role> findByPurposeCodeOrPurposeCode(String purposeCode,String purposeCode1);
}

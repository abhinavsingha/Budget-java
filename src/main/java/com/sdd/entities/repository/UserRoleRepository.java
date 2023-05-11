package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.UserRole;
import com.sdd.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {


}

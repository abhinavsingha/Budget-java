package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {


}

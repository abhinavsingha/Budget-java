package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.CurrntStateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrentStateRepository extends JpaRepository<CurrntStateType, String> {


    List<CurrntStateType> findByTypeOrTypeAndIsFlag(String type1, String type2, String isFlag);

    CurrntStateType findByTypeAndIsFlag(String type,String isFlag);
}

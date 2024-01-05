package com.sdd.entities.repository;


import com.sdd.entities.AllocationType;
import com.sdd.entities.TransferContingentBillHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferBCbBillRepository extends JpaRepository<TransferContingentBillHistory, String> {


}

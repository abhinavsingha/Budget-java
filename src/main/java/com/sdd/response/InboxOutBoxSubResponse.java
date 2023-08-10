package com.sdd.response;

import com.sdd.entities.AllocationType;
import com.sdd.entities.BudgetAllocation;
import com.sdd.entities.CgUnit;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter

public class InboxOutBoxSubResponse {

   private Date createdOn;
   private String isFlag;
   private String remarks;
   private String roleId;
   private HradataResponse userData;
   private CgUnit toUnit;
   private CgUnit fromUnit;
   private String isBgOrCg;
   private String groupId;
   private String mangeInboxId;
   private String amount;
   private String isRebase;
   private String type;
   private String status;
   private Integer isRevision;
   private AllocationType allocationType;

   private Boolean isCda;
   private BudgetAllocation budgetAllocation;
   private String msg;
}

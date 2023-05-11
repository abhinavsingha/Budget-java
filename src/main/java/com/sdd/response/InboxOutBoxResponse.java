package com.sdd.response;

import com.sdd.entities.AllocationType;
import com.sdd.entities.CgUnit;
import com.sdd.entities.HrData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;


@Getter
@Setter

public class InboxOutBoxResponse {

   List<InboxOutBoxSubResponse> inboxList;
   List<InboxOutBoxSubResponse> outList;

}

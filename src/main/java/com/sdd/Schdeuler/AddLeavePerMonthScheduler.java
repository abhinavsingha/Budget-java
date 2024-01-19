//package com.sdd.Schdeuler;
//
//
//
//import com.sdd.entities.ContigentBill;
//import com.sdd.entities.MangeInboxOutbox;
//import com.sdd.entities.repository.ContigentBillRepository;
//import com.sdd.entities.repository.MangeInboxOutBoxRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import java.io.IOException;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//
//@Service
//@Configuration
//@EnableScheduling
//public class AddLeavePerMonthScheduler {
//
//    @Autowired
//    MangeInboxOutBoxRepository inboxOutBoxRepository;
//
//    @Autowired
//    ContigentBillRepository contigentBillRepository;
//
//
//    @Scheduled(cron = "30 0 0 * * ?")
//    public void approve() {
//
//        List<MangeInboxOutbox> getAllMainFormData = inboxOutBoxRepository.findByRemarks("Contingent Bill");
//
//        System.out.println("scheduler running===============" + getAllMainFormData.size());
//        getAllMainFormData.parallelStream().forEach(s -> {
//            try {
//                call(s);
//            } catch (Exception e) {
//                System.out.println("scheduler running===============" + e.toString());
//                e.printStackTrace();
//            }
//        });
//
//    }
//
//    public void call(MangeInboxOutbox str) throws IOException {
//
//
//
//        List<ContigentBill> congigentBill =   contigentBillRepository.findByAuthGroupId(str.getGroupId());
//        for (Integer n = 0; n < congigentBill.size(); n++) {
//
//            ContigentBill contigentBillData = congigentBill.get(n);
//            System.out.println("scheduler running===============" + contigentBillData.getAuthGroupId().toString());
//            contigentBillData.setCreatedBy(str.getCreaterpId());
//            contigentBillRepository.save(contigentBillData);
//
//        }
//    }
//
//
//}

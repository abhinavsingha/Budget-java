package com.sdd;


import com.sdd.entities.MangeInboxOutbox;
import com.sdd.entities.repository.MangeInboxOutBoxRepository;
import com.sdd.utils.ConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
@Configuration
@EnableScheduling
public class AutoSetExpiryScheduler {


    @Autowired
    MangeInboxOutBoxRepository mangeInboxOutBoxRepository;


    @Scheduled(cron = "0 */1 * ? * *")
    public void approve() {

        List<MangeInboxOutbox> getAllMainFormData = mangeInboxOutBoxRepository.findAll();


        getAllMainFormData.parallelStream().forEach(inboxOutBoxMsg -> {
            try {
                call(inboxOutBoxMsg);

            } catch (Exception e) {
                System.out.println("scheduler running===============" + e.toString());
                e.printStackTrace();
            }
        });

    }

    public void call(MangeInboxOutbox inboxOutbox) throws IOException {


        Date date =  inboxOutbox.getCreatedOn();
        Date currentDate =  new Date();

        long dayDiffer = ConverterUtils.timeDiffer(currentDate,date);

        System.out.println("scheduler working==============="+dayDiffer);

    }


}

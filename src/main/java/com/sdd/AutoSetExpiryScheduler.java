package com.sdd;


import com.sdd.entities.MangeInboxOutbox;
import com.sdd.entities.repository.MangeInboxOutBoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
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

        System.out.println("scheduler running===============" + getAllMainFormData.size());

        getAllMainFormData.parallelStream().forEach(s -> {
            try {
                call(s);

            } catch (Exception e) {
                System.out.println("scheduler running===============" + e.toString());
                e.printStackTrace();
            }
        });

    }

    public void call(MangeInboxOutbox str) throws IOException {

        System.out.println("scheduler working===============");

    }


}

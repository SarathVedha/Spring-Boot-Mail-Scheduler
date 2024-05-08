package com.vedha.mail.scheduler;

import com.vedha.mail.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailScheduler {

    private final MailSenderService mailSenderService;

    // cron expression for every second is "0 * * * * *"
    // corn expression for every 5 seconds is "0/5 * * * * *"
    // cron expression for every 10 seconds is "0/10 * * * * *"
    @Scheduled(cron = "0/30 * * * * *") // every 30 seconds
    public void runScheduler() {

        int total = mailSenderService.sendScheduledMailWithAttachment();

        log.info("Total scheduled mails sent: {}", total);
    }
}

package com.vedha.mail.service;

import com.vedha.mail.dto.MailAttachDTO;
import com.vedha.mail.dto.MailDTO;
import com.vedha.mail.dto.ScheduledMailDTO;
import com.vedha.mail.entity.ScheduledMailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MailSenderService {

    void sendMail(MailDTO mailDTO);

    void sendMailWithAttachment(MailAttachDTO mailAttachDTO);

    ScheduledMailEntity sendScheduledMail(ScheduledMailDTO scheduledMailDTO);

    int sendScheduledMailWithAttachment();

    Page<ScheduledMailEntity> getAllScheduledMails(Pageable pageable);

}

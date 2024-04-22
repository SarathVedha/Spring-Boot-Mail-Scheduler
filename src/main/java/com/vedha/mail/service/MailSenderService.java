package com.vedha.mail.service;

import com.vedha.mail.dto.MailAttachDTO;
import com.vedha.mail.dto.MailDTO;
import com.vedha.mail.dto.ScheduledMailDTO;
import com.vedha.mail.entity.ScheduledMailEntity;

public interface MailSenderService {

    void sendMail(MailDTO mailDTO);

    void sendMailWithAttachment(MailAttachDTO mailAttachDTO);

    ScheduledMailEntity sendScheduledMail(ScheduledMailDTO scheduledMailDTO);

    int sendScheduledMailWithAttachment();
}

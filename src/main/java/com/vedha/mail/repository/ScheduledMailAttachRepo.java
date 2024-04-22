package com.vedha.mail.repository;

import com.vedha.mail.entity.ScheduledMailAttachEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledMailAttachRepo extends JpaRepository<ScheduledMailAttachEntity, Long> {

    ScheduledMailAttachEntity findByMailId(Long scheduledMailId);
}

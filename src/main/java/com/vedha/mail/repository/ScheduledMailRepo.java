package com.vedha.mail.repository;

import com.vedha.mail.entity.ScheduledMailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledMailRepo extends JpaRepository<ScheduledMailEntity, Long>, PagingAndSortingRepository<ScheduledMailEntity, Long> {

    List<ScheduledMailEntity> findByScheduledTimeBeforeAndMailStatus(LocalDateTime scheduledTime, String status);
}

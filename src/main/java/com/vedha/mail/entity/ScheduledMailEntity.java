package com.vedha.mail.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mailId;

    @Column(nullable = false)
    private String mailTo;

    @Column(nullable = false)
    private String mailSubject;

    @Column(nullable = false)
    private String mailContent;

    private Boolean isAttachment;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    private String mailStatus;
}

package com.vedha.mail.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "ScheduledMailEntity", description = "Scheduled mail entity")
public class ScheduledMailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Mail ID", example = "1")
    private Long mailId;

    @Column(nullable = false)
    @Schema(description = "Recipient email address", example = "vedha@gmail.com")
    private String mailTo;

    @Column(nullable = false)
    @Schema(description = "Subject of mail", example = "Test mail")
    private String mailSubject;

    @Column(nullable = false)
    @Schema(description = "Body of mail", example = "Hello, this is a test mail")
    private String mailContent;

    @Schema(description = "Attachment status", example = "true")
    private Boolean isAttachment;

    @Column(nullable = false)
    @Schema(description = "Scheduled date and time", example = "2021-09-01T10:00:00")
    private LocalDateTime scheduledTime;

    @Schema(description = "Mail status", example = "SENT")
    private String mailStatus;
}

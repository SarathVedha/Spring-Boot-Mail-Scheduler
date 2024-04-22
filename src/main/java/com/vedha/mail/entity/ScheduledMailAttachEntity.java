package com.vedha.mail.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMailAttachEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachId;

    @Column(nullable = false)
    private Long mailId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String fileSize;

    @Lob
    @Column(nullable = false)
    private byte[] fileData;
}

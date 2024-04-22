package com.vedha.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMailDTO {

    private String to;

    private String subject;

    private String body;

    private LocalDateTime scheduledDate;

    private MultipartFile multipartFile;
}

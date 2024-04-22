package com.vedha.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailAttachDTO {

    private String to;

    private String subject;

    private String body;

    private MultipartFile multipartFile;
}

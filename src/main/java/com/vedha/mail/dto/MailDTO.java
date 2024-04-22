package com.vedha.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mail DTO")
public class MailDTO {

    @Schema(description = "Recipient email address", example = "vedha@gmail.com")
    private String to;

    @Schema(description = "Subject of the mail", example = "Test mail")
    private String subject;

    @Schema(description = "Body of the mail", example = "This is a test mail")
    private String body;
}

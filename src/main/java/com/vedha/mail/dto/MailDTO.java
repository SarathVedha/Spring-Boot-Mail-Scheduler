package com.vedha.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Email(message = "Invalid email address")
    private String to;

    @Schema(description = "Subject of the mail", example = "Test mail")
    @NotBlank(message = "Subject cannot be empty")
    @Size(min = 3, max = 100, message = "Subject must be between 3 and 100 characters")
    private String subject;

    @Schema(description = "Body of the mail", example = "This is a test mail")
    @NotBlank(message = "Body cannot be empty")
    @Size(min = 3, max = 1000, message = "Body must be between 3 and 1000 characters")
    private String body;
}

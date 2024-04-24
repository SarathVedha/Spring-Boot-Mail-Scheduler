package com.vedha.mail.controller;

import com.vedha.mail.dto.MailAttachDTO;
import com.vedha.mail.dto.MailDTO;
import com.vedha.mail.dto.ScheduledMailDTO;
import com.vedha.mail.entity.ScheduledMailEntity;
import com.vedha.mail.service.MailSenderService;
import com.vedha.mail.util.ScheduledMailSortField;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
@Tag(name = "Mail", description = "Mail operations")
@Validated
public class MailController {

    private final MailSenderService mailSenderService;

    @Operation(summary = "Send mail", description = "Send mail to the recipient")
    @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK")
    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> sendMail(@RequestBody @Valid MailDTO mailDTO) {

        mailSenderService.sendMail(mailDTO);

        return ResponseEntity.ok(Map.of("message", "Mail sent successfully"));
    }

    @Operation(summary = "Send mail with attachment", description = "Send mail to the recipient with attachment")
    @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK")
    @PostMapping(value = "/send/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> sendMailWithAttachment(
            @RequestParam("to") @Parameter(name = "to", description = "Recipient email address", example = "vedha@gmail.com") @Email(message = "Invalid email address") String to,
            @RequestParam("subject") @Parameter(name = "subject", description = "Subject of mail", example = "Test mail") @NotBlank(message = "Subject cannot be empty") @Size(min = 3, max = 100, message = "Subject must be between 3 and 100 characters") String subject,
            @RequestParam("body") @Parameter(name = "body", description = "Body of mail", example = "Hello, this is a test mail") @NotBlank(message = "Body cannot be empty") @Size(min = 3, max = 1000, message = "Body must be between 3 and 1000 characters") String body,
            @RequestParam("file") @Parameter(description = "file for attachment", example = "vedha.txt") @NotNull(message = "File attachment cannot be empty") MultipartFile file) {

        MailAttachDTO mailAttachDTO = MailAttachDTO.builder().to(to).subject(subject).body(body).multipartFile(file).build();

        mailSenderService.sendMailWithAttachment(mailAttachDTO);

        return ResponseEntity.ok(Map.of("message", "Mail sent successfully"));
    }

    @Operation(summary = "Send mail with scheduled date", description = "Send mail to the recipient with attachment and scheduled date")
    @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK")
    @PostMapping(value = "send/scheduled", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduledMailEntity> sendScheduledMail(
            @RequestParam("to") @Parameter(name = "to", description = "Recipient email address", example = "vedha@gmail.com") @Email(message = "Invalid email address") String to,
            @RequestParam("subject") @Parameter(name = "subject", description = "Subject of mail", example = "Test mail") @NotBlank(message = "Subject cannot be empty") @Size(min = 3, max = 100, message = "Subject must be between 3 and 100 characters") String subject,
            @RequestParam("body") @Parameter(name = "body", description = "Body of mail", example = "Hello, this is a test mail") @NotBlank(message = "Body cannot be empty") @Size(min = 3, max = 1000, message = "Body must be between 3 and 1000 characters") String body,
            @RequestParam("scheduledDate") @Parameter(name = "scheduledDate", description = "Scheduled date for mail MM/dd/yyyy HH:mm:ss", example = "04/12/2000 12:00:00") @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss") @NotNull(message = "Scheduled date cannot be empty") LocalDateTime scheduledDate,
            @RequestParam(value = "file", required = false) @Parameter(description = "file for attachment", example = "vedha.txt") MultipartFile file) {

        ScheduledMailDTO scheduledMailDTO = ScheduledMailDTO.builder().to(to).subject(subject).body(body).multipartFile(file).scheduledDate(scheduledDate).build();

        ScheduledMailEntity scheduledMailEntity = mailSenderService.sendScheduledMail(scheduledMailDTO);

        return ResponseEntity.ok(scheduledMailEntity);
    }

    @Operation(summary = "Get all scheduled mails", description = "Get all scheduled mails by page")
    @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK")
    @GetMapping(value = "/all/scheduled", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ScheduledMailEntity>> getAllScheduledMailsByPage(
            @Parameter(description = "Page number", example = "0") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Size of page", example = "10") @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "Sort direction", example = "ASC") @RequestParam(value = "direction", defaultValue = "ASC") Sort.Direction direction,
            @Parameter(description = "Sort field", example = "SCHEDULED_DATE") @RequestParam(value = "sort", defaultValue = "SCHEDULED_DATE") ScheduledMailSortField scheduledMailSortField) {

        PageRequest pageRequest = PageRequest.of(page, size, direction, scheduledMailSortField.getField());

        return ResponseEntity.ok(mailSenderService.getAllScheduledMails(pageRequest));
    }

    @Operation(summary = "Get Environment Property", description = "Get Environment Property")
    @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK")
    @GetMapping(value = "/getProperties", consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public ResponseEntity<String> getProperties(@Parameter(description = "Get Environment Property", example = "server.port") @RequestParam("property") @NotBlank(message = "property cannot br empty") String property) {

        return ResponseEntity.ok(mailSenderService.getEnvironmentProperty(property));
    }
}

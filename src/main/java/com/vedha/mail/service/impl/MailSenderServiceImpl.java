package com.vedha.mail.service.impl;

import com.vedha.mail.dto.MailAttachDTO;
import com.vedha.mail.dto.MailDTO;
import com.vedha.mail.dto.ScheduledMailDTO;
import com.vedha.mail.entity.ScheduledMailAttachEntity;
import com.vedha.mail.entity.ScheduledMailEntity;
import com.vedha.mail.exception.MailException;
import com.vedha.mail.repository.ScheduledMailAttachRepo;
import com.vedha.mail.repository.ScheduledMailRepo;
import com.vedha.mail.service.MailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    // Spring will inject the JavaMailSender bean, configs from application.properties
    private final JavaMailSender javaMailSender;

//    private final ResourceLoader resourceLoader;

    private final ScheduledMailRepo scheduledMailRepo;

    private final ScheduledMailAttachRepo scheduledMailAttachRepo;

    @Value("${spring.mail.username}")
    private String from;

    private MimeMessageHelper initMimeMessageHelper() throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        return new MimeMessageHelper(mimeMessage, true); // true indicates multipart message
    }

    @Override
    public void sendMail(MailDTO mailDTO) {

        try {

//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);// true indicates multipart message
            MimeMessageHelper mimeMessageHelper = initMimeMessageHelper();

            mimeMessageHelper.setFrom(from, "Vedha");
            mimeMessageHelper.setTo(mailDTO.getTo());
            mimeMessageHelper.setSubject(mailDTO.getSubject());
            mimeMessageHelper.setText(mailDTO.getBody(), true);

//            mimeMessageHelper.addAttachment("Test.txt", new ClassPathResource("/file/Test.txt"), "text/plain");
//            Resource resource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "/file/Test.txt");
//            mimeMessageHelper.addAttachment("Test.txt", resource, "text/plain");

//            javaMailSender.send(mimeMessage);
            javaMailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (Exception e) {

            log.error("Error while sending mail to: {}", mailDTO.getTo(), e);
            throw new MailException("Error while sending mail to: " + mailDTO.getTo() + " : " + e);
        }

    }

    @Override
    public void sendMailWithAttachment(MailAttachDTO mailAttachDTO) {

        Optional.ofNullable(mailAttachDTO.getMultipartFile()).orElseThrow(() -> new MailException("Attachment is missing"));

        try {

            MimeMessageHelper mimeMessageHelper = initMimeMessageHelper();

            mimeMessageHelper.setFrom(from, "Vedha");
            mimeMessageHelper.setTo(mailAttachDTO.getTo());
            mimeMessageHelper.setSubject(mailAttachDTO.getSubject());
            mimeMessageHelper.setText(mailAttachDTO.getBody(), true);

            MultipartFile multipartFile = mailAttachDTO.getMultipartFile();
            String fileName = Optional.ofNullable(multipartFile.getOriginalFilename()).orElseThrow(() -> new MailException("Attachment file name is missing"));
            String contentType = Optional.ofNullable(multipartFile.getContentType()).orElseThrow(() -> new MailException("Attachment content type is missing"));

            ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(multipartFile.getBytes(), contentType);
            mimeMessageHelper.addAttachment(fileName, byteArrayDataSource);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (Exception e) {

            log.error("Error while sending attachment mail to: {}", mailAttachDTO.getTo(), e);
            throw new MailException("Error while sending attachment mail to: " + mailAttachDTO.getTo() + " : " + e);
        }

    }

    @Override
    public ScheduledMailEntity sendScheduledMail(ScheduledMailDTO scheduledMailDTO) {

        try {

            boolean before = scheduledMailDTO.getScheduledDate().isBefore(LocalDateTime.now());
            if (before) {
                throw new MailException("Scheduled date should be in future");
            }

            ScheduledMailEntity mail = ScheduledMailEntity.builder()
                    .mailTo(scheduledMailDTO.getTo())
                    .mailSubject(scheduledMailDTO.getSubject())
                    .mailContent(scheduledMailDTO.getBody())
                    .isAttachment(scheduledMailDTO.getMultipartFile() != null)
                    .scheduledTime(scheduledMailDTO.getScheduledDate())
                    .mailStatus("SCHEDULED")
                    .build();

            ScheduledMailEntity save = scheduledMailRepo.save(mail);

            if (scheduledMailDTO.getMultipartFile() != null) {

                MultipartFile multipartFile = scheduledMailDTO.getMultipartFile();
                String fileName = Optional.ofNullable(multipartFile.getOriginalFilename()).orElseThrow(() -> new MailException("Attachment file name is missing"));
                String contentType = Optional.ofNullable(multipartFile.getContentType()).orElseThrow(() -> new MailException("Attachment content type is missing"));

                ScheduledMailAttachEntity attachment = ScheduledMailAttachEntity.builder()
                        .mailId(save.getMailId())
                        .fileName(fileName)
                        .fileType(contentType)
                        .fileData(multipartFile.getBytes())
                        .fileSize(String.valueOf(multipartFile.getSize()))
                        .build();

                scheduledMailAttachRepo.save(attachment);
            }

            return save;

        } catch (Exception e) {

            log.error("Error while sending scheduled mail to: {}", scheduledMailDTO.getTo(), e);
            throw new MailException("Error while sending scheduled mail to: " + scheduledMailDTO.getTo() + " : " + e);
        }
    }

    @Override
    public int sendScheduledMailWithAttachment() {

        // get all scheduled mails
        // check if scheduled time is before current time
        List<ScheduledMailEntity> scheduled = scheduledMailRepo.findByScheduledTimeBeforeAndMailStatus(LocalDateTime.now(), "SCHEDULED");

        scheduled.forEach(scheduledMailEntity -> {

            scheduledMailEntity.setMailStatus("PROCESSING");
            scheduledMailRepo.save(scheduledMailEntity);
        });

        // send mail
        scheduled.forEach(scheduledMailEntity -> {

            try {

                MimeMessageHelper mimeMessageHelper = initMimeMessageHelper();

                mimeMessageHelper.setFrom(from, "Vedha");
                mimeMessageHelper.setTo(scheduledMailEntity.getMailTo());
                mimeMessageHelper.setSubject(scheduledMailEntity.getMailSubject());
                mimeMessageHelper.setText(scheduledMailEntity.getMailContent(), true);

                if (scheduledMailEntity.getIsAttachment()) {

                    ScheduledMailAttachEntity attachment = scheduledMailAttachRepo.findByMailId(scheduledMailEntity.getMailId());

                    ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(attachment.getFileData(), attachment.getFileType());
                    mimeMessageHelper.addAttachment(attachment.getFileName(), byteArrayDataSource);
                }

                javaMailSender.send(mimeMessageHelper.getMimeMessage());

                // update status to SENT
                scheduledMailEntity.setMailStatus("SENT");
                scheduledMailRepo.save(scheduledMailEntity);

            } catch (Exception e) {

                log.error("Error while sending scheduled mail to: {}", scheduledMailEntity.getMailTo(), e);
                scheduledMailEntity.setMailStatus("FAILED");
                scheduledMailRepo.save(scheduledMailEntity);
            }
        });

        return scheduled.size();
    }

    @Override
    public Page<ScheduledMailEntity> getAllScheduledMails(Pageable pageable) {

        return scheduledMailRepo.findAll(pageable);
    }
}

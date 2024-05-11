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
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private final Environment environment;

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
    public int sendScheduledMailWithAttachment() throws InterruptedException {

        // get all scheduled mails
        // to check if scheduled time is before current time
        List<ScheduledMailEntity> scheduled = scheduledMailRepo.findByScheduledTimeBeforeAndMailStatus(LocalDateTime.now(), "SCHEDULED");

        scheduled.forEach(scheduledMailEntity -> {

            scheduledMailEntity.setMailStatus("PROCESSING");
            scheduledMailRepo.save(scheduledMailEntity);
        });

        // create thread pool with 2 threads to send mails in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // send mail
        scheduled.forEach(scheduledMailEntity -> {

            // ExecutorService will execute the task in a separate thread from the thread pool and reuse the thread
            // execute method will take a Runnable task and return void, submit method will take a Callable task and return Future
            executorService.execute(() -> {

                try {

                    log.warn("Starting Thread: {} MailId: {}", Thread.currentThread().getName(), scheduledMailEntity.getMailId());

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

                    log.warn("Ending Thread: {} MailId: {}", Thread.currentThread().getName(), scheduledMailEntity.getMailId());

                } catch (Exception e) {

                    log.error("Thread: {} Error while sending scheduled mail to: {}", Thread.currentThread().getName(), scheduledMailEntity.getMailTo(), e);
                    scheduledMailEntity.setMailStatus("FAILED");
                    scheduledMailRepo.save(scheduledMailEntity);
                }
            });
        });

        // shutdown the executor service after all tasks are completed to release the resources and threads in the pool and main thread will continue
        executorService.shutdown();

        while (!executorService.awaitTermination(5, TimeUnit.SECONDS)) { // wait for 5 seconds to complete the tasks

            log.warn("Waiting for scheduled mails to complete");
        }

        log.warn("All tasks completed: {}", executorService.isTerminated());

        return scheduled.size();
    }

    @Override
    public Page<ScheduledMailEntity> getAllScheduledMails(Pageable pageable) {

        return scheduledMailRepo.findAll(pageable);
    }

    @Override
    public String getEnvironmentProperty(String key) {

        // get property value from application.properties, System properties, OS environment variables, Program arguments
        // System properties are set by the JVM and are accessible using System.getProperty("key") -- java -Dkey=value -jar app.jar
        // Environment variables are set in the OS and are accessible using System.getenv("key") -- linux export key=value, windows set key=value
        // what is program arguments? -- java -jar app.jar --key=value
        // Spring will load properties and make it available using Environment object
        // priority order: application.properties, System properties, OS environment variables, Program arguments (last one will override the previous one)

        return environment.getProperty(key, "NO Value Found For Key: " + key);
    }
}

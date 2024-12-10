package com.fptgamebookingbe.service.impl;

import com.fptgamebookingbe.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Async
    public void sendEmail(String to, String subject, String text) throws MessagingException {

        // Create a new MimeMessage and MimeMessageHelper object
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set the recipient, subject, and text content of the email
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        // Send the email
        mailSender.send(message);
    }

    @Override
    public String getProcessedTemplate(int mId, Map<String, String> placeholders) {

        // Retrieve the email template
        String emailTemplate = "";


        // Check if the email template exists
        if (emailTemplate == null) {
            throw new RuntimeException("Email template not found");
        }

        // Replace placeholders with actual values
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            emailTemplate = emailTemplate.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        // Return the processed email template
        return emailTemplate;
    }
}

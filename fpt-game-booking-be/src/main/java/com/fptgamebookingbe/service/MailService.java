package com.fptgamebookingbe.service;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface MailService {

    /**
     * Sends an email to the specified recipient with the given subject and text.
     *
     * @param to      the email address of the recipient
     * @param subject the subject of the email
     * @param text    the text content of the email
     * @throws MessagingException if an error occurs while sending the email
     */
    void sendEmail(String to, String subject, String text) throws MessagingException;

    /**
     * Retrieves and processes a template based on the specified mId,
     * replacing the placeholders with the provided values.
     *
     * @param mId          the identifier of the template to be processed
     * @param placeholders a map containing key-value pairs to replace placeholders in the template
     * @return the processed template with placeholders replaced
     */
    String getProcessedTemplate(int mId, Map<String, String> placeholders);
}

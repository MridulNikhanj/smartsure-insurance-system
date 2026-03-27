package com.smartsure.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Welcome to SmartSure Insurance!");
            message.setText(
                    "Dear " + userName + ",\n\n" +
                            "Thank you for registering with SmartSure!\n\n" +
                            "Your account has been created successfully. " +
                            "You can now log in and explore our insurance plans.\n\n" +
                            "Best regards,\n" +
                            "The SmartSure Team"
            );

            mailSender.send(message);

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }
}
package com.example.booking_hotel.service;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;


    public void sendEmail(String to, String subject, String content, byte[] imageBytes) throws MessagingException {
        if (to == null || to.isEmpty() || subject == null || subject.isEmpty() || content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Email parameters cannot be null or empty.");
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);

        // Sử dụng thẻ <br> để xuống dòng
        content = content.replace("\n", "<br>");

        // Nhúng ảnh vào email
        helper.setText(content, true);
        helper.addInline("roomPhoto", new ByteArrayResource(imageBytes), "image/jpeg");
        helper.addInline("avatarPhoto", new ByteArrayResource(imageBytes), "image/jpeg");

        javaMailSender.send(message);
    }


    public MimeMessage createMimeMessage() {
        return javaMailSender.createMimeMessage();
    }


}

package com.example.booking_hotel.controller;

import com.example.booking_hotel.request.EmailRequest;
import com.example.booking_hotel.service.IEmailService;
import com.example.booking_hotel.service.Impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/emails")
public class EmailController {

    @Autowired
    private IEmailService emailService;
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getContent());
            return ResponseEntity.ok("Email sent successfully to: " + emailRequest.getTo());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }
}

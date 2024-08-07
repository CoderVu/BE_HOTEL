package com.example.booking_hotel.controller;

import com.example.booking_hotel.request.EmailRequest;
import com.example.booking_hotel.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getContent(), null);
            return ResponseEntity.ok("Email sent successfully to: " + emailRequest.getTo());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send emaill: " + e.getMessage());
        }
    }


}

package com.example.booking_hotel.service;

public interface IEmailService {
    void sendEmail(String to, String subject, String content);
}

package com.example.booking_hotel.util;

import java.util.Random;

public class OTPGenerator {

    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int digit = new Random().nextInt(10);
            otp.append(digit);
        }

        return otp.toString();
    }
}
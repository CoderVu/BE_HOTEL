package com.example.booking_hotel.service;

import com.example.booking_hotel.model.User;

import java.util.List;

public interface IUserService {
    User registerUser(User user);
    List<User> getUsers();
    void deleteUser(String email);
    User getUser(String email);

    User getUserProfile(String userId);

    User getUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String otp);

    boolean validateOTP(String email, String otp);

    void updatePassword(String email, String newPassword);
}

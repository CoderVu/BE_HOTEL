package com.example.booking_hotel.service;

import com.example.booking_hotel.model.BookedRoom;
import com.example.booking_hotel.respository.BookingRespose;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookingServiceImpl {

    List<BookedRoom> getAllBookings();

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    List<BookedRoom> getAllBookingByRoomId(Long roomId);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    void cancelBooking(Long bookingId);

    List<BookedRoom> getBookingsByEmail(String email);
}

package com.example.booking_hotel.service;

import com.example.booking_hotel.model.BookedRoom;

import java.util.List;
import java.util.Optional;

public interface BookingServiceImpl {

    List<BookedRoom> getAllBookings();

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    List<BookedRoom> getAllBookingByRoomId(Long roomId);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    void cancelBooking(Long bookingId);

    List<BookedRoom> getBookingsByEmail(String email);

    Optional<BookedRoom> getBookingById(Long bookingId);
}

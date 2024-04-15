package com.example.booking_hotel.controller;

import com.example.booking_hotel.exception.InvalidBookingRequestException;
import com.example.booking_hotel.exception.ResourceNotFoundException;
import com.example.booking_hotel.model.BookedRoom;
import com.example.booking_hotel.model.Room;
import com.example.booking_hotel.respository.BookingRespose;
import com.example.booking_hotel.respository.RoomModel;
import com.example.booking_hotel.service.BookingServiceImpl;
import com.example.booking_hotel.service.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/bookings")
public class BookingController {

    private final BookingServiceImpl bookingService;
    private final RoomServiceImpl roomService;

    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingRespose>> getAllBookings() {
        List<BookedRoom> bookings = bookingService.getAllBookings();
        List<BookingRespose> bookingResponses = new ArrayList<>();
        for (BookedRoom booking : bookings) {
            BookingRespose bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        try {
            BookedRoom booking = bookingService.findByBookingConfirmationCode(confirmationCode);
            BookingRespose bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/room/{roomId}/bookings")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @RequestBody BookedRoom bookingRequest) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok(confirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking with ID " + bookingId + " has been canceled successfully.");
    }

    private BookingRespose getBookingResponse(BookedRoom booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        RoomModel room = new RoomModel(theRoom.getId(), theRoom.getRoomType(), theRoom.getRoomPrice());
        return new BookingRespose(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren() ,// Fix typo: NumOfChilren to numOfChildren
                booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(),
                room);
    }
}

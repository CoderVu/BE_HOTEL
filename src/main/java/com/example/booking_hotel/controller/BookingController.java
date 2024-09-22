package com.example.booking_hotel.controller;

import com.example.booking_hotel.exception.InvalidBookingRequestException;
import com.example.booking_hotel.exception.ResourceNotFoundException;
import com.example.booking_hotel.model.BookedRoom;
import com.example.booking_hotel.model.Room;
import com.example.booking_hotel.response.BookingRespose;
import com.example.booking_hotel.response.RoomResponse;
import com.example.booking_hotel.service.IBookingService;
import com.example.booking_hotel.service.IEmailService;
import com.example.booking_hotel.service.Impl.EmailServiceImpl;
import com.example.booking_hotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final IBookingService bookingService;
    private final IRoomService roomService;
    @Autowired
    private IEmailService emailService;
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
    @GetMapping("/history-booking/email/{email}")
    public ResponseEntity<List<BookingRespose>> getBookingHistoryByEmail(@PathVariable String email) {
        List<BookedRoom> bookings = bookingService.getBookingsByEmail(email);
        List<BookingRespose> bookingResponses = new ArrayList<>();
        for (BookedRoom booking : bookings) {
            BookingRespose bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

@PostMapping("/room/{roomId}/bookings")
public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                     @RequestBody BookedRoom bookingRequest){
    try{
        String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
        Room theRoom = roomService.getRoomById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));
        String guestName = bookingRequest.getGuestName() != null ? bookingRequest.getGuestName() : "Khách hàng";
        String checkInDate = bookingRequest.getCheckInDate() != null ? bookingRequest.getCheckInDate().toString() : "không xác định";
        String checkOutDate = bookingRequest.getCheckOutDate() != null ? bookingRequest.getCheckOutDate().toString() : "không xác định";
        String roomType = theRoom.getRoomType() != null ? theRoom.getRoomType() : "không xác định";
        String roomPrice = theRoom.getRoomPrice() != null ? theRoom.getRoomPrice().toString() : "không xác định";
        String emailSubject = "Xác nhận đặt phòng";
        String emailContent = "<html><head><style>";
        emailContent += "body {font-family: Arial, sans-serif;}";
        emailContent += "h1 {color: #333333;}";
        emailContent += "ul {list-style-type: none;}";
        emailContent += "li {margin-bottom: 10px;}";
        emailContent += "</style></head><body>";
        emailContent += "<h1>Xác nhận đặt phòng</h1>";
        emailContent += "<p>Xin chào " + guestName + ",</p>";
        emailContent += "<p>Bạn đã đặt phòng thành công.</p>";
        emailContent += "<p>Thông tin đặt phòng:</p>";
        emailContent += "<ul>";
        emailContent += "<li><strong>Mã xác nhận:</strong> " + confirmationCode + "</li>";
        emailContent += "<li><strong>Ngày check-in:</strong> " + checkInDate + "</li>";
        emailContent += "<li><strong>Ngày check-out:</strong> " + checkOutDate + "</li>";
        emailContent += "<li><strong>Loại phòng:</strong> " + roomType + "</li>";
        emailContent += "<li><strong>Giá phòng:</strong> " + roomPrice + "</li>";
        emailContent += "</ul>";
        emailContent += "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>";
        emailContent += "</body></html>";
        String guestEmail = bookingRequest.getGuestEmail();
        emailService.sendEmail(guestEmail, emailSubject, emailContent);
        return ResponseEntity.ok("Room booked successfully, Your booking confirmation code is: " + confirmationCode);
    } catch (InvalidBookingRequestException e){
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
        RoomResponse room = new RoomResponse(theRoom.getId(), theRoom.getRoomType(), theRoom.getRoomPrice());
        return new BookingRespose(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren() ,
                booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(),
                room);
    }
}

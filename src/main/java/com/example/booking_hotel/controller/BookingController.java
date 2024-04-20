package com.example.booking_hotel.controller;

import com.example.booking_hotel.exception.InvalidBookingRequestException;
import com.example.booking_hotel.exception.ResourceNotFoundException;
import com.example.booking_hotel.model.BookedRoom;
import com.example.booking_hotel.model.Room;
import com.example.booking_hotel.respository.BookingRespose;
import com.example.booking_hotel.respository.RoomModel;
import com.example.booking_hotel.service.BookingServiceImpl;
import com.example.booking_hotel.service.EmailService;
import com.example.booking_hotel.service.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingServiceImpl bookingService;
    private final RoomServiceImpl roomService;
    @Autowired
    private EmailService emailService;

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

//    @PostMapping("/room/{roomId}/bookings")
//    public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @RequestBody BookedRoom bookingRequest) {
//        try {
//            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
//            return ResponseEntity.ok(confirmationCode);
//        } catch (InvalidBookingRequestException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
@PostMapping("/room/{roomId}/bookings")
public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                     @RequestBody BookedRoom bookingRequest){
    try{
        String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);

        // Lấy thông tin phòng đã đặt
        Room theRoom = roomService.getRoomById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        // Kiểm tra và xử lý nếu các giá trị là null
        String guestName = bookingRequest.getGuestName() != null ? bookingRequest.getGuestName() : "Khách hàng";
        String checkInDate = bookingRequest.getCheckInDate() != null ? bookingRequest.getCheckInDate().toString() : "không xác định";
        String checkOutDate = bookingRequest.getCheckOutDate() != null ? bookingRequest.getCheckOutDate().toString() : "không xác định";
        String roomType = theRoom.getRoomType() != null ? theRoom.getRoomType() : "không xác định";
        String roomPrice = theRoom.getRoomPrice() != null ? theRoom.getRoomPrice().toString() : "không xác định";

        // Gửi email tới địa chỉ của người đặt phòng
        String guestEmail = bookingRequest.getGuestEmail();
        String emailSubject = "Xác nhận đặt phòng";
        String emailContent = String.format("Xin chào %s,\n"
                        + "Bạn đã đặt phòng thành công.\n"
                        + "Thông tin đặt phòng:\n"
                        + "- Mã xác nhận: %s\n"
                        + "- Ngày check-in: %s\n"
                        + "- Ngày check-out: %s\n"
                        + "- Loại phòng: %s\n"
                        + "- Giá phòng: %s\n"
                        + "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.",
                guestName,
                confirmationCode,
                checkInDate,
                checkOutDate,
                roomType,
                roomPrice);

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

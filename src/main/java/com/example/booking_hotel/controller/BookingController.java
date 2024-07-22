package com.example.booking_hotel.controller;

import com.example.booking_hotel.exception.InvalidBookingRequestException;
import com.example.booking_hotel.exception.ResourceNotFoundException;
import com.example.booking_hotel.model.BookedRoom;
import com.example.booking_hotel.model.Room;
import com.example.booking_hotel.respo.Respose.BookingRespose;
import com.example.booking_hotel.respo.Respose.RoomResponse;
import com.example.booking_hotel.service.BookingServiceImpl;
import com.example.booking_hotel.service.EmailService;
import com.example.booking_hotel.service.RatingService;
import com.example.booking_hotel.service.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

import static com.example.booking_hotel.util.ImageGeneral.decodeBase64ToImage;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final BookingServiceImpl bookingService;
    private final RoomServiceImpl roomService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RatingService ratingService;

    @GetMapping("/all-booking")
    public ResponseEntity<List<BookingRespose>> getAllBookings() {
        List<BookedRoom> bookings = bookingService.getAllBookings();
        List<BookingRespose> bookingResponses = new ArrayList<>();
        for (BookedRoom booking : bookings) {
            BookingRespose bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }
    @GetMapping("/all-bookingOfOneHotel/{hotelId}")
    public ResponseEntity<List<BookingRespose>> getAllBookingsOfOneHotel(@PathVariable Long hotelId) {
        List<BookedRoom> bookings = bookingService.getAllBookingsOfOneHotel(hotelId);
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

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookedRoom bookingRequest) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);

            Room theRoom = roomService.getRoomById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

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
            emailContent += "<p>Hình ảnh của phòng:</p>";
            emailContent += "<img src='cid:roomPhoto' alt='Room photo' style='width: 100%; max-width: 500px; height: auto;'>";
            emailContent += "</body></html>";

            String guestEmail = bookingRequest.getGuestEmail();
            String roomPhoto = theRoom.getPhoto();
            byte[] imageBytes = decodeBase64ToImage(roomPhoto);
            // Gửi email kèm theo ảnh
            emailService.sendEmail(guestEmail, emailSubject, emailContent, imageBytes);

            return ResponseEntity.ok("Room booked successfully, your booking confirmation code is: " + confirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while sending email");
        }
    }



    @DeleteMapping("/booking/{bookingId}/delete")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking with ID " + bookingId + " has been canceled successfully.");
    }

    private BookingRespose getBookingResponse(BookedRoom booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        RoomResponse roomResponse = new RoomResponse(
                theRoom.getId(),
                theRoom.getRoomType(),
                theRoom.getRoomPrice(),
                theRoom.getDescription(),
                theRoom.getAverageRating(),
                theRoom.getRatingCount(),
                theRoom.getHotel()
        );

        // Create BookingRespose without rating details
        BookingRespose bookingResponse = new BookingRespose(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren(),
                booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(),
                roomResponse,
                booking.isRated(),
                booking.getComment(),
                booking.getCreatedAt(),
                booking.getStarRating(),

                booking.getRoom().getHotel());

        return bookingResponse;
    }
}

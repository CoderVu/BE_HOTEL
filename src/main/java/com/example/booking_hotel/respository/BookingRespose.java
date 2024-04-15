package com.example.booking_hotel.respository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRespository {
    private Long bookingId;
    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String guestFullName;

    private String gestEmail;

    private int  NumOfAdults;

    private int NumOfChilren;

    private int totalNumOfGuest;

    private String bookingConfirmationCode;
    private RoomModel room;

    public BookingRespository(Long bookingId, LocalDate checkInDate, LocalDate checkOutDate, String bookingConfirmationCode) {
        this.bookingId = bookingId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingConfirmationCode = bookingConfirmationCode;
    }
}

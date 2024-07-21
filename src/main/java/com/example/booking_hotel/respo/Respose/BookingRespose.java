package com.example.booking_hotel.respo.Respose;

import com.example.booking_hotel.model.Hotel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingRespose {
    private Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String guestFullName;
    private String guestEmail;
    private int numOfAdults;
    private int numOfChildren;
    private int totalNumOfGuest;
    private String bookingConfirmationCode;
    private RoomResponse room;
    private boolean isRated;
    private String comment;
    private LocalDateTime createdAt;
    private double starRating;

    public BookingRespose(Long bookingId, LocalDate checkInDate, LocalDate checkOutDate, String guestFullName, String guestEmail, int numOfAdults, int numOfChildren, int totalNumOfGuest, String bookingConfirmationCode, RoomResponse room, boolean isRated, String comment, LocalDateTime createdAt, double starRating, Hotel hotel) {
        this.bookingId = bookingId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.guestFullName = guestFullName;
        this.guestEmail = guestEmail;
        this.numOfAdults = numOfAdults;
        this.numOfChildren = numOfChildren;
        this.totalNumOfGuest = totalNumOfGuest;
        this.bookingConfirmationCode = bookingConfirmationCode;
        this.room = room;
        this.isRated = isRated;
        this.comment = comment;
        this.createdAt = createdAt;
        this.starRating = starRating;
    }
}
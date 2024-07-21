package com.example.booking_hotel.respo.Respose;

import com.example.booking_hotel.model.Hotel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
@Data
@NoArgsConstructor
public class RoomResponse {
    private Long id;
    private Hotel hotel; // Add this line
    private String roomType;
    private BigDecimal roomPrice;
    private  boolean isBooked ;
    private String description;
    private String photo;
    private Double averageRating = 0.0;
    private Integer ratingCount = 0;
    private List<BookingRespose> bookingResposeList;

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice, String description, Double averageRating, Integer ratingCount, Hotel hotel) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.description = description;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.hotel = hotel;
    }

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice, String description, boolean isBooked, String photo,
                        List<BookingRespose> bookings, Double averageRating, Integer ratingCount, Hotel hotel) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.description = description;
        this.isBooked = isBooked;
        this.photo = photo;
        this.bookingResposeList = bookings;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.hotel = hotel;

    }

}

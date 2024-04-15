package com.example.booking_hotel.respository;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
@Data
@NoArgsConstructor
public class RoomRespository {
    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private  boolean isBooked ;
    private String photo;
    private List<BookingRespository> bookingRespositoryList;

    public RoomRespository(Long id, String roomType, BigDecimal roomPrice) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
    }

    public RoomRespository(Long id, String roomType, BigDecimal roomPrice, boolean isBooked, byte[] photoBytes, List<BookingRespository> bookingRespositoryList) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isBooked = isBooked;
        this.photo = photoBytes != null ? Base64.getEncoder().encodeToString(photoBytes) : null;
        this.bookingRespositoryList = bookingRespositoryList;
    }
}

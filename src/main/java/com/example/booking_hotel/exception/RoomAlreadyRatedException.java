package com.example.booking_hotel.exception;

public class RoomAlreadyRatedException extends RuntimeException {
    public RoomAlreadyRatedException(String message) {
        super(message);
    }
}
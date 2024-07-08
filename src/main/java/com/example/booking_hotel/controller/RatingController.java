package com.example.booking_hotel.controller;

import com.example.booking_hotel.exception.RoomAlreadyRatedException;
import com.example.booking_hotel.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/rate")
    public ResponseEntity<String> rateRoom(
            @RequestParam("userId") Long userId,
            @RequestParam("roomId") Long roomId,
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("starRating") double starRating,
            @RequestParam("comment") String comment) {
        try {
            String responseMessage = ratingService.rateRoom(userId, roomId, bookingId, starRating, comment);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (RoomAlreadyRatedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
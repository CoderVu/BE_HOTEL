package com.example.booking_hotel.service;

import com.example.booking_hotel.exception.RoomAlreadyRatedException;
import com.example.booking_hotel.model.BookedRoom;
import com.example.booking_hotel.model.Rating;
import com.example.booking_hotel.model.Room;
import com.example.booking_hotel.model.User;
import com.example.booking_hotel.respo.Repositoty.BookedRoomRepository;
import com.example.booking_hotel.respo.Repositoty.RatingRepository;
import com.example.booking_hotel.respo.Repositoty.RoomRepository;
import com.example.booking_hotel.respo.Repositoty.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookedRoomRepository bookedRoomRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public String rateRoom(Long userId, Long roomId, Long bookingId, double starRating, String comment) {
        // Fetch user, bookedRoom, and room entities
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BookedRoom bookedRoom = bookedRoomRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("BookedRoom not found"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check if the room has already been rated
        if (bookedRoom.isRated()) {
            throw new RoomAlreadyRatedException("This room has already been rated.");
        }

        // Proceed to create a new rating
        Rating rating = new Rating();
        rating.setUser(user);
        rating.setBookedRoom(bookedRoom);
        rating.setRoom(room);
        rating.setStarRating(starRating);
        rating.setComment(comment);
        rating.setCreatedAt(LocalDateTime.now());

        try {
            // Save the new rating
            ratingRepository.save(rating);

            // Mark the booked room as rated and update the new fields
            bookedRoom.setRated(true);
            bookedRoom.setStarRating(starRating);
            bookedRoom.setComment(comment);
            bookedRoom.setCreatedAt(LocalDateTime.now());
            bookedRoomRepository.save(bookedRoom);

            // Update the room's star rating
            room.updateStarRating(starRating);
            roomRepository.save(room);

            // Return a success message
            return "Rating successfully added for room with ID: " + room.getId();
        } catch (DataIntegrityViolationException e) {
            // Handle potential data integrity issues
            throw new RuntimeException("Error saving rating or updating room status.", e);
        }
    }

    public Optional<Rating> getRatingByBookedRoom(BookedRoom bookedRoom) {
        return ratingRepository.findByBookedRoom(bookedRoom);
    }

    public List<Rating> getAllRatingsByRoomId(Long roomId) {
        return ratingRepository.findAllByRoomId(roomId);
    }
}

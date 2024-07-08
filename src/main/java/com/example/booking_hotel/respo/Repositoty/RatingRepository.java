package com.example.booking_hotel.respo.Repositoty;
import com.example.booking_hotel.model.BookedRoom;
import com.example.booking_hotel.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByBookedRoom(BookedRoom bookedRoom);
}
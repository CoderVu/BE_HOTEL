package com.example.booking_hotel.respo.Repositoty;

import com.example.booking_hotel.model.BookedRoom;
import com.example.booking_hotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookedRoom, Long> {
    Optional <BookedRoom> findByBookingConfirmationCode(String confirmationCode);
    List<BookedRoom> findByRoomId(Long roomId);

    List<BookedRoom> findByGuestEmail(String email);

    List<BookedRoom> findByRoom(Room room);

}

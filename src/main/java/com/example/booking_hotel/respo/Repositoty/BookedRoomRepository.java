package com.example.booking_hotel.respo.Repositoty;
import com.example.booking_hotel.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookedRoomRepository extends JpaRepository<BookedRoom, Long> {
}
package com.example.booking_hotel.repository;

import com.example.booking_hotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Long> {

}

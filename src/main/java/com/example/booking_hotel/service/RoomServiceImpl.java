package com.example.booking_hotel.service;

import com.example.booking_hotel.model.Hotel;
import com.example.booking_hotel.model.Room;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomServiceImpl {
    Room addNewRoom(String roomType, BigDecimal roomPrice, String description, String photo, Hotel hotel) throws SQLException, IOException;
    List<String> getAllRoomTypes();
    List<Room> getAllRooms();

    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;
    void deleteRoom(Long roomId);
    Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, String description, String photo);

    Optional<Room> getRoomById(Long roomId);

    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);


    List<Room> getRoomsByHotelId(Long hotelId);
}

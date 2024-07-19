package com.example.booking_hotel.service;

import com.example.booking_hotel.exception.ResourceNotFoundException;
import com.example.booking_hotel.model.Room;
import com.example.booking_hotel.respo.Repositoty.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService implements RoomServiceImpl {
    private final RoomRepository roomRepository;



    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if (theRoom.isEmpty()) {
            throw new ResourceNotFoundException("Sorry, Room not found!");
        }
        Room room = theRoom.get();
        String photoString = room.getPhoto();
        if (photoString != null && !photoString.isEmpty()) {
            // Kiểm tra xem chuỗi base64 có hợp lệ không
            if (isValidBase64(photoString)) {
                try {
                    return Base64.getDecoder().decode(photoString);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid base64 photo data for room ID: " + roomId);
                }
            } else {
                throw new IllegalArgumentException("Invalid base64 photo data for room ID: " + roomId);
            }
        }
        return null;
    }

    // Hàm kiểm tra chuỗi base64 có hợp lệ không
    private boolean isValidBase64(String base64) {
        // Kiểm tra xem chuỗi có độ dài chia hết cho 4 không
        if (base64.length() % 4 != 0) {
            return false;
        }
        // Kiểm tra xem chuỗi có chứa các ký tự hợp lệ của base64 không
        String base64Pattern = "^[A-Za-z0-9+/]*={0,3}$";
        return base64.matches(base64Pattern);
    }


    @Override
    public void deleteRoom(Long roomId) {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if( theRoom.isPresent()){
            roomRepository.deleteById(roomId);
        }
    }
    @Override
    public Room addNewRoom(String roomType, BigDecimal roomPrice, String description, String photo) throws SQLException, IOException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        room.setDescription(description);
        room.setPhoto(photo);
        return roomRepository.save(room);
    }
    @Override
        public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, String description, String photo) {
            Optional<Room> theRoom = roomRepository.findById(roomId);
            if (theRoom.isEmpty()) {
                throw new ResourceNotFoundException("Sorry, Room not found!");
            }
            Room room = theRoom.get();
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setDescription(description);
            room.setPhoto(photo);
            return roomRepository.save(room);
        }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId).get());
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomRepository.findAvailableRoomsByDatesAndType(checkInDate, checkOutDate, roomType);
    }
//    @Override
//    public void rateRoom(Long roomId, double rating) {
//        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
//        room.addRating(rating);
//        roomRepository.save(room);
//    }

}

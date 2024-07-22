package com.example.booking_hotel.controller;

import com.example.booking_hotel.exception.PhotoRetrievalException;
import com.example.booking_hotel.exception.UnauthorizedException;
import com.example.booking_hotel.model.*;
import com.example.booking_hotel.respo.Respose.BookingRespose;
import com.example.booking_hotel.respo.Respose.ReviewResponse;
import com.example.booking_hotel.respo.Respose.RoomResponse;
import com.example.booking_hotel.service.*;
import com.example.booking_hotel.util.ImageGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomServiceImpl roomService;
    private final BookingService bookingService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private UserService userService; // Add this line
    @Autowired
    private HotelService hotelService; // Add this line
    @PostMapping("/admin/add/new-room")
    public ResponseEntity<?> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice,
            @RequestParam("description") String description,
            @RequestParam("hotelId") Long hotelId) {
        try {
            Hotel hotel = hotelService.getHotelById(hotelId);
            User currentUser = userService.getCurrentUser();

            // Check if the current user is the admin of the hotel
            if (!hotel.getAdmin().equals(currentUser)) {
                return new ResponseEntity<>("You are not authorized to manage this hotel", HttpStatus.FORBIDDEN);
            }

            InputStream photoInputStream = photo.getInputStream();
            String base64Photo = ImageGeneral.fileToBase64(photoInputStream);
            Room savedRoom = roomService.addNewRoom(roomType, roomPrice, description, base64Photo, hotel);
            RoomResponse roomResponse = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice(), savedRoom.getDescription(), savedRoom.getAverageRating(), savedRoom.getRatingCount(), savedRoom.getHotel());

            return ResponseEntity.ok(roomResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    @PutMapping("/admin/update/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable Long roomId,
                                        @RequestParam(required = false) String roomType,
                                        @RequestParam(required = false) BigDecimal roomPrice,
                                        @RequestParam(required = false) String description,
                                        @RequestParam(required = false) MultipartFile photo) throws IOException, SQLException, PhotoRetrievalException {
        try {
            Optional<Room> roomOpt = roomService.getRoomById(roomId);
            User currentUser = userService.getCurrentUser();

            // Check if the current user is the admin of the hotel that the room belongs to
            if (!roomOpt.isPresent() ||
                    !(roomOpt.get().getHotel().getAdmin().equals(currentUser) ||
                            currentUser.getRoles().stream().anyMatch(role -> role.getName() == Role.RoleName.ROLE_SUPPERUSER))) {
                return new ResponseEntity<>("You are not authorized to manage this room", HttpStatus.FORBIDDEN);
            }

            Room room = roomOpt.get();
            InputStream photoInputStream = photo.getInputStream();
            String base64Photo = ImageGeneral.fileToBase64(photoInputStream);
            Room updatedRoom = roomService.updateRoom(roomId, roomType, roomPrice, description, base64Photo);
            RoomResponse roomResponse = getRoomModel(updatedRoom);
            return ResponseEntity.ok(roomResponse);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/room/types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException, PhotoRetrievalException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.getEncoder().encodeToString(photoBytes);
                RoomResponse roomResponse = getRoomModel(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);

            }
        }
        return ResponseEntity.ok(roomResponses);


    }
    @GetMapping("/all-rooms/{hotelId}")
    public ResponseEntity<List<RoomResponse>> getAllRoomsByHotel(@PathVariable Long hotelId) throws SQLException, PhotoRetrievalException {
        Hotel hotel = hotelService.getHotelById(hotelId);
        User currentUser = userService.getCurrentUser();

        // Check if the current user is the admin of the hotel
        if (!hotel.getAdmin().equals(currentUser)) {
            throw new UnauthorizedException("You are not authorized to manage this hotel");
        }


        List<Room> rooms = roomService.getRoomsByHotelId(hotelId);
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.getEncoder().encodeToString(photoBytes);
                RoomResponse roomResponse = getRoomModel(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
        return ResponseEntity.ok(roomResponses);
    }
    @DeleteMapping("delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }


    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId) {
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        if (theRoom.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            RoomResponse roomResponse = getRoomModel(theRoom.get());
            return ResponseEntity.ok(Optional.of(roomResponse));
        } catch (PhotoRetrievalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/room/{roomId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getRoomReviews(@PathVariable Long roomId) {
        List<Rating> ratings = ratingService.getAllRatingsByRoomId(roomId);
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        for (Rating rating : ratings) {
            ReviewResponse reviewResponse = new ReviewResponse();
            reviewResponse.setUserEmail(rating.getUser().getEmail());
            reviewResponse.setStars((int) rating.getStarRating());
            reviewResponse.setComment(rating.getComment());
            reviewResponses.add(reviewResponse);
        }
        return ResponseEntity.ok(reviewResponses);
    }

    private RoomResponse getRoomModel(Room room) throws PhotoRetrievalException {
        // Fetch all bookings for the given room
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());

        // Map bookings to BookingRespose with appropriate details
        List<BookingRespose> bookingInfo = bookings.stream()
                .map(booking -> {
                    // Fetch rating by BookedRoom
                    Optional<Rating> ratingOpt = ratingService.getRatingByBookedRoom(booking);
                    Rating rating = ratingOpt.orElse(null);

                    // Create BookingRespose with rating details if available
                    return new BookingRespose(
                            booking.getBookingId(),
                            booking.getCheckInDate(),
                            booking.getCheckOutDate(),
                            booking.getGuestName(),
                            booking.getGuestEmail(),
                            booking.getNumOfAdults(),
                            booking.getNumOfChildren(),
                            booking.getTotalNumOfGuest(),
                            booking.getBookingConfirmationCode(),
                            new RoomResponse(
                                    room.getId(),
                                    room.getRoomType(),
                                    room.getRoomPrice(),
                                    room.getDescription(),
                                    room.getAverageRating(),
                                    room.getRatingCount(),
                                    room.getHotel()
                            ),
                            booking.isRated(),
                            booking.getComment(),
                            booking.getCreatedAt(),
                            booking.getStarRating(),
                            booking.getRoom().getHotel()

                    );
                }).toList();

        // Retrieve photo bytes from the room if available
        String photoBlob = room.getPhoto();
        byte[] photoBytes = null;
        if (photoBlob != null) {
            photoBytes = Base64.getDecoder().decode(photoBlob);
        }
        String photoString = Base64.getEncoder().encodeToString(photoBytes);

        // Create and return RoomResponse with booking info
        return new RoomResponse(
                room.getId(),
                room.getRoomType(),
                room.getRoomPrice(),
                room.getDescription(),
                room.isBooked(),
                photoString,
                bookingInfo,
                room.getAverageRating(),
                room.getRatingCount(),
                room.getHotel()
        );
    }



    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingByRoomId(roomId);

    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam("roomType") String roomType) throws SQLException, PhotoRetrievalException {
        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : availableRooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
                RoomResponse roomResponse = getRoomModel(room);
                roomResponse.setPhoto(photoBase64);
                roomResponses.add(roomResponse);
            }
        }
        if (roomResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(roomResponses);
        }
    }



    }
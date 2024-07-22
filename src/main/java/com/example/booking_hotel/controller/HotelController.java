package com.example.booking_hotel.controller;

import com.example.booking_hotel.exception.UnauthorizedException;
import com.example.booking_hotel.model.Hotel;
import com.example.booking_hotel.model.Role;
import com.example.booking_hotel.model.User;
import com.example.booking_hotel.respo.Respose.HotelResponse;
import com.example.booking_hotel.service.HotelService;
import com.example.booking_hotel.service.IUserService;
import com.example.booking_hotel.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotel")
public class HotelController {
    private final IUserService userService;
    private final HotelService hotelService;
    private final RoleService roleService;

    @PostMapping("/add-hotel")
    public ResponseEntity<?> addHotel(@RequestBody Hotel hotel){
        User currentUser = userService.getCurrentUser();

        // Check if the current user is an admin
        Role userRole = roleService.findByName(Role.RoleName.valueOf(Role.RoleName.ROLE_ADMIN.name()));
        if (!currentUser.getRoles().contains(userRole)) {
            return new ResponseEntity<>("You are not authorized to add a hotel", HttpStatus.FORBIDDEN);
        }

        hotel.setAdmin(currentUser);
        hotelService.saveHotel(hotel);

        return ResponseEntity.ok("Hotel added successfully!");
    }
    @GetMapping("/hotels/managed-by/{email}")
    public ResponseEntity<?> getHotelsManagedByUser(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        List<Hotel> hotels = hotelService.getHotelsManagedByUser(user);

        List<HotelResponse> hotelResponses = hotels.stream().map(hotel -> {
            HotelResponse hotelResponse = new HotelResponse();
            hotelResponse.setId(hotel.getId());
            hotelResponse.setName(hotel.getName());
            hotelResponse.setAddress(hotel.getAddress());
            return hotelResponse;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(hotelResponses);
    }
    @GetMapping("/all-hotels")
    public ResponseEntity<?> getAllHotels() {
        List<Hotel> hotels = hotelService.getAllHotels();

        List<HotelResponse> hotelResponses = hotels.stream().map(hotel -> {
            HotelResponse hotelResponse = new HotelResponse();
            hotelResponse.setId(hotel.getId());
            hotelResponse.setName(hotel.getName());
            hotelResponse.setAddress(hotel.getAddress());
            return hotelResponse;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(hotelResponses);
    }
    @GetMapping("/{name}")
    public ResponseEntity<?> getHotelByName(@PathVariable String name) {
        Hotel hotel = hotelService.getHotelByName(name);

        if (hotel == null) {
            return new ResponseEntity<>("Hotel not found", HttpStatus.NOT_FOUND);
        }

        HotelResponse hotelResponse = new HotelResponse();
        hotelResponse.setId(hotel.getId());
        hotelResponse.setName(hotel.getName());
        hotelResponse.setAddress(hotel.getAddress());

        return ResponseEntity.ok(hotelResponse);
    }


}
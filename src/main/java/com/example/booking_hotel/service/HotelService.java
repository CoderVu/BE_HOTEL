package com.example.booking_hotel.service;

import com.example.booking_hotel.model.Hotel;
import com.example.booking_hotel.model.User;
import com.example.booking_hotel.respo.Repositoty.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    public Hotel getHotelById(Long hotelId) {
        return hotelRepository.findById(hotelId).orElse(null);
    }

    public void saveHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public List<Hotel> getHotelsManagedByUser(User user) {
        return hotelRepository.findAllByAdmin(user);
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel getHotelByName(String name) {
        return hotelRepository.findByName(name);
    }
}
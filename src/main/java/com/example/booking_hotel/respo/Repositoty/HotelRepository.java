package com.example.booking_hotel.respo.Repositoty;

import com.example.booking_hotel.model.Hotel;
import com.example.booking_hotel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {


    List<Hotel> findAllByAdmin(User user);

    Hotel findByName(String name);
}

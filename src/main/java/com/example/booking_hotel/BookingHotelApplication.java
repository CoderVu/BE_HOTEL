package com.example.booking_hotel;

import com.example.booking_hotel.model.Role;
import com.example.booking_hotel.model.Room;
import com.example.booking_hotel.respo.Repositoty.RoleRepository;
import com.example.booking_hotel.respo.Repositoty.RoomRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BookingHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingHotelApplication.class, args);
    }
}

@Component
class RoleInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final RoomRepository roomRepository;

    public RoleInitializer(RoleRepository roleRepository, RoomRepository roomRepository) {
        this.roleRepository = roleRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//          // Tạo role ADMIN
//        Role adminRole = new Role("ROLE_ADMIN");
//        roleRepository.save(adminRole);
//        Role userRole = new Role("ROLE_USER");
//        roleRepository.save(userRole);
//        // Tạo phòng ngẫu nhiên
//        Room randomRoom = Room.createRandomRoom();
//
//        // Lưu phòng vào cơ sở dữ liệu
//        roomRepository.save(randomRoom);
    }
}

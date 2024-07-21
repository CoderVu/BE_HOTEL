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

import java.math.BigDecimal;

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

    public void run(ApplicationArguments args) throws Exception {
        if (roleRepository.findByName(Role.RoleName.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(Role.RoleName.ROLE_USER));
        }
        if (roleRepository.findByName(Role.RoleName.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(Role.RoleName.ROLE_ADMIN));
        }
        if (roleRepository.findByName(Role.RoleName.ROLE_SUPPERUSER).isEmpty()) {
            roleRepository.save(new Role(Role.RoleName.ROLE_SUPPERUSER));
        }
    }
}

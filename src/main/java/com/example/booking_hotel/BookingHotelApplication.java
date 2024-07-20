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


    public RoleInitializer(RoleRepository roleRepository , RoomRepository roomRepository) {
        this.roleRepository = roleRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception{
//        Kiểm tra xem đã có role nào trong database chưa nếu chưa
        if(roleRepository.findByName("ROLE_USER").isEmpty()){
            roleRepository.save(new Role("ROLE_USER"));
        }
        if(roleRepository.findByName("ROLE_ADMIN").isEmpty()){
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
//        Kiểm tra xem đã có room kiểm tra theo id room nào trong database chưa nếu chưa
        if(roomRepository.findById(1L).isEmpty()){
            roomRepository.save(new Room(1L, "Single", BigDecimal.valueOf(10.1),"Single room", false, null, null, 0.0, 0, null));
        }


    }
}

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


    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception{
//        Kiểm tra xem đã có role nào trong database chưa nếu chưa
//        thì thêm role user và admin vào database
        if(roleRepository.findByName("ROLE_USER") == null){
            roleRepository.save(new Role("ROLE_USER"));
        }
        if(roleRepository.findByName("ROLE_ADMIN") == null){
            roleRepository.save(new Role("ROLE_ADMIN"));
        }

    }
}

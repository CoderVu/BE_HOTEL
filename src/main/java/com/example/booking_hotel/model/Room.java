package com.example.booking_hotel.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Getter
@Setter
@AllArgsConstructor

public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name="room_id")
    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private  boolean isBooked = false;
    @Lob
    private Blob photo;
    @OneToMany(mappedBy = "room",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<BookedRoom> bookedRoomList;

    public Room() {
        this.bookedRoomList = new ArrayList<>();
    }
    public void addBooking(BookedRoom bookedRoom){
        if(bookedRoomList == null){
            bookedRoomList = new ArrayList<>();
        }
        bookedRoomList.add(bookedRoom);
        bookedRoom.setRoom(this);
        isBooked = true;
        String bookingCode = RandomStringUtils.randomNumeric(10);
        bookedRoom.setBookingConfirmationCode(bookingCode);
    }

}

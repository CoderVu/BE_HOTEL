package com.example.booking_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    private String roomType;
    private BigDecimal roomPrice;

    @Column(name = "description")
    private String description;

    private boolean isBooked = false;


    @Column(name = "photo", columnDefinition = "LONGTEXT")
    private String photo;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookedRoom> bookedRoomList = new ArrayList<>();

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Rating> ratings = new ArrayList<>();

    public void updateStarRating(double rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5.");
        }

        // Ensure averageRating and ratingCount are not null
        if (this.averageRating == null) {
            this.averageRating = 0.0;
        }
        if (this.ratingCount == null) {
            this.ratingCount = 0;
        }

        double totalRating = this.averageRating * this.ratingCount;
        this.ratingCount++;
        this.averageRating = (totalRating + rating) / this.ratingCount;
    }

    public void addBooking(BookedRoom bookedRoom) {
        if (bookedRoomList == null) {
            bookedRoomList = new ArrayList<>();
        }
        bookedRoomList.add(bookedRoom);
        bookedRoom.setRoom(this);
        isBooked = true;
        String bookingCode = RandomStringUtils.randomNumeric(6);
        bookedRoom.setBookingConfirmationCode(bookingCode);
    }
}

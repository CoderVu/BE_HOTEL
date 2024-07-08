//package com.example.booking_hotel.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class Rating {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "booking_id", unique = true, nullable = false)
//    private BookedRoom bookedRoom;
//
//    @ManyToOne
//    @JoinColumn(name = "room_id", nullable = false)
//    private Room room;
//
//    @Column(name = "comment")
//    private String comment;
//
//    @Column(name = "star_rating")
//    private double starRating;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    public void setUserId(Long userId) {
//        if (this.user == null) {
//            this.user = new User();
//        }
//        this.user.setId(userId);
//    }
//
//    public void setBookingId(Long bookingId) {
//        if (this.bookedRoom == null) {
//            this.bookedRoom = new BookedRoom();
//        }
//        this.bookedRoom.setId(bookingId);
//    }
//
//    public void setRoomId(Long roomId) {
//        if (this.room == null) {
//            this.room = new Room();
//        }
//        this.room.setId(roomId);
//    }
//
//}
////ALTER TABLE rating ADD CONSTRAINT unique_booking_id UNIQUE (booking_id);
////Nếu bạn muốn đảm bảo rằng mỗi booking chỉ có một đánh giá, bạn có thể thêm một ràng buộc duy nhất vào bảng Rating trong cơ sở dữ liệu. Giả sử bảng Rating có một cột booking_id, bạn có thể thêm chỉ mục duy nhất để đảm bảo không có hai bản ghi với cùng một booking_id.
package com.example.booking_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private BookedRoom bookedRoom;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "comment")
    private String comment;

    @Column(name = "star_rating")
    private double starRating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}